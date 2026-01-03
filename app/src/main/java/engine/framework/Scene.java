package engine.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import engine.internal.graphics.ShaderProgram;

public class Scene {
    private ArrayList<GameObject> objects = new ArrayList<>();
    private ArrayList<SceneInterface> interfaces = new ArrayList<>();
    public boolean active = true;
    public EngineInstance targetEngineInstance;
    public GameObject[] getObjects() {
        return objects.toArray(new GameObject[0]);
    }
    public SceneInterface[] getInterfaces() {
        return interfaces.toArray(new SceneInterface[0]);
    }
    public HashMap<String,Object> userData = new HashMap<>();
    public <T extends SceneInterface> List<T> getInterface(Class<T> cls){
        List<T> sceneInterfaces = new ArrayList<>();
        for(SceneInterface sceneInterface : interfaces){
            if(sceneInterface.getClass() == cls){
                sceneInterfaces.add((T)sceneInterface);
            }
        }
        return sceneInterfaces;
    }
    public <T extends SceneInterface> List<T> getAssignableInterface(Class<T> cls){
        List<T> sceneInterfaces = new ArrayList<>();
        for(SceneInterface sceneInterface : interfaces){
            if(cls.isAssignableFrom(sceneInterface.getClass())){
                sceneInterfaces.add((T)sceneInterface);
            }
        }
        return sceneInterfaces;
    }
    public int getObjectCount(){
        return objects.size();
    }
    public GameObject getObject(int i){
        return objects.get(i);
    }
    public List<GameObject> getObjectWithName(String name){
        return null;
    }
    public GameObject getFirstObjectWithName(String name){
        return null;
    }
    public void addObject(GameObject object){
        objects.add(object);
        if(targetEngineInstance != null){
            object.targetScene = this;
            ObjectModifier[] modifiers = object.getModifiers();
            if(!targetEngineInstance.hasValue("editor_instance")) {
                for (ObjectModifier objectModifier : modifiers) {
                    objectModifier.targetObject = object;
                    objectModifier.onCreate();
                }
                modifiers = object.getModifiers();
                for (ObjectModifier objectModifier : modifiers) {
                    objectModifier.onInitialize();
                }
                modifiers = object.getModifiers();
                for (ObjectModifier objectModifier : modifiers) {
                    objectModifier.onStart();
                }
            }else {
                for (ObjectModifier objectModifier : modifiers) {
                    objectModifier.targetObject = object;
                    objectModifier.invoke(0,null);
                }
                modifiers = object.getModifiers();
                for (ObjectModifier objectModifier : modifiers) {
                    objectModifier.invoke(1,null);
                }
                modifiers = object.getModifiers();
                for (ObjectModifier objectModifier : modifiers) {
                    objectModifier.invoke(2,null);
                }
            }
        }
    }
    public void removeObject(GameObject object){
        if(targetEngineInstance != null) {
            ObjectModifier[] modifiers = object.getModifiers();
            for (ObjectModifier objectModifier : modifiers) {
                objectModifier.onRemoved();
            }
        }
        objects.remove(object);
    }

    public void addInterface(SceneInterface sceneInterface){
        interfaces.add(sceneInterface);
        if(targetEngineInstance != null){
            if(targetEngineInstance.hasValue("editor_instance")) {
                sceneInterface.invoke(0, null);
                sceneInterface.invoke(1, null);
                sceneInterface.invoke(2, null);
            }else {
                sceneInterface.targetScene = this;
                sceneInterface.onCreate();
                sceneInterface.onInitialize();
                sceneInterface.onStart();
            }
        }
    }
    public void removeInterface(SceneInterface sceneInterface){
        if(targetEngineInstance != null){
                sceneInterface.onRemoved();
        }
        interfaces.remove(sceneInterface);
    }

}