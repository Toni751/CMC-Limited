/*
 * 13.09.2016 Minor edit
 * 11.10.2010 dump() removed
 * 21.10.2009 New folder structure
 * 29.09.2006 Original version
 */
 
package ast.ast;


import java.util.ArrayList;
import java.util.List;

public class Block extends AST {
	public List<BlockItem> blockItems;

	public Block() {
		blockItems = new ArrayList<>();
	}
}