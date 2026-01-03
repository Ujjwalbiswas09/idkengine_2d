package idk.ide;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class JarPackage {
    public int subDir;
    public HashMap<String,JarPackage> childPack = new HashMap<>();
    public HashMap<String,String> files = new HashMap<>();
    private String name="";
    public JarPackage parent;
    public JarPackage(){
        subDir = 0;
    }

    public String getAbsName(){
        return"";
    }

    public String hasClass(String[] name){
        int dlen =name.length -1;
        if(dlen==subDir){
            return files.get(name[dlen]);
        }else{
            String key = name[subDir];
            // System.out.println("Search:"+key);
            if(childPack.containsKey(key)){
                System.out.println("Pack Found: "+key);
                return childPack.get(key).hasClass(name);
            }
        }
        return null;
    }
    public List<String[]> search(String prefix){
        List<String[]> result = new ArrayList<>();
        for(Map.Entry<String,String> map : files.entrySet()){
            String key = map.getKey();
            if(key.startsWith(prefix)){
                result.add(new String[]{key,map.getValue()});
            }
        }
        for(JarPackage jar : childPack.values()){
            result.addAll(jar.search(prefix));
        }
        return result;
    }

    public void loadParent(String... args){
        for(String path : args) {
            try {
                ZipFile zipInputStream = new ZipFile(path);
                Enumeration<? extends ZipEntry> entryEnumeration = zipInputStream.entries();

                while (entryEnumeration.hasMoreElements()){
                    ZipEntry entry = entryEnumeration.nextElement();
                    if(!entry.isDirectory()) {
                        try {
                            addEntry(entry, new ZipFile(path));
                        }catch (Exception e){

                        }
                    }
                }
                zipInputStream.close();
            } catch (Exception e) {

            }
        }
    }
    public JarPackage hasPackage(String[] nae){
        int len = nae.length -1;
        System.out.println(nae[subDir]+":"+name);
        if(len==subDir){
            return childPack.get(nae[subDir]);
        }else{
            return childPack.get(nae[subDir]).hasPackage(nae);
        }

    }
    public static boolean isNumericRegex(String str) {
        if (str == null)
            return false;
        return str.matches("-?\\d+");
    }
    public void addEntry(ZipEntry entry, ZipFile file){
        String name = entry.getName();
        if(!name.endsWith(".class")){
            return;
        }
        if(name.contains("$")){
            name = name.replace("$","/");
        }
        String[] dir = name.split("/");
        int direclen = dir.length - 1;
        if(direclen==subDir){
            String nam = dir[direclen];
            String file_name = nam.substring(0,nam.lastIndexOf(".class"));
            if(isNumericRegex(file_name)){
                return;
            }
            files.put(file_name,String.join(".",dir));
        }else if(direclen > subDir){
            String key = dir[subDir];
            if(!childPack.containsKey(key)){
                JarPackage child = new JarPackage();
                child.subDir = subDir +1;
                child.name = key;
                child.parent = this;
                childPack.put(key,child);
                //System.out.println("put;"+key);
            }
            JarPackage ch = childPack.get(key);
            ch.addEntry(entry,file);
        }
    }
    public static ClassType createFromEntry(ZipEntry entry,ZipFile file){
       return null;
    }

}
