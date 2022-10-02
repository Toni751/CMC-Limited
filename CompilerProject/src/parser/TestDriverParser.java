package parser;

import scanner.Scanner;
import scanner.SourceFile;

import javax.swing.*;

public class TestDriverParser {
    private static final String EXAMPLES_DIR = "C:\\Users\\anton\\IdeaProjects\\CMC-Limited\\CompilerProject\\src\\examples\\ex1.txt";

    public static void main(String args[]) {
        JFileChooser fc = new JFileChooser(EXAMPLES_DIR);

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            SourceFile in = new SourceFile(fc.getSelectedFile().getAbsolutePath());
            Scanner s = new Scanner(in);
            Parser p = new Parser(s);

            p.parseProgram();
        }
    }
}
