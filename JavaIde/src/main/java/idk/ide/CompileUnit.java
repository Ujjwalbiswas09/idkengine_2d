package idk.ide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CompileUnit {
    public CharSequence code;
    public int position;
    public List<File> sourceFiles = new ArrayList<>();
    public List<File> libraries =  new ArrayList<>();
    public CompletionListener listener;
}
