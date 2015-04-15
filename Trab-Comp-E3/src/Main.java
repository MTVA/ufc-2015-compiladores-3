import syntaxtree.*;
import visitor.*;
import syntaxChecker.*;

public class Main {
   public static void main(String [] args) {
      try {
         Program root = new MiniJavaParser(System.in).Goal();
          root.accept(new PrettyPrintVisitor());
      }
      catch (Exception e) {
         System.out.println(e.toString());
      }
   }
}
