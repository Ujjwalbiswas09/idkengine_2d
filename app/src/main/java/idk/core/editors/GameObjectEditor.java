package idk.core.editors;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import engine.framework.GameObject;
import engine.framework.ObjectModifier;
import idk.core.engine.HomeActivity;
import idk.core.engine.R;
import idk.core.engine.drawers.ClassList;

public class GameObjectEditor {
    private Activity activity;
    private GameObject gameObject;
    public GameObjectEditor(Activity act){
        activity = act;
    }
    private View view;

    public View getView() {
        return view;
    }

    public void setTarget(GameObject tar){
        gameObject = tar;
        view = ViewGroup.inflate(activity, R.layout.object_editor_panel,null);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        EditText editText = view.findViewById(R.id.object_name_panel);
        CheckBox checkBox  = view.findViewById(R.id.object_state_panel);
        editText.setText(gameObject.name);
        checkBox.setChecked(gameObject.active);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    gameObject.name = editText.getText().toString();
                }
                return true;
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                gameObject.active = b;
            }
        });

        showModi();
        view.findViewById(R.id.add_modifier).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showClassList();
            }
        });
    }
    private AlertDialog dialog;
    private void showModi(){
        LinearLayout layout = view.findViewById(R.id.container_for_modi);
        layout.removeAllViews();
        VectorEditor position = new VectorEditor(layout,"position");
        position.setTarget(gameObject.position);

        //layout.addView(position.getView());

        VectorEditor rot = new VectorEditor(layout,"rotation");
        rot.setTarget(gameObject.rotation);
        //layout.addView(rot.getView());

        VectorEditor scale = new VectorEditor(layout,"scale");
        scale.setTarget(gameObject.scale);
        ObjectModifier[] modifiers = gameObject.getModifiers();
        for(ObjectModifier md : modifiers){
            ModifierContainer container = new ModifierContainer(activity,layout,md);
            new ObjectEditor(container.getLn()).setTarget(md,md.getClass().getSimpleName());
        }
    }
    private void showClassList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.getCurrent());
        ClassList classList = ClassList.getInstance(null);
        classList.setClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Class cls = (Class) classList.getItem(i);
                gameObject.targetScene.targetEngineInstance.engineQueue(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            gameObject.addModifier((ObjectModifier) cls.newInstance());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                dialog.dismiss();
                showModi();
            }
        });
        builder.setView(classList.getListView());
        builder.setCancelable(true);
        dialog  = builder.create();
        dialog.show();
    }
}
