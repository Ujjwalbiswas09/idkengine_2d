package idk.core.editors;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import engine.framework.EngineQuery;
import engine.framework.Flags;
import engine.internal.math.Color;
import engine.internal.math.Vector2;
import engine.internal.math.Vector3;
import engine.internal.math.Vector4;
import idk.core.engine.R;

public class ObjectEditor implements View.OnClickListener {

    private View root;
    private LinearLayout parent;
    private LinearLayout rt;
    private TextView expand;
    private Object tar;
    private TextView nam;
    public ObjectEditor(LinearLayout linearLayout){
        parent = linearLayout;
        root = ViewGroup.inflate(parent.getContext(), R.layout.object_editor,null);
        rt = root.findViewById(R.id.object_root);
        expand = root.findViewById(R.id.object_expand);
        nam = root.findViewById(R.id.object_editor_name);
        expand.setOnClickListener(this);
        parent.addView(root,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }
    public void setTarget(Object object,String name) {
        if (object == null) {
            return;
        }
        nam.setText(name);
        tar = object;
    }
    private void setUp(){
        List<Field> fieldList = new ArrayList<>();
        Class<?> cls = tar.getClass();
        for(Field fld : cls.getFields()){
            if(!fieldList.contains(fld)){
                fieldList.add(fld);
            }
        }
        for(Field fld : cls.getDeclaredFields()){
            if(!fieldList.contains(fld)){
                fieldList.add(fld);
            }
        }
        for(Field fld : fieldList){
            parseField(fld);
        }
    }

    private void parseField(Field fld){
        int mode = fld.getModifiers();
        if(Modifier.isFinal(mode) || Modifier.isStatic(mode)){
            return;
        }
        boolean bol = Modifier.isPublic(mode);
        if(fld.isAnnotationPresent(Flags.class)){
            Flags flags = fld.getAnnotation(Flags.class);
            if(flags.flag() != null){
            for(String val : flags.flag()){
              if (val.equals("VISIBLE")){
                  bol = true;
              } else if (val.equals("INVISIBLE")){
                  return;
              }
            }}
        }
        if(!bol){
            return;
        }

        Class<?> type = fld.getType();
        fld.setAccessible(true);
        if(type.isPrimitive()){
            if(type == int.class ||type == long.class ||type == short.class ||type == byte.class ||type == char.class ){
                new NumberEditor(rt).setTarget(fld,tar);
            }
            if(type == double.class||type == float.class){
                new DecimalEditor(rt).setTarget(fld,tar);
            }
            if(type == boolean.class){
                new BooleanEditor(rt).setTarget(fld,tar);
            }
        }else if(type == String.class) {
            if(fld.isAnnotationPresent(EngineQuery.class)) {
                EngineQuery engineQuery = fld.getAnnotation(EngineQuery.class);
                if (engineQuery.name().equals("file")) {
                    String value = engineQuery.value();
                    if(value.equals("texture")){
                        new TexturePicker(rt).setTarget(fld,tar);
                    }else if (value.equals("mesh")){
                        new FilePicker(rt,new String[]{".obj"}).setTarget(fld,tar);
                    }else if(value.equals("all")){
                        new FilePicker(rt,null).setTarget(fld,tar);
                    }else if(value.startsWith("\\.")){
                        String[] arr = new String[]{value};
                        if(value.contains(",")){
                            arr = value.split(",");
                        }
                        new FilePicker(rt,arr).setTarget(fld,tar);
                    }else {
                        new TextEditor(rt).setTarget(fld, tar);
                    }
                }else {
                    new TextEditor(rt).setTarget(fld, tar);
                }
            }else {
                new TextEditor(rt).setTarget(fld, tar);
            }
        }else if(Vector4.class.isAssignableFrom(type)|| Vector3.class.isAssignableFrom(type)|| Vector2.class.isAssignableFrom(type)){
            try {
                Object object = fld.get(tar);
                if(object !=null) {
                    new VectorEditor(rt,fld.getName()).setTarget(object);
                }
            }catch (Exception e){

            }
        }else if(Map.class.isAssignableFrom(type)){
            //not implemented
        }else if(Collection.class.isAssignableFrom(type)){
            //not implemented
        }else if(type.isArray()){
            //not implemented
        }else if(type.isEnum()){
           new OptionEditor(rt).setTarget(tar,fld);
        }else if(type == Color.class) {
            try {
                Color color = (Color) fld.get(tar);
                if(color==null) {
                    return;
                }
                new ColorPicker(rt).setTarget(color);
            }catch (Exception e) {
            }
        }else {
                try {
                    if (fld.get(tar) != null) {
                        new ObjectEditor(rt).setTarget(fld.get(tar), fld.getName());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
    }
    public void close(){
        parent.removeView(root);
    }

    @Override
    public void onClick(View v) {
        if(rt.getVisibility() == View.GONE){
            rt.setVisibility(View.VISIBLE);
            expand.setText("Collapse");
            setUp();
        }else {
            rt.removeAllViews();
            rt.setVisibility(View.GONE);
            expand.setText("Expand");
        }
    }
}
