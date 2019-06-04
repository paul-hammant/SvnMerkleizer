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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class AuthzReader {

    public String getAuthBlurbFor(String who, String orig_authz) {

        StringBuilder authz = new StringBuilder(orig_authz);

        authz = replace(authz, "=", " = ");
        authz = replace(authz, ",", " , ");
        authz = replaceTwoSpacesForOneCompletely(authz);

        List<String> groups = Arrays.stream(authz.toString().split("\n"))
                .filter(line -> !line.startsWith("#"))
                .map(String::trim)
                .map(line -> line.replace(",", ""))
                .filter(line -> (line + " ").contains(" " + who + " "))
                .filter(line -> asList(line.split(" ")).contains(who))
                .map(line -> line.split("=")[0].trim())
                .collect(Collectors.toList());

        String wip = Arrays.stream(authz.toString().split("\n"))
                .filter(line -> !line.startsWith("#"))
                .map(line -> thisLine(groups, who, line))
                .collect(Collectors.joining());

        wip = Arrays.stream(wip.split("\n"))
                .filter(line -> line.endsWith("-y") || line.endsWith("-n"))
                .collect(Collectors.joining(","));

        StringBuilder sb = new StringBuilder(wip);
        replace(sb, "-y,-y","-y");
        replace(sb, "][","],[");

        return Arrays.stream(sb.toString().split(","))
                .filter(line -> line.endsWith("-y") || line.endsWith("-n"))
                .collect(Collectors.joining(","));

    }

    private StringBuilder replaceTwoSpacesForOneCompletely(StringBuilder sb) {
        int prevLen = 0;
        do {
            prevLen = sb.length();
            replace(sb, "  ", " ");
        } while(sb.length() != prevLen);
        return sb;
    }

    private StringBuilder replace(StringBuilder stringBuilder, String s, String s1) {
        int ix = stringBuilder.indexOf(s, 0);;
        do {
            if (ix > -1) {
                stringBuilder.replace(ix, ix + s.length(), s1);
                ix = ix + s1.length();
            }
            ix = stringBuilder.indexOf(s, ix);
        } while (ix != -1);
        return stringBuilder;
    }

    private String thisLine(List<String> groups, String who, String line) {
        if (line.startsWith("[") && line.endsWith("]") && !line.equals("[groups]")) {
            return line;
        }
        boolean incl = false;
        boolean excl = false;
        if (line.equals(who + " = ")) {
            excl = true; // no 'r' or 'rw'
        } else if (line.equals("* = ")) {
            excl = true;
        } else {
            if (line.startsWith(who + " ")) {
                incl = true;
            } else if (line.startsWith("* = r")) {
                incl = true;
            } else {
                for (String s : groups) {
                    if (line.contains("@" + s + " ")) {
                        incl = true;
                    }
                }
            }
        }
        return (excl? "-n\n": "") + (incl? "-y\n": "");
    }

}
