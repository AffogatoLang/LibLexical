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

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;

/**
 * Provides configurable discovery utilities for files that likely contain lexical definitions. File 
 * discovery is extension based and extensions need to be defined before trying to find anything
 *
 * @author Louis Capitanchik &lt;contact@louiscap.co&gt;
 */
public class LexicalFileDiscovery {

    private final ArrayList<String> extensions;

    public LexicalFileDiscovery() {
        extensions = new ArrayList<>();
    }

    /**
     * Reduce function to combine two strings with the separator for the default
     * file system;
     */
    private final BinaryOperator<String> createFileString
            = (t, c) -> (t == null ? 
                            "" : 
                            t + FileSystems.getDefault().getSeparator()
                        ) + c;

    private boolean pathHasValidExtension(Path pathToCheck) {
        boolean isValid = false;
        String pathAsString = pathToCheck.toString();
        for (String ext : extensions) {
            isValid = isValid || FilenameUtils.getExtension(pathAsString)
                                              .equalsIgnoreCase(ext);
        }
        return isValid;
    }

    /**
     * Adds one or more extensions to the list of valid file extensions that
     * will be interpreted as lexical files
     *
     * @param ext The file extension
     */
    public void addExtension(String... ext) {
        extensions.addAll(Arrays.asList(ext));
    }

    /**
     * Creates a list of lexical files that can then be parsed from a given directory and its
     * subdirectories. Constructs a
     * {@link java.nio.file.Path} from the provided path fragments and returns
     * the result of calling
     * {@link LexicalFileReader#getDeepPathsFromDir(java.nio.file.Path)} with
     * the new Path.
     *
     * @see LexicalFileReader#recursePathsFromDir(java.nio.file.Path)
     * @param dirPath One or more strings that define a path to a directory that
     * will be searched for lexical files.
     * @return An {@link java.util.ArrayList} of Paths that match the lexical
     * file pattern, likely containing lexical definitions (Matching based on
     * file extension)
     */
    public ArrayList<Path> getDeepPathsFromDir(String... dirPath) {
        if (dirPath == null) {
            throw new IllegalArgumentException("Can't construct path from null fragment array");
        }

        Path dir = (new File(Arrays.stream(dirPath)
                                   .reduce(null, createFileString))).toPath();

        return getDeepPathsFromDir(dir);
    }

    public ArrayList<Path> getDeepPathsFromDir(Path dir) {
        ArrayDeque<Path> pathsToCheck = new ArrayDeque<>(2);
        ArrayList<Path> validPaths = new ArrayList<>(1);
        Path curPath;
        File pathAsFile;
        
        pathsToCheck.add(dir);
        while (!pathsToCheck.isEmpty()) {
            curPath = pathsToCheck.pop();
            pathAsFile = curPath.toFile();
            if(pathAsFile.isDirectory()) {
                List<Path> entries = Arrays.stream(pathAsFile.listFiles())
                                           .map(f -> f.toPath())
                                           .collect(Collectors.toList());
                pathsToCheck.addAll(entries);
            } else {
                if(pathHasValidExtension(curPath)){
                    validPaths.add(curPath);
                }
            }
        }
        
        return validPaths;
    }

    public ArrayList<Path> getPathsFromDir(String... dirPath) {
        if (dirPath == null) {
            throw new IllegalArgumentException("Can't construct path from null fragment array");
        }

        Path dir = (new File(Arrays.stream(dirPath)
                .reduce(null, createFileString))).toPath();

        return getPathsFromDir(dir);
    }

    public ArrayList<Path> getPathsFromDir(Path dir) {
        ArrayDeque<Path> pathsToCheck = new ArrayDeque<>(2);
        ArrayList<Path> validPaths = new ArrayList<>(1);
        Path curPath;
        File pathAsFile;
        
        pathsToCheck.addAll(Arrays.stream(dir.toFile().listFiles())
                                  .map(f -> f.toPath())
                                  .collect(Collectors.toList()));
        while (!pathsToCheck.isEmpty()) {
            curPath = pathsToCheck.pop();
            pathAsFile = curPath.toFile();
            if(!pathAsFile.isDirectory()) {
                if(pathHasValidExtension(curPath)){
                    validPaths.add(curPath);
                }
            }
        }
        
        return validPaths;
    }
}
