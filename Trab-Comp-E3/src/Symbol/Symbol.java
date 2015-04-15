package Symbol;

public class Symbol {
	private static java.util.Dictionary<String, Symbol> dic = new java.util.Hashtable<String, Symbol>();
	private String name;
	private Symbol(String n) {
		name=n;
	}
	
	public static Symbol symbol(String n) 
	{
		String u = n.intern();
		Symbol s = (Symbol)dic.get(u);
		if (s==null) {
			s = new Symbol(u); 
			dic.put(u,s); 
		}
		return s;
	}
	public String toString() {
		return name;
	}
}
