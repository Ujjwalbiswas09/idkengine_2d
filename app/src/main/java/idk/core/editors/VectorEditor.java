package idk.core.editors;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.javagl.obj.Obj;
import engine.internal.math.Vector2;
import engine.internal.math.Vector3;
import engine.internal.math.Vector4;
import idk.core.engine.R;

public class VectorEditor implements TextView.OnEditorActionListener {
    private LinearLayout parent;
    private Object target;
    private Class cls;
    private View view;
    private EditText x;
    private EditText y;
    private EditText z;
    private EditText w;
    private String name="";
    public VectorEditor(LinearLayout lmn,String str){
        parent = lmn;
        name = str;
    }

    public View getView() {
        return view;
    }

    public void setTarget(Object o){
        target = o;
        if(o == null){
            return;
        }
        cls = o.getClass();
        view = ViewGroup.inflate(parent.getContext(), R.layout.vector_editor,null);
        x = view.findViewById(R.id.vector_x);
        w = view.findViewById(R.id.vector_w);
        y = view.findViewById(R.id.vector_y);
        z = view.findViewById(R.id.vector_z);
        if(Vector2.class.isAssignableFrom(cls)){
            x.setVisibility(GONE);
            Vector2 vec = (Vector2) target;
            x.setText(vec.x+"");
            y.setText(vec.y+"");
        }else
        if(Vector4.class.isAssignableFrom(cls)){
            w.setVisibility(VISIBLE);
            Vector4 vec = (Vector4) target;
            x.setText(vec.x+"");
            y.setText(vec.y+"");
            z.setText(vec.z+"");
            w.setText(vec.w+"");
        }else
        if(Vector3.class.isAssignableFrom(cls)){
            Vector3 vec = (Vector3) target;
            x.setText(vec.x+"");
            y.setText(vec.y+"");
            z.setText(vec.z+"");
        }
        x.setOnEditorActionListener(this);
        y.setOnEditorActionListener(this);
        z.setOnEditorActionListener(this);
        w.setOnEditorActionListener(this);
        parent.addView(view);
        TextView rv = view.findViewById(R.id.vector_name);
        rv.setText(name);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        float vx = Float.parseFloat(x.getText().toString());
        float vw = Float.parseFloat(w.getText().toString());
        float vy = Float.parseFloat(y.getText().toString());
        float vz = Float.parseFloat(z.getText().toString());
        if(Vector2.class.isAssignableFrom(cls)){
            Vector2 vec = (Vector2) target;
            vec.set(vx,vy);
        }else if(Vector3.class.isAssignableFrom(cls)){
            Vector3 vec = (Vector3) target;
            vec.set(vx,vy,vz);
        }else if(Vector4.class.isAssignableFrom(cls)){
            Vector4 vec = (Vector4) target;
            vec.set(vx,vy,vz,vw);
        }
        return true;
    }
}
