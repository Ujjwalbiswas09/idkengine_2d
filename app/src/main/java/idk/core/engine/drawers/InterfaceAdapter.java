package idk.core.engine.drawers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import engine.framework.GameObject;
import engine.framework.Scene;
import engine.framework.SceneInterface;
import idk.core.engine.R;

public abstract class InterfaceAdapter extends ArrayAdapter<SceneInterface> {

    public Scene scene;
    public SceneInterface[] cache;
    public ListView listView;
    public InterfaceAdapter(@NonNull Context context,Scene scene) {
        super(context, android.R.layout.simple_list_item_1);
        this.scene = scene;
        cache = scene.getInterfaces();
        listView = new ListView(context);
        listView.setAdapter(this);
    }
    public ListView getListView(){
        return listView;
    }
    public void refresh(){
        cache = scene.getInterfaces();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return cache.length;
    }

    @Nullable
    @Override
    public SceneInterface getItem(int position) {
        return cache[position];
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = ViewGroup.inflate(getContext(), R.layout.object_item_view,null);
        SceneInterface sceneInterface = getItem(position);
        TextView tv = view.findViewById(R.id.object_name);
        tv.setText(sceneInterface.getClass().getSimpleName());
        CheckBox checkBox = view.findViewById(R.id.object_state);
        checkBox.setChecked(sceneInterface.active);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sceneInterface.active = b;
            }
        });

        view.findViewById(R.id.object_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInterfaceEdit(sceneInterface);
            }
        });
        return view;
    }
    public abstract void onInterfaceEdit(SceneInterface gameObject);

}
