package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import renderEngine.DisplayManager;
import terrain.Terrain;

public class Player extends Entity 
{
	private static final float RUN_SPEED = 40;
	private static final float TURN_SPEED = 160;
	public static final float GRAVITY = -50;
	private static final float JUMP_POWER = 20;
	private static final float PLAYER_RADIUS = 5;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private boolean isInAir = false;
	
	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) 
	{
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void Move(Terrain terrain)
	{
		CheckInputs();
		
		super.IncreaseRotation(0, currentTurnSpeed * DisplayManager.GetFrameTimeSeconds(), 0);
		
		float distance = currentSpeed * DisplayManager.GetFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.IncreasePosition(dx, 0, dz);
		
		upwardsSpeed += GRAVITY * DisplayManager.GetFrameTimeSeconds();
		super.IncreasePosition(0, upwardsSpeed * DisplayManager.GetFrameTimeSeconds(), 0);
		
		float terrainHeight = terrain.GetHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		if (super.getPosition().y < terrainHeight)
		{
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}
	}
	
	private void CheckInputs()
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			this.currentSpeed = RUN_SPEED;
		else if (Keyboard.isKeyDown(Keyboard.KEY_S))
			this.currentSpeed = -RUN_SPEED;
		else
			this.currentSpeed = 0;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			this.currentTurnSpeed = -TURN_SPEED;
		else if (Keyboard.isKeyDown(Keyboard.KEY_A))
			this.currentTurnSpeed = TURN_SPEED;
		else 
			this.currentTurnSpeed = 0;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			Jump();
	}
	
	private void Jump()
	{
		if (!isInAir)
		{
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}
	
	public String toString()
	{
		return "(" + getPosition().x + ", " + getPosition().y + ", " + getPosition().z + ")";
	}
}
