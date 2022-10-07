/*
 * 27.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 29.09.2006 Original version
 */
 
package ast.ast;


public class IfStatement extends Statement {
	public Comparison cmpr;
	public Block thenBlock;
	public Block elseBlock;
	
	public IfStatement(Comparison cmpr, Block thenBlock, Block elseBlock) {
		this.cmpr = cmpr;
		this.thenBlock = thenBlock;
		this.elseBlock = elseBlock;
	}
}