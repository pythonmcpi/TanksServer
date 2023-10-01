package tanks.network.event;

import io.netty.buffer.ByteBuf;
import tanks.*;
import tanks.gui.ChatMessage;
import tanks.gui.screen.ScreenPartyHost;
import tanks.gui.screen.ScreenPartyLobby;
import tanks.network.NetworkUtils;
import tanks.network.ServerHandler;

public class EventShareCrusade extends PersonalEvent
{
	public String crusade;
	public String name;
	public String username = "";

	public EventShareCrusade()
	{

	}

	public EventShareCrusade(Crusade c, String name)
	{
		this.crusade = c.contents;
		this.name = name;
	}

	@Override
	public void execute() 
	{		
		if (this.clientID != null)
		{
			String s = "\u00A7255000000255Crusades may not be shared in a Fortress Wars lobby.";
			NetworkUtils.sendEventToClient(new EventChat(s), this.clientID);
		}
		else
		{
			ScreenPartyLobby.sharedCrusades.add(new ScreenPartyHost.SharedCrusade(this.crusade, this.name, this.username));
		}
	}

	@Override
	public void write(ByteBuf b)
	{
		NetworkUtils.writeString(b, this.crusade);
		NetworkUtils.writeString(b, this.name);
		NetworkUtils.writeString(b, this.username);
	}

	@Override
	public void read(ByteBuf b)
	{
		this.crusade = NetworkUtils.readString(b);
		this.name = NetworkUtils.readString(b);
		this.username = NetworkUtils.readString(b);
	}
}
