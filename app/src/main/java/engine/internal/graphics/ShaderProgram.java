package engine.internal.graphics;

import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShaderProgram extends GLES20
{
    private int programID;
    private boolean hasError= false;
    private String vertex_code;
    public String fragment_code;
    private HashMap<String,Integer> attribute_location= new HashMap<>();
    private HashMap<String,Integer> uniform_location= new HashMap<>();
    private List<String> attribute_list = new ArrayList<>();
    private List<String> uniform_list = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    private int vertexId;
    private int fragId;
    public ShaderProgram(String vert,String frag){
        vertex_code=vert;
        fragment_code=frag;
        drawType = GL_TRIANGLES;
    }
    public ShaderProgram(InputStream vert,InputStream frag){
        vertex_code=readInput(vert);
        fragment_code=readInput(frag);
        drawType = GL_TRIANGLES;
    }
    private String readInput(InputStream in){ try{
        BufferedReader bis = new BufferedReader(new InputStreamReader(in));
        StringWriter write = new StringWriter();
        char[] arr = new char[1024];
        int i;
        while((i=bis.read(arr)) != -1){
            write.append(new String(arr,0,i));
        }
        return write.toString();
    }catch(Exception e){e.printStackTrace();}
        return "";
    }
    public void compile(){
        programID= glCreateProgram();
        int vertexId = glCreateShader(GL_VERTEX_SHADER);
        int fragId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(vertexId,vertex_code);
        glShaderSource(fragId,fragment_code);
        glCompileShader(vertexId);
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(vertexId,GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            String error = GLES20.glGetShaderInfoLog(vertexId);

            errors.add("Vertex: "+error.trim());
        }
        glCompileShader(fragId);
        int[] compileStatus1 = new int[1];
        GLES20.glGetShaderiv(fragId,GLES20.GL_COMPILE_STATUS, compileStatus1, 0);
        if (compileStatus1[0] == 0) {
            String error = GLES20.glGetShaderInfoLog(fragId);
            errors.add("Fragment: "+error.trim());
        }
        if(!errors.isEmpty()){
            hasError = true;
            glDeleteShader(vertexId);
            glDeleteShader(fragId);
            return;
        }
        glAttachShader(programID,vertexId);
        glAttachShader(programID,fragId);
        glLinkProgram(programID);
        bind();
        int[] numActiveAttribs = new int[1];
        glGetProgramiv(programID, GL_ACTIVE_ATTRIBUTES, numActiveAttribs, 0);
        for (int i = 0; i < numActiveAttribs[0]; i++) {
            int[] size = {1};
            int[] type = {1};
            String attributeName = glGetActiveAttrib(programID, i, size, 0, type, 0);
            attribute_list.add(attributeName);
        }

        int[] numActiveUniforms = new int[1];
        glGetProgramiv(programID, GL_ACTIVE_UNIFORMS, numActiveUniforms, 0);
        for (int i = 0; i < numActiveUniforms[0]; i++) {
            int[] size = new int[1];
            int[] type = new int[1];
            byte[] name = new byte[1024];
            glGetActiveUniform(programID, i, 1024, null, 0, size, 0, type, 0, name, 0);
            String uniformName = new String(name);
            uniform_list.add(uniformName.trim());
        }
        for(String s : attribute_list){
            attribute_location.put(s,glGetAttribLocation(programID,s));
        }
        for(String s : uniform_list){
            uniform_location.put(s,glGetUniformLocation(programID,s));
        }
        glDeleteShader(vertexId);
        glDeleteShader(fragId);
    }

    public void setAttributeFloatBuffer(String id,FloatBuffer buffer,int size){
        Object obj;
        if((obj = attribute_location.get(id)) != null){
            int i = (int) obj;
            //System.out.println(i);
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i,size,GL_FLOAT,false,size*4,buffer);
        }
    }

    public void setAttributeBuffer(String id, VertexBufferObject vbo, int size){
        Object obj;
        if((obj = attribute_location.get(id)) != null){
            int d = (int)obj;
            //System.out.println(d);
            vbo.bind();
            glEnableVertexAttribArray(d);
            glVertexAttribPointer(d,size,vbo.data_type,false,vbo.data_size*size,0);
            vbo.unbind();
        }
    }

    public void setAttributeIntBuffer(String id,IntBuffer buffer,int size){
        Object obj;
        if((obj = attribute_location.get(id)) != null){
            int i = (int) obj;
            //System.out.println(i);
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i,size,GL_UNSIGNED_INT,false,size*4,buffer);
        }
    }
    public void setDrawType(int i){
        drawType = i;
    }
    public void setDrawType(DrawType type){
        if(type == DrawType.TRIANGLE){
            setDrawType(GL_TRIANGLES);
        }else if(type == DrawType.LINE){
            setDrawType(GL_LINES);
        }else if(type == DrawType.POINT){
            setDrawType(GL_POINTS);
        }else if(type == DrawType.LINE_STRIP){
            setDrawType(GL_LINE_STRIP);
        }
    }
    private int drawType=0;
    public void draw(IntBuffer buffer){
        buffer.position(0);
        glDrawElements(drawType,buffer.remaining(),GL_UNSIGNED_INT,buffer);
    }

    public void draw(IntBuffer buffer,int size){
        glDrawElements(drawType,size,GL_UNSIGNED_INT,buffer);
    }


    public void draw(ShortBuffer buffer){
        glDrawElements(drawType,buffer.limit(),GL_UNSIGNED_SHORT,buffer);
    }

    public void draw(VertexBufferObject buffer){
        buffer.bind();
        glDrawElements(drawType,buffer.buffer_size,buffer.data_type,0);
        VertexBufferObject.unbind(GL_ELEMENT_ARRAY_BUFFER);
    }

    public int getUniformId(String s){
        return glGetUniformLocation(programID,s);
    }

    public void setMatrix(String id,float[] val){
        Object obj;
        if((obj = uniform_location.get(id)) != null){
            int i = (int) obj;
            glUniformMatrix4fv(i,1,false,val,0);
        }else{
            int i = glGetUniformLocation(programID,id);
            if(i != -1){
                uniform_location.put(id,i);
                glUniformMatrix4fv(i,1,false,val,0);
            }
        }
    }
    public void setMatrixArray(String id,List<float[]> val){
        for(int i = 0; i<val.size();i++){
            int handle = glGetUniformLocation(programID,id+"["+i+"]");
            glUniformMatrix4fv(handle,1,false,val.get(i),0);
        }}

    public void setInt(String id,int val){
        Object obj;
        if((obj = uniform_location.get(id)) != null){
            int i = (int) obj;
            glUniform1i(i,val);
        }else {

        }
    }
    public void setFloat(String id,float val){
        Object obj;
        if((obj = uniform_location.get(id)) != null){
            int i = (int) obj;
            glUniform1f(i,val);
        }
    }
    public void setVector3(String id,float[] val){
        Object obj;
        if((obj = uniform_location.get(id)) != null){
            int i = (int) obj;
            glUniform3fv(i,1,val,0);
        }
    }

    public void setVector4(String id,float[] val){
        Object obj;
        if((obj = uniform_location.get(id)) != null){
            int i = (int) obj;
            glUniform4fv(i,1,val,0);
        }
    }

    public void setVector2(String id,float[] val){
        Object obj;
        if((obj = uniform_location.get(id)) != null){
            int i = (int) obj;
            glUniform2fv(i,1,val,0);
        }
    }
    public void bind(){
        glUseProgram(programID);
    }
    public void unbind(){
        glUseProgram(0);
    }

    public void printError(){
        if(hasError){
            for(String e : errors)
                System.out.println(e);
        }
    }
    public void close(){
        glDeleteShader(vertexId);
        glDeleteShader(fragId);
        glDeleteProgram(programID);
    }
    public void setLineSize(float size){
        glLineWidth(size);
    }

    public List<String> getUniformList() {
        List<String> list = new ArrayList<>(uniform_list);
        return list;
    }
    public void draw(int count){
        glDrawArrays(drawType,0,count);
    }
    public boolean isHasError() {
        return hasError;
    }

    public List<String> getAttributeList() {
        return new ArrayList<String>(attribute_list);
    }

    public enum DrawType{
        TRIANGLE,LINE,POINT,LINE_STRIP
    }

}
