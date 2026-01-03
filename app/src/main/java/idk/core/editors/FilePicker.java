package idk.core.editors;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.logging.Handler;

import engine.framework.ObjectModifier;
import idk.core.engine.HomeActivity;
import idk.core.engine.R;
import idk.core.engine.drawers.FileManager;

public class FilePicker {

    ImageView image;
    View view;
    TextView textView;
    Field field;
    Object targt;
    private String[] supportedFormat;
    TextView text_path;
    public FilePicker(LinearLayout ln,String[] formats){
        view = ViewGroup.inflate(ln.getContext(), R.layout.image_picker,null);
        image = view.findViewById(R.id.texture_preview);
        image.setVisibility(View.GONE);
        textView = view.findViewById(R.id.texture_name);
        text_path = view.findViewById(R.id.texture_path);
        ln.addView(view);
        text_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });
        supportedFormat = formats;
    }
    public void setTarget(Field fld,Object target){
        try {
            targt = target;
            field =fld;
            textView.setText(fld.getName());
            fld.setAccessible(true);
            String str = (String) fld.get(target);
            if(str != null) {
                text_path.setText(str);
            }else {
                text_path.setText("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private AlertDialog dialog;
    public void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.getCurrent());
        builder.setCancelable(true);
        FileManager fileManager = new FileManager(HomeActivity.getCurrent(),HomeActivity.getCurrent().project+"/assets"){
            @Override
            public void onFileSelect(File file) {
                String projectPath = file.getAbsolutePath().substring(
                        (HomeActivity.getCurrent().project+
                                "/assets").length()
                );
                try {
                    field.set(targt,projectPath);
                    text_path.setText((String)field.get(targt));
                    if(ObjectModifier.class.isAssignableFrom( targt.getClass())){
                        ObjectModifier objectModifier = (ObjectModifier)targt;
                        objectModifier.targetObject.targetScene.targetEngineInstance.engineQueue(new Runnable() {
                            @Override
                            public void run() {
                                objectModifier.onDataReceived(field.getName(),projectPath);
                            }
                        });
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println(projectPath);
                dialog.dismiss();
            }

            @Override
            public boolean filter(File file) {
                String name = file.getName().toLowerCase();
                if(name.length() > 5){
                    name = name.substring(name.length() - 5);
                }
                if(supportedFormat != null){
                    for(String s : supportedFormat){
                        if(name.endsWith(s)){
                            return true;
                        }
                    }
                    return false;
                }else {
                    return true;
                }

            }
        };
        builder.setView(fileManager.getListView());
        dialog = builder.create();
        dialog.show();
    }

}

