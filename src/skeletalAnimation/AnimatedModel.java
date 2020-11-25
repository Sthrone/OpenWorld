package skeletalAnimation;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import models.Vao;
import textures.Texture;

public class AnimatedModel 
{
	// Skin
	private final Vao model;
	private final Texture texture;

	// Skeleton
	private final Joint rootJoint;
	private final int jointCount;
	
	private final Animator animator;
	
	private Vector3f position;
	
	public AnimatedModel(Vao model, Texture texture, Joint rootJoint, int jointCount)
	{
		this.model = model;
		this.texture = texture;
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		this.animator = new Animator(this);
		
		// Root nema roditelja, pa mu se samo salje jedinicna matrica
		rootJoint.CalcInverseBindTransform(new Matrix4f());
	}
	
	public void DoAnimation(Animation animation)
	{
		animator.DoAnimation(animation);
	}
	
	public void Update()
	{
		animator.Update();
	}
	
	public void StopAnimation()
	{
		animator.StopAnimation();
	}
	
	public void Delete()
	{
		model.Delete();
		texture.Delete();
	}
	
	public Matrix4f[] GetJointTranforms()
	{
		Matrix4f[] jointMatrices = new Matrix4f[jointCount];
		AddJointsToArray(rootJoint, jointMatrices);
		
		return jointMatrices;
	}
	
	private void AddJointsToArray(Joint headJoint, Matrix4f[] jointMatrices)
	{
		jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
		
		for (Joint childJoint: headJoint.children)
			AddJointsToArray(childJoint, jointMatrices);
	}

	public Vao getModel() {
		return model;
	}

	public Texture getTexture() {
		return texture;
	}

	public Joint getRootJoint() {
		return rootJoint;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
}
