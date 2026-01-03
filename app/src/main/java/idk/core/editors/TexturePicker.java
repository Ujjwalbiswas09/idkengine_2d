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

public class TexturePicker {

    ImageView image;
    View view;
    TextView textView;
    Field field;
    Object targt;
    TextView text_path;
    public TexturePicker(LinearLayout ln){
        view = ViewGroup.inflate(ln.getContext(), R.layout.image_picker,null);
        image = view.findViewById(R.id.texture_preview);
        textView = view.findViewById(R.id.texture_name);
        text_path = view.findViewById(R.id.texture_path);
        ln.addView(view);
        text_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text_path.performClick();
            }
        });
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
            InputStream ins = HomeActivity.getCurrent().engineInstance.openAssetFile(str);
            if(ins != null){
                HomeActivity.getCurrent().demonThread.tasks.add(new Runnable() {
                    @Override
                    public void run() {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.outHeight = 200;
                        options.outWidth = 200;
                        Rect rect = new Rect();
                        Bitmap bitmap = BitmapFactory.decodeStream(ins,rect,options);
                        HomeActivity.current.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                image.setImageBitmap(bitmap);
                            }
                        });
                    }
                });
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
                    if(projectPath != null) {
                        text_path.setText(projectPath);
                    }else {
                        text_path.setText("");
                    }
                    if(ObjectModifier.class.isAssignableFrom( targt.getClass())){
                        ObjectModifier objectModifier = (ObjectModifier)targt;
                        objectModifier.targetObject.targetScene.targetEngineInstance.engineQueue(new Runnable() {
                            @Override
                            public void run() {
                                objectModifier.onDataReceived(field.getName(),projectPath);
                            }
                        });
                    }
                    InputStream ins = HomeActivity.getCurrent().engineInstance.openAssetFile(projectPath);
                    if(ins != null){
                        HomeActivity.getCurrent().demonThread.tasks.add(new Runnable() {
                            @Override
                            public void run() {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.outHeight = 200;
                                options.outWidth = 200;
                                Rect rect = new Rect();
                                Bitmap bitmap = BitmapFactory.decodeStream(ins,rect,options);
                                HomeActivity.current.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        image.setImageBitmap(bitmap);
                                    }
                                });
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
                return name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg");
            }
        };
        builder.setView(fileManager.getListView());
        dialog = builder.create();
        dialog.show();
    }
}
