package models;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Vao 
{	
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_INT = 4;
	public final int id;
	
	private List<Vbo> dataVbos = new ArrayList<Vbo>();
	private Vbo indexVbo;
	private int indexCount;

	public static Vao Create() 
	{
		int id = GL30.glGenVertexArrays();
		return new Vao(id);
	}

	private Vao(int id) 
	{
		this.id = id;
	}
	
	public int getIndexCount()
	{
		return indexCount;
	}

	public void Bind(int... attributes)
	{
		Bind();
		for (int i : attributes) 
		{
			GL20.glEnableVertexAttribArray(i);
		}
	}

	public void Unbind(int... attributes)
	{
		for (int i : attributes) 
		{
			GL20.glDisableVertexAttribArray(i);
		}
		
		Unbind();
	}
	
	public void CreateIndexBuffer(int[] indices)
	{
		this.indexVbo = Vbo.Create(GL15.GL_ELEMENT_ARRAY_BUFFER);
		indexVbo.Bind();
		indexVbo.StoreData(indices);
		this.indexCount = indices.length;
	}

	public void CreateAttribute(int attribute, float[] data, int attrSize)
	{
		Vbo dataVbo = Vbo.Create(GL15.GL_ARRAY_BUFFER);
		dataVbo.Bind();
		dataVbo.StoreData(data);
		GL20.glVertexAttribPointer(attribute, attrSize, GL11.GL_FLOAT, false, attrSize * BYTES_PER_FLOAT, 0);
		dataVbo.Unbind();
		dataVbos.add(dataVbo);
	}
	
	public void CreateIntAttribute(int attribute, int[] data, int attrSize)
	{
		Vbo dataVbo = Vbo.Create(GL15.GL_ARRAY_BUFFER);
		dataVbo.Bind();
		dataVbo.StoreData(data);
		GL30.glVertexAttribIPointer(attribute, attrSize, GL11.GL_INT, attrSize * BYTES_PER_INT, 0);
		dataVbo.Unbind();
		dataVbos.add(dataVbo);
	}
	
	public void Delete() 
	{
		GL30.glDeleteVertexArrays(id);
		for(Vbo vbo : dataVbos)
		{
			vbo.Delete();
		}
		
		indexVbo.Delete();
	}

	private void Bind()
	{
		GL30.glBindVertexArray(id);
	}

	private void Unbind() 
	{
		GL30.glBindVertexArray(0);
	}
}
