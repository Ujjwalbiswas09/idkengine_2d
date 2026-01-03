package engine.core;

import engine.framework.EngineInstance;
import engine.internal.math.Matrix4;

public class Stage {
    private EngineInstance engineInstance;
    public Matrix4 view = new Matrix4();
    public Matrix4 projection  = new Matrix4();
    public Stage(EngineInstance instance){
        engineInstance = instance;
    }
    public void update(){
        projection.reset();
        projection.ortho(0,engineInstance.getWidth(),0,engineInstance.getHeight(),-10,10);
        view.reset();
    }
}
