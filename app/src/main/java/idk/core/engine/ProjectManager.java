package idk.core.engine;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;

import engine.internal.util.FileUtils;

public class ProjectManager {
    private Context context;
    public ProjectManager(Context context1){
        context = context1;
    }
    public boolean createProject(String name,String pack){
        String media = context.getExternalMediaDirs()[0].getAbsolutePath()+"/Projects";
        String rpo =media+"/"+name;
        if(new File(rpo).exists()){
            return false;
        }
        IfNot(rpo);
        IfNot(rpo+"/app");
        IfNot(rpo+"/app/res");
        IfNot(rpo+"/app/src");
        IfNot(rpo+"/app/src/"+pack.replace("\\.","/"));
        IfNot(rpo+"/assets");
        IfNot(rpo+"/logic");
        IfNot(rpo+"/build");
        IfNot(rpo+"/build/dex");
        IfNot(rpo+"/build/cache");
        IfNot(rpo+"/build/class");

        try {
            String[] files = context.getAssets().list("");
            for (String file : files) {
                FileUtils.copy(context.getAssets().open(file), new FileOutputStream(rpo + "/assets/"+file));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
    public void IfNot(String path){
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
    }
}
