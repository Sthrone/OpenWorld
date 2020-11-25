package entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera 
{	
	private Vector3f position;
	private float pitch;
	private float yaw;
	private float roll;
	
	private Player player;
	private AnimatedPlayer animPlayer;
	
	private float distanceFromPlayer = 140;
	private float angleAroundPlayer = 0;
	
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix = new Matrix4f();
	
	private static final float FOV = 70.0f;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000.0f;
	
	private static final float PLAYER_HEIGHT = 20;
	private static final float MAX_ZOOM_OUT = 416;
	private static final float MAX_ZOOM_IN = 56;
	private static final float MAX_PITCH = 90;
	private static final float MIN_PITCH = 0;
	
	
	public Camera()
	{
		position = new Vector3f(0, 0, 0);
		pitch = 20;
		yaw = 0;
		
		this.projectionMatrix = CreateProjectionMatrix();
	}
	
	public Camera(Player player)
	{
		this();
		this.player = player;
	}
	
	public Camera(AnimatedPlayer animPlayer)
	{
		this();
		this.animPlayer = animPlayer;
	}
	
	public Camera(Camera cam)
	{
		this.animPlayer = cam.animPlayer;
		this.position = new Vector3f(animPlayer.getPosition());
		this.pitch = 90;
		this.yaw = 0;
		this.angleAroundPlayer = 0;
		
		//this.player = cam.player;
		this.projectionMatrix = CreateProjectionMatrix();
		this.Move();
		
		this.yaw = 0;
		this.position.y = 450;
	}
	
	public void Move()
	{
		CalculateZoom();
		CalculatePitch();
		CalculateAngleAroundPlayer();
		
		float horizontalDistance = CalculateHorizontalDistance();
		float verticalDistance = CalculateVerticalDistance();
		CalculateCameraPosition(horizontalDistance, verticalDistance);
		
		//System.out.println("Rot: " + animPlayer.getRotY() + ", Ang: " + angleAroundPlayer);
		
		this.yaw = 180 - (animPlayer.getRotY() + angleAroundPlayer);
		yaw %= 360;
		
		UpdateViewMatrix();
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}
	
	//------------------------------------------------------
	
	private float CalculateHorizontalDistance()
	{
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private float CalculateVerticalDistance()
	{
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void CalculateCameraPosition(float horizDistance, float vertDistance)
	{
		float theta = animPlayer.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		
		position.x = animPlayer.getPosition().x - offsetX;
		position.z = animPlayer.getPosition().z - offsetZ;
		position.y = animPlayer.getPosition().y + vertDistance;
	}
	
	//------------------------------------------------------
	
	private void CalculateZoom()
	{
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
		
		if (distanceFromPlayer > MAX_ZOOM_OUT)
			distanceFromPlayer = MAX_ZOOM_OUT;
		else if (distanceFromPlayer < MAX_ZOOM_IN)
			distanceFromPlayer = MAX_ZOOM_IN;
		
		//System.out.println(distanceFromPlayer);
	}
	
	private void CalculatePitch()
	{
		if (Mouse.isButtonDown(1))
		{
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
			
			if(pitch < MIN_PITCH)
				pitch = 0;
			else if(pitch > MAX_PITCH)
				pitch = 90;
		}
	}
	
	private void CalculateAngleAroundPlayer()
	{
		if (Mouse.isButtonDown(0))
		{
			float angleChange = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= angleChange;
		}
	}
	
	public void InvertPitch()
	{
		this.pitch = -pitch;
	}
	
	//------------------------------------------------------
	
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Matrix4f GetProjectionViewMatrix() 
	{
		return Matrix4f.mul(projectionMatrix, viewMatrix, null);
	}

	private void UpdateViewMatrix() 
	{
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f negativeCameraPos = new Vector3f(-position.x, -position.y, -position.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
	}

	private Matrix4f CreateProjectionMatrix() 
	{
		Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
		return projectionMatrix;
	}
	
}
