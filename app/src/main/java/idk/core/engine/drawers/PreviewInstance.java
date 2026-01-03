package idk.core.engine.drawers;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import engine.framework.DefaultEngineInstance;
import idk.core.engine.HomeActivity;

public class PreviewInstance extends DefaultEngineInstance {

    String project;
    public PreviewInstance(Activity activity) {
        super(activity);
        project = activity.getIntent().getStringExtra("project");
    }
    @Override
    public AssetFileDescriptor getAssetFile(String name) {
        try {
            File file = new File(HomeActivity.getCurrent().project+"/assets",name);
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY);
            AssetFileDescriptor assetFileDescriptor = new AssetFileDescriptor(parcelFileDescriptor,0,file.length());
            return  assetFileDescriptor;
        } catch (IOException e) {
            return null;
        }
    }


    @Override
    public InputStream openAssetFile(String name) {
        try {
            return new FileInputStream(new File(project+"/assets",name));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public long getFileSize(String name) {
        File file = new File(project+"/assets",name);
        return file.length();
    }

    @Override
    public String[] assetList(String path) {
        try {
            File[] file = new File(project+"/assets",path).listFiles();
            String[] arr = new String[file.length];
            for(int i= 0;i < arr.length;i++){
                String projectPath = file[i].getAbsolutePath().substring(
                        (project+
                                "/assets").length());
                arr[i] = projectPath;
            }
            return arr;
        } catch (Exception e) {

        }
        return null;
    }

}
