package Symbol;

public class ClassDeclaration {
	public Table vt;
	public Table mt;
	public String i;
	public ClassDeclaration(String i, Table vt, Table mt) {
		this.vt = vt;
		this.mt = mt;
		this.i = i;
	}
}
