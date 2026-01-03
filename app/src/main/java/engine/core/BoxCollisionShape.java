package engine.core;

import engine.internal.math.Vector3;

public class BoxCollisionShape extends CollisionShape{
    public boolean followTargetObject = true;
    public Vector3 dimensions = new Vector3(1,1,1);
}
