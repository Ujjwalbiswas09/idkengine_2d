package idk.core.editors;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import engine.framework.ObjectModifier;
import idk.core.engine.R;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class NumberEditor implements View.OnClickListener, TextView.OnEditorActionListener {
    private View root;
    private LinearLayout parent;
    private Field field;
    private Object object;
    private TextView name;
    private EditText value;
    public NumberEditor(LinearLayout parent) {
        this.parent = parent;
        root = ViewGroup.inflate(parent.getContext(), R.layout.number_editor,null);
        name = root.findViewById(R.id.number_name);
        value = root.findViewById(R.id.number_edit);
        value.setOnEditorActionListener(this);
        parent.addView(root);
    }

    public void setTarget(Field fld,Object obj){
        field = fld;
        object = obj;
        if(fld != null){
            name.setText(field.getName());
        }
        try {
            if (fld.get(obj) != null) {
                value.setText(""+fld.get(obj));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void close(){
        parent.removeView(root);
    }

    @Override
    public void onClick(View v) {
        if(field != null){
            try {
                field.setAccessible(true);
                long vale = Long.parseLong(value.getText().toString());
                Type type = field.getType();
                if(type == int.class){
                    if(vale >= Integer.MAX_VALUE){
                        vale = Integer.MAX_VALUE;
                    }
                    field.setInt(object,(int)vale);
                    value.setText(""+vale);
                }else if(type == long.class){
                    field.setLong(object,vale);
                    value.setText(""+vale);
                }else if(type == byte.class){
                    int i = (int)vale;
                    if(i >= 255){
                        i = 255;
                    }
                    field.setByte(object,(byte) i);
                    value.setText(""+i);
                }else if(type == short.class){
                    int i = (int)vale;
                    if(i >= Short.MAX_VALUE){
                        i = Short.MAX_VALUE;
                    }
                    field.setShort(object,(short)i);
                    value.setText(""+i);
                }else if(type == char.class) {
                    int i = (int)vale;
                    if(i >= Character.MAX_VALUE){
                        i = Character.MAX_VALUE;
                    }
                    field.setChar(object,(char)i);
                    value.setText(""+i);
                }
                if(ObjectModifier.class.isAssignableFrom( object.getClass())){
                    ObjectModifier objectModifier = (ObjectModifier)object;
                    objectModifier.targetObject.targetScene.targetEngineInstance.engineQueue(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                objectModifier.onDataReceived(field.getName(),field.get(object));
                            } catch (IllegalAccessException e) {

                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
                value.setText("0");
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE){
            onClick(null);
            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        }
        return false;
    }
}
