package idk.ide;



import java.util.ArrayList;
import java.util.List;

public class MethodData implements MemberData{

    public MethodData(){

    }
    public String name;
    public String type;
    public boolean isStatic;
    public String assistName;
    public List<Parameter> params = new ArrayList<>();
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String assistName() {
        return assistName+"->"+type;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public String toString() {
        return name+"->"+type+":"+isStatic;
    }
}
