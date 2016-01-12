/*
 Copyright (c) 2015, Louis Capitanchik
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of Affogato nor the names of its associated properties or
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package co.louiscap.lib.lexical.io;

import co.louiscap.lib.compat.string.StringChunker;
import co.louiscap.lib.lexical.CliLexer;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * @author Louis Capitanchik &lt;contact@louiscap.co&gt;
 */
public class LexicalFileReader {
    
    private final Path filePath;
    private final File file;
    
    public LexicalFileReader(Path pathToFile) {
        this.filePath = pathToFile;
        this.file = pathToFile.toFile();
        if(!this.file.exists()) {
            throw new IllegalArgumentException("Attempt to create reader for non-existent file " + pathToFile.toString());
        }
    }
    
    public void parse() throws IOException {
        StringChunker sc;
        String currentNamespace = null;
        String priority, regex, captures;
        String[] captureSplit;
        
        CliLexer.PRINTER.println("Starting to parse " + filePath.toString(), "debug");
        LineIterator it = FileUtils.lineIterator(file, "UTF-8");
        while(it.hasNext()) {
            String line = it.nextLine();
            sc = new StringChunker(line);
            
            if(sc.peekNext(2).equals("::")) {
                sc.getUntil(" ");
                currentNamespace = sc.tail().trim();
            } else {
                priority = sc.getUntil("/").trim();
                regex = sc.getUntil("/", true);
                while(!(sc.peekNext(1).equals(" ") || sc.peekNext(1).equals(""))) {
                    regex += sc.getUntil("/", true);
                }
                regex = regex.substring(0, regex.length()-1);
                captures = sc.tail().trim();
            }
            
        }
    }
    
}
