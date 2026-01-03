package engine.internal.util;

import android.content.res.AssetFileDescriptor;

import java.io.IOException;
import java.io.InputStream;

import engine.framework.EngineQuery;
import engine.framework.Flags;
import engine.framework.ObjectModifier;
import engine.framework.SceneInterface;

public class AssetFile {

    @EngineQuery(name = "file",value = "all")
    private String path="";
    private ObjectModifier modifier;
    private SceneInterface sceneInterface;
    public AssetFile(ObjectModifier objectModifier){
        modifier = objectModifier;
    }
    public AssetFile(SceneInterface sceneInterface){
        this.sceneInterface = sceneInterface;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
    public boolean isExist(){
        if(sceneInterface !=null){
            InputStream ins= sceneInterface.targetScene.targetEngineInstance.openAssetFile(path);
            if(ins != null){
                try {
                    ins.close();
                } catch (Exception e) {

                }
                return true;
            }
        }
        if(modifier !=null){
            InputStream ins= modifier.targetObject.targetScene.targetEngineInstance.openAssetFile(path);
            if(ins != null){
                try {
                    ins.close();
                } catch (Exception e) {

                }
                return true;
            }
        }
        return false;
    }
    public InputStream getInputStream(){
        if(sceneInterface !=null){
           return sceneInterface.targetScene.targetEngineInstance.openAssetFile(path);
        }
        if(modifier !=null){
            return modifier.targetObject.targetScene.targetEngineInstance.openAssetFile(path);
        }
       return null;
    }
    public long getFileSize(){
        if(sceneInterface !=null){
            return sceneInterface.targetScene.targetEngineInstance.getFileSize(path);
        }
        if(modifier !=null){
            return modifier.targetObject.targetScene.targetEngineInstance.getFileSize(path);
        }
        return -1;
    }
    public AssetFileDescriptor getFileDescriptor(){
        if(sceneInterface !=null){
            return sceneInterface.targetScene.targetEngineInstance.getAssetFile(path);
        }
        if(modifier !=null){
            return modifier.targetObject.targetScene.targetEngineInstance.getAssetFile(path);
        }
        return null;
    }
}
