import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.io.IOException;

public class StupsCompiler {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java StupsCompiler <-mode> <file.pas>");
            return;
        }

        String filename =  args[1];

        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File does not exist: " + filename);
            return;
        }

        if (args[0].equals("-compile")) {
            CharStream input = CharStreams.fromFileName(args[1]);
            PascalLexer lexer = new PascalLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PascalParser parser = new PascalParser(tokens);

            parser.removeErrorListeners();
            ParseErrorListener parseErrorListener = new ParseErrorListener();
            parser.addErrorListener(parseErrorListener);

            ParseTree tree = parser.start();

            if(parseErrorListener.hasError()) {
                return;
            }

            Scope scope = new Scope(null);
            TypeChecker typeChecker = new  TypeChecker(scope);
            typeChecker.visit(tree);
        }
    }
}