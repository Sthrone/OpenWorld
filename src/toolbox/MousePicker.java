package toolbox;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;

public class MousePicker 
{
	private Vector3f currentRay;
	
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Camera camera;
	
	public MousePicker(Camera cam, Matrix4f projection)
	{
		this.currentRay = new Vector3f();
		
		this.camera = cam;
		this.projectionMatrix = projection;
		this.viewMatrix = Maths.CreateViewMatrix(camera);
	}
	
	public void Update()
	{
		viewMatrix = Maths.CreateViewMatrix(camera);
		currentRay = CalculateMouseRay();
	}
	
	private Vector3f CalculateMouseRay()
	{
		float mouseX = Mouse.getX();
		float mouseY = Mouse.getY();
		
		Vector2f normalizedCoords = GetNormalizedDeviceCoords(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1, 1);
		Vector4f eyeCoords = ToEyeCoords(clipCoords);
		Vector3f worldRay = ToWorldCoords(eyeCoords);
		
		return worldRay;
	}
	
	private Vector2f GetNormalizedDeviceCoords(float mouseX, float mouseY)
	{
		float x = (2 * mouseX) / Display.getWidth() - 1;
		float y = (2 * mouseY) / Display.getHeight() - 1;
		
		return new Vector2f(x, y);
	}
	
	private Vector4f ToEyeCoords(Vector4f clipCoords)
	{
		Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
		Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
		
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1, 0);
	}
	
	private Vector3f ToWorldCoords(Vector4f eyeCoords)
	{
		Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
		Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		
		return mouseRay;
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	public Camera getCamera() {
		return camera;
	}
}
