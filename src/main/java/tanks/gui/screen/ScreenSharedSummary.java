package tanks.gui.screen;

import tanks.Crusade;
import tanks.Drawing;
import tanks.Game;
import tanks.Level;
import tanks.gui.Button;
import tanks.gui.ButtonList;

import java.util.ArrayList;

public class ScreenSharedSummary extends Screen
{
    public ButtonList sharedLevels;

    public boolean showLevels;

    Button quit = new Button(this.centerX, this.centerY + this.objYSpace * 4.5, this.objWidth, this.objHeight, "Back", () ->
    {
        if (ScreenPartyHost.isServer)
            Game.screen = ScreenPartyHost.activeScreen;
        else
            Game.screen = new ScreenPartyLobby();
    }
    );

    public Button levels = new Button(this.centerX + this.objXSpace, this.centerY + 150, this.objWidth, this.objHeight, "More levels", () ->
    {
        if (ScreenPartyHost.isServer)
            Game.screen = new ScreenSharedLevels(ScreenPartyHost.activeScreen.sharedLevels);
        else
            Game.screen = new ScreenSharedLevels(ScreenPartyLobby.sharedLevels);
    });

    public ScreenSharedSummary(ArrayList<ScreenPartyHost.SharedLevel> levels, ArrayList<ScreenPartyHost.SharedCrusade> crusades)
    {
        super(350, 40, 380, 60);

        this.music = "menu_4.ogg";
        this.musicID = "menu";

        this.showLevels = levels.size() > 12;

        ArrayList<Button> buttons2 = new ArrayList<>();
        for (int i = Math.max(0, levels.size() - 6); i < levels.size(); i++)
        {
            ScreenPartyHost.SharedLevel l = levels.get(i);

            buttons2.add(new Button(0, 0, this.objWidth, this.objHeight, l.name.replace("_", " "), () ->
            {
                ScreenSaveLevel sc = new ScreenSaveLevel(l.name, l.level, Game.screen);
                Level lev = new Level(l.level);
                lev.preview = true;
                lev.loadLevel(sc);
                Game.screen = sc;
            }
                    , "Uploaded by " + l.creator));
        }

        sharedLevels = new ButtonList(buttons2, 0, 0,  -180);
        sharedLevels.rows = 6;

        sharedLevels.sortButtons();

        this.levels.image = "icons/forward.png";
        this.levels.imageSizeX = 25;
        this.levels.imageSizeY = 25;
        this.levels.imageXOffset = 145;
    }

    @Override
    public void update()
    {
        sharedLevels.update();

        if (showLevels)
            levels.update();

        quit.update();
    }

    @Override
    public void draw()
    {
        this.drawDefaultBackground();
        sharedLevels.draw();

        if (showLevels)
            levels.draw();

        Drawing.drawing.setInterfaceFontSize(this.titleSize);
        Drawing.drawing.setColor(0, 0, 0);
        Drawing.drawing.displayInterfaceText(this.centerX, this.centerY - this.objYSpace * 4.5, "Uploaded Forts");

        quit.draw();
    }
}
