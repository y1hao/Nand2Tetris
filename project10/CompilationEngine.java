import java.io.*;
public class CompilationEngine {
	private BufferedWriter bw;
	private JackTokenizer jt;
	
	public CompilationEngine(String fileName, JackTokenizer jt) {
		try {
			File outputFile = new File(fileName.substring(0, fileName.length() - 5) + ".xml");
			bw = new BufferedWriter(new FileWriter(outputFile));
			outputFile.createNewFile();
			this.jt = jt;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startCompilation() {
		while (jt.hasMoreTokens()) {
			jt.advance();
			try {
				compileClass();
			}
			catch(CompileException e) {
				e.printStackTrace();
				try {
					bw.write(e.getMessage() + "\n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void close() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeToken() {
		String tokenType = jt.tokenType();
		String tokenContent = jt.tokenContent();
		String tag = "";
		if (tokenType.equals("KEYWORD")) tag = "keyword";
		if (tokenType.equals("SYMBOL")) tag = "symbol";
		if (tokenType.equals("INT_CONST")) tag = "integerConstant";
		if (tokenType.equals("STRING_CONST")) tag = "stringConstant";
		if (tokenType.equals("IDENTIFIER")) tag = "identifier";
		try {
			bw.write("<" + tag + "> " + tokenContent + " </" + tag + ">\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeTag(String tag) {
		try {
			bw.write("<" + tag + ">\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void checkTokenContent(String token) {
		if (jt.tokenContent().equals(token)) writeToken();
		else throw new CompileException("Compile error: " + token + " is expected");
	}
	
	private void checkTokenContent(String[] tokens) {
		boolean checked = false;
		String expected = "";
		for (int i = 0; i < tokens.length; i++) {
			if (jt.tokenContent().equals(tokens[i])) checked = true;
			expected += " " + tokens[i];
		}
		if (checked) writeToken();
		else throw new CompileException("Compile error: " + expected + " are expected");
	}
	
	private void checkTokenType(String[] tokens, String tokenType) {
		boolean checked = false;
		String expected = "";
		for (int i = 0; i < tokens.length; i++) {
			if (jt.tokenContent().equals(tokens[i])) checked = true;
			expected += " " + tokens[i];
		}
		if (jt.tokenType().equals(tokenType)) checked = true;
		expected += " or " + tokenType;
		if (checked) writeToken();
		else throw new CompileException("Compile error: " + expected + " are expected");
	}
	
	private void checkTokenType(String tokenType) {
		if (jt.tokenType().equals(tokenType)) writeToken();
		else throw new CompileException("Compile error: " + tokenType + " is expected");
	}
	
	private void compileClass() throws CompileException {
		writeTag("class");
		checkTokenContent("class");
		jt.advance();
		checkTokenType("IDENTIFIER");
		jt.advance();
		checkTokenContent("{");
		jt.advance();
		while (jt.tokenContent().equals("field") || jt.tokenContent().equals("static")) {
			compileClassVarDec();
			jt.advance();
		}
		while (jt.tokenContent().equals("constructor") ||
				jt.tokenContent().equals("function") || 
				jt.tokenContent().equals("method")) {
			compileSubroutineDec();
			jt.advance();
		}
		checkTokenContent("}");
		writeTag("/class");
	}
	
	private void compileClassVarDec() throws CompileException {
		writeTag("classVarDec");
		checkTokenContent(new String[]{"field", "static"});
		jt.advance();
		checkTokenType(new String[]{"int", "char", "boolean"}, "IDENTIFIER");
		jt.advance();
		checkTokenType("IDENTIFIER");
		jt.advance();
		while (jt.tokenContent().equals(",")) {
			writeToken();
			jt.advance();
			checkTokenType("IDENTIFIER");
			jt.advance();
		}
		checkTokenContent(";");
		writeTag("/classVarDec");
	}
	
	private void compileSubroutineDec() throws CompileException {
		writeTag("subroutineDec");
		checkTokenContent(new String[]{"constructor", "function", "method"});
		jt.advance();
		checkTokenType(new String[]{"int", "char", "boolean", "void"}, "IDENTIFIER");
		jt.advance();
		checkTokenType("IDENTIFIER");
		jt.advance();
		checkTokenContent("(");
		jt.advance();
		compileParameterListAD();
		checkTokenContent(")");
		jt.advance();
		compileSubroutineBody();
		writeTag("/subroutineDec");
	}
	
	private void compileParameterListAD() throws CompileException {
		writeTag("parameterList");
		if (!jt.tokenContent().equals(")")) {
			checkTokenType(new String[]{"int", "char", "boolean"}, "IDENTIFIER");
			jt.advance();
			checkTokenType("IDENTIFIER");
			jt.advance();
			while (jt.tokenContent().equals(",")) {
				checkTokenContent(",");
				jt.advance();
				checkTokenType(new String[]{"int", "char", "boolean"}, "IDENTIFIER");
				jt.advance();
				checkTokenType("IDENTIFIER");
				jt.advance();
			}
		}
		writeTag("/parameterList");
	}
	
	private void compileSubroutineBody() throws CompileException {
		writeTag("subroutineBody");
		checkTokenContent("{");
		jt.advance();
		while(jt.tokenContent().equals("var")) {
			compileVarDec();
			jt.advance();
		}
		compileStatementsAD();
		checkTokenContent("}");
		writeTag("/subroutineBody");
	}
	
	private void compileVarDec() throws CompileException {
		writeTag("varDec");
		checkTokenContent("var");
		jt.advance();
		checkTokenType(new String[]{"int", "char", "boolean"}, "IDENTIFIER");
		jt.advance();
		checkTokenType("IDENTIFIER");
		jt.advance();
		while(!jt.tokenContent().equals(";")) {
			checkTokenContent(",");
			jt.advance();
			checkTokenType("IDENTIFIER");
			jt.advance();
		}
		checkTokenContent(";");
		writeTag("/varDec");
	}
	
	private void compileStatementsAD() throws CompileException {
		writeTag("statements");
		while(!jt.tokenContent().equals("}")) {
			if (jt.tokenContent().equals("if")) compileIfAD();
			else {
				if (jt.tokenContent().equals("do")) compileDo();
				if (jt.tokenContent().equals("let")) compileLet();
				if (jt.tokenContent().equals("while")) compileWhile();
				if (jt.tokenContent().equals("return")) compileReturn();
				jt.advance();
			}
		}
		writeTag("/statements");
	}
	
	private void compileDo() throws CompileException {
		writeTag("doStatement");
		checkTokenContent("do");
		jt.advance();
		checkTokenType("IDENTIFIER");
		jt.advance();
		if (jt.tokenContent().equals(".")) {
			checkTokenContent(".");
			jt.advance();
			checkTokenType("IDENTIFIER");
			jt.advance();
		}
		checkTokenContent("(");
		jt.advance();
		compileExpressionListAD();
		checkTokenContent(")");
		jt.advance();
		checkTokenContent(";");
		writeTag("/doStatement");
	}
	
	private void compileLet() throws CompileException {
		writeTag("letStatement");
		checkTokenContent("let");
		jt.advance();
		checkTokenType("IDENTIFIER");
		jt.advance();
		if (!jt.tokenContent().equals("=")) {
			checkTokenContent("[");
			jt.advance();
			compileExpressionAD();
			checkTokenContent("]");
			jt.advance();
		}
		checkTokenContent("=");
		jt.advance();
		compileExpressionAD();
		checkTokenContent(";");
		writeTag("/letStatement");
	}
	
	private void compileWhile() throws CompileException {
		writeTag("whileStatement");
		checkTokenContent("while");
		jt.advance();
		checkTokenContent("(");
		jt.advance();
		compileExpressionAD();
		checkTokenContent(")");
		jt.advance();
		checkTokenContent("{");
		jt.advance();
		compileStatementsAD();
		checkTokenContent("}");
		writeTag("/whileStatement");
	}
	
	private void compileReturn() throws CompileException {
		writeTag("returnStatement");
		checkTokenContent("return");
		jt.advance();
		if (jt.tokenContent().equals(";")) checkTokenContent(";");
		else {
			compileExpressionAD();
			checkTokenContent(";");
		}
		writeTag("/returnStatement");
	}
	
	private void compileIfAD() throws CompileException {
		writeTag("ifStatement");
		checkTokenContent("if");
		jt.advance();
		checkTokenContent("(");
		jt.advance();
		compileExpressionAD();
		checkTokenContent(")");
		jt.advance();
		checkTokenContent("{");
		jt.advance();
		compileStatementsAD();
		checkTokenContent("}");
		jt.advance();
		if (jt.tokenContent().equals("else")) {
			checkTokenContent("else");
			jt.advance();
			checkTokenContent("{");
			jt.advance();
			compileStatementsAD();
			checkTokenContent("}");
			jt.advance();
		}
		writeTag("/ifStatement");
	}
	
	private void compileExpressionAD() throws CompileException {
		writeTag("expression");
		compileTermAD();
		while(jt.tokenContent().equals("+") || 
				jt.tokenContent().equals("-") ||
				jt.tokenContent().equals("*") || 
				jt.tokenContent().equals("/") ||
				jt.tokenContent().equals("&amp;") ||
				jt.tokenContent().equals("|") ||
				jt.tokenContent().equals("&gt;") ||
				jt.tokenContent().equals("&lt;") ||
				jt.tokenContent().equals("=")) {
			checkTokenContent(new String[]{"+", "-", "*", "/", "&amp;", "|", "&gt;", "&lt;", "="});
			jt.advance();
			compileTermAD();
		}
		writeTag("/expression");
	}
	
	private void compileTermAD() throws CompileException {
		writeTag("term");
		if (jt.tokenType().equals("INT_CONST")) {
			checkTokenType("INT_CONST");
			jt.advance();
		}
		else if (jt.tokenType().equals("STRING_CONST")) {
			checkTokenType("STRING_CONST");
			jt.advance();
		}
		else if (jt.tokenType().equals("KEYWORD")) {
			checkTokenType("KEYWORD");
			jt.advance();
		}
		else if (jt.tokenContent().equals("(")) {
			checkTokenContent("(");
			jt.advance();
			compileExpressionAD();
			checkTokenContent(")");
			jt.advance();
		}
		else if (jt.tokenContent().equals("-") || jt.tokenContent().equals("~")) {
			checkTokenContent(new String[]{"-", "~"});
			jt.advance();
			compileTermAD();
		}
		else {
			checkTokenType("IDENTIFIER");
			jt.advance();
			if (jt.tokenContent().equals("(")) {
				checkTokenContent("(");
				jt.advance();
				compileExpressionListAD();
				checkTokenContent(")");
				jt.advance();
			}
			if (jt.tokenContent().equals("[")) {
				checkTokenContent("[");
				jt.advance();
				compileExpressionAD();
				checkTokenContent("]");
				jt.advance();
			}
			if (jt.tokenContent().equals(".")) {
				checkTokenContent(".");
				jt.advance();
				checkTokenType("IDENTIFIER");
				jt.advance();
				checkTokenContent("(");
				jt.advance();
				compileExpressionListAD();
				checkTokenContent(")");
				jt.advance();
			}
		}
		writeTag("/term");
	}
	
	private void compileExpressionListAD() throws CompileException {
		writeTag("expressionList");
		if (!jt.tokenContent().equals(")")) {
			compileExpressionAD();
			while (jt.tokenContent().equals(",")) {
				checkTokenContent(",");
				jt.advance();
				compileExpressionAD();
			}
		}
		writeTag("/expressionList");
	}
	
}
