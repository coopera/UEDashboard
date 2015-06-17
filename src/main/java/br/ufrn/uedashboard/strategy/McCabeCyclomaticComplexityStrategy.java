package br.ufrn.uedashboard.strategy;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;

import br.ufrn.uedashboard.visitor.McCabeVisitor;

public class McCabeCyclomaticComplexityStrategy implements CyclomaticComplexityCalculatorStrategy {
	private ASTParser parser;
	private Block compilationUnit;
	private McCabeVisitor visitor;
	
	@SuppressWarnings("deprecation")
	public McCabeCyclomaticComplexityStrategy(String methodBody) {
		parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(methodBody.toCharArray());
		parser.setKind(ASTParser.K_STATEMENTS);
		compilationUnit = (Block) parser.createAST(null);
		visitor = new McCabeVisitor();
		compilationUnit.accept(visitor);
	}
	
	@Override
	public int getCyclomaticComplexity() {
		return visitor.getCyclomaticComplexity();
	}

}
