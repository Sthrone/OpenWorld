package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import entities.AnimatedPlayer;
import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;
import shaders.TerrainShader;
import skeletalAnimation.AnimatedModel;
import skeletalAnimation.AnimatedModelRenderer;
import skybox.SkyboxRenderer;
import terrain.Terrain;

public class MasterRenderer 
{
	private static final float FOV = 70.0f;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000.0f;
	
	/*
	private static final float RED = 0.5444f;
	private static final float GREEN = 0.62f;
	private static final float BLUE = 0.69f;
	*/
	
	public static final float RED = 0.87f;
	public static final float GREEN = 0.87f;
	public static final float BLUE = 0.82f;

	private Matrix4f projectionMatrix;
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	private TerrainShader terrainShader = new TerrainShader();
	private TerrainRenderer terrainRenderer;

	private SkyboxRenderer skyboxRenderer;
	
	private AnimatedModelRenderer animRenderer;
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private AnimatedPlayer player;

	public MasterRenderer(Loader loader)
	{
		EnableCulling();
		
		CreateProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		animRenderer = new AnimatedModelRenderer();
	}
	
	public void RenderScene(List<Entity> entities, List<Terrain> terrains, List<Light> lights, AnimatedPlayer player, Camera camera, Vector4f clipPlane)
	{
		/*
		
		Entity lampEntity = new Entity(lamp, 3, new Vector3f(293, -6.8f, -305), 0, 0, 0, 1);
		entities.add(lampEntity);
		
		Light lampLight = new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f));
		lights.add(lampLight);
		
		Vector3f terrainPoint = picker.getCurrentTerrainPoint();
		if (terrainPoint != null)
		{
			lampEntity.setPosition(terrainPoint);
			lampLight.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y + 14, terrainPoint.z));
		}
		
		*/
		
		for (Terrain terrain: terrains)
			ProcessTerrain(terrain);
		
		for (Entity entity: entities)
			ProcessEntity(entity);
		
		this.player = player;
			
		Render(lights, camera, clipPlane);
	}
	
	public void Render(List<Light> lights, Camera camera, Vector4f clipPlane)
	{
		Prepare();
		
		shader.Start();
		shader.LoadClipPlane(clipPlane);
		shader.LoadSkyColor(RED, GREEN, BLUE);
		shader.LoadLights(lights);
		shader.LoadViewMatrix(camera);
		renderer.Render(entities);
		shader.Stop();
		
		terrainShader.Start();
		terrainShader.LoadClipPlane(clipPlane);
		terrainShader.LoadSkyColor(RED, GREEN, BLUE);
		terrainShader.LoadLights(lights);
		terrainShader.LoadViewMatrix(camera);
		terrainRenderer.Render(terrains);
		terrainShader.Stop();
		
		skyboxRenderer.Render(camera, RED, GREEN, BLUE);
		
		animRenderer.Render(player, camera, lights.get(0).getPosition());
		
		entities.clear();
		terrains.clear();
	}
	
	public void ProcessEntity(Entity entity)
	{
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		
		if (batch != null)
			batch.add(entity);
		else
		{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}
	
	public void ProcessTerrain(Terrain terrain)
	{
		terrains.add(terrain);
	}
	
	public void Prepare()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1f);
	}
	
	public void CleanUp()
	{
		shader.CleanUp();
		terrainShader.CleanUp();
	}
	
	private void CreateProjectionMatrix()
	{
		float aspectRatio = (float)Display.getWidth() / (float)Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
	
	public static void EnableCulling()
	{
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void DisableCulling()
	{
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
}
