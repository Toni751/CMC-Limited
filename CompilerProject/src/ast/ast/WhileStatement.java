/*
 * 27.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 29.09.2006 Original version
 */
 
package ast.ast;


public class WhileStatement
	extends Statement
{
	public Expression exp;
	public Statements stats;
	
	
	public WhileStatement( Expression exp, Statements stats )
	{
		this.exp = exp;
		this.stats = stats;
	}
}