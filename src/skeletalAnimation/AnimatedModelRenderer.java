package skeletalAnimation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.AnimatedPlayer;
import entities.Camera;
import entities.Entity;
import toolbox.Maths;
import toolbox.OpenGlUtils;

/**
 * 
 * This class deals with rendering an animated entity. Nothing particularly new
 * here. The only exciting part is that the joint transforms get loaded up to
 * the shader in a uniform array.
 * 
 * @author Karl
 *
 */
public class AnimatedModelRenderer 
{
	private AnimatedModelShader shader;

	/**
	 * Initializes the shader program used for rendering animated models.
	 */
	public AnimatedModelRenderer() 
	{
		this.shader = new AnimatedModelShader();
	}

	/**
	 * Renders an animated entity. The main thing to note here is that all the
	 * joint transforms are loaded up to the shader to a uniform array. Also 5
	 * attributes of the VAO are enabled before rendering, to include joint
	 * indices and weights.
	 * 
	 * @param entity
	 *            - the animated entity to be rendered.
	 * @param camera
	 *            - the camera used to render the entity.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	public void Render(AnimatedPlayer player, Camera camera, Vector3f lightDir) 
	{
		Prepare(player, camera, lightDir);
		
		player.getPlayer().getTexture().BindToUnit(0);
		player.getPlayer().getModel().Bind(0, 1, 2, 3, 4);
		
		shader.jointTransforms.loadMatrixArray(player.getPlayer().GetJointTranforms());
		GL11.glDrawElements(GL11.GL_TRIANGLES, player.getPlayer().getModel().getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		
		player.getPlayer().getModel().Unbind(0, 1, 2, 3, 4);
		
		Finish();
	}

	/**
	 * Deletes the shader program when the game closes.
	 */
	public void CleanUp() 
	{
		shader.cleanUp();
	}

	/**
	 * Starts the shader program and loads up the projection view matrix, as
	 * well as the light direction. Enables and disables a few settings which
	 * should be pretty self-explanatory.
	 * 
	 * @param camera
	 *            - the camera being used.
	 * @param lightDir
	 *            - the direction of the light in the scene.
	 */
	private void Prepare(AnimatedPlayer player, Camera camera, Vector3f lightDir) 
	{
		shader.start();
		shader.projectionViewMatrix.loadMatrix(camera.GetProjectionViewMatrix());
		shader.lightDirection.loadVec3(lightDir);
		PrepareInstance(player);
		
		OpenGlUtils.Antialias(true);
		OpenGlUtils.DisableBlending();
		OpenGlUtils.EnableDepthTesting(true);
	}
	
	private void PrepareInstance(AnimatedPlayer player)
	{
		Matrix4f transformationMatrix = Maths.CreateTransformationMatrix(player.getPosition(), player.getRotX(), player.getRotY(), player.getRotZ(), player.getScale());
		shader.tranformationMatrix.loadMatrix(transformationMatrix);
		//shader.LoadOffset(entity.GetTextureXOffset(), entity.GetTextureYOffset());
	}

	/**
	 * Stops the shader program after rendering the entity.
	 */
	private void Finish() 
	{
		shader.stop();
	}

}
