package tanks.gui.screen;

import basewindow.BaseFile;
import tanks.Drawing;
import tanks.Game;
import tanks.Level;
import tanks.gui.Button;
import tanks.gui.ChatBox;
import tanks.gui.ChatMessage;

import java.io.IOException;
import java.util.ArrayList;

public class ScreenSharedLevels extends Screen implements IPartyMenuScreen
{
	public static final String levelDir = Game.directoryPath + "/levels";

	int rows = 6;
	int yoffset = -150;
	static int page = 0;

	Button quit = new Button(Drawing.drawing.interfaceSizeX / 2, Drawing.drawing.interfaceSizeY / 2 + 300, 350, 40, "Back", new Runnable()
	{
		@Override
		public void run()
		{
			if (ScreenPartyHost.isServer)
				Game.screen = ScreenPartyHost.activeScreen;
			else
				Game.screen = new ScreenPartyLobby();
		}
	}
			);

	Button next = new Button(Drawing.drawing.interfaceSizeX / 2 + 190, Drawing.drawing.interfaceSizeY / 2 + 240, 350, 40, "Next page", new Runnable()
	{
		@Override
		public void run()
		{
			page++;
		}
	}
			);

	Button previous = new Button(Drawing.drawing.interfaceSizeX / 2 - 190, Drawing.drawing.interfaceSizeY / 2 + 240, 350, 40, "Previous page", new Runnable()
	{
		@Override
		public void run()
		{
			page--;
		}
	}
			);

	ArrayList<Button> buttons = new ArrayList<Button>();

	public ScreenSharedLevels(ArrayList<ScreenPartyHost.SharedLevel> levels)
	{
		this.music = "tomato_feast_4.ogg";
		this.musicID = "menu";

		BaseFile savedLevelsFile = Game.game.fileManager.getFile(Game.homedir + levelDir);
		if (!savedLevelsFile.exists())
		{
			savedLevelsFile.mkdirs();
		}

		for (ScreenPartyHost.SharedLevel l: levels)
		{
			buttons.add(new Button(0, 0, 350, 40, l.name.replace("_", " "), new Runnable()
			{
				@Override
				public void run()
				{
					ScreenSaveSharedLevel sc = new ScreenSaveSharedLevel(l.name, l.level, Game.screen);
					Level lev = new Level(l.level);
					lev.preview = true;
					lev.loadLevel(sc);
					Game.screen = sc;
				}
			}
					, "Shared by " + l.creator));

		}

		for (int i = 0; i < buttons.size(); i++)
		{
			int page = i / (rows * 3);
			int offset = 0;

			if (page * rows * 3 + rows < buttons.size())
				offset = -190;

			if (page * rows * 3 + rows * 2 < buttons.size())
				offset = -380;

			buttons.get(i).posY = Drawing.drawing.interfaceSizeY / 2 + yoffset + (i % rows) * 60;

			if (i / rows % 3 == 0)
				buttons.get(i).posX = Drawing.drawing.interfaceSizeX / 2 + offset;
			else if (i / rows % 3 == 1)
				buttons.get(i).posX = Drawing.drawing.interfaceSizeX / 2 + offset + 380;
			else
				buttons.get(i).posX = Drawing.drawing.interfaceSizeX / 2 + offset + 380 * 2;
		}


	}

	@Override
	public void update()
	{		
		for (int i = page * rows * 3; i < Math.min(page * rows * 3 + rows * 3, buttons.size()); i++)
		{
			buttons.get(i).update();
		}

		quit.update();

		if (page > 0)
			previous.update();

		if (buttons.size() > (1 + page) * rows * 3)
			next.update();

		if (ScreenPartyHost.isServer)
			ScreenPartyHost.chatbox.update();
		else if (ScreenPartyLobby.isClient)
			ScreenPartyLobby.chatbox.update();
	}

	@Override
	public void draw()
	{
		this.drawDefaultBackground();

		for (int i = Math.min(page * rows * 3 + rows * 3, buttons.size()) - 1; i >= page * rows * 3; i--)
		{
			buttons.get(i).draw();
		}

		quit.draw();

		ChatBox chatbox = null;
		ArrayList<ChatMessage> chat = null;

		if (ScreenPartyHost.isServer)
		{
			chatbox = ScreenPartyHost.chatbox;
			chat = ScreenPartyHost.chat;
		}
		else if (ScreenPartyLobby.isClient)
		{
			chatbox = ScreenPartyLobby.chatbox;
			chat = ScreenPartyLobby.chat;
		}

		Drawing.drawing.drawInterfaceText(Drawing.drawing.interfaceSizeX / 2, Drawing.drawing.interfaceSizeY / 2 - 210, "Shared levels");

		if (page > 0)
			previous.draw();

		if (buttons.size() > (1 + page) * rows * 3)
			next.draw();

		chatbox.draw();

		Drawing.drawing.setColor(0, 0, 0);
		long time = System.currentTimeMillis();
		for (int i = 0; i < chat.size(); i++)
		{
			ChatMessage c = chat.get(i);
			if (time - c.time <= 30000 || chatbox.selected)
			{
				Drawing.drawing.drawInterfaceText(20, Drawing.drawing.interfaceSizeY - i * 30 - 70, c.message, false);
			}
		}
	}
}