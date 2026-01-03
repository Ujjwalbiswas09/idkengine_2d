package engine.framework;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import engine.internal.util.ObjectUtils;

public class ObjectModifier {
    public void onDataReceived(String key, Object data) {

    }
    public Object invoke(int i,Object object){
        return null;
    }
    public boolean active = true;

    @Flags(flag = {"IGNORE","INVISIBLE"})
    public GameObject targetObject;

    @Flags(flag = {"IGNORE","INVISIBLE"})
    public Object userData = null;
    private boolean state = true;
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

    public GameObject getTargetObject() {
        return targetObject;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println(getClass().getSimpleName()+" Destroyed By JVM");
        super.finalize();
    }
}
