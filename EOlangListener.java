// Generated from EOlang.g4 by ANTLR 4.12.0
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link EOlangParser}.
 */
public interface EOlangListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link EOlangParser#file_input}.
	 * @param ctx the parse tree
	 */
	void enterFile_input(EOlangParser.File_inputContext ctx);
	/**
	 * Exit a parse tree produced by {@link EOlangParser#file_input}.
	 * @param ctx the parse tree
	 */
	void exitFile_input(EOlangParser.File_inputContext ctx);
	/**
	 * Enter a parse tree produced by {@link EOlangParser#stmt}.
	 * @param ctx the parse tree
	 */
	void enterStmt(EOlangParser.StmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link EOlangParser#stmt}.
	 * @param ctx the parse tree
	 */
	void exitStmt(EOlangParser.StmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link EOlangParser#simple_stmt}.
	 * @param ctx the parse tree
	 */
	void enterSimple_stmt(EOlangParser.Simple_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link EOlangParser#simple_stmt}.
	 * @param ctx the parse tree
	 */
	void exitSimple_stmt(EOlangParser.Simple_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link EOlangParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void enterCompound_stmt(EOlangParser.Compound_stmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link EOlangParser#compound_stmt}.
	 * @param ctx the parse tree
	 */
	void exitCompound_stmt(EOlangParser.Compound_stmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link EOlangParser#suite}.
	 * @param ctx the parse tree
	 */
	void enterSuite(EOlangParser.SuiteContext ctx);
	/**
	 * Exit a parse tree produced by {@link EOlangParser#suite}.
	 * @param ctx the parse tree
	 */
	void exitSuite(EOlangParser.SuiteContext ctx);
	/**
	 * Enter a parse tree produced by the {@code namingExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNamingExpr(EOlangParser.NamingExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code namingExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNamingExpr(EOlangParser.NamingExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code objectExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterObjectExpr(EOlangParser.ObjectExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code objectExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitObjectExpr(EOlangParser.ObjectExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code abstractExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAbstractExpr(EOlangParser.AbstractExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code abstractExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAbstractExpr(EOlangParser.AbstractExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parensExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParensExpr(EOlangParser.ParensExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parensExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParensExpr(EOlangParser.ParensExprContext ctx);
}