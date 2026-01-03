package engine.framework;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AndroidFileProvider implements AssetFileProvider {
    private AssetManager assetManager;

    public AndroidFileProvider(AssetManager manager) {
        assetManager = manager;

    }

    @Override
    public AssetFileDescriptor getFileDescriptor(String path) {
        try {
            return assetManager.openFd(path);
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public long getLength(String path) {

        return 0;
    }

    @Override
    public boolean exist(String path) {
        return false;
    }

    @Override
    public InputStream getInputStream(String path) {
        return null;
    }

    @Override
    public String[] list(String path) {
        return new String[0];
    }

    @Override
    public boolean isFile(String path) {
        return false;
    }

    private class Node {

    }
}
