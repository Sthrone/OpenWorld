package skeletalAnimation;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

public class Joint 
{
	public final int index;
	public final String name;
	public final List<Joint> children = new ArrayList<Joint>();
	
	private Matrix4f animatedTransform = new Matrix4f();
	
	private final Matrix4f localBindTranform;
	private Matrix4f inverseBindTransform = new Matrix4f();
	
	public Joint(int index, String name, Matrix4f bindLocalTransform)
	{
		this.index = index;
		this.name = name;
		this.localBindTranform = bindLocalTransform;
	}
	
	public void AddChild(Joint child)
	{
		this.children.add(child);
	}
	
	protected void CalcInverseBindTransform(Matrix4f parentBindTransform)
	{
		Matrix4f bindTransform = Matrix4f.mul(parentBindTransform, localBindTranform, null);
		Matrix4f.invert(bindTransform, inverseBindTransform);
		
		for (Joint child: children)
			child.CalcInverseBindTransform(bindTransform);
	}

	public Matrix4f getAnimatedTransform() {
		return animatedTransform;
	}

	public void setAnimatedTransform(Matrix4f animatedTransform) {
		this.animatedTransform = animatedTransform;
	}

	public Matrix4f getInverseBindTransform() {
		return inverseBindTransform;
	}
}
