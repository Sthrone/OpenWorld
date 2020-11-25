package particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;
import renderEngine.Loader;

public class ParticleMaster 
{
	private static Map<ParticleTexture, List<Particle>> particles = new HashMap<ParticleTexture, List<Particle>>();
	private static ParticleRenderer renderer;
	
	public static void Init(Loader loader, Matrix4f projectionMatrix)
	{
		renderer = new ParticleRenderer(loader, projectionMatrix);
	}
	
	public static void Update(Camera camera)
	{
		Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
		while (mapIterator.hasNext())
		{
			List<Particle> list = mapIterator.next().getValue();
			Iterator<Particle> iterator = list.iterator();
			
			while (iterator.hasNext())
			{
				Particle p = iterator.next();
				boolean stillAlive = p.Update(camera);
				
				if (!stillAlive)
				{
					iterator.remove();
					if (list.isEmpty())
					{
						mapIterator.remove();
					}
				}
			}
			
			InsertionSort.SortHighToLow(list);
		}
	}
	
	public static void RenderParticles(Camera camera)
	{
		renderer.Render(particles, camera);
	}
	
	public static void CleanUp()
	{
		renderer.CleanUp();
	}
	
	public static void AddParticle(Particle particle)
	{
		List<Particle> list = particles.get(particle.getTexture());
		
		if (list == null)
		{
			list = new ArrayList<Particle>();
			particles.put(particle.getTexture(), list);
		}
		
		list.add(particle);
	}
}
