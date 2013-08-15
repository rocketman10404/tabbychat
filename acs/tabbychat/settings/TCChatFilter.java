package acs.tabbychat.settings;

import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.minecraft.src.Minecraft;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;

public class TCChatFilter {	
	public boolean inverseMatch = false;
	public boolean caseSensitive = false;
	public boolean highlightBool = true;
	public boolean audioNotificationBool = false;
	public boolean sendToTabBool = false;
	public boolean sendToAllTabs = false;
	public boolean removeMatches = false;
	
	public ColorCodeEnum highlightColor = ColorCodeEnum.YELLOW;
	public FormatCodeEnum highlightFormat = FormatCodeEnum.BOLD;
	public NotificationSoundEnum audioNotificationSound = NotificationSoundEnum.ORB;
	{
		this.highlightBool = false;
	}
	
	public String sendToTabName = "";	
	public String expressionString = ".*";
	
	public Pattern expressionPattern = Pattern.compile(this.expressionString);
	private static final Pattern allFormatCodes = Pattern.compile("(?i)(\\u00A7[0-9A-FK-OR])+");
	public String filterName;
	private String lastMatch = "";
	private String tabName = null;
	
	public TCChatFilter(String name) {
		this.filterName = name;
	}
	
	public TCChatFilter(TCChatFilter orig) {
		this(orig.filterName);
		this.copyFrom(orig);
	}
	
	public boolean applyFilterToDirtyChat(String input) {
		// Map to store current format codes and their locations
		TreeMap<Integer, String>oldCodes = new TreeMap();
		// Map to store format codes to be inserted, depending on filter configuration
		HashMap<Integer, String>newCodes = new HashMap();
		// StringBuilder object to track result progress
		StringBuilder result = new StringBuilder().append(input);
		
		// Remove and store formatting codes in provided input string
		Matcher findFormatCodes = allFormatCodes.matcher(input);
		int start = 0;
		int trimmed = 0;
		while(findFormatCodes.find()) {
			start = findFormatCodes.start();
			int end = findFormatCodes.end();
			oldCodes.put(start - trimmed, findFormatCodes.group());
			result.delete(start- trimmed, end - trimmed);
			trimmed += end - start;
		}
		
		// Prepare data for formatting
		String prefix = "";
		String suffix = "";
		if(this.highlightBool) {
			suffix = "\u00A7r"; // Reset
			prefix = this.highlightColor.toCode() + this.highlightFormat.toCode();
		}
		
		// All formatting codes have been cleansed from input; apply filter
		Matcher findFilterMatches = this.expressionPattern.matcher(result.toString());
		boolean foundMatch = false;
		while(findFilterMatches.find()) {
			foundMatch = true;
			// If highlighting, store desired locations for format codes
			if(this.highlightBool) {
				start = findFilterMatches.start();
				newCodes.put(start, prefix);
				int end = findFilterMatches.end();
				Entry<Integer, String> newSuffix = oldCodes.lowerEntry(end);
				if(newSuffix == null) newCodes.put(end, suffix);
				else {
					// Store last-used formatting code as the tail to this highlight
					newCodes.put(end, newSuffix.getValue());
					// Removed any previous codes between the beginning and end of this highlight
					while(newSuffix.getKey() >= start) {
						oldCodes.put(newSuffix.getKey(), "");
						newSuffix = oldCodes.lowerEntry(newSuffix.getKey());
						if(newSuffix == null) break;
					}
				}
			} else break;
		}
		
		// Pull name of destination tab
		if(this.sendToTabBool && !this.sendToAllTabs) {
			if(this.inverseMatch) this.tabName = this.sendToTabName;
			else if(this.sendToTabName.startsWith("%")) {
				int group = TabbyChatUtils.parseInteger(this.sendToTabName.substring(1));
				if(foundMatch && group >= 0 && findFilterMatches.groupCount() >= group) {
					this.tabName = findFilterMatches.group(group);
					if(this.tabName == null) this.tabName = this.filterName;
				} else {
					this.tabName = this.filterName;
				}
			} else {
				this.tabName = this.sendToTabName;
			}
		} else {
			this.tabName = null;
		}
		
		// Insert old formatting codes and new highlight codes if highlighting has been requested
		if(this.highlightBool) {
			// Add new codes into TreeMap for sorting
			oldCodes.putAll(newCodes);
			// Re-insert from end (all code indices assume clean string)
			Entry<Integer, String> ptr = oldCodes.pollLastEntry();
			while(ptr != null) {
				result.insert(ptr.getKey(), ptr.getValue());
				ptr = oldCodes.pollLastEntry();
			}
			this.lastMatch = result.toString();
		} else this.lastMatch = input;
		
		// Return result status of filter application
		if(!foundMatch && this.inverseMatch) return true;
		else if(foundMatch && !this.inverseMatch) return true;
		else return false;
	}
	
	public void audioNotification() {
		Minecraft.getMinecraft().sndManager.playSoundFX(this.audioNotificationSound.file(), 1.0F, 1.0F);
	}
	
	public void compilePattern() {
		try {
			if(this.caseSensitive) this.expressionPattern = Pattern.compile(this.expressionString);
			else this.expressionPattern = Pattern.compile(this.expressionString, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			TabbyChat.printMessageToChat("Invalid expression entered for filter '"+this.filterName+"', resetting to default.");
			this.expressionString = ".*";
			this.expressionPattern = Pattern.compile(this.expressionString);
		}
	}
	
	public void compilePattern(String newExpression) {
		this.expressionString = newExpression;
		this.compilePattern();
	}
	
	public void copyFrom(TCChatFilter orig) {
		this.filterName = orig.filterName;
		this.inverseMatch = orig.inverseMatch;
		this.caseSensitive = orig.caseSensitive;
		this.highlightBool = orig.highlightBool;
		this.audioNotificationBool = orig.audioNotificationBool;
		this.sendToTabBool = orig.sendToTabBool;
		this.sendToAllTabs = orig.sendToAllTabs;
		this.removeMatches = orig.removeMatches;
		this.highlightColor = orig.highlightColor;
		this.highlightFormat = orig.highlightFormat;
		this.audioNotificationSound = orig.audioNotificationSound;
		this.sendToTabName = orig.sendToTabName;
		this.expressionString = orig.expressionString;
		
		this.compilePattern();
	}
	
	public String getLastMatchPretty() {
		String tmp = this.lastMatch;
		this.lastMatch = "";
		return tmp;
	}
	
	public Properties getProperties() {
		Properties myProps = new Properties();
		myProps.put("filterName", this.filterName);
		myProps.put("inverseMatch", this.inverseMatch);
		myProps.put("caseSensitive", this.caseSensitive);
		myProps.put("highlightBool", this.highlightBool);
		myProps.put("audioNotificationBool", this.audioNotificationBool);
		myProps.put("sendToTabBool", this.sendToTabBool);
		myProps.put("sendToAllTabs", this.sendToAllTabs);
		myProps.put("removeMatches", this.removeMatches);
		myProps.put("highlightColor", this.highlightColor.name());
		myProps.put("highlightFormat", this.highlightFormat.name());
		myProps.put("audioNotificationSound", this.audioNotificationSound.name());
		myProps.put("sendToTabName", this.sendToTabName);
		myProps.put("expressionString", this.expressionString);
		return myProps;
	}
	
	public String getTabName() {
		String tmp = this.tabName;
		this.tabName = null;
		return tmp;
	}
}
