package acs.tabbychat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;

import net.minecraft.src.Gui;

public class TabbyChatUtils extends Thread {
	
	private static Calendar logDay = Calendar.getInstance();
	private static File logFile;
	private static SimpleDateFormat logNameFormat = new SimpleDateFormat("'TabbyChatLog_'MM-dd-yyyy'.txt'");
	
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
		BackgroundChatThread sendProc = new BackgroundChatThread(toSend);
		sendProc.start();
	}
	
	public static void logChat(String theChat) {
		Calendar tmpcal = Calendar.getInstance();
	
		if (logFile == null || tmpcal.get(Calendar.DAY_OF_YEAR) != logDay.get(Calendar.DAY_OF_YEAR)) {
			logDay = tmpcal;
			logFile = new File(Minecraft.getMinecraftDir(), new StringBuilder().append("TabbyChatLogs").append(File.separatorChar).append(logNameFormat.format(logDay.getTime())).toString());
		}
		
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (Exception e) {
				TabbyChat.printErr("Cannot create log file : '" + e.getLocalizedMessage() + "' : " + e.toString());
				return;
			}
		}
		
		try {
			FileOutputStream logStream = new FileOutputStream(logFile, true);
			PrintStream logPrint = new PrintStream(logStream);
			logPrint.println(theChat);
			logPrint.close();
		} catch (Exception e) {
			TabbyChat.printErr("Cannot write to log file : '" + e.getLocalizedMessage() + "' : " + e.toString());
			return;
		}
	}

	public static Integer parseInteger(String _input, int min, int max, int fallback) {
		Integer result;
		try {
			result = Integer.parseInt(_input);
			result = Math.max(min, result);
			result = Math.min(max, result);
		} catch (NumberFormatException e) {
			result = fallback;
		}
		return result;
	}
	
	public static Float parseFloat(String _input, float min, float max, float fallback) {
		Float result;
		try {
			result = new Float(_input);
			result = Math.max(min, result);
			result = Math.min(max, result);
		} catch (NumberFormatException e) {
			result = fallback;
		}
		return result;
	}

	public static ChannelDelimEnum parseDelimiters(String _input) {
		try {
			return ChannelDelimEnum.valueOf(_input);
		} catch (IllegalArgumentException e) {
			return ChannelDelimEnum.BRACKETS;
		}
	}
	
	public static ColorCodeEnum parseColor(String _input) {
		try {
			return ColorCodeEnum.valueOf(_input);
		} catch (IllegalArgumentException e) {
			return ColorCodeEnum.YELLOW;
		}
	}
	
	public static FormatCodeEnum parseFormat(String _input) {
		try {
			return FormatCodeEnum.valueOf(_input);
		} catch (IllegalArgumentException e) {
			return FormatCodeEnum.BOLD;
		}
	}
	
	public static NotificationSoundEnum parseSound(String _input) {
		try {
			return NotificationSoundEnum.valueOf(_input);
		} catch (IllegalArgumentException e) {
			return NotificationSoundEnum.ORB;
		}
	}
	
	public static TimeStampEnum parseTimestamp(String _input) {
		try {
			return TimeStampEnum.valueOf(_input);
		} catch (IllegalArgumentException e) {
			return TimeStampEnum.MILITARY;
		}
	}
}
