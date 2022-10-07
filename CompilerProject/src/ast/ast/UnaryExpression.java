/*
 * 27.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 01.10.2006 Original version
 */
 
package ast.ast;


public class UnaryExpression
	extends Expression
{
	public Operator operator;
	public Expression operand;
	
	
	public UnaryExpression( Operator operator, Expression operand )
	{
		this.operator = operator;
		this.operand = operand;
	}
}