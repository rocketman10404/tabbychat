package acs.tabbychat;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;

public class BackgroundUpdateCheck extends Thread {

	BackgroundUpdateCheck() { }
	
	public void run() {
		Minecraft mc = Minecraft.getMinecraft();
		String ver = TabbyChat.getNewestVersion();
		ArrayList<TCChatLine> updateMsg = new ArrayList<TCChatLine>();
		if (!ver.equals(TabbyChatUtils.version)) {
			ver = "\u00A77TabbyChat: An update is available!  (Current version is "+TabbyChatUtils.version+", newest is "+ver+")";
			String ver2 = " \u00A77Visit the TabbyChat forum thread at minecraftforum.net to download.";
			TCChatLine updateLine = new TCChatLine(mc.ingameGUI.getUpdateCounter(), ver, 0);
			TCChatLine updateLine2 = new TCChatLine(mc.ingameGUI.getUpdateCounter(), ver2, 0);
			if(!TabbyChat.instance.channelMap.containsKey("TabbyChat")) TabbyChat.instance.channelMap.put("TabbyChat", new ChatChannel("TabbyChat"));
			updateMsg.add(updateLine);
			updateMsg.add(updateLine2);			
			TabbyChat.instance.processChat(updateMsg);
			TabbyChat.instance.channelMap.get("TabbyChat").chatLog.addAll(0, TabbyChat.instance.lastChat);
		}
	}	
}
