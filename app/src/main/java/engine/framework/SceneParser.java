package engine.framework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SceneParser {
    public void store(Scene scene, OutputStream outputStream) throws Exception {
        DataOutputStream dos = new DataOutputStream(outputStream);
        dos.writeUTF("1.0.0");
        dos.writeBoolean(scene.active);
        GameObject[] objects = scene.getObjects();
        SceneInterface[] interfaces =scene.getInterfaces();
        dos.writeInt(interfaces.length);
        for(SceneInterface sceneInterface : interfaces){
            byte[] data = sceneInterface.getBytes();
            dos.writeUTF(sceneInterface.getClass().getName());
            dos.writeBoolean(sceneInterface.active);
            if(data == null){
                dos.writeInt(0);
            }else {
                dos.writeInt(data.length);
                dos.write(data);
            }

        }
        dos.writeInt(objects.length);
        for(GameObject gameObject : objects){
            dos.writeUTF(gameObject.name);
            dos.writeBoolean(gameObject.active);

            dos.writeFloat(gameObject.position.x);
            dos.writeFloat(gameObject.position.y);
            dos.writeFloat(gameObject.position.z);

            dos.writeFloat(gameObject.rotation.x);
            dos.writeFloat(gameObject.rotation.y);
            dos.writeFloat(gameObject.rotation.z);
            dos.writeFloat(gameObject.rotation.w);

            dos.writeFloat(gameObject.scale.x);
            dos.writeFloat(gameObject.scale.y);
            dos.writeFloat(gameObject.scale.z);

            ObjectModifier modifiers[] = gameObject.getModifiers();
            dos.writeInt(modifiers.length);
            for(ObjectModifier objectModifier : modifiers){
                dos.writeUTF(objectModifier.getClass().getName());
                dos.writeBoolean(objectModifier.active);
                byte[] data = objectModifier.getBytes();
                if(data == null){
                    dos.writeInt(0);
                }else {
                    dos.writeInt(data.length);
                    dos.write(data);
                }
            }

        }
        dos.flush();

    }
    public Scene load(InputStream inputStream) throws Exception {
       return load(inputStream,SceneParser.class.getClassLoader());
    }
    public Scene load(InputStream inputStream,ClassLoader classloader) throws Exception {
        DataInputStream dis = new DataInputStream(inputStream);
        String version = dis.readUTF();
        System.out.println("version:"+version);
        Scene scene = new Scene();
        scene.active =dis.readBoolean();
        System.out.println("active:"+scene.active);
        int sceneInterfaceCount = dis.readInt();
        System.out.println("Interface Count:"+sceneInterfaceCount);
        for(int i=0;i < sceneInterfaceCount;i++){
            String clss = dis.readUTF();
            System.out.println(clss);
            boolean state = dis.readBoolean();
            int data = dis.readInt();
            byte[] dataArray = readNBytes(dis,data);
            System.out.println("data:"+data);
            try{
                SceneInterface sceneInterface = (SceneInterface) classloader.loadClass(clss).newInstance();
                scene.addInterface(sceneInterface);
                sceneInterface.active = state;
                sceneInterface.fromBytes(dataArray);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        int objectCount = dis.readInt();
        System.out.println("gobjec:"+objectCount);
        for(int o=0;o < objectCount;o++){
            GameObject gameObject = new GameObject();
            gameObject.name = dis.readUTF();
            gameObject.active = dis.readBoolean();
            System.out.println("GObjct :"+gameObject.name);
            gameObject.position.set(dis.readFloat(),dis.readFloat(),dis.readFloat());
            gameObject.rotation.set(dis.readFloat(),dis.readFloat(),dis.readFloat(),dis.readFloat());
            gameObject.scale.set(dis.readFloat(),dis.readFloat(),dis.readFloat());

            int mod_count = dis.readInt();
            for(int m=0;m < mod_count;m++){
                String cls = dis.readUTF();
                boolean state = dis.readBoolean();
                int i = dis.readInt();
                byte[] data = readNBytes(dis,i);
                try{
                    ObjectModifier modifier = (ObjectModifier) classloader.loadClass(cls).newInstance();
                    gameObject.addModifier(modifier);
                    modifier.active = state;
                    modifier.fromBytes(data);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            scene.addObject(gameObject);
        }
        return scene;
    }

    public byte[] readNBytes(java.io.InputStream inputStream, int length) throws Exception {
        byte[] data = new byte[length];
        int bytesRead = 0;
        while (bytesRead < length) {
            int result = inputStream.read(data, bytesRead, length - bytesRead);
            if (result == -1) break;
            bytesRead += result;
        }
        if (bytesRead < length) {
            byte[] actualData = new byte[bytesRead];
            System.arraycopy(data, 0, actualData, 0, bytesRead);
            return actualData;
        }
        return data;
    }
}
