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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import net.minecraft.src.MathHelper;
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
	protected static String version = "1.5.00";
	protected Calendar cal = Calendar.getInstance();
	public List<ChatLine> lastChat;
	public LinkedHashMap<String, ChatChannel> channelMap = new LinkedHashMap();
	public int nextID = 3600;
	public GlobalSettings globalPrefs = new GlobalSettings();
	public ServerSettings serverPrefs = new ServerSettings();
	public GuiSettings prefsWindow;
	public GuiChatFilters filtersWindow;
	public TCSettingsGeneral generalSettings;
	public TCSettingsServer serverSettings;
	public TCSettingsFilters filterSettings;
	public TCSettingsAdvanced advancedSettings;
	public static final TabbyChat instance = new TabbyChat();
	
	private TabbyChat() {
		mc = Minecraft.getMinecraft();
		
		this.prefsWindow = new GuiSettings(this);
		this.filtersWindow = new GuiChatFilters(this);
		this.generalSettings = new TCSettingsGeneral(this);
		this.serverSettings = new TCSettingsServer(this);
		this.filterSettings = new TCSettingsFilters(this);
		this.advancedSettings = new TCSettingsAdvanced(this);
		this.globalPrefs.loadSettings();
		this.generalSettings.loadSettingsFile();
		this.advancedSettings.loadSettingsFile();
		if (!this.enabled())
			this.disable();
		else {
			this.enable();
			this.channelMap.get("*").active = true;
			
			String ver = TabbyChat.getNewestVersion();
			ArrayList firstmsg = new ArrayList<ChatLine>();
			if (!ver.equals(version)) {
				ver = "\u00A77TabbyChat: An update is available!  (Current version is "+version+", newest is "+ver+")";
				String ver2 = "\u00A77Visit the TabbyChat forum thread at minecraftforum.net to download.";
				ChatLine updateLine = new ChatLine(mc.ingameGUI.getUpdateCounter(), ver, 0);
				ChatLine updateLine2 = new ChatLine(mc.ingameGUI.getUpdateCounter(), ver2, 0);
				this.channelMap.put("TabbyChat", new ChatChannel("TabbyChat"));
				this.addToChannel("*", updateLine);
				this.addToChannel("*", updateLine2);
				this.addToChannel("TabbyChat", updateLine);
				this.addToChannel("TabbyChat", updateLine2);
				firstmsg.add(updateLine);
				firstmsg.add(updateLine2);				
			} else {
				firstmsg.add(new ChatLine(mc.ingameGUI.getUpdateCounter(), "", 0));
			}
			this.lastChat = firstmsg;
			mc.ingameGUI.getChatGUI().addChatLines(firstmsg);
		}
	}

	private int addToChannel(String name, ChatLine thisChat) {
		int ret = 0;
		ChatLine newChat = this.withTimeStamp(thisChat);
		ChatChannel theChan = this.channelMap.get(name);
		theChan.chatLog.add(0, newChat);
		theChan.trimLog();
		if (theChan.active || this.channelMap.get("*").active)
			ret = 1;
		return ret;		
	}

	private int addToChannel(String _name, List<ChatLine> thisChat) {
		for (String ichan : Pattern.compile("[ ]?,[ ]?").split(this.serverSettings.ignoredChannels.getValue())) {
			if (ichan.length() > 0 && _name.equals(ichan)) {
				return 0;
			}
		}

		if (this.channelMap.containsKey(_name)) {
			int ret = 0;
			for (ChatLine cl : thisChat) {
				ret += this.addToChannel(_name, cl);
			}
			return ret;
		}
					
		if (this.channelMap.size() >= 20) return 0; // Too many tabs

		if (this.serverSettings.autoChannelSearch.getValue()) {
			this.channelMap.put(_name, new ChatChannel(_name));
			int ret = 0;
			for (ChatLine cl : thisChat) {
				ret += this.addToChannel(_name, cl);
			}
			return ret;
		}
		return 0;
	}

	private ChatLine withTimeStamp(ChatLine _orig) {
		ChatLine stamped = _orig;
		if (this.generalSettings.timeStampEnable.getValue()) {
			this.cal = Calendar.getInstance();
			stamped = new ChatLine(_orig.getUpdatedCounter(), this.globalPrefs.timeStamp.format(this.cal.getTime())+_orig.getChatLineString(), _orig.getChatLineID());
		}
		return stamped;
	}
	
	private String withTimeStamp(String _orig) {
		String stamped = _orig;
		if (this.generalSettings.timeStampEnable.getValue()) {
			this.cal = Calendar.getInstance();
			stamped = this.globalPrefs.timeStamp.format(this.cal.getTime()) + _orig;
		}
		return stamped;
	}
		
	protected void ding() {
		mc.sndManager.playSoundFX("random.orb", 1.0F, 1.0F);
	}
	
	protected void disable() {	
		this.channelMap.clear();
		this.channelMap.put("*", new ChatChannel("*"));
	}
	
	protected void enable() {
		if (!this.channelMap.containsKey("*")) {
			this.channelMap.put("*", new ChatChannel("*"));
			this.channelMap.get("*").active = true;
		}
		this.serverPrefs.updateForServer();
		this.serverPrefs.loadSettings();
		this.serverSettings.loadSettingsFile();
		this.filterSettings.loadSettingsFile();
		this.loadPatterns();
		this.updateDefaults();
		this.updateFilters();
	}

	protected void loadPatterns() {
		ChannelDelimEnum delims = (ChannelDelimEnum)this.serverSettings.delimiterChars.getValue();
		
		String colCode = "";
		String fmtCode = "";
		if (this.serverSettings.delimColorBool.getValue())
			colCode = ((ColorCodeEnum)this.serverSettings.delimColorCode.getValue()).toCode();
		if (this.serverSettings.delimFormatBool.getValue())
			fmtCode = ((FormatCodeEnum)this.serverSettings.delimFormatCode.getValue()).toCode();
		
		String frmt = colCode + fmtCode;

		if (((ColorCodeEnum)this.serverSettings.delimColorCode.getValue()).toString().equals("White")) {
			frmt = "(" + colCode + ")?" + fmtCode;
		} else if (frmt.length() > 7)
			frmt = "[" + frmt + "]{2}";
		if (frmt.length() > 0)
			frmt = "(?i:"+frmt+")";
		if (frmt.length() == 0)
			frmt = "(?i:\u00A7[0-9A-FK-OR])*";
		
		
		this.chatChannelPatternDirty = Pattern.compile("^"+frmt+"\\"+delims.open()+"([A-Za-z0-9_\u00A7]+)\\"+delims.close());
		this.chatChannelPatternClean = Pattern.compile("^"+"\\"+delims.open()+"([A-Za-z0-9_]{1,"+this.advancedSettings.maxLengthChannelName.getValue()+"})\\"+delims.close());
		this.chatPMtoMePattern = Pattern.compile("^"+"\\"+delims.open()+"([A-Za-z0-9_]{1,16})[ ]\\-\\>[ ](?:me)\\"+delims.close());
		this.chatPMfromMePattern = Pattern.compile("^"+"\\"+delims.open()+"(?:me)[ ]\\-\\>[ ]([A-Za-z0-9_]{1,16})\\"+delims.close());
	}

	protected void updateFilters() {
		if (!this.generalSettings.tabbyChatEnable.getValue()) return;
		if (this.filterSettings.numFilters == 0) return;
		for (int i=0; i<this.filterSettings.numFilters; i++) {
			if (this.filterSettings.sendToTabBool(i) && !this.channelMap.containsKey(this.filterSettings.sendToTabName(i))) {
				String newName = this.filterSettings.sendToTabName(i);
				this.channelMap.put(newName, new ChatChannel(newName));
			}
		}
	}

	protected void updateDefaults() {
		if (!this.generalSettings.tabbyChatEnable.getValue()) return;
		List<String> dList = new ArrayList(Arrays.asList(Pattern.compile("[ ]?,[ ]?").split(this.serverSettings.defaultChannels.getValue())));
		int ind;
		for (ChatChannel chan : this.channelMap.values()) {
			ind = dList.indexOf(chan.title);
			if (ind >= 0) dList.remove(ind);
		}
		
		for (String defChan : dList) {
			if (defChan.length() > 0) this.channelMap.put(defChan, new ChatChannel(defChan));
		}
	}
	
	public void checkServer() {
		if (mc.getServerData() == null)
			return;
		
		if (mc.getServerData().serverIP != this.serverPrefs.ip) {
			this.channelMap.clear();
			if (this.enabled())
				this.enable();
			else
				this.disable();
		}
		return;
	}

	public void copyTab(String toName, String fromName) {
		this.channelMap.put(toName, this.channelMap.get(fromName));
	}

	public void displayChatLines(Minecraft mc, String cName) {
		mc.ingameGUI.getChatGUI().addChatLines(this.channelMap.get(cName).chatLog);
	}
	
	public boolean enabled() {
		if (mc.isSingleplayer()) {
			return false;
		} else
			return this.generalSettings.tabbyChatEnable.getValue();
	}
	
	public List<String> getActive() {
		int n = this.channelMap.size();
		List<String> actives = new ArrayList<String>(n);

		for (ChatChannel chan : this.channelMap.values()) {
			if (chan.active)
				actives.add(new String(chan.title));
		}
		return actives;
	}

	public ChatLine getChatLine(String _chan, int _line) {
		return this.channelMap.get(_chan).chatLog.get(_line);
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

	public void pollForUnread(Gui _gui, int _y, int _tick) {
		int _opacity = 0;
		if (this.lastChat == null || this.lastChat.size() == 0) return;
		int tickdiff = _tick - this.lastChat.get(0).getUpdatedCounter();
		
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

			for (ChatChannel chan : this.channelMap.values()) {
				if (chan.unread)
					chan.unreadNotify(_gui, _y, _opacity);
			}
		}
	}
	
	public static void printErr(String err) {
		System.err.println(err);
	}
	
	public int processChat(List<ChatLine> theChat) {
		ArrayList<ChatLine> filteredChatLine = new ArrayList<ChatLine>(theChat.size());
		List<String> toTabs = new ArrayList<String>();
		toTabs.add("*");

		int _ind;
		int ret = 0;
		boolean skip = false;
		
		int n = theChat.size();
		StringBuilder filteredChat = new StringBuilder(theChat.get(0).getChatLineString().length() * n);
		for (int z=0; z<n; z++)
			filteredChat.append(theChat.get(z).getChatLineString());
	
		for (int i = 0; i < this.filterSettings.numFilters; i++) {
			if (!this.filterSettings.applyFilterToDirtyChat(i, filteredChat.toString())) continue;
			if (this.filterSettings.removeMatches(i)) {
				skip = true;
				break;
			}
			filteredChat = new StringBuilder(this.filterSettings.getLastMatchPretty());
			if (this.filterSettings.sendToTabBool(i)) {
				if (this.filterSettings.sendToAllTabs(i)) {
					toTabs.clear();
					for (ChatChannel chan : this.channelMap.values())
						toTabs.add(chan.title);
					skip = true;
					continue;
				} else {
					String destTab = this.filterSettings.sendToTabName(i);
					if (!this.channelMap.containsKey(destTab)) {
						this.channelMap.put(destTab, new ChatChannel(destTab));
					}
					if (!toTabs.contains(destTab))
						toTabs.add(destTab);
				}
			}
			if (this.filterSettings.audioNotificationBool(i))
				this.filterSettings.audioNotification(i);
		} 
		
		Iterator splitChat = mc.fontRenderer.listFormattedStringToWidth(filteredChat.toString(), MathHelper.floor_float((float)mc.ingameGUI.getChatGUI().func_96126_f() / mc.ingameGUI.getChatGUI().func_96131_h())).iterator();
		while (splitChat.hasNext()) {
			filteredChatLine.add(new ChatLine(theChat.get(0).getUpdatedCounter(), (String)splitChat.next(), theChat.get(0).getChatLineID()));
		}
		
		for (String c : toTabs) {
			this.addToChannel(c, filteredChatLine);
		}

		for (String _act : this.getActive()) {
			if (toTabs.contains(_act))
				ret++;
		}
		
		String coloredChat = "";
		for (ChatLine cl : theChat)
			coloredChat = coloredChat + cl.getChatLineString();
		String cleanedChat = StringUtils.stripControlCodes(coloredChat);
		if (this.generalSettings.saveChatLog.getValue()) TabbyChatUtils.logChat(this.withTimeStamp(cleanedChat));
		
		if (!skip) {
			Matcher findChannelClean = this.chatChannelPatternClean.matcher(cleanedChat);
			Matcher findChannelDirty = this.chatChannelPatternDirty.matcher(coloredChat);
			String cName;
			if (findChannelClean.find() && findChannelDirty.find()) {
				cName = cleanedChat.substring(findChannelClean.start(1), findChannelClean.end(1));
				ret += this.addToChannel(cName, filteredChatLine);
				toTabs.add(cName);
			} else {
				Matcher findPMtoMe = this.chatPMtoMePattern.matcher(cleanedChat);
				if (findPMtoMe.find()) {
					cName = cleanedChat.substring(findPMtoMe.start(1), findPMtoMe.end(1));
					ret += this.addToChannel(cName, filteredChatLine);
					toTabs.add(cName);
				} else {
					Matcher findPMfromMe = this.chatPMfromMePattern.matcher(cleanedChat);
					if (findPMfromMe.find()) {
						cName = cleanedChat.substring(findPMfromMe.start(1), findPMfromMe.end(1));
						ret += this.addToChannel(cName, filteredChatLine);
						toTabs.add(cName);
					}
				}
			}
		}
		
		if (ret == 0) {
			for (String c : toTabs) {
				if (c != "*")
					this.channelMap.get(c).unread = true;
			}
		}
		this.lastChat = filteredChatLine;
				
		if (this.generalSettings.timeStampEnable.getValue()) {
			for (int i=0; i<this.lastChat.size(); i++) {
				this.lastChat.set(i, this.withTimeStamp(this.lastChat.get(i)));
			}
		}
		return ret;
	}
	
	public void removeTab(String _name) {
		this.channelMap.remove(_name);
	}

 	public void resetDisplayedChat() {
 		mc.ingameGUI.getChatGUI().clearChatLines();
 		List<String> actives = this.getActive();
 		if (actives.size() < 1) return;
 		mc.ingameGUI.getChatGUI().addChatLines(this.channelMap.get(actives.get(0)).chatLog);
 		int n = actives.size();
 		for (int i = 1; i < n; i++) {
 			mc.ingameGUI.getChatGUI().mergeChatLines(this.channelMap.get(actives.get(i)).chatLog);
 		}
 	}
 	
 	public void updateButtonLocations() {
 		int xOff = 0;
 		int yOff = 0; 		
 		boolean screenPresent = (mc.currentScreen != null);
 		
 		int maxlines = mc.ingameGUI.getChatGUI().getHeightSetting() / 9;
 		int clines = (mc.ingameGUI.getChatGUI().GetChatHeight() < maxlines) ? mc.ingameGUI.getChatGUI().GetChatHeight() : maxlines;
 		int vert = screenPresent ? mc.currentScreen.height - (clines * 9) - 42 + yOff: 0;
 		int horiz = 5;
 		int n = this.channelMap.size();
 		
 		try {
 			if (TabbyChatUtils.is(mc.ingameGUI.getChatGUI(), "GuiNewChatWrapper")) {
 				Class aHudCls = Class.forName("advancedhud.ahuditem.DefaultHudItems");
 				Field aHudFld = aHudCls.getField("chat");
 				Object aHudObj = aHudFld.get(null);
 				aHudCls = Class.forName("advancedhud.ahuditem.HudItem");
 				int dVert = mc.currentScreen.height - 22 - 6 * 18;
 				xOff = aHudCls.getField("posX").getInt(aHudObj) - 3;
 				yOff = aHudCls.getField("posY").getInt(aHudObj) - dVert;
 				horiz += xOff;
 				vert -= yOff;
 				if (mc.ingameGUI.getChatGUI().getChatOpen() && screenPresent)
 					((GuiChat)mc.currentScreen).scrollBar.setOffset(xOff, yOff);
 			}
 		} catch (Throwable e) {}
 		
 		int i = 0;
 		for (ChatChannel chan : this.channelMap.values()) {
 			if (horiz + chan.tab.width() > 327) {
 				vert = vert - chan.tab.height();
 				horiz = 5;
 			}
 			chan.setButtonLoc(horiz, vert);
 			if (chan.tab == null) {
 				chan.setButtonObj(new ChatButton(chan.getID(), horiz, vert, chan.tab.width(), chan.tab.height(), chan.getDisplayTitle()));
 			} else {
 				chan.tab.id = chan.getID();
 				chan.tab.xPosition = horiz;
 				chan.tab.yPosition = vert;
 				chan.tab.displayString = chan.getDisplayTitle();
 			}			
 			horiz = chan.getButtonEnd() + 1;
 		}
 	}
}
