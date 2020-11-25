package water;

import org.lwjgl.util.vector.Matrix4f;
import shaders.ShaderProgram;
import toolbox.Maths;
import entities.Camera;
import entities.Light;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "/water/waterVertex.txt";
	private final static String FRAGMENT_FILE = "/water/waterFragment.txt";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_reflectionTexture;
	private int location_refractionTexture;
	private int location_dudvMap;
	private int location_moveFactor;
	private int location_cameraPosition;
	private int location_normalMap;
	private int location_lightPosition;
	private int location_lightColor;
	private int location_depthMap;
	private int location_simpleWater;
	
	public WaterShader() 
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void BindAttributes() 
	{
		BindAttribute(0, "position");
	}

	@Override
	protected void GetAllUniformLocations() 
	{
		location_projectionMatrix = GetUniformLocation("projectionMatrix");
		location_viewMatrix = GetUniformLocation("viewMatrix");
		location_modelMatrix = GetUniformLocation("modelMatrix");
		location_reflectionTexture = GetUniformLocation("reflectionTexture");
		location_refractionTexture = GetUniformLocation("refractionTexture");
		location_dudvMap = GetUniformLocation("dudvMap");
		location_moveFactor = GetUniformLocation("moveFactor");
		location_cameraPosition = GetUniformLocation("cameraPosition");
		location_normalMap = GetUniformLocation("normalMap");
		location_lightPosition = GetUniformLocation("lightPosition");
		location_lightColor = GetUniformLocation("lightColor");
		location_depthMap = GetUniformLocation("depthMap");
		location_simpleWater = GetUniformLocation("simpleWater");
	}
	
	public void ConnectTextureUnits()
	{
		super.LoadInt(location_reflectionTexture, 0);
		super.LoadInt(location_refractionTexture, 1);
		super.LoadInt(location_dudvMap, 2);
		super.LoadInt(location_normalMap, 3);
		super.LoadInt(location_depthMap, 4);
	}
	
	public void LoadSimpleWater(boolean simple)
	{
		super.LoadBoolean(location_simpleWater, simple);
	}
	
	public void LoadLight(Light sun)
	{
		super.LoadVector(location_lightPosition, sun.getPosition());
		super.LoadVector(location_lightColor, sun.getColor());
	}
	
	public void LoadMoveFactor(float factor)
	{
		super.LoadFloat(location_moveFactor, factor);
	}

	public void LoadProjectionMatrix(Matrix4f projection) 
	{
		LoadMatrix(location_projectionMatrix, projection);
	}
	
	public void LoadViewMatrix(Camera camera)
	{
		Matrix4f viewMatrix = Maths.CreateViewMatrix(camera);
		LoadMatrix(location_viewMatrix, viewMatrix);
		
		super.LoadVector(location_cameraPosition, camera.getPosition());
	}

	public void LoadModelMatrix(Matrix4f modelMatrix)
	{
		LoadMatrix(location_modelMatrix, modelMatrix);
	}
}
