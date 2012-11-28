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
        String cmdPrefix = "";
        int start = 0;
        if (toSplit[0].startsWith("/")) {
        	if (toSplit[0].startsWith("/msg")) {
        		cmdPrefix = toSplit[0] + " " + toSplit[1] + " ";
        		start = 2;
        	} else { 
        		cmdPrefix = toSplit[0] + " ";
        		start = 1;
        	}
        }
        int suffix = cmdPrefix.length();
        StringBuilder sendPart = new StringBuilder(119);
       	for (int word = start; word < toSplit.length; word++) {
       		if (sendPart.length() + toSplit[word].length() + suffix > 100) {
       			mc.thePlayer.sendChatMessage(cmdPrefix + sendPart.toString().trim());
       			System.out.println("Chat sent -- "+cmdPrefix+sendPart.toString().trim());
       			sendPart = new StringBuilder(119);
       		}
       		sendPart.append(toSplit[word] + " ");
       	}
       	if (sendPart.length() > 0 || cmdPrefix.length() > 0) {
       		mc.thePlayer.sendChatMessage(cmdPrefix + sendPart.toString().trim());
       		System.out.println("Chat sent -- "+cmdPrefix+sendPart.toString().trim());
       	}
	}
}
