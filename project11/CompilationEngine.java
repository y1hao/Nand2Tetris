public class CompilationEngine {
	private JackTokenizer jt;
	private VMWriter vw;
	private SymbolTable stClass;
	private SymbolTable stSub;
	private String className;
	private String subName;
	private int labelNum = 0;
	
	public CompilationEngine(String fileName) {
		this.jt = new JackTokenizer(fileName);
		this.vw = new VMWriter(fileName);
		if (jt.hasMoreTokens()) 
			jt.advance();
	}
	
	public void compile() {
		try {
			compileClass();
			vw.close();
		}
		catch(CompileException e) {
			vw.writeErrorInformation(e);
		}
	}
	
	private String check(String token) throws CompileException {
		String tokenContent;
		if (jt.tokenContent().equals(token)) {
			tokenContent = jt.tokenContent();
			jt.advance();
		}	
		else throw new CompileException(token + " is expected");
		return tokenContent;
	}
	
	private String check(String[] tokens) throws CompileException{
		String tokenContent;
		boolean checked = false;
		String expected = "";
		for (int i = 0; i < tokens.length; i++) {
			if (jt.tokenContent().equals(tokens[i])) checked = true;
			expected += " " + tokens[i];
		}
		if (checked) {
			tokenContent = jt.tokenContent();
			jt.advance();
		}
		else throw new CompileException(expected + " are expected");
		return tokenContent;
	}
	
	private String checkType(String[] tokens, String tokenType) throws CompileException {
		String tokenContent;
		boolean checked = false;
		String expected = "";
		for (int i = 0; i < tokens.length; i++) {
			if (jt.tokenContent().equals(tokens[i])) checked = true;
			expected += " " + tokens[i];
		}
		if (jt.tokenType().equals(tokenType)) 
			checked = true;
		expected += " or " + tokenType;
		if (checked){
			tokenContent = jt.tokenContent();
			jt.advance();
		}
		else throw new CompileException(expected + " are expected");
		return tokenContent;
	}
	
	private String checkType(String tokenType) throws CompileException {
		String tokenContent;
		if (jt.tokenType().equals(tokenType)) {
			tokenContent = jt.tokenContent();
			jt.advance();
		}
		else throw new CompileException(tokenType + " is expected");
		return tokenContent;
	}
	
	private void compileClass() throws CompileException {  // done
		stClass = new SymbolTable();
		check("class");
		className = checkType("IDENTIFIER");
		check("{");
		while (jt.tokenContent().equals("field") || jt.tokenContent().equals("static")) {
			compileClassVarDec();
		}
		while (jt.tokenContent().equals("constructor") ||
				jt.tokenContent().equals("function") || 
				jt.tokenContent().equals("method")) {
			compileSubroutineDec();
		}
		if (!jt.tokenContent().equals("}"))
			throw new CompileException("Compile error: } is expected");
	}
	
	private void compileClassVarDec() throws CompileException {  // fields and statics, done
		String kind = check(new String[]{"field", "static"});
		String type = checkType(new String[]{"int", "char", "boolean"}, "IDENTIFIER");
		String name = checkType("IDENTIFIER");
		stClass.define(name, type, kind);
		while (jt.tokenContent().equals(",")) {
			check(",");
			name = checkType("IDENTIFIER");
			stClass.define(name, type, kind);
		}
		check(";");
	}
	
	private void compileSubroutineDec() throws CompileException {
		stSub = new SymbolTable();
		String kind = check(new String[]{"constructor", "function", "method"});
		String type = checkType(new String[]{"int", "char", "boolean", "void"}, "IDENTIFIER");
		subName = checkType("IDENTIFIER");
		if (kind.equals("method"))
			stSub.define("this", type, "arg");
		check("(");
		compileParameterList();
		check(")");
		compileSubroutineBody(kind);
	}
	
	private void compileParameterList() throws CompileException {  // args, done
		if (!jt.tokenContent().equals(")")) {
			String kind = "arg";
			String type = checkType(new String[]{"int", "char", "boolean"}, "IDENTIFIER");
			String name = checkType("IDENTIFIER");
			stSub.define(name, type, kind);
			while (jt.tokenContent().equals(",")) {
				check(",");
				type = checkType(new String[]{"int", "char", "boolean"}, "IDENTIFIER");
				name = checkType("IDENTIFIER");
				stSub.define(name, type, kind);
			}
		}
	}
	
	private void compileSubroutineBody(String kind) throws CompileException {  // done
		check("{");
		while(jt.tokenContent().equals("var")) {
			compileVarDec();
		}
		vw.writeComment("Subroutine declaration:");
		vw.writeFunction(className + "." + subName, stSub.varCount("var"));
		if (kind.equals("constructor")) {
			vw.writePush("constant", stClass.varCount("field"));
			vw.writeCall("Memory.alloc", 1);
			vw.writePop("pointer", 0);
		}
		else if (kind.equals("method")) {
			vw.writePush("arg", 0);
			vw.writePop("pointer", 0);
		}
		compileStatements();
		check("}");
	}
	
	private void compileVarDec() throws CompileException {  // locals, done
		String kind = check("var");
		String type = checkType(new String[]{"int", "char", "boolean"}, "IDENTIFIER");
		String name = checkType("IDENTIFIER");
		stSub.define(name, type, kind);
		while(!jt.tokenContent().equals(";")) {
			check(",");
			name = checkType("IDENTIFIER");
			stSub.define(name, type, kind);
		}
		check(";");
	}
	
	private void compileStatements() throws CompileException {  // done
		while(!jt.tokenContent().equals("}")) {
			if (jt.tokenContent().equals("if")) compileIf();
			if (jt.tokenContent().equals("do")) compileDo();
			if (jt.tokenContent().equals("let")) compileLet();
			if (jt.tokenContent().equals("while")) compileWhile();
			if (jt.tokenContent().equals("return")) compileReturn();
		}
	}
	
	private void compileDo() throws CompileException {
		vw.writeComment("do");
		check("do");
		String obName = null;
		String funcName = checkType("IDENTIFIER");
		if (jt.tokenContent().equals(".")) {
			check(".");
			obName = funcName;
			funcName = checkType("IDENTIFIER");
		}
		int numThis = 0;
		if (obName == null) {
			vw.writePush("pointer", 0); 
			obName = className;
			numThis++;
		}
		else if (stSub.contains(obName) || stClass.contains(obName)) {
			String segment = "";
			int index = 0;
			if (stSub.contains(obName)) {
				segment = stSub.kindOf(obName);
				index = stSub.indexOf(obName);
				obName = stSub.typeOf(obName);
			}	
			else {
				segment = stClass.kindOf(obName);
				index = stClass.indexOf(obName);
				obName = stClass.typeOf(obName);
			}
			vw.writePush(segment, index);
			numThis++;
		}
		check("(");
		int nArgs = compileExpressionList() + numThis;
		check(")");
		vw.writeCall(obName + "." + funcName, nArgs);//
		vw.writePop("temp", 0);
		check(";");
	}
	
	private void compileLet() throws CompileException {
		vw.writeComment("let");
		check("let");
		String name = checkType("IDENTIFIER");
		if (jt.tokenContent().equals("[")) { // array
			if (stSub.contains(name)) 
				vw.writePush(stSub.kindOf(name), stSub.indexOf(name));
			else vw.writePush(stClass.kindOf(name), stClass.indexOf(name));
			check("[");
			compileExpression();
			check("]");
			vw.writeArithmetic("add");
			check("=");
			compileExpression();
			check(";");
			vw.writePop("temp", 0);
			vw.writePop("pointer", 1);
			vw.writePush("temp", 0);
			vw.writePop("that", 0);
		}
		else {  // non-array
			check("=");
			compileExpression();
			check(";");
			if (stSub.contains(name)) 
				vw.writePop(stSub.kindOf(name), stSub.indexOf(name));
			else vw.writePop(stClass.kindOf(name), stClass.indexOf(name));
		}
	}
	
	private void compileWhile() throws CompileException {
		int n = labelNum++;
		vw.writeComment("while");
		check("while");
		vw.writeLabel("L1@" + className + "#" + n);
		check("(");
		compileExpression();
		check(")");
		vw.writeArithmetic("not");
		vw.writeIf("L2@" + className + "#" + n);
		check("{");
		compileStatements();
		check("}");
		vw.writeGoto("L1@" + className + "#" + n);
		vw.writeLabel("L2@" + className + "#" + n);
	}
	
	private void compileReturn() throws CompileException {
		vw.writeComment("return");
		check("return");
		if (jt.tokenContent().equals(";")) 
			vw.writePush("constant", 42);
		else compileExpression();
		check(";");
		vw.writeReturn();
	}
	
	private void compileIf() throws CompileException {
		int n = labelNum++;
		vw.writeComment("if");
		check("if");
		check("(");
		compileExpression();
		check(")");
		vw.writeArithmetic("not");
		vw.writeIf("L1@" + className + "#" + n);
		check("{");
		compileStatements();
		check("}");
		vw.writeGoto("L2@" + className + "#" + n);
		vw.writeLabel("L1@" + className + "#" + n);
		if (jt.tokenContent().equals("else")) {
			check("else");
			check("{");
			compileStatements();
			check("}");
		}
		vw.writeLabel("L2@" + className + "#" + n);
	}
	
	private void compileExpression() throws CompileException { // compile expressions, done
		compileTerm();
		while(jt.tokenContent().equals("+") || 
				jt.tokenContent().equals("-") ||
				jt.tokenContent().equals("*") || 
				jt.tokenContent().equals("/") ||
				jt.tokenContent().equals("&") ||
				jt.tokenContent().equals("|") ||
				jt.tokenContent().equals(">") ||
				jt.tokenContent().equals("<") ||
				jt.tokenContent().equals("=")) {
			String op = check(new String[]{"+", "-", "*", "/", "&", "|", ">", "<", "="});
			compileTerm();
			switch (op) {
			case "+":
				vw.writeArithmetic("add");
				break;
			case "-":
				vw.writeArithmetic("sub");
				break;
			case "*":
				vw.writeArithmetic("multiply");
				break;
			case "/":
				vw.writeArithmetic("divide");
				break;
			case "&":
				vw.writeArithmetic("and");
				break;
			case "|":
				vw.writeArithmetic("or");
				break;
			case ">":
				vw.writeArithmetic("gt");
				break;
			case "<":
				vw.writeArithmetic("lt");
				break;
			case "=":
				vw.writeArithmetic("eq");
				break;
			}
		}
	}
	
	private void compileTerm() throws CompileException { // handles terms
		if (jt.tokenType().equals("INT_CONST")) {  // number, done
			vw.writePush("constant", Integer.parseInt(checkType("INT_CONST")));
		}
		else if (jt.tokenType().equals("STRING_CONST")) {  // string
			String str = checkType("STRING_CONST");
			vw.writePush("constant", str.length());
			vw.writeCall("String.new", 1);
			for (int i = 0; i < str.length(); i++) {
				vw.writePush("constant", (short)str.charAt(i));
				vw.writeCall("String.appendChar", 2);
			}
		}
		else if (jt.tokenType().equals("KEYWORD")) {  // this, null, true, false, this
			String k = checkType("KEYWORD");
			if (k.equals("this"))
				vw.writePush("pointer", 0); 
			else if (k.equals("true")) {
				vw.writePush("constant", 0);
				vw.writeArithmetic("not");
			}
			else vw.writePush("constant", 0); // false and null
		}
		else if (jt.tokenContent().equals("(")) {  // expressions in brackets, done
			check("(");
			compileExpression();
			check(")");
		}
		else if (jt.tokenContent().equals("-") || jt.tokenContent().equals("~")) { // unary operators, done
			String op = check(new String[]{"-", "~"});
			compileTerm();
			if (op.equals("-")) 
				vw.writeArithmetic("neg");
			else vw.writeArithmetic("not");
		}
		else {
			String name = checkType("IDENTIFIER");  	
			if (jt.tokenContent().equals("[")) {  // array
				if (stSub.contains(name)) 
					vw.writePush(stSub.kindOf(name), stSub.indexOf(name));
				else vw.writePush(stClass.kindOf(name), stClass.indexOf(name));
				check("[");
				compileExpression();
				check("]");
				vw.writeArithmetic("add");
				vw.writePop("pointer", 1);
				vw.writePush("that", 0);
			}
			else if (jt.tokenContent().equals("(")) {  // sub call, omitted class name
				String funcName = name;
				String obName = className;
				check("(");
				int nArgs = compileExpressionList();
				check(")");
				vw.writePush("pointer", 0);  // changed from pointer 0
				vw.writeCall(obName + "." + funcName, nArgs + 1);//
			}
			else if (jt.tokenContent().equals(".")) {  // method call
				check(".");
				String funcName = checkType("IDENTIFIER");
				String obName = name;
				int numThis = 0;
				if (stSub.contains(obName) || stClass.contains(obName)) {
					String segment = "";
					int index = 0;
					if (stSub.contains(obName)) {
						segment = stSub.kindOf(obName);
						index = stSub.indexOf(obName);
						obName = stSub.typeOf(obName);
					}	
					else {
						segment = stClass.kindOf(obName);
						index = stClass.indexOf(obName);
						obName = stClass.typeOf(obName);
					} 
					vw.writePush(segment, index);  // changed from pointer 0
					numThis++;
				}
				check("(");
				int nArgs = compileExpressionList() + numThis;
				check(")");
				vw.writeCall(obName + "." + funcName, nArgs);//
			}
			else { // variable
				if (stSub.contains(name)) 
					vw.writePush(stSub.kindOf(name), stSub.indexOf(name));
				else vw.writePush(stClass.kindOf(name), stClass.indexOf(name));
			}
		}
	}
	
	private int compileExpressionList() throws CompileException { // handles expression lists, done
		int n = 0;
		if (!jt.tokenContent().equals(")")) {
			compileExpression();
			n++;
			while (jt.tokenContent().equals(",")) {
				check(",");
				compileExpression();
				n++;
			}
		}
		return n;
	}
	
}
