import java.io.*;

public class JackCompiler {
	
	public static void main(String[] args) {
		File file = new File(args[0]);
		if (file.isFile()) 
			(new CompilationEngine(args[0])).compile();
		else {
			String[] fileNames = file.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".jack");
				}
			});
			for (int i = 0; i < fileNames.length; i++) {
				(new CompilationEngine(args[0] + "/" + fileNames[i])).compile();
			}	
		}	
	}
}
