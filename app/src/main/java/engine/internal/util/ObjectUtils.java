package engine.internal.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.javagl.obj.Obj;
import engine.framework.Flags;

public class ObjectUtils {
    public static byte[] saveState(Object value){
        List<Field> fieldList = new ArrayList<>();
        Class cls = value.getClass();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        for(Field field : cls.getFields()){
            int mod = field.getModifiers();
            if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                continue;
            }
            boolean all = Modifier.isPublic(mod);
            if(field.isAnnotationPresent(Flags.class)){
                Flags flags = field.getAnnotation(Flags.class);
                String[] values = flags.flag();
                for (String str : values){
                    if(str.equals("STORE")){
                        field.setAccessible(true);
                        all = true;
                    }else if(str.equals("IGNORE")){
                        all = false;
                    }
                }
            }
            if(all) {
                if (!fieldList.contains(field)) {
                    fieldList.add(field);
                }
            }
        }

        for(Field field : cls.getDeclaredFields()){
            int mod = field.getModifiers();
            if(Modifier.isStatic(mod) || Modifier.isFinal(mod)){
                continue;
            }
            boolean all = Modifier.isPublic(mod);
            if(field.isAnnotationPresent(Flags.class)){
                Flags flags = field.getAnnotation(Flags.class);
                String[] values = flags.flag();
                for (String str : values){
                    if(str.equals("STORE")){
                        field.setAccessible(true);
                        all = true;
                    }else if(str.equals("IGNORE")){
                        all = false;
                    }
                }
            }
            if(all) {
                if (!fieldList.contains(field)) {
                    fieldList.add(field);
                }
            }
        }

        try {
            dos.writeUTF(cls.getName());
            dos.writeInt(fieldList.size());
            for (Field field : fieldList) {
                boolean resotePrivate=false;
                if(field.isAnnotationPresent(Flags.class)){
                    Flags flags = field.getAnnotation(Flags.class);
                    String[] values = flags.flag();
                    for (String str : values){
                        if(str.equals("STORE")){
                            field.setAccessible(true);
                            resotePrivate = true;
                        }
                    }
                }

                    String name = field.getName();
                    Class type = field.getType();
                    if(type.isPrimitive()){
                    dos.writeUTF(name);
                    if (type == int.class) {
                        dos.writeInt(field.getInt(value));
                    } else if (type == short.class) {
                        dos.writeShort(field.getShort(value));
                    } else if (type == long.class) {
                        dos.writeLong(field.getLong(value));
                    } else if (type == byte.class) {
                        dos.writeByte(field.getByte(value));
                    } else if (type == char.class) {
                        dos.writeChar(field.getChar(value));
                    } else if (type == float.class) {
                        dos.writeFloat(field.getFloat(value));
                    } else if (type == double.class) {
                        dos.writeDouble(field.getDouble(value));
                    } else if (type == boolean.class) {
                        dos.writeBoolean(field.getBoolean(value));
                    } }
                    else if (type == String.class) {
                        String str = (String) field.get(value);
                        if(str != null) {
                            dos.writeUTF(name);
                            dos.writeUTF(field.get(value).toString());
                        }
                    }else if (type.isEnum()) {
                        Enum enu = (Enum) field.get(value);
                        if (enu != null) {
                            dos.writeUTF(name);
                            dos.writeUTF(enu.name());
                        }
                    } else if(type.isArray()) {

                    }else {
                        Object object = field.get(value);
                        if(object != null) {
                            dos.writeUTF(name);
                            dos.writeUTF(object.getClass().getName());
                            byte[] data = saveState(object);
                            dos.writeInt(data.length);
                            dos.write(data);
                        }
                    }

                if(resotePrivate){
                    field.setAccessible(false);
                }
            }
            dos.flush();
            return bos.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new byte[0];
    }
    public static void recallState(Object target,byte[] value,ClassLoader loader){
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(value);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        try {
            String className = dataInputStream.readUTF();
            int fieldCount = dataInputStream.readInt();
            Class cls = loader.loadClass(className);
            HashMap<String,Field> fieldHashMap = new HashMap<>();
            for(Field field : cls.getFields()){
                Class fieldClass = field.getType();
                    if (!fieldHashMap.containsValue(field)) {
                        fieldHashMap.put(field.getName(),field);
                    }
            }
            for(Field field : cls.getDeclaredFields()){
                Class fieldClass = field.getType();
                    if (!fieldHashMap.containsValue(field)) {
                        fieldHashMap.put(field.getName(),field);
                    }
            }
            System.out.println("Count:"+fieldCount);
            System.out.println("name:"+className);
            for(int i=0;i < fieldCount;i++){
                String fieldName = dataInputStream.readUTF();
                Field  field = fieldHashMap.get(fieldName);
                boolean resotePrivate = false;
                if(field.isAnnotationPresent(Flags.class)){
                    Flags flags = field.getAnnotation(Flags.class);
                    String[] values = flags.flag();
                    for (String str : values){
                        if(str.equals("STORE")){
                            field.setAccessible(true);
                            resotePrivate = true;
                        }
                    }
                }
                parseFields(dataInputStream,target,field,loader);
                if(resotePrivate){
                    field.setAccessible(false);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void parseFields(DataInputStream dis,Object value,Field field,ClassLoader loader) throws Exception{
        Class type = field.getType();
        if (type == int.class) {
            field.setInt(value, dis.readInt());
        } else if (type == short.class) {
            field.setShort(value, dis.readShort());
        } else if (type == long.class) {
            field.setLong(value, dis.readLong());
        } else if (type == byte.class) {
            field.setByte(value, dis.readByte());
        } else if (type == char.class) {
            field.setChar(value, dis.readChar());
        } else if (type == float.class) {
            field.setFloat(value, dis.readFloat());
        } else if (type == double.class) {
            field.setDouble(value, dis.readDouble());
        } else if (type == boolean.class) {
            field.setBoolean(value, dis.readBoolean());
        } else if (type == String.class) {
            //String nameRead = dis.readUTF(); // name
            field.set(value, dis.readUTF());
        } else if (type.isEnum()) {
            //String nameRead = dis.readUTF(); // name
            String enumName = dis.readUTF();
            Enum<?> enumValue = Enum.valueOf((Class<Enum>) type, enumName);
            field.set(value, enumValue);
        }else {
            String className = dis.readUTF();
            int dataSize = dis.readInt();
            byte[] data = new byte[dataSize];
            dis.read(data);
            try {
                Class cls = loader.loadClass(className);
                Object target = field.get(value);
                if (target == null || !cls.isAssignableFrom(field.getType())) {
                    target = cls.newInstance();
                    field.set(value,target);
                }
                recallState(target, data, loader);
            }catch (Exception e){

            }
        }

    }
}
