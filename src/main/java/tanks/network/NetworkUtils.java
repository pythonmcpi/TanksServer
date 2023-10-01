package tanks.network;

import io.netty.buffer.ByteBuf;
import tanks.gui.screen.ScreenPartyHost;
import tanks.network.event.EventChat;
import tanks.network.event.INetworkEvent;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class NetworkUtils 
{
	public static final Charset charset = StandardCharsets.UTF_8;
	
	public static String readString(ByteBuf b)
	{
		int l = b.readInt();

		if (l < 0)
			return null;

		return b.readCharSequence(l, charset).toString();
	}
	
	public static void writeString(ByteBuf b, String s)
	{
		int extra = 0;

		if (s == null)
		{
			b.writeInt(-1);
			return;
		}

		for (int i = 0; i < s.length(); i++)
			if (s.charAt(i) == '\u00A7')
				extra++;
		
		b.writeInt(s.length() + extra);
		b.writeCharSequence(s, charset);
	}

	public static boolean sendEventToClient(INetworkEvent e, UUID clientId) {
		for (int i = 0; i < ScreenPartyHost.server.connections.size(); i++) {
			ServerHandler c = ScreenPartyHost.server.connections.get(i);
			if (c.clientID == clientId) {
				c.sendEvent(e);

				return true;
			}
		}

		return false;
	}
}
