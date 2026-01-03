package idk.core.engine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends Activity {

    private ProjectListAdapter listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        getWindow().getDecorView().setPadding(0, getStatusBarHeight(MainActivity.this), 0, 0);
        }
       setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.add_project);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(R.layout.new_project);
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                alertDialog.findViewById(R.id.save_project).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView tv = alertDialog.findViewById(R.id.project_name);
                        TextView pk = alertDialog.findViewById(R.id.package_name);
                        ProjectManager projectManager = new ProjectManager(getApplicationContext());
                        if(projectManager.createProject(tv.getText().toString(),pk.getText().toString())){
                            alertDialog.dismiss();
                            listAdapter.refresh();
                        }else {
                            Toast.makeText(MainActivity.this,"Already Exists",1).show();
                        }
                    }
                });
                alertDialog.findViewById(R.id.cancel_project).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });


            }
        });
        listAdapter = new ProjectListAdapter(this){
            @Override
            public void onClick(int i, View view) {
                Intent intent = new Intent();
                intent.putExtra("path",getItem(i).getAbsolutePath());
                intent.setClass(getContext(),ProjectActivity.class);
                getContext().startActivity(intent);
                finish();
            }
        };
        ListView listView = findViewById(R.id.projects_list);
        listView.setAdapter(listAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(listAdapter != null) {
            listAdapter.refresh();
        }
    }
    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }
}