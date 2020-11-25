package textures;

import toolbox.MyFile;

public class TextureBuilder 
{	
	private boolean clampEdges = false;
	private boolean mipmap = false;
	private boolean anisotropic = true;
	private boolean nearest = false;
	
	private MyFile file;
	
	protected TextureBuilder(MyFile textureFile)
	{
		this.file = textureFile;
	}
	
	public Texture Create()
	{
		TextureData textureData = TextureUtils.DecodeTextureFile(file);
		int textureId = TextureUtils.LoadTextureToOpenGL(textureData, this);
		return new Texture(textureId, textureData.getWidth());
	}
	
	public TextureBuilder ClampEdges()
	{
		this.clampEdges = true;
		return this;
	}
	
	public TextureBuilder NormalMipMap()
	{
		this.mipmap = true;
		this.anisotropic = false;
		return this;
	}
	
	public TextureBuilder NearestFiltering()
	{
		this.mipmap = false;
		this.anisotropic = false;
		this.nearest = true;
		return this;
	}
	
	public TextureBuilder Anisotropic()
	{
		this.mipmap = true;
		this.anisotropic = true;
		return this;
	}
	
	protected boolean isClampEdges() {
		return clampEdges;
	}

	protected boolean isMipmap() {
		return mipmap;
	}

	protected boolean isAnisotropic() {
		return anisotropic;
	}

	protected boolean isNearest() {
		return nearest;
	}
}
