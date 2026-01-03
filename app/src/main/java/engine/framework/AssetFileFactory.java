package engine.framework;

public class AssetFileFactory {
    private static AssetFileProvider provider;
    public static void setProvider(AssetFileProvider provider) {
        provider = provider;
    }
    public static AssetFileProvider getProvider() {
        return provider;
    }
}
