package entities;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import terrain.Terrain;

public class EntityCollision 
{
	private static List<Entity> entities;
	
	public static void Init(List<Entity> staticEntities)
	{
		entities = staticEntities;
	}
	
	// Vraca true ako igrac zeli da stupi negde gde je to zabranjeno. Vraca false za slobodno kretanje.
	public static boolean CheckForCollision(Vector3f playerPos, float radius)
	{
		if (CheckForWorldEdge(playerPos))
			return true;
		
		for (Entity entity: entities)
		{
			if (!entity.isCollidable())
				continue;
			
			Vector3f entityPos = entity.getPosition();
			
			if ((entityPos.x - playerPos.x) * (entityPos.x - playerPos.x) +
				(entityPos.z - playerPos.z) * (entityPos.z - playerPos.z) <=
				(radius + entity.getRadius()) * (radius + entity.getRadius()))
			{
				//System.out.println("COLLISION!");
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean CheckForWorldEdge(Vector3f pos)
	{
		if (pos.x <= Terrain.SIZE_MAX_X && pos.x >= Terrain.SIZE_MIN_X &&
			pos.z <= Terrain.SIZE_MAX_Z && pos.z >= Terrain.SIZE_MIN_Z)
			return false;
		
		return true;
	}
	
	public static boolean CheckBoxCollision(Vector3f playerPos, float radius, Entity box)
	{
		Vector3f entityPos = box.getPosition();
		
		if ((entityPos.x - playerPos.x) * (entityPos.x - playerPos.x) +
			(entityPos.z - playerPos.z) * (entityPos.z - playerPos.z) <=
			(radius + box.getRadius()) * (radius + box.getRadius()))
		{
			//System.out.println("COLLISION!");
			return true;
		}
	
		return false;
	}
	
}
