/*
 * 27.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 23.10.2006 IdList replaced by Declarations for function parameters
 * 29.09.2006 Original version
 */
 
package ast.ast;


import checker.Visitor;

public class FunctionCall extends Statement {
	public Identifier identifier;
	public ValueList valueList;
	public FunctionDeclaration declaration;

	public FunctionCall(Identifier identifier, ValueList valueList) {
		this.identifier = identifier;
		this.valueList = valueList;
	}

	@Override
	public Object visit(Visitor v, Object arg) {
		return v.visitFunctionCall(this, arg);
	}
}