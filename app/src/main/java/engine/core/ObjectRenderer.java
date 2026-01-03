package engine.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import engine.framework.Flags;
import engine.framework.ObjectModifier;
import engine.framework.EngineQuery;
import engine.internal.graphics.Texture;
import engine.internal.graphics.VertexBufferObject;
import engine.internal.math.Color;
import engine.internal.math.Matrix4;
import engine.internal.util.Mesh;

public class ObjectRenderer extends ObjectModifier{

    @EngineQuery(name = "file",value = "mesh")
    @Flags(flag = {"VISIBLE","STORE"})
    private String defaultFile = "";

    @EngineQuery(name = "file",value = "texture")
    @Flags(flag = {"VISIBLE","STORE"})
    private String defaultTexture = "";

    @Flags(flag = {"INVISIBLE","IGNORE"})
    public VertexBufferObject position;
    @Flags(flag = {"INVISIBLE","IGNORE"})
    public VertexBufferObject tex;
    @Flags(flag = {"INVISIBLE","IGNORE"})
    public VertexBufferObject indice;
    @Flags(flag = {"INVISIBLE","IGNORE"})
    public Texture texture;
    public Color color = new Color(1,1,1,1);
    public ObjectRenderer(){

    }
    public ObjectRenderer(String model,String texture){
        defaultFile = model;
        defaultTexture = texture;
    }
    @Override
    public void onDataReceived(String key, Object alue) {
        if(key .equals("defaultTexture")){
            defaultTexture = (String) alue;
            try {
                texture.setData(targetObject.targetScene.targetEngineInstance.openAssetFile(defaultTexture));
            }catch (Exception e){

            }
        }
        if(key.equals("defaultFile")){
            defaultFile = (String) alue;
            try{
                if(defaultFile == null || defaultFile.isBlank()){
                    return;
                }
                InputStream ins = targetObject.targetScene.targetEngineInstance.openAssetFile(defaultFile);
                if(ins == null){
                    return;
                }
                Obj obj = ObjReader.read(ins);
                obj = ObjUtils.triangulate(obj);
                obj = ObjUtils.convertToRenderable(obj);
                FloatBuffer positionB = ObjData.getVertices(obj);
                int[] data = ObjData.getFaceVertexIndicesArray(obj);
                short[] tm = new short[data.length];
                for (int i=0;i < data.length;i++){
                    tm[i] =(short) data[i];
                }

                position.setData(positionB);
                tex.setData(ObjData.getTexCoords(obj,2));
                indice.setData(tm);
                indice.unbind();
            }catch (Exception e){

            }
        }
    }

    @Override
    public void onInitialize() {
        try {
            position = VertexBufferObject.createVertexBuffer(false);
            tex = VertexBufferObject.createVertexBuffer(false);
            indice = VertexBufferObject.createIndexBuffer(false);
            texture = new Texture();

            if(defaultFile == null || defaultFile.isBlank()){
                return;
            }
            InputStream ins = targetObject.targetScene.targetEngineInstance.openAssetFile(defaultFile);
            if(ins == null){
                return;
            }
            Mesh mesh = new Mesh();
            mesh.loadMesh(ins);
            position.setData(mesh.positionBuffer);
            tex.setData(mesh.texcoordBuffer);
            indice.setData(mesh.indice16);
            indice.unbind();
            mesh.dispose();
            if(defaultTexture == null || defaultTexture.isBlank() ){

            }else {
                ins = targetObject.targetScene.targetEngineInstance.openAssetFile(defaultTexture);
                if(ins !=null) {
                    texture.setData(ins);
                }
            }
            GraphicInterface graphicInterface = targetObject.targetScene.getInterface(GraphicInterface.class).get(0);
            graphicInterface.addRenderer(this);

        } catch (Exception e) {
            e.printStackTrace();
           /// throw new RuntimeException(e);
        }
    }

    @Override
    public void onStateChange(boolean current) {
        super.onStateChange(current);
        GraphicInterface graphicInterface = targetObject.targetScene.getInterface(GraphicInterface.class).get(0);
        if(current){
            graphicInterface.addRenderer(this);
        }else {
            graphicInterface.removeRenderer(this);
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

    @Override
    public void onRemoved() {
        GraphicInterface graphicInterface = targetObject.targetScene.getInterface(GraphicInterface.class).get(0);
        graphicInterface.removeRenderer(this);
    }


}
