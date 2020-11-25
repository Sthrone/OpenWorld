package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import audio.AudioMaster;
import audio.Source;
import entities.AnimatedPlayer;
import entities.Camera;
import entities.Entity;
import entities.EntityCollision;
import entities.Light;
import entities.MiniGame;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import shaders.TerrainShader;
import skeletalAnimation.AnimatedModel;
import skeletalAnimation.AnimatedModelLoader;
import skeletalAnimation.Animation;
import skeletalAnimation.AnimationLoader;
import terrain.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MouseTerrainIntersection;
import toolbox.MyFile;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class MainGameLoop 
{
	public static boolean NoWater(float x, float z, List<WaterTile> waters)
	{
		float offset = 15;
		
		for (WaterTile tile: waters)
		{
			float size = tile.getSize() - offset;
			
			if (x <= tile.getX() + size && x >= tile.getX() - size &&
		        z <= tile.getZ() + size && z >= tile.getZ() - size)
				return false;
		}
		
		return true;
	}
	
	public static Vector3f GeneratePosition(Random random, Terrain terrain, List<WaterTile> waters)
	{
		float x, y, z;
		
		do
		{
			x = random.nextFloat() * 750;
			z = random.nextFloat() * -750;
		}
		while (!NoWater(x, z, waters));	
		
		y = terrain.GetHeightOfTerrain(x, z);
		
		return new Vector3f(x, y, z);
	}
	
	//public static Camera SetMapCamera()
	
	public static void main(String[] args)
	{
		DisplayManager.CreateDisplay();
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer(loader);
		
		TextMaster.Init(loader);
		AudioMaster.Init();
		AudioMaster.SetListenerData(0, 0, 0);
		
		Random random = new Random(676452);
		
		//------------------------- TERRAIN -------------------------
		TerrainTexture backgroundTexture = new TerrainTexture(loader.LoadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.LoadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.LoadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.LoadTexture("path"));
		
		TerrainTexture blendMap = new TerrainTexture(loader.LoadTexture("blendMap"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		
		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmapNew");
		//Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap, "heightMap");
		List<Terrain> terrains = new ArrayList<Terrain>();
		terrains.add(terrain);
		
		
		//------------------------- WATER -------------------------
		WaterFrameBuffers fbos = new WaterFrameBuffers();
		WaterFrameBuffers mapBuffer = new WaterFrameBuffers();
		
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		//WaterRenderer mapWaterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), mapBuffer);
		
		List<WaterTile> waters = new ArrayList<WaterTile>();
		waters.add(new WaterTile(406, -284, 10, 87));
		waters.add(new WaterTile(138, -530, 9, 81));
		
		
		//------------------------- LIGHT -------------------------
		List<Light> lights = new ArrayList<Light>();
		int NUM_OF_LIGHTS = TerrainShader.MAX_LIGHTS;
		
		Light sun = new Light(new Vector3f(0, 1000, -1000), new Vector3f(0.9f, 0.9f, 0.9f));
		lights.add(sun);
		
		ArrayList<Vector3f> lampColorArray = new ArrayList<Vector3f>();
		lampColorArray.add(new Vector3f(2f, 0f, 0f));
		lampColorArray.add(new Vector3f(0f, 2f, 2f));
		lampColorArray.add(new Vector3f(2f, 2f, 0f));
		lampColorArray.add(new Vector3f(0f, 2f, 0f));
		
		/*
		lights.add(new Light(new Vector3f(185, 10, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(370, 17, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
		lights.add(new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
		*/
		
		
		//------------------------- ENTITIES -------------------------
		TexturedModel tree = new TexturedModel(OBJLoader.LoadObjModel("pine", loader), new ModelTexture(loader.LoadTexture("pine")));
		TexturedModel grass = new TexturedModel(OBJLoader.LoadObjModel("grassModel", loader), new ModelTexture(loader.LoadTexture("grassTexture")));
		TexturedModel flower = new TexturedModel(OBJLoader.LoadObjModel("grassModel", loader), new ModelTexture(loader.LoadTexture("flower")));
		TexturedModel bobble = new TexturedModel(OBJLoader.LoadObjModel("lowPolyTree", loader), new ModelTexture(loader.LoadTexture("lowPolyTree")));
		TexturedModel crate = new TexturedModel(OBJLoader.LoadObjModel("crate", loader), new ModelTexture(loader.LoadTexture("crate")));
		
		ModelTexture lampTextureAtlas = new ModelTexture(loader.LoadTexture("lamp"));
		lampTextureAtlas.setNumberOfRows(2);
		TexturedModel lamp = new TexturedModel(OBJLoader.LoadObjModel("lamp", loader), lampTextureAtlas);
		
		ModelTexture fernTextureAtlas = new ModelTexture(loader.LoadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(OBJLoader.LoadObjModel("fern", loader), fernTextureAtlas);
		
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		fern.getTexture().setHasTransparency(true);
		fern.getTexture().setUseFakeLighting(true);
		lamp.getTexture().setHasTransparency(true);
		lamp.getTexture().setUseFakeLighting(true);
		
		List<Entity> entities = new ArrayList<Entity>();
		int NUM_OF_ENTITIES = 500;
		
		for (int i = 0; i < NUM_OF_ENTITIES; i++)
		{
			if (i % 3 == 0)
			{
				//entities.add(new Entity(grass, GeneratePosition(random, terrain), 0, 0, 0, 1.8f));
				entities.add(new Entity(fern, random.nextInt(4), GeneratePosition(random, terrain, waters), 0, 0, 0, 0.9f, false));
			}
			
			if (i % 5 == 0)
			{
				//entities.add(new Entity(flower, GeneratePosition(random, terrain, waters), 0, 0, 0, 2.3f));
				float randScale = (random.nextFloat() * 0.1f + 1.0f) * 0.2f;
				entities.add(new Entity(bobble, GeneratePosition(random, terrain, waters), 0, random.nextFloat() * 360, 0, random.nextFloat() * 0.1f + 1.0f, 5.0f * randScale));
				
				randScale = (random.nextFloat() + random.nextFloat() + 0.6f) * 0.2f;
				entities.add(new Entity(tree, GeneratePosition(random, terrain, waters), 0, 0, 0, random.nextFloat() + random.nextFloat() + 0.6f, 5.0f * randScale));
			}
		}
		
		for (int i = 0; i < NUM_OF_LIGHTS-1; i++)
		{
			int lampCol = random.nextInt(4);
			Vector3f pos = GeneratePosition(random, terrain, waters);
			
			entities.add(new Entity(lamp, lampCol, pos, 0, 0, 0, 2, 0.2f));
			lights.add(new Light(new Vector3f(pos.x, pos.y + 30f, pos.z), lampColorArray.get(lampCol), new Vector3f(1f, 0.01f, 0.002f)));
		};
		
		/*
		entities.add(new Entity(lamp, 3, new Vector3f(185, -4.7f, -293), 0, 0, 0, 1));
		entities.add(new Entity(lamp, 3, new Vector3f(370, 4.2f, -300), 0, 0, 0, 1));
		entities.add(new Entity(lamp, 3, new Vector3f(293, -6.8f, -305), 0, 0, 0, 1));
		*/
		
		EntityCollision.Init(entities);
		
		
		//------------------------- GUI -------------------------
		GuiRenderer guiRenderer = new GuiRenderer(loader);
		GuiTexture crateGui = new GuiTexture(loader.LoadTexture("crateGui"), new Vector2f(-0.82f, 0.7f), new Vector2f(0.15f, 0.25f), 0);
		GuiTexture mapGui = new GuiTexture(mapBuffer.GetRefractionTexture(), new Vector2f(0.83f, 0.7f), new Vector2f(0.27f, 0.27f), 1);
		GuiTexture mapFrame = new GuiTexture(loader.LoadTexture("mapFrame"), new Vector2f(0.83f, 0.7f), new Vector2f(0.28f, 0.28f), 1);
		GuiTexture mapBox = new GuiTexture(loader.LoadTexture("crateGui"), new Vector2f(0.84f, 0.68f), new Vector2f(0.025f, 0.05f), 0);
		GuiTexture arrow = new GuiTexture(loader.LoadTexture("arrow"), new Vector2f(0.83f, 0.7f), new Vector2f(0.03f, 0.06f), 1);
		//GuiTexture arrow2 = new GuiTexture(loader.LoadTexture("circleArrow"), new Vector2f(0.0145f, -0.035f), new Vector2f(0.05f, 0.1f), 0); 
		//GuiTexture dot = new GuiTexture(loader.LoadTexture("white"), new Vector2f(0f, 0f), new Vector2f(0.006f, 0.006f), 1);
		
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		guis.add(crateGui);
		guis.add(mapFrame);
		guis.add(mapGui);
		guis.add(mapBox);
		guis.add(arrow);
		//guis.add(dot);
		
		/*
		GuiTexture reflection = new GuiTexture(fbos.GetReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.2f, 0.2f));
		GuiTexture refraction = new GuiTexture(fbos.GetRefractionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.2f, 0.2f));
		guis.add(reflection);
		guis.add(refraction);
		*/

		
		//------------------------- PLAYER -------------------------		
		//TexturedModel bunny = new TexturedModel(OBJLoader.LoadObjModel("stanfordBunny", loader), new ModelTexture(loader.LoadTexture("white")));
		//Player player = new Player(bunny, new Vector3f(400, 37, -226), 0, -90, 0, 0.8f);
		//entities.add(player);
		
		AnimatedPlayer player = new AnimatedPlayer(new Vector3f(400, 37, -226), 0, -90, 0, 3.0f);
		Camera camera = new Camera(player);
		
		//MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix());
		MouseTerrainIntersection picker = new MouseTerrainIntersection(camera, renderer.getProjectionMatrix(), terrain);
		
		random = new Random();
		Entity box = new Entity(crate, GeneratePosition(random, terrain, waters), 0, random.nextFloat() * 360, 0, 0.1f, 10f, false);
		box.getPosition().y += 10;
		entities.add(box);
		
		MiniGame miniGame = new MiniGame(box);
		
		Camera mapCamera = new Camera(); 
		mapCamera.setPosition(new Vector3f(player.getPosition().x, 400f, player.getPosition().z));
		mapCamera.setPitch(90);
		
		
		//------------------------- AUDIO -------------------------		
		int fireworksBuffer = AudioMaster.LoadSound("audio/firework.wav");
		Source fireworksSource = new Source();
		fireworksSource.SetVolume(0.3f);
		
		int musicBuffer = AudioMaster.LoadSound("audio/music.wav");
		Source mainSource = new Source();
		mainSource.SetVolume(0.8f);
		mainSource.SetLooping(true);
		mainSource.Play(musicBuffer);
		
		
		//------------------------- FONT -------------------------
		FontType font = new FontType(loader.LoadTexture("candara"), new File("res/candara.fnt"));
		GUIText text = new GUIText("0", 3f, font, new Vector2f(0.045f, 0.05f), 1f, false);
		text.setColour(0, 0, 0);
		
		
		//------------------------- PARTICLES -------------------------
		ParticleMaster.Init(loader, renderer.getProjectionMatrix());
		ParticleTexture starParticle = new ParticleTexture(loader.LoadTexture("particleStar"), 1);
		ParticleTexture fireworks = new ParticleTexture(loader.LoadTexture("particleAtlas"), 4);
		ParticleSystem fireworksSystem = new ParticleSystem(fireworks, 30, 20, 0.1f, 4);
		
		int MAX_PARTICLES = 50;
		int particleGen = 0;
		
		
		//------------------------- GAME LOOP -------------------------
		while(!Display.isCloseRequested())
		{
			player.Move(terrain);
			camera.Move();
			picker.Update();
			
			if (Keyboard.isKeyDown(Keyboard.KEY_W))
			{
				new Particle(starParticle, new Vector3f(player.getPosition()), new Vector3f(0, 30f, 0), 0.8f, 1f, 0f, 0.5f);
			}
			
			player.getPlayer().Update();
			
			boolean collision = miniGame.CheckCollision(player.getPosition());
			if (collision)
			{
				box.setPosition(GeneratePosition(random, terrain, waters));
				box.setRotY(random.nextFloat() * 360);
				box.getPosition().y += 10;
				
				text.Remove();
				text = new GUIText(miniGame.getScore() + "", 3f, font, new Vector2f(0.045f, 0.05f), 1f, false);
				
				fireworksSource.Play(fireworksBuffer);
				
				particleGen += MAX_PARTICLES;
			}
			
			if (particleGen > 0)
			{
				fireworksSystem.GenerateParticles(player.getPosition());
				particleGen--;
			}
				
			ParticleMaster.Update(camera);
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			
			// Render reflection texture...
			fbos.BindReflectionFrameBuffer();
			float dist = 2 * (camera.getPosition().y - waters.get(0).getHeight());
			camera.getPosition().y -= dist;
			camera.InvertPitch();
			renderer.RenderScene(entities, terrains, lights, player, camera, new Vector4f(0, 1, 0, -waters.get(0).getHeight() + 1f));
			camera.getPosition().y += dist;
			camera.InvertPitch();
			
			// Render refraction texture...
			fbos.BindRefractionFrameBuffer();
			renderer.RenderScene(entities, terrains, lights, player, camera, new Vector4f(0, -1, 0, waters.get(0).getHeight() + 1f));
			
			// Render map texture...
			mapCamera = new Camera(camera);
			mapBuffer.BindRefractionFrameBuffer();
			renderer.RenderScene(entities, terrains, lights, player, mapCamera, new Vector4f(0, -1, 0, 100000));
			waterRenderer.Render(waters, mapCamera, sun, true);
			
			/*
			mapBuffer.BindReflectionFrameBuffer();
			renderer.RenderScene(entities, terrains, lights, player, mapCamera, new Vector4f(0, -1, 0, 100000));
			waterRenderer.Render(waters, mapCamera, sun);
			*/
			
			//System.out.println(player);
			// Render to screen.
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			mapBuffer.UnbindCurrentFrameBuffer();
			renderer.RenderScene(entities, terrains, lights, player, camera, new Vector4f(0, -1, 0, 100000));
			waterRenderer.Render(waters, camera, sun, false);
			
			ParticleMaster.RenderParticles(camera);
			
			Vector2f mapBoxLocation = miniGame.SetBoxDirection(player.getPosition());
			mapBox.setPosition(mapBoxLocation);
			
			//System.out.println(camera.getYaw());
			//float rotateArrowAngle = (float) Math.toRadians(camera.getYaw());
			float rotateArrowAngle = miniGame.SetArrowAngle(camera.getYaw());
			arrow.setRotate(rotateArrowAngle);
			
			//Vector2f arrowPos = miniGame.GetArrowPosition(arrow.getPosition(), camera.getYaw());
			//arrow.setPosition(arrowPos);
			
			guiRenderer.Render(guis);
			TextMaster.Render();
			
			DisplayManager.UpdateDisplay();
		}
		
		//------------------------- CLEAN UP -------------------------
		AudioMaster.CleanUp();
		ParticleMaster.CleanUp();
		TextMaster.CleanUp();
		mapBuffer.CleanUp();
		fbos.CleanUp();
		renderer.CleanUp();
		guiRenderer.CleanUp();
		loader.CleanUp();
		DisplayManager.CloseDisplay();
	}
}
