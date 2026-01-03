package idk.core.editors;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;



import java.lang.reflect.Field;

import idk.core.engine.R;

public class BooleanEditor implements CompoundButton.OnCheckedChangeListener {
    private View root;
    private LinearLayout parent;
    private Field field;
    private Object object;
    private Switch aSwitch;
    public BooleanEditor(LinearLayout parent) {
        this.parent = parent;
        root = LayoutInflater.from(parent.getContext()).inflate(R.layout.boolean_editor,null);
        aSwitch = root.findViewById(R.id.boolean_switch);
        parent.addView(root);
        if(aSwitch==null){
            System.out.println("Null Switch");
            return;
        }
        aSwitch.setOnCheckedChangeListener(this);

    }

    public void setTarget(Field fld,Object obj){
        field = fld;
        object = obj;
        if(fld != null){
            aSwitch.setText(field.getName());
        }
        try {
            field.setAccessible(true);
            if (fld.get(obj) != null) {
               aSwitch.setChecked(field.getBoolean(obj));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void close(){
        parent.removeView(root);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(field != null){
            try {
                field.setAccessible(true);
                field.setBoolean(object,isChecked);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
