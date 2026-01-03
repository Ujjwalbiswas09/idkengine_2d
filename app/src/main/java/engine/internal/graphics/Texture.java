package engine.internal.graphics;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES30.GL_FIXED;
import static android.opengl.GLES30.GL_LINEAR;
import static android.opengl.GLES30.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES30.GL_LINEAR_MIPMAP_NEAREST;
import static android.opengl.GLES30.GL_MIRRORED_REPEAT;
import static android.opengl.GLES30.GL_NEAREST;
import static android.opengl.GLES30.GL_NEAREST_MIPMAP_LINEAR;
import static android.opengl.GLES30.GL_NEAREST_MIPMAP_NEAREST;
import static android.opengl.GLES30.GL_REPEAT;
import static android.opengl.GLES30.GL_TEXTURE0;
import static android.opengl.GLES30.GL_TEXTURE_2D;
import static android.opengl.GLES30.GL_TEXTURE_3D;
import static android.opengl.GLES30.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES30.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES30.GL_TEXTURE_WRAP_R;
import static android.opengl.GLES30.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES30.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES30.glActiveTexture;
import static android.opengl.GLES30.glBindTexture;
import static android.opengl.GLES30.glDeleteTextures;
import static android.opengl.GLES30.glGenTextures;
import static android.opengl.GLES30.glGenerateMipmap;
import static android.opengl.GLES30.glTexParameterf;
import static android.opengl.GLES30.glTexParameteri;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLUtils;
import android.os.Build;
import android.util.Log;

import java.io.InputStream;

public class Texture
{

    private int id=-1;
    public boolean loadFromPath=true;
    public Filter filter_min = Filter.LINEAR;
    public Filter filter_mag = Filter.LINEAR;
    public Wrap wrap_t = Wrap.REPEAT;
    public Wrap wrap_s = Wrap.REPEAT;
    protected int usage = 0;
    private int width=0;
    private int height=0;

    public Texture(){
        int[] pointer = {0};
        glGenTextures(1,pointer,0);
        id= pointer[0];
        Bitmap map;
        map =Bitmap.createBitmap(10,10,Bitmap.Config.RGB_565);
        map.eraseColor(Color.WHITE);
        setData(map);
    }

    public Texture(Bitmap bitmap) {
        int[] pointer = {0};
        glGenTextures(1,pointer,0);
        id= pointer[0];
        setData(bitmap);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Texture(float r, float g, float b){
        int[] pointer = {0};
        glGenTextures(1,pointer,0);
        id= pointer[0];
        Bitmap map;
        map =Bitmap.createBitmap(10,10,Bitmap.Config.RGB_565);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            map.eraseColor(Color.rgb(r,g,b));
        }else {
            map.eraseColor(0);
        }
        setData(map);
    }

    public Texture(byte[] data){
        int[] pointer = {0};
        glGenTextures(1,pointer,0);
        id= pointer[0];
        Bitmap map =BitmapFactory.decodeByteArray(data,0,data.length);
        setData(map);
    }

    public Texture(InputStream data){
        int[] pointer = {0};
        glGenTextures(1,pointer,0);
        id= pointer[0];
        Bitmap map =BitmapFactory.decodeStream(data);
        setData(map);
    }

    public void setData(InputStream str){
        Bitmap map =BitmapFactory.decodeStream(str);
        setData(map);
    }


    public void setTexture(Texture t1){
        if(id != -1){
            close();
        }
        id = t1.id;
        wrap_s = t1.wrap_s;
        wrap_t = t1.wrap_t;
        filter_min = t1.filter_min;
        filter_mag = t1.filter_mag;
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D,id);
    }
    public static void unbind(){
        glBindTexture(GL_TEXTURE_2D,0);
    }
    public void active(int i){
        glActiveTexture(GL_TEXTURE0+i);
        glBindTexture(GL_TEXTURE_2D,id);
    }
    public void close(){
        glDeleteTextures(1,new int[]{id},0);
    }
    public void setData(Bitmap map){
        bind();
        glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,getFilterInt(filter_min));
        glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,getFilterInt(filter_mag));
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,getWrapInt(wrap_s));
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,getWrapInt(wrap_t));
        GLUtils.texImage2D(GL_TEXTURE_2D,0,map,0);
        //glGenerateMipmap(GL_TEXTURE_2D);
        height = map.getHeight();
        width = map.getWidth();
        System.out.println("Loaded Texture "+width+"x"+height+" in "+id);
        unbind();
        map.recycle();

    }
    public void setFilter(Filter mn,Filter mg){
        filter_min = mn;
        filter_mag = mg;
        bind();
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,getFilterInt(mg));
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,getFilterInt(mn));
        unbind();
    }
    public static int getFilterInt(Filter gl){
        if(gl == Filter.LINEAR){
            return GL_LINEAR;
        }
        if(gl == Filter.NEAREST){
            return GL_NEAREST;
        }
        if(gl == Filter.LINEAR_MIPMAP_LINEAR){
            return GL_LINEAR_MIPMAP_LINEAR;
        }
        if(gl == Filter.LINEAR_MIPMAP_NEAREST){
            return GL_LINEAR_MIPMAP_NEAREST;
        }
        if(gl == Filter.NEAREST_MIPMAP_LINEAR){
            return GL_NEAREST_MIPMAP_LINEAR;
        }
        if(gl == Filter.NEAREST_MIPMAP_NEAREST){
            return GL_NEAREST_MIPMAP_NEAREST;
        }
        return 0;
    }

    public static int getWrapInt(Wrap vl){
        if(vl == Wrap.REPEAT){
            return GL_REPEAT;
        }
        if(vl == Wrap.MIRROR_REPEAT){
            return GL_MIRRORED_REPEAT;
        }
        if(vl == Wrap.FIXED){
            return GL_CLAMP_TO_EDGE;
        }

        return 0;
    }

    public void setWrap(Wrap s,Wrap t){
        wrap_t=t;
        wrap_s=s;
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,getWrapInt(wrap_t));
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,getWrapInt(wrap_s));
    }

    public Bitmap getData(){
        return null;
    }
    public void delete(){
        if(id != -1) {
            glDeleteTextures(1, new int[]{id}, 0);
        }
    }
    public enum Filter{
        LINEAR_MIPMAP_NEAREST,
        LINEAR_MIPMAP_LINEAR,
        NEAREST_MIPMAP_LINEAR,
        NEAREST_MIPMAP_NEAREST,
        LINEAR,NEAREST
    }
    public enum Wrap{
        FIXED,REPEAT,MIRROR_REPEAT
    }
}
