/*
 * 27.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 29.09.2006 Original version
 */
 
package ast.ast;


import checker.Visitor;

public class ShowStatement extends Statement {
	public Identifier value;

	public ShowStatement(Identifier value) {
		this.value = value;
	}

	@Override
	public Object visit(Visitor v, Object arg) {
		return v.visitShowStatement(this, arg);
	}
}