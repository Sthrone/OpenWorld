package particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Player;
import renderEngine.DisplayManager;

public class Particle 
{
	private Vector3f position;
	private Vector3f velocity;
	private float gravityEffect;
	private float lifeLength;
	private float rotation;
	private float scale;
	
	private ParticleTexture texture;
	private Vector2f texOffset1 = new Vector2f();
	private Vector2f texOffset2 = new Vector2f();
	private float blend;
	private float distanceFromCamera;
	
	private float elapsedTime = 0;

	public Particle(Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale) 
	{
		super();
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		
		ParticleMaster.AddParticle(this);
	}
	
	public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale) 
	{
		super();
		this.texture = texture;
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		
		ParticleMaster.AddParticle(this);
	}
	
	// Vraca da li je cestica i dalje ziva.
	protected boolean Update(Camera camera)
	{
		velocity.y += Player.GRAVITY * gravityEffect * DisplayManager.GetFrameTimeSeconds();
		
		Vector3f change = new Vector3f(velocity);
		change.scale(DisplayManager.GetFrameTimeSeconds());
		Vector3f.add(change, position, position);
		
		distanceFromCamera = Vector3f.sub(camera.getPosition(), position, null).lengthSquared();
		
		UpdateTextureCoordInfo();
		
		elapsedTime += DisplayManager.GetFrameTimeSeconds();
		
		return elapsedTime < lifeLength;
	}
	
	private void UpdateTextureCoordInfo()
	{
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
		float atlasProgression = lifeFactor * stageCount;
		
		int index1 = (int)Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		this.blend = atlasProgression % 1;
		
		SetTextureOffset(texOffset1, index1);
		SetTextureOffset(texOffset2, index2);	
	}
	
	private void SetTextureOffset(Vector2f offset, int index)
	{
		int column = index % texture.getNumberOfRows();
		int row = index / texture.getNumberOfRows();
		
		offset.x = (float) column / texture.getNumberOfRows();
		offset.y = (float) row / texture.getNumberOfRows();
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	public ParticleTexture getTexture() {
		return texture;
	}

	public Vector2f getTexOffset1() {
		return texOffset1;
	}

	public Vector2f getTexOffset2() {
		return texOffset2;
	}

	public float getBlend() {
		return blend;
	}

	public float getDistanceFromCamera() {
		return distanceFromCamera;
	}
}
