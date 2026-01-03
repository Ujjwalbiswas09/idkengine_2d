package engine.core;


import engine.internal.graphics.ShaderProgram;

public class SpriteShaderProgram extends ShaderProgram {
    private static final String vs="attribute vec3 position;\n" +
            "attribute vec2 a_uv;\n"+
            "uniform mat4 projectionMatrix;\n" +
            "uniform mat4 viewMatrix;\n" +
            "uniform mat4 modelMatrix;\n" +
            "varying vec2 v_uv;\n"+
            "void main(){\n" +
            "v_uv = a_uv;\n"+
            "gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(position,1.0);\n" +
            "}";
    public static final String fs = "precision mediump float;\n" +
            "uniform vec4 color;\n" +
            "uniform sampler2D tex;\n"+
            "varying vec2 v_uv;\n"+
            "uniform int uv_change;\n"+
            "uniform vec2 offset;\n"+
            "uniform vec2 scale;\n"+
            "void main(){\n" +
            "vec2 uv = v_uv;\n"+
            "if(uv_change==1){\n uv = offset+ (scale * v_uv); \n}\n"+
            "vec4 tc = texture2D(tex,uv);\n"+
            "if(tc.a < 0.1){ discard; }\n"+
            "gl_FragColor = vec4(1.0,1.0,1.0,1.0);\n" +
            "}";


    public SpriteShaderProgram() {
        super(vs,fs);
    }
}
