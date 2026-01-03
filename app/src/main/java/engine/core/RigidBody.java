package engine.core;

import engine.framework.Flags;
import engine.framework.ObjectModifier;

public class RigidBody extends CollisionBody {
    @Flags(flag = {"VISIBLE","STORE"})
    private float mass=1f;
    @Flags(flag ="VISIBLE")
    private CollisionShape.Type type = CollisionShape.Type.BOX;
    @Flags(flag ="VISIBLE")
    private CollisionShape shape = new BoxCollisionShape();
    @Override
    public Object invoke(int i, Object object) {
        switch (i){
            case 0 :
                onCreate();
                break;
            case 1:
                onInitialize();
                break;
            case 2:
                onStart();
                break;
            case 4:
                onPreUpdate();
                break;
            case 5:
                onUpdate();
                break;
            case 6:
                onPostUpdate();
                break;
            case 7:
                onDisabled();
                break;
            case -100:
                //becomeEditable Element
                break;
        }
        return null;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public CollisionShape getShape() {
        return shape;
    }

    public void setShape(CollisionShape shape) {
        this.shape = shape;
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

    @Override
    public void onCreate() {
        PhysicsInterface physicsInterface = targetObject.targetScene.getInterface(PhysicsInterface.class).get(0);
        physicsInterface.invoke(566,this);
        physicsInterface.invoke(573,this);
    }

    @Override
    public void onStateChange(boolean current) {
        PhysicsInterface physicsInterface = targetObject.targetScene.getInterface(PhysicsInterface.class).get(0);
        if(current){
            physicsInterface.invoke(566,this);
        }else {
            physicsInterface.invoke(567,this);
        }
    }

    @Override
    public void onRemoved() {
        PhysicsInterface physicsInterface = targetObject.targetScene.getInterface(PhysicsInterface.class).get(0);
        super.onRemoved();
        physicsInterface.invoke(567,this);
    }
}

