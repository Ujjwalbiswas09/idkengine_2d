package idk.core.engine.drawers;

import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import engine.framework.GameObject;
import engine.framework.Scene;
import idk.core.engine.HomeActivity;
import idk.core.engine.R;

public abstract class ObjectAdapter extends ArrayAdapter<GameObject> {

    public Scene scene;
    public GameObject[] cache;
    public ListView listView;
    public ObjectAdapter(@NonNull Context context,Scene scene) {
        super(context, android.R.layout.simple_list_item_1);
        this.scene = scene;
        cache = scene.getObjects();
        listView = new ListView(context);
        listView.setAdapter(this);
    }
    public ListView getListView(){
        return listView;
    }
    public void refresh(){
        cache = scene.getObjects();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return cache.length;
    }

    @Nullable
    @Override
    public GameObject getItem(int position) {
        return cache[position];
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        if(convertView != null){
            view= convertView;
        }else {
            view = ViewGroup.inflate(getContext(), R.layout.object_item_view, null);
        }
        GameObject gameObject = getItem(position);
        TextView tv = view.findViewById(R.id.object_name);
        tv.setText(gameObject.name);
        CheckBox checkBox = view.findViewById(R.id.object_state);
        checkBox.setChecked(gameObject.active);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                gameObject.active = b;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onObjectSelect(gameObject);
            }
        });
        view.findViewById(R.id.object_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onObjectEdit(gameObject);
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(),view);
                Menu menu =popupMenu.getMenu();
                menu.add("Copy");
                menu.add("Delete");
                popupMenu.setOnMenuItemClickListener(item -> {
                    if(item.getTitle().equals("Copy")){

                    }else if(item.getTitle().equals("Delete")){
                        gameObject.targetScene.targetEngineInstance.engineQueue(new Runnable() {
                            @Override
                            public void run() {
                                scene.removeObject(gameObject);
                                scene.targetEngineInstance.removeValue("selectedObject");
                                HomeActivity.current.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refresh();
                                    }
                                });
                            }
                        });

                    }
                    return true;
                });
                popupMenu.show();
                return true;
            }
        });
        return view;
    }
    public abstract void onObjectSelect(GameObject gameObject);
    public abstract void onObjectEdit(GameObject gameObject);

}
