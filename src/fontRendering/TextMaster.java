package fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontMeshCreator.TextMeshData;
import renderEngine.Loader;

public class TextMaster 
{
	private static Loader loader;
	private static Map<FontType, List<GUIText>> texts = new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;
	
	public static void Init(Loader theLoader)
	{
		renderer = new FontRenderer();
		loader = theLoader;
	}
	
	public static void Render()
	{
		renderer.Render(texts);
	}
	
	public static void LoadText(GUIText text)
	{
		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);
		
		int vao = loader.LoadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());
		
		List<GUIText> textBatch = texts.get(font);
		if (textBatch == null)
		{
			textBatch = new ArrayList<GUIText>();
			texts.put(font, textBatch);
		}
		
		textBatch.add(text);
	}
	
	public static void RemoveText(GUIText text)
	{
		List<GUIText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);
		
		if (textBatch.isEmpty())
		{
			texts.remove(text.getFont());
		}
	}
	
	public static void CleanUp()
	{
		renderer.CleanUp();
	}
}
