package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import toolbox.Maths;

public class StaticShader extends ShaderProgram 
{
	private static final String VERTEX_FILE = "/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "/shaders/fragmentShader.txt";
	
	private static final int MAX_LIGHTS = 10;
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColor[];
	private int location_attenuation[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColor;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	
	public StaticShader()
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
		location_useFakeLighting = super.GetUniformLocation("useFakeLighting");
		location_skyColor = super.GetUniformLocation("skyColor");
		location_numberOfRows = super.GetUniformLocation("numberOfRows");
		location_offset = super.GetUniformLocation("offset");
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
	
	public void LoadClipPlane(Vector4f plane)
	{
		super.LoadVector(location_plane, plane);
	}
	
	public void LoadOffset(float x, float y)
	{
		super.Load2DVector(location_offset, new Vector2f(x, y));
	}
	
	public void LoadNumberOfRows(int numberOfRows)
	{
		super.LoadFloat(location_numberOfRows, numberOfRows);
	}
	
	public void LoadSkyColor(float r, float g, float b)
	{
		super.LoadVector(location_skyColor, new Vector3f(r, g, b));
	}
	
	public void LoadFakeLightingVariable(boolean useFake)
	{
		super.LoadBoolean(location_useFakeLighting, useFake);
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
