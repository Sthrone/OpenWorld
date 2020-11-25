package guis;

import org.lwjgl.util.vector.Matrix4f;

import shaders.ShaderProgram;

public class GuiShader extends ShaderProgram 
{
	private static final String VERTEX_FILE = "/guis/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "/guis/guiFragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_shape;
	private int location_aspect;
	
	public GuiShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void GetAllUniformLocations()
	{
		location_transformationMatrix = super.GetUniformLocation("transformationMatrix");
		location_shape = super.GetUniformLocation("shape");
		location_aspect = super.GetUniformLocation("aspect");
	}
	
	public void LoadTransformation(Matrix4f matrix)
	{
		super.LoadMatrix(location_transformationMatrix, matrix);
	}
	
	public void LoadShape(int shape)
	{
		super.LoadInt(location_shape, shape);
	}
	
	public void LoadAspect(float aspect)
	{
		super.LoadFloat(location_aspect, aspect);
	}

	@Override
	protected void BindAttributes() 
	{
		super.BindAttribute(0, "position");
	}
}
