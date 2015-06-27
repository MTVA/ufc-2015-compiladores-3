package visitor;

import java.beans.Expression;
import java.util.Enumeration;

import javax.management.openmbean.ArrayType;
import javax.swing.text.TabExpander;
import javax.swing.text.html.FormSubmitEvent;

import Symbol.ClassDeclaration;
import Symbol.MethodDeclaration;
import Symbol.Symbol;
import Symbol.Table;
import syntaxtree.*;

public class TypeDepthFirstVisitor implements TypeVisitor {
	
	  public static Table table = new Table();
	  public static ClassDeclaration currentClass = null;
	  public static MethodDeclaration currentMethod = null;
	  
	  // MainClass m;
  // ClassDeclList cl;
  public Type visit(Program n) {
	table.beginScope();
	
    IdentifierType classType = (IdentifierType)n.m.accept(this);
    
    for ( int i = 0; i < n.cl.size(); i++ ) {
    	classType = (IdentifierType)n.cl.elementAt(i).accept(this);
    }
    Enumeration<Symbol> keys = table.keys();
  
    
    while( keys.hasMoreElements() )
    {
    	Symbol s = keys.nextElement();
    	ClassDeclaration cd = (ClassDeclaration) table.get(s.toString());
//    	System.out.println("class " + cd.i + "{");
    	Enumeration<Symbol> classKeys = cd.vt.keys();
    	while( classKeys.hasMoreElements() ){
    		
    		Symbol vk = classKeys.nextElement();
	    	String t = (String) cd.vt.get(vk.toString());
//	    	System.out.println(t + " " + vk.toString());
    	}
    	
    	Enumeration<Symbol> methodKeys = cd.mt.keys();
    	while( methodKeys.hasMoreElements() ){
    		Symbol mk = methodKeys.nextElement();
	    	MethodDeclaration md = (MethodDeclaration) cd.mt.get(mk.toString());
	    	String ident = md.i;
	    	String retur = md.r;
//	    	System.out.println(retur + " " + ident + "(");
	    	Enumeration<Symbol> formalKeys = md.pt.keys();
	    	while( formalKeys.hasMoreElements() ){
	    		Symbol pk = formalKeys.nextElement();
		    	String t = (String) md.pt.get(pk.toString());
//		    	System.out.println(t + " " + pk.toString());
	    	}
//	    	System.out.println("){");
	    	Enumeration<Symbol> varKeys = md.vt.keys();
	    	while( varKeys.hasMoreElements() ){
	    		Symbol vk = varKeys.nextElement();
		    	String t = (String) md.vt.get(vk.toString());
//		    	System.out.println(t + " " + vk.toString());
	    	}
	    	
//	    	System.out.println("}");
    	}
//    	System.out.println("}");
    }
    
    table.endScope();
    return null;
  }
  
  // Identifier i1,i2;
  // Statement s;
  public Type visit(MainClass n) {		
	IdentifierType classType = (IdentifierType)n.i1.accept(this);
	currentClass = new ClassDeclaration(classType.s, new Table(), new Table());
	IdentifierType argumentType = (IdentifierType)n.i2.accept(this);
	currentMethod = new MethodDeclaration("main", "void", new Table(), new Table());
    n.s.accept(this);
    currentClass.mt.put("main", currentMethod);
    table.put(classType.s, currentClass);
    currentClass = null;
    currentMethod = null;
    return classType;
  }
  
  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public Type visit(ClassDeclSimple n) {		
	IdentifierType classType = (IdentifierType)n.i.accept(this);
	currentClass = new ClassDeclaration(classType.s, new Table(), new Table());
	table.put(classType.s, currentClass);

	for ( int i = 0; i < n.vl.size(); i++ ) {
	    n.vl.elementAt(i).accept(this);
	}
	for ( int i = 0; i < n.ml.size(); i++ ) {
	    n.ml.elementAt(i).accept(this);
	}
	currentClass = null;
	return classType;
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public Type visit(ClassDeclExtends n) {
	IdentifierType classType = (IdentifierType)n.i.accept(this);
	currentClass = new ClassDeclaration(classType.s, new Table(), new Table());
	table.put(classType.s, currentClass);
	ClassDeclaration extendedClass = (ClassDeclaration) table.get(n.j.s);
    n.j.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
        n.ml.elementAt(i).accept(this);
    }
    return classType;
  }

  // Type t;
  // Identifier i;
  public Type visit(VarDecl n) {
	Type t = n.t.accept(this);
	String id = n.i.s;
	String type = typeName(t);
	if (currentMethod == null) {
		if ( currentClass.vt.get(id) != null )
			System.out.println(id + "is already defined in " + currentClass.i);
		else
			currentClass.vt.put(id, type);
	}else if ( currentMethod.vt.get(id) != null ){
			System.out.println(id + "is already defined in " + currentClass.i + "." + currentMethod.i);
	}else
		currentMethod.vt.put(id, type);
    return t;
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public Type visit(MethodDecl n) {
	currentMethod = new MethodDeclaration(n.i.s, typeName(n.t), new Table(), new Table());
	
    n.t.accept(this);
    n.i.accept(this);
    for ( int i = 0; i < n.fl.size(); i++ ) {
        n.fl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.elementAt(i).accept(this);
    }
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    n.e.accept(this);
    currentClass.mt.put(n.i.s, currentMethod);
    currentMethod = null;
    return n.t;
  }

  // Type t;
  // Identifier i;
  public Type visit(Formal n) {
    Type formalType = n.t.accept(this);
    n.i.accept(this);
    currentMethod.pt.put(n.i.s, typeName(formalType));
    return formalType;
  }

  public Type visit(IntArrayType n) {
    return n;
  }

  public Type visit(BooleanType n) {
    return n;
  }

  public Type visit(IntegerType n) {
    return n;
  }

  // String s;
  public Type visit(IdentifierType n) {
    return n;
  }

  // StatementList sl;
  public Type visit(Block n) {
    for ( int i = 0; i < n.sl.size(); i++ ) {
        n.sl.elementAt(i).accept(this);
    }
    return null;
  }

  // Exp e;
  // Statement s1,s2;
  public Type visit(If n) {
    Type expressionType = n.e.accept(this);
    n.s1.accept(this);
    n.s2.accept(this);  
    return null;
  }

  // Exp e;
  // Statement s;
  public Type visit(While n) {
	Type expressionType = n.e.accept(this);    
    n.s.accept(this); 
    return null;
  }

  // Exp e;
  public Type visit(Print n) {
	n.e.accept(this);
    return null;
  }
  
  // Identifier i;
  // Exp e;
  public Type visit(Assign n) {
    Type identifierType = n.i.accept(this);
    Type expressionType = n.e.accept(this);
    
    return identifierType;
  }

  // Identifier i;
  // Exp e1,e2;
  public Type visit(ArrayAssign n) {
    n.i.accept(this);
    Type indexExpression = n.e1.accept(this);
    Type objectExpression = n.e2.accept(this);   
    return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(And n) {
    Type leftAndType = n.e1.accept(this);
    Type rightAndType = n.e2.accept(this);
    return new BooleanType();
  }

  // Exp e1,e2;
  public Type visit(LessThan n) {
    Type leftLessThan = n.e1.accept(this);
    Type rightLessThan = n.e2.accept(this);
     
    return new BooleanType();
  }

  // Exp e1,e2;
  public Type visit(Plus n) {
    Type leftPlusType = n.e1.accept(this);
    Type rightPlusType = n.e2.accept(this);
    return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Minus n) {
	  Type leftMinusType = n.e1.accept(this);
	  Type rightMinusType = n.e2.accept(this);
	  return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Times n) {
	  Type leftTimesType = n.e1.accept(this);
	  Type rightTimesType = n.e2.accept(this);	
	  return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(ArrayLookup n) {
    
    Type arrayExpression = n.e1.accept(this);
	Type arrayIndex = n.e2.accept(this);
    return new IntegerType();
  }

  // Exp e;
  public Type visit(ArrayLength n) {
    Type lengthExpression = n.e.accept(this);
    
    if( !(lengthExpression instanceof IntArrayType) )
    		System.out.println("The expression in the left must to be an IntArray type.");

    return new IntegerType();
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public Type visit(Call n) {
    n.e.accept(this);
    n.i.accept(this);
    
    for ( int i = 0; i < n.el.size(); i++ ) {
        n.el.elementAt(i).accept(this);
    }
    return null;
  }

  // int i;
  public Type visit(IntegerLiteral n) {
    return new IntegerType();
  }

  public Type visit(True n) {
    return new BooleanType();
  }

  public Type visit(False n) {
    return new BooleanType();
  }

  // String s;
  public Type visit(IdentifierExp n) {
    return null;
  }

  public Type visit(This n) {
    return null;
  }

  // Exp e;
  public Type visit(NewArray n) {
    Type expressionType = n.e.accept(this);
    return new IntArrayType();
  }

  // Identifier i;
  public Type visit(NewObject n) {
    return null;
  }

  // Exp e;
  public Type visit(Not n) {
    Type expressionType = n.e.accept(this);
    System.out.println("NNOT : ");
    return new BooleanType();
  }

  // String s;
  public Type visit(Identifier n) {
	IdentifierType type = new IdentifierType(n.s);
    return type;
  }
  
  String typeName(Type type){
	  if(type instanceof IntegerType){
		  return "int";
	  }else if(type instanceof BooleanType){
		  return "boolean";
	  }else if(type instanceof IntArrayType){
		  return "int[]";
	  }else if(type instanceof IdentifierType){
		  return ((IdentifierType)type).s;
	  }else{
		  return "void";
		  }
		  
	  }
}
