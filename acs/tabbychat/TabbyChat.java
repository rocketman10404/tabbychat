package acs.tabbychat;

/****************************************************
 * This document is Copyright ©(2012) and is the intellectual property of the author. 
 * It may be not be reproduced under any circumstances except for personal, private
 * use as long as it remains in its unaltered, unedited form. It may not be placed on
 * any web site or otherwise distributed publicly without advance written permission.
 * Use of this mod on any other website or as a part of any public display is strictly
 * prohibited, and a violation of copyright. 
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiChat;
import net.minecraft.src.ChatLine;
import net.minecraft.src.StringUtils;
import net.minecraft.src.SoundManager;
import net.minecraft.src.Gui;

import net.minecraft.src.GuiContainer;

public class TabbyChat {
	private static Minecraft mc;
	private Pattern chatChannelPatternClean = Pattern.compile("^\\[([A-Za-z0-9_]{1,10})\\]");
	private Pattern chatChannelPatternDirty = Pattern.compile("^\\[([A-Za-z0-9_]{1,10})\\]");
	private Pattern chatPMfromMePattern = Pattern.compile("^\\[(?:me)[ ]\\-\\>[ ]([A-Za-z0-9_]{1,16})\\]");
	private Pattern chatPMtoMePattern = Pattern.compile("^\\[([A-Za-z0-9_]{1,16})[ ]\\-\\>[ ](?:me)\\]");
	protected Calendar cal = Calendar.getInstance();
	public List<ChatChannel> channels = new ArrayList<ChatChannel>(20);
	public int nextID = 3600;
	public boolean showLastChat;
	public GlobalSettings globalPrefs = new GlobalSettings();
	public ServerSettings serverPrefs = new ServerSettings();
	public GuiSettings prefsWindow;
	public GuiChatFilters filtersWindow;
	public static final TabbyChat instance = new TabbyChat();
	
	private TabbyChat() {
		mc = Minecraft.getMinecraft();
		this.prefsWindow = new GuiSettings(this);
		this.filtersWindow = new GuiChatFilters(this);
		this.globalPrefs.loadSettings();
		if (enabled()) {		
			this.showLastChat = true;
			this.channels.add(0, new ChatChannel("*"));
			this.channels.get(0).active = true;
			
			this.serverPrefs.loadSettings();			
			this.loadPatterns();
			this.updateDefaults();
			this.updateFilters();
		} else {
			this.globalPrefs.TCenabled = false;
		}
	}
	
	private int addToChannel(int index, ChatLine thisChat) {
		ChatLine newChat = this.withTimeStamp(thisChat);
		this.channels.get(index).chatLog.add(0, newChat);
		this.channels.get(index).trimLog();
		if (!this.channels.get(index).active && !this.channels.get(0).active) {
			this.showLastChat = false;
			if (index != 0)
				this.channels.get(index).unread = true;
		} else
			this.showLastChat = true;
		return 0;
	}

	private int addToChannel(int index, ChatLine[] thisChat) {
		int ret = -1;
		for (ChatLine cl : thisChat) {
			ret = this.addToChannel(index, cl);
		}
		return ret;
	}
	
	private int addToChannel(String _name, ChatLine[] thisChat) {
		for (String ichan : this.serverPrefs.ignoredChans) {
			if (ichan.length() > 0 && _name.equals(ichan)) {
				return 1;
			}
		}		
		if (this.channels.size() > 20) return 1; // Too many tabs
		
		for (int i = 0; i < this.channels.size(); i++) {
			if (_name.equals(this.channels.get(i).title)) {
				return this.addToChannel(i, thisChat);
			}
		}

		if (this.globalPrefs.autoSearchEnabled) {
			this.channels.add(new ChatChannel(_name));
			return this.addToChannel(this.channels.size()-1, thisChat);
		}
		return -1;
	}
	
	private int getChanIndexByID(int _id) {
		for (int i = 1; i < this.channels.size(); i++) {
			if (_id == this.channels.get(i).chanID) {
				return i;
			}
		}
		return -1;
	}
	
	private ChatLine withTimeStamp(ChatLine _orig) {
		ChatLine stamped = _orig;
		if (this.globalPrefs.timestampsEnabled) {
			this.cal = Calendar.getInstance();
			stamped = new ChatLine(_orig.getUpdatedCounter(), this.globalPrefs.timeStamp.format(this.cal.getTime())+_orig.getChatLineString(), _orig.getChatLineID());
		}
		return stamped;
	}
	
	private String withTimeStamp(String _orig) {
		String stamped = _orig;
		if (this.globalPrefs.timestampsEnabled) {
			this.cal = Calendar.getInstance();
			stamped = this.globalPrefs.timeStamp.format(this.cal.getTime()) + _orig;
		}
		return stamped;
	}
		
	protected void ding() {
		mc.sndManager.playSoundFX("random.orb", 1.0F, 1.0F);
	}
	
	protected void disable() {
		this.globalPrefs.TCenabled = false;
		
		mc.ingameGUI.getChatGUI().clearChatLines();
		for (ChatChannel chan : this.channels) {
			chan.clear();
			chan = null;
		}
		this.channels.clear();
	}
	
	protected void enable() {
		this.globalPrefs.TCenabled = true;
		this.showLastChat = true;
		if (this.getChanId("*") < 0) {
			this.channels.add(0, new ChatChannel("*"));
			this.channels.get(0).active = true;
		}
		
		this.globalPrefs.loadSettings();
		this.serverPrefs.loadSettings();
		this.loadPatterns();
		this.updateDefaults();
		this.updateFilters();
	}

	protected void loadPatterns() {	
		String frmt = this.serverPrefs.chanDelimColor.getCode() + this.serverPrefs.chanDelimFormat.getCode();

		if (this.serverPrefs.chanDelimColor.toString().equals("White")) {
			frmt = "(" + this.serverPrefs.chanDelimColor.getCode() + ")?" + this.serverPrefs.chanDelimFormat.getCode();
		} else if (frmt.length() > 7)
			frmt = "[" + frmt + "]{2}";
		if (frmt.length() > 0)
			frmt = "(?i:"+frmt+")";
		if (frmt.length() == 0)
			frmt = "(?i:\u00A7[0-9A-FK-OR])*";
		
		this.chatChannelPatternDirty = Pattern.compile("^"+frmt+"\\"+this.serverPrefs.chanDelims.open()+"([A-Za-z0-9_\u00A7]+)\\"+this.serverPrefs.chanDelims.close());
		this.chatChannelPatternClean = Pattern.compile("^"+"\\"+this.serverPrefs.chanDelims.open()+"([A-Za-z0-9_]{1,"+Integer.toString(this.globalPrefs.maxChannelNameLength)+"})\\"+this.serverPrefs.chanDelims.close());
		this.chatPMtoMePattern = Pattern.compile("^"+"\\"+this.serverPrefs.chanDelims.open()+"([A-Za-z0-9_]{1,16})[ ]\\-\\>[ ](?:me)\\"+this.serverPrefs.chanDelims.close());
		this.chatPMfromMePattern = Pattern.compile("^"+"\\"+this.serverPrefs.chanDelims.open()+"(?:me)[ ]\\-\\>[ ]([A-Za-z0-9_]{1,16})\\"+this.serverPrefs.chanDelims.close());
	}
	
	protected void updateFilters() {
		ArrayList<Integer> track = new ArrayList<Integer>(this.serverPrefs.customFilters.size());
		boolean keeper = false;
		// Search through all current channels/tabs that have associated filter
		// attempt to identify their associated filter and remove any that no longer have one
		for (int i = this.channels.size()-1; i >= 0; i--) {
			if (!this.channels.get(i).hasFilter) continue; 
			keeper = false;
			for (CustomChatFilter cf : this.serverPrefs.customFilters) {
				if (cf.chanID == this.channels.get(i).chanID && cf.sendToTab) {
					track.add(cf.chanID);
					keeper = true;
				}
			}
			
			if (!keeper) {
				// Associated filter no longer exists for this channel, remove it
				removeTab(i);
			}
		}
		// Now, all remaining channels have existing associated updated filters
		// Still need to search through all OTHER filters for any needing new tabs		
		for (CustomChatFilter cf : this.serverPrefs.customFilters) {
			if (cf.sendToTab && !track.contains(cf.chanID)) {
				this.channels.add(new ChatChannel(cf.name));
				this.channels.get(this.channels.size()-1).hasFilter = true;
				cf.chanID = this.channels.get(this.channels.size()-1).chanID;
			} else if (!cf.sendToTab) cf.chanID = 0;
		}
	}

	protected void updateDefaults() {
		List<String> dList = new ArrayList(Arrays.asList(this.serverPrefs.defaultChans));
		int ind;
		for (ChatChannel chan : this.channels) {
			ind = dList.indexOf(chan.title);
			if (ind >= 0) dList.remove(ind);
		}
		
		for (String defChan : dList) {
			if (defChan.length() > 0) this.channels.add(new ChatChannel(defChan));
		}
	}

	public void copyTab(int toIndex, int fromIndex) {
		this.channels.set(toIndex, this.channels.get(fromIndex));
	}
	
	public void displayChatLines(Minecraft mc, int index) {
		mc.ingameGUI.getChatGUI().addChatLines(this.channels.get(index).chatLog);
	}
	
	public boolean doesButtonEqual(int index, GuiButton btnObj) {
		return this.channels.get(index).doesButtonEqual(btnObj);
	}
	
	public boolean enabled() {
		if (mc.isSingleplayer()) {
			this.disable();
			return false;
		} else if (!mc.isSingleplayer() && !this.globalPrefs.TCenabled) {
			enable();
			return true;
		} else
			return this.globalPrefs.TCenabled;
	}
	
	public int getActive() {
		for (int i = 0; i < this.channels.size(); i++) {
			if (this.channels.get(i).active) {
				return i;
			}
		}
		return 0;
	}
	
	public int getChanId(String _name) {
		for (int i=0; i<this.channels.size(); i++) {
			if (_name.equals(this.channels.get(i).title)) {
				return this.channels.get(i).chanID;
			}
		}
		return -1;
	}
	
	public ChatLine getChatLine(int _chan, int _line) {
		return this.channels.get(_chan).chatLog.get(_line);
	}
	
	public int numberOfTabs() {
		return this.channels.size();
	}
	
	public static void printErr(String err) {
		System.err.println(err);
	}
	
	public int processChat(ChatLine[] theChat) {
		
		ChatLine[] filteredChatLine = new ChatLine[theChat.length]; 
		ArrayList<Integer> goesHere = new ArrayList<Integer>();
		goesHere.add(0);
		String filteredChat;
		int _ind;
		
		for (int z=0; z<theChat.length; z++) {
			filteredChat = theChat[z].getChatLineString();
			for (CustomChatFilter filter : this.serverPrefs.customFilters) {
				if (!filter.applyFilterToDirtyChat(filteredChat)) continue;
				filteredChat = filter.getLastMatchPretty();
				if (filter.sendToTab) {
					_ind = this.getChanIndexByID(filter.chanID);
					if (_ind > 0) goesHere.add(_ind);
				}
				if (filter.ding) this.ding(); 
		}
			filteredChatLine[z] = new ChatLine(theChat[z].getUpdatedCounter(), filteredChat, theChat[z].getChatLineID());
		}
		
		
		for (Integer c : goesHere) {
			this.addToChannel(c, filteredChatLine);
		}
		if (goesHere.contains(this.getActive())) this.showLastChat = true;			
		
		int ret = 1;
		String coloredChat = "";
		for (ChatLine cl : theChat)
			coloredChat = coloredChat + cl.getChatLineString();
		
		String cleanedChat = StringUtils.stripControlCodes(coloredChat);
		if (this.globalPrefs.saveLocalLogEnabled) this.globalPrefs.logChat(this.withTimeStamp(cleanedChat));
		
		Matcher findChannelClean = this.chatChannelPatternClean.matcher(cleanedChat);
		Matcher findChannelDirty = this.chatChannelPatternDirty.matcher(coloredChat);
		if (findChannelClean.find() && findChannelDirty.find())
			ret = this.addToChannel(cleanedChat.substring(findChannelClean.start(1), findChannelClean.end(1)), filteredChatLine);
		else {
			Matcher findPMtoMe = this.chatPMtoMePattern.matcher(cleanedChat);
			if (findPMtoMe.find()) {
				ret = this.addToChannel(cleanedChat.substring(findPMtoMe.start(1), findPMtoMe.end(1)), filteredChatLine);
			} else {
				Matcher findPMfromMe = this.chatPMfromMePattern.matcher(cleanedChat);
				if (findPMfromMe.find())
					ret = this.addToChannel(cleanedChat.substring(findPMfromMe.start(1), findPMfromMe.end(1)), filteredChatLine);
			}
		}
		return ret;
	}
	
	public void reloadServerPrefs() {
		this.channels.clear();
		this.channels.add(0, new ChatChannel("*"));
		this.channels.get(0).active = true;
		this.serverPrefs = null;
		this.serverPrefs = new ServerSettings();
		this.serverPrefs.loadSettings();
		this.loadPatterns();
		this.updateDefaults();
		this.updateFilters();
	}
	
 	public void removeTab(int index) {
 		this.channels.remove(index);
	}
}
