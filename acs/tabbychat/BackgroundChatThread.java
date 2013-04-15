package acs.tabbychat;

import net.minecraft.client.Minecraft;

public class BackgroundChatThread extends Thread {
	String sendChat = "";
	
	BackgroundChatThread(String _send) {
		this.sendChat = _send;
	}
	
	public synchronized void run() {
		Minecraft mc = Minecraft.getMinecraft();
		mc.ingameGUI.getChatGUI().addToSentMessages(this.sendChat);
		String[] toSplit = this.sendChat.split(" ");
		String cmdPrefix = "";
		int start = 0;
		if (toSplit.length > 0 && toSplit[0].startsWith("/")) {
			if (toSplit[0].startsWith("/msg")) {
				cmdPrefix = toSplit[0] + " " + toSplit[1] + " ";
				start = 2;
			} else if (!toSplit[0].trim().equals("/")) { 
				cmdPrefix = toSplit[0] + " ";
				start = 1;
			}
		}
		int suffix = cmdPrefix.length();
		StringBuilder sendPart = new StringBuilder(119);
		for (int word = start; word < toSplit.length; word++) {
			if (sendPart.length() + toSplit[word].length() + suffix > 100) {
				mc.thePlayer.sendChatMessage(cmdPrefix + sendPart.toString().trim());
				try {
					Thread.sleep(Integer.parseInt(TabbyChat.instance.advancedSettings.multiChatDelay.getValue()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sendPart = new StringBuilder(119);
			}
			sendPart.append(toSplit[word] + " ");
		}
		if (sendPart.length() > 0 || cmdPrefix.length() > 0) {
			mc.thePlayer.sendChatMessage(cmdPrefix + sendPart.toString().trim());
			
		}
	}
}
