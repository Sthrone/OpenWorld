package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import terrain.Terrain;

public class MouseTerrainIntersection extends MousePicker 
{
	private static final int RECURSION_COUNT = 200;
	private static final float RAY_RANGE = 600;
	
	private Terrain terrain;
	private Vector3f currentTerrainPoint;

	public MouseTerrainIntersection(Camera cam, Matrix4f projection, Terrain terrain) 
	{
		super(cam, projection);
		
		this.terrain = terrain;
	}
	
	public void Update()
	{
		super.Update();
		
		if (IntersectionInRange(0, RAY_RANGE, super.getCurrentRay()))
			currentTerrainPoint = BinarySearch(0, 0, RAY_RANGE, super.getCurrentRay());
		else
			currentTerrainPoint = null;
	}
	
	private boolean IntersectionInRange(float start, float finish, Vector3f ray) 
	{
		Vector3f startPoint = GetPointOnRay(ray, start);
		Vector3f endPoint = GetPointOnRay(ray, finish);
		
		if (!IsUnderGround(startPoint) && IsUnderGround(endPoint))
			return true;
		else 
			return false;
	}
	
	private Vector3f GetPointOnRay(Vector3f ray, float distance) 
	{
		Vector3f camPos = super.getCamera().getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		
		return Vector3f.add(start, scaledRay, null);
	}
	
	private boolean IsUnderGround(Vector3f testPoint) 
	{
		Terrain terrain = GetTerrain(testPoint.getX(), testPoint.getZ());
		
		float height = 0;
		if (terrain != null)
			height = terrain.GetHeightOfTerrain(testPoint.getX(), testPoint.getZ());
		
		if (testPoint.y < height)
			return true;
		else
			return false;
	}
	
	private Terrain GetTerrain(float worldX, float worldZ)
	{
		return terrain;
	}
	
	private Vector3f BinarySearch(int count, float start, float finish, Vector3f ray) 
	{
		float half = start + ((finish - start) / 2f);
		
		if (count >= RECURSION_COUNT) 
		{
			Vector3f endPoint = GetPointOnRay(ray, half);
			Terrain terrain = GetTerrain(endPoint.getX(), endPoint.getZ());
			
			if (terrain != null)
				return endPoint;
			else 
				return null;
		}
		
		if (IntersectionInRange(start, half, ray))
			return BinarySearch(count + 1, start, half, ray);
		else
			return BinarySearch(count + 1, half, finish, ray);
	}

	public Vector3f getCurrentTerrainPoint() {
		return currentTerrainPoint;
	}
}
