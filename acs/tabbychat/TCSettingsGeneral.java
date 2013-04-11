package acs.tabbychat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ServerData;

public class TCSettingsGeneral extends TCSettingsGUI {

	private static final int tabbyChatEnableID = 9101;
	private static final int saveChatLogID = 9102;
	private static final int timeStampEnableID = 9103;
	private static final int timeStampStyleID = 9104;
	private static final int groupSpamID = 9105;
	private static final int unreadFlashingID = 9106;
	
	public TCSettingBool tabbyChatEnable = new TCSettingBool(true, "TabbyChat Enabled", tabbyChatEnableID);
	protected TCSettingBool saveChatLog = new TCSettingBool(false, "Log chat to file", saveChatLogID);
	public TCSettingBool timeStampEnable = new TCSettingBool(false, "Timestamp chat", timeStampEnableID);
	public TCSettingEnum timeStampStyle = new TCSettingEnum(TimeStampEnum.MILITARY, "\u00A7oTimestamp Style\u00A7r", timeStampStyleID);
	protected TCSettingBool groupSpam = new TCSettingBool(true, "Consolidate spammed chat", groupSpamID);
	protected TCSettingBool unreadFlashing = new TCSettingBool(true, "Unread notification flashing", unreadFlashingID);
	
	public TCSettingsGeneral() {
		super();
		this.name = "General Config";
		this.bgcolor = 0x664782be;
	}
	
	protected TCSettingsGeneral(TabbyChat _tc) {
		this();
		tc = _tc;
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
	
		this.tabbyChatEnable.setButtonLoc(col1x, this.rowY(1));
		this.tabbyChatEnable.labelX = col1x + 19;
		this.tabbyChatEnable.buttonOnColor = buttonColor;
		this.buttonList.add(this.tabbyChatEnable);
		
		this.saveChatLog.setButtonLoc(col1x, this.rowY(2));
		this.saveChatLog.labelX = col1x + 19;
		this.saveChatLog.buttonOnColor = buttonColor;
		this.buttonList.add(this.saveChatLog);
		
		this.timeStampEnable.setButtonLoc(col1x,  this.rowY(3));
		this.timeStampEnable.labelX = col1x + 19;
		this.timeStampEnable.buttonOnColor = buttonColor;
		this.buttonList.add(this.timeStampEnable);
		
		this.timeStampStyle.setButtonDims(80, 11);
		this.timeStampStyle.setButtonLoc(effRight - 80, this.rowY(4));
		this.timeStampStyle.labelX = this.timeStampStyle.xPosition - 10 - mc.fontRenderer.getStringWidth(this.timeStampStyle.description);
		this.buttonList.add(this.timeStampStyle);
		
		this.groupSpam.setButtonLoc(col1x, this.rowY(5));
		this.groupSpam.labelX = col1x + 19;
		this.groupSpam.buttonOnColor = buttonColor;
		this.buttonList.add(this.groupSpam);
		
		this.unreadFlashing.setButtonLoc(col1x, this.rowY(6));
		this.unreadFlashing.labelX = col1x + 19;
		this.unreadFlashing.buttonOnColor = buttonColor;
		this.buttonList.add(this.unreadFlashing);
		
		this.validateButtonStates();
	}
	
	public void validateButtonStates() {
		this.timeStampStyle.enabled = this.timeStampEnable.getTempValue();
	}
	
	protected void loadSettingsFile() { 
		this.settingsFile = new File(tabbyChatDir, "general.cfg");
	
		if (!this.settingsFile.exists())
			return;		
		Properties settingsTable = new Properties();
		
		try {
			FileInputStream fInStream = new FileInputStream(this.settingsFile);
			settingsTable.load(fInStream);
			fInStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to read from general settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
		
		try {
			this.tabbyChatEnable.setValue(Boolean.parseBoolean((String)settingsTable.get("tabbyChatEnable")));
			this.saveChatLog.setValue(Boolean.parseBoolean((String)settingsTable.get("saveChatLog")));
			this.timeStampEnable.setValue(Boolean.parseBoolean((String)settingsTable.get("timeStampEnable")));
			this.timeStampStyle.setValue(TimeStampEnum.valueOf((String)settingsTable.get("timeStampStyle")));
			this.groupSpam.setValue(Boolean.parseBoolean((String)settingsTable.get("groupSpam")));
			this.unreadFlashing.setValue(Boolean.parseBoolean((String)settingsTable.get("unreadFlashing")));
		} catch (Exception e) {
			TabbyChat.printErr("Invalid property found in general settings file.");
			this.tabbyChatEnable.setValue(true);
			this.saveChatLog.setValue(false);
			this.timeStampEnable.setValue(false);
			this.timeStampStyle.setValue(TimeStampEnum.MILITARY);
			this.groupSpam.setValue(true);
			this.unreadFlashing.setValue(true);
		}
		this.resetTempVars();
	}
	
	protected void saveSettingsFile() { 
		if (!tabbyChatDir.exists())
			tabbyChatDir.mkdirs();

		Properties settingsTable = new Properties();
		settingsTable.put("tabbyChatEnable", this.tabbyChatEnable.getValue().toString());
		settingsTable.put("saveChatLog", this.saveChatLog.getValue().toString());
		settingsTable.put("timeStampEnable", this.timeStampEnable.getValue().toString());
		settingsTable.put("timeStampStyle", this.timeStampStyle.getValue().name());
		settingsTable.put("groupSpam", this.groupSpam.getValue().toString());
		settingsTable.put("unreadFlashing", this.unreadFlashing.getValue().toString());
		
		try {
			FileOutputStream fOutStream = new FileOutputStream(this.settingsFile);
			settingsTable.store(fOutStream, "General settings");
			fOutStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to write to general settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}
	
	protected void storeTempVars() {
		this.tabbyChatEnable.save();
		this.saveChatLog.save();
		this.timeStampEnable.save();
		this.timeStampStyle.save();
		this.groupSpam.save();
		this.unreadFlashing.save();
	}
	
	protected void resetTempVars() {
		this.tabbyChatEnable.reset();
		this.saveChatLog.reset();
		this.timeStampEnable.reset();
		this.timeStampStyle.reset();
		this.groupSpam.reset();
		this.unreadFlashing.reset();
	}
}
