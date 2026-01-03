package idk.core.editors;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import engine.framework.ObjectModifier;
import idk.core.engine.R;

public class ModifierContainer {
    private final View root;
    private LinearLayout ln;
    private TextView  delete;
    private LinearLayout parent;
    public ModifierContainer(Context context, LinearLayout par, ObjectModifier mod){
        root = ViewGroup.inflate(context, R.layout.modifier_container,null);
        ln = root.findViewById(R.id.modifier_container_ln);
        parent = par;
        delete = root.findViewById(R.id.modifier_remove_img);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viw) {
                viw.setVisibility(GONE);
                root.findViewById(R.id.modifier_delete_root).setVisibility(VISIBLE);
                root.findViewById(R.id.modifier_delete_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      parent.removeView(root);
                      mod.targetObject.targetScene.targetEngineInstance.engineQueue(new Runnable() {
                          @Override
                          public void run() {
                              mod.targetObject.removeModifier(mod);
                          }
                      });

                    }
                });
                root.findViewById(R.id.modifier_delete_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       delete.setVisibility(VISIBLE);
                        root.findViewById(R.id.modifier_delete_root).setVisibility(GONE);
                    }
                });

            }
        });
        parent.addView(root);
    }

    public LinearLayout getLn() {
        return ln;
    }

    public View getView() {
        return root;
    }
}
