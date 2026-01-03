package engine.internal.math;

import android.opengl.Matrix;

import androidx.annotation.NonNull;

public class Quaternion extends Vector4{

    private Matrix4 matrix4 = new Matrix4();
    public Quaternion(float w,float x,float y,float z){
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Quaternion(){
        w = 1;
    }
    
    public void set(Quaternion other){
        this.w =other.w;
        this.x =other.x;
        this.y =other.y;
        this.z =other.z;
    }
    public Matrix4 matrix4(){
        matrix4.reset();
        matrix4.rotate(w,x,y,z);
        return matrix4;
    }
    public Vector3 getUP(){
        float[] in = {0,1,0,0};
        float[] out = new float[4];
        Matrix.multiplyMV(out,0,matrix4.val,0,in,0);
        return new Vector3(out[0],out[1],out[2]).normalized();
    }
    public Vector3 getFORWARD(){
        float[] in = {0,0,1,0};
        float[] out = new float[4];
        Matrix.multiplyMV(out,0,matrix4.val,0,in,0);
        return new Vector3(out[0],out[1],out[2]).normalized();
    }
    public Vector3 getRIGHT(){
        float[] in = {1,0,0,0};
        float[] out = new float[4];
        Matrix.multiplyMV(out,0,matrix4.val,0,in,0);
        return new Vector3(out[0],out[1],out[2]).normalized();
    }

    public void setEulerRotation(float roll, float pitch, float yaw) {
        // Convert degrees to radians if needed
        float cy = (float)Math.cos(yaw * 0.5f);
        float sy = (float)Math.sin(yaw * 0.5f);
        float cp = (float)Math.cos(pitch * 0.5f);
        float sp = (float)Math.sin(pitch * 0.5f);
        float cr = (float)Math.cos(roll * 0.5f);
        float sr = (float)Math.sin(roll * 0.5f);

        w = cr * cp * cy + sr * sp * sy;
        x = sr * cp * cy - cr * sp * sy;
        y = cr * sp * cy + sr * cp * sy;
        z = cr * cp * sy - sr * sp * cy;
    }

    public void setEulerRotationRadian(float roll, float pitch, float yaw) {
       setEulerRotation((float)Math.toRadians(roll),
               (float)Math.toRadians(pitch),
               (float)Math.toRadians(yaw));
    }


    public void eulerToQuaternion(double rollDegrees, double pitchDegrees, double yawDegrees) {
        // Convert degrees to radians
        double rollRad = Math.toRadians(rollDegrees);
        double pitchRad = Math.toRadians(pitchDegrees);
        double yawRad = Math.toRadians(yawDegrees);

        double cy = Math.cos(yawRad * 0.5);
        double sy = Math.sin(yawRad * 0.5);
        double cp = Math.cos(pitchRad * 0.5);
        double sp = Math.sin(pitchRad * 0.5);
        double cr = Math.cos(rollRad * 0.5);
        double sr = Math.sin(rollRad * 0.5);

       w =(float)(cr * cp * cy + sr * sp * sy);
       x =(float)(sr * cp * cy - cr * sp * sy);
       y =(float)(cr * sp * cy + sr * cp * sy);
       z =(float)(cr * cp * sy - sr * sp * cy);

    }

    public Quaternion fromBasis(Vector3 forward, Vector3 up) {
        // Normalize forward
        Vector3 f = forward.normalized();

        // Build right = forward x up
        Vector3 r = f.crossProduct(up).normalized();

        // Recompute up = right x forward
        Vector3 u = r.crossProduct(f);

        // Rotation matrix (column-major)
        float m00 = r.x, m01 = u.x, m02 = -f.x;
        float m10 = r.y, m11 = u.y, m12 = -f.y;
        float m20 = r.z, m21 = u.z, m22 = -f.z;

        // Convert rotation matrix to quaternion
        float trace = m00 + m11 + m22;

        if (trace > 0) {
            float s = (float) Math.sqrt(trace + 1.0) * 2;
            w = 0.25f * s;
            x = (m21 - m12) / s;
            y = (m02 - m20) / s;
            z = (m10 - m01) / s;
        } else if ((m00 > m11) && (m00 > m22)) {
            float s = (float) Math.sqrt(1.0 + m00 - m11 - m22) * 2;
            w = (m21 - m12) / s;
            x = 0.25f * s;
            y = (m01 + m10) / s;
            z = (m02 + m20) / s;
        } else if (m11 > m22) {
            float s = (float) Math.sqrt(1.0 + m11 - m00 - m22) * 2;
            w = (m02 - m20) / s;
            x = (m01 + m10) / s;
            y = 0.25f * s;
            z = (m12 + m21) / s;
        } else {
            float s = (float) Math.sqrt(1.0 + m22 - m00 - m11) * 2;
            w = (m10 - m01) / s;
            x = (m02 + m20) / s;
            y = (m12 + m21) / s;
            z = 0.25f * s;
        }
        return this;
    }

        @NonNull
    @Override
    public String toString() {
        return x+","+y+","+z+","+w;
    }
}
