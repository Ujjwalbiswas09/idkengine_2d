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


import java.lang.reflect.Field;

import idk.core.engine.R;

public class DecimalEditor implements View.OnClickListener, TextView.OnEditorActionListener {
    private View root;
    private LinearLayout parent;
    private Field field;
    private Object object;
    private TextView name;
    private EditText value;
    public DecimalEditor(LinearLayout parent) {
        this.parent = parent;
        root = ViewGroup.inflate(parent.getContext(), R.layout.decimal_editor,null);
        name = root.findViewById(R.id.decimal_name);
        value = root.findViewById(R.id.decimal_edit);
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
                double d = Double.parseDouble( value.getText().toString().trim());
                if(field.getType() == double.class){
                    field.setDouble(object,d);
                }else if(field.getType()==float.class){
                    field.setFloat(object,(float)d);
                }
                value.setText(""+d);
            }catch (Exception e){
                e.printStackTrace();
                value.setText("0.0");
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
