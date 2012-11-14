package acs.tabbychat;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;

import net.minecraft.src.Gui;

public class TabbyChatUtils {
	
	private TabbyChatUtils() {}
	
	public static String join(String[] arr, String glue) {
		if (arr.length < 1)
			return "";
		else if (arr.length == 1)
			return arr[0];
		StringBuilder bucket = new StringBuilder();
		for (String s : Arrays.copyOf(arr,  arr.length-1)) {
			bucket.append(s);
			bucket.append(glue);
		}
		bucket.append(arr[arr.length-1]);
		return bucket.toString();
	}
	
	public static boolean is(Gui _gui, String className) {
		try {
			return _gui.getClass().getSimpleName().contains(className);
		} catch (Throwable e) {}
		return false;
	}

	public static void writeLargeChat(String toSend) {
		Minecraft mc = Minecraft.getMinecraft();
        mc.ingameGUI.getChatGUI().addToSentMessages(toSend);
        String[] toSplit = toSend.split(" ");
        StringBuilder sendPart = new StringBuilder(119);
        String firstLine = (toSend.length() > 99) ? toSend.substring(0, 100) : toSend;
        if (!mc.handleClientCommand(firstLine)) {
        	for (int word = 0; word < toSplit.length; word++) {
        		if (sendPart.length() + toSplit[word].length() > 99) {
        			mc.thePlayer.sendChatMessage(sendPart.toString().trim());
        			sendPart = new StringBuilder(119);
        		} else
        			sendPart.append(toSplit[word] + " ");
        	}
        	if (sendPart.length() > 0)
        		mc.thePlayer.sendChatMessage(sendPart.toString().trim());
        }
	}
}
