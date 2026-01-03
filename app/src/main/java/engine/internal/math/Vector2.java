package engine.internal.math;

import androidx.annotation.Nullable;

public class Vector2{

        public float x=0;
        public float y=0;

        public Vector2(){

        }
        public Vector2(float all){
            set(all,all);
        }

        public Vector2(float vx,float vy){
            x = vx;
            y = vy;
        }
        public void set(float vx,float vy){
            x = vx;
            y = vy;
        } public void set(Vector2 v){
            x = v.x;
            y = v.y;
        }
        public float distance(Vector2 next){
            float a = x - next.x;
            float b = y - next.y;
            a *= a;
            b *= b;
            return (float)Math.sqrt(a+b);
        }
        public float getLength(){
            float a = x;
            float b = y;
            a *= a;
            b *= b;
            return (float)Math.sqrt(a+b);
        }
        public Vector2 normalize(){
            return this;
        }

        public static Vector2 ZERO(){
            return new Vector2(0,0);
        }

        public static Vector2 LEFT(){
            return new Vector2(-1,0);
        }
        public static Vector2 RIGHT(){
            return new Vector2(1,0);
        }
        public static Vector2 UP(){
            return new Vector2(0,1);
        }
        public static Vector2 DOWN(){
            return new Vector2(0,-1);
        }

        public Vector2 add(Vector2 b){
            x += b.x;
            y += b.y;
            return this;
        }

        public Vector2 sub(Vector2 b){
            x -= b.x;
            y -= b.y;
            return this;
        }
        public Vector2 mul(Vector2 b){
            x *= b.x;
            y *= b.y;
            return this;
        }
        public Vector2 div(Vector2 b){
            x /= b.x;
            y /= b.y;
            return this;
        }
        public float[] toArray(){
            return new float[]{x,y};
        }

        @Override
        public String toString() {
            return x+":"+y;
        }


    public boolean equals(@Nullable Vector2 obj) {
        return x == obj.x && y == obj.y;
    }
}

