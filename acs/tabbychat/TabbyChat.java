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
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
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
	protected static Minecraft mc;
	private Pattern chatChannelPatternClean = Pattern.compile("^\\[([A-Za-z0-9_]{1,10})\\]");
	private Pattern chatChannelPatternDirty = Pattern.compile("^\\[([A-Za-z0-9_]{1,10})\\]");
	private Pattern chatPMfromMePattern = Pattern.compile("^\\[(?:me)[ ]\\-\\>[ ]([A-Za-z0-9_]{1,16})\\]");
	private Pattern chatPMtoMePattern = Pattern.compile("^\\[([A-Za-z0-9_]{1,16})[ ]\\-\\>[ ](?:me)\\]");
	protected static String version = "1.2.0";
	protected Calendar cal = Calendar.getInstance();
	protected ChatLine lastChat;
	public List<ChatChannel> channels = new ArrayList<ChatChannel>(20);
	public int nextID = 3600;
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
			this.channels.add(0, new ChatChannel("*"));
			this.channels.get(0).active = true;
			
			String ver = TabbyChat.getNewestVersion();
			if (!ver.equals(version)) {
				ver = "\u00A77TabbyChat: An update is available!  (Current version is "+version+", newest is "+ver+")";
				String ver2 = "\u00A77Visit the TabbyChat forum thread at minecraftforum.net to download.";
				ChatLine updateLine = new ChatLine(mc.ingameGUI.getUpdateCounter(), ver, 0);
				ChatLine updateLine2 = new ChatLine(mc.ingameGUI.getUpdateCounter(), ver2, 0);
				this.channels.add(new ChatChannel("TabbyChat"));
				this.addToChannel(0, updateLine);
				this.addToChannel(0, updateLine2);
				this.addToChannel(this.channels.size()-1, updateLine);
				this.addToChannel(this.channels.size()-1, updateLine2);
				
			}
			
			this.serverPrefs.loadSettings();			
			this.loadPatterns();
			this.updateDefaults();
			this.updateFilters();
		} else {
			this.globalPrefs.TCenabled = false;
		}
	}
	
	private int addToChannel(int index, ChatLine thisChat) {
		int ret = 0;
		ChatLine newChat = this.withTimeStamp(thisChat);
		this.channels.get(index).chatLog.add(0, newChat);
		this.channels.get(index).trimLog();
		if (this.channels.get(index).active || this.channels.get(0).active)
			ret = 1;
		return ret;
	}

	private int addToChannel(int index, ChatLine[] thisChat) {
		int ret = 0;
		for (ChatLine cl : thisChat) {
			ret += this.addToChannel(index, cl);
		}
		return ret;
	}
	
	private int addToChannel(String _name, ChatLine[] thisChat) {
		for (String ichan : this.serverPrefs.ignoredChans) {
			if (ichan.length() > 0 && _name.equals(ichan)) {
				return 0;
			}
		}	
		
		for (int i = 0; i < this.channels.size(); i++) {
			if (_name.equals(this.channels.get(i).title)) {
				return this.addToChannel(i, thisChat);
			}
		}
		
		if (this.channels.size() >= 20) return 0; // Too many tabs

		if (this.globalPrefs.autoSearchEnabled) {
			this.channels.add(new ChatChannel(_name));
			return this.addToChannel(this.channels.size()-1, thisChat);
		}
		return 0;
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
					track.add(new Integer(cf.chanID));
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
			if (cf.sendToTab && !track.contains(new Integer(cf.chanID))) {
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

	public static String getNewestVersion() {
		try {
			URLConnection conn = new URL("http://dl.dropbox.com/u/8347166/tabbychat_ver.txt").openConnection();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String newestVersion = buffer.readLine();
			buffer.close();
			return newestVersion;
		} catch (Throwable e) {
			printErr("Unable to check for TabbyChat update.");
		}
		return TabbyChat.version;
	}
	
	public void copyTab(int toIndex, int fromIndex) {
		this.channels.set(toIndex, this.channels.get(fromIndex));
	}
	
	public void displayChatLines(Minecraft mc, int index) {
		mc.ingameGUI.getChatGUI().addChatLines(this.channels.get(index).chatLog);
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
	
	public int getChanInd(String _name) {
		for (int i=0; i<this.channels.size(); i++) {
			if (_name.equals(this.channels.get(i).title)) {
				return i;
			}
		}
		return -1;
	}
	
	public ChatLine getChatLine(int _chan, int _line) {
		return this.channels.get(_chan).chatLog.get(_line);
	}

	public boolean matchChannelWithButton(int index, GuiButton btnObj) {
		return (this.channels.get(index).chanID == btnObj.id);
	}
	
	public void pollForUnread(Gui _gui, int _x, int _y, int _tick) {
		int _opacity;
		int tickdiff = _tick - this.lastChat.getUpdatedCounter();
		
		if (tickdiff < 50) {
			float var6 = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
			double var10 = (double)tickdiff / 50.0D;
			var10 = 1.0D - var10;
			var10 *= 10.0D;
			if (var10 < 0.0D) var10 = 0.0D;
			if (var10 > 1.0D) var10 = 1.0D;
			
			var10 *= var10;
			_opacity = (int)(255.0D * var10);
			_opacity = (int)((float)_opacity * var6);
			if (_opacity <= 3) return;
			this.updateButtonLocations();
			for (ChatChannel _chan : this.channels) {
				if (_chan.unread) {
					_chan.unreadNotify(_gui, _x, _y, _opacity); 
				}
			}			
		}
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
		int ret = 0;
		
		for (int z=0; z<theChat.length; z++) {
			filteredChat = theChat[z].getChatLineString();
			for (CustomChatFilter filter : this.serverPrefs.customFilters) {
				if (!filter.applyFilterToDirtyChat(filteredChat)) continue;
				filteredChat = filter.getLastMatchPretty();
				if (filter.sendToTab) {
					_ind = this.getChanIndexByID(filter.chanID);
					if (_ind > 0) goesHere.add(new Integer(_ind));
				}
				if (filter.ding) this.ding(); 
			}
			filteredChatLine[z] = new ChatLine(theChat[z].getUpdatedCounter(), filteredChat, theChat[z].getChatLineID());
		}
		
		
		for (Integer c : goesHere) {
			this.addToChannel(c.intValue(), filteredChatLine);
		}
		if (goesHere.contains(new Integer(this.getActive()))) ret += 1;
		
		String coloredChat = "";
		for (ChatLine cl : theChat)
			coloredChat = coloredChat + cl.getChatLineString();
		
		String cleanedChat = StringUtils.stripControlCodes(coloredChat);
		if (this.globalPrefs.saveLocalLogEnabled) this.globalPrefs.logChat(this.withTimeStamp(cleanedChat));
		
		Matcher findChannelClean = this.chatChannelPatternClean.matcher(cleanedChat);
		Matcher findChannelDirty = this.chatChannelPatternDirty.matcher(coloredChat);
		String cName;
		if (findChannelClean.find() && findChannelDirty.find()) {
			cName = cleanedChat.substring(findChannelClean.start(1), findChannelClean.end(1));
			ret += this.addToChannel(cName, filteredChatLine);
			goesHere.add(new Integer(this.getChanInd(cName)));
		} else {
			Matcher findPMtoMe = this.chatPMtoMePattern.matcher(cleanedChat);
			if (findPMtoMe.find()) {
				cName = cleanedChat.substring(findPMtoMe.start(1), findPMtoMe.end(1));
				ret += this.addToChannel(cName, filteredChatLine);
				goesHere.add(new Integer(this.getChanInd(cName)));
			} else {
				Matcher findPMfromMe = this.chatPMfromMePattern.matcher(cleanedChat);
				if (findPMfromMe.find()) {
					cName = cleanedChat.substring(findPMfromMe.start(1), findPMfromMe.end(1));
					ret += this.addToChannel(cName, filteredChatLine);
					goesHere.add(new Integer(this.getChanInd(cName)));
				}
			}
		}
		
		if (ret == 0) {
			for (Integer read : goesHere) {
				if (read.intValue() > 0)
					this.channels.get(read.intValue()).unread = true;
			}
		}
		this.lastChat = theChat[0];
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

 	public void updateButtonLocations() {
 		if (mc.currentScreen == null) return;
 		
 		int clines = (mc.ingameGUI.getChatGUI().GetChatHeight() < 20) ? mc.ingameGUI.getChatGUI().GetChatHeight() : 20;
 		int vert = mc.currentScreen.height - ((clines - 1) * 9 + 8) - 55;
 		int horiz = 3;
 		int n = this.channels.size();
 		
 		int xOff = 0;
 		int yOff = 0;
 		try {
 			if (TabbyChatUtils.is(mc.ingameGUI.getChatGUI(), "GuiNewChatWrapper")) {
 				Class aHudCls = Class.forName("ahud.ahuditem.DefaultHudItems");
 				Field aHudFld = aHudCls.getField("chat");
 				Object aHudObj = aHudFld.get(null);
 				aHudCls = aHudObj.getClass();
 				aHudFld = aHudCls.getField("config");
 				aHudObj = aHudFld.get(aHudObj);
 				aHudCls = aHudObj.getClass();
 				int dVert = mc.currentScreen.height - 22 - 6 * 18;
 				xOff = aHudCls.getField("posX").getInt(aHudObj) - 3;
 				yOff = aHudCls.getField("posY").getInt(aHudObj) - dVert;
 				horiz += xOff;
 				vert += yOff;
 				if (mc.ingameGUI.getChatGUI().getChatOpen())
 					((GuiChat)mc.currentScreen).scrollBar.setOffset(xOff, yOff);
 			}
 		} catch (Throwable e) {}
 		for (int i = 0; i < n; i++) {
 			if (i > 0)
 				horiz = this.channels.get(i-1).getButtonEnd() + 1;
 			if (horiz + this.channels.get(i).tab.width() > 327) {
 				vert = vert - this.channels.get(i).tab.height() - 1;
 				horiz = 3;
 			}
 			this.channels.get(i).setButtonLoc(horiz, vert);
 			if (this.channels.get(i).tab == null) {
 				this.channels.get(i).setButtonObj(new ChatButton(this.channels.get(i).getID(),
 						horiz,
 						vert,
 						this.channels.get(i).tab.width(),
 						this.channels.get(i).tab.height(),
 						this.channels.get(i).getDisplayTitle()));
 			} else {
 				this.channels.get(i).tab.id = this.channels.get(i).getID();
 				this.channels.get(i).tab.xPosition = horiz;
 				this.channels.get(i).tab.yPosition = vert;
 			}
 		}
 	}
}
