package idk.ide;

import java.util.ArrayList;
import java.util.List;

public class CompletionResult {
    public List<MemberData> members;
    public String lastWord;
    public int pos;
    public List<Integer> error;
    public CharSequence code;
    public boolean insideMethod;
    public List<Integer> warning;
}
