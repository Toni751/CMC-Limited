/*
 * 27.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 29.09.2006 Original version
 */
 
package ast.ast;

import java.util.List;

public class Program extends AST {
	public Block block;
	public List<FunctionDeclaration> functions;
	
	public Program(Block block, List<FunctionDeclaration> functions) {
		this.block = block;
		this.functions = functions;
	}
}