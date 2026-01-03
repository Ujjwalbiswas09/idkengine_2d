package idk.core.engine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.Date;

public abstract class ProjectListAdapter extends ArrayAdapter<File> {

    private String path;
    private File[] list;
    private Activity act;

    public ProjectListAdapter(@NonNull Activity context) {
        super(context, android.R.layout.simple_list_item_1);
        path = context.getExternalMediaDirs()[0]+"/Projects";
        list = new File(path).listFiles();
        if(list == null){
            new File(path).mkdirs();
            list = new File(path).listFiles();
        }
        act = context;
    }
    public void refresh(){
        list = new File(path).listFiles();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Nullable
    @Override
    public File getItem(int position) {
        return list[position];
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = ViewGroup.inflate(getContext(),R.layout.project_item,null);
        File file = getItem(position);
        TextView name = view.findViewById(R.id.project_name_item);
        TextView date = view.findViewById(R.id.project_date);
        name.setText(file.getName());
        date.setText(new Date(file.lastModified()).toString());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectListAdapter.this.onClick(position,view);
            }
        });
        return view;
    }
    public abstract void onClick(int i,View view);
}
