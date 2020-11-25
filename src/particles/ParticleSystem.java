package particles;

import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;

public class ParticleSystem 
{	
	private float pps;
	private float speed;
	private float gravityComplient;
	private float lifeLength;
	
	private ParticleTexture texture;
	
	public ParticleSystem(float pps, float speed, float gravityComplient, float lifeLength) 
	{
		this.pps = pps;
		this.speed = speed;
		this.gravityComplient = gravityComplient;
		this.lifeLength = lifeLength;
	}
	
	public ParticleSystem(ParticleTexture texture, float pps, float speed, float gravityComplient, float lifeLength) 
	{
		this.texture = texture;
		this.pps = pps;
		this.speed = speed;
		this.gravityComplient = gravityComplient;
		this.lifeLength = lifeLength;
	}
	
	public void GenerateParticles(Vector3f systemCenter)
	{
		float delta = DisplayManager.GetFrameTimeSeconds();
		float particlesToCreate = pps * delta;
		int count = (int) Math.floor(particlesToCreate);
		float partialParticle = particlesToCreate % 1;
		
		for(int i=0; i<count; i++)
		{
			EmitParticle(systemCenter);
		}
		
		if(Math.random() < partialParticle)
		{
			EmitParticle(systemCenter);
		}
	}
	
	private void EmitParticle(Vector3f center)
	{
		float dirX = (float) Math.random() * 2f - 1f;
		float dirZ = (float) Math.random() * 2f - 1f;
		Vector3f velocity = new Vector3f(dirX, 1, dirZ);
		velocity.normalise();
		velocity.scale(speed);
		
		new Particle(texture, new Vector3f(center.x, center.y + 10f, center.z), velocity, gravityComplient, lifeLength, 0, 4);
	}
}
