/*
 * 27.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 01.10.2006 Original version
 */
 
package ast.ast;


public class IntLitExpression
	extends Expression
{
	public IntegerLiteral literal;
	
	
	public IntLitExpression( IntegerLiteral literal )
	{
		this.literal = literal;
	}
}