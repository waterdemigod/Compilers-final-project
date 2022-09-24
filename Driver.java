import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import java.lang.Exception;
//import java.FileWriter;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.*;

public class Driver {

	static MapOfIns Moi = new MapOfIns();

	public static void main(String[] args) throws IOException {
		String output = "group10.out";

		FileWriter myWriter = new FileWriter(output);

		// Creates a CharStream from the input file
		CharStream input = CharStreams.fromStream(System.in);

		LittleLexer lexer = new LittleLexer(input); // Creates a Lexer
		CommonTokenStream tokens = new CommonTokenStream((TokenSource) lexer); // Generates a tokenstream from the Lexer
		LittleParser parser = new LittleParser(tokens); // Generates a parser to identify program

		ParseTreeWalker walker = new ParseTreeWalker();

		ParseTree tree = parser.program();

		SymbolExtractor SE = new SymbolExtractor();

		walker.walk(SE, tree);
		// output = SE.PrintSymbolExtractor();
		// System.out.println(output);
		myWriter.write(output);

		myWriter.close();
		// System.out.printf((parser.getNumberOfSyntaxErrors() < 1) ? "Accepted" : "Not
		// accepted");
		System.out.println("sys halt");
	}

	public static class SymbolExtractor extends LittleBaseListener {

		private Stack<SymbolTable> SymbolTableStack;

		private String nullString = "";

		private SymbolTable currentTable;

		private int bcount;

		private ArrayList<SymbolTable> printList;

		private String varType;

		public SymbolExtractor() {
			this.SymbolTableStack = new Stack<SymbolTable>();
			this.printList = new ArrayList<SymbolTable>();
			this.currentTable = null;
			this.bcount = 1;
			this.varType = "";

		}

		public void pushToLists(SymbolTable nTable) {
			this.SymbolTableStack.push(nTable);
			this.printList.add(nTable);
		}

		private int getBcount() {
			return this.bcount;
		}

		private void setBcount(int n) {
			this.bcount = n;
		}

		private void newBlock() {
			pushToLists(new SymbolTable("BLOCK " + this.bcount));
			this.setBcount(this.bcount + 1);
			this.currentTable = this.SymbolTableStack.peek();
		}

		public void enterProgram(LittleParser.ProgramContext ctx) {

			pushToLists(new SymbolTable("GLOBAL"));
			this.currentTable = this.SymbolTableStack.peek();

		}

		public String getVarType(String val) {

			String ret = val;

			for (int i = 0; i < this.printList.size(); i++) {
				ret = this.printList.get(i).getType(val);

				if (!ret.equals(""))
					return ret;

			}
			return ret;
		}

		@Override
		public void enterString_decl(LittleParser.String_declContext ctx) {

			this.currentTable.addSymbol(ctx.id().IDENTIFIER().getText(),
					new SymbolAttributes("STRING", ctx.str().STRINGLITERAL().getText()));
			String str_decl = "str " + ctx.id().IDENTIFIER().getText() + " " + ctx.str().STRINGLITERAL().getText();
			System.out.println(str_decl);
		}

		@Override
		public void enterVar_decl(LittleParser.Var_declContext ctx) {
			this.varType = ctx.var_type().getText();
			String idlist = ctx.id_list().getText();
			String[] idlistSplit = idlist.split(",");
			for (int i = 0; i < idlistSplit.length; i++) {
				String Var = idlistSplit[i];
				this.currentTable.addSymbol(Var, new SymbolAttributes(this.varType));
				System.out.println("var " + Var);
			}
		}

		/*
		 * @Override
		 * public void enterId_tail(LittleParser.Id_tailContext ctx) {
		 * try {
		 * if (!(ctx.getParent().getParent().getText().contains("WRITE")
		 * || ctx.getParent().getParent().getText().contains("READ")))
		 * this.currentTable.addSymbol(ctx.id().IDENTIFIER().getText(),
		 * new SymbolAttributes(this.varType));
		 * System.out.println("Burger");
		 * } catch (NullPointerException e) {
		 * }
		 * }
		 */
		@Override
		public void enterParam_decl_tail(LittleParser.Param_decl_tailContext ctx) {
			try {
				this.currentTable.addSymbol(ctx.param_decl().id().IDENTIFIER().getText(),
						new SymbolAttributes(ctx.param_decl().var_type().getText()));
			} catch (NullPointerException e) {
			}
		}

		@Override
		public void enterFunc_declarations(LittleParser.Func_declarationsContext ctx) {
			String name = null;
			try {

				name = "" + ctx.func_decl().id().IDENTIFIER().getText();

			} catch (Exception e) {
			}
			if (name != null) {
				pushToLists(new SymbolTable(name));
				this.currentTable = this.SymbolTableStack.peek();
			}
			// parm_decl_list
			try {
				this.currentTable.addSymbol(ctx.func_decl().param_decl_list().param_decl().id().IDENTIFIER().getText(),
						new SymbolAttributes(ctx.func_decl().param_decl_list().param_decl().var_type().getText()));
			} catch (NullPointerException e) {
			}
		}

		@Override
		public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
			newBlock();
			// System.out.println("if");
			// if(ctx.else_part().)
		}

		@Override
		public void enterElse_part(LittleParser.Else_partContext ctx) {
			// System.out.println("else");
			if (ctx.getText().contains("ELSE")) {
				newBlock();
			}
		}

		@Override
		public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {
			String Var = ctx.id().getText();
			String eq = Var + " " + ctx.children.get(1).getText() + " " + ctx.children.get(2).getText();
			String equation = addSpaces(eq);
			String type = getVarType(Var);
			Moi.setMapofIns(equation, type);
			String tiny = Moi.getTinyCode(equation);
			System.out.println(tiny);
		}

		@Override
		public void exitAssign_expr(LittleParser.Assign_exprContext ctx) {
		}

		public String PrintSymbolExtractor() {
			String ret = "";
			for (SymbolTable i : printList) {

				ret = ret + (i.PrintSymbolTable());
			}
			return ret;
		}

		@Override
		public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {
			int index = 1;
			String expression = addSpaces(ctx.children.get(2).getText());
			String[] SplWrSt = expression.split(" ");
			for (int i = 0; i < SplWrSt.length; i++) {
				String Var = SplWrSt[i];
				String equation = ("READ " + Var);
				String type = getVarType(Var);
				Moi.setMapofIns(equation, type);
				String tiny = Moi.getTinyCode(equation);
				System.out.println(tiny);
			}
		}

		@Override
		public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
			int index = 1;
			String expression = addSpaces(ctx.children.get(2).getText());
			String[] SplWrSt = expression.split(" ");
			for (int i = 0; i < SplWrSt.length; i++) {
				String Var = SplWrSt[i];
				String equation = ("WRITE " + Var);
				String type = getVarType(Var);
				Moi.setMapofIns(equation, type);
				String tiny = Moi.getTinyCode(equation);
				System.out.println(tiny);
			}
		}

		public String addSpaces(String expression) {

			if (expression.contains("(")) {
				expression = expression.replace("(", "");
			}

			if (expression.contains(")")) {
				expression = expression.replace(")", "");
			}

			if (expression.contains(",")) {
				expression = expression.replace(",", " ");
			}

			if (expression.contains("+")) {
				expression = expression.replace("+", " + ");
			} else if (expression.contains("-")) {
				expression = expression.replace("-", " - ");
			} else if (expression.contains("/")) {
				expression = expression.replace("/", " / ");
			} else if (expression.contains("*")) {
				expression = expression.replace("*", " * ");
			} else {
				expression = expression;
			}

			return expression;

		}

		public class SymbolTable {

			private String scope;

			private HashMap<String, SymbolAttributes> SymbolTable;

			private ArrayList<String> SymbolNames;

			public SymbolTable(String scope) {
				this.scope = scope;
				this.SymbolTable = new HashMap<String, SymbolAttributes>();
				this.SymbolNames = new ArrayList<String>();
			}

			public String getScope() {
				return this.scope;

			}

			public void addSymbol(String symbolName, SymbolAttributes attribute) {
				if (this.SymbolTable.containsKey(symbolName)) {
					System.out.printf("DECLERATION ERROR %s\n", symbolName);
					System.exit(0);
				}

				this.SymbolTable.put(symbolName, attribute);
				this.SymbolNames.add(symbolName);
			}

			public String PrintSymbolTable() {
				String ret = "\nSymbol Table " + this.scope + "\n";

				SymbolAttributes attr = null;
				String name = "";
				for (int i = 0; i < SymbolNames.size(); i++) {
					name = SymbolNames.get(i);
					attr = SymbolTable.get(name);
					if (attr.value.equals("")) {
						ret = ret + "name " + name + " type " + attr.type + "\n";
					} else {
						ret = ret + "name " + name + " type " + attr.type + " value " + attr.value + "\n";
					}
				}

				return ret;

			}

			public String getType(String val) {
				String ret = "";
				if (this.SymbolTable.containsKey(val))
					ret = this.SymbolTable.get(val).getType();

				return ret;
			}
		}

		public class SymbolAttributes {
			String type;
			String value;

			public SymbolAttributes(String type) {
				this.type = type;
				this.value = "";
			}

			public SymbolAttributes(String type, String value) {
				this.type = type;
				this.value = value;
			}

			public String getType() {
				return this.type;
			}

			public String getValue() {
				return this.value;

			}
		}
	}
}
