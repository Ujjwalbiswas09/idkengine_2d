package engine.core;

import static android.opengl.GLES31.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES31.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES31.GL_RGBA;
import static android.opengl.GLES31.GL_UNSIGNED_BYTE;
import static android.opengl.GLES31.glClear;
import static android.opengl.GLES31.glClearColor;
import static android.opengl.GLES31.glReadPixels;
import static android.opengl.GLES31.glViewport;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import engine.framework.GameObject;
import engine.framework.SceneInterface;
import engine.internal.graphics.ShaderProgram;
import engine.internal.graphics.VertexBufferObject;
import engine.internal.input.InputManager;
import engine.internal.input.TouchData;
import engine.internal.input.TouchListener;
import engine.internal.math.Matrix4;
import engine.internal.math.Quaternion;
import engine.internal.math.Vector3;

public class GraphicInterface extends SceneInterface {
    private List<ObjectRenderer> objectRenderers = new ArrayList<>();
    private List<Camera> cameras = new ArrayList<>();
    private SimpleShader simpleShader;
    private LineShaderProgram lineShaderProgram;
    private Stage stage;
    private VertexBufferObject lines;
    private VertexBufferObject lineColor;
    private VertexBufferObject indice;

    private Matrix4 projection=new Matrix4();
    private Matrix4 view= new Matrix4();
    private SpriteShaderProgram spriteShaderProgram;
    private VertexBufferObject spriteVertex;

    @Override
    public void onStart() {
        super.onStart();
        simpleShader = new SimpleShader();
        simpleShader.compile();
        simpleShader.printError();

        stage =  new Stage(targetScene.targetEngineInstance);

        lineShaderProgram = new LineShaderProgram();
        lineShaderProgram.compile();
        lineShaderProgram.printError();
        lineShaderProgram.setDrawType(ShaderProgram.DrawType.LINE);
        lines = VertexBufferObject.createVertexBuffer(false);
        lines.setData(new float[]{
                0,0,0,
                1,0,0,

                0,0,0,
                0,1,0,

                0,0,0,
                0,0,1,
        });


        lineColor = VertexBufferObject.createVertexBuffer(false);
        lineColor.setData(new float[]{
                1,0,0,
                1,0,0,

                0,1,0,
                0,1,0,

                0,0,1,
                0,0,1,
        });


        indice = VertexBufferObject.createIndexBuffer(false);
        indice.setData(new int[]{0,1, 2,3, 4,5 });

        spriteShaderProgram = new SpriteShaderProgram();
        spriteShaderProgram.compile();
        spriteShaderProgram.printError();

        spriteVertex = VertexBufferObject.createVertexBuffer(false);

        spriteVertex.setData(new float[]{
                -1.0f, -1.0f, 0.0f,   // BL
                -1.0f,  1.0f, 0.0f,   // TL
                1.0f,  1.0f, 0.0f,   // TR

                // Triangle 2 (BL → TR → BR)  CCW
                -1.0f, -1.0f, 0.0f,   // BL
                1.0f,  1.0f, 0.0f,   // TR
                1.0f, -1.0f, 0.0f    // BR
        });

    }
    public static float[] reverseFloatArray(float[] arr) {
        if (arr == null || arr.length <= 1) {
            return arr;
        }

        int start = 0;
        int end = arr.length - 1;
        while (start < end) {
            float temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
        return arr;
    }

    public void addCamera(Camera camera){
        cameras.add(camera);
    }
    public void addRenderer(ObjectRenderer renderer){
        objectRenderers.add(renderer);
    }
    public void removeRenderer(ObjectRenderer renderer){
        objectRenderers.remove(renderer);
    }
    public void removeCamera(Camera camera){
        cameras.remove(camera);
    }
    private boolean done;
    @Override
    public void onUpdate() {
        glViewport(0,0,targetScene.targetEngineInstance.getWidth(),targetScene.targetEngineInstance.getHeight());
        for(Camera camera : cameras){
        render(camera);
       }
        //editorUpdate();
    }

    private GameObject selectObject(int x,int y){
        glClearColor(0,0,0,0);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        Matrix4 matrix4 = new Matrix4();
        int i =0;
        for(ObjectRenderer objectRenderer : objectRenderers) {
            i+= 1;
            matrix4.reset();
            matrix4.set(objectRenderer.targetObject.position,objectRenderer.targetObject.rotation,objectRenderer.targetObject.scale);
          editorAction.selectionShader.bind();
          editorAction.selectionShader.setMatrix("projection", editorAction.editorCamera.projectMatrix.val);
          editorAction.selectionShader.setMatrix("view", editorAction.editorCamera.viewMatrx.val);
          editorAction.selectionShader.setMatrix("model", matrix4.val);
          editorAction.selectionShader.setFloat("objectID", i / 255f);
          editorAction.selectionShader.setAttributeBuffer("position", objectRenderer.position, 3);
          editorAction.selectionShader.draw(objectRenderer.indice);
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.order(ByteOrder.nativeOrder());
        buffer.position(0);
        glReadPixels(x,y,1,1,GL_RGBA,GL_UNSIGNED_BYTE,buffer);
        buffer.position(0);
        Bitmap bitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        int id = Color.red(bitmap.getPixel(0,0));
        System.out.println("id "+id);
        if(id > 0){
            id -= 1;
            GameObject go = objectRenderers.get(id).targetObject;
            System.out.println(go.name+" is select");
            return go;
        }
        return null;
    }


    @Override
    public Object invoke(int i, Object object) {
        switch (i){
            case 0 :
                onCreate();
                editorStart();
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
                //onUpdate();
                editorUpdate();
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
    private void render(Camera camera){
        Matrix4 matrix4 = new Matrix4();
        for(ObjectRenderer objectRenderer : objectRenderers) {
            matrix4.reset();
            matrix4.set(objectRenderer.targetObject.position,objectRenderer.targetObject.rotation,objectRenderer.targetObject.scale);
            simpleShader.bind();
            objectRenderer.texture.active(0);
            simpleShader.setInt("uTexture", 0);
            simpleShader.setVector4("color", objectRenderer.color.toArray());

            simpleShader.setMatrix("uProjection", camera.projectMatrix.val);
            simpleShader.setMatrix("uView", camera.viewMatrx.val);
            simpleShader.setMatrix("uModel", matrix4.val);
            simpleShader.setAttributeBuffer("aPosition", objectRenderer.position, 3);
            simpleShader.setAttributeBuffer("aCoord", objectRenderer.tex, 2);
            simpleShader.draw(objectRenderer.indice);
        }
    }
    private void editrender(Camera camera,GameObject selected){
        Matrix4 matrix4 = new Matrix4();
        for(ObjectRenderer objectRenderer : objectRenderers) {
            matrix4.reset();
            matrix4.set(objectRenderer.targetObject.position,objectRenderer.targetObject.rotation,objectRenderer.targetObject.scale);
            simpleShader.bind();
            objectRenderer.texture.active(0);
            simpleShader.setInt("uTexture", 0);
            if(selected == objectRenderer.targetObject) {
                simpleShader.setVector4("color", objectRenderer.color.mix(engine.internal.math.Color.GREEN()).toArray());
            }else {
                simpleShader.setVector4("color", objectRenderer.color.toArray());
            }
            simpleShader.setMatrix("uProjection", camera.projectMatrix.val);
            simpleShader.setMatrix("uView", camera.viewMatrx.val);
            simpleShader.setMatrix("uModel", matrix4.val);
            simpleShader.setAttributeBuffer("aPosition", objectRenderer.position, 3);
            simpleShader.setAttributeBuffer("aCoord", objectRenderer.tex, 2);
            simpleShader.draw(objectRenderer.indice);
        }


    }
    private EditorAction editorAction;
    private void editorStart(){
       editorAction = new EditorAction();
       editorAction.start();
    }
    private void editorUpdate(){

       GameObject targetObject = (GameObject) targetScene.targetEngineInstance.getObject("selectedObject");
        editrender(editorAction.editorCamera,targetObject);
       if(targetObject != null) {
           Matrix4 matrix4 = new Matrix4();
           matrix4.reset();
           matrix4.translate(targetObject.position);
           matrix4.scale(new Vector3(targetObject.position.distance(editorAction.editorCamera.targetObject.position) * 0.3f));
           lineShaderProgram.bind();
           lineShaderProgram.setAttributeBuffer("position", lines, 3);
           lineShaderProgram.setAttributeBuffer("aC", lineColor, 3);
           lineShaderProgram.setMatrix("projectionMatrix", editorAction.editorCamera.projectMatrix.val);
           lineShaderProgram.setMatrix("viewMatrix", editorAction.editorCamera.viewMatrx.val);
           lineShaderProgram.setMatrix("modelMatrix", matrix4.val);
           lineShaderProgram.draw(indice);
           lineShaderProgram.draw(6);

       }
        editorAction.update();
    }
    private class EditorAction implements TouchListener {
        private Camera editorCamera;
        private GameObject fake;
        private InputManager manager;
        private float previous;
        private float z;
        private float x;

        private ObjectSelectionShader selectionShader;

        private void start(){
            editorCamera = new Camera();
            fake = new GameObject();
            editorCamera.targetObject = fake;
            fake.targetScene = targetScene;
            manager = targetScene.targetEngineInstance.getInputManager();
            manager.addTouchListener(this);
            selectionShader = new ObjectSelectionShader(targetScene.targetEngineInstance.getApplicationContext());
            selectionShader.compile();
            selectionShader.printError();
            targetScene.targetEngineInstance.putValue("editorCamera",editorCamera);

        }
        private int distanceFromObject = -500;
        private Quaternion cameraQuat=new Quaternion();
        private void update(){
            editorCamera.onPreUpdate();
            int count = manager.getTouchCount();
            GameObject gameObject = (GameObject) targetScene.targetEngineInstance.getObject("selectedObject");
            if(count==2){

                    float dis = manager.getTouchData(0).current.distance(manager.getTouchData(1).current);
                    if (previous == 0) {
                        previous = dis;
                    }
                    float grow = dis - previous;
                    previous = dis;
                if (gameObject != null){
                    distanceFromObject += grow;
                    //System.out.println(distanceFromObject);
                }else {
                    editorCamera.targetObject.position.add(editorCamera.targetObject.rotation.getFORWARD().mul(grow * 0.01f));
                }


            }else {
                previous = 0;
            }
            if(count == 1){
                TouchData touchData = manager.getTouchData(0);
                x -= (touchData.current.x - touchData.previous.x) * 0.1f;
                z += (touchData.current.y - touchData.previous.y) * 0.1f;
                if(x < -360){
                    x += 360;
                }
                if(x > 360){
                    x -= 360;
                }
                if(z < -360){
                    z += 360;
                }
                if(z > 360){
                    z -= 360;
                }
                if(gameObject != null) {
                    cameraQuat.setEulerRotationRadian(z,-x,0);
                }else {
                    editorCamera.targetObject.rotation.setEulerRotationRadian(z, x, 0);
                }
            }
            if(gameObject != null){
                cameraQuat.matrix4();
                editorCamera.targetObject.position.set(cameraQuat.getFORWARD().mul(-(distanceFromObject*0.01f)).add(gameObject.position));
                editorCamera.targetObject.rotation.matrix4();
                editorCamera.targetObject.rotation.fromBasis(cameraQuat.getFORWARD()
                        ,cameraQuat.getUP());
            }
        }

        @Override
        public void onPressed(TouchData data) {

        }

        @Override
        public void onReleased(TouchData data) {
            if((System.currentTimeMillis() -data.timestamp) < 100){
                GameObject object = selectObject((int)data.current.x,(int)data.current.y);

                if(object != null){
                    System.out.println("Selcted");
                    GameObject object1 = (GameObject) targetScene.targetEngineInstance.getObject("selectedObject");
                    if(object1 == object){
                       targetScene.targetEngineInstance.removeValue("selectedObject");
                    }else {
                        targetScene.targetEngineInstance.putValue("selectedObject",object);
                    }
                }else {
                    targetScene.targetEngineInstance.removeValue("selectedObject");
                }
            }
        }

        @Override
        public void onDragged(TouchData data) {

        }
    }
}
