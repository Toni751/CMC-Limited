/*
 * 27.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 29.09.2006 Original version
 */
 
package ast.ast;


public class ExpressionStatement
	extends Statement
{
	public Expression exp;
	
	
	public ExpressionStatement( Expression exp )
	{
		this.exp = exp;
	}
}