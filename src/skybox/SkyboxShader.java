package skybox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import renderEngine.DisplayManager;
import shaders.ShaderProgram;
import toolbox.Maths;

public class SkyboxShader extends ShaderProgram
{
	private static final String VERTEX_FILE = "/skybox/skyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "/skybox/skyboxFragmentShader.txt";
	
	private static final float ROTATE_SPEED = 1f;
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColor;
	private int location_cubeMap1;
	private int location_cubeMap2;
	private int location_blendFactor;
	
	private float rotation = 0;
	
	public SkyboxShader() 
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void GetAllUniformLocations() 
	{
		location_projectionMatrix = super.GetUniformLocation("projectionMatrix");
		location_viewMatrix = super.GetUniformLocation("viewMatrix");
		location_fogColor = super.GetUniformLocation("fogColor");
		location_cubeMap1 = super.GetUniformLocation("cubeMap1");
		location_cubeMap2 = super.GetUniformLocation("cubeMap2");
		location_blendFactor = super.GetUniformLocation("blendFactor");
	}
	
	public void LoadBlendFactor(float blend)
	{
		super.LoadFloat(location_blendFactor, blend);
	}
	
	public void ConnectTextureUnits()
	{
		super.LoadInt(location_cubeMap1, 0);
		super.LoadInt(location_cubeMap2, 1);
	}
	
	public void LoadFogColor(float r, float g, float b)
	{
		super.LoadVector(location_fogColor, new Vector3f(r, g, b));
	}
	
	public void LoadViewMatrix(Camera camera)
	{
		Matrix4f matrix = Maths.CreateViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		
		rotation += ROTATE_SPEED * DisplayManager.GetFrameTimeSeconds();
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 1, 0), matrix, matrix);
		
		super.LoadMatrix(location_viewMatrix, matrix);
	}
	
	public void LoadProjectionMatrix(Matrix4f matrix)
	{
		super.LoadMatrix(location_projectionMatrix, matrix);
	}
	
	@Override
	protected void BindAttributes() 
	{
		super.BindAttribute(0, "position");
	}
}
