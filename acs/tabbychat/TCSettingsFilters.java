package acs.tabbychat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.ServerData;

public class TCSettingsFilters extends TCSettingsGUI {
	protected int curFilterId = 0;
	protected int numTempFilters = 0;
	protected int numFilters = 0;
	private static String lastMatch = "";

	private static final int inverseMatchID = 9301;
	private static final int caseSenseID = 9302;
	private static final int highlightBoolID = 9303;
	private static final int highlightColorID = 9304;
	private static final int highlightFormatID = 9305;
	private static final int audioNotificationBoolID = 9306;
	private static final int audioNotificationEnumID = 9307;
	private static final int prevButtonID = 9308;
	private static final int nextButtonID = 9309;
	private static final int filterNameID = 9310;
	private static final int sendToTabBoolID = 9311;
	private static final int sendToTabNameID = 9312;
	private static final int sendToAllTabsID = 9313;
	private static final int removeMatchesID = 9314;
	private static final int expressionID = 9315;
	private static final int addNewID = 9316;
	private static final int delID = 9317;
	
	protected TCSettingBool inverseMatch = new TCSettingBool(false, "Inverse match", inverseMatchID);
	protected TCSettingBool caseSensitive = new TCSettingBool(false, "Case Sensitive", caseSenseID);
	protected TCSettingBool highlightBool = new TCSettingBool(true, "Highlight matches", highlightBoolID);
	protected TCSettingEnum highlightColor = new TCSettingEnum(ColorCodeEnum.YELLOW, "\u00A7oColor\u00A7r", highlightColorID);
	protected TCSettingEnum highlightFormat = new TCSettingEnum(FormatCodeEnum.BOLD, "\u00A7oFormat\u00A7r", highlightFormatID);
	protected TCSettingBool audioNotificationBool = new TCSettingBool(false, "Audio notification", audioNotificationBoolID);
	protected TCSettingEnum audioNotificationSound = new TCSettingEnum(NotificationSoundEnum.ORB, "\u00A7oSound\u00A7r", audioNotificationEnumID);
	protected TCSettingTextBox filterName = new TCSettingTextBox("Filter Name", filterNameID);
	protected TCSettingBool sendToTabBool = new TCSettingBool(false, "Send matches to tab", sendToTabBoolID);
	protected TCSettingTextBox sendToTabName = new TCSettingTextBox("Tab Name", sendToTabNameID);
	protected TCSettingBool sendToAllTabs = new TCSettingBool(false, "All tabs", sendToAllTabsID);
	protected TCSettingBool removeMatches = new TCSettingBool(false, "Hide matches from chat", removeMatchesID);
	protected TCSettingTextBox expressionString = new TCSettingTextBox("Expression", expressionID);
	
	public HashMap filterMap = new HashMap();
	protected HashMap tempFilterMap = new HashMap();
	
	public TCSettingsFilters() {
		super();
		this.name = "Custom Filters";
		this.bgcolor = 0x66289f28;
	}
	
	protected TCSettingsFilters(TabbyChat _tc) {
		this();
		tc = _tc;
	}
	
	protected boolean removeMatches(int ind) {
		return (Boolean)this.filterMap.get(Integer.toString(ind) + ".removeMatches");
	}
	
	protected boolean audioNotificationBool(int ind) {
		return (Boolean)this.filterMap.get(Integer.toString(ind) + ".audioNotificationBool");
	}
	
	protected boolean sendToAllTabs(int ind) {
		return (Boolean)this.filterMap.get(Integer.toString(ind) + ".sendToAllTabs");
	}
	
	protected boolean sendToTabBool(int ind) {
		return (Boolean)this.filterMap.get(Integer.toString(ind) + ".sendToTabBool");
	}
	
	protected String sendToTabName(int ind) {
		return (String)this.filterMap.get(Integer.toString(ind) + ".sendToTabName");
	}
	
	protected void audioNotification(int ind) {
		NotificationSoundEnum ding = (NotificationSoundEnum)this.filterMap.get(Integer.toString(ind) + ".audioNotificationSound");
		mc.sndManager.playSoundFX(ding.file(), 1.0F, 1.0F);
	}
	
	private int addNewFilter() {
		int nextId = this.numTempFilters;
		this.numTempFilters++;
		String sId = Integer.toString(nextId);		
		this.tempFilterMap.put(sId + ".filterName", "New"+sId);
		this.tempFilterMap.put(sId + ".inverseMatch", false);
		this.tempFilterMap.put(sId + ".caseSensitive", false);
		this.tempFilterMap.put(sId + ".highlightBool", true);
		this.tempFilterMap.put(sId + ".highlightColor", ColorCodeEnum.YELLOW);
		this.tempFilterMap.put(sId + ".highlightFormat", FormatCodeEnum.BOLD);
		this.tempFilterMap.put(sId + ".audioNotificationBool", false);
		this.tempFilterMap.put(sId + ".audioNotificationSound", NotificationSoundEnum.ORB);
		this.tempFilterMap.put(sId + ".sendToTabBool", false);
		this.tempFilterMap.put(sId + ".sendToTabName", "");
		this.tempFilterMap.put(sId + ".sendToAllTabs", false);
		this.tempFilterMap.put(sId + ".removeMatches", false);
		this.tempFilterMap.put(sId + ".expressionString", "");
		return nextId;
	}
	
	private int deleteFilter() {
		String here;
		String next;
		
		if (this.curFilterId < 0 || this.curFilterId > this.numTempFilters)
			return 0;
		for (int i = this.curFilterId; i < this.numTempFilters - 1; i++) {
			here = Integer.toString(i);
			next = Integer.toString(i+1);
			this.tempFilterMap.put(here + ".filterName", this.tempFilterMap.get(next + ".filterName"));
			this.tempFilterMap.put(here + ".inverseMatch", this.tempFilterMap.get(next + ".inverseMatch"));
			this.tempFilterMap.put(here + ".caseSensitive", this.tempFilterMap.get(next + ".caseSensitive"));
			this.tempFilterMap.put(here + ".highlightBool", this.tempFilterMap.get(next + ".highlightBool"));
			this.tempFilterMap.put(here + ".highlightColor", this.tempFilterMap.get(next + ".highlightColor"));
			this.tempFilterMap.put(here + ".highlightFormat", this.tempFilterMap.get(next + ".highlightFormat"));
			this.tempFilterMap.put(here + ".audioNotificationBool", this.tempFilterMap.get(next + ".audioNotificationBool"));
			this.tempFilterMap.put(here + ".audioNotificationSound", this.tempFilterMap.get(next + ".audioNotificationSound"));
			this.tempFilterMap.put(here + ".sendToTabBool", this.tempFilterMap.get(next + ".sendToTabBool"));
			this.tempFilterMap.put(here + ".sendToTabName", this.tempFilterMap.get(next + ".sendToTabName"));
			this.tempFilterMap.put(here + ".sendToAllTabs", this.tempFilterMap.get(next + ".sendToAllTabs"));
			this.tempFilterMap.put(here + ".removeMatches", this.tempFilterMap.get(next + ".removeMatches"));
			this.tempFilterMap.put(here + ".expressionString", this.tempFilterMap.get(next + ".expressionString"));
		}
		here = Integer.toString(this.numTempFilters - 1);
		this.tempFilterMap.remove(here + ".filterName");
		this.tempFilterMap.remove(here + ".inverseMatch");
		this.tempFilterMap.remove(here + ".caseSensitive");
		this.tempFilterMap.remove(here + ".highlightBool");
		this.tempFilterMap.remove(here + ".highlightColor");
		this.tempFilterMap.remove(here + ".highlightFormat");
		this.tempFilterMap.remove(here + ".audioNotificationBool");
		this.tempFilterMap.remove(here + ".audioNotificationSound");
		this.tempFilterMap.remove(here + ".sendToTabBool");
		this.tempFilterMap.remove(here + ".sendToTabName");
		this.tempFilterMap.remove(here + ".sendToAllTabs");
		this.tempFilterMap.remove(here + ".removeMatches");
		this.tempFilterMap.remove(here + ".expressionString");
		this.numTempFilters--;
		return (this.curFilterId > 0) ? this.curFilterId - 1 : 0;
	}
	
	private void displayFilter(int fId) {
		if (this.numTempFilters == 0 || !this.tempFilterMap.containsKey(Integer.toString(fId)+".filterName")) {
			this.filterName.setTempValue("");
			this.inverseMatch.setTempValue(false);
			this.caseSensitive.setTempValue(false);
			this.highlightBool.setTempValue(true);
			this.highlightColor.setTempValue(ColorCodeEnum.YELLOW);
			this.highlightFormat.setTempValue(FormatCodeEnum.BOLD);
			this.audioNotificationBool.setTempValue(false);
			this.audioNotificationSound.setTempValue(NotificationSoundEnum.ORB);
			this.sendToTabBool.setTempValue(false);
			this.sendToAllTabs.setTempValue(false);
			this.sendToTabName.setTempValue("");
			this.removeMatches.setTempValue(false);
			this.expressionString.setTempValue("");
		} else {
			String sId = Integer.toString(fId);
			this.filterName.setTempValue((String)this.tempFilterMap.get(sId + ".filterName"));
			this.inverseMatch.setTempValue((Boolean)this.tempFilterMap.get(sId + ".inverseMatch"));
			this.caseSensitive.setTempValue((Boolean)this.tempFilterMap.get(sId + ".caseSensitive"));
			this.highlightBool.setTempValue((Boolean)this.tempFilterMap.get(sId + ".highlightBool"));
			this.highlightColor.setTempValue((ColorCodeEnum)this.tempFilterMap.get(sId + ".highlightColor"));
			this.highlightFormat.setTempValue((FormatCodeEnum)this.tempFilterMap.get(sId + ".highlightFormat"));
			this.audioNotificationBool.setTempValue((Boolean)this.tempFilterMap.get(sId + ".audioNotificationBool"));
			this.audioNotificationSound.setTempValue((NotificationSoundEnum)this.tempFilterMap.get(sId + ".audioNotificationSound"));
			this.sendToTabBool.setTempValue((Boolean)this.tempFilterMap.get(sId + ".sendToTabBool"));
			this.sendToTabName.setTempValue((String)this.tempFilterMap.get(sId + ".sendToTabName"));
			this.sendToAllTabs.setTempValue((Boolean)this.tempFilterMap.get(sId + ".sendToAllTabs"));
			this.removeMatches.setTempValue((Boolean)this.tempFilterMap.get(sId + ".removeMatches"));
			this.expressionString.setTempValue((String)this.tempFilterMap.get(sId + ".expressionString"));
			this.curFilterId = fId;
		}
	}
	
	private void storeTempFilter(int fId) {
		if (this.numTempFilters == 0)
			return;
		
		String sId = Integer.toString(fId);
		this.tempFilterMap.put(sId + ".filterName", this.filterName.getTempValue());
		this.tempFilterMap.put(sId + ".inverseMatch", this.inverseMatch.getTempValue());
		this.tempFilterMap.put(sId + ".caseSensitive", this.caseSensitive.getTempValue());
		this.tempFilterMap.put(sId + ".highlightBool", this.highlightBool.getTempValue());
		this.tempFilterMap.put(sId + ".highlightColor", this.highlightColor.getTempValue());
		this.tempFilterMap.put(sId + ".highlightFormat", this.highlightFormat.getTempValue());
		this.tempFilterMap.put(sId + ".audioNotificationBool", this.audioNotificationBool.getTempValue());
		this.tempFilterMap.put(sId + ".audioNotificationSound", this.audioNotificationSound.getTempValue());
		this.tempFilterMap.put(sId + ".sendToTabBool", this.sendToTabBool.getTempValue());
		this.tempFilterMap.put(sId + ".sendToTabName", this.sendToTabName.getTempValue());
		this.tempFilterMap.put(sId + ".sendToAllTabs", this.sendToAllTabs.getTempValue());
		this.tempFilterMap.put(sId + ".removeMatches", this.removeMatches.getTempValue());
		this.tempFilterMap.put(sId + ".expressionString", this.expressionString.getTempValue());
	}
	
	public void actionPerformed(GuiButton button) {
		this.storeTempFilter(this.curFilterId);
		super.actionPerformed(button);
		switch (button.id) {
		case addNewID:
			this.curFilterId = this.addNewFilter();
			this.displayFilter(this.curFilterId);
			break;
		case delID:
			if (this.numTempFilters > 0) {
				this.curFilterId = this.deleteFilter();
				this.displayFilter(this.curFilterId);
			}
			break;
		case prevButtonID:
			if (this.numTempFilters > 1)
				this.displayFilter((this.curFilterId > 0) ? this.curFilterId - 1 : this.numTempFilters - 1);
			break;
		case nextButtonID:
			if (this.numTempFilters > 1)
				this.displayFilter((this.curFilterId < this.numTempFilters-1) ? this.curFilterId + 1 : 0);
			break;		
		}
			
		this.validateButtonStates();
	}
	
	public void validateButtonStates() {
		this.inverseMatch.enabled = !this.highlightBool.getTempValue();
		this.caseSensitive.enabled = true;
		
		this.highlightBool.enabled = !this.removeMatches.getTempValue() && !this.inverseMatch.getTempValue();
		this.audioNotificationBool.enabled = !this.removeMatches.getTempValue();
		this.removeMatches.enabled = !this.sendToTabBool.getTempValue() && !this.highlightBool.getTempValue() && !this.audioNotificationBool.getTempValue();
		this.sendToTabBool.enabled = !this.removeMatches.getTempValue();
		
		this.highlightColor.enabled = this.highlightBool.getTempValue();
		this.highlightFormat.enabled = this.highlightBool.getTempValue();
		this.audioNotificationSound.enabled = this.audioNotificationBool.getTempValue();
		this.sendToAllTabs.enabled = this.sendToTabBool.getTempValue();
		

		
		for (int i = 0; i < this.buttonList.size(); i++) {
			if (TCSetting.class.isInstance(this.buttonList.get(i))) {
				TCSetting tmp = (TCSetting)this.buttonList.get(i);
				if (this.numTempFilters == 0)
					tmp.disable();
				else if (tmp.type == "textbox")
					tmp.enable();
				else if (tmp.type == "bool")
					((TCSettingBool)tmp).setTempValue(((TCSettingBool)tmp).getTempValue() && tmp.enabled);
			}
		}
		this.sendToTabName.enabled(this.sendToTabBool.getTempValue() && !this.sendToAllTabs.getTempValue());
	}
	
	public void mouseClicked(int par1, int par2, int par3) {
		for (int i = 0; i < this.buttonList.size(); i++) {
			if (TCSetting.class.isInstance(this.buttonList.get(i))) {
				TCSetting tmp = (TCSetting)this.buttonList.get(i);
				if (tmp.type == "textbox" || (tmp.type == "enum" && tmp.id != audioNotificationEnumID)) {
					tmp.mouseClicked(par1, par2, par3);
				}
			}
		}
		if (this.audioNotificationSound.hovered(par1, par2)) {
			this.audioNotificationSound.mouseClicked(par1, par2, par3);
			mc.sndManager.playSoundFX(((NotificationSoundEnum)audioNotificationSound.tempValue).file(), 1.0F, 1.0F);
		} else
			super.mouseClicked(par1, par2, par3);
	}
	
	protected void resetTempVars() {
		this.tempFilterMap.clear();
		String sId;
		this.curFilterId = 0;
		this.numTempFilters = 0;
		for (int i = 0; i < this.filterMap.size(); i++) {
			sId = Integer.toString(i);
			if (!this.filterMap.containsKey(sId + ".filterName"))
				return;
			this.tempFilterMap.put(sId + ".filterName", this.filterMap.get(sId + ".filterName"));
			this.tempFilterMap.put(sId + ".inverseMatch", this.filterMap.get(sId + ".inverseMatch"));
			this.tempFilterMap.put(sId + ".caseSensitive", this.filterMap.get(sId + ".caseSensitive"));
			this.tempFilterMap.put(sId + ".highlightBool", this.filterMap.get(sId + ".highlightBool"));
			this.tempFilterMap.put(sId + ".highlightColor", this.filterMap.get(sId + ".highlightColor"));
			this.tempFilterMap.put(sId + ".highlightFormat", this.filterMap.get(sId + ".highlightFormat"));
			this.tempFilterMap.put(sId + ".audioNotificationBool", this.filterMap.get(sId + ".audioNotificationBool"));
			this.tempFilterMap.put(sId + ".audioNotificationSound", this.filterMap.get(sId + ".audioNotificationSound"));
			this.tempFilterMap.put(sId + ".sendToTabBool", this.filterMap.get(sId + ".sendToTabBool"));
			this.tempFilterMap.put(sId + ".sendToTabName", this.filterMap.get(sId + ".sendToTabName"));
			this.tempFilterMap.put(sId + ".sendToAllTabs", this.filterMap.get(sId + ".sendToAllTabs"));
			this.tempFilterMap.put(sId + ".removeMatches", this.filterMap.get(sId + ".removeMatches"));
			this.tempFilterMap.put(sId + ".expressionString", this.filterMap.get(sId + ".expressionString"));
			this.numTempFilters++;
		}
	}
	
	protected void storeTempVars() {
		this.filterMap.clear();
		String sId;
		this.numFilters = 0;
		for (int i = 0; i < this.tempFilterMap.size(); i++) {
			sId = Integer.toString(i);
			if (!this.tempFilterMap.containsKey(sId + ".filterName"))
				return;
			this.filterMap.put(sId + ".filterName", this.tempFilterMap.get(sId + ".filterName"));
			this.filterMap.put(sId + ".inverseMatch", this.tempFilterMap.get(sId + ".inverseMatch"));
			this.filterMap.put(sId + ".caseSensitive", this.tempFilterMap.get(sId + ".caseSensitive"));
			this.filterMap.put(sId + ".highlightBool", this.tempFilterMap.get(sId + ".highlightBool"));
			this.filterMap.put(sId + ".highlightColor", this.tempFilterMap.get(sId + ".highlightColor"));
			this.filterMap.put(sId + ".highlightFormat", this.tempFilterMap.get(sId + ".highlightFormat"));
			this.filterMap.put(sId + ".audioNotificationBool", this.tempFilterMap.get(sId + ".audioNotificationBool"));
			this.filterMap.put(sId + ".audioNotificationSound", this.tempFilterMap.get(sId + ".audioNotificationSound"));
			this.filterMap.put(sId + ".sendToTabBool", this.tempFilterMap.get(sId + ".sendToTabBool"));
			this.filterMap.put(sId + ".sendToTabName", this.tempFilterMap.get(sId + ".sendToTabName"));
			this.filterMap.put(sId + ".sendToAllTabs", this.tempFilterMap.get(sId + ".sendToAllTabs"));
			this.filterMap.put(sId + ".removeMatches", this.tempFilterMap.get(sId + ".removeMatches"));
			try {
				this.filterMap.put(sId + ".expressionString", this.tempFilterMap.get(sId + ".expressionString"));
				if ((Boolean)this.tempFilterMap.get(sId + ".caseSensitive"))
					this.filterMap.put(sId + ".expressionPattern", Pattern.compile((String)this.tempFilterMap.get(sId + ".expressionString")));
				else
					this.filterMap.put(sId + ".expressionPattern", Pattern.compile((String)this.tempFilterMap.get(sId + ".expressionString"), Pattern.CASE_INSENSITIVE));
			} catch (PatternSyntaxException e) {
				this.filterMap.put(sId + ".expressionString", ".*");
				this.filterMap.put(sId + ".expressionPattern", Pattern.compile(".*"));
			}
			this.numFilters++;
		}
	}
	
	protected void importSettings() {
		String sId;
		this.numFilters = 0;
		for (CustomChatFilter oldfilter : tc.serverPrefs.customFilters) {
			sId = Integer.toString(this.numFilters);
			this.filterMap.put(sId + ".filterName", oldfilter.name);
			this.filterMap.put(sId + ".inverseMatch", oldfilter.invert);
			this.filterMap.put(sId + ".caseSensitive", oldfilter.caseSensitive);
			this.filterMap.put(sId + ".highlightBool", oldfilter.highlight);
			this.filterMap.put(sId + ".highlightColor", TabbyChatUtils.parseColor(oldfilter.highlightColor.name()));
			this.filterMap.put(sId + ".highlightFormat", TabbyChatUtils.parseFormat(oldfilter.highlightFormat.name()));
			this.filterMap.put(sId + ".audioNotificationBool", oldfilter.ding);
			this.filterMap.put(sId + ".audioNotificationSound", NotificationSoundEnum.ORB);
			this.filterMap.put(sId + ".sendToTabBool", oldfilter.sendToTab);
			if (oldfilter.sendToTab)
				this.filterMap.put(sId + ".sendToTabName", oldfilter.name);
			else
				this.filterMap.put(sId + ".sendToTabName", "");
			this.filterMap.put(sId + ".sendToAllTabs", false);
			this.filterMap.put(sId + ".removeMatches", false);
			this.filterMap.put(sId + ".expressionString", oldfilter.filter.toString()); 
			this.filterMap.put(sId + ".expressionPattern", oldfilter.filter);
			this.numFilters++;
		}
		this.resetTempVars();
	}
	
	protected boolean loadSettingsFile() {
		boolean loaded = false;
		this.filterMap.clear();
		this.numFilters = 0;
		ServerData server = Minecraft.getMinecraft().getServerData();
		if (server == null)
			return loaded;
		String sname = server.serverName;
		String ip = server.serverIP;
	
		if (ip.contains(":")) {
			ip = ip.replaceAll(":", "(") + ")";
		}
		
		File settingsDir = new File(tabbyChatDir, ip);
		this.settingsFile = new File(settingsDir, "filters.cfg");
	
		if (!this.settingsFile.exists())
			return loaded;		
		Properties settingsTable = new Properties();
		
		try {
			FileInputStream fInStream = new FileInputStream(this.settingsFile);
			settingsTable.load(fInStream);
			fInStream.close();
			loaded = true;
		} catch (Exception e) {
			TabbyChat.printErr("Unable to read from filter settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
			loaded = false;
		}
		
		String sId;
		int _ind = settingsTable.size();
		for (int i = 0; i < _ind; i++) {
			sId = Integer.toString(i);
			if (!loaded || !settingsTable.containsKey(sId+".filterName"))
				break;

			this.filterMap.put(sId + ".filterName", settingsTable.getProperty(sId + ".filterName"));
			this.filterMap.put(sId + ".inverseMatch", Boolean.parseBoolean(settingsTable.getProperty(sId + ".inverseMatch")));
			this.filterMap.put(sId + ".caseSensitive", Boolean.parseBoolean(settingsTable.getProperty(sId + ".caseSensitive")));
			this.filterMap.put(sId + ".highlightBool", Boolean.parseBoolean(settingsTable.getProperty(sId + ".highlightBool")));
			this.filterMap.put(sId + ".highlightColor", ColorCodeEnum.valueOf(settingsTable.getProperty(sId + ".highlightColor")));
			this.filterMap.put(sId + ".highlightFormat", FormatCodeEnum.valueOf(settingsTable.getProperty(sId + ".highlightFormat")));
			this.filterMap.put(sId + ".audioNotificationBool", Boolean.parseBoolean(settingsTable.getProperty(sId + ".audioNotificationBool")));
			this.filterMap.put(sId + ".audioNotificationSound", TabbyChatUtils.parseSound(settingsTable.getProperty(sId + ".audioNotificationSound")));
			this.filterMap.put(sId + ".sendToTabBool", Boolean.parseBoolean(settingsTable.getProperty(sId + ".sendToTabBool")));
			this.filterMap.put(sId + ".sendToTabName", settingsTable.getProperty(sId + ".sendToTabName"));
			this.filterMap.put(sId + ".sendToAllTabs", Boolean.parseBoolean(settingsTable.getProperty(sId + ".sendToAllTabs")));
			this.filterMap.put(sId + ".removeMatches", Boolean.parseBoolean(settingsTable.getProperty(sId + ".removeMatches")));
			try {
				this.filterMap.put(sId + ".expressionString", settingsTable.getProperty(sId + ".expressionString"));
				if (Boolean.parseBoolean(settingsTable.getProperty(sId + ".caseSensitive")))
					this.filterMap.put(sId + ".expressionPattern", Pattern.compile(settingsTable.getProperty(sId + ".expressionString")));
				else
					this.filterMap.put(sId + ".expressionPattern", Pattern.compile(settingsTable.getProperty(sId + ".expressionString"), Pattern.CASE_INSENSITIVE));
			} catch (PatternSyntaxException e) {
				this.filterMap.put(sId + ".expressionString", ".*");
				this.filterMap.put(sId + ".expressionPattern", Pattern.compile(".*"));
			}
			this.numFilters++;
		}		
		this.resetTempVars();
		return loaded;
	}
	
	protected void saveSettingsFile() { 
		ServerData server = Minecraft.getMinecraft().getServerData();
		String sname = server.serverName;
		String ip = server.serverIP;
	
		if (ip.contains(":")) {
			ip = ip.replaceAll(":", "(") + ")";
		}
		
		File settingsDir = new File(tabbyChatDir, ip);
	
		if (!settingsDir.exists())
			settingsDir.mkdirs();
		settingsFile = new File(settingsDir, "filters.cfg");
		Properties settingsTable = new Properties();
		Set<String> filterKeys = this.filterMap.keySet();
		Object stg;
		
		for(String key : filterKeys) {
			stg = this.filterMap.get(key);
			if(Enum.class.isInstance(stg))
				settingsTable.put(key, ((Enum)stg).name());
			else
				settingsTable.put(key, stg.toString());
		}

		try {
			FileOutputStream fOutStream = new FileOutputStream(settingsFile);
			settingsTable.store(fOutStream, "Custom filters");
			fOutStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to write to filter settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}
	
	public void initGui() {
		super.initGui();
				
		
		int effLeft = (this.width - this.displayWidth)/2;
		int absLeft = effLeft - this.margin;
		int effTop = (this.height - this.displayHeight)/2;
		int absTop = effTop - this.margin;
		int effRight = (this.width + this.displayWidth)/2;
		int col1x = (this.width - this.displayWidth)/2 + 100;
		int col2x = (this.width + this.displayWidth)/2 - 65;
		
		int buttonColor = (this.bgcolor & 0x00ffffff) + 0xff000000;
		
		
		PrefsButton newButton = new PrefsButton(addNewID, col1x, (this.height + this.displayHeight)/2 - this.line_height, 45 ,this.line_height, "New");
		PrefsButton delButton = new PrefsButton(delID, col1x + 50, (this.height + this.displayHeight)/2 - this.line_height, 45, this.line_height, "Delete");
		newButton.bgcolor = this.bgcolor;
		delButton.bgcolor = this.bgcolor;
		this.buttonList.add(newButton);
		this.buttonList.add(delButton);
		
		this.filterName.setButtonDims(100, 11);
		this.filterName.labelX = col1x;
		this.filterName.setButtonLoc(col1x + 33 + mc.fontRenderer.getStringWidth(this.filterName.description), this.rowY(1));
		this.filterName.textBox.setMaxStringLength(50);
		this.buttonList.add(this.filterName);
		PrefsButton prevButton = new PrefsButton(prevButtonID, this.filterName.xPosition - 23, this.rowY(1), 20, this.line_height, "<<");
		PrefsButton nextButton = new PrefsButton(nextButtonID, this.filterName.xPosition + 103, this.rowY(1), 20, this.line_height, ">>");
		this.buttonList.add(prevButton);
		this.buttonList.add(nextButton);
		
		this.sendToTabBool.setButtonLoc(col1x, this.rowY(2));
		this.sendToTabBool.labelX = col1x + 19;
		this.sendToTabBool.buttonOnColor = buttonColor;
		this.buttonList.add(this.sendToTabBool);
		
		this.sendToAllTabs.setButtonLoc(col1x + 20, this.rowY(3));
		this.sendToAllTabs.labelX = col1x + 39;
		this.sendToAllTabs.buttonOnColor = buttonColor;
		this.buttonList.add(this.sendToAllTabs);
		
		this.sendToTabName.labelX = effRight - mc.fontRenderer.getStringWidth(this.sendToTabName.description) - 55;
		this.sendToTabName.setButtonLoc(effRight - 50 , this.rowY(3));
		this.sendToTabName.setButtonDims(50, 11);
		this.sendToTabName.textBox.setMaxStringLength(20);
		this.buttonList.add(this.sendToTabName);
		
		this.removeMatches.setButtonLoc(col1x,  this.rowY(4));
		this.removeMatches.labelX = col1x + 19;
		this.removeMatches.buttonOnColor = buttonColor;
		this.buttonList.add(this.removeMatches);
		
		this.highlightBool.setButtonLoc(col1x,  this.rowY(5));
		this.highlightBool.labelX = col1x + 19;
		this.highlightBool.buttonOnColor = buttonColor;
		this.buttonList.add(this.highlightBool);
		
		this.highlightColor.setButtonDims(70, 11);
		this.highlightColor.setButtonLoc(col1x + 15 + mc.fontRenderer.getStringWidth(this.highlightColor.description), this.rowY(6));
		this.highlightColor.labelX = col1x + 10;
		this.buttonList.add(this.highlightColor);
		
		this.highlightFormat.setButtonDims(60, 11);;
		this.highlightFormat.setButtonLoc(effRight - 60,  this.rowY(6));
		this.highlightFormat.labelX = this.highlightFormat.xPosition - 5 - mc.fontRenderer.getStringWidth(this.highlightFormat.description);
		this.buttonList.add(this.highlightFormat);
		
		this.audioNotificationBool.setButtonLoc(col1x, this.rowY(7));
		this.audioNotificationBool.labelX = col1x + 19;
		this.audioNotificationBool.buttonOnColor = buttonColor;
		this.buttonList.add(this.audioNotificationBool);
		
		this.audioNotificationSound.setButtonDims(60, 11);
		this.audioNotificationSound.setButtonLoc(effRight - 60, this.rowY(7));
		this.audioNotificationSound.labelX = this.audioNotificationSound.xPosition - 5 - mc.fontRenderer.getStringWidth(this.audioNotificationSound.description);
		this.buttonList.add(this.audioNotificationSound);	
		
		this.inverseMatch.setButtonLoc(col1x, this.rowY(8));
		this.inverseMatch.labelX = col1x + 19;
		this.inverseMatch.buttonOnColor = buttonColor;
		this.buttonList.add(this.inverseMatch);
		
		this.caseSensitive.labelX = effRight - mc.fontRenderer.getStringWidth(this.caseSensitive.description);
		this.caseSensitive.setButtonLoc(this.caseSensitive.labelX - 19,  this.rowY(8));
		this.caseSensitive.buttonOnColor = buttonColor;
		this.buttonList.add(this.caseSensitive);
		
		this.expressionString.labelX = col1x;
		this.expressionString.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.expressionString.description), this.rowY(9));
		this.expressionString.setButtonDims(effRight - this.expressionString.xPosition, 11);
		this.expressionString.textBox.setMaxStringLength(600);
		this.buttonList.add(this.expressionString);
		
		this.displayFilter(0);
		this.validateButtonStates();
	}
	
	protected boolean applyFilterToDirtyChat(int filterNum, String input) {
		if (filterNum >= this.numFilters)
			return false;
		
// Pull data for the requested filter number
		String fNum = Integer.toString(filterNum);
		Boolean caseSensitive = (Boolean)this.filterMap.get(fNum + ".caseSensitive");
		Boolean inverseMatch = (Boolean)this.filterMap.get(fNum + ".inverseMatch");
		Boolean highlightBool = (Boolean)this.filterMap.get(fNum + ".highlightBool");
		ColorCodeEnum highlightColor = (ColorCodeEnum)this.filterMap.get(fNum + ".highlightColor");
		FormatCodeEnum highlightFormat = (FormatCodeEnum)this.filterMap.get(fNum + ".highlightFormat");
		String expressionString = new String((String)this.filterMap.get(fNum + ".expressionString"));
		if (expressionString.equals(""))
			return false;
		Pattern filter = (Pattern)this.filterMap.get(fNum + ".expressionPattern");

		Pattern pullCodes = Pattern.compile("(?i)(\\u00A7[0-9A-FK-OR])+");
		int _start = 0;
		int _end = 0;
		
		TreeMap<Integer, String>chatCodes = new TreeMap<Integer, String>();
		HashMap<Integer, String>hlCodes = new HashMap<Integer, String>();
		
// Remove color/formatting codes from input, store codes and locations for later re-insertion		
		StringBuilder result = new StringBuilder(input);
		Matcher matchCodes = pullCodes.matcher(result.toString());
		while(matchCodes.find()) {
			_start = matchCodes.start();
			_end = matchCodes.end();
			chatCodes.put(_start, result.substring(_start, _end));
			result.replace(_start, _end, "");		
			matchCodes = pullCodes.matcher(result.toString());
		}

// Apply this filter expression to the clean input
		Matcher matchFilter = filter.matcher(result.toString());
		boolean matched = false;
		String prefix = highlightColor.toCode()+ highlightFormat.toCode();
		String suffix = "\u00A7r";

		while(matchFilter.find()) {
			matched = true;
			if (highlightBool) {
				_start = matchFilter.start();
				_end = matchFilter.end();
				Entry<Integer, String> newSuffix = chatCodes.lowerEntry(_end);
				hlCodes.put(_start, prefix);
				if (newSuffix == null)
					hlCodes.put(_end, suffix);
				else
					hlCodes.put(_end, (String)newSuffix.getValue());
			} else {
				break;
			}
		}
		
// If highlighting, re-insert color/format codes to return highlighted result.  Otherwise, just return the original input.		
		if (highlightBool) {
			chatCodes.putAll(hlCodes);
			Entry<Integer, String> ptr = chatCodes.pollLastEntry();
			while (ptr != null) {
				result.insert(ptr.getKey(), ptr.getValue());
				ptr = chatCodes.pollLastEntry();
			}
			lastMatch = result.toString();
		} else
			lastMatch = input;
		if (!matched && inverseMatch)
			return true;
		else if (matched && !inverseMatch)
			return true;
		else if (matched && inverseMatch)
			return false;
		else if (!matched && !inverseMatch)
			return false;
		return false;
	}

	protected String getLastMatchPretty() {
		String tmp = new String(lastMatch);
		lastMatch = "";
		return tmp;
	}

}
