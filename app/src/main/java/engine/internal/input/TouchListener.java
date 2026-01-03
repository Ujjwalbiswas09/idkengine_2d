package engine.internal.input;

public interface TouchListener {
    void onPressed(TouchData data);
    void onReleased(TouchData data);
    void onDragged(TouchData data);
}
