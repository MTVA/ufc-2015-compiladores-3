package Symbol;

import java.util.Stack;

public class Table {
	private static java.util.Dictionary<Symbol, Stack<Object>> hash = new java.util.Hashtable<Symbol, Stack<Object>>();
	private Stack<Symbol> symbols = new Stack<Symbol>();
	private Symbol marker = Symbol.symbol("end-of-scope");
	public Table(){}
	public java.util.Enumeration<Symbol> keys(){
		
		return hash.keys();
	}
	public void put(String id, Object value){
		Symbol key = Symbol.symbol(id);
		Stack<Object> s = hash.get(key);
		if (s==null) {
			s = new Stack<Object>();
			hash.put(key,s); 
		}
		symbols.push(key);
		s.push(value);
	}
	public void endScope(){
		Symbol s = null;
		do{
			s = symbols.pop();
			Stack<Object> st = hash.get(s);
			if( st != null ){
				if(st.size() > 0)
					st.pop();
				hash.remove(s);
			}
		}while(s != marker);
	}
	public Object get(String id){
		Symbol key = Symbol.symbol(id);
		Stack<Object> s = hash.get(key);
		if( s != null ){
			return s.peek();
		}
		return null;
	}
	public void beginScope(){
		symbols.push(marker);
	}
}
