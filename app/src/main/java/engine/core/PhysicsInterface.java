package engine.core;


import android.app.NativeActivity;
import android.util.ArrayMap;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.EmptyShape;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.jme3.bullet.objects.PhysicsBody;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.NativeLibrary;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import engine.framework.SceneInterface;
import engine.internal.graphics.ShaderProgram;
import engine.internal.graphics.VertexBufferObject;
import engine.internal.math.Color;
import engine.internal.math.Quaternion;
import engine.internal.math.Vector3;
import engine.internal.util.Mesh;
public class PhysicsInterface extends SceneInterface {

    final static short GROUND_FLAG = 1<<8;
    final static short OBJECT_FLAG = 1<<9;
    final static short ALL_FLAG = -1;
    private static BulletAppState appState;
    private HashMap<RigidBody,PhysicsUnit> rigidBodies = new HashMap<>();
    private HashMap<StaticBody,PhysicsUnit> staticBodies = new HashMap<>();
    private PhysicsSpace world;
    public PhysicsInterface() {
        if(appState == null) {
            try {
                System.loadLibrary("bulletjme");
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
            }
            appState = new BulletAppState();
        }
    }

    @Override
    public void onCreate() {

        world =   new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);//appState.getPhysicsSpace();
        //appState.startPhysics();
        world.setGravity(new com.jme3.math.Vector3f(0, -9.81f, 0));
        world.activateAll(true);
    }

    @Override
    public void onPostUpdate() {
        for(PhysicsUnit unit : rigidBodies.values()){
            unit.rigidBody.targetObject.position.set(convert(unit.rigidBodyCore.getPhysicsLocation()));
            unit.rigidBody.targetObject.rotation.set(convert(unit.rigidBodyCore.getPhysicsRotation()));
        }
        for(PhysicsUnit unit : staticBodies.values()){
            unit.staticBodyCore.setPhysicsLocation(convert(unit.staticBody.targetObject.position));
           // unit.staticBodyCore.setPhysicsRotation(convert(unit.staticBody.targetObject.rotation));
        }
        world.update(1f/60f);
    }

    @Override
    public Object invoke(int i, Object object) {

        if(i==566){//addRigid
            RigidBody rigidBody = (RigidBody) object;
            PhysicsUnit unit = new PhysicsUnit();
            unit.rigidBody = rigidBody;
            unit.shape = new EmptyShape(true);
            PhysicsRigidBody rigidBodyCore = new PhysicsRigidBody(unit.shape,rigidBody.getMass());
            unit.rigidBodyCore = rigidBodyCore;
            world.add(unit.rigidBodyCore);
            rigidBodies.put(rigidBody,unit);

        }else if(i==567){//removeRigid

        }else if(i==568){//addStaticBody
            engine.core.StaticBody staticBody = (StaticBody) object;
            PhysicsUnit unit = new PhysicsUnit();
            unit.staticBody = staticBody;
            unit.shape = new EmptyShape(true);
            PhysicsRigidBody rigidBodyCore = new PhysicsRigidBody(unit.shape,0);
            unit.staticBodyCore = rigidBodyCore;
            world.add(unit.staticBodyCore);
            staticBodies.put((StaticBody) object,unit);
        }else if(i==569){//removeStaticBody
            engine.core.StaticBody staticBody = (StaticBody) object;
            PhysicsUnit unit = staticBodies.get(staticBody);
            if(unit != null) {
                System.out.println(unit);
                world.remove(unit.staticBodyCore);
            }
        }else if(i==570){//mass

        }else if(i==571){//deleteShape

        }else if(i==573){//setShape
            if(object instanceof RigidBody) {
                RigidBody rigidBody = (RigidBody) object;
                PhysicsUnit unit = rigidBodies.get(rigidBody);
                unit.shape = getCoreShape(rigidBody);
                unit.rigidBodyCore.setCollisionShape(unit.shape);
            }else if(object instanceof StaticBody) {
                StaticBody staticBody = (StaticBody) object;
                PhysicsUnit unit = staticBodies.get(staticBody);
                unit.shape = getCoreShape(staticBody);
                unit.staticBodyCore.setCollisionShape(unit.shape);
            }
        }


        switch (i){
            case 0 :
                onCreate();
                editorMode.start();
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
                //onPostUpdate();
                ///editorMode.render();
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
    private static Vector3 convert(Vector3f vector3f){
        return new Vector3(vector3f.x,vector3f.y,vector3f.z);
    }
    private static Vector3f convert(Vector3 vector3) {
        return new Vector3f(vector3.x, vector3.y, vector3.z);
    }
    private static Quaternion convert(com.jme3.math.Quaternion quaternion){
        return new Quaternion(quaternion.getW(),quaternion.getX(),quaternion.getY(),quaternion.getZ());
    }
    private static com.jme3.math.Quaternion convert(Quaternion quaternion){
        return new com.jme3.math.Quaternion(quaternion.x,quaternion.y,quaternion.z,quaternion.w);
    }
    private CollisionShape getCoreShape(CollisionBody collisionBody){
        Class shapeClass = collisionBody.getShape().getClass();
        Vector3 scl = collisionBody.targetObject.scale.copy();
        if(shapeClass == BoxCollisionShape.class){
            scl.mul(0.5f);
            com.jme3.bullet.collision.shapes.BoxCollisionShape coreShape =
                    new com.jme3.bullet.collision.shapes.BoxCollisionShape(scl.x,scl.y,scl.z);
            return coreShape;
        }else if(shapeClass == MeshCollisionShape.class){
            try {
                Mesh mesh = new Mesh();
                MeshCollisionShape shape = (MeshCollisionShape) collisionBody.getShape();
                mesh.loadMesh(targetScene.targetEngineInstance.openAssetFile(shape.meshPath));
                IndexedMesh ms = new IndexedMesh(mesh.positionBuffer,convert(mesh.indice16));
                com.jme3.bullet.collision.shapes.MeshCollisionShape meshCollisionShape =
                        new com.jme3.bullet.collision.shapes.MeshCollisionShape(true,ms);
                return meshCollisionShape;
            }catch (Exception e){
                System.out.println(e.fillInStackTrace());
            }
        }
        return null;
    }

    private static IntBuffer convert(ShortBuffer buffer){
        buffer.position(0);
        ByteBuffer tmp = ByteBuffer.allocateDirect(buffer.capacity() * 4);
        tmp.order(ByteOrder.nativeOrder());
        IntBuffer intBuffer = tmp.asIntBuffer();
        intBuffer.position(0);
        short[] reader = new short[buffer.capacity()];
        int[] put = new int[reader.length];
        buffer.get(reader);
        for(int i=0;i < reader.length;i++){
            put[i] = reader[i];
        }
        intBuffer.put(put);
        intBuffer.position(0);
        return intBuffer;
    }


    private EditorMode editorMode = new EditorMode();

    @Override
    public void onRemoved() {
    }

    private class EditorMode {
        private LineShaderProgram shaderProgram;

        private void start() {
            shaderProgram = new LineShaderProgram();
            shaderProgram.compile();
            shaderProgram.setDrawType(ShaderProgram.DrawType.LINE);
        }
    }

    class PhysicsUnit{
        PhysicsRigidBody rigidBodyCore;
        PhysicsRigidBody staticBodyCore;
        RigidBody rigidBody;
        StaticBody staticBody;
        CollisionShape shape;
    }
}
