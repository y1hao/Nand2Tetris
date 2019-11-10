
public class VMTranslator {

	public static void main(String[] args) {
		int N = args.length;
		String[] pathNames = new String[N];
		String[] fileNames = new String[N];
		
		for (int i = 0; i < args.length; i ++) {
			pathNames[i] = "" + args[i];
			int index = args[i].lastIndexOf('/') > args[i].lastIndexOf('\\')?  args[i].lastIndexOf('/') : args[i].lastIndexOf('\\');
			fileNames[i] = args[i].substring(index + 1, args[i].length() - 3);
		}
		
		for (int i = 0; i < args.length; i ++) {
			Parser ps = new Parser(pathNames[i]);
			CodeWriter cw = new CodeWriter(pathNames[i]);
			String type;
			cw.writeInit();
			while (ps.hasMoreCommands()) {
				type = ps.commandType();
				if (type.equals("C_ARITHMETIC")) {
					cw.writeArithmetic(ps.arg1());
				}
				if (type.equals("C_PUSH") || type.equals("C_POP")) {
					cw.writePushPop(type, ps.arg1(), ps.arg2());
				}
				if (type.equals("C_LABEL")) {
					cw.writeLabel(ps.arg1());
				}
				if (type.equals("C_GOTO")) {
					cw.writeGoto(ps.arg1());
				}
				if (type.equals("C_IF")) {
					cw.writeIf(ps.arg1());
				}
				if (type.equals("C_FUNCTION")) {
					cw.writeFunction(ps.arg1(), ps.arg2());
				}
				if (type.equals("C_CALL")) {
					cw.writeCall(ps.arg1(), ps.arg2());
				}
				if (type.equals("C_RETURN")) {
					cw.writeReturn();
				}
				ps.advance();
			}
			cw.close();
		}
	}

}
