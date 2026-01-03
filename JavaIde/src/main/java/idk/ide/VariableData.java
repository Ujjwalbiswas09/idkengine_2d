package idk.ide;

public class VariableData implements MemberData{

    public String name;
    public String type;
    public boolean isStatic;
    public VariableData(){

    }

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
        return name+"-> "+type;
    }
    @Override
    public boolean isStatic() {
        return isStatic;
    }
    public String toString() {
        return name+"->"+type+":"+isStatic;
    }
}
