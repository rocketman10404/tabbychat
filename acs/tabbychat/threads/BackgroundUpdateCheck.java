package acs.tabbychat.threads;

import java.util.ArrayList;

import acs.tabbychat.core.ChatChannel;
import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TCChatLine;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;
import net.minecraft.src.Minecraft;

public class BackgroundUpdateCheck extends Thread {
	
	public BackgroundUpdateCheck() {
	}
	
	public void run() {
		String newest = TabbyChat.getNewestVersion();
		if(!TabbyChat.generalSettings.tabbyChatEnable.getValue()) return;
		Minecraft mc = Minecraft.getMinecraft();
		ArrayList<TCChatLine> updateMsg = new ArrayList<TCChatLine>();
		if (newest != null && !newest.equals(TabbyChatUtils.version)) {
			StringBuilder updateReport = new StringBuilder("\u00A77");
			updateReport.append(TabbyChat.translator.getString("messages.update1"));
			updateReport.append(TabbyChatUtils.version);
			updateReport.append(TabbyChat.translator.getString("messages.update2"));
			updateReport.append(newest+") ");
			updateReport.append(TabbyChat.translator.getString("messages.update3"));
			updateReport.append("\u00A7r");
			GuiNewChatTC.getInstance().tc.printMessageToChat(updateReport.toString());
		}
	}	
}
