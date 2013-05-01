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
import java.util.Map;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiChat;
import net.minecraft.src.ChatLine;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.StringUtils;
import net.minecraft.src.SoundManager;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiContainer;

public class TabbyChat {
	protected static Minecraft mc;
	private Pattern chatChannelPatternClean = Pattern.compile("^\\[([A-Za-z0-9_]{1,10})\\]");
	private Pattern chatChannelPatternDirty = Pattern.compile("^\\[([A-Za-z0-9_]{1,10})\\]");
	private Pattern chatPMfromMePattern = null;
	private Pattern chatPMtoMePattern = null;
	public static String version = TabbyChatUtils.version;
	protected Calendar cal = Calendar.getInstance();
	public List<TCChatLine> lastChat;
	public LinkedHashMap<String, ChatChannel> channelMap = new LinkedHashMap();
	public int nextID = 3600;
	public GlobalSettings globalPrefs = new GlobalSettings();
	public ServerSettings serverPrefs = new ServerSettings();
	public static TCSettingsGeneral generalSettings;
	public static TCSettingsServer serverSettings;
	public static TCSettingsFilters filterSettings;
	public static TCSettingsAdvanced advancedSettings;
	public static final GuiNewChatTC gnc = GuiNewChatTC.me;
	public static final TabbyChat instance = new TabbyChat();
	
	private TabbyChat() {
		mc = Minecraft.getMinecraft();

		generalSettings = new TCSettingsGeneral(this);
		serverSettings = new TCSettingsServer(this);
		filterSettings = new TCSettingsFilters(this);
		advancedSettings = new TCSettingsAdvanced(this);
		boolean globalLoaded1 = generalSettings.loadSettingsFile();
		boolean globalLoaded2 = advancedSettings.loadSettingsFile();
		if (!globalLoaded1 && !globalLoaded2) {
			this.globalPrefs.loadSettings();
			generalSettings.importSettings();
			advancedSettings.importSettings();
		}
		if (!this.enabled()) this.disable();
		else this.enable();
	}

	protected int addToChannel(String name, TCChatLine thisChat) {
		int ret = 0;
		TCChatLine newChat = this.withTimeStamp(thisChat);
		ChatChannel theChan = this.channelMap.get(name);
		theChan.chatLog.add(0, newChat);
		theChan.trimLog();
		if (theChan.active || this.channelMap.get("*").active)
			ret = 1;
		return ret;		
	}
	
	private void spamCheck(String _chan, List<TCChatLine> lastChat) {
		ChatChannel theChan = this.channelMap.get(_chan);
		String oldChat = "";
		String oldChat2 = "";
		String newChat = "";

		if (theChan.chatLog.size() < lastChat.size()) {
			theChan.hasSpam = false;
			theChan.spamCount = 1;
			return;
		}
		
		int _size = lastChat.size();
		for (int i=0; i<_size; i++) {
			if(lastChat.get(i).getChatLineString() == null || theChan.chatLog.get(i).getChatLineString() == null) continue;
			newChat = newChat + lastChat.get(i).getChatLineString();
			if (generalSettings.timeStampEnable.getValue()) {
				oldChat = theChan.chatLog.get(i).getChatLineString().replaceAll("^"+((TimeStampEnum)generalSettings.timeStampStyle.getValue()).regEx, "") + oldChat;
			} else {
				oldChat = theChan.chatLog.get(i).getChatLineString() + oldChat;
			}
		}
		if (theChan.hasSpam) {
			oldChat2 = oldChat.substring(0, oldChat.length() - 4 - Integer.toString(theChan.spamCount).length());
			oldChat = oldChat2;
		}
		if (oldChat.equals(newChat)) {
			theChan.hasSpam = true;
			theChan.spamCount++;
			for (int i=1; i<_size; i++)
				theChan.chatLog.set(i, this.withTimeStamp(lastChat.get(lastChat.size()-i-1)));
			theChan.chatLog.set(0, new TCChatLine(lastChat.get(lastChat.size()-1).getUpdatedCounter(), this.withTimeStamp(lastChat.get(lastChat.size()-1).getChatLineString()) + " [" + theChan.spamCount + "x]", lastChat.get(lastChat.size()-1).getChatLineID()));
		} else {
			theChan.hasSpam = false;
			theChan.spamCount = 1;
		}
	}

	protected int addToChannel(String _name, List<TCChatLine> thisChat) {
		ChatChannel theChan = this.channelMap.get(_name);
		for (String ichan : Pattern.compile("[ ]?,[ ]?").split(serverSettings.ignoredChannels.getValue())) {
			if (ichan.length() > 0 && _name.equals(ichan)) {
				return 0;
			}
		}

		if (theChan != null) {
			int ret = 0;

			if (generalSettings.groupSpam.getValue()) {
				this.spamCheck(_name, thisChat);
				if (!theChan.hasSpam) {
					for (TCChatLine cl : thisChat) {
						ret += this.addToChannel(_name, cl);
					}
				}
			} else {
				for (TCChatLine cl : thisChat) {
					ret += this.addToChannel(_name, cl);
				}				
			}
			return ret;
		}
					
		if (this.channelMap.size() >= 20) return 0; // Too many tabs

		if (serverSettings.autoChannelSearch.getValue()) {
			this.channelMap.put(_name, new ChatChannel(_name));
			int ret = 0;
			for (TCChatLine cl : thisChat) {
				ret += this.addToChannel(_name, cl);
			}
			return ret;
		}
		return 0;
	}

	private List<TCChatLine> withTimeStamp(List<TCChatLine> _orig) {
		List<TCChatLine> stamped = new ArrayList();
		for (TCChatLine cl : _orig)
			stamped.add(0, this.withTimeStamp(cl));
		return stamped;
	}
	
	private TCChatLine withTimeStamp(TCChatLine _orig) {
		TCChatLine stamped = _orig;
		if (generalSettings.timeStampEnable.getValue()) {
			this.cal = Calendar.getInstance();
			stamped = new TCChatLine(_orig.getUpdatedCounter(), generalSettings.timeStamp.format(this.cal.getTime())+_orig.getChatLineString(), _orig.getChatLineID());
		}
		return stamped;
	}
	
	private String withTimeStamp(String _orig) {
		String stamped = _orig;
		if (generalSettings.timeStampEnable.getValue()) {
			this.cal = Calendar.getInstance();
			stamped = generalSettings.timeStamp.format(this.cal.getTime()) + _orig;
		}
		return stamped;
	}
	
	protected static String getNewestVersion() {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("http://goo.gl/LkiHT").openConnection();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String newestVersion = buffer.readLine();
			buffer.close();
			return newestVersion;
		} catch (Throwable e) {
			printErr("Unable to check for TabbyChat update.");
		}
		return TabbyChat.version;
	}	
	
	protected void disable() {	
		this.channelMap.clear();
		this.channelMap.put("*", new ChatChannel("*"));
	}
	
	protected synchronized void enable() {
		if (!this.channelMap.containsKey("*")) {
			this.channelMap.put("*", new ChatChannel("*"));
			this.channelMap.get("*").active = true;
		}
		serverSettings.updateForServer();
		this.reloadServerData();
		this.loadPatterns();
		this.updateDefaults();
		this.updateFilters();
		if(serverSettings.server != null) this.loadPMPatterns();
		if (generalSettings.saveChatLog.getValue() && serverSettings.server != null) {
			TabbyChatUtils.logChat("\nBEGIN CHAT LOGGING FOR "+serverSettings.serverName+"("+serverSettings.serverIP+") -- "+(new SimpleDateFormat()).format(Calendar.getInstance().getTime()));
		}
	}
	
	private void reloadServerData() {
		boolean serverLoaded1 = serverSettings.loadSettingsFile();
		boolean serverLoaded2 = filterSettings.loadSettingsFile();
		if(!serverLoaded1 && !serverLoaded2) {
			this.serverPrefs.updateForServer();
			this.serverPrefs.loadSettings();
			serverSettings.importSettings();
			filterSettings.importSettings();
		}
		this.loadChannelData();
	}

	protected void loadPatterns() {
		ChannelDelimEnum delims = (ChannelDelimEnum)serverSettings.delimiterChars.getValue();
		
		String colCode = "";
		String fmtCode = "";
		if (serverSettings.delimColorBool.getValue())
			colCode = ((ColorCodeEnum)serverSettings.delimColorCode.getValue()).toCode();
		if (serverSettings.delimFormatBool.getValue())
			fmtCode = ((FormatCodeEnum)serverSettings.delimFormatCode.getValue()).toCode();
		
		String frmt = colCode + fmtCode;

		if (((ColorCodeEnum)serverSettings.delimColorCode.getValue()).toString().equals("White")) {
			frmt = "(" + colCode + ")?" + fmtCode;
		} else if (frmt.length() > 7)
			frmt = "[" + frmt + "]{2}";
		if (frmt.length() > 0)
			frmt = "(?i:"+frmt+")";
		if (frmt.length() == 0)
			frmt = "(?i:\u00A7[0-9A-FK-OR])*";
		
		
		this.chatChannelPatternDirty = Pattern.compile("^(\u00A7r)?"+frmt+"\\"+delims.open()+"([A-Za-z0-9_\u00A7]+)\\"+delims.close());
		this.chatChannelPatternClean = Pattern.compile("^"+"\\"+delims.open()+"([A-Za-z0-9_]{1,"+this.advancedSettings.maxLengthChannelName.getValue()+"})\\"+delims.close());
	}
	
	protected void loadPMPatterns() {
		String me = mc.thePlayer.username;
		StringBuilder toPM = new StringBuilder();
		// Matches '[Player -> me]' and '[Player->Player1]', capturing name of Player
		toPM.append("^").append("\\[(\\w{3,16})[ ]?\\-\\>[ ]?(?:me|").append(me).append(")\\]");
		// Matches 'From Player' and 'Player whispers to you', capturing name of player
		toPM.append("|^From (\\w{3,16})").append("|^(\\w{3,16}) whispers to you");
		this.chatPMtoMePattern = Pattern.compile(toPM.toString());
		
		StringBuilder fromPM = new StringBuilder();
		// Matches '[me -> Player]' and '[Player1->Player]', capturing name of player
		fromPM.append("^").append("\\[(?:me|").append(me).append(")[ ]?\\-\\>[ ]?(\\w{3,16})\\]");
		// Matches 'To Player' and 'You whisper to Player', capturing name of player
		fromPM.append("|^To (\\w{3,16})").append("|^You whisper to ([A-Za-z0-9_]{3,16})");
		this.chatPMfromMePattern = Pattern.compile(fromPM.toString());
	}

	protected void updateFilters() {
		if (!generalSettings.tabbyChatEnable.getValue()) return;
		if (filterSettings.numFilters == 0) return;
		String newName;
		for (int i=0; i<filterSettings.numFilters; i++) {
			newName = filterSettings.sendToTabName(i);
			if (filterSettings.sendToTabBool(i) && 
					!filterSettings.sendToAllTabs(i) &&
					!this.channelMap.containsKey(newName)) {
				this.channelMap.put(newName, new ChatChannel(newName));
			}
		}
	}

	protected void updateDefaults() {
		if (!this.generalSettings.tabbyChatEnable.getValue()) return;
		List<String> dList = new ArrayList(Arrays.asList(Pattern.compile("[ ]?,[ ]?").split(serverSettings.defaultChannels.getValue())));
		int ind;
		for (ChatChannel chan : this.channelMap.values()) {
			ind = dList.indexOf(chan.title);
			if (ind >= 0) dList.remove(ind);
		}
		
		for (String defChan : dList) {
			if (defChan.length() > 0) this.channelMap.put(defChan, new ChatChannel(defChan));
		}
	}
	
	protected void loadChannelData() {
		LinkedHashMap<String, ChatChannel> importData = null;
		File chanDataFile;
		
		String ip = TabbyChat.serverSettings.serverIP;
		if (ip == null || ip.length() == 0) return;
		if (ip.contains(":")) {
			ip = ip.replaceAll(":", "(") + ")";
		}
		
		String pName = "";
		if(mc.thePlayer != null && mc.thePlayer.username != null) pName = mc.thePlayer.username;
		
		File settingsDir = new File(TCSettingsGUI.tabbyChatDir, ip);
		chanDataFile = new File(settingsDir, pName+"_chanData.ser");
		if(!chanDataFile.exists()) return;
		
		FileInputStream cFileStream = null;
		BufferedInputStream cBuffStream = null;
		ObjectInputStream cObjStream = null;
		try {
			cFileStream = new FileInputStream(chanDataFile);
			cBuffStream = new BufferedInputStream(cFileStream);
			cObjStream = new ObjectInputStream(cBuffStream);
			importData = (LinkedHashMap<String, ChatChannel>)cObjStream.readObject();
			cObjStream.close();
			cBuffStream.close();
		} catch (Exception e) {
			printErr("Unable to read channel data file : '" + e.getLocalizedMessage() + "' : " + e.toString());
			return;
		}
		
		if(importData == null) return;
		int oldIDs = 0;
		for(Map.Entry<String, ChatChannel> chan : importData.entrySet()) {
			ChatChannel _new = null;
			if(!this.channelMap.containsKey(chan.getKey())) {
				_new = new ChatChannel(chan.getKey());
				_new.chanID = chan.getValue().chanID;
				this.channelMap.put(_new.title, _new);
			} else {
				_new = this.channelMap.get(chan.getKey());
			}
			_new.alias = chan.getValue().alias;
			_new.active = chan.getValue().active;
			_new.notificationsOn = chan.getValue().notificationsOn;
			_new.cmdPrefix = chan.getValue().cmdPrefix;
			this.addToChannel(chan.getKey(), new TCChatLine(-1, "-- chat history from "+(new SimpleDateFormat()).format(chanDataFile.lastModified()), 0, true));
			_new.importOldChat(chan.getValue().chatLog);
			oldIDs++;
		}
		ChatChannel.nextID = 3600 + oldIDs;
		this.resetDisplayedChat();
	}
	
	protected void storeChannelData() {
		LinkedHashMap<String, ChatChannel> chanData = TabbyChat.instance.channelMap;
		File chanDataFile;
		
		String ip = TabbyChat.serverSettings.serverIP;
		if (ip == null || ip.length() == 0) return;
		if (ip.contains(":")) {
			ip = ip.replaceAll(":", "(") + ")";
		}
		
		String pName = "";
		if(mc.thePlayer != null && mc.thePlayer.username != null) pName = mc.thePlayer.username;
		
		File settingsDir = new File(TCSettingsGUI.tabbyChatDir, ip);
	
		if (!settingsDir.exists())
			settingsDir.mkdirs();
		chanDataFile = new File(settingsDir, pName+"_chanData.ser");
		
		FileOutputStream cFileStream = null;
		BufferedOutputStream cBuffStream = null;
		ObjectOutputStream cObjStream = null;		
		try {
			cFileStream = new FileOutputStream(chanDataFile);
			cBuffStream = new BufferedOutputStream(cFileStream);
			cObjStream = new ObjectOutputStream(cBuffStream);
			cObjStream.writeObject(chanData);
			cObjStream.flush();
			cObjStream.close();
			cBuffStream.close();
		} catch (Exception e) {
			printErr("Unable to write channel data to file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}
	
	protected void finalize() {
		this.storeChannelData();
	}
	
	public void checkServer() {
		if (mc.getServerData() == null) return;
		
		if(serverSettings.server == null) {
			BackgroundUpdateCheck buc = new BackgroundUpdateCheck();
			buc.start();
		}
		
		if (!mc.getServerData().serverIP.equalsIgnoreCase(serverSettings.serverIP)) {
			this.storeChannelData();
			this.channelMap.clear();
			if (this.enabled()) {
				this.enable();
				this.resetDisplayedChat();
			} else this.disable();
		}
		return;
	}

	public void copyTab(String toName, String fromName) {
		this.channelMap.put(toName, this.channelMap.get(fromName));
	}

	public boolean enabled() {
		if (mc.isSingleplayer()) {
			return false;
		} else
			return generalSettings.tabbyChatEnable.getValue();
	}
	
	public List<String> getActive() {
		int n = this.channelMap.size();
		List<String> actives = new ArrayList<String>(n);

		for (ChatChannel chan : this.channelMap.values()) {
			if (chan.active)
				actives.add(chan.title);
		}
		return actives;
	}

	public ChatLine getChatLine(String _chan, int _line) {
		return this.channelMap.get(_chan).chatLog.get(_line);
	}
	
	public void pollForUnread(Gui _gui, int _y, int _tick) {
		int _opacity = 0;
		int tickdiff;

		try {
			if(this.lastChat == null || this.lastChat.size() == 0) return;
			tickdiff = _tick - this.lastChat.get(0).getUpdatedCounter();
		} catch (Exception e) { 
			tickdiff = 50;
		}
		
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
				if (chan.unread && chan.notificationsOn)
					chan.unreadNotify(_gui, _y, _opacity);
			}
		}
	}
	
	public static void printErr(String err) {
		System.err.println(err);
		mc.getLogAgent().logWarning(err);
	}
	
	public int processChat(List<TCChatLine> theChat) {
		ArrayList<TCChatLine> filteredChatLine = new ArrayList<TCChatLine>(theChat.size());
		List<String> toTabs = new ArrayList<String>();
		toTabs.add("*");

		int _ind;
		int ret = 0;
		boolean skip = false;
		
		int n = theChat.size();
		StringBuilder filteredChat = new StringBuilder(theChat.get(0).getChatLineString().length() * n);
		for (int z=0; z<n; z++)
			filteredChat.append(theChat.get(z).getChatLineString());
	
		for (int i = 0; i < filterSettings.numFilters; i++) {
			if (!filterSettings.applyFilterToDirtyChat(i, filteredChat.toString())) continue;
			if (filterSettings.removeMatches(i)) {
				toTabs.clear();
				toTabs.add("*");
				skip = true;
				break;
			}
			filteredChat = new StringBuilder(filterSettings.getLastMatchPretty());
			if (filterSettings.sendToTabBool(i)) {
				if (filterSettings.sendToAllTabs(i)) {
					toTabs.clear();
					for (ChatChannel chan : this.channelMap.values())
						toTabs.add(chan.title);
					skip = true;
					continue;
				} else {
					String destTab = filterSettings.sendToTabName(i);
					if (!this.channelMap.containsKey(destTab)) {
						this.channelMap.put(destTab, new ChatChannel(destTab));
					}
					if (!toTabs.contains(destTab))
						toTabs.add(destTab);
				}
			}
			if (filterSettings.audioNotificationBool(i))
				filterSettings.audioNotification(i);
		} 
		
		Iterator splitChat = mc.fontRenderer.listFormattedStringToWidth(filteredChat.toString(), gnc.chatWidth).iterator();
		boolean firstline = true;
		while (splitChat.hasNext()) {
			String _line = (String)splitChat.next();
			if (!firstline)
				_line = " " + _line;
			filteredChatLine.add(new TCChatLine(theChat.get(0).getUpdatedCounter(), _line, theChat.get(0).getChatLineID()));
			firstline = false;
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
		if (generalSettings.saveChatLog.getValue()) TabbyChatUtils.logChat(this.withTimeStamp(cleanedChat));
		
		if (!skip) {
			Matcher findChannelClean = this.chatChannelPatternClean.matcher(cleanedChat);
			Matcher findChannelDirty = this.chatChannelPatternDirty.matcher(coloredChat);
			String cName = null;
			boolean dirtyValid = (!serverSettings.delimColorBool.getValue() && !serverSettings.delimFormatBool.getValue()) ? true : findChannelDirty.find();
			if (findChannelClean.find() && dirtyValid) {
				cName = cleanedChat.substring(findChannelClean.start(1), findChannelClean.end(1));
				ret += this.addToChannel(cName, filteredChatLine);
				toTabs.add(cName);
			} else if(this.chatPMtoMePattern != null){
				Matcher findPMtoMe = this.chatPMtoMePattern.matcher(cleanedChat);
				if (findPMtoMe.find()) {					
					for(int i=1;i<=findPMtoMe.groupCount();i++) {
						if(findPMtoMe.group(i) != null) {
							cName = findPMtoMe.group(i);
							ChatChannel newPM = new ChatChannel(cName);
							newPM.cmdPrefix = "/msg "+cName;
							this.channelMap.put(cName, newPM);							
							ret += this.addToChannel(cName, filteredChatLine);							
							toTabs.add(cName);
							break;
						}
					}
				} else if(this.chatPMfromMePattern != null) {
					Matcher findPMfromMe = this.chatPMfromMePattern.matcher(cleanedChat);
					if (findPMfromMe.find()) {						
						for(int i=1;i<=findPMfromMe.groupCount();i++) {
							if(findPMfromMe.group(i) != null) {
								cName = findPMfromMe.group(i);
								ChatChannel newPM = new ChatChannel(cName);
								newPM.cmdPrefix = "/msg "+cName;
								this.channelMap.put(cName, newPM);
								ret += this.addToChannel(cName, filteredChatLine);
								toTabs.add(cName);
								break;
							}
						}
					}
				}
			}
		}
		
		if (ret == 0) {
			for (String c : toTabs) {
				if (c != "*" && this.channelMap.containsKey(c)) {
					this.channelMap.get(c).unread = true;
				}
			}
		}
		
		List<String> activeTabs = this.getActive();
		if (generalSettings.groupSpam.getValue() && activeTabs.size() > 0) {
			if (toTabs.contains(activeTabs.get(0)))
				this.lastChat = this.channelMap.get(activeTabs.get(0)).chatLog.subList(0, filteredChatLine.size());
			else
				this.lastChat = this.withTimeStamp(filteredChatLine);
		} else
			this.lastChat = this.withTimeStamp(filteredChatLine);
		
		if (ret > 0) {
			if (generalSettings.groupSpam.getValue() && this.channelMap.get(activeTabs.get(0)).hasSpam) {
				gnc.setChatLines(0, this.lastChat);
			} else {
				gnc.addChatLines(0, this.lastChat);
			}
		}

		return ret;
	}
	
	public void removeTab(String _name) {
		this.channelMap.remove(_name);
	}

 	public void resetDisplayedChat() {
 		gnc.clearChatLines();
 		List<String> actives = this.getActive();
 		if (actives.size() < 1) return;
 		gnc.addChatLines(this.channelMap.get(actives.get(0)).chatLog);
 		int n = actives.size();
 		for (int i = 1; i < n; i++) {
 			gnc.mergeChatLines(this.channelMap.get(actives.get(i)).chatLog);
 		}
 	}
 	
 	public void updateButtonLocations() {
 		int xOff = 0;
 		int yOff = 0; 
 		ScaledResolution sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
 		
 		
 		int maxlines = gnc.getHeightSetting() / 9;
 		int clines = (gnc.GetChatHeight() < maxlines) ? gnc.GetChatHeight() : maxlines;
 		int vert = sr.getScaledHeight() - gnc.chatHeight - 51;;
 		int horiz = 5;
 		int n = this.channelMap.size();
 		
/* 		try {
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
 				if (gnc.getChatOpen()) ((GuiChatTC)mc.currentScreen).scrollBar.setOffset(xOff, yOff);
 			}
 		} catch (Throwable e) {}*/
 		
 		int i = 0;
 		for (ChatChannel chan : this.channelMap.values()) {
 			chan.tab.width(mc.fontRenderer.getStringWidth(chan.getDisplayTitle()) + 8);
 			
 			if (horiz + chan.tab.width() > gnc.chatWidth - 5) {
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
