package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import audio.AudioMaster;
import audio.Source;
import renderEngine.DisplayManager;
import skeletalAnimation.AnimatedModel;
import skeletalAnimation.AnimatedModelLoader;
import skeletalAnimation.Animation;
import skeletalAnimation.AnimationLoader;
import terrain.Terrain;
import toolbox.MyFile;

public class AnimatedPlayer 
{
	private static final String RES_FOLDER = "collada";
	private static final String MODEL_FILE = "model.dae";
	private static final String ANIM_FILE = "model.dae";
	private static final String DIFFUSE_FILE = "diffuse.png";
	private static final float RUN_SPEED = 40;
	private static final float NITRO = 80;
	private static final float TURN_SPEED = 160;
	public static final float GRAVITY = -80;
	private static final float JUMP_POWER = 40;
	public static final float PLAYER_RADIUS = 5;
	
	private AnimatedModel player;
	private Animation animation;
	private Source soundSource;
	private int soundBuffer;
	
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private boolean isInAir = false;

	
	public AnimatedPlayer(Vector3f position, float rotX, float rotY, float rotZ, float scale) 
	{
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	
		player = AnimatedModelLoader.LoadEntity(new MyFile(RES_FOLDER, MODEL_FILE), new MyFile(RES_FOLDER, DIFFUSE_FILE));
		animation = AnimationLoader.loadAnimation(new MyFile(RES_FOLDER, ANIM_FILE));
		player.setPosition(position);
		player.DoAnimation(animation);
		
		this.soundBuffer = AudioMaster.LoadSound("audio/jump.wav");
		soundSource = new Source();
		soundSource.SetVolume(0.2f);
	}
	
	public void Move(Terrain terrain)
	{
		CheckInputs();
		
		IncreaseRotation(0, currentTurnSpeed * DisplayManager.GetFrameTimeSeconds(), 0);
		
		float distance = currentSpeed * DisplayManager.GetFrameTimeSeconds();
		float dx = (float) (distance * Math.sin(Math.toRadians(this.rotY)));
		float dz = (float) (distance * Math.cos(Math.toRadians(this.rotY)));
		
		if (!EntityCollision.CheckForCollision(new Vector3f(position.x + dx, position.y, position.z + dz), PLAYER_RADIUS))
			IncreasePosition(dx, 0, dz);
		
		upwardsSpeed += GRAVITY * DisplayManager.GetFrameTimeSeconds();
		IncreasePosition(0, upwardsSpeed * DisplayManager.GetFrameTimeSeconds(), 0);
		
		float terrainHeight = terrain.GetHeightOfTerrain(position.x, position.z);
		if (position.y < terrainHeight)
		{
			upwardsSpeed = 0;
			isInAir = false;
			position.y = terrainHeight;
		}
	}
	
	private void CheckInputs()
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			this.currentSpeed = NITRO;
			player.DoAnimation(animation);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			this.currentSpeed = RUN_SPEED;
			player.DoAnimation(animation);
		}
		else
		{
			this.currentSpeed = 0;
			player.StopAnimation();
		}
			
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			this.currentTurnSpeed = -TURN_SPEED;
		else if (Keyboard.isKeyDown(Keyboard.KEY_A))
			this.currentTurnSpeed = TURN_SPEED;
		else 
			this.currentTurnSpeed = 0;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			Jump();
		}
	}
	
	private void Jump()
	{
		if (!isInAir)
		{
			this.upwardsSpeed = JUMP_POWER;
			isInAir = true;
			
			soundSource.Play(soundBuffer);
		}
	}
	
	public String toString()
	{
		return "(" + getPosition().x + ", " + getPosition().y + ", " + getPosition().z + ")";
	}
	
	//----------------------------------------------------------------
	
	public void IncreasePosition(float dx, float dy, float dz)
	{
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	
	public void IncreaseRotation(float dx, float dy, float dz)
	{
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}
	
	//-------------------------------------------------------

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public AnimatedModel getPlayer() {
		return player;
	}
}
