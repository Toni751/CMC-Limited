/*
 * 27.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 08.10.2010 name made public
 * 21.10.2009 New folder structure
 * 01.10.2006 Original version
 */

package ast.ast;


public class VarValue extends Value {
    public Identifier name;

    public VarValue(Identifier name) {
        this.name = name;
    }
}