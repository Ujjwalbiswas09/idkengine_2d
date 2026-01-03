package engine.core;

import android.content.Context;

import engine.internal.graphics.ShaderProgram;
import idk.core.engine.R;

public class ObjectSelectionShader extends ShaderProgram {
    public ObjectSelectionShader(Context context){
        super("#version 300 es\n" +
                        "in vec4 position;\n" +
                        "\n" +
                        "uniform mat4 projection;\n" +
                        "uniform mat4 view;\n" +
                        "uniform mat4 model;\n" +
                        "\n" +
                        "void main(){\n" +
                        "vec4 pos = position;\n" +
                        "pos.w = 1.0;\n" +
                        "    gl_Position = projection * view * model * pos;\n" +
                        "}",
                "#version 300 es\n" +
                        "precision mediump float;\n" +
                        "precision mediump int;\n" +
                        "out vec4 fragColor;\n" +
                        "uniform float objectID;\n" +
                        "void main(){\n" +
                        "fragColor = vec4(objectID);\n" +
                        "}");
    }
}
