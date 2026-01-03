package engine.internal.math;

public class Vector4 {
    public float x;
    public float y;
    public float z;
    public float w;
    public Vector4(){

    }
    public Vector4(float x,float y,float z,float w){
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void set(float x,float y,float z,float w){
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return x+","+y+","+x+","+w;
    }

    public float[] toArray(){
        return new float[]{x,y,z,w};
    }
}
