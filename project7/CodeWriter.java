import java.io.*;

public class CodeWriter {
	private File asmFile;
	private BufferedWriter bw;
	private String fileName;
	private int arithJudgeNum = 0;
	
	public CodeWriter(String fileName) {
		int index = fileName.lastIndexOf('/');
		this.fileName = fileName.substring(index + 1, fileName.length() - 3);
		try {
			asmFile = new File(fileName.substring(0, fileName.length() - 3) + ".asm");
			asmFile.createNewFile();
			bw = new BufferedWriter(new FileWriter(asmFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeArithmetic(String command) {
		try {
			bw.write("\n// " + command + "\n");
			bw.write("@SP\n");
			bw.write("A=M-1\n");
			
			if (command.equals("neg")) {
				bw.write("M=-M\n");
			}
			else if (command.equals("not")) {
				bw.write("M=!M\n");
			}
			else {
				bw.write("D=M\n");
				bw.write("@SP\n");
				bw.write("M=M-1\n");
				bw.write("A=M-1\n");
				
				if (command.equals("add")) {
					bw.write("M=D+M\n");
				}
				else if (command.equals("and")) {
					bw.write("M=D&M\n");
				}
				else if (command.equals("or")) {
					bw.write("M=D|M\n");
				}
				else {
					bw.write("M=M-D\n");
					bw.write("D=M\n");
					if (! command.equals("sub")) {
						arithJudgeNum  ++;
						bw.write("@TRUE" + arithJudgeNum + "\n");
						if (command.equals("eq")) {
							bw.write("D;JEQ\n");
						}
						if (command.equals("gt")) {
							bw.write("D;JGT\n");
						}
						if (command.equals("lt")) {
							bw.write("D;JLT\n");
						}
						bw.write("@SP\n");
						bw.write("A=M-1\n");
						bw.write("M=0\n");
						bw.write("@ENDAJ" + arithJudgeNum + "\n");
						bw.write("0;JMP\n");
						bw.write("(TRUE" + arithJudgeNum + ")\n");
						bw.write("@1\n");
						bw.write("D=-A\n");
						bw.write("@SP\n");
						bw.write("A=M-1\n");
						bw.write("M=D\n");
						bw.write("(ENDAJ" + arithJudgeNum + ")\n");
					}
				}
			}
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writePushPop(String command, String segment, int index) {
		try {
			bw.write("\n// " + command + " " + segment + " " + index + "\n");
			if (command.equals("C_PUSH")) {
				if (segment.equals("static")) {
					bw.write("@" + fileName + "." + index + "\n");
					bw.write("D=M\n");
				}
				if (segment.equals("constant")) {
					bw.write("@" + index + "\n");
					bw.write("D=A\n");
				}
				if (segment.equals("argument")) {
					bw.write("@ARG\n");
					bw.write("D=M\n");
					bw.write("@" + index + "\n");
					bw.write("A=D+A\n");
					bw.write("D=M\n");
				}
				if (segment.equals("local")) {
					bw.write("@LCL\n");
					bw.write("D=M\n");
					bw.write("@" + index + "\n");
					bw.write("A=D+A\n");
					bw.write("D=M\n");
				}
				if (segment.equals("this")) {
					bw.write("@THIS\n");
					bw.write("D=M\n");
					bw.write("@" + index + "\n");
					bw.write("A=D+A\n");
					bw.write("D=M\n");
				}
				if (segment.equals("that")) {
					bw.write("@THAT\n");
					bw.write("D=M\n");
					bw.write("@" + index + "\n");
					bw.write("A=D+A\n");
					bw.write("D=M\n");
				}
				if (segment.equals("pointer")) {
					bw.write("@3\n");
					bw.write("D=A\n");
					bw.write("@" + index + "\n");
					bw.write("A=D+A\n");
					bw.write("D=M\n");
				}
				if (segment.equals("temp")) {
					bw.write("@5\n");
					bw.write("D=A\n");
					bw.write("@" + index + "\n");
					bw.write("A=D+A\n");
					bw.write("D=M\n");
				}
				bw.write("@SP\n");
				bw.write("A=M\n");
				bw.write("M=D\n");
				bw.write("@SP\n");
				bw.write("M=M+1\n");
			}
			else {
				if (segment.equals("static")) {
					bw.write("@SP\n");
					bw.write("A=M-1\n");
					bw.write("D=M\n");
					bw.write("@" + fileName + "." + index + "\n");
					bw.write("M=D\n");
				}
				else {
					if (segment.equals("argument")) {
						bw.write("@ARG\n");
						bw.write("D=M\n");
					}
					if (segment.equals("local")) {
						bw.write("@LCL\n");
						bw.write("D=M\n");
					}
					if (segment.equals("this")) {
						bw.write("@THIS\n");
						bw.write("D=M\n");
					}
					if (segment.equals("that")) {
						bw.write("@THAT\n");
						bw.write("D=M\n");
					}
					if (segment.equals("pointer")) {
						bw.write("@3\n");
						bw.write("D=A\n");
					}
					if (segment.equals("temp")) {
						bw.write("@5\n");
						bw.write("D=A\n");
					}
					bw.write("@" + index + "\n");
					bw.write("D=D+A\n");
					bw.write("@POPTEMP\n");
					bw.write("M=D\n");
					bw.write("@SP\n");
					bw.write("A=M-1\n");
					bw.write("D=M\n");
					bw.write("@POPTEMP\n");
					bw.write("A=M\n");
					bw.write("M=D\n");
				}	
				bw.write("@SP\n");
				bw.write("M=M-1\n");
			}
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeInit() {
		try {
			bw.write("// Initialization...\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeLabel(String label) {
		
	}
	
	public void writeGoto(String label) {
		
	}
	
	public void writeIf(String label) {
		
	}
	
	public void writeCall(String functionName, int numArgs) {
		
	}
	
	public void writeReturn() {
		
	}
	
	public void writeFunction(String functionName, int numLocals) {
		
	}
	
	public void close() {
		try {
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
