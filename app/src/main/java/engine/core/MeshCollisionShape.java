package engine.core;

import engine.framework.EngineQuery;

public class MeshCollisionShape extends CollisionShape{
    @EngineQuery(name = "file",value = "mesh")
    public String meshPath= "";
}
