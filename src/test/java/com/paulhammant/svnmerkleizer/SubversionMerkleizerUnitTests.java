/*
        SvnMerkleizer: Adds a Merkle Tree to Subversion

        Copyright (c) 2017-2019, Paul Hammant
        All rights reserved.

        Redistribution and use in source and binary forms, with or without
        modification, are permitted provided that the following conditions are met:

        1. Redistributions of source code must retain the above copyright notice, this
        list of conditions and the following disclaimer.
        2. Redistributions in binary form must reproduce the above copyright notice,
        this list of conditions and the following disclaimer in the documentation
        and/or other materials provided with the distribution.

        THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
        ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
        WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
        DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
        ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
        (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
        LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
        ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
        (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
        SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

        The views and conclusions contained in the software and documentation are those
        of the authors and should not be interpreted as representing official policies,
        either expressed or implied, of the Servirtium project.
*/

package com.paulhammant.svnmerkleizer;

import com.cedarsoftware.util.DeepEquals;
import com.paulhammant.svnmerkleizer.pojos.*;
import com.thoughtworks.xstream.XStream;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SubversionMerkleizerUnitTests {

    @Test
    public void emptyListShouldNotMakeCSV() {
        List<Entry> list = new ArrayList<>();
        assertThat(SvnMerkleizer.toCSV(list)).isEmpty();
    }

    @Test
    public void xmlNamespaceShouldBeRemoved() {
        String incoherent = "<D:h<lp1:e<lp2:l<lp3:l<g0:o";
        String greeting = SvnMerkleizer.eliminateNamespaces(incoherent);
        assertThat(greeting).isEqualTo("<h<e<l<l<o");
    }

    @Test
    public void simplelistShouldHaveCsv() {
        assertThat(SvnMerkleizer.toCSV(makeTwo()))
                .isEqualTo(("ddd/,ssshhaa11\nfff,syda87sf8e6w"));
    }

    @Test
    public void simplelistShouldHaveHtml() {
        Directory dir = new Directory();
        dir.contents = makeTwo();
        dir.sha1 = "mockSha1";
        assertThat(SvnMerkleizer.toHtml(dir))
                .isEqualTo(("<html><body>\n" +
                        "<p>mockSha1</p>\n" +
                        "<table>\n" +
                        "<tr><td><a href=\"ddd/.merkle.html\">ddd</a></td><td>ssshhaa11</td></tr>\n" +
                        "<tr><td>fff</td><td>syda87sf8e6w</td></tr>\n" +
                        "</table></body></html>"));
    }

    @Test
    public void simplelistShouldHaveText() {
        assertThat(SvnMerkleizer.toText(makeTwo()))
                .isEqualTo("ddd/ ssshhaa11\n" +
                        "fff syda87sf8e6w");
    }

    @Test
    public void simplelistShouldHaveJson() {
        Directory dir = new Directory();
        dir.contents = makeTwo();
        dir.sha1 = "mockSha1";

        assertThat(SvnMerkleizer.toPrettyJson(dir))
                .isEqualTo("{\n" +
                        "  \"sha1\" : \"mockSha1\",\n" +
                        "  \"contents\" : [ {\n" +
                        "    \"dir\" : \"ddd\",\n" +
                        "    \"sha1\" : \"ssshhaa11\"\n" +
                        "  }, {\n" +
                        "    \"file\" : \"fff\",\n" +
                        "    \"sha1\" : \"syda87sf8e6w\"\n" +
                        "  } ]\n" +
                        "}");
    }

    @NotNull
    private static List<Entry> makeTwo() {
        List<Entry> list = new ArrayList<>();
        list.add(Entry.dir("ddd", "ssshhaa11"));
        list.add(Entry.file("fff", "syda87sf8e6w"));
        return list;
    }

    @Test
    public void pluckVersionDoesItsThing() {


        PropfindSvnResult result = new PropfindSvnResult();
        result.responses = new ArrayList<>();
        try {
            SvnMerkleizer.pluckVersion(result);
            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("No version number found");
        }

        DResponse e = new DResponse();
        e.propstats = new ArrayList<>();
        DPropstat e1 = new DPropstat();
        e1.prop = new DProp();
        e1.prop.baselineRelativePath = "xyz";
        e1.prop.sha1Checksum = "fhfhf";
        e1.prop.versionName = "33";
        e.propstats.add(e1);
        result.responses.add(e);
        assertThat(SvnMerkleizer.pluckVersion(result))
                .isEqualTo(33);

    }

    @Test
    public void mediumSizedPropfindCanBeFlattenedIntoTerseXML() {
        XStream xStream = new XStream();
        xStream.alias("PropfindSvnResult", PropfindSvnResult.class);
        xStream.alias("DResponse", DResponse.class);
        xStream.alias("Entry", Entry.class);
        xStream.alias("Directory", Directory.class);
        xStream.alias("DPropstat", DPropstat.class);

        Directory actual = SvnMerkleizer.flattenToItems((PropfindSvnResult) xStream.fromXML(
                "<PropfindSvnResult>\n" +
                        "  <responses>\n" +
                        "    <DResponse>\n" +
                        "      <href>hhhhh</href>\n" +
                        "      <propstats>\n" +
                        "        <DPropstat>\n" +
                        "          <prop/>\n" +
                        "        </DPropstat>\n" +
                        "      </propstats>\n" +
                        "    </DResponse>\n" +
                        "    <DResponse>\n" +
                        "      <href>hhhhh/</href>\n" +
                        "      <propstats>\n" +
                        "        <DPropstat>\n" +
                        "          <prop>\n" +
                        "            <versionName></versionName>\n" +
                        "            <baselineRelativePath>ddd/</baselineRelativePath>\n" +
                        "            <sha1Checksum>ssshhaa11</sha1Checksum>\n" +
                        "          </prop>\n" +
                        "          <status>A-OK</status>\n" +
                        "        </DPropstat>\n" +
                        "      </propstats>\n" +
                        "    </DResponse>\n" +
                        "    <DResponse>\n" +
                        "      <href>hhhhh</href>\n" +
                        "      <propstats>\n" +
                        "        <DPropstat>\n" +
                        "          <prop>\n" +
                        "            <versionName></versionName>\n" +
                        "            <baselineRelativePath>fff</baselineRelativePath>\n" +
                        "            <sha1Checksum>syda87sf8e6w</sha1Checksum>\n" +
                        "          </prop>\n" +
                        "          <status>A-OK</status>\n" +
                        "        </DPropstat>\n" +
                        "      </propstats>\n" +
                        "    </DResponse>\n" +
                        "  </responses>\n" +
                        "</PropfindSvnResult>"));
        assertTrue(DeepEquals.deepEquals(actual, xStream.fromXML(
                "<Directory>\n" +
                        "  <contents>\n" +
                        "    <Entry>\n" +
                        "      <dir>ddd</dir>\n" +
                        "      <sha1>ssshhaa11</sha1>\n" +
                        "      <versionInfo>\n" +
                        "        <sha1>ssshhaa11</sha1>\n" +
                        "        <svnRevision>0</svnRevision>\n" +
                        "        <xmlHashCode>0</xmlHashCode>\n" +
                        "      </versionInfo>\n" +
                        "    </Entry>\n" +
                        "    <Entry>\n" +
                        "      <file>fff</file>\n" +
                        "      <sha1>syda87sf8e6w</sha1>\n" +
                        "      <versionInfo>\n" +
                        "        <sha1>syda87sf8e6w</sha1>\n" +
                        "        <svnRevision>0</svnRevision>\n" +
                        "        <xmlHashCode>0</xmlHashCode>\n" +
                        "      </versionInfo>\n" +
                        "    </Entry>\n" +
                        "  </contents>\n" +
                        "</Directory>")));
    }

}
