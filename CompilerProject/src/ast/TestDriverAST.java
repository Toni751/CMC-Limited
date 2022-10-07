/*
 * 27.09.2016 Minor edit
 * 11.10.2010 Various changes
 * 21.10.2009 New folder structure
 * 29.09.2006 Original version
 */
 
 
package ast;
import ast.ast.AST;
import scanner.Scanner;
import scanner.SourceFile;

import javax.swing.*;


public class TestDriverAST
{
	private static final String EXAMPLES_DIR = "C:\\Users\\Ana-Maria\\IdeaProjects\\CMC-Limited\\CompilerProject\\src\\examples\\ex1.txt";
	
	
	public static void main( String args[] )
	{
		JFileChooser fc = new JFileChooser( EXAMPLES_DIR );
		
		if( fc.showOpenDialog( null ) == fc.APPROVE_OPTION ) {
			SourceFile in = new SourceFile(fc.getSelectedFile().getAbsolutePath());
			Scanner s = new Scanner(in);
			ParserAST p = new ParserAST(s);
			AST ast = p.parseProgram();
			// new ASTViewer(ast);
		}
	}
}