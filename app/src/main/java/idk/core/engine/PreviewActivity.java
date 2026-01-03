package idk.core.engine;

import android.app.Activity;
import android.os.Bundle;

import java.io.FileInputStream;

import engine.framework.Scene;
import engine.framework.SceneParser;
import idk.core.engine.drawers.PreviewInstance;

public class PreviewActivity extends Activity {

    private PreviewInstance previewInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String file = getIntent().getStringExtra("file");
        previewInstance = new PreviewInstance(this);
        setContentView(previewInstance.getView());
        SceneParser sceneParser = new SceneParser();
        try {
            Scene scene = sceneParser.load(new FileInputStream(file));
            previewInstance.engineQueue(new Runnable() {
                @Override
                public void run() {
                    previewInstance.addScene(scene);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}