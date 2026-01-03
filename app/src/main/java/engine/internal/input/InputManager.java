package engine.internal.input;

public abstract class InputManager {

    public int getTouchCount(){
        return 0;
    }
    public TouchData getTouchData(int i){
        return null;
    }
    public void addTouchListener(TouchListener listener){

    }
    public void removeTouchListener(TouchListener listener){

    }
}
