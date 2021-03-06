package guis;

import org.lwjgl.util.vector.Vector2f;

public class GuiTexture 
{
	private int texture;
	private Vector2f position;
	private Vector2f scale;
	private float rotate;
	private int shape;			// 0 - quad, 1 - circle
	
	public GuiTexture(int texture, Vector2f position, Vector2f scale, int shape) 
	{
		super();
		this.texture = texture;
		this.position = position;
		this.scale = scale;
		this.shape = shape;
		this.rotate = 0.0f;
	}
	
	public GuiTexture(int texture, Vector2f position, Vector2f scale, float rotate, int shape) 
	{
		super();
		this.texture = texture;
		this.position = position;
		this.scale = scale;
		this.rotate = rotate;
		this.shape = shape;
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getScale() {
		return scale;
	}

	public int getShape() {
		return shape;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public void setScale(Vector2f scale) {
		this.scale = scale;
	}

	public float getRotate() {
		return rotate;
	}

	public void setRotate(float rotate) {
		this.rotate = rotate;
	}
}
