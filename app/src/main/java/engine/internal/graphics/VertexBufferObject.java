package engine.internal.graphics;

import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glGenBuffers;

import static android.opengl.GLES20.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class VertexBufferObject
{
    private int type;
    public int data_size=4;
    public int data_type;
    public int buffer_size;
    private final int  buffer_type;
    private VertexBufferObject(int i,boolean dynamic){
        int[] tmp = {0};
        buffer_type = i;
        glGenBuffers(1,tmp,0);
        buffer_id = tmp[0];
        type = GL_STATIC_DRAW;
        if(dynamic){
            type = GL_DYNAMIC_DRAW;
        }
        data_type = GL_FLOAT;
    }


    private final int buffer_id;
    public static VertexBufferObject createIndexBuffer(boolean dynamic){
        return new VertexBufferObject(GL_ELEMENT_ARRAY_BUFFER,dynamic);
    }
    public static VertexBufferObject createVertexBuffer(boolean dynamic){
        return new VertexBufferObject(GL_ARRAY_BUFFER,dynamic);
    }

    public void bind(){
        glBindBuffer(buffer_type,buffer_id);
    }

    public void unbind(){
        glBindBuffer(buffer_type,0);
    }

    public static void unbind(int i){
        glBindBuffer(i,0);
    }

    public void setData(FloatBuffer buffer){
        bind();
        buffer.position(0);
        buffer_size = buffer.remaining();
        glBufferData(buffer_type,buffer.remaining()*4,null,type);
        glBufferSubData(buffer_type,0,buffer_size*4,buffer);
        unbind();
        data_type = GL_FLOAT;
        buffer.clear();
    }
    public void setSubData(FloatBuffer fb){
        bind();
        fb.position(0);
        glBufferSubData(buffer_type,0,fb.remaining()*4,fb);
        unbind();
        fb.clear();
    }
    public void setSubData(IntBuffer fb){
        bind();
        fb.position(0);
        glBufferSubData(buffer_type,0,fb.remaining()*4,fb);
        unbind();
        fb.clear();
    }
    public void setSubData(ShortBuffer fb){
        bind();
        fb.position(0);
        glBufferSubData(buffer_type,0,fb.remaining()*2,fb);
        unbind();
        fb.clear();
    }
    public void setData(ShortBuffer buffer){
        bind();
        buffer.position(0);
        buffer_size = buffer.remaining();
        glBufferData(buffer_type,buffer.remaining()*2,buffer,type);
        //glBufferSubData(buffer_id,0,buffer.remaining()*2,buffer);
        unbind();
        data_size = 2;
        data_type = GL_UNSIGNED_SHORT;
        buffer.clear();
    }
    public void setData(IntBuffer buffer){
        bind();
        buffer.position(0);
        buffer_size = buffer.remaining();
        glBufferData(buffer_type,buffer.remaining()*4,buffer,type);
        unbind();
        data_size = 4;
        data_type=GL_UNSIGNED_INT;
        buffer.clear();
    }
    public void setData(float[] buffer){
        FloatBuffer tmp = create(buffer.length*4).asFloatBuffer().put(buffer);
        //tmp.position(0);
        setData(tmp);
    }
    public void setData(int[] buffer){
        setData(create(buffer.length*4).asIntBuffer().put(buffer));
    }
    public void setData(short[] buffer){
        setData(create(buffer.length*2).asShortBuffer().put(buffer));
    }

    private static ByteBuffer create(int size){
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        buffer.order(ByteOrder.nativeOrder());
        return buffer;
    }

    public void close(){
        glDeleteBuffers(1,new int[]{buffer_id},0);
    }
}
