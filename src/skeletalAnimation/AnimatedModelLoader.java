package skeletalAnimation;

import skeletalStructs.*;
import collada.ColladaLoader;
import models.Vao;
import textures.Texture;
import toolbox.MyFile;

public class AnimatedModelLoader 
{
	private static final int MAX_WEIGHTS = 3;
	
	/**
	 * Creates an AnimatedEntity from the data in an entity file. It loads up
	 * the collada model data, stores the extracted data in a VAO, sets up the
	 * joint heirarchy, and loads up the entity's texture.
	 * 
	 * @param entityFile
	 *            - the file containing the data for the entity.
	 * @return The animated entity (no animation applied though)
	 */
	public static AnimatedModel LoadEntity(MyFile modelFile, MyFile textureFile) 
	{
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, MAX_WEIGHTS);
		Vao model = CreateVao(entityData.getMeshData());
		Texture texture = LoadTexture(textureFile);
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = CreateJoints(skeletonData.headJoint);
		
		return new AnimatedModel(model, texture, headJoint, skeletonData.jointCount);
	}

	/**
	 * Loads up the diffuse texture for the model.
	 * 
	 * @param textureFile
	 *            - the texture file.
	 * @return The diffuse texture.
	 */
	private static Texture LoadTexture(MyFile textureFile) 
	{
		Texture diffuseTexture = Texture.NewTexture(textureFile).Anisotropic().Create();
		return diffuseTexture;
	}

	/**
	 * Constructs the joint-hierarchy skeleton from the data extracted from the
	 * collada file.
	 * 
	 * @param data
	 *            - the joints data from the collada file for the head joint.
	 * @return The created joint, with all its descendants added.
	 */
	private static Joint CreateJoints(JointData data) 
	{
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
		for (JointData child : data.children) 
		{
			joint.AddChild(CreateJoints(child));
		}
		
		return joint;
	}

	/**
	 * Stores the mesh data in a VAO.
	 * 
	 * @param data
	 *            - all the data about the mesh that needs to be stored in the
	 *            VAO.
	 * @return The VAO containing all the mesh data for the model.
	 */
	private static Vao CreateVao(MeshData data) 
	{
		Vao vao = Vao.Create();
		
		vao.Bind();
		
		vao.CreateIndexBuffer(data.getIndices());
		vao.CreateAttribute(0, data.getVertices(), 3);
		vao.CreateAttribute(1, data.getTextureCoords(), 2);
		vao.CreateAttribute(2, data.getNormals(), 3);
		
		// Koji zglobovi uticu na koje cvorove. Vektor sadrzi ID-eve 3 zgloba od kojih zavisi.
		vao.CreateIntAttribute(3, data.getJointIds(), 3);
		// Jacina kojom uticu. Zbir svih jacina = 1.
		vao.CreateAttribute(4, data.getVertexWeights(), 3);
		
		vao.Unbind();
		
		return vao;
	}

}
