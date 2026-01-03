package engine.framework;

import de.javagl.obj.Obj;
import engine.internal.util.ObjectUtils;

public class SceneInterface {
    public boolean active = true;
    @Flags(flag = "IGNORE")
    public Scene targetScene;
    private boolean state;
    public void onCreate(){

    }
    public void onInitialize(){

    }
    public void onStart(){

    }
    public void onPreUpdate(){

    }
    public void onUpdate(){
        if(!state){
            state = true;
            onStateChange(state);
        }
    }
    public void onPostUpdate(){

    }
    public void onDisabled(){
        if(state){
            state = false;
            onStateChange(state);
        }
    }
    public void onStateChange(boolean current){

    }
    public void onRemoved(){

    }
    public byte[] getBytes(){
        return ObjectUtils.saveState(this);
    }
    public void fromBytes(byte[] bytes){
        ObjectUtils.recallState(this,bytes,getClass().getClassLoader());
    }
    public Object invoke(int i,Object object){
        return null;
    }
    public void onDataChange(String name,Object value){

    }
    @Override
    protected void finalize() throws Throwable {
        System.out.println(getClass().getSimpleName()+" is Destroyed By JVM");
    }
}
