package engine.internal.util;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import engine.internal.graphics.VertexBufferObject;

public class Mesh {

    public FloatBuffer positionBuffer;
    public FloatBuffer texcoordBuffer;
    public FloatBuffer normalBuffer;
    public ShortBuffer indice16;

    public Mesh(){

    }
    public void loadMesh(InputStream ins)throws Exception{
        Obj obj = ObjReader.read(ins);
        obj = ObjUtils.triangulate(obj);
        obj = ObjUtils.convertToRenderable(obj);
        positionBuffer = ObjData.getVertices(obj);
        int[] data = ObjData.getFaceVertexIndicesArray(obj);
        short[] tm = new short[data.length];
        for (int i=0;i < data.length;i++){
            tm[i] =(short) data[i];
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(tm.length * 2);
        buffer.order(ByteOrder.nativeOrder());
        buffer.position(0);
        indice16 = buffer.asShortBuffer();
        indice16.put(tm);
        indice16.position(0);

        texcoordBuffer = ObjData.getTexCoords(obj,2);
        normalBuffer = ObjData.getNormals(obj);

    }
    public VertexBufferObject createPositionVBO(){
        if(positionBuffer == null){
            return null;
        }
        VertexBufferObject vbo = VertexBufferObject.createVertexBuffer(false);
        vbo.setData(positionBuffer);
        vbo.unbind();
        positionBuffer = null;
        return vbo;
    }
    public VertexBufferObject createTexcoordVBO(){
        if(texcoordBuffer == null){
            return null;
        }
        VertexBufferObject vbo = VertexBufferObject.createVertexBuffer(false);
        vbo.setData(texcoordBuffer);
        vbo.unbind();
        texcoordBuffer = null;
        return vbo;
    }
    public VertexBufferObject createNormalVBO(){
        if(normalBuffer == null){
            return null;
        }
        VertexBufferObject vbo = VertexBufferObject.createVertexBuffer(false);
        vbo.setData(normalBuffer);
        vbo.unbind();
        normalBuffer = null;
        return vbo;
    }
    public VertexBufferObject createIndiceVBO(){
        if(indice16 == null){
            return null;
        }
        VertexBufferObject vbo = VertexBufferObject.createIndexBuffer(false);
        vbo.setData(indice16);
        vbo.unbind();
        indice16 = null;
        return vbo;
    }
    public void dispose(){
        if(positionBuffer != null){
            positionBuffer.clear();
        }
        if(texcoordBuffer != null){
            texcoordBuffer.clear();
        }
        if(normalBuffer != null){
            normalBuffer.clear();
        }
        if(indice16 != null){
            indice16.clear();
        }

        positionBuffer = null;
        texcoordBuffer = null;
        normalBuffer = null;
        indice16 = null;

    }
}
