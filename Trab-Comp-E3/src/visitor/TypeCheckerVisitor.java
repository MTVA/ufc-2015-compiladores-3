	package visitor;
	
	import java.util.Enumeration;
	
	import Symbol.ClassDeclaration;
	import Symbol.MethodDeclaration;
	import Symbol.Symbol;
	import Symbol.Table;
	import syntaxtree.*;
	
	public class TypeCheckerVisitor implements TypeVisitor {
	
		Table table;
		String currentClass = null;
		String currentMethod = null;
		public TypeCheckerVisitor(Table table) {
			this.table = table;
		}
	  // MainClass m;
	  // ClassDeclList cl;
	  public Type visit(Program n) {
	    n.m.accept(this);
	    for ( int i = 0; i < n.cl.size(); i++ ) {
	        n.cl.elementAt(i).accept(this);
	    }
	    return null;
	  }
	  
	  // Identifier i1,i2;
	  // Statement s;
	  public Type visit(MainClass n) {
		currentClass = n.i1.s;
	    n.i1.accept(this);
	    currentMethod = "main";
	    n.i2.accept(this);
	    n.s.accept(this);
	    currentClass = null;
	    currentMethod = null;
	    return null;
	  }
	  
	  // Identifier i;
	  // VarDeclList vl;
	  // MethodDeclList ml;
	  public Type visit(ClassDeclSimple n) {
	    n.i.accept(this);
	    currentClass = n.i.s;
	    for ( int i = 0; i < n.vl.size(); i++ ) {
	        n.vl.elementAt(i).accept(this);
	    }
	    for ( int i = 0; i < n.ml.size(); i++ ) {
	        n.ml.elementAt(i).accept(this);
	    }
	    currentClass = null;
	    return null;
	  }
	 
	  // Identifier i;
	  // Identifier j;
	  // VarDeclList vl;
	  // MethodDeclList ml;
	  public Type visit(ClassDeclExtends n) {
	    n.i.accept(this);
	    currentClass = n.i.s;
	    n.j.accept(this);
	    for ( int i = 0; i < n.vl.size(); i++ ) {
	        n.vl.elementAt(i).accept(this);
	    }
	    for ( int i = 0; i < n.ml.size(); i++ ) {
	        n.ml.elementAt(i).accept(this);
	    }
	    currentClass = null;
	    return null;
	  }
	
	  // Type t;
	  // Identifier i;
	  public Type visit(VarDecl n) {
	    n.t.accept(this);
	    String type = typeName(n.t);
	    if( n.t instanceof IdentifierType )
	    if( TypeDepthFirstVisitor.table.get(type) == null )
	    	System.out.println("This type of declaration don't reffer to a specified type");
	    n.i.accept(this);
	    return n.t;
	  }
	
	  // Type t;
	  // Identifier i;
	  // FormalList fl;
	  // VarDeclList vl;
	  // StatementList sl;
	  // Exp e;
	  public Type visit(MethodDecl n) {
		currentMethod = n.i.s;
	    n.t.accept(this);
	    n.i.accept(this);
	    
	    if( n.t instanceof IdentifierType ){
		    String t = typeName(n.t);
		    if( TypeDepthFirstVisitor.table.get(t) == null )	
		    	System.out.println("This type declaration don't reffer to a specified type");
	}
	
	for ( int i = 0; i < n.fl.size(); i++ ) {
	    n.fl.elementAt(i).accept(this);
	}
	for ( int i = 0; i < n.vl.size(); i++ ) {
	    n.vl.elementAt(i).accept(this);
	}
	for ( int i = 0; i < n.sl.size(); i++ ) {
	    n.sl.elementAt(i).accept(this);
	}
	Type returnType = n.e.accept(this);
	String r = typeName(returnType);
	String t = typeName(n.t);
	if( r != t )
		System.out.println("Method type declaration does not match with the return type.");
	    
	    currentMethod = null;
	    return null;
	  }
	
	  // Type t;
	  // Identifier i;
	  public Type visit(Formal n) {
		  
		String type = typeName(n.t);
	    if( n.t instanceof IdentifierType )
	    if( TypeDepthFirstVisitor.table.get(type) == null )
	    	System.out.println("This type declaration don't reffer to a specified type");
	    n.t.accept(this);
	    n.i.accept(this);
	    return null;
	  }
	
	  public Type visit(IntArrayType n) {
	    return new IntArrayType();
	  }
	
	  public Type visit(BooleanType n) {
	    return new BooleanType();
	  }
	
	  public Type visit(IntegerType n) {
	    return new IntegerType();
	  }
	
	  // String s;
	  public Type visit(IdentifierType n) {
	    return new IdentifierType(n.s);
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
	    if( !(expressionType instanceof BooleanType) )
	    	System.out.println("The if statement needs a boolean type expression.");
	    
	    n.s1.accept(this);
	    n.s2.accept(this);
	    return null;
	  }
	
	  // Exp e;
	  // Statement s;
	  public Type visit(While n) {
	  Type expressionType = n.e.accept(this);
	    if( !(expressionType instanceof BooleanType) )
	    	System.out.println("The while statement needs a boolean type expression.");
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
		
	    n.i.accept(this);
	    Type typeExp = n.e.accept(this);
	    String te = typeName(typeExp);
	    
	    ClassDeclaration cd = (ClassDeclaration) TypeDepthFirstVisitor.table.get(currentClass);
	    MethodDeclaration md = (MethodDeclaration) cd.mt.get(currentMethod);
	    String type =  (String)md.pt.get(n.i.s);
	    if( type == null){
	    	type = (String) md.vt.get(n.i.s);
	        if( type == null )
	        	type = (String)cd.vt.get(n.i.s);
	    }
	    
	    if(type == null)
	    	System.out.println("The identifier was not declared in this scope.");
	   
	    else{
	    	if( type != te )
	    		System.out.println("The type expression doesn't match with identifier type.");
	    }
	    
	    return typeExp;
	  }
	
	  // Identifier i;
	  // Exp e1,e2;
	  public Type visit(ArrayAssign n) {
	    n.i.accept(this);
	    n.e1.accept(this);
	    Type typeExp = n.e2.accept(this);
	    
	    String te = typeName(typeExp);
	    
	    ClassDeclaration cd = (ClassDeclaration) TypeDepthFirstVisitor.table.get(currentClass);
	    MethodDeclaration md = (MethodDeclaration) cd.mt.get(currentMethod);
	    String type =  (String)md.pt.get(n.i.s);
	    if( type == null){
	    	type = (String) md.vt.get(n.i.s);
	        if( type == null )
	        	type = (String)cd.vt.get(n.i.s);
	    }
	    
	    if(type == null)
	    	System.out.println("The identifier was not declared in this scope. ");
	   
	    else{
	    	if( type != "int[]" )
	System.out.println("The identifier is not an array type.");
	else if( te != "int" )
	System.out.println("The expression don't refer to a integer type.");
	    }
	    
	    return null;
	  }
	
	  // Exp e1,e2;
	  public Type visit(And n) {
	    Type leftAndType = n.e1.accept(this);
	    Type rightAndType = n.e2.accept(this);
	    
	    if( !(leftAndType instanceof BooleanType) )
	    	System.out.println("The left 'And' statement needs a boolean type expression.");
	if( !(rightAndType instanceof BooleanType) )
		System.out.println("The right 'And' statement needs a boolean type expression.");
	    
	    return new BooleanType();
	  }
	
	  // Exp e1,e2;
	  public Type visit(LessThan n) {
		Type leftLessThan = n.e1.accept(this);
	    Type rightLessThan = n.e2.accept(this);
	    
		if( !( leftLessThan instanceof IntegerType ))
			System.out.println("The left 'LessThan' expression type must to be an Integer type expression");
	
	if( !( rightLessThan instanceof IntegerType ) )
		System.out.println("But the right 'LessThan' expression type must to be an Integer type expression");
	    
	    return new BooleanType();
	  }
	
	  // Exp e1,e2;
	  public Type visit(Plus n) {
		  	Type leftPlusType = n.e1.accept(this);
		    Type rightPlusType = n.e2.accept(this);
		    
		    if( !( leftPlusType instanceof IntegerType ))
				System.out.println("The left 'Plus' expression type must to be an Integer type expression");
	
	if( !( rightPlusType instanceof IntegerType ))
		System.out.println("The right 'Plus' expression type must to be an Integer type expression");
		    
		    return new IntegerType();
	  }
	
	  // Exp e1,e2;
	  public Type visit(Minus n) {
		  Type leftMinusType = n.e1.accept(this);
		  Type rightMinusType = n.e2.accept(this);
		    
		  if( !( leftMinusType instanceof IntegerType ))
			  System.out.println("The left 'Minus' expression type must to be an Integer type expression");
		
	  if( !( rightMinusType instanceof IntegerType ))
		  System.out.println("The right 'Minus' expression type must to be an Integer type expression");
			
		  return new IntegerType();
	
	  }
	
	  // Exp e1,e2;
	  public Type visit(Times n) {
		  Type leftTimesType = n.e1.accept(this);
		  Type rightTimesType = n.e2.accept(this);
		    
		  if(!( leftTimesType instanceof IntegerType ))
			  System.out.println("The left 'Times' expression type must to be an Integer type expression");
		
	  if(!( rightTimesType instanceof IntegerType ))
		  System.out.println("The right 'Times' expression type must to be an Integer type expression");
			
		  return new IntegerType();
	  }
	
	  // Exp e1,e2;
	  public Type visit(ArrayLookup n) {
		    Type arrayExpression = n.e1.accept(this);
			Type arrayIndex = n.e2.accept(this);
			
			if(!( arrayExpression instanceof IntArrayType ))
				System.out.println("The expression must to be an array type");
	if(!( arrayIndex instanceof IntegerType ))
		System.out.println("The index must to be an integer expression");    
		    
		    return new IntegerType();		
	  }
	
	  // Exp e;
	  public Type visit(ArrayLength n) {
	    Type lengthExpression = n.e.accept(this);
		    
		    if( !(lengthExpression instanceof IntArrayType) )
		    		System.out.println("The left expression must to be an IntArray type.");
		    
		    return new IntegerType();
		
	  }
	
	  // Exp e;
	  // Identifier i;
	  // ExpList el;
	  public Type visit(Call n) {
	    Type typeExp = n.e.accept(this);
	    n.i.accept(this);
	    
	    if( !(typeExp instanceof IdentifierType) ){
	    	System.out.println("The left expression must to be a class type.");
		return null;
	}
	
	String type = typeName(typeExp); 
	ClassDeclaration cd = (ClassDeclaration) TypeDepthFirstVisitor.table.get(type);
	MethodDeclaration md = (MethodDeclaration) cd.mt.get(n.i.s);
	if( md == null ){
		System.out.println("I don't have phrases for that anymore. Identifier dont refer a "+cd.i+" method");
		return null;
	}
	
	Enumeration<Symbol> keys = md.pt.keys();
	for ( int i = 0; i < n.el.size(); i++ ) {
	    Type t = n.el.elementAt(i).accept(this);
	    Symbol k = keys.nextElement();
	    String typename = (String)md.pt.get(k.toString());
	    if(typename != typeName(t))
	    	System.out.println("Parameters dont match!");
	    }
	    return null;
	  }
	
	  // int i;
	  public Type visit(IntegerLiteral n) {
	    return null;
	  }
	
	  public Type visit(True n) {
	    return null;
	  }
	
	  public Type visit(False n) {
	    return null;
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
	    n.e.accept(this);
	    return null;
	  }
	
	  // Identifier i;
	  public Type visit(NewObject n) {
	    return null;
	  }
	
	  // Exp e;
	  public Type visit(Not n) {
		   Type expressionType = n.e.accept(this);
		    if( expressionType instanceof BooleanType )
		    	System.out.println("Your expression don't refer to a Boolean type.");
		    return new BooleanType();
	  }
	
	  // String s;
	  public Type visit(Identifier n) {
	    return null;
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