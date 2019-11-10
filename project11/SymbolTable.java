import java.util.*;

public class SymbolTable {
	int nStatics = 0;
	int nFields = 0;
	int nArgs = 0;
	int nLocals = 0;
	HashMap<String, Symbol> symbols;
	private class Symbol {
		private String type;
		private String kind;
		private int number;
		public Symbol(String type, String kind, int number) {
			this.type = type;
			this.kind = kind;
			this.number = number;
		}
		public String getType() {return type;}
		public String getKind() {return kind;}
		public int getNumber() {return number;}
	}
	
	public SymbolTable() {
		symbols = new HashMap<>();
	}
	
	public void define(String name, String type, String kind) {
		switch (kind) {
		case "static":
			symbols.put(name, new Symbol(type, kind, nStatics++));
			break;
		case "field":
			symbols.put(name, new Symbol( type, kind, nFields++));
			break;
		case "arg":
			symbols.put(name, new Symbol(type, kind, nArgs++));
			break;
		case "var":
			symbols.put(name, new Symbol(type, kind, nLocals++));
			break;
		}
	}
	
	public int varCount(String kind) {
		switch (kind) {
		case "static":
			return nStatics;
		case "field":
			return nFields;
		case "arg":
			return nArgs;
		case "var":
			return nLocals;
		}
		return 0;
	}
	
	public boolean contains(String name) {
		return symbols.containsKey(name);
	}
	
	public String kindOf(String name) {
		return symbols.get(name).getKind();
	}
	
	public String typeOf(String name) {
		return symbols.get(name).getType();
	}
	
	public int indexOf(String name) {
		return symbols.get(name).getNumber();
	}
}
