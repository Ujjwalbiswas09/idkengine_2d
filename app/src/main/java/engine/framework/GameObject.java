package engine.framework;

import java.util.ArrayList;
import java.util.List;

import engine.internal.math.Quaternion;
import engine.internal.math.Vector3;

public class GameObject {
    private ArrayList<ObjectModifier> modifiers = new ArrayList<>();
    public String name = "Default";
    public boolean active = true;
    @Flags(flag = "IGNORE")
    public Scene targetScene;
    public Vector3 position = new Vector3();
    public Vector3 scale = new Vector3(1,1,1);
    public Quaternion rotation = new Quaternion();

    public void addModifier(ObjectModifier modifier){
        modifiers.add(modifier);
        if(targetScene != null && targetScene.targetEngineInstance != null){
            modifier.targetObject = this;
            if(targetScene.targetEngineInstance.hasValue("editor_instance")) {
                modifier.invoke(0,null);
                modifier.invoke(1,null);
                modifier.invoke(2,null);
            }else{

                modifier.onCreate();
                modifier.onInitialize();
                modifier.onStart();
            }
        }
    }
    public void removeModifier(ObjectModifier modifier){
        if(targetScene != null){
            modifier.onRemoved();
        }
        modifiers.remove(modifier);
    }

    public ObjectModifier[] getModifiers(){
        return modifiers.toArray(new ObjectModifier[0]);
    }
    public int getModifierCount(){
        return modifiers.size();
    }
    public ObjectModifier getModifier(int i){
        return modifiers.get(i);
    }

    public <T extends ObjectModifier> List<T> getModifier(Class<T> cls){
       List<T> objectModifiers = new ArrayList<>();
       for(ObjectModifier objectModifier : modifiers){
           if(cls.isAssignableFrom(objectModifier.getClass())){
               objectModifiers.add((T)objectModifier);
           }
       }
       return objectModifiers;
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("GameObject \"" + name + "\" is Destroyed By JVM");
    }
}
