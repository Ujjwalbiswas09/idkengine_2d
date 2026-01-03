package engine.internal.math;

import androidx.annotation.NonNull;

public class Vector3 {
    public float x;
    public float y;
    public float z;

    public Vector3() {

    }

    public Vector3(float all) {
        set(all, all, all);
    }

    public Vector3(float vx, float vy, float vz) {
        x = vx;
        y = vy;
        z = vz;
    }

    public void set(float vx, float vy, float vz) {
        x = vx;
        y = vy;
        z = vz;
    }

    public void set(Vector3 v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public float distance(Vector3 next) {
        float a = x - next.x;
        float b = y - next.y;
        float c = z - next.z;
        a *= a;
        b *= b;
        c *= c;
        return (float) Math.sqrt(a + b + c);
    }

    public float getDotProduct(Vector3 b) {
        return x * b.x + y * b.y + z * b.z;
    }

    public static Vector3 ZERO() {
        return new Vector3(0, 0, 0);
    }

    public static Vector3 LEFT() {
        return new Vector3(-1, 0, 0);
    }

    public static Vector3 RIGHT() {
        return new Vector3(1, 0, 0);
    }

    public static Vector3 UP() {
        return new Vector3(0, 1, 0);
    }

    public static Vector3 DOWN() {
        return new Vector3(0, -1, 0);
    }

    public static Vector3 FORWARD() {
        return new Vector3(0, 0, 1);
    }

    public static Vector3 BACKWARD() {
        return new Vector3(0, 0, -1);
    }

    public Vector3 add(Vector3 b) {
        x += b.x;
        y += b.y;
        z += b.z;
        return this;
    }

    public Vector3 sub(Vector3 b) {
        x -= b.x;
        y -= b.y;
        z -= b.z;
        return this;
    }

    public Vector3 mul(Vector3 b) {
        x *= b.x;
        y *= b.y;
        z *= b.z;
        return this;
    }

    public Vector3 mul(float b) {
        x *= b;
        y *= b;
        z *= b;
        return this;
    }

    public Vector3 div(float b) {
        x /= b;
        y /= b;
        z /= b;
        return this;
    }

    public Vector3 add(float b) {
        x += b;
        y += b;
        z += b;
        return this;
    }

    public Vector3 sub(float b) {
        x -= b;
        y -= b;
        z -= b;
        return this;
    }

    public Vector3 div(Vector3 b) {
        x /= b.x;
        y /= b.y;
        z /= b.z;
        return this;
    }

    public Vector3 crossProduct(Vector3 b) {
        return new Vector3(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y * b.x);
    }

    public float[] getValues() {
        return new float[]{x, y, z};
    }

    public void setValue(float[] array, int st) {
        x = array[st];
        y = array[st + 1];
        z = array[st + 2];
    }

    public double getSqrMagnitude() {
        return x * x + z * z + y * y;
    }

    public double getMagnitude() {
        return Math.sqrt(x * x + z * z + y * y);
    }

    public Vector3 normalized() {
        if (getSqrMagnitude() != 1) {
            float magnitude = (float) getMagnitude();
            x /= magnitude;
            y /= magnitude;
            z /= magnitude;
        }
        return this;
    }

    public Vector3 copy() {
        return new Vector3(x, y, z);
    }

    @NonNull
    @Override
    public String toString() {
        return x+", "+y+", "+z;
    }
}