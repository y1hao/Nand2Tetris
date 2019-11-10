import java.io.*;

public class Parser {
	private File vmFile;
	private BufferedReader br;
	private String curLine;
	private String[] command;
	
	public Parser(String fileName) {
		try {
			vmFile = new File(fileName);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(vmFile)));
			curLine = "";
			advance();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasMoreCommands() {
		return curLine != null;
	}
	
	public void advance() {
		try {
			 do {
				curLine = br.readLine();
				if (curLine == null) {
					break;
				}
				curLine = curLine.trim().toLowerCase();
				if (curLine.indexOf('/') != -1) {
					curLine = curLine.substring(0,curLine.indexOf('/')).trim();
				}
			} while (curLine.equals(""));
			 
			if (curLine != null) {
				command = curLine.split("\\s+");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String commandType() {
		String c = command[0];
		if (c.equals("push")) {
			return "C_PUSH";
		}
		if (c.equals("pop")) {
			return "C_POP";
		}
		if (c.equals("add") || c.equals("sub") || 
			c.equals("neg") || c.equals("eq") ||
			c.equals("gt") || c.equals("lt") ||
			c.equals("and") || c.equals("or") ||
			c.equals("not")) {
			return "C_ARITHMETIC";
		}
		if (c.equals("label")) {
			return "C_LABEL";
		}
		if (c.equals("goto")) {
			return "C_GOTO";
		}
		if (c.equals("if-goto")) {
			return "C_IF";
		}
		if (c.equals("call")) {
			return "C_CALL";
		}
		if (c.equals("function")) {
			return "C_FUNCTION";
		}
		return "C_RETURN";
	}
	
	public String arg1() {
		if (commandType().equals("C_ARITHMETIC")) {
			return command[0];
		}
		return command[1];
	}
	
	public int arg2() {
		return Integer.parseInt(command[2]);
	}
}
