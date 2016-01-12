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
package co.louiscap.lib.lexical;

import co.louiscap.lib.compat.output.MultiplexedStringPrinter;
import co.louiscap.lib.lexical.io.LexicalFileDiscovery;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Lexes the given file based upon the 
 * @author Louis Capitanchik &lt;contact@louiscap.co&gt;
 */
public class CliLexer {

    public static final MultiplexedStringPrinter PRINTER = new MultiplexedStringPrinter();
    public static CommandLine PROGOPTS;
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException If the user tries to write the token data to a file, but it is
     * in some way not accessible (exists but is not writable, does not exist but can't be created, etc)
     */
    public static void main(String[] args) throws FileNotFoundException {
        PRINTER.addChannel("out", System.out);
        PRINTER.addChannel("err", System.err);
        
        CommandLineParser cliparse = new DefaultParser();
        Options opts = setupCommandLine();
        try {
            PROGOPTS = cliparse.parse(opts, args);
        } catch (ParseException ex) {
            PRINTER.println(ex.getMessage(), "err");
        }
        
        if(PROGOPTS.hasOption("h")) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("LibLexical", opts, true);
            System.exit(0);
        }
        
        if(PROGOPTS.hasOption("v")) {
            PRINTER.addChannel("debug", System.out);
        }
        
        if(PROGOPTS.hasOption("o")) {
            Path p = Paths.get(PROGOPTS.getOptionValue("o", "./out.tok"));
            File f = p.toFile();
            PrintStream ps = new PrintStream(f);
            PRINTER.addChannel("out", null);
        }
        
        LexicalFileDiscovery lfd = new LexicalFileDiscovery();
        lfd.addExtension("lex");
        lfd.getPathsFromDir(".", "lexf").forEach(System.out::println);
    }
    
    private static Options setupCommandLine() {
        Options options = new Options();
        options.addOption("v", false, "Verbose; Print debug info to stdout");
        options.addOption("o", "out", true, "Outfile; output lexical info to file"
                + "instead of stdout");
        options.addOption("e", true, "File encoding. Defaults to UTF-8");
        options.addOption("h", "help", false, "Print out help text");
        return options;
    }
    
}
