package idk.core.engine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ProjectActivity extends Activity {

    private FileAdapter adapter;
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getDecorView().setPadding(0, getStatusBarHeight(), 0, 0);
        }
        path = getIntent().getStringExtra("path");
        adapter = new FileAdapter(this,path);
        setContentView(R.layout.project_home);
        ListView lv = findViewById(R.id.project_file_list);
        lv.setAdapter(adapter);
        ImageView img = findViewById(R.id.add_new_file);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newFile();
            }
        });
    }
    private void newFile(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.new_file_dialog);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.findViewById(R.id.create_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView name = dialog.findViewById(R.id.new_file_name);
                File file = new File(adapter.getPath(),name.getText().toString().trim());
                if(!file.exists()){
                    try {
                        file.createNewFile();
                        adapter.refresh(adapter.getPath());
                        dialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(ProjectActivity.this,"File Failed",2).show();
                    }
                }else {
                    Toast.makeText(ProjectActivity.this,"File Already Exist",2).show();
                }
            }
        });
        dialog.findViewById(R.id.cancel_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    public int getStatusBarHeight() {
        int result = 0;
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        if(adapter.getPath().equals(path)){
            super.onBackPressed();
        }else {
            adapter.refresh(adapter.getParentPath());
        }
    }
}