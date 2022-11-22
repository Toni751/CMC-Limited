/*
 * 27.09.2016 Minor edit
 * 08.10.2010 Original Version
 */
 
package ast;

import ast.ast.*;
import ast.ast.Character;
import ast.ast.Number;


import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;


public class ASTViewer extends JFrame
{
	private static final Font NODE_FONT = new Font( "Verdana", Font.PLAIN, 24 );


	public ASTViewer( AST ast )
	{
		super( "Abstract Syntax Tree" );

		DefaultMutableTreeNode root = createTree( ast );
		JTree tree = new JTree( root );
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setFont( NODE_FONT );
		tree.setCellRenderer( renderer );

		add( new JScrollPane( tree ) );

		setSize( 1024, 768 );
		setVisible( true );

		setDefaultCloseOperation( EXIT_ON_CLOSE );
	}


	private DefaultMutableTreeNode createTree( AST ast )
	{
		DefaultMutableTreeNode node = new DefaultMutableTreeNode( "*** WHAT??? ***" );

		if( ast == null )
			node.setUserObject( "*** NULL ***" );
		else if( ast instanceof Program) {
			node.setUserObject( "Program" );
			node.add( createTree( ((Program)ast).block ) );
            for( FunctionDeclaration fd: ((Program)ast).functions )
                node.add( createTree( fd ) );
		} else if( ast instanceof Block) {
			node.setUserObject( "Block" );
            for( BlockItem d: ((Block)ast).blockItems )
                node.add( createTree( d ) );
		} else if( ast instanceof VariableDeclaration ) {
			node.setUserObject( "VariableDeclaration" );
            node.add( createTree( ((VariableDeclaration)ast).type ) );
            node.add( createTree( ((VariableDeclaration)ast).identifier ) );
		} else if( ast instanceof VariableInitialization ) {
            node.setUserObject( "VariableInitialization" );
            node.add( createTree( ((VariableInitialization)ast).type ) );
            node.add( createTree( ((VariableInitialization)ast).identifier ) );
            node.add( createTree( ((VariableInitialization)ast).value ) );
            node.add( createTree( ((VariableInitialization)ast).valueList ) );
        } else if( ast instanceof ReadCharDeclaration ) {
            node.setUserObject( "ReadCharDeclaration" );
            node.add( createTree( ((ReadCharDeclaration)ast).identifier ) );
        } else if( ast instanceof ReadNumDeclaration ) {
            node.setUserObject( "ReadNumDeclaration" );
            node.add( createTree( ((ReadNumDeclaration)ast).identifier ) );
        } else if( ast instanceof FunctionDeclaration ) {
			node.setUserObject( "FunctionDeclaration" );
			node.add( createTree( ((FunctionDeclaration)ast).identifier ) );
			node.add( createTree( ((FunctionDeclaration)ast).varList ) );
			node.add( createTree( ((FunctionDeclaration)ast).block ) );
		} else if( ast instanceof IfStatement ) {
			node.setUserObject( "IfStatement" );
			node.add( createTree( ((IfStatement)ast).cmpr ) );
			node.add( createTree( ((IfStatement)ast).thenBlock ) );
			node.add( createTree( ((IfStatement)ast).elseBlock ) );
		} else if( ast instanceof DoStatement ) {
			node.setUserObject( "DoStatement" );
			node.add( createTree( ((DoStatement)ast).block) );
			node.add( createTree( ((DoStatement)ast).comparison ) );
		} else if( ast instanceof ShowStatement ) {
			node.setUserObject( "ShowStatement" );
			node.add( createTree( ((ShowStatement)ast).value ) );
		} else if( ast instanceof Operation ) {
			node.setUserObject( "Operation" );
			node.add( createTree( ((Operation)ast).operator ) );
			node.add( createTree( ((Operation)ast).operand1 ) );
			node.add( createTree( ((Operation)ast).operand2 ) );
		} else if( ast instanceof Comparison ) {
            node.setUserObject( "Comparison" );
            node.add( createTree( ((Comparison)ast).comparator ) );
            node.add( createTree( ((Comparison)ast).operand1 ) );
            node.add( createTree( ((Comparison)ast).operand2 ) );
        } else if( ast instanceof FunctionCall ) {
			node.setUserObject( "FunctionCall" );
			node.add( createTree( ((FunctionCall)ast).identifier ) );
			node.add( createTree( ((FunctionCall)ast).valueList ) );
		} else if( ast instanceof ValueList ) {
            node.setUserObject( "ValueList" );
            for( Value d: ((ValueList)ast).values )
                node.add( createTree( d ) );
        } else if( ast instanceof VariableList ) {
			node.setUserObject( "VariableList" );
			for( VariableDeclaration d: ((VariableList)ast).variables )
				node.add( createTree( d ) );
		} else if( ast instanceof Identifier ) {
			node.setUserObject( "Identifier " + ((Identifier)ast).spelling );
		} else if( ast instanceof CharacterValue ) {
			node.setUserObject( "CharacterValue");
            node.add( createTree( ((CharacterValue)ast).character ) );
        } else if( ast instanceof NumberValue ) {
            node.setUserObject( "NumberValue");
            node.add( createTree( ((NumberValue)ast).number ) );
        } else if( ast instanceof VarValue ) {
            node.setUserObject( "VarValue");
            node.add( createTree( ((VarValue)ast).name ) );
        } else if( ast instanceof Operator ) {
			node.setUserObject( "Operator " + ((Operator)ast).spelling );
		} else if( ast instanceof Comparator ) {
            node.setUserObject( "Comparator " + ((Comparator)ast).spelling );
        } else if( ast instanceof Number) {
            node.setUserObject( "Number " + ((Number)ast).spelling );
        } else if( ast instanceof Character) {
            node.setUserObject( "Character " + ((Character)ast).spelling );
        } else if( ast instanceof ast.ast.Type) {
			node.setUserObject( "Type " + ((ast.ast.Type)ast).spelling );
		}
        return node;
	}
}