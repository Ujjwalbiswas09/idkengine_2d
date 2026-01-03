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

public class TextEditor implements View.OnClickListener, TextView.OnEditorActionListener {
    private View root;
    private LinearLayout parent;
    private Field field;
    private Object object;
    private TextView name;
    private EditText value;
    public TextEditor(LinearLayout parent) {
        this.parent = parent;
        root = ViewGroup.inflate(parent.getContext(), R.layout.string_editor,null);
        name = root.findViewById(R.id.string_name);
        value = root.findViewById(R.id.string_value);
        parent.addView(root);
        value.setOnEditorActionListener(this);
    }

    public void setTarget(Field fld,Object obj){
        field = fld;
        object = obj;
        if(fld != null){
            name.setText(field.getName());
        }
        try {
            fld.setAccessible(true);
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
                field.set(object,value.getText().toString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId == EditorInfo.IME_ACTION_DONE){
            if(field != null){
                try {
                    boolean d = field.isAccessible();
                    field.setAccessible(true);
                    String str = value.getText().toString();
                    field.set(object,str);
                    field.setAccessible(d);

                    if(ObjectModifier.class.isAssignableFrom( object.getClass())){
                        ObjectModifier objectModifier = (ObjectModifier)object;
                        objectModifier.targetObject.targetScene.targetEngineInstance.engineQueue(new Runnable() {
                            @Override
                            public void run() {
                                objectModifier.onDataReceived(field.getName(),str);
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        }
        return false;
    }
}
