package fontRendering;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;

public class FontRenderer 
{
	private FontShader shader;

	public FontRenderer() 
	{
		shader = new FontShader();
	}
	
	public void Render(Map<FontType, List<GUIText>> texts)
	{
		Prepare();
		
		for (FontType font: texts.keySet())
		{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
			
			for (GUIText text: texts.get(font))
			{
				RenderText(text);
			}
		}
		
		EndRendering();
	}

	public void CleanUp()
	{
		shader.CleanUp();
	}
	
	private void Prepare()
	{
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		shader.Start();
	}
	
	private void RenderText(GUIText text)
	{
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		shader.LoadColor(text.getColour());
		shader.LoadTranslation(text.getPosition());
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES,  0,  text.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}
	
	private void EndRendering()
	{
		shader.Stop();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

}
