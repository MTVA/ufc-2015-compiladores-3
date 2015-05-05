package Symbol;

public class MethodDeclaration {
	public Table vt;
	public Table pt;
	public String i;
	public String r;
	
	public MethodDeclaration(String i, String r, Table vt, Table pt) {
		this.vt = vt;
		this.pt = pt;
		this.i = i;
		this.r = r;
	}
}
