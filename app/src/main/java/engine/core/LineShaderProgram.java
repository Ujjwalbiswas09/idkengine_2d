package engine.core;


import engine.internal.graphics.ShaderProgram;

public class LineShaderProgram extends ShaderProgram {
    private static final String vs = "attribute vec4 position;\n" +
            "attribute vec3 aC;\n"+
            "uniform mat4 projectionMatrix;\n" +
            "uniform mat4 viewMatrix;\n" +
            "uniform mat4 modelMatrix;\n" +
            "varying vec4 color;\n"+
            "void main(){\n" +
            "color = vec4(aC.x,aC.y,aC.z,1.0);\n"+
            "gl_Position = projectionMatrix * viewMatrix * modelMatrix * position;\n" +
            "}";
    public static final String fs = "precision mediump float;\n" +
            "varying vec4 color;\n" +
            "uniform vec4 offset;"+
            "void main(){\n" +
            "gl_FragColor = color + offset;\n"+
            "\n}";
    public LineShaderProgram() {
        super(vs,fs);
    }

}
