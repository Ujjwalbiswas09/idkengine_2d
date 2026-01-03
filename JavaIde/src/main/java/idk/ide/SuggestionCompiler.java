package idk.ide;

import android.util.Log;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Scope;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;

import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

public class SuggestionCompiler implements DiagnosticListener<JavaFileObject> {

    private JavacTool normal;
    private Trees trees1;
    private JavacTask task1;
    private CompilationUnitTree tree;
    public String[] libraray;
    private JavacTaskImpl fullTask;
    private Trees mainTree;
    private static Elements elements;
    private Types types;
    private CompilationUnitTree mainRoot;
    public List<MemberData> suggestion = new ArrayList<>();
    public JarPackage jarPackage;
    private HashMap<String,StringJavaObject> clases = new HashMap<>();
    private HashMap<String,String> pack = new HashMap<>();
    private static final String[] TOP_LEVEL_KEYWORDS = {
            "package",
            "import",
            "public",
            "private",
            "protected",
            "abstract",
            "class",
            "interface",
            "@interface",
            "extends",
            "implements",
    };

    private static final String[] CLASS_BODY_KEYWORDS = {
            "public",
            "private",
            "protected",
            "static",
            "final",
            "native",
            "synchronized",
            "abstract",
            "default",
            "class",
            "interface",
            "void",
            "boolean",
            "int",
            "long",
            "float",
            "double",
    };

    private static final String[] METHOD_BODY_KEYWORDS = {
            "new",
            "assert",
            "try",
            "catch",
            "finally",
            "throw",
            "return",
            "break",
            "case",
            "continue",
            "default",
            "do",
            "while",
            "for",
            "switch",
            "if",
            "else",
            "instanceof",
            "final",
            "class",
            "void",
            "boolean",
            "int",
            "long",
            "float",
            "double",
    };
    public ArrayList<String> error;
    public ArrayList<Integer> number;
    public String partial;
    public String sourcePath;

    public SuggestionCompiler(){
        normal = JavacTool.create();
    }

    public HashMap<String, StringJavaObject> getClasses() {
        return clases;
    }


    public void compile(String sourceCode, String name, int position) {
        try {
            error = new ArrayList<>();
            number = new ArrayList<>();
            suggestion = new ArrayList<>();
            if(!clases.containsKey("/"+name)){
                return;
            }
            StringJavaObject current = new StringJavaObject(name,sourceCode);
            clases.remove("/"+name);
            clases.put("/"+name,current);
            task1 = (JavacTaskImpl)
                        normal.getTask(null, normal.getStandardFileManager(null, Locale.ENGLISH, Charset.defaultCharset()),
                                this, null, null,
                                List.of(current));

            tree = task1.parse().iterator().next();
            trees1 = Trees.instance(task1);
            StringBuilder contents = new StringBuilder();//PruneMethodBodies(task1).scan(tree, (long)position);;
            contents.append(sourceCode);
            int endOfLine = endOfLine(contents, (int) position);
            contents.insert(endOfLine, ';');
            compileAndComplete(name,contents.toString(),position);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int endOfLine(CharSequence contents, int cursor) {
        while (cursor < contents.length()) {
            char c = contents.charAt(cursor);
            if (c == '\r' || c == '\n') break;
            cursor++;
        }
        return cursor;
    }

    private void compileAndComplete(String name,String contents, long cursor) {
        partial = partialIdentifier(contents, (int) cursor);
        boolean endsWithParen = endsWithParen(contents, (int) cursor);
        fullCompile(contents,name);
        TreePath path = new FindCompletionsAt(fullTask).scan(mainRoot,cursor);
        System.out.println(path.getLeaf().getKind().toString());
        System.out.println("Partial:"+partial);
        switch (path.getLeaf().getKind()) {
            case IDENTIFIER:
                //System.out.println("indentify");
                completeIdentifier(path,partial,endsWithParen);
                addClassList();
                return;
            case MEMBER_SELECT:
                //System.out.println("member select");
                completeMemberSelect(path,endsWithParen);
                return;
            case MEMBER_REFERENCE:
                //System.out.println("member reference");
                completeMemberReference(path);
                return;
            case SWITCH:
                //System.out.println("switch");
                return;
            case IMPORT:
                addImportClassList();
                return;
            default:
        }
    }

    private String qualifiedPartialIdentifier(String contents, int end) {
        int start = end;
        while (start > 0 && isQualifiedIdentifierChar(contents.charAt(start - 1))) {
            start--;
        }
        return contents.substring(start, end);
    }

    private boolean isQualifiedIdentifierChar(char c) {
        return c == '.' || Character.isJavaIdentifierPart(c);
    }
    private void fullCompile(String nc,String name){
        StringJavaObject current = new StringJavaObject(name,nc);
        clases.remove("/"+name);
        clases.put("/"+name,current);
        List<JavaFileObject> tmp = new ArrayList<>();
        tmp.addAll(clases.values());
            fullTask = (JavacTaskImpl) JavacTool.create().getTask(new PrintWriter(System.out),
                    normal.getStandardFileManager(this, Locale.ENGLISH, Charset.defaultCharset()),
                    this, options(),null,
                    tmp
            );

        mainTree = Trees.instance(fullTask);
        types = fullTask.getTypes();
        try {
            pack.clear();
            for(CompilationUnitTree unitTree : fullTask.parse()) {
                if(unitTree.getPackageName() !=null){
                    pack.put(unitTree.getSourceFile().getName().toString(),unitTree.getPackageName().toString());
                }else {
                    pack.put(unitTree.getSourceFile().getName().toString(),"");
                }
                if(!unitTree.getSourceFile().getName().toString().equals(current.getName().toString())){
                    continue;
                }
                mainRoot = unitTree;
                fullTask.enter();
                fullTask.analyze();
                elements = fullTask.getElements();
                //return;
            }
        }catch (Exception e){
           // e.printStackTrace();
        }
    }

    private void completeMemberReference(TreePath path) {
        MemberReferenceTree select = (MemberReferenceTree)path.getLeaf();
        path = new TreePath(path, select.getQualifierExpression());
        Element element = mainTree.getElement(path);
        boolean isStatic = element instanceof TypeElement;
        Scope scope = mainTree.getScope(path);
        TypeMirror type = mainTree.getTypeMirror(path);
        if (type instanceof Type.ArrayType) {

        } else if (type instanceof TypeVariable) {
            completeTypeVariableMemberReference(scope,(TypeVariable) type,isStatic);
        } else if (type instanceof DeclaredType) {
            completeDeclaredTypeMemberReference(scope,(DeclaredType) type,isStatic);
        }
    }


    private List<String> options() {
        ArrayList<String> list = new ArrayList<String>();
        Collections.addAll(list, "-bootclasspath", String.join(":",libraray));
        // Collections.addAll(list, "-verbose");
        Collections.addAll(list, "-proc:none");
        Collections.addAll(list, "-g");
        list.add("-sourcepath");
        list.add(sourcePath);

       Collections.addAll(
                list,
                "-Xlint:cast",
                "-Xlint:deprecation",
                "-Xlint:empty",
                "-Xlint:fallthrough",
                "-Xlint:finally",
                "-Xlint:path",
                "-Xlint:unchecked",
                "-Xlint:varargs",
                "-Xlint:static");


        return list;
    }
    private String partialIdentifier(String contents, int end) {
        int start = end;
        while (start > 0 && Character.isJavaIdentifierPart(contents.charAt(start - 1))) {
            start--;
        }
        return contents.substring(start, end);
    }

    void completeMemberSelect(TreePath path,boolean ends){
        MemberSelectTree select = (MemberSelectTree) path.getLeaf();
        path = new TreePath(path, select.getExpression());
        boolean isStatic = mainTree.getElement(path) instanceof TypeElement;
        Scope scope = mainTree.getScope(path);
        TypeMirror type = mainTree.getTypeMirror(path);
        if (type instanceof Type.ArrayType) {

        } else if (type instanceof TypeVariable) {
            completeTypeVariableMemberSelect(scope,(TypeVariable) type,isStatic,ends);
        } else if (type instanceof DeclaredType) {
            completeDeclaredTypeMemberSelect(scope,(DeclaredType) type,isStatic,ends);
        }
    }
    private boolean memberMatchesImport(javax.lang.model.element.Name staticImport, Element member) {
        return staticImport.contentEquals("*") || staticImport.contentEquals(member.getSimpleName());
    }
    private void addStaticImport(){
        for(ImportTree i : mainRoot.getImports()){
            if (!i.isStatic()){
                continue;
            }
            MemberSelectTree id = (MemberSelectTree) i.getQualifiedIdentifier();
            if(id.getIdentifier().contentEquals("*")
                    ||matchesPartialName(id.getIdentifier(),partial)){
                TreePath path = mainTree.getPath(mainRoot,id.getExpression());
                TypeElement element = (TypeElement) mainTree.getElement(path);
                for(Element member : fullTask.getElements().getAllMembers(element)){
                    if (!member.getModifiers().contains(Modifier.STATIC)){
                        continue;
                    }
                    if(!memberMatchesImport(id.getIdentifier(),member)){
                        continue;
                    }
                    if(!member.getModifiers().contains(Modifier.PUBLIC)){
                        continue;
                    }
                    if (!matchesPartialName(member.getSimpleName(), partial)) continue;
                    if (member.getKind() == ElementKind.METHOD) {
                        addMethod(member);
                    }else if(member instanceof Symbol.VarSymbol){
                        addField(member);
                    }
                }
            }
        }
    }
    private void completeDeclaredTypeMemberSelect(Scope scope, DeclaredType type, boolean isStatic, boolean endsWithParen) {
        TypeElement typeElement = (TypeElement) type.asElement();
        for (Element member : fullTask.getElements().getAllMembers(typeElement)) {
            if (member.getKind() == ElementKind.CONSTRUCTOR) continue;
            if (!matchesPartialName(member.getSimpleName(), partial)) continue;
            if (!mainTree.isAccessible(scope, member, type)) continue;
            if (isStatic != member.getModifiers().contains(Modifier.STATIC)) continue;
            if (member.getKind() == ElementKind.METHOD) {
                addMethod(member);
            } else if (member instanceof Symbol.VarSymbol){
                addField(member);
            }
        }
    }

    private void completeTypeVariableMemberSelect( Scope scope, TypeVariable type, boolean isStatic, boolean endsWithParen) {
        if (type.getUpperBound() instanceof DeclaredType) {
            completeDeclaredTypeMemberSelect(scope, (DeclaredType) type.getUpperBound(), isStatic, endsWithParen);
        } else if (type.getUpperBound() instanceof TypeVariable) {
            completeTypeVariableMemberSelect( scope, (TypeVariable) type.getUpperBound(), isStatic,  endsWithParen);
        }
    }

    private void completeTypeVariableMemberReference(Scope scope, TypeVariable type, boolean isStatic){
        if (type.getUpperBound() instanceof DeclaredType) {
            completeDeclaredTypeMemberReference(scope,
                    (DeclaredType) type.getUpperBound(), isStatic);
        } else if (type.getUpperBound() instanceof TypeVariable) {
            completeTypeVariableMemberReference(
                    scope, (TypeVariable) type.getUpperBound(), isStatic);
        }
    }
    private void completeDeclaredTypeMemberReference(Scope scope, DeclaredType type, boolean isStatic){
        TypeElement typeElement = (TypeElement) type.asElement();
        for (Element member : fullTask.getElements().getAllMembers(typeElement)) {
            if(!matchesPartialName(member.getSimpleName().toString(),partial)){
                continue;
            }
            if (member.getKind() != ElementKind.METHOD) continue;
            if (!mainTree.isAccessible(scope, member, type)) continue;
            if (!isStatic && member.getModifiers().contains(Modifier.STATIC)) continue;
            if (member.getKind() == ElementKind.METHOD) {
                addMethod(member);
            } else if(member instanceof Symbol.VarSymbol){
                addField(member);
            }
        }
    }

    private boolean endsWithParen(String contents, int cursor) {
        for (int i = cursor; i < contents.length(); i++) {
            if (!Character.isJavaIdentifierPart(contents.charAt(i))) {
                return contents.charAt(i) == '(';
            }
        }
        return false;
    }

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        if(diagnostic.getKind() == Diagnostic.Kind.ERROR) {
            error.add(diagnostic.toString());
            number.add((int) diagnostic.getLineNumber());
        }
    }
    private void addClassList(){
        for(Map.Entry<String,String> vl : pack.entrySet()){
            VariableData data = new VariableData();
            String name = vl.getKey().substring(1).replace(".java","");
            if(name.startsWith(partial)) {
                data.type = vl.getValue().length() > 0 ? vl.getValue() + "." + name + ".class" : name;
                data.name = name;
                suggestion.add(data);
            }
        }
        if(jarPackage != null){
            for(String[] name : jarPackage.search(partial)){
                VariableData data = new VariableData();
                data.type = name[1];
                data.name = name[0];
                suggestion.add(data);
            }
        }
    }
    private void addImportClassList(){
        for(Map.Entry<String,String> vl : pack.entrySet()){
            VariableData data = new VariableData();
            String name = vl.getKey().substring(1).replace(".java","");
            if(name.startsWith(partial)) {
                data.type = vl.getValue().length() > 0 ? vl.getValue() + "." + name + ".class" : name;
                data.name = data.type.substring(0, data.type.length() - 6);
                suggestion.add(data);
            }
        }
        if(jarPackage != null){
            for(String[] name : jarPackage.search(partial)){
                VariableData data = new VariableData();
                data.type = name[1];
                data.name = name[1].substring(0, name[1].length() - 6);
                suggestion.add(data);
            }

        }

    }

    private void completeIdentifier( TreePath path, String partial, boolean endsWithParen) {
        for(String nn : METHOD_BODY_KEYWORDS){
            if(nn.startsWith(partial)){
                VariableData data = new VariableData();
                data.type = "";
                data.name = nn;
                suggestion.add(data);
            }
        }
        
        Scope scope = mainTree.getScope(path);
        ExecutableElement method = scope.getEnclosingMethod();
        if(method == null){
           if(scope.getEnclosingClass()!=null){
               addOverride(scope,partial);
           }
            return;
        }
        List<Element> members = scopeMembers(fullTask, scope, partial);
        for(Element member : members){
            if (member.getKind() == ElementKind.METHOD) {
                addMethod(member);
            } else if(member instanceof Symbol.VarSymbol){
                addField(member);
            }else if(member instanceof  Symbol.ClassSymbol){
                if (member.getSimpleName().toString().startsWith(partial)) {
                    VariableData data = new VariableData();
                    data.type = member.getSimpleName().toString();
                    data.name = member.getSimpleName().toString();
                    suggestion.add(data);
                }
            }
        }
        addStaticImport();
    }

    private void addMethod(Element member){
        Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) member;
        if(!methodSymbol.isConstructor()) {
            MethodData methodData = new MethodData();
            methodData.isStatic = true;
            methodData.name = methodSymbol.name.toString();
            methodData.type = methodSymbol.getReturnType().toString();
            methodData.assistName = methodData.toString();
            List<Symbol.VarSymbol> params = methodSymbol.getParameters();
            int co=0;
            if (params != null) {
                for (Symbol.VarSymbol sym : params) {
                    Parameter par = new Parameter();
                    par.argName = sym.getSimpleName().toString();
                    if(par.argName.startsWith("arg") && !sym.type.isPrimitive()){
                        par.argName = sym.type.tsym.getSimpleName().toString().toLowerCase();
                    }
                    par.className = sym.type.toString();
                    methodData.params.add(par);
                    co++;
                }
            }
            suggestion.add(methodData);
        }
    }

    private void addField(Element member){
        Symbol.VarSymbol val = (Symbol.VarSymbol)member;
        VariableData variableData = new VariableData();
        variableData.name = val.name.toString();
        variableData.type = val.type.toString();
        variableData.isStatic = member.getModifiers().contains(Modifier.STATIC);
        suggestion.add(variableData);
    }

    private void addOverride(Scope scope,String part){
        if (scope.getEnclosingClass() != null) {
            TypeElement typeElement = scope.getEnclosingClass();
            DeclaredType typeType = (DeclaredType) typeElement.asType();
            for (Element member : elements.getAllMembers(typeElement)) {
                if (!matchesPartialName(member.getSimpleName(),part)) continue;
                if (! mainTree.isAccessible(scope, member, typeType)) continue;
                if (member.getModifiers().contains(Modifier.STATIC)) continue;
                if (!member.getModifiers().contains(Modifier.PUBLIC)) continue;
                if (member.getKind() == ElementKind.METHOD) {
                    Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) member;
                    if(methodSymbol.isConstructor()) continue;
                    MethodData methodData = new MethodData();
                    methodData.name = createNameOverride(methodSymbol);
                    suggestion.add(methodData);
                }
            }
        }
    }
    private String createNameOverride(Symbol.MethodSymbol methodSymbol){
        StringBuilder sb = new StringBuilder();
        long flags = methodSymbol.flags();
        if ((flags & Flags.PUBLIC) != 0) sb.append("public ");
        if ((flags & Flags.PROTECTED) != 0) sb.append("protected ");
        if ((flags & Flags.PRIVATE) != 0) sb.append("private ");
        if ((flags & Flags.STATIC) != 0) sb.append("static ");
        if ((flags & Flags.ABSTRACT) != 0) sb.append("abstract ");
        if ((flags & Flags.FINAL) != 0) sb.append("final ");
        if ((flags & Flags.SYNCHRONIZED) != 0) sb.append("synchronized ");
        if ((flags & Flags.NATIVE) != 0) sb.append("native ");
        if ((flags & Flags.STRICTFP) != 0) sb.append("strictfp ");

        // 2. Return Type
        Type returnType = methodSymbol.getReturnType();
        sb.append(returnType.toString()).append(" ");
        // 3. Method Name
        sb.append(methodSymbol.getSimpleName()).append("(");
        List<Symbol.VarSymbol> parameters = methodSymbol.getParameters();
        List<String> args = new ArrayList<>();
        for(Symbol.VarSymbol symbol : parameters){
            args.add(symbol.type.tsym +" "+symbol.getSimpleName().toString());
        }
        if(!args.isEmpty()){
            sb.append(String.join(",",args));
        }
        args.clear();
        sb.append(")");
        List<Type> throes = methodSymbol.getThrownTypes();
        for(Type a : throes){
            args.add(a.toString());
        }
        if(!args.isEmpty()){
            sb.append(" throws ");
            sb.append(String.join(",",args));
        }
        sb.append("{\n\n}\n");
        return sb.toString();
    }
    private static List<Element> scopeMembers(JavacTask task, Scope inner,String part) {
        Trees trees = Trees.instance(task);
        elements = task.getElements();
        boolean isStatic = false;
        List<Element> list = new ArrayList<Element>();
        for (Scope scope : fastScopes(inner)) {
            if (scope.getEnclosingMethod() != null) {
                isStatic = isStatic || scope.getEnclosingMethod().getModifiers().contains(Modifier.STATIC);
            }
            for (Element member : scope.getLocalElements()) {
                if (!matchesPartialName(member.getSimpleName(),part)) continue;
                if (isStatic && member.getSimpleName().contentEquals("this")) continue;
                if (isStatic && member.getSimpleName().contentEquals("super")) continue;
                list.add(member);
            }
            if (scope.getEnclosingClass() != null) {
                TypeElement typeElement = scope.getEnclosingClass();
                DeclaredType typeType = (DeclaredType) typeElement.asType();
                for (Element member : elements.getAllMembers(typeElement)) {
                    if (!matchesPartialName(member.getSimpleName(),part)) continue;
                    if (!trees.isAccessible(scope, member, typeType)) continue;
                    if (isStatic && !member.getModifiers().contains(Modifier.STATIC)) continue;
                    list.add(member);
                }
                isStatic = isStatic || typeElement.getModifiers().contains(Modifier.STATIC);
            }
        }
        return list;
    }

    private static List<Scope> fastScopes(Scope start) {
        List<Scope> scopes = new ArrayList<Scope>();
        for (Scope s = start; s != null; s = s.getEnclosingScope()) {
            scopes.add(s);
        }
        return scopes.subList(0, scopes.size() - 2);
    }

    private static boolean matchesPartialName(CharSequence candidate, CharSequence partialName) {
        if (candidate.length() < partialName.length()) return false;
        for (int i = 0; i < partialName.length(); i++) {
            if (candidate.charAt(i) != partialName.charAt(i)) return false;
        }
        return true;
    }



}
