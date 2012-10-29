package acs.tabbychat;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
}
