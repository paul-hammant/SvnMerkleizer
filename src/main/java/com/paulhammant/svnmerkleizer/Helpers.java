package com.paulhammant.svnmerkleizer;

import com.paulhammant.svnmerkleizer.pojos.*;
import com.thoughtworks.xstream.XStream;

public class Helpers {

    public static XStream makePropfindXmlConverter() {
        XStream svnXmlConverter = new XStream();
        svnXmlConverter.allowTypesByWildcard(new String[]{
                "com.paulhammant.svnmerkleizer.pojos.**"
        });
        svnXmlConverter.processAnnotations(new Class[]{PropfindSvnResult.class, DResponse.class, DProp.class, DPropstat.class});
        return svnXmlConverter;
    }

    public static XStream makeDirectoryXmlSerializer() {
        XStream svnXmlConverter = new XStream();
        svnXmlConverter.allowTypesByWildcard(new String[]{
                "com.paulhammant.svnmerkleizer.pojos.**"
        });
        svnXmlConverter.processAnnotations(new Class[]{Directory.class, Entry.class});
        return svnXmlConverter;
    }

}
