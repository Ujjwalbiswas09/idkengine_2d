package engine.core;

import engine.internal.graphics.ShaderProgram;

public class SimpleShader extends ShaderProgram {
    public static final String vert =
            "attribute vec3 aPosition;\n" +
            "attribute vec2 aCoord;\n"+
            "uniform mat4 uProjection;\n" +
            "uniform mat4 uView;\n" +
            "uniform mat4 uModel;\n" +
            "varying vec2 uCoord;\n"+
            "void main() {\n" +
            "uCoord = aCoord;\n" +
            " gl_Position = uProjection * uView * uModel * vec4(aPosition, 1.0);"+
            "\n}\n";
    public static final String frag = "precision mediump float;\n" +
            "varying vec2 uCoord;\n" +
            "uniform sampler2D uTexture;\n"+
            "uniform vec4 color;\n"+
            "void main() {\n" +
            "gl_FragColor = texture2D(uTexture, vec2(uCoord.x,1.0 - uCoord.y) ) * color;"+
            "\n}\n";
    public SimpleShader(){
        super(vert,frag);
    }
}
