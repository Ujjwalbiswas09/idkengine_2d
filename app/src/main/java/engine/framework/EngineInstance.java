package engine.framework;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.opengl.GLES31;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import engine.internal.graphics.Texture;
import engine.internal.input.InputManager;

public abstract class EngineInstance {

    private static List<EngineInstance> instances = new ArrayList<>();
    public EngineInstance(){
        instances.add(this);
    }

    public static List<EngineInstance> getInstances() {
        return instances;
    }

    public abstract InputManager getInputManager();
    public abstract AssetFileDescriptor getAssetFile(String name);
    public abstract InputStream openAssetFile(String name);
    public abstract long getFileSize(String name);
    public abstract String[] assetList(String path);
    public abstract void addScene(Scene scene);
    public abstract Scene getScene(int i);
    public abstract int getSceneCount();
    public abstract void addSceneAsync(Scene scene);
    public abstract void removeScene(Scene scene);
    public abstract void engineQueue(Runnable runnable);
    public abstract int getWidth();
    public abstract int getHeight();
    public abstract Context getApplicationContext();
    public abstract Object getObject(String key);
    public abstract boolean hasValue(String key);
    public abstract void putValue(String key,Object value);
    public abstract void removeValue(String key);
}
