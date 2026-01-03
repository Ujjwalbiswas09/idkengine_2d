package engine.core;


import engine.internal.graphics.ShaderProgram;

public class UIShaderProgram extends ShaderProgram
{	
private static final String fragmentCode =
"precision mediump float;\n"+
"uniform sampler2D texture;\n uniform int hasTexture;\n"+
"uniform vec4 color;\n uniform int cut;\n"+
"varying vec2 v_coord;\n void main(){\n"+
"vec4 clr = vec4(1.0,1.0,1.0,1.0);\n"+
"if(hasTexture == 1){\n clr = texture2D(texture,v_coord);\n}\n"+
"if(cut==1){\nif(clr.a == 0.0){\n discard; \n} \n}\n"+
"gl_FragColor = color * clr; \n}";
	private static final String vertexCode =
	"attribute vec3 pos;\n attribute vec2 coord;\n"+
	"uniform vec3 position;\n uniform vec2 scale;\n"+
	"varying vec2 v_coord; \nvoid main(){\n"+
	"v_coord = coord;\n vec3 p = (pos * vec3(scale,1.0))+position;\n"
	+"\n gl_Position = vec4(p,1.0); \n}";
public UIShaderProgram(){
	super(vertexCode,fragmentCode);
	}
}
