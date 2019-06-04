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
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

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
        List<Entry> list = new ArrayList<>();
        list.add(Entry.dir("ddd", "ssshhaa11"));
        list.add(Entry.file("fff", "syda87sf8e6w"));
        assertThat(SvnMerkleizer.toCSV(list))
                .isEqualTo(("ddd/,ssshhaa11\nfff,syda87sf8e6w"));
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
