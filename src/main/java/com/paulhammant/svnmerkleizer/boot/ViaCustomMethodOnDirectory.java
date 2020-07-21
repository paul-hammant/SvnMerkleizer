package com.paulhammant.svnmerkleizer.boot;

import com.paulhammant.svnmerkleizer.SubversionDirectoryMerkleizerService;
import com.paulhammant.svnmerkleizer.SvnMerkleizer;
import com.thoughtworks.xstream.XStream;
import okhttp3.OkHttpClient;
import org.jooby.Mutant;
import org.jooby.Request;
import org.jooby.Response;

import static com.paulhammant.svnmerkleizer.Helpers.makeDirectoryXmlSerializer;
import static com.paulhammant.svnmerkleizer.Helpers.makePropfindXmlConverter;

public class ViaCustomMethodOnDirectory extends SubversionDirectoryMerkleizerService {

    public ViaCustomMethodOnDirectory(String method, String delegateToUrl, String contextDir, String cacheFilePath,
                                      SvnMerkleizer.Metrics metrics, int port, final OkHttpClient okHttpClient) {
        super(new SvnMerkleizer(delegateToUrl, contextDir, metrics, cacheFilePath, okHttpClient));

        XStream svnXmlConverter = makePropfindXmlConverter();
        XStream directoryXmlSerializer = makeDirectoryXmlSerializer();

        use(method, "**/", (req, rsp) -> {
            port(port);
            Mutant expectsTypeHdr = req.header("expects-type");
            expectsTypeHeaderShouldBeSetCheck(rsp, expectsTypeHdr);
            if (rsp.status().isPresent()) return;
            // Delete this block when https://github.com/jooby-project/jooby/issues/967 is fixed
            onlyForDirectriesCheck(method, req, rsp);
            if (rsp.status().isPresent()) return;

            String expectsType = expectsTypeHdr.value();
            switch (expectsType) {
                case "JSON": json(svnXmlConverter, req, rsp); break;
                case "CSV": csv(svnXmlConverter, req, rsp); break;
                case "TXT": txt(svnXmlConverter, req, rsp); break;
                case "XML": xml(svnXmlConverter, directoryXmlSerializer, req, rsp); break;
                case "HTML": html(svnXmlConverter, req, rsp); break;
                default:
                    rsp.status(500);
                    rsp.type("text/plain");
                    rsp.send("Specified 'expects-type' header of '" + expectsType + "' isn't supported. JSON, CSV, TXT, XML, HTML are the allowed ones");
            }
        });
    }

    private void onlyForDirectriesCheck(String method, Request req, Response rsp) throws Throwable {
        if (!(req.rawPath().endsWith("/"))) {
            rsp.status(500);
            rsp.send("You can only do " + method + " operations on directories");
        }
    }

    private void expectsTypeHeaderShouldBeSetCheck(Response rsp, Mutant expectsHdr) throws Throwable {
        if (!(expectsHdr.isSet())) {
            rsp.status(500);
            rsp.send("The 'expects-type' header should have been set");
        }
    }

}
