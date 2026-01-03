package engine.internal.math;

public class Color {
    public float r = 0;
    public float b =0;
    public float g =0;
    public float a = 1;
    public Color(){

    }

    public Color(float r,float g,float b){
        this.r = r;
        this.g = g;
        this.b = b;
    }
    public Color(float r,float g,float b,float a){
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    public Color mix(Color bs){
        return new Color((r+bs.r)*0.5f,(g+bs.g)*0.5f,(b+bs.b)*0.5f,(a+bs.a)*0.5f);
    }
    public float[] toArray(){
        return new float[]{r,g,b,a};
    }// Basic colors
    public static Color WHITE() { return new Color(1.0f, 1.0f, 1.0f, 1.0f); }
    public static Color BLACK() { return new Color(0.0f, 0.0f, 0.0f, 1.0f); }
    public static Color RED() { return new Color(1.0f, 0.0f, 0.0f, 1.0f); }
    public static Color GREEN() { return new Color(0.0f, 1.0f, 0.0f, 1.0f); }
    public static Color BLUE() { return new Color(0.0f, 0.0f, 1.0f, 1.0f); }
    public static Color YELLOW() { return new Color(1.0f, 1.0f, 0.0f, 1.0f); }
    public static Color CYAN() { return new Color(0.0f, 1.0f, 1.0f, 1.0f); }
    public static Color MAGENTA() { return new Color(1.0f, 0.0f, 1.0f, 1.0f); }
    public static Color GRAY() { return new Color(0.5f, 0.5f, 0.5f, 1.0f); }
    public static Color TRANSPARENT() { return new Color(0.0f, 0.0f, 0.0f, 0.0f); }

    // Additional shades and common colors
    public static Color DARK_GRAY() { return new Color(0.25f, 0.25f, 0.25f, 1.0f); }
    public static Color LIGHT_GRAY() { return new Color(0.75f, 0.75f, 0.75f, 1.0f); }
    public static Color ORANGE() { return new Color(1.0f, 0.5f, 0.0f, 1.0f); }
    public static Color PURPLE() { return new Color(0.5f, 0.0f, 1.0f, 1.0f); }
    public static Color PINK() { return new Color(1.0f, 0.75f, 0.8f, 1.0f); }
    public static Color BROWN() { return new Color(0.6f, 0.4f, 0.2f, 1.0f); }
    public static Color LIME() { return new Color(0.75f, 0.1f, 0.0f, 1.0f); }
    public static Color TEAL() { return new Color(0.0f, 0.5f, 0.5f, 1.0f); }
    public static Color INDIGO() { return new Color(0.3f, 0.0f, 0.5f, 1.0f); }
    public static Color VIOLET() { return new Color(0.93f, 0.51f, 0.93f, 1.0f); }

    // Pastels and softer tones
    public static Color PASTEL_RED() { return new Color(1.0f, 0.41f, 0.38f, 1.0f); }
    public static Color PASTEL_GREEN() { return new Color(0.47f, 0.87f, 0.47f, 1.0f); }
    public static Color PASTEL_BLUE() { return new Color(0.68f, 0.85f, 0.9f, 1.0f); }
    public static Color PASTEL_YELLOW() { return new Color(1.0f, 0.96f, 0.58f, 1.0f); }
    public static Color PASTEL_PURPLE() { return new Color(0.8f, 0.6f, 0.9f, 1.0f); }

    // Darker variants
    public static Color DARK_RED() { return new Color(0.55f, 0.0f, 0.0f, 1.0f); }
    public static Color DARK_GREEN() { return new Color(0.0f, 0.39f, 0.0f, 1.0f); }
    public static Color DARK_BLUE() { return new Color(0.0f, 0.0f, 0.55f, 1.0f); }
    public static Color DARK_CYAN() { return new Color(0.0f, 0.55f, 0.55f, 1.0f); }
    public static Color DARK_MAGENTA() { return new Color(0.55f, 0.0f, 0.55f, 1.0f); }
}