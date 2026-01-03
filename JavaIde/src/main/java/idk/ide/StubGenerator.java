package idk.ide;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;

public class StubGenerator {
    private String name;
    public StubGenerator(String fileName ,CompilationUnitTree unitTree){
        name = fileName;
    }
    public StringJavaObject generateShub(){
        return null;
    }
}
