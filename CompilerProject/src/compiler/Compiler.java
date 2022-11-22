package compiler;

import ast.ParserOperatorPrecedence;
import ast.ast.Program;
import checker.Checker;
import scanner.Scanner;
import scanner.SourceFile;

import javax.swing.*;

public class Compiler {
    private static final String EXAMPLES_DIR = "C:\\Users\\anton\\IdeaProjects\\CMC-Limited\\CompilerProject\\src\\examples\\ex1.txt";

    public static void main(String[] args)
    {
        JFileChooser fc = new JFileChooser( EXAMPLES_DIR );

        if (fc.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION) {
            String sourceName = fc.getSelectedFile().getAbsolutePath();

            SourceFile in = new SourceFile( sourceName );
            Scanner s = new Scanner( in );
            ParserOperatorPrecedence p = new ParserOperatorPrecedence( s );
            Checker c = new Checker();
            Encoder e = new Encoder();

            Program program = p.parseProgram();
            c.check( program );
            e.encode( program );

            String targetName;
            if (sourceName.endsWith( ".txt" )) {
                targetName = sourceName.substring( 0, sourceName.length() - 4 ) + ".tam";
            } else {
                targetName = sourceName + ".tam";
            }

            e.saveTargetProgram( targetName );
        }
    }
}
