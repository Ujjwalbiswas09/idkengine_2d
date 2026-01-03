package engine.framework;

import android.content.res.AssetFileDescriptor;

import java.io.InputStream;

public interface AssetFileProvider {
    public AssetFileDescriptor getFileDescriptor(String path);
    public long getLength(String path);
    public boolean exist(String path);
    public InputStream getInputStream(String path);
    public String[] list(String path);
    public boolean isFile(String path);
}
