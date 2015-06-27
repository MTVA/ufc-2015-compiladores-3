import java.io.ByteArrayInputStream;
import java.io.InputStream;

import syntaxtree.*;
import visitor.*;
import syntaxChecker.*;

public class Main {
   public static void main(String [] args) {
	   String myString = "class Factorial {public static void main(String[] a) {System.out.println(new Fac().ComputeFac(10));}}class Fac { public int ComputeFac(int num) {int num_aux; if (num < 1) num_aux = 1; else num_aux = num * (this.ComputeFac(num-1)); return num_aux;}}";
	   InputStream is = new ByteArrayInputStream( myString.getBytes() );
      try {
          Program root = new MiniJavaParser(is).Goal();
          root.accept(new PrettyPrintVisitor());
          TypeDepthFirstVisitor visitor = new TypeDepthFirstVisitor();
          root.accept(visitor);
      }
      catch (Exception e) {
         System.out.println(e.toString());
      }
   }
}
