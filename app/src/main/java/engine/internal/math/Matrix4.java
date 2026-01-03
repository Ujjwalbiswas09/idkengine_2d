package engine.internal.math;

import static android.opengl.Matrix.*;

import android.opengl.Matrix;

public class Matrix4 {
    public final float[] val = new float[16];

    public Matrix4() {
        reset();
    }

    public Matrix4(float[] data) {
        set(data);
    }

    public void set(float[] values) {
        if (values.length == 16) {
            for (int i = 0; i < 16; i++) {
                val[i] = values[i];
            }
        }
    }

    public void setLookAt(Vector3 position, Vector3 up, Vector3 forward) {
        setLookAtM(val, 0, forward.x,
                forward.y, forward.z, position.x, position.y,
                position.z, up.x, up.y, up.z);
    }

    public void multiply(Matrix4 mat2) {
        multiplyMM(val, 0, val, 0, mat2.val, 0);
    }

    public void translate(float x, float y, float z) {
        translateM(val, 0, x, y, z);
    }

    public void translate(Vector3 position) {
        translateM(val, 0, position.x, position.y, position.z);
    }

    public void scale(Vector3 scale) {
        scaleM(val, 0, scale.x, scale.y, scale.z);
    }

    public void rotation(Quaternion rotation) {
        multiplyMM(val, 0, val, 0, rotation.matrix4().val, 0);
    }

    public void set(Vector3 position, Quaternion rotation, Vector3 scale) {
        translate(position);
        rotation(rotation);
        scale(scale);
    }

    public void scale(float x, float y, float z) {
        scaleM(val, 0, x, y, z);
    }

    public void rotate(float w, float x, float y, float z) {
        quaternionToMatrix(val, x, y, z, w);
    }

    public void setRotateEuler(float x, float y, float z) {
        setRotateEulerM(val, 0, x, y, z);
    }

    public void inverse() {
        invertM(val, 0, val, 0);
    }

    public void transpose() {
        transposeM(val, 0, val, 0);
    }

    public void multiply(Vector4 position) {
        float[] result = new float[4];
        multiplyMV(result, 0, val, 0, position.toArray(), 0);
        position.set(result[0], result[1], result[2], result[3]);
    }

    public void perspective(float fov, float aspect, float min, float max) {
        perspectiveM(val, 0, fov, aspect, min, max);
    }


    public void ortho(float left, float right,
                                float bottom, float top,
                                float near, float far) {
        reset();
        val[0]  =  2.0f / (right - left);
        val[5]  =  2.0f / (top - bottom);
        val[10] = -2.0f / (far - near);
        val[12] = -(right + left) / (right - left);
        val[13] = -(top + bottom) / (top - bottom);
        val[14] = -(far + near) / (far - near);
        val[15] =  1.0f;

    }

    public void multiply(Vector3 input) {
        float[] out = new float[4];
        multiplyMV(out, 0, val, 0, new float[]{input.x, input.y, input.z, 1}, 0);
        input.setValue(out, 0);
        input.div(out[3]);
    }

    public void reset() {
        setIdentityM(val, 0);
    }

    public static void buildViewMatrix(
            float[] viewMatrix,
            Vector3 position,
            Vector3 right,
            Vector3 up,
            Vector3 forward) {
        viewMatrix[0] = right.x;
        viewMatrix[1] = up.x;
        viewMatrix[2] = -forward.x; // OpenGL: camera looks down -Z
        viewMatrix[3] = 0f;

        viewMatrix[4] = right.y;
        viewMatrix[5] = up.y;
        viewMatrix[6] = -forward.y;
        viewMatrix[7] = 0f;

        viewMatrix[8] = right.z;
        viewMatrix[9] = up.z;
        viewMatrix[10] = -forward.z;
        viewMatrix[11] = 0f;

        // Translation part: -dot(axis, position)
        viewMatrix[12] = -(right.x * position.x + right.y * position.y + right.z * position.z);
        viewMatrix[13] = -(up.x * position.x + up.y * position.y + up.z * position.z);
        viewMatrix[14] = (forward.x * position.x + forward.y * position.y + forward.z * position.z);
        viewMatrix[15] = 1f;
    }

    private void quaternionToMatrix(float[] matrix,float x, float y, float z, float w) {


        // Normalize the quaternion if it's not already
        float lengthSq = x * x + y * y + z * z + w * w;
        if (lengthSq != 1.0f) {
            float invLength = 1.0f / (float) Math.sqrt(lengthSq);
            x *= invLength;
            y *= invLength;
            z *= invLength;
            w *= invLength;
        }

        // Pre-calculate squares and products
        float x2 = x * x;
        float y2 = y * y;
        float z2 = z * z;
        float xy = x * y;
        float xz = x * z;
        float yz = y * z;
        float wx = w * x;
        float wy = w * y;
        float wz = w * z;

        // First column
        matrix[0] = 1.0f - 2.0f * (y2 + z2);
        matrix[1] = 2.0f * (xy + wz);
        matrix[2] = 2.0f * (xz - wy);
        matrix[3] = 0.0f;

        // Second column
        matrix[4] = 2.0f * (xy - wz);
        matrix[5] = 1.0f - 2.0f * (x2 + z2);
        matrix[6] = 2.0f * (yz + wx);
        matrix[7] = 0.0f;

        // Third column
        matrix[8] = 2.0f * (xz + wy);
        matrix[9] = 2.0f * (yz - wx);
        matrix[10] = 1.0f - 2.0f * (x2 + y2);
        matrix[11] = 0.0f;

        // Fourth column
        matrix[12] = 0.0f;
        matrix[13] = 0.0f;
        matrix[14] = 0.0f;
        matrix[15] = 1.0f;

    }
}