package tanks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import lwjglwindow.LWJGLWindow;
import tanks.tank.*;

public class Game 
{
	public static final int tank_size = 50;
	
	public static ArrayList<Movable> movables = new ArrayList<Movable>();
	public static ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	public static ArrayList<Effect> effects = new ArrayList<Effect>();
	public static ArrayList<Effect> belowEffects = new ArrayList<Effect>();

	public static ArrayList<Movable> removeMovables = new ArrayList<Movable>();
	public static ArrayList<Obstacle> removeObstacles = new ArrayList<Obstacle>();
	public static ArrayList<Effect> removeEffects = new ArrayList<Effect>();
	public static ArrayList<Effect> removeBelowEffects = new ArrayList<Effect>();

	public static ArrayList<Effect> recycleEffects = new ArrayList<Effect>();

	//public static Team playerTeam = new Team(new Color(0, 0, 255));
	//public static Team enemyTeam = new Team(new Color(255, 0, 0));
	
	public static Team playerTeam = new Team("ally");
	public static Team enemyTeam = new Team("enemy");

	public static int currentSizeX = 28;
	public static int currentSizeY = 18;
	public static double bgResMultiplier = 1;	
	public static double[][] tilesR = new double[28][18];
	public static double[][] tilesG = new double[28][18];
	public static double[][] tilesB = new double[28][18];

	public static double levelSize = 1;
	
	public static TankPlayer player;
	
	public static boolean bulletLocked = false;
		
	public static String crashMessage = "Yay! The game hasn't crashed yet!";
	
	public static Screen screen;

	public static boolean fancyGraphics = true;

	public static boolean autostart = true;
	public static double startTime = 400;
	
	public static Item[] items = new Item[5];
	
	public static boolean enableCustomTankRegistry = false;
	public static boolean enableCustomObstacleRegistry = false;

	public static RegistryTank registryTank = new RegistryTank();
	public static RegistryObstacle registryObstacle = new RegistryObstacle();

	public LWJGLWindow window;
	
	public static String currentLevel = "";	
	
	public static PrintStream logger = System.err;
	
	public static final String directoryPath = "/.tanks";
	public static final String logPath = directoryPath + "/logfile.txt";
	public static final String tankRegistryPath = directoryPath + "/tank-registry.txt";
	public static final String obstacleRegistryPath = directoryPath + "/obstacle-registry.txt";
	public static final String optionsPath = directoryPath + "/options.txt";

	public static String homedir;
	
	public static ArrayList<RegistryTank.DefaultTankEntry> defaultTanks = new ArrayList<RegistryTank.DefaultTankEntry>();
	public static ArrayList<RegistryObstacle.DefaultObstacleEntry> defaultObstacles = new ArrayList<RegistryObstacle.DefaultObstacleEntry>();

	public static Game game = new Game();
	
	private Game() {}
	
	public static void initScript() 
	{
		Drawing.initialize();
		Panel.initialize();
		Game.exitToTitle();
		
		defaultObstacles.add(new RegistryObstacle.DefaultObstacleEntry(Obstacle.class, "normal"));
		defaultObstacles.add(new RegistryObstacle.DefaultObstacleEntry(ObstacleIndestructible.class, "hard"));
		defaultObstacles.add(new RegistryObstacle.DefaultObstacleEntry(ObstacleHole.class, "hole"));
		defaultObstacles.add(new RegistryObstacle.DefaultObstacleEntry(ObstacleBouncy.class, "bouncy"));
		defaultObstacles.add(new RegistryObstacle.DefaultObstacleEntry(ObstacleShrubbery.class, "shrub"));
		defaultObstacles.add(new RegistryObstacle.DefaultObstacleEntry(ObstacleTeleporter.class, "teleporter"));

		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankBrown.class, "brown", 1));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankGray.class, "gray", 1));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankMint.class, "mint", 1.0 / 2));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankYellow.class, "yellow", 1.0 / 2));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankMagenta.class, "magenta", 1.0 / 3));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankRed.class, "red", 1.0 / 3));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankGreen.class, "green", 1.0 / 4));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankPurple.class, "purple", 1.0 / 4));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankBlue.class, "blue", 1.0 / 4));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankWhite.class, "white", 1.0 / 4));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankCyan.class, "cyan", 1.0 / 5));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankOrange.class, "orange", 1.0 / 6));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankMaroon.class, "maroon", 1.0 / 7));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankMedic.class, "medic", 1.0 / 8));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankDarkGreen.class, "darkgreen", 1.0 / 9));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankBlack.class, "black", 1.0 / 10));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankPink.class, "pink", 1.0 / 15));
		defaultTanks.add(new RegistryTank.DefaultTankEntry(TankBoss.class, "boss", 1.0 / 25));

		//defaultTanks.add(new RegistryTank.DefaultTankEntry(TankCustom.class, "custom", 1));

		homedir = System.getProperty("user.home");
		
		if (Files.exists(Paths.get(homedir + "/.tanks.d"))) 
		{
			try
			{
				Files.move(Paths.get(homedir + "/.tanks.d"), Paths.get(homedir + directoryPath));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
		}
		
		if (!Files.exists(Paths.get(directoryPath))) 
		{
			new File(homedir + directoryPath).mkdir();
			new File(homedir + directoryPath + ScreenSavedLevels.levelDir).mkdir();

			try 
			{
				new File(homedir + logPath).createNewFile();
				Game.logger = new PrintStream(new FileOutputStream (homedir + logPath, true));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		if (!Files.exists(Paths.get(homedir + tankRegistryPath)))
		{
			RegistryTank.initRegistry(homedir);
		}
		
		if (!Files.exists(Paths.get(homedir + obstacleRegistryPath)))
		{
			RegistryObstacle.initRegistry(homedir);
		}
		
		if (!Files.exists(Paths.get(homedir + optionsPath)))
		{
			ScreenOptions.initOptions(homedir);
		}
		
		try 
		{
			Game.logger = new PrintStream(new FileOutputStream (homedir + logPath, true));
		} 
		catch (FileNotFoundException e) 
		{
			Game.logger = System.err;
			Game.logger.println(new Date().toString() + " (syswarn) logfile not found despite existence of tanks directory! using stderr instead.");
		}
		
		ScreenOptions.loadOptions(homedir);
		RegistryTank.loadRegistry(homedir);
		RegistryObstacle.loadRegistry(homedir);
	}
	
	public static void main(String[] args)
	{		
		Game.initScript();
		Game.game.window = new LWJGLWindow("Tanks", 1400, 940, new GameUpdater(), new GameDrawer());
		Game.game.window.run();
	}
	
	public static void reset()
	{
		obstacles.clear();
		belowEffects.clear();
		movables.clear();
		effects.clear();
		recycleEffects.clear();
		removeEffects.clear();
		removeBelowEffects.clear();

		System.gc();
		start();
	}
	
	public static void exit()
	{
		screen = new ScreenInterlevel();
		obstacles.clear();
		belowEffects.clear();
		movables.clear();
		effects.clear();
		recycleEffects.clear();
		removeEffects.clear();
		removeBelowEffects.clear();
		
		System.gc();
	}
	
	public static void exit(String name)
	{
		obstacles.clear();
		belowEffects.clear();
		movables.clear();
		effects.clear();
		recycleEffects.clear();
		removeEffects.clear();
		removeBelowEffects.clear();
		
		System.gc();
		
		ScreenLevelBuilder s = new ScreenLevelBuilder(name);
		Game.loadLevel(new File(Game.homedir + ScreenSavedLevels.levelDir + "/" + name), s);
		Game.screen = s;	
	}
	
	public static void exitToCrash(Exception e)
	{
		obstacles.clear();
		belowEffects.clear();
		movables.clear();
		effects.clear();
		e.printStackTrace();
		Game.crashMessage = e.toString();
		Game.logger.println(new Date().toString() + " (syserr) the game has crashed! below is a crash report, good luck:");
		e.printStackTrace(Game.logger);
		screen = new ScreenCrashed();
		recycleEffects.clear();
		removeEffects.clear();
		removeBelowEffects.clear();
		
		System.gc();
	}
	
	public static void resetTiles()
	{
		Game.tilesR = new double[28][18];
		Game.tilesG = new double[28][18];
		Game.tilesB = new double[28][18];

		for (int i = 0; i < 28; i++)
		{
			for (int j = 0; j < 18; j++)
			{
				Game.tilesR[i][j] = (255 - Math.random() * 20);
				Game.tilesG[i][j] = (227 - Math.random() * 20);
				Game.tilesB[i][j] = (186 - Math.random() * 20);
			}
		}
		
		Level.currentColorR = 235; 
		Level.currentColorG = 207;
		Level.currentColorB = 166;
	}
	
	public static void exitToTitle()
	{
		resetTiles();

		Drawing.drawing.setScreenBounds(Game.tank_size * 28, Game.tank_size * 18);
		obstacles.clear();
		belowEffects.clear();
		movables.clear();
		effects.clear();
		recycleEffects.clear();
		removeEffects.clear();
		removeBelowEffects.clear();
		
		Panel.panel.hotbar.currentCoins = new Coins();
		Panel.panel.hotbar.enabledCoins = false;
		Panel.panel.hotbar.currentItemBar = new ItemBar(Panel.panel.hotbar);
		Panel.panel.hotbar.enabledItemBar = false;

		screen = new ScreenTitle();
		System.gc();
	}
	
	public static void loadLevel(File f)
	{
		Game.loadLevel(f, null);
	}
	
	public static void loadLevel(File f, ScreenLevelBuilder s)
	{
		Scanner in;
		try
		{
			in = new Scanner(f);
			
			while (in.hasNextLine()) 
			{
				String line = in.nextLine();
				Level l = new Level(line);
				l.loadLevel(s);
			}
		}
		catch (FileNotFoundException e)
		{
			Game.exitToCrash(e);
		}
	}
	
	public static void start()
	{		
		//Level level = new Level("{28,18|4...11-6,11-0...5,17...27-6,16-3...6,0...10-11,11-11...14,16...23-11,16-12...17|3-15-player,7-3-purple2-2,20-14-green,22-3-green-2,8-8.5-brown,19-8.5-mint-2,13.5-5-yellow-1}");
		
		//System.out.println(LevelGenerator.generateLevelString());		
		//Game.currentLevel = "{28,18|0-17,1-16,2-15,3-14,4-13,5-12,6-11,7-10,10-7,12-5,15-2,16-1,17-0,27-0,26-1,25-2,24-3,23-4,22-5,21-6,20-7,17-10,15-12,12-15,11-16,10-17,27-17,26-16,25-15,24-14,23-13,22-12,21-11,20-10,17-7,15-5,12-2,11-1,10-0,0-0,1-1,3-3,2-2,4-4,5-5,6-6,7-7,10-10,12-12,15-15,16-16,17-17,11-11,16-11,16-6,11-6|0-8-player-0,13-8-magenta-1,14-9-magenta-3,12-10-yellow-0,15-7-yellow-2,13-0-mint-1,14-17-mint-3,27-8-mint-2,27-9-mint-2}";///LevelGenerator.generateLevelString();
		Level level = new Level(LevelGenerator.generateLevelString());
		//Level level = new Level("{28,18|3...6-3...4,3...4-5...6,10...19-13...14,18...19-4...12|22-14-player,14-10-brown}");
		//Level level = new Level("{28,18|0...27-9,0...27-7|2-8-player,26-8-purple2-2}");
		level.loadLevel();
	}

	
}
