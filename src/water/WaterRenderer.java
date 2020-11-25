package water;

import java.util.List;

import models.RawModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import toolbox.Maths;
import entities.Camera;
import entities.Light;

public class WaterRenderer 
{
	private static final String DUDV_MAP = "waterDUDV";
	private static final String NORMAL_MAP = "normalMap";
	private static final float WAVE_SPEED = 0.03f;
	
	private RawModel quad;
	private WaterShader shader;
	private WaterFrameBuffers fbos;
	
	private float moveFactor = 0;
	private int dudvTexture;
	private int mormalMap;
	
	public WaterRenderer(Loader loader, WaterShader shader, Matrix4f projectionMatrix, WaterFrameBuffers fbos) 
	{
		this.shader = shader;
		this.fbos = fbos;
		this.dudvTexture = loader.LoadTexture(DUDV_MAP);
		this.mormalMap = loader.LoadTexture(NORMAL_MAP);
		
		shader.Start();
		shader.ConnectTextureUnits();
		shader.LoadProjectionMatrix(projectionMatrix);
		shader.Stop();
		
		SetUpVAO(loader);
	}

	public void Render(List<WaterTile> water, Camera camera, Light sun, boolean simpleWater) 
	{
		PrepareRender(camera, sun);
		
		for (WaterTile tile : water) 
		{
			Matrix4f modelMatrix = Maths.CreateTransformationMatrix(new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0f, 0f, 0f, tile.getSize());
			shader.LoadModelMatrix(modelMatrix);
			shader.LoadSimpleWater(simpleWater);
			
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
		}
		
		Unbind();
	}
	
	private void PrepareRender(Camera camera, Light sun)
	{
		shader.Start();
		shader.LoadViewMatrix(camera);
		
		moveFactor += WAVE_SPEED * DisplayManager.GetFrameTimeSeconds();
		moveFactor %= 1;
		shader.LoadMoveFactor(moveFactor);
		shader.LoadLight(sun);
		
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.GetReflectionTexture());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.GetRefractionTexture());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.dudvTexture);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mormalMap);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.GetRefractionDepthTexture());
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void Unbind()
	{
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.Stop();
	}

	private void SetUpVAO(Loader loader) 
	{
		// Just x and z vectex positions here, y is set to 0 in v.shader
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		quad = loader.LoadToVAO(vertices, 2);
	}
}
