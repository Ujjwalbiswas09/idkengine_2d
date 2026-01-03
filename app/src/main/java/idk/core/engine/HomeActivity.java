package idk.core.engine;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import engine.core.Camera;
import engine.core.GraphicInterface;
import engine.core.ObjectRenderer;
import engine.core.PhysicsInterface;
import engine.core.RigidBody;
import engine.framework.GameObject;
import engine.framework.Scene;
import engine.framework.SceneInterface;
import engine.framework.SceneParser;
import engine.internal.math.Vector3;
import idk.core.editors.GameObjectEditor;
import idk.core.engine.drawers.FileManager;
import idk.core.engine.drawers.InterfaceAdapter;
import idk.core.engine.drawers.LogCatView;
import idk.core.engine.drawers.ObjectAdapter;

public class HomeActivity extends Activity implements View.OnDragListener, SeekBar.OnSeekBarChangeListener {

    public EditorInstance engineInstance;
    public Camera camera;
    private ObjectAdapter objectAdapter;
    private LinearLayout root_layout;
    private LinearLayout root_container;
    public String path;
    public static HomeActivity current;
    private FrameLayout layout;
    private LinearLayout buttom_container;
    private View currentView;
    private LogCatView logCatView;
    private FileManager fileManager;
    public String project;
    private InterfaceAdapter interfaceAdapter;
    public Scene scene;
    public DemonThread demonThread;
    public static HomeActivity getCurrent() {
        return current;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current = this;
        demonThread = DemonThread.getInstance();
        path = getIntent().getStringExtra("path");
        project = getIntent().getStringExtra("project");

        logCatView = new LogCatView(this);
        fileManager = new FileManager(this,project+"/assets"){
            @Override
            public void onFileSelect(File file) {

            }

            @Override
            public boolean filter(File file) {
                return true;
            }
        };
        engineInstance = new EditorInstance(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getDecorView().setPadding(0, getStatusBarHeight(), 0, 0);
        }
        setContentView(R.layout.main_editor);
        layout = findViewById(R.id.frame);
        layout.setOnDragListener(this);
        layout.addView(engineInstance.getView());
        setUpCursor();
        engineInstance.engineQueue(new Runnable() {
            @Override
            public void run() {
                SceneParser sceneParser = new SceneParser();
                try {
                 scene = sceneParser.load(new FileInputStream(path));
                engineInstance.addScene(scene);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onScene(scene);
                    }
                });

                }catch (Exception e){
                    e.printStackTrace();

                    scene = new Scene();
                    scene.addInterface(new GraphicInterface());
                    scene.addInterface(new PhysicsInterface());

                    GameObject gameObject = new GameObject();
                    gameObject.addModifier(new ObjectRenderer("cube.obj","default.png"));
                    gameObject.addModifier(new RigidBody());
                    scene.addObject(gameObject);

                    gameObject = new GameObject();
                    // gameObject.position.z = ;
                    camera = new Camera();
                    gameObject.addModifier(camera);
                    scene.addObject(gameObject);

                    GameObject object = new GameObject();
                    object.addModifier(new ObjectRenderer("cube.obj","libgdx.png"));
                    object.position.x = 5f;
                    scene.addObject(object);
                    engineInstance.addScene(scene);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(HomeActivity.this,"Fall Back",2).show();
                            onScene(scene);
                        }
                    });

                }

            }
        });
        buttom_container = findViewById(R.id.editor_bottom);
        root_layout = findViewById(R.id.container_root);
        root_container = findViewById(R.id.container_main);
        root_container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        findViewById(R.id.container_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeContainer();
            }
        });

        findViewById(R.id.editor_console).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentView != logCatView.getView()) {
                    setButtom_container(logCatView.getView());
                }
            }
        });
        findViewById(R.id.editor_objects).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentView != objectAdapter.getListView()) {
                    setButtom_container(objectAdapter.getListView());
                }else {
                    objectAdapter.refresh();
                    objectAdapter.listView.invalidateViews();
                }
            }
        });
        findViewById(R.id.editor_files).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentView != fileManager.getListView()) {
                    setButtom_container(fileManager.getListView());
                }
            }
        });
        findViewById(R.id.editor_interfaces).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentView != interfaceAdapter.getListView()) {
                    setButtom_container(interfaceAdapter.getListView());
                }
            }
        });
        findViewById(R.id.view_scene).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("project",project);
                intent.putExtra("file",path);
                intent.setClass(getApplicationContext(), PreviewActivity.class);
                startActivity(intent);
            }
        });

    }
    public void addContainer(View viw){
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        root_container.setVisibility(VISIBLE);
        root_layout.addView(viw,layoutParams);
    }
    public void closeContainer(){
        root_layout.removeAllViews();
        root_container.setVisibility(GONE);
        System.gc();
    }
    public void onScene(Scene c){
        objectAdapter = new ObjectAdapter(this, c) {
            @Override
            public void onObjectSelect(GameObject gameObject) {
               engineInstance.putValue("selectedObject",gameObject);
            }
            @Override
            public void onObjectEdit(GameObject gameObject) {
                GameObjectEditor gameObjectEditor = new GameObjectEditor(HomeActivity.this);
                gameObjectEditor.setTarget(gameObject);
                addContainer(gameObjectEditor.getView());
            }
        };
        interfaceAdapter = new InterfaceAdapter(this,c) {
            @Override
            public void onInterfaceEdit(SceneInterface sceneInterface) {
                System.out.println(sceneInterface.getClass().getSimpleName());
            }
        };
        setButtom_container(objectAdapter.getListView());
        objectAdapter.refresh();
        Button btn = findViewById(R.id.add_object);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c.targetEngineInstance.engineQueue(new Runnable() {
                    @Override
                    public void run() {
                        c.addObject(new GameObject());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                objectAdapter.refresh();
                            }
                        });
                    }
                });
            }
        });

        Button save = findViewById(R.id.save_scene);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SceneParser parser = new SceneParser();
                    parser.store(engineInstance.getScene(0),new FileOutputStream(path));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode,event);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    public void setButtom_container(View view){
        buttom_container.removeAllViews();
        System.gc();
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        buttom_container.addView(view,params);
        currentView = view;
    }

    public int getStatusBarHeight() {
        int result = 0;
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onBackPressed() {

        if(root_container.getVisibility()==VISIBLE){
            closeContainer();
        }else if(currentView == fileManager.getListView()){
            if(!fileManager.getPath().equals(project)){
                fileManager.refresh(fileManager.getParentPath());
            }
        }else {
            super.onBackPressed();
        }
    }

    private View Cursor;
    private SeekBar seek_x;
    private SeekBar seek_y;
    private SeekBar seek_z;
    private void setUpCursor(){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Cursor = ViewGroup.inflate(this,R.layout.transform_object,null);
        layout.addView(Cursor,params);
        seek_x = Cursor.findViewById(R.id.seek_x_value);
        seek_y = Cursor.findViewById(R.id.seek_y_value);
        seek_z = Cursor.findViewById(R.id.seek_z_value);
        seek_x.setOnSeekBarChangeListener(this);
        seek_y.setOnSeekBarChangeListener(this);
        seek_z.setOnSeekBarChangeListener(this);
    }
    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {

        if(dragEvent.getAction() == DragEvent.ACTION_DROP){
            //System.out.println("Drag Event "+dragEvent.toString());
            if(dragEvent.getLocalState() != null){
                File file = (File) dragEvent.getLocalState();
                if(file.isFile()){
                    String name = file.getName().toLowerCase();
                    if(name.endsWith(".obj")){
                        System.out.println("New OBJECT");
                        scene.targetEngineInstance.engineQueue(new Runnable() {
                            @Override
                            public void run() {
                                ObjectRenderer renderer = new ObjectRenderer(getProjectPath(file),null);
                                GameObject gameObject = new GameObject();
                                gameObject.addModifier(renderer);
                                scene.addObject(gameObject);
                            }
                        });
                    }else if(name.endsWith(".png") || name.endsWith(".jpg")|| name.endsWith(".jpeg")){
                        System.out.println("New TEXTURE");
                        scene.targetEngineInstance.engineQueue(new Runnable() {
                            @Override
                            public void run() {
                                GameObject object = (GameObject) engineInstance.getObject("selectedObject");
                                if(object != null){
                                    List<ObjectRenderer> objectRendererList = object.getModifier(ObjectRenderer.class);
                                    if(!objectRendererList.isEmpty()){
                                        ObjectRenderer renderer = objectRendererList.get(0);
                                        renderer.onDataReceived("defaultTexture",getProjectPath(file));
                                    }
                                }
                            }
                        });

                    }
                }
            }
        }
        return true;
    }
    private String getProjectPath(File file){
        return file.getAbsolutePath().substring((project+"/assets").length());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        GameObject object = (GameObject) engineInstance.getObject("selectedObject");
        if(seek_x.getProgress()==500&&seek_y.getProgress()==500 &&seek_z.getProgress()==500){
            return;
        }
        if(object !=null){
            Camera camera1 = (Camera) engineInstance.getObject("editorCamera");
            float  dih = camera1.targetObject.position.distance(realPos);
            dih *= 0.5d;
         object.position.y = realPos.y + ((seek_y.getProgress()-500) *
                 (0.002f* dih));
            object.position.x = realPos.x + ((seek_x.getProgress()-500) *
                    (0.002f* dih));
            object.position.z = realPos.z + ((seek_z.getProgress()-500) *
                    (0.002f* dih));
        }
    }
    private Vector3 realPos = new Vector3();
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        GameObject object = (GameObject) engineInstance.getObject("selectedObject");
        if(object !=null){
            realPos.set(object.position);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        seek_x.setProgress(500);
        seek_y.setProgress(500);
        seek_z.setProgress(500);
    }
}