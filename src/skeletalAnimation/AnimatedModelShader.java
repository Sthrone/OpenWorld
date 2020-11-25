package skeletalAnimation;

import toolbox.*;

public class AnimatedModelShader extends ShaderProgram 
{
	private static final int MAX_JOINTS = 50; // max number of joints in a skeleton
	private static final int DIFFUSE_TEX_UNIT = 0;

	private static final MyFile VERTEX_SHADER = new MyFile("skeletalAnimation", "animatedEntityVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("skeletalAnimation", "animatedEntityFragment.glsl");

	protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
	protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
	protected UniformMat4Array jointTransforms = new UniformMat4Array("jointTransforms", MAX_JOINTS);
	protected UniformSampler diffuseMap = new UniformSampler("diffuseMap");
	protected UniformMatrix tranformationMatrix = new UniformMatrix("transformationMatrix");

	/**
	 * Creates the shader program for the {@link AnimatedModelRenderer} by
	 * loading up the vertex and fragment shader code files. It also gets the
	 * location of all the specified uniform variables, and also indicates that
	 * the diffuse texture will be sampled from texture unit 0.
	 */
	public AnimatedModelShader() 
	{
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position", "in_textureCoords", "in_normal", "in_jointIndices", "in_weights");
		super.StoreAllUniformLocations(projectionViewMatrix, diffuseMap, lightDirection, jointTransforms, tranformationMatrix);
		ConnectTextureUnits();
	}

	/**
	 * Indicates which texture unit the diffuse texture should be sampled from.
	 */
	private void ConnectTextureUnits() 
	{
		super.start();
		diffuseMap.loadTexUnit(DIFFUSE_TEX_UNIT);
		super.stop();
	}
}
