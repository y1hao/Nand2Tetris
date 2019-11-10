import java.io.*;

public class JackAnalyzer {
	
	public static void main(String[] args) {
		String name = args[0];
		File file = new File(name);
		int N;
		String[] fileNames;
		JackTokenizer[] jt;
		CompilationEngine[] ce;
		if (file.isFile()) {
			N = 1;
			jt = new JackTokenizer[N];
			ce = new CompilationEngine[N];
			jt[0] = new JackTokenizer(name);
			ce[0] = new CompilationEngine(name, jt[0]);
		}
		else {
			fileNames = file.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".jack");
				}
			});
			N = fileNames.length;
			jt = new JackTokenizer[N];
			ce = new CompilationEngine[N];
			for (int i = 0; i < N; i ++) {
				jt[i] = new JackTokenizer(name + "/" + fileNames[i]);
				ce[i] = new CompilationEngine(name + "/" + fileNames[i], jt[i]);
			}	
		}	
		for (int i = 0; i < N; i ++) {
			ce[i].startCompilation();
			ce[i].close();
		}	
	}

}
