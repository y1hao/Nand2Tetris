import java.io.*;

public class VMTranslator {
	
	static class VMFilter implements FilenameFilter {
		private String type;
		public VMFilter(String type) {
			this.type = type;
		}
		public boolean accept(File dir, String name) {
			return name.endsWith(type);
		}
	}
	
	public static void main(String[] args) {
		String name = args[0];
		File f = new File(name);
		int N;
		String[] pathNames;
		
		CodeWriter cw = new CodeWriter(name);
		
		if (f.isFile()) {
			N = 1;
			pathNames = new String[N];
			pathNames[0] = name;
		}
		else {
			VMFilter filter = new VMFilter(".vm");
			String[] list = f.list(filter);
			N = list.length;
			pathNames = new String[N];
			pathNames[0] = name;
			for (int i = 0; i < N; i ++) {
				pathNames[i] = name + "/" + list[i];
			}
			cw.writeInit();
		}
		
		for (int i = 0; i < N; i ++) {
			Parser ps = new Parser(pathNames[i]);
			cw.setFileName(pathNames[i]);
			String type;
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
		}
		cw.close();
	}

}
