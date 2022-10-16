/*
 * 13.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 29.09.2006 Original version (based on Watt&Chase)
 */
 
package ast.ast;


import checker.Visitor;

public abstract class AST
{
    public abstract Object visit(Visitor v, Object arg);
}