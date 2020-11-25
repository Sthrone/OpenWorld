package entities;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import toolbox.Maths;

public class MiniGame 
{
	private final double COLLISION_PROXIMITY = 10.0f;
	private final double ANGLE_SENSITIVITY = 10E-8;
	
	private Entity box;
	private int score;
	
	private Vector2f mapCenter = new Vector2f(0.84f, 0.68f);
	private float mapR = 0.266f;
	
	private Vector3f arrow;
	private float currentArrowAngle;
	
	public MiniGame(Entity box)
	{
		this.box = box;
		score = 0;
		
		this.arrow = new Vector3f(1.0f, 0.0f, 0.0f);
		this.currentArrowAngle = 0.0f;
	}
	
	public boolean CheckCollision(Vector3f player)
	{
		/*
		double dist = Math.sqrt
				(
						Math.pow(player.x - box.getPosition().x, 2) + 
						Math.pow(player.y + 10f - box.getPosition().y, 2) +
						Math.pow(player.z - box.getPosition().z, 2)
				);
		
		if (dist <= COLLISION_PROXIMITY)
		{
			score++;
			return true;
		}
		
		return false;
		*/
		
		if (EntityCollision.CheckBoxCollision(player, AnimatedPlayer.PLAYER_RADIUS, box))
		{
			score++;
			return true;
		}
		
		return false;
	}
	
	public Vector2f SetBoxDirection(Vector3f player)
	{	
		Vector3f direction = new Vector3f();
		Vector3f.sub(box.getPosition(), player, direction);
		
		float aspect = (float) DisplayManager.HEIGHT / (float) DisplayManager.WIDTH;
		Vector2f mapDirection = new Vector2f(direction.x, direction.z);
		mapDirection.normalise();
		mapDirection.x *= aspect;
		mapDirection.scale(mapR);

		Vector2f pos = new Vector2f();
		Vector2f.add(mapCenter, mapDirection, pos);
		return pos;
	}
	
	public float RotateArrow(Vector3f camera, Vector3f player)
	{
		Vector3f facingSide = new Vector3f();
		Vector3f.sub(player, camera, facingSide);
		facingSide.y = 0.0f;
		facingSide.normalise();
		
		float angle = (float)Maths.AngleBetween(facingSide, arrow);
		float sign = (float)Maths.AngleBetween(facingSide, new Vector3f(1,0,0));
		if (Math.abs(angle) > ANGLE_SENSITIVITY)
		{
			if (sign >= currentArrowAngle)
				currentArrowAngle += angle;
			else
				currentArrowAngle -= angle;
			
			arrow = new Vector3f(facingSide);
		}
		
		//System.out.println("Angle: " + Math.toDegrees(angle));
		//return (float)Math.toRadians(180.0);
		
		return currentArrowAngle;
	}
	
	public Vector2f GetArrowPosition(Vector2f currentPos, float newAngle)
	{
		Vector2f dir = new Vector2f(); 
		Vector2f.sub(currentPos, mapCenter, dir);
		
		float r = dir.length();
		float radAngle = (float) Math.toRadians(newAngle);
		
		float x = mapCenter.x + r * (float) Math.cos(radAngle);
		float y = mapCenter.y + r * (float) Math.sin(radAngle);
		
		return new Vector2f(x, y);
	}
	
	public float SetArrowAngle(float yaw)
	{
		float angle = yaw;
		
		if (angle < 0)
		{
			angle *= -1;
			angle %= 360;
			angle = 360 - angle;
		}
			
		return (float) Math.toRadians(angle);
	}
	

	public Entity getBox() {
		return box;
	}

	public void setBox(Entity box) {
		this.box = box;
	}

	public int getScore() {
		return score;
	}
}
