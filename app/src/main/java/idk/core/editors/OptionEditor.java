package idk.core.editors;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import de.javagl.obj.Obj;
import engine.framework.ObjectModifier;
import idk.core.engine.R;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class OptionEditor implements AdapterView.OnItemSelectedListener {

    private View root;
    private TextView name;
    private Spinner spinner;
    private ArrayAdapter adapter;
    private Field field;
    private Object object;

    public OptionEditor(LinearLayout linearLayout){
        root = View.inflate(linearLayout.getContext(), R.layout.option_editor,null);
        spinner = root.findViewById(R.id.option_spinner);
        name = root.findViewById(R.id.option_name);
        linearLayout.addView(root);
    }
    public void setTarget(Object obj, Field fld){
        field = fld;
        object = obj;
        if(fld != null){
            name.setText(fld.getName());
        }
        adapter = new ArrayAdapter<>(root.getContext(),
                android.R.layout.simple_spinner_dropdown_item);
        for (Object en : fld.getType().getEnumConstants()){
            adapter.add(en);
        }

        spinner.setAdapter(adapter);
        if(adapter.isEmpty()){
            return;
        }

        try {
            fld.setAccessible(true);
            if (fld.get(obj) != null) {
                spinner.setSelection(adapter.getPosition(fld.get(obj)));
            }else {
                spinner.setSelection(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try{

            field.setAccessible(true);
            if(adapter.getItem(position)==field.get(object)){
                return;
            }
            Object val = adapter.getItem(position);
            field.set(object,val);
            if(ObjectModifier.class.isAssignableFrom( object.getClass())){
                ObjectModifier objectModifier = (ObjectModifier)object;
                objectModifier.targetObject.targetScene.targetEngineInstance.engineQueue(new Runnable() {
                    @Override
                    public void run() {
                        objectModifier.onDataReceived(field.getName(),val);
                    }
                });
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
