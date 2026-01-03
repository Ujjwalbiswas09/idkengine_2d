package engine.internal.math;

public class Rect {
    public float top =0;
    public float bottom =0;
    public float left =0;
    public float right=0;

    public Rect(float left, float right, float top, float bottom) {
        this.right = right;
        this.left = left;
        this.top = top;
        this.bottom = bottom;
    }
    public Rect(){}

    public boolean isInside(Vector2 vector2){
       if(vector2.x >= left && vector2.x <= right){
           if (vector2.y >= bottom && vector2.y <= top){
               return true;
           }
       }
       return false;
    }
    public Vector2 getCenter(){
        return null;
    }
    public Rect copy(){
        return new Rect(left,right,top,bottom);
    }
    public Vector2 getPosition(){
        return new Vector2((left+right)*0.5f,(top+bottom)*0.5f);
    }
    public Vector2 getSize(){
        return new Vector2((right-left)*0.5f,(top-bottom)*0.5f);
    }
}
