/*
 * 13.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 01.10.2006 Original version
 */
 
package ast.ast;


public class BinaryExpression
	extends Expression
{
	public Operator operator;
	public Expression operand1;
	public Expression operand2;
	
	
	public BinaryExpression( Operator operator, Expression operand1, Expression operand2 )
	{
		this.operator = operator;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}
}