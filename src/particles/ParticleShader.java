package particles;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import shaders.ShaderProgram;

public class ParticleShader extends ShaderProgram 
{
	private static final String VERTEX_FILE = "/particles/particleVShader.txt";
	private static final String FRAGMENT_FILE = "/particles/particleFShader.txt";

	private int location_modelViewMatrix;
	private int location_projectionMatrix;
	private int location_texOffset1;
	private int location_texOffset2;
	private int location_texCoordInfo;

	public ParticleShader() 
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void GetAllUniformLocations() 
	{
		location_modelViewMatrix = super.GetUniformLocation("modelViewMatrix");
		location_projectionMatrix = super.GetUniformLocation("projectionMatrix");
		location_texOffset1 = super.GetUniformLocation("texOffset1");
		location_texOffset1 = super.GetUniformLocation("texOffset2");
		location_texCoordInfo = super.GetUniformLocation("texCoordInfo");
	}

	@Override
	protected void BindAttributes() 
	{
		super.BindAttribute(0, "position");
	}
	
	protected void LoadTextureCoordInfo(Vector2f offset1, Vector2f offset2, float numRows, float blend)
	{
		super.Load2DVector(location_texOffset1, offset1);
		super.Load2DVector(location_texOffset2, offset2);
		super.Load2DVector(location_texCoordInfo, new Vector2f(numRows, blend));
	}

	protected void loadModelViewMatrix(Matrix4f modelView) 
	{
		super.LoadMatrix(location_modelViewMatrix, modelView);
	}

	protected void loadProjectionMatrix(Matrix4f projectionMatrix) 
	{
		super.LoadMatrix(location_projectionMatrix, projectionMatrix);
	}
}
