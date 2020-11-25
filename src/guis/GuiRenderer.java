package guis;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import models.RawModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import toolbox.Maths;

public class GuiRenderer 
{
	private final RawModel quad;
	private final RawModel circle;
	private GuiShader shader;
	
	public GuiRenderer(Loader loader)
	{
		float[] positions;
		
		positions = GenerateQuad();
		quad = loader.LoadToVAO(positions, 2);
		
		positions = GenerateCircle();
		circle = loader.LoadToVAO(positions, 2);
		
		shader = new GuiShader();
	}
	
	public void Render(List<GuiTexture> guis)
	{
		shader.Start();
		
		/*
		GL20.glEnableVertexAttribArray(0);
		GL30.glBindVertexArray(quad.getVaoID());
		*/
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		for (GuiTexture gui : guis)
		{
			int vertexCount = SetVao(gui.getShape());
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			
			Matrix4f matrix;
			if (gui.getRotate() > 0.0)
				matrix = Maths.CreateTransformationMatrix(gui.getPosition(), gui.getScale(), gui.getRotate());
			else
				matrix = Maths.CreateTransformationMatrix(gui.getPosition(), gui.getScale());
			
			shader.LoadTransformation(matrix);
			shader.LoadShape(gui.getShape());
			shader.LoadAspect((float) DisplayManager.HEIGHT / (float) DisplayManager.WIDTH);
			
			GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, vertexCount);
			
			RemoveVao();
		}
		
		/*
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		*/
	
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		
		shader.Stop();
	}
	
	public void CleanUp()
	{
		shader.CleanUp();
	}
	
	private float[] GenerateQuad()
	{
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		return positions;
	}
	
	private float[] GenerateCircle()
	{
		int circlePoints = 100;
		double alpha = 2 * Math.PI / (float) circlePoints;
	
		float[] positions = new float[2 * (circlePoints + 1)];
		positions[0] = 0.0f;
		positions[1] = 0.0f;
		
		for (int i = 0; i < circlePoints+1; i++)
		{
			double angle = i * alpha;
			positions[2 * i] = (float)Math.cos(angle);
			positions[2 * i + 1] = (float)Math.sin(angle);
		}
		
		return positions;
	}
	
	private int SetVao(int shape)
	{
		int n = 0;
		
		if (shape == 0)
		{
			GL30.glBindVertexArray(quad.getVaoID());
			n = quad.getVertexCount();
		}
		else if (shape == 1)
		{
			GL30.glBindVertexArray(circle.getVaoID());
			n = circle.getVertexCount();
		}
		
		GL20.glEnableVertexAttribArray(0);
		
		return n;
	}
	
	private void RemoveVao()
	{
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
}
