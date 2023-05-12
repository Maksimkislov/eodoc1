// Generated from EOlang.g4 by ANTLR 4.12.0
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link EOlangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface EOlangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link EOlangParser#file_input}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile_input(EOlangParser.File_inputContext ctx);
	/**
	 * Visit a parse tree produced by {@link EOlangParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(EOlangParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link EOlangParser#simple_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_stmt(EOlangParser.Simple_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link EOlangParser#compound_stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompound_stmt(EOlangParser.Compound_stmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link EOlangParser#suite}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuite(EOlangParser.SuiteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code namingExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamingExpr(EOlangParser.NamingExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code objectExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectExpr(EOlangParser.ObjectExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code abstractExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAbstractExpr(EOlangParser.AbstractExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parensExpr}
	 * labeled alternative in {@link EOlangParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParensExpr(EOlangParser.ParensExprContext ctx);
}