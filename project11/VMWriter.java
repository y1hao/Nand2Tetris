import java.io.*;

public class VMWriter {
	private BufferedWriter bw;
	
	public VMWriter(String fileName) {
		try {
			File outputFile = new File(fileName.substring(0, fileName.length() - 5) + ".vm");
			bw = new BufferedWriter(new FileWriter(outputFile));
			outputFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeComment(String comment) {
		try {
			//bw.write("\n//  " + comment + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writePush(String segment, int index) { // segment: argument, local, static, constant, this, that, temp, pointer
		try {
			if (segment.equals("field"))
				segment = "this";
			else if (segment.equals("var"))
				segment = "local";
			else if (segment.equals("arg"))
				segment = "argument";
			bw.write("push " + segment + " " + index + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writePop(String segment, int index) {
		try {
			if (segment.equals("field"))
				segment = "this";
			else if (segment.equals("var"))
				segment = "local";
			else if (segment.equals("arg"))
				segment = "argument";
			bw.write("pop " + segment + " " + index + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeArithmetic(String command) { // command: multiply, devide, add, sub, and, or, neg, not, eq, lt, gt
		try {
			if(command.equals("multiply")) 
				writeCall("Math.multiply", 2);
			else if(command.equals("divide")) 
				writeCall("Math.divide", 2);
			else {
				bw.write(command + "\n");
				bw.flush();
			}
		}
	    catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeLabel(String label) {
		try {
			bw.write("label " + label + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeGoto(String label) {
		try {
			bw.write("goto " + label + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeIf(String label) {
		try {
			bw.write("if-goto " + label + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeCall(String name, int nArgs) {
		try {
			bw.write("call " + name + " " + nArgs + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeFunction(String name, int nLocals) {
		try {
			bw.write("function " + name + " "+ nLocals + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeReturn() {
		try {
			bw.write("return\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeErrorInformation(CompileException e) {
		try {
			bw.write("Compile Error: " + e.getMessage() + "\n");
			bw.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void close() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
