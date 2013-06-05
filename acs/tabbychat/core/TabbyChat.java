package acs.tabbychat.core;

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
import java.util.Collections;
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
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.lwjgl.input.Keyboard;

import acs.tabbychat.gui.ChatBox;
import acs.tabbychat.gui.ChatButton;
import acs.tabbychat.gui.TCSettingsAdvanced;
import acs.tabbychat.gui.TCSettingsFilters;
import acs.tabbychat.gui.TCSettingsGUI;
import acs.tabbychat.gui.TCSettingsGeneral;
import acs.tabbychat.gui.TCSettingsServer;
import acs.tabbychat.lang.TCTranslate;
import acs.tabbychat.settings.ChannelDelimEnum;
import acs.tabbychat.settings.ColorCodeEnum;
import acs.tabbychat.settings.FormatCodeEnum;
import acs.tabbychat.settings.TimeStampEnum;
import acs.tabbychat.threads.BackgroundUpdateCheck;
import acs.tabbychat.util.TabbyChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Gui;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.StringUtils;

public class TabbyChat {
	public static Minecraft mc;
	private Pattern chatChannelPatternClean = Pattern.compile("^\\[([A-Za-z0-9_]{1,10})\\]");
	private Pattern chatChannelPatternDirty = Pattern.compile("^\\[([A-Za-z0-9_]{1,10})\\]");
	private Pattern chatPMfromMePattern = null;
	private Pattern chatPMtoMePattern = null;
	public static String version = TabbyChatUtils.version;
	public static TCTranslate translator;
	protected Calendar cal = Calendar.getInstance();
	private volatile List<TCChatLine> lastChat = new ArrayList();
	public LinkedHashMap<String, ChatChannel> channelMap = new LinkedHashMap();
	public int nextID = 3600;
	public static boolean defaultUnicode;
	public static TCSettingsGeneral generalSettings;
	public static TCSettingsServer serverSettings;
	public static TCSettingsFilters filterSettings;
	public static TCSettingsAdvanced advancedSettings;
	protected Semaphore serverDataLock = new Semaphore(0, true);
	private final ReentrantReadWriteLock lastChatLock = new ReentrantReadWriteLock(true);
	private final Lock lastChatReadLock = lastChatLock.readLock();
	private final Lock lastChatWriteLock = lastChatLock.writeLock();
	public static GuiNewChatTC gnc;
	public static final TabbyChat instance = new TabbyChat();
	
	private TabbyChat() {}
	
	public void postInit() {
		mc = Minecraft.getMinecraft();
		gnc = GuiNewChatTC.me;
		translator = new TCTranslate(mc.gameSettings.language);
		generalSettings = new TCSettingsGeneral(this);
		advancedSettings = new TCSettingsAdvanced(this);
		filterSettings = new TCSettingsFilters(this);
		serverSettings = new TCSettingsServer(this);
		generalSettings.loadSettingsFile();
		advancedSettings.loadSettingsFile();
		defaultUnicode = mc.fontRenderer.getUnicodeFlag();
		if (!this.enabled()) this.disable();
		else {
			this.enable();
		}
	}
	
	private static String getNewestVersion() {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL("http://tabbychat.port0.org/tabbychat/current_version.php?mc=1.5.2").openConnection();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String newestVersion = buffer.readLine();
			buffer.close();
			return newestVersion;
		} catch (Throwable e) {
			printErr("Unable to check for TabbyChat update.");
		}
		return TabbyChat.version;
	}
	
	public static void printErr(String err) {
		System.err.println("[TabbyChat] "+err);
		mc.getLogAgent().logWarning("[TABBYCHAT] "+err);
	}
	
	public static void printException(String err, Exception e) {
		System.err.println("[TabbyChat] "+err);
		mc.getLogAgent().logWarningException("[TABBYCHAT] "+err, e);
	}
	
	public static void printMessageToChat(String msg) {
		if(TabbyChat.instance == null) return;
		if(!TabbyChat.instance.channelMap.containsKey("TabbyChat")) {
			TabbyChat.instance.channelMap.put("TabbyChat", new ChatChannel("TabbyChat"));
		}
		
		List<String> split = mc.fontRenderer.listFormattedStringToWidth(msg, ChatBox.current.width);
		for(String splitMsg : split) {
			TabbyChat.instance.addToChannel("TabbyChat", new TCChatLine(mc.ingameGUI.getUpdateCounter(), splitMsg, 0, true));
		}
		TabbyChat.instance.channelMap.get("TabbyChat").unread = true;
	}
	
	public int addToChannel(String _name, List<TCChatLine> thisChat) {
		int ret = 0;
		ChatChannel theChan = this.channelMap.get(_name);
		if (theChan != null && generalSettings.groupSpam.getValue()) {	
			this.spamCheck(theChan, thisChat);
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
	
	public int addToChannel(String name, TCChatLine thisChat) {
		if(serverSettings.ignoredChanList.contains(name)) return 0;
		
		TCChatLine newChat = this.withTimeStamp(thisChat);
		ChatChannel theChan = this.channelMap.get(name);
		if(theChan == null) {
			if(this.channelMap.size() >= 20) return 0;
			if(serverSettings.autoChannelSearch.getValue()) {
				theChan = new ChatChannel(name);
				this.channelMap.put(name, theChan);
			} else return 0;
		}
		
		theChan.addChat(newChat);
		theChan.trimLog();
		if (theChan.active || this.channelMap.get("*").active)
			return 1;
		return 0;
	}
	
	public boolean channelExists(String name) {
		return (this.channelMap.get(name) != null);
	}
	
	public void checkServer() {
		if (mc.getServerData() == null) return;
		
		if(serverSettings.server == null) {
			BackgroundUpdateCheck buc = new BackgroundUpdateCheck(this.getNewestVersion());
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
	
	public void createNewChannel(String name) {
		if(this.channelExists(name)) return;
		if(name == null || name.length() <= 0 || this.channelMap.size() >= 20) return;
		this.channelMap.put(name, new ChatChannel(name));
		return;
	}
	
	public void copyTab(String toName, String fromName) {
		this.channelMap.put(toName, this.channelMap.get(fromName));
	}
	
	public void disable() {	
		this.channelMap.clear();
		this.channelMap.put("*", new ChatChannel("*"));
	}	
	
	public void enable() {
		if (!this.channelMap.containsKey("*")) {
			this.channelMap.put("*", new ChatChannel("*"));
			this.channelMap.get("*").active = true;
		}
		
		this.serverDataLock.tryAcquire();
		serverSettings.updateForServer();
		this.reloadServerData();
		this.reloadSettingsData(false);
		if(serverSettings.server != null) this.loadPMPatterns();
		this.serverDataLock.release();

		if (generalSettings.saveChatLog.getValue() && serverSettings.server != null) {
			TabbyChatUtils.logChat("\nBEGIN CHAT LOGGING FOR "+serverSettings.serverName+"("+serverSettings.serverIP+") -- "+(new SimpleDateFormat()).format(Calendar.getInstance().getTime()));
		}
	}
	
	public boolean enabled() {
		if (mc.isSingleplayer()) {
			return false;
		} else
			return generalSettings.tabbyChatEnable.getValue();
	}
	
	protected void finalize() {
		this.storeChannelData();
	}

	public List<String> getActive() {
		int n = this.channelMap.size();
		List<String> actives = new ArrayList<String>(n);

		for (ChatChannel chan : this.channelMap.values()) {
			if (chan.active)
				actives.add(chan.getTitle());
		}
		return actives;
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
		try {
			for(Map.Entry<String, ChatChannel> chan : importData.entrySet()) {
				if(chan.getKey().contentEquals("TabbyChat")) continue;
				ChatChannel _new = null;
				if(!this.channelMap.containsKey(chan.getKey())) {
					_new = new ChatChannel(chan.getKey());
					_new.chanID = chan.getValue().chanID;
					this.channelMap.put(_new.getTitle(), _new);
				} else {
					_new = this.channelMap.get(chan.getKey());
				}
				_new.setAlias(chan.getValue().getAlias());
				_new.active = chan.getValue().active;
				_new.notificationsOn = chan.getValue().notificationsOn;
				_new.cmdPrefix = chan.getValue().cmdPrefix;
				this.addToChannel(chan.getKey(), new TCChatLine(-1, "-- chat history from "+(new SimpleDateFormat()).format(chanDataFile.lastModified()), 0, true));
				_new.importOldChat(chan.getValue());
				oldIDs++;
			}
		} catch (ClassCastException e) {
			this.printMessageToChat("Unable to load channel history data due to upgrade (sorry!)");
		}
		ChatChannel.nextID = 3600 + oldIDs;
		this.resetDisplayedChat();
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
		StringBuilder toMePM = new StringBuilder();
		StringBuilder fromMePM = new StringBuilder();
		
		// Matches '[Player -> me]' and '[me -> Player]'
		toMePM.append("^\\[(\\w{3,16})[ ]?\\-\\>[ ]?me\\]");
		fromMePM.append("^\\[me[ ]?\\-\\>[ ]?(\\w{3,16})\\]");
		
		// Matches 'From Player' and 'From Player'
		toMePM.append("|^From (\\w{3,16})[ ]?:");
		fromMePM.append("|^To (\\w{3,16})[ ]?:");
		
		// Matches 'Player whispers to you' and 'You whisper to Player'
		toMePM.append("|^(\\w{3,16}) whispers to you");
		fromMePM.append("|^You whisper to (\\w{3,16})");
		
		if(mc.thePlayer != null && mc.thePlayer.username != null) {
			String me = mc.thePlayer.username;
			
			// Matches '[Player->Player1]' and '[Player1->Player]'
			toMePM.append("|^\\[(\\w{3,16})[ ]?\\-\\>[ ]?").append(me).append("\\]");
			fromMePM.append("|^\\[").append(me).append("[ ]?\\-\\>[ ]?(\\w{3,16})\\]");
		}

		this.chatPMtoMePattern = Pattern.compile(toMePM.toString());
		this.chatPMfromMePattern = Pattern.compile(fromMePM.toString());
	}
	
	public void pollForUnread(Gui _gui, int _tick) {
		int _opacity = 0;
		int tickdiff = 50;
		
		this.lastChatReadLock.lock();
		try {
			if(this.lastChat != null && this.lastChat.size() > 0) tickdiff = _tick - this.lastChat.get(0).getUpdatedCounter();
		} finally {
			this.lastChatReadLock.unlock();
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
			ChatBox.updateTabs(this.channelMap);

			for (ChatChannel chan : this.channelMap.values()) {
				if (chan.unread && chan.notificationsOn)
					chan.unreadNotify(_gui, _opacity);
			}
		}
	}
	
	public int processChat(List<TCChatLine> theChat) {
		if(this.serverDataLock.availablePermits() == 0) {
			this.serverDataLock.acquireUninterruptibly();
			this.serverDataLock.release();
		}
		
		ArrayList<TCChatLine> filteredChatLine = new ArrayList<TCChatLine>(theChat.size());
		List<String> toTabs = new ArrayList<String>();
		toTabs.add("*");

		int _ind;
		int ret = 0;
		boolean skip = !serverSettings.autoChannelSearch.getValue();
		
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
						toTabs.add(chan.getTitle());
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
			filteredChatLine.add(new TCChatLine(theChat.get(0).getUpdatedCounter(), _line, theChat.get(0).getChatLineID(), theChat.get(0).statusMsg));
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
		for (TCChatLine cl : theChat)
			coloredChat = coloredChat + cl.getChatLineString();
		String cleanedChat = StringUtils.stripControlCodes(coloredChat);
		if (generalSettings.saveChatLog.getValue()) TabbyChatUtils.logChat(this.withTimeStamp(cleanedChat));
		
		Matcher findChannelClean = this.chatChannelPatternClean.matcher(cleanedChat);
		Matcher findChannelDirty = this.chatChannelPatternDirty.matcher(coloredChat);
		String cName = null;
		boolean dirtyValid = (!serverSettings.delimColorBool.getValue() && !serverSettings.delimFormatBool.getValue()) ? true : findChannelDirty.find();
		if (findChannelClean.find() && dirtyValid) {
			cName = findChannelClean.group(1);
			ret += this.addToChannel(cName, filteredChatLine);
			toTabs.add(cName);
		} else if(this.chatPMtoMePattern != null && !skip){
			Matcher findPMtoMe = this.chatPMtoMePattern.matcher(cleanedChat);
			if (findPMtoMe.find()) {					
				for(int i=1;i<=findPMtoMe.groupCount();i++) {
					if(findPMtoMe.group(i) != null) {
						cName = findPMtoMe.group(i);
						if(!this.channelMap.containsKey(cName)) {
							ChatChannel newPM = new ChatChannel(cName);
							newPM.cmdPrefix = "/msg "+cName;
							this.channelMap.put(cName, newPM);
						}
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
							if(!this.channelMap.containsKey(cName)) {
								ChatChannel newPM = new ChatChannel(cName);
								newPM.cmdPrefix = "/msg "+cName;
								this.channelMap.put(cName, newPM);
							}
							ret += this.addToChannel(cName, filteredChatLine);
							toTabs.add(cName);
							break;
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
		this.lastChatWriteLock.lock();
		try {
			if (generalSettings.groupSpam.getValue() && activeTabs.size() > 0) {
				if (toTabs.contains(activeTabs.get(0)))
					this.lastChat = this.channelMap.get(activeTabs.get(0)).getChatLogSublistCopy(0, filteredChatLine.size());
				else
					this.lastChat = this.withTimeStamp(filteredChatLine);
			} else
				this.lastChat = this.withTimeStamp(filteredChatLine);
		} finally {
			this.lastChatWriteLock.unlock();
		}
		this.lastChatReadLock.lock();
		try {
			if (ret > 0) {
				if (generalSettings.groupSpam.getValue() && this.channelMap.get(activeTabs.get(0)).hasSpam) {
					gnc.setChatLines(0, new ArrayList<TCChatLine>(this.lastChat));
				} else {
					gnc.addChatLines(0, new ArrayList<TCChatLine>(this.lastChat));
				}
			}
		} finally {
			this.lastChatReadLock.unlock();
		}

		return ret;
	}
	
	private void reloadServerData() {
		serverSettings.loadSettingsFile();
		filterSettings.loadSettingsFile();
		this.loadChannelData();
	}
	
	public void reloadSettingsData(boolean withSave) {
		this.updateDefaults();
		this.loadPatterns();
		this.updateFilters();
		if(withSave) this.storeChannelData();
	}
	
	public void removeTab(String _name) {
		this.channelMap.remove(_name);
	}

	public void resetDisplayedChat() {
 		gnc.clearChatLines();
 		List<String> actives = this.getActive();
 		if (actives.size() < 1) return;
 		gnc.addChatLines(this.channelMap.get(actives.get(0)));
 		int n = actives.size();
 		for (int i = 1; i < n; i++) {
 			gnc.mergeChatLines(this.channelMap.get(actives.get(i)));
 		}
 	}

	private void spamCheck(ChatChannel theChan, List<TCChatLine> lastChat) {
		String oldChat = "";
		String oldChat2 = "";
		String newChat = "";
		if (theChan.getChatLogSize() < lastChat.size()) {
			theChan.hasSpam = false;
			theChan.spamCount = 1;
			return;
		}
		int _size = lastChat.size();
		for (int i=0; i<_size; i++) {
			if(lastChat.get(i).getChatLineString() == null || theChan.getChatLine(i).getChatLineString() == null) continue;
			newChat = newChat + lastChat.get(i).getChatLineString();
			if (generalSettings.timeStampEnable.getValue()) {
				oldChat = theChan.getChatLine(i).getChatLineString().replaceAll("^(\u00A7.)?"+((TimeStampEnum)generalSettings.timeStampStyle.getValue()).regEx+"(\u00A7r)?", "") + oldChat;
			} else {
				oldChat = theChan.getChatLine(i).getChatLineString() + oldChat;
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
				theChan.setChatLogLine(i, this.withTimeStamp(lastChat.get(lastChat.size()-i-1)));
			theChan.setChatLogLine(0, new TCChatLine(lastChat.get(lastChat.size()-1).getUpdatedCounter(), this.withTimeStamp(lastChat.get(lastChat.size()-1).getChatLineString()) + " [" + theChan.spamCount + "x]", lastChat.get(lastChat.size()-1).getChatLineID()));
		} else {
			theChan.hasSpam = false;
			theChan.spamCount = 1;
		}	
	}
	
	public void storeChannelData() {
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
	
	protected void updateDefaults() {
		if (!this.generalSettings.tabbyChatEnable.getValue()) return;
		List<String> dList = new ArrayList(serverSettings.defaultChanList);
		int ind;
		for (ChatChannel chan : this.channelMap.values()) {
			ind = dList.indexOf(chan.getTitle());
			if (ind >= 0) dList.remove(ind);
		}
		
		for (String defChan : dList) {
			if (defChan.length() > 0) this.channelMap.put(defChan, new ChatChannel(defChan));
		}
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
	
	private List<TCChatLine> withTimeStamp(List<TCChatLine> _orig) {
		List<TCChatLine> stamped = new ArrayList();
		for (TCChatLine cl : _orig)
			stamped.add(0, this.withTimeStamp(cl));
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
 	
 	private TCChatLine withTimeStamp(TCChatLine _orig) {
		TCChatLine stamped = _orig;
		if (generalSettings.timeStampEnable.getValue()) {
			this.cal = Calendar.getInstance();
			stamped = new TCChatLine(_orig.getUpdatedCounter(), generalSettings.timeStamp.format(this.cal.getTime())+_orig.getChatLineString(), _orig.getChatLineID());
		}
		return stamped;
	}
}
