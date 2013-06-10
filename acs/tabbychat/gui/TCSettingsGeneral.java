package acs.tabbychat.gui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.ColorCodeEnum;
import acs.tabbychat.settings.TCSettingBool;
import acs.tabbychat.settings.TCSettingEnum;
import acs.tabbychat.settings.TimeStampEnum;
import acs.tabbychat.util.TabbyChatUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.ServerData;

public class TCSettingsGeneral extends TCSettingsGUI {

	public SimpleDateFormat timeStamp = new SimpleDateFormat();
	
	private static final int tabbyChatEnableID = 9101;
	private static final int saveChatLogID = 9102;
	private static final int timeStampEnableID = 9103;
	private static final int timeStampStyleID = 9104;
	private static final int groupSpamID = 9105;
	private static final int unreadFlashingID = 9106;
	private static final int timeStampColorID = 9107;
	
	public TCSettingBool tabbyChatEnable = new TCSettingBool(true, TabbyChat.translator.getString("settings.general.tabbychatenable"), tabbyChatEnableID);
	public TCSettingBool saveChatLog = new TCSettingBool(false, TabbyChat.translator.getString("settings.general.savechatlog"), saveChatLogID);
	public TCSettingBool timeStampEnable = new TCSettingBool(false, TabbyChat.translator.getString("settings.general.timestampenable"), timeStampEnableID);
	public TCSettingEnum timeStampStyle = new TCSettingEnum(TimeStampEnum.MILITARY, "\u00A7o"+TabbyChat.translator.getString("settings.general.timestampstyle")+"\u00A7r", timeStampStyleID);
	public TCSettingEnum timeStampColor = new TCSettingEnum(ColorCodeEnum.DEFAULT, "\u00A7o"+TabbyChat.translator.getString("settings.general.timestampcolor")+"\u00A7r", timeStampColorID);
	public TCSettingBool groupSpam = new TCSettingBool(false, TabbyChat.translator.getString("settings.general.groupspam"), groupSpamID);
	public TCSettingBool unreadFlashing = new TCSettingBool(true, TabbyChat.translator.getString("settings.general.unreadflashing"), unreadFlashingID);
	
	public TCSettingsGeneral() {
		super();
		this.name = TabbyChat.translator.getString("settings.general.name");
		this.bgcolor = 0x664782be;
	}
	
	public TCSettingsGeneral(TabbyChat _tc) {
		this();
		tc = _tc;
	}
	
	public void actionPerformed(GuiButton button) {
		TabbyChat tc = GuiNewChatTC.getInstance().tc;
		super.actionPerformed(button);
		switch (button.id) {
		case tabbyChatEnableID:
			if (tc.enabled())
				tc.disable();
			else {
				tc.enable();
			}
			break;	
		}
			
		this.validateButtonStates();
	}
	
	private void applyTimestampPattern() {
		if(((ColorCodeEnum)this.timeStampColor.getValue()).toCode().length() > 0) {
			StringBuilder tsPattern = new StringBuilder();
			tsPattern.append("'").append(((ColorCodeEnum)this.timeStampColor.getValue()).toCode()).append("'");
			tsPattern.append(((TimeStampEnum)this.timeStampStyle.getValue()).toCode());
			tsPattern.append("'\u00A7r'");
			this.timeStamp.applyPattern(tsPattern.toString());
		} else {
			this.timeStamp.applyPattern(((TimeStampEnum)this.timeStampStyle.getValue()).toCode());
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
	
		this.tabbyChatEnable.setButtonLoc(col1x, this.rowY(1));
		this.tabbyChatEnable.setLabelLoc(col1x + 19);
		this.tabbyChatEnable.buttonColor = buttonColor;
		this.buttonList.add(this.tabbyChatEnable);
		
		this.saveChatLog.setButtonLoc(col1x, this.rowY(2));
		this.saveChatLog.setLabelLoc(col1x + 19);
		this.saveChatLog.buttonColor = buttonColor;
		this.buttonList.add(this.saveChatLog);
		
		this.timeStampEnable.setButtonLoc(col1x,  this.rowY(3));
		this.timeStampEnable.setLabelLoc(col1x + 19);
		this.timeStampEnable.buttonColor = buttonColor;
		this.buttonList.add(this.timeStampEnable);
		
		this.timeStampStyle.setButtonDims(80, 11);
		this.timeStampStyle.setButtonLoc(effRight - 80, this.rowY(4));
		this.timeStampStyle.setLabelLoc(this.timeStampStyle.xPosition - 10 - mc.fontRenderer.getStringWidth(this.timeStampStyle.description));
		this.buttonList.add(this.timeStampStyle);
		
		this.timeStampColor.setButtonDims(80, 11);
		this.timeStampColor.setButtonLoc(effRight - 80, this.rowY(5));
		this.timeStampColor.setLabelLoc(this.timeStampColor.xPosition - 10 - mc.fontRenderer.getStringWidth(this.timeStampColor.description));
		this.buttonList.add(this.timeStampColor);
		
		this.groupSpam.setButtonLoc(col1x, this.rowY(6));
		this.groupSpam.setLabelLoc(col1x + 19);
		this.groupSpam.buttonColor = buttonColor;
		this.buttonList.add(this.groupSpam);
		
		this.unreadFlashing.setButtonLoc(col1x, this.rowY(7));
		this.unreadFlashing.setLabelLoc(col1x + 19);
		this.unreadFlashing.buttonColor = buttonColor;
		this.buttonList.add(this.unreadFlashing);
		
		this.validateButtonStates();
	}

	public void loadSettingsFile() { 
		this.settingsFile = new File(tabbyChatDir, "general.cfg");

		if (!this.settingsFile.exists())
			return;		
		Properties settingsTable = new Properties();

		try {
			FileInputStream fInStream = new FileInputStream(this.settingsFile);
			BufferedInputStream bInStream = new BufferedInputStream(fInStream);
			settingsTable.load(bInStream);
			bInStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to read from general settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}

		this.tabbyChatEnable.setCleanValue(settingsTable.get("tabbyChatEnable"));
		this.saveChatLog.setCleanValue(settingsTable.get("saveChatLog"));
		this.timeStampEnable.setCleanValue(settingsTable.get("timeStampEnable"));
		this.timeStampStyle.setCleanValue(TabbyChatUtils.parseTimestamp(settingsTable.get("timeStampStyle")));
		this.groupSpam.setCleanValue(settingsTable.get("groupSpam"));
		this.unreadFlashing.setCleanValue(settingsTable.get("unreadFlashing"));
		this.timeStampColor.setCleanValue(TabbyChatUtils.parseColor(settingsTable.get("timeStampColor")));

		this.applyTimestampPattern();
		this.resetTempVars();
		return;
	}

	protected void resetTempVars() {
		this.tabbyChatEnable.reset();
		this.saveChatLog.reset();
		this.timeStampEnable.reset();
		this.timeStampStyle.reset();
		this.groupSpam.reset();
		this.unreadFlashing.reset();
		this.timeStampColor.reset();
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
		settingsTable.put("timeStampColor", this.timeStampColor.getValue().name());
		
		try {
			FileOutputStream fOutStream = new FileOutputStream(this.settingsFile);
			BufferedOutputStream bOutStream = new BufferedOutputStream(fOutStream);
			settingsTable.store(bOutStream, "General settings");
			bOutStream.close();
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
		this.timeStampColor.save();
		this.applyTimestampPattern();
	}
	
	public void validateButtonStates() {
		this.timeStampColor.enabled = this.timeStampEnable.getTempValue();
	}
}
