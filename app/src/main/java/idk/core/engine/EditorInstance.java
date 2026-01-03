package idk.core.engine;

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_CCW;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FRONT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glFrontFace;

import static javax.microedition.khronos.opengles.GL10.GL_CW;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.opengl.GLSurfaceView;
import android.os.ParcelFileDescriptor;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import engine.framework.DefaultEngineInstance;
import engine.framework.EngineInstance;
import engine.framework.GameObject;
import engine.framework.ObjectModifier;
import engine.framework.Scene;
import engine.framework.SceneInterface;
import engine.internal.input.InputManager;
import engine.internal.input.TouchData;
import engine.internal.input.TouchListener;
import engine.internal.math.Vector2;

public class EditorInstance extends EngineInstance implements GLSurfaceView.Renderer{
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

    public String assetDirectory;
    public HashMap<String,Object> customData = new HashMap<>();
    public EditorInstance(Activity activity){
        this.activity = activity;
        view = new CustomView(activity);
        view.setEGLContextClientVersion(3);
        view.setPreserveEGLContextOnPause(true);
        view.setRenderer(this);
        for(int i =0;i < 16; i++){
            touchData[i]= new TouchData2();
            touchData[i].id = -1;
        }
        putValue("editor_instance",true);
    }
    @Override
    public InputManager getInputManager() {
        return new TouchManagerImpl();
    }

    @Override
    public AssetFileDescriptor getAssetFile(String name) {
        try {
            File file = new File(HomeActivity.getCurrent().project+"/assets",name);
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY);
            AssetFileDescriptor assetFileDescriptor = new AssetFileDescriptor(parcelFileDescriptor,0,file.length());
            return  assetFileDescriptor;
        } catch (IOException e) {
            return null;
        }
    }


    @Override
    public InputStream openAssetFile(String name) {
        try {
            return new FileInputStream(new File(HomeActivity.getCurrent().project+"/assets",name));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public long getFileSize(String name) {
        File file = new File(HomeActivity.getCurrent().project+"/assets",name);
        return file.length();
    }

    @Override
    public String[] assetList(String path) {
        try {
            File[] file = new File(HomeActivity.getCurrent().project+"/assets",path).listFiles();
            String[] arr = new String[file.length];
            for(int i= 0;i < arr.length;i++){
                String projectPath = file[i].getAbsolutePath().substring(
                        (HomeActivity.getCurrent().project+
                                "/assets").length());
                arr[i] = projectPath;
            }
            return arr;
        } catch (Exception e) {

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
        return customData.get(key);
    }

    @Override
    public boolean hasValue(String key) {
        return customData.containsKey(key);
    }

    @Override
    public void putValue(String key, Object value) {
        customData.put(key,value);
    }

    @Override
    public void removeValue(String key) {
        customData.remove(key);
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
        glClearColor(0.1f ,0.1f,0.1f,1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CW);
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
                sceneInterface.invoke(5,null);
            }else {
                sceneInterface.invoke(7,null);
            }
        }
        GameObject[] objects = scene.getObjects();
        for(int go =0;go < objects.length;go++){
            GameObject current = objects[go];
            ObjectModifier[] modifiers = current.getModifiers();
            if(current.active){
                for(ObjectModifier objectModifier : modifiers){
                    if(objectModifier.active){
                        objectModifier.invoke(5,null);
                    }else {
                        objectModifier.invoke(7,null);
                    }
                }
            }else {
                for(ObjectModifier objectModifier : modifiers){
                    objectModifier.invoke(7,null);
                }
            }
            modifiers = null;
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
                sceneInterface.invoke(4,null);
            }
        }
        GameObject[] objects = scene.getObjects();
        for(int go =0;go < objects.length;go++){
            GameObject current = objects[go];
            if(current.active){
                ObjectModifier[] modifiers = current.getModifiers();
                for(ObjectModifier objectModifier : modifiers){
                    if(objectModifier.active){
                        objectModifier.invoke(4,null);
                    }
                }
                modifiers = null;
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
                        objectModifier.invoke(6,null);
                    }
                }
                modifiers = null;
            }
        }
        SceneInterface[] interfaces = scene.getInterfaces();
        for (SceneInterface sceneInterface : interfaces){
            if(sceneInterface.active) {
                sceneInterface.invoke(6,null);
            }
        }
    }

    private void startScene(Scene scene){
        scene.targetEngineInstance = this;
        SceneInterface[] interfaces = scene.getInterfaces();
        for (SceneInterface sceneInterface : interfaces){
            sceneInterface.targetScene = scene;
            sceneInterface.invoke(0,null);
        }
        GameObject[] objects = scene.getObjects();
        for(int go =0;go < objects.length;go++){
            GameObject current = objects[go];
            current.targetScene = scene;
            ObjectModifier[] modifiers = current.getModifiers();
            for(ObjectModifier objectModifier : modifiers){
                objectModifier.targetObject = current;
                objectModifier.invoke(0,null);
            }
        }

        interfaces = scene.getInterfaces();
        for (SceneInterface sceneInterface : interfaces){
            sceneInterface.invoke(1,null);
        }

        objects = scene.getObjects();
        for(int go =0;go < objects.length;go++){
            GameObject current = objects[go];
            ObjectModifier[] modifiers = current.getModifiers();
            for(ObjectModifier objectModifier : modifiers){
                objectModifier.invoke(1,null);
            }
        }

        objects = scene.getObjects();
        for(int go =0;go < objects.length;go++){
            GameObject current = objects[go];
            ObjectModifier[] modifiers = current.getModifiers();
            for(ObjectModifier objectModifier : modifiers){
                objectModifier.invoke(2,null);
            }
        }

        interfaces = scene.getInterfaces();
        for (SceneInterface sceneInterface : interfaces){
            sceneInterface.invoke(2,null);
        }
    }
    private ArrayList<Integer> integers = new ArrayList<>();
    private void updateTouch(MotionEvent event) {
        boolean[] states = new boolean[16];
        for(int i = 0; i < event.getPointerCount();i++){
            int id = event.getPointerId(i);
            states[id] = true;
            float px = event.getX(i);
            float py = height - event.getY(i);
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
                }else {
                    data.previous.set(data.current);
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
