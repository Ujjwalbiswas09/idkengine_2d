package engine.core;

import engine.framework.Flags;
import engine.framework.ObjectModifier;

public class StaticBody extends CollisionBody {
    @Flags(flag = {"VISIBLE","STORE"})
    private CollisionShape.Type type = CollisionShape.Type.BOX;
    @Flags(flag = {"VISIBLE","STORE"})
    private CollisionShape shape = new BoxCollisionShape();

    public void setShape(CollisionShape shape) {
        this.shape = shape;
    }

    public CollisionShape getShape() {
        return shape;
    }


    @Override
    public void onCreate() {
        PhysicsInterface physicsInterface = targetObject.targetScene.getInterface(PhysicsInterface.class).get(0);
        physicsInterface.invoke(568,this);
        physicsInterface.invoke(573,this);
        //physicsInterface.invoke(569,this);
    }

    @Override
    public void onStateChange(boolean current) {
        PhysicsInterface physicsInterface = targetObject.targetScene.getInterface(PhysicsInterface.class).get(0);
        if(current){
            physicsInterface.invoke(568,this);
        }else {
            physicsInterface.invoke(569,this);
        }
    }

    @Override
    public void onRemoved() {
        PhysicsInterface physicsInterface = targetObject.targetScene.getInterface(PhysicsInterface.class).get(0);
        super.onRemoved();
        physicsInterface.invoke(569,this);
    }

    @Override
    public void onDataReceived(String key, Object data) {

        if(key.equals("type")) {
            type = (CollisionShape.Type) data;
            switch (type) {
                case BOX:
                    shape = new BoxCollisionShape();
                    break;
                case MESH:
                    shape = new MeshCollisionShape();
                    break;
                case SPHERE:
                    shape = new SphereCollisionShape();
                    break;
            }
        }

    }

}
