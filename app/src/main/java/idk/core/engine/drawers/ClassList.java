package idk.core.engine.drawers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import engine.core.Camera;
import engine.core.ObjectRenderer;
import engine.core.RigidBody;
import engine.core.StaticBody;
import idk.core.engine.HomeActivity;

public class ClassList extends ArrayAdapter<Class> implements AdapterView.OnItemClickListener {
    private ListView listView;
    private static ClassList instance;
    private AdapterView.OnItemClickListener clickListener;
    public static ClassList getInstance(AdapterView.OnItemClickListener listener){

            instance = new ClassList(HomeActivity.getCurrent());
            instance.addAll(ObjectRenderer.class, Camera.class, RigidBody.class, StaticBody.class);
            instance.clickListener = listener;

        return instance;
    }
    public ClassList(@NonNull Context context) {
        super(context, android.R.layout.simple_list_item_1);
        listView = new ListView(context);
        listView.setAdapter(this);
        listView.setOnItemClickListener(this);
    }

    public ListView getListView() {
        return listView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = new TextView(getContext());
        view.setText(getItem(position).getSimpleName());
        view.setTextColor(Color.BLACK);
        view.setTypeface(Typeface.MONOSPACE,Typeface.BOLD);
        view.setTextSize(15f);
        return view;
    }

    public void setClickListener(AdapterView.OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(clickListener !=null){
            clickListener.onItemClick(adapterView,view,i,l);
        }
    }
}
