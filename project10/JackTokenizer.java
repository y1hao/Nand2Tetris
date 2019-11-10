import java.io.*;
import java.util.*;

public class JackTokenizer {
	private BufferedReader br;
	private String nextLine;
	private String[] tokens;
	private String curToken;
	private int curIndex;
	private boolean inComment;
	private HashSet<String> KEYWORDS;
	private HashSet<String> SYMBOLS;
	
	public JackTokenizer(String fileName) {
		KEYWORDS = new HashSet<>();
		KEYWORDS.add("class");
		KEYWORDS.add("constructor");
		KEYWORDS.add("function");
		KEYWORDS.add("method");
		KEYWORDS.add("field");
		KEYWORDS.add("static");
		KEYWORDS.add("var");
		KEYWORDS.add("int");
		KEYWORDS.add("char");
		KEYWORDS.add("boolean");
		KEYWORDS.add("void");
		KEYWORDS.add("true");
		KEYWORDS.add("false");
		KEYWORDS.add("null");
		KEYWORDS.add("this");
		KEYWORDS.add("let");
		KEYWORDS.add("do");
		KEYWORDS.add("if");
		KEYWORDS.add("else");
		KEYWORDS.add("while");
		KEYWORDS.add("return");
		SYMBOLS = new HashSet<>();
		SYMBOLS.add("(");
		SYMBOLS.add(")");
		SYMBOLS.add("{");
		SYMBOLS.add("}");
		SYMBOLS.add("[");
		SYMBOLS.add("]");
		SYMBOLS.add(".");
		SYMBOLS.add(",");
		SYMBOLS.add(";");
		SYMBOLS.add("+");
		SYMBOLS.add("-");
		SYMBOLS.add("*");
		SYMBOLS.add("/");
		SYMBOLS.add("&");
		SYMBOLS.add("|");
		SYMBOLS.add("<");
		SYMBOLS.add(">");
		SYMBOLS.add("=");
		SYMBOLS.add("~");
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		inComment = false;
		curIndex = -1;
		nextLine = getLine();
	}
	
	public boolean hasMoreTokens() {
		if (nextLine == null && tokens != null && curIndex == tokens.length - 1) return false;
		return true;
	}
	
	private String getLine() {
		String line = "";
		while (line != null && (line.equals("") || inComment)) {
			try {
				line = br.readLine();
				if (line != null) {
					if (line.contains("//")) {
						line = line.substring(0, line.indexOf("//"));
					}
					if (line.contains("*/")) {
						inComment = false;
						line = line.substring(line.indexOf("*/") + 2);
					}
					if (line.contains("/*") && !line.contains("*/")) {
						inComment = true;
						line = line.substring(0, line.indexOf("/*"));
					}
					line = line.trim();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		return line;
	}
	
	private String[] getTokens(String line) {
		String[] tokens;
		if (line.length() == 1) {
			tokens = new String[1];
			tokens[0] = line;
			return tokens;
		}
		HashMap<Integer, String> sl = new HashMap<>();
		int i = 0;
		while (line.contains("\"")) {
			int left = line.indexOf("\"");
			int right = line.indexOf("\"", left + 1);
			sl.put(i++, line.substring(left, right + 1));
			line = line.substring(0, left) + " $$$ " + line.substring(right+1);
		}
		i = 0;
		while (i < line.length()) {
			String c = "" +line.charAt(i);
			if (SYMBOLS.contains(c)) {
				line = line.substring(0,i) + " " + c + " " + line.substring(i + 1);
				i = i + 3;
			}
			else i ++;
		}
		tokens = line.split("\\s+");
		int n = 0;
		for (i = 0; i < tokens.length; i ++) {
			if (tokens[i].equals("$$$")) tokens[i] = sl.get(n++);
		}
		return tokens;
	}
	
	public void advance() {
		if (tokens == null || curIndex == tokens.length - 1 || curIndex == -1) {
			tokens = getTokens(nextLine);
			curIndex = 0;
			curToken = tokens[curIndex];
			nextLine = getLine();
		}
		else curToken = tokens[++curIndex];
	}
	
	public String tokenType() {
		if (curToken.charAt(0) == '"') return "STRING_CONST";
		if (KEYWORDS.contains(curToken)) return "KEYWORD";
		if (SYMBOLS.contains(curToken)) return "SYMBOL";
		if (Character.isDigit(curToken.charAt(0))) 	return "INT_CONST";
		return "IDENTIFIER";
	}
	
	public String tokenContent() {
		if (curToken.charAt(0) == '"') return curToken.substring(1, curToken.length()-1);
		if (curToken.equals("<")) return "&lt;";
		if (curToken.equals(">")) return "&gt;";
		if (curToken.equals("&")) return "&amp;";
		if (curToken.equals("\"")) return "&quot;";
		return curToken;
	}
	
}
