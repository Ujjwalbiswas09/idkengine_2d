package engine.core;

import android.opengl.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

import engine.framework.EngineInstance;
import engine.framework.ObjectModifier;
import engine.framework.SceneParser;
import engine.internal.input.InputManager;
import engine.internal.input.TouchData;
import engine.internal.math.Matrix4;
import engine.internal.math.Vector3;

public class Camera extends ObjectModifier {
    public float fieldOfView = 40f;
    public float minDistance = 0.1f;
    public float maxDistance = 500f;
    public Matrix4 projectMatrix = new Matrix4();
    public Matrix4 viewMatrx = new Matrix4();

    public boolean isForeground = true;

    private InputManager manager;

    @Override
    public void onStart() {
        super.onStart();
        GraphicInterface graphicInterface = targetObject.targetScene.getInterface(GraphicInterface.class).get(0);
        graphicInterface.addCamera(this);
        manager = targetObject.targetScene.targetEngineInstance.getInputManager();
    }

    private float previous = 0;
    @Override
    public void onPreUpdate() {

        EngineInstance instance = targetObject.targetScene.targetEngineInstance;
        float as = ((float) instance.getWidth() )/ (float) instance.getHeight();
        projectMatrix.perspective(fieldOfView,as,minDistance,maxDistance);
        viewMatrx.reset();
        targetObject.rotation.matrix4();
        Vector3 position   = targetObject.position.copy();
        Vector3 right = targetObject.rotation.getRIGHT();
        Vector3 up = targetObject.rotation.getUP();
        Vector3 forward = targetObject.rotation.getFORWARD();

        Matrix4.buildViewMatrix(viewMatrx.val, position,right,up,forward);

    }
    public Vector3 getScreenMatrix(Vector3 input){
        float[] data = new float[]{input.x,input.y,input.z,1};
        Matrix.multiplyMV(data,0, viewMatrx.val, 0,data,0);
        Matrix.multiplyMV(data,0, projectMatrix.val, 0,data,0);
        Vector3 vc = new Vector3(data[0],data[1],data[2]).div(data[3]);
        vc.add(new Vector3(1f,1f,0));
        vc.mul(new Vector3(0.5f,0.5f,1));
        vc.mul(new Vector3(targetObject.targetScene.targetEngineInstance.getWidth(),
               targetObject.targetScene.targetEngineInstance.getHeight(),1));
        return vc;
    }
    private float x;
    private float z;


    @Override
    public void onStateChange(boolean current) {
        super.onStateChange(current);
        if(current){
            GraphicInterface graphicInterface = targetObject.targetScene.getInterface(GraphicInterface.class).get(0);
            graphicInterface.addCamera(this);
        }else {
            GraphicInterface graphicInterface = targetObject.targetScene.getInterface(GraphicInterface.class).get(0);
            graphicInterface.removeCamera(this);
        }
    }

    @Override
    public Object invoke(int i, Object object) {
        switch (i){
            case 0 :
                onCreate();
                break;
            case 1:
                onInitialize();
                break;
            case 2:
                onStart();
                break;
            case 4:
                onPreUpdate();
                break;
            case 5:
                onUpdate();
                break;
            case 6:
                onPostUpdate();
                break;
            case 7:
                onDisabled();
                break;
            case -100:
                //becomeEditable Element
                break;
        }
        return null;
    }
}
