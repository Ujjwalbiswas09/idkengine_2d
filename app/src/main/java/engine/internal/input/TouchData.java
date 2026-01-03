package engine.internal.input;

import engine.internal.math.Vector2;

public class TouchData {
    public int id;
    public Vector2 current = new Vector2(-1);
    public Vector2 previous = new Vector2(-1);
    public long timestamp;
    public TouchData copy(){
        TouchData touchData = new TouchData();
        touchData.id = id;
        touchData.previous.set(previous);
        touchData.current.set(current);
        touchData.timestamp = timestamp;
        return touchData;
    }
}
