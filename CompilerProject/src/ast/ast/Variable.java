/*
 * 27.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 29.09.2006 Original version
 */
 
package ast.ast;


public class Variable
{
	public Identifier id;
	public Type type;

	public Variable(Type type, Identifier id )
	{
		this.type = type;
		this.id = id;
	}
}