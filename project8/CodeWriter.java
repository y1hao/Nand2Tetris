import java.io.*;

public class CodeWriter {
	private File asmFile;
	private BufferedWriter bw;
	private String fileName;
	private int arithJudgeNum = 0;
	private int callNum = 0;
	private int funcNum = 0;
	private int retNum = 0;
	
	public CodeWriter(String fileName) {
		try {
			if (fileName.indexOf('.') != -1) {
				asmFile = new File(fileName.substring(0, fileName.length() - 3) + ".asm");
			}
			else {
				asmFile = new File(fileName + "/" + fileName + ".asm");
			}
			asmFile.createNewFile();
			bw = new BufferedWriter(new FileWriter(asmFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setFileName(String fileName) {
		int index = fileName.lastIndexOf('/');
		this.fileName = fileName.substring(index + 1, fileName.length() - 3);
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
			bw.write("// Initialization\n");
			bw.write("@256\n");
			bw.write("D=A\n");
			bw.write("@SP\n");
			bw.write("M=D\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		writeCall("sys.init", 0);
	}
	
	public void writeLabel(String label) {
		try {
			bw.write("\n// Label " + label + "\n");
			bw.write("(" + label +")\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeGoto(String label) {
		try {
			bw.write("\n// goto " + label + "\n");
			bw.write("@" + label +"\n");
			bw.write("0;JMP\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeIf(String label) {
		try {
			bw.write("\n// if-goto " + label + "\n");
			bw.write("@SP\n");
			bw.write("M=M-1\n");
			bw.write("A=M\n");
			bw.write("D=M\n");
			bw.write("@" + label +"\n");
			bw.write("D;JNE\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeCall(String functionName, int numArgs) {
		callNum ++;
		try {
			bw.write("\n// call " + functionName + " " + numArgs + "\n");
			bw.write("@" + "returnAddress" + callNum + "\n");
			bw.write("D=A\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
			bw.write("@SP\n");
			bw.write("M=M+1\n");
			
			bw.write("@LCL\n");
			bw.write("D=M\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
			bw.write("@SP\n");
			bw.write("M=M+1\n");
			
			bw.write("@ARG\n");
			bw.write("D=M\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
			bw.write("@SP\n");
			bw.write("M=M+1\n");
			
			bw.write("@THIS\n");
			bw.write("D=M\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
			bw.write("@SP\n");
			bw.write("M=M+1\n");
			
			bw.write("@THAT\n");
			bw.write("D=M\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
			bw.write("@SP\n");
			bw.write("M=M+1\n");
			
			bw.write("@SP\n");
			bw.write("D=M\n");
			bw.write("@ARG\n");
			bw.write("M=D\n");
			bw.write("@" + numArgs+ "\n");
			bw.write("D=A\n");
			bw.write("@ARG\n");
			bw.write("M=M-D\n");
			bw.write("@5\n");
			bw.write("D=A\n");
			bw.write("@ARG\n");
			bw.write("M=M-D\n");
			
			bw.write("@SP\n");
			bw.write("D=M\n");
			bw.write("@LCL\n");
			bw.write("M=D\n");
			
			bw.write("@" + functionName + "\n");
			bw.write("0;JMP\n");
			bw.write("(returnAddress" + callNum + ")\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeFunction(String functionName, int numLocals) {
		funcNum ++;
		try {
			bw.write("\n//" + functionName + " " + numLocals + "\n");
			bw.write("(" + functionName+ ")\n");
			bw.write("@" + numLocals + "\n");
			bw.write("D=A\n");
			bw.write("@FUNCTEMP" + funcNum + "\n");
			bw.write("M=D\n");
			bw.write("D=M\n");
			bw.write("@FUNCEND" + funcNum + "\n");
			bw.write("D;JEQ\n");
			bw.write("(FUNCLOOP" + funcNum + ")\n");
			bw.write("@SP\n");
			bw.write("A=M\n");
			bw.write("M=0\n");
			bw.write("@SP\n");
			bw.write("M=M+1\n");
			bw.write("@FUNCTEMP" + funcNum + "\n");
			bw.write("M=M-1\n");
			bw.write("D=M\n");
			bw.write("@FUNCLOOP" + funcNum + "\n");
			bw.write("D;JGT\n");
			bw.write("(FUNCEND" + funcNum + ")\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeReturn() {
		retNum ++;
		try {
			bw.write("\n// return\n");
			bw.write("@LCL\n");
			bw.write("D=M\n");
			bw.write("@endFrame" + retNum + "\n");
			bw.write("M=D\n");
			
			bw.write("@5\n");
			bw.write("D=A\n");
			bw.write("@endFrame" + retNum + "\n");
			bw.write("D=M-D\n");
			bw.write("A=D\n");
			bw.write("D=M\n");
			bw.write("@returnAddr" + retNum + "\n");
			bw.write("M=D\n");
			
			bw.write("@SP\n");
			bw.write("A=M-1\n");
			bw.write("D=M\n");
			bw.write("@ARG\n");
			bw.write("A=M\n");
			bw.write("M=D\n");
			bw.write("@SP\n");
			bw.write("M=M-1\n");
			
			bw.write("@ARG\n");
			bw.write("D=M+1\n");
			bw.write("@SP\n");
			bw.write("M=D\n");
			
			bw.write("@endFrame" + retNum + "\n");
			bw.write("D=M-1\n");
			bw.write("A=D\n");
			bw.write("D=M\n");
			bw.write("@THAT\n");
			bw.write("M=D\n");
			
			bw.write("@2\n");
			bw.write("D=A\n");
			bw.write("@endFrame" + retNum + "\n");
			bw.write("D=M-D\n");
			bw.write("A=D\n");
			bw.write("D=M\n");
			bw.write("@THIS\n");
			bw.write("M=D\n");
			
			bw.write("@3\n");
			bw.write("D=A\n");
			bw.write("@endFrame" + retNum + "\n");
			bw.write("D=M-D\n");
			bw.write("A=D\n");
			bw.write("D=M\n");
			bw.write("@ARG\n");
			bw.write("M=D\n");
			
			bw.write("@4\n");
			bw.write("D=A\n");
			bw.write("@endFrame" + retNum + "\n");
			bw.write("D=M-D\n");
			bw.write("A=D\n");
			bw.write("D=M\n");
			bw.write("@LCL\n");
			bw.write("M=D\n");
			
			bw.write("@returnAddr" + retNum + "\n");
			bw.write("A=M\n");
			bw.write("0;JMP\n");

			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
