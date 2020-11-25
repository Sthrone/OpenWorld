package fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.ShaderProgram;

public class FontShader extends ShaderProgram
{
	private static final String VERTEX_FILE = "/fontRendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "/fontRendering/fontFragment.txt";
	
	private int location_color;
	private int location_translation;
	
	public FontShader() 
	{
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void GetAllUniformLocations() 
	{
		location_color = super.GetUniformLocation("color");
		location_translation = super.GetUniformLocation("translation");
	}

	@Override
	protected void BindAttributes() 
	{
		super.BindAttribute(0, "position");
		super.BindAttribute(1, "textureCoords");
	}
	
	protected void LoadColor(Vector3f color)
	{
		super.LoadVector(location_color, color);
	}
	
	protected void LoadTranslation(Vector2f translation)
	{
		super.Load2DVector(location_translation, translation);
	}
}
