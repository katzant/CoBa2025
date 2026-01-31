Java Version: 21.0.8
ANTLR Version: 4.13.2

Vorraussetzungen:

- antlr-4.13.2-complete.jar im aktuellen Verzeichnis
  
Projekt kompilieren und ausführen:

1. `java -cp "antlr-4.13.2-complete.jar:." org.antlr.v4.Tool -visitor PascalLexer.g4 PascalParser.g4`
2. `javac -cp "antlr-4.13.2-complete.jar:." *.java`
3. `java -cp "antlr-4.13.2-complete.jar:." StupsCompiler -compile <Filename.pas>`
