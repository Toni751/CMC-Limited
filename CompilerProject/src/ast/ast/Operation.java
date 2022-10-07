/*
 * 13.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 01.10.2006 Original version
 */
 
package ast.ast;


public class Operation extends Value {
	public Operator operator;
	public Value operand1;
	public Value operand2;
	
	public Operation(Operator operator, Value operand1, Value operand2) {
		this.operator = operator;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}
}