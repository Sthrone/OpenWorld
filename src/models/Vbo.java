package models;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

public class Vbo 
{	
	private final int vboId;
	private final int type;
	
	private Vbo(int vboId, int type)
	{
		this.vboId = vboId;
		this.type = type;
	}
	
	public static Vbo Create(int type)
	{
		int id = GL15.glGenBuffers();
		return new Vbo(id, type);
	}
	
	public void Bind()
	{
		GL15.glBindBuffer(type, vboId);
	}
	
	public void Unbind()
	{
		GL15.glBindBuffer(type, 0);
	}
	
	public void StoreData(float[] data) 
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		StoreData(buffer);
	}

	public void StoreData(int[] data)
	{
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		StoreData(buffer);
	}
	
	public void StoreData(IntBuffer data)
	{
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
	}
	
	public void StoreData(FloatBuffer data)
	{
		GL15.glBufferData(type, data, GL15.GL_STATIC_DRAW);
	}

	public void Delete()
	{
		GL15.glDeleteBuffers(vboId);
	}
}
