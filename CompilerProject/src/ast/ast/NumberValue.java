/*
 * 27.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 01.10.2006 Original version
 */
 
package ast.ast;


import checker.Visitor;

public class NumberValue extends Value {
	public Number number;
	
	public NumberValue(Number number) {
		this.number = number;
	}

	@Override
	public Object visit(Visitor v, Object arg) {
		return v.visitNumberValue(this, arg);
	}
}