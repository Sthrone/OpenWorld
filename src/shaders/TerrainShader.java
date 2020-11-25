package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import toolbox.Maths;

public class TerrainShader extends ShaderProgram
{
	private static final String VERTEX_FILE = "/shaders/terrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "/shaders/terrainFragmentShader.txt";
	
	public static final int MAX_LIGHTS = 10;
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColor[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColor;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	private int location_plane;
	
	public TerrainShader()
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void BindAttributes() 
	{
		super.BindAttribute(0, "position");
		super.BindAttribute(1, "textureCoords");
		super.BindAttribute(2, "normal");
	}

	@Override
	protected void GetAllUniformLocations()
	{
		location_transformationMatrix = super.GetUniformLocation("transformationMatrix");
		location_projectionMatrix = super.GetUniformLocation("projectionMatrix");
		location_viewMatrix = super.GetUniformLocation("viewMatrix");
		location_shineDamper = super.GetUniformLocation("shineDamper");
		location_reflectivity = super.GetUniformLocation("reflectivity");
		location_skyColor = super.GetUniformLocation("skyColor");
		location_backgroundTexture = super.GetUniformLocation("backgroundTexture");
		location_rTexture = super.GetUniformLocation("rTexture");
		location_gTexture = super.GetUniformLocation("gTexture");
		location_bTexture = super.GetUniformLocation("bTexture");
		location_blendMap = super.GetUniformLocation("blendMap");
		location_plane = super.GetUniformLocation("plane");
		
		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColor = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		
		for (int i=0; i<MAX_LIGHTS; i++)
		{
			location_lightPosition[i] = super.GetUniformLocation("lightPosition[" + i + "]");
			location_lightColor[i] = super.GetUniformLocation("lightColor[" + i + "]");
			location_attenuation[i] = super.GetUniformLocation("attenuation[" + i + "]");
		}
	}
	
	public void ConnectTextureUnits()
	{
		super.LoadInt(location_backgroundTexture, 0);
		super.LoadInt(location_rTexture, 1);
		super.LoadInt(location_gTexture, 2);
		super.LoadInt(location_bTexture, 3);
		super.LoadInt(location_blendMap, 4);
	}
	
	public void LoadClipPlane(Vector4f plane)
	{
		super.LoadVector(location_plane, plane);
	}
	
	public void LoadSkyColor(float r, float g, float b)
	{
		super.LoadVector(location_skyColor, new Vector3f(r, g, b));
	}
	
	public void LoadShineVariables(float damper, float reflectivity)
	{
		super.LoadFloat(location_shineDamper, damper);
		super.LoadFloat(location_reflectivity, reflectivity);
	}
	
	public void LoadTransformationMatrix(Matrix4f matrix)
	{
		super.LoadMatrix(location_transformationMatrix, matrix);
	}
	
	public void LoadLights(List<Light> lights)
	{
		for (int i=0; i<MAX_LIGHTS; i++)
		{
			if (i < lights.size())
			{
				super.LoadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.LoadVector(location_lightColor[i], lights.get(i).getColor());
				super.LoadVector(location_attenuation[i], lights.get(i).getAttenuation());
			}
			else
			{
				super.LoadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.LoadVector(location_lightColor[i], new Vector3f(0, 0, 0));
				super.LoadVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
	}
	
	public void LoadProjectionMatrix(Matrix4f projection)
	{
		super.LoadMatrix(location_projectionMatrix, projection);
	}
	
	public void LoadViewMatrix(Camera camera)
	{
		Matrix4f viewMatrix = Maths.CreateViewMatrix(camera);
		super.LoadMatrix(location_viewMatrix, viewMatrix);
	}
}
