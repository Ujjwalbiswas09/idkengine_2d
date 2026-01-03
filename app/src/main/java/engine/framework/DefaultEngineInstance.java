package engine.framework;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import static android.opengl.GLES31.*;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import engine.internal.input.InputManager;
import engine.internal.input.TouchData;
import engine.internal.input.TouchListener;
import engine.internal.math.Vector2;

public class DefaultEngineInstance extends EngineInstance implements GLSurfaceView.Renderer {

    private GLSurfaceView view;
    private Activity activity;
    private List<Scene> sceneList = new ArrayList<>();
    private List<Scene> preparedList = new ArrayList<>();
    private List<Runnable> runnables = new Vector<>();
    private int width;
    private int height;
    private TouchData2[] touchData = new TouchData2[16];
    private List<TouchListener> listeners = new ArrayList<>();
    private List<TouchData> valid = new ArrayList<>();
    
    public DefaultEngineInstance(Activity activity){
        this.activity = activity;
        view = new CustomView(activity);
        view.setEGLContextClientVersion(3);
        view.setPreserveEGLContextOnPause(true);
        view.setRenderer(this);
        for(int i =0;i < 16; i++){
            touchData[i]= new TouchData2();
            touchData[i].id = -1;
        }
        
    }
    @Override
    public InputManager getInputManager() {
        return new TouchManagerImpl();
    }

    @Override
    public AssetFileDescriptor getAssetFile(String name) {
        try {
            return activity.getAssets().openFd(name);
        } catch (IOException e) {
           //throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public InputStream openAssetFile(String name) {
        try {
            return activity.getAssets().open(name);
        } catch (IOException e) {
            //hrow new RuntimeException(e);
        }
        return null;
    }

    @Override
    public long getFileSize(String name) {
        return getAssetFile(name).getLength();
    }

    @Override
    public String[] assetList(String path) {
        try {
            return activity.getAssets().list(path);
        } catch (IOException e) {
           // throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void addScene(Scene scene) {
        sceneList.add(scene);
        engineQueue(new Runnable() {
            @Override
            public void run() {
                startScene(scene);
                preparedList.add(scene);
            }
        });
    }

    @Override
    public Scene getScene(int i) {
        return sceneList.get(i);
    }

    @Override
    public int getSceneCount() {
        return sceneList.size();
    }

    @Override
    public void addSceneAsync(Scene scene) {
        scene.targetEngineInstance = this;
        sceneList.add(scene);
    }

    @Override
    public void removeScene(Scene scene) {
        sceneList.remove(scene);
        preparedList.remove(scene);
    }

    @Override
    public void engineQueue(Runnable runnable) {
       runnables.add(runnable);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Context getApplicationContext() {
        return activity.getApplicationContext();
    }

    @Override
    public Object getObject(String key) {
        return null;
    }

    @Override
    public boolean hasValue(String key) {
        return false;
    }

    @Override
    public void putValue(String key, Object value) {

    }

    @Override
    public void removeValue(String key) {

    }

    public View getView(){
        return view;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        width = i;
        height = i1;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        while (!runnables.isEmpty()){
            Runnable ra = runnables.remove(0);
            ra.run();
        }
        glClearColor(1,1,0,1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        Scene[] scenes = preparedList.toArray(new Scene[0]);
        for(Scene scene : scenes){
            preUpdate(scene);
        }
        scenes = preparedList.toArray(new Scene[0]);
        for(Scene scene : scenes){
            updateScene(scene);
        }
        scenes = preparedList.toArray(new Scene[0]);
        for(Scene scene : scenes){
            postUpdate(scene);
        }
    }
    private void updateScene(Scene scene){
        SceneInterface[] interfaces = scene.getInterfaces();
        for (SceneInterface sceneInterface : interfaces){
            if(sceneInterface.active){
                sceneInterface.onUpdate();
            }else {
                sceneInterface.onDisabled();
            }
        }
        GameObject[] objects = scene.getObjects();
        for(int go =0;go < objects.length;go++){
            GameObject current = objects[go];
            ObjectModifier[] modifiers = current.getModifiers();
            if(current.active){
                for(ObjectModifier objectModifier : modifiers){
                    if(objectModifier.active){
                        objectModifier.onUpdate();
                    }else {
                        objectModifier.onDisabled();
                    }
                }
            }else {
                for(ObjectModifier objectModifier : modifiers){
                    objectModifier.onDisabled();
                }
            }
        }
    }
    private void preUpdate(Scene scene){
        valid.clear();
        for(int j=0;j < 16;j++){
            TouchData2 touchData2 = touchData[j];
            if(touchData2.id != -1){
                valid.add(touchData2);
            }
            if(touchData2.Pressed){
                for(int i=0;i < listeners.size();i++){
                    listeners.get(i).onPressed(touchData2.copy());
                }
                touchData2.Pressed = false;
            }
            if(!touchData2.current.equals(touchData2.previous)){
                for(int i=0;i < listeners.size();i++){
                    listeners.get(i).onDragged(touchData2.copy());
                }
            }
            if(touchData2.Released){
                for(int i=0;i < listeners.size();i++){
                    listeners.get(i).onReleased(touchData2.copy());
                }
                touchData2.Released = false;
            }
        }
        SceneInterface[] interfaces = scene.getInterfaces();
        for (SceneInterface sceneInterface : interfaces){
            if(sceneInterface.active) {
                sceneInterface.onPreUpdate();
            }
        }
        GameObject[] objects = scene.getObjects();
        for(int go =0;go < objects.length;go++){
            GameObject current = objects[go];
            if(current.active){
                ObjectModifier[] modifiers = current.getModifiers();
                for(ObjectModifier objectModifier : modifiers){
                    if(objectModifier.active){
                        objectModifier.onPreUpdate();
                    }
                }
            }
        }
    }

    private void postUpdate(Scene scene){

        GameObject[] objects = scene.getObjects();
        for(int go =0;go < objects.length;go++){
            GameObject current = objects[go];
            if(current.active){
                ObjectModifier[] modifiers = current.getModifiers();
                for(ObjectModifier objectModifier : modifiers){
                    if(objectModifier.active){
                        objectModifier.onPostUpdate();
                    }
                }
            }
        }
        SceneInterface[] interfaces = scene.getInterfaces();
        for (SceneInterface sceneInterface : interfaces){
            if(sceneInterface.active) {
                sceneInterface.onPostUpdate();
            }
        }
    }

    private void startScene(Scene scene){
        scene.targetEngineInstance = this;
        SceneInterface[] interfaces = scene.getInterfaces();
        for (SceneInterface sceneInterface : interfaces){
            sceneInterface.targetScene = scene;
                sceneInterface.onCreate();
        }
        GameObject[] objects = scene.getObjects();
        for(int go =0;go < objects.length;go++){
            GameObject current = objects[go];
            current.targetScene = scene;
                ObjectModifier[] modifiers = current.getModifiers();
                for(ObjectModifier objectModifier : modifiers){
                    objectModifier.targetObject = current;
                  objectModifier.onCreate();
                }
        }

        interfaces = scene.getInterfaces();
        for (SceneInterface sceneInterface : interfaces){
            sceneInterface.onInitialize();
        }

        objects = scene.getObjects();
        for(int go =0;go < objects.length;go++){
            GameObject current = objects[go];
            ObjectModifier[] modifiers = current.getModifiers();
            for(ObjectModifier objectModifier : modifiers){
                objectModifier.onInitialize();
            }
        }

        objects = scene.getObjects();
        for(int go =0;go < objects.length;go++){
            GameObject current = objects[go];
            ObjectModifier[] modifiers = current.getModifiers();
            for(ObjectModifier objectModifier : modifiers){
                objectModifier.onStart();
            }
        }

        interfaces = scene.getInterfaces();
        for (SceneInterface sceneInterface : interfaces){
            sceneInterface.onStart();
        }
    }
    private ArrayList<Integer> integers = new ArrayList<>();
    private void updateTouch(MotionEvent event) {
      //  if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
       //     System.out.println("Down");
     //   }
        boolean[] states = new boolean[16];
        for(int i = 0; i < event.getPointerCount();i++){
            int id = event.getPointerId(i);
            states[id] = true;
            float px = event.getX(i);
            float py = event.getY(i);
            touchData[id].tmp.set(px,py);
        }

        if(event.getActionMasked() == MotionEvent.ACTION_UP){
            states[event.getPointerId(event.getActionIndex())] = false;
        }
        if(event.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
            states[event.getPointerId(event.getActionIndex())] = false;
        }
        if(event.getActionMasked() == MotionEvent.ACTION_CANCEL){
            for(int i = 0; i < 16;i++) {
                states[i] = false;
            }
        }
        for(int i = 0; i < 16;i++){
            boolean pressed = states[i];
            TouchData2 data = touchData[i];
            if(pressed){

                if(data.id == -1){
                    data.id = i;
                    data.timestamp = System.currentTimeMillis();
                    data.previous.set(data.tmp);
                    data.Pressed = true;
                   // System.out.println("pressed");
                }else {
                    data.previous.set(data.current);
                    //System.out.println("dragged");
                }
                data.current.set(data.tmp);
            }else {
                data.previous.set(data.tmp);
                data.current.set(data.tmp);
                if(data.id != -1){
                    data.Released = true;
                }
                data.id = -1;
            }
        }

    }

    private class CustomView extends GLSurfaceView{
        CustomView(Context context){
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            try {
                updateTouch(event);
               // System.out.println(event);
            }catch (Exception e){
                e.printStackTrace(System.out);
            }
            return true;
        }
    }
    private static class TouchData2 extends TouchData{
        boolean Pressed;
        boolean Released;
        Vector2 tmp = new Vector2();
    }

    private class TouchManagerImpl extends InputManager{
        public int getTouchCount(){
            return valid.size();
        }
        public TouchData getTouchData(int i){
            return valid.get(i).copy();
        }
        public void addTouchListener(TouchListener listener){
            listeners.add(listener);
        }
        public void removeTouchListener(TouchListener listener){
            listeners.remove(listener);
        }
    }
}
