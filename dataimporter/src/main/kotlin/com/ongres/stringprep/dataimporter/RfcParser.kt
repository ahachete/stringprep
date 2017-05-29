/*
 * Copyright 2017, OnGres, Inc.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */


package com.ongres.stringprep.dataimporter


import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.regex.Pattern


val RFC_3454_URL = "https://tools.ietf.org/rfc/rfc3454.txt"


class StringPrepRfcParser {
    private companion object RfcText {
        val contents: String by lazy {
            BufferedReader(
                    InputStreamReader(
                            URL(RFC_3454_URL).openStream()
                    )
            ).use { it.readText() }
        }
    }

    fun extractTable(table: String): Sequence<String> {
        val extractTableRegex = "----- Start Table $table -----(.*)----- End Table $table -----"
        val m = Pattern.compile(extractTableRegex, Pattern.DOTALL).matcher(RfcText.contents)
        val filterRowsRegex = Regex("^[A-Z0-9]+(-[A-Z0-9]+)?" + "(; ([A-Z0-9]+( [A-Z0-9]+)*)?)?" + "(; .+)?$")

        return if (m.find()) {
            m.group(1)
                    .lineSequence()
                    .map { it.trim() }
                    .filter { it.matches(filterRowsRegex) }
        } else emptySequence()
    }
}