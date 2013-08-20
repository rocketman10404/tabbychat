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
import acs.tabbychat.settings.FormatCodeEnum;
import acs.tabbychat.settings.ITCSetting;
import acs.tabbychat.settings.TCSettingBool;
import acs.tabbychat.settings.TCSettingEnum;
import acs.tabbychat.settings.TimeStampEnum;
import acs.tabbychat.util.TabbyChatUtils;

import net.minecraft.src.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.ServerData;

public class TCSettingsGeneral extends TCSettingsGUI {
	public SimpleDateFormat timeStamp = new SimpleDateFormat();
	
	private static final int TABBYCHAT_ENABLE_ID = 9101;
	private static final int SAVE_CHATLOG_ID = 9102;
	private static final int TIMESTAMP_ENABLE_ID = 9103;
	private static final int TIMESTAMP_STYLE_ID = 9104;
	private static final int GROUP_SPAM_ID = 9105;
	private static final int UNREAD_FLASHING_ID = 9106;
	private static final int TIMESTAMP_COLOR_ID = 9107;
	private static final int SPELL_CHECK_ENABLE = 9108;
	
	{
		this.propertyPrefix = "settings.general";
	}
		
	public TCSettingBool tabbyChatEnable = new TCSettingBool(true, "tabbyChatEnable", this.propertyPrefix, TABBYCHAT_ENABLE_ID);
	public TCSettingBool saveChatLog = new TCSettingBool(false, "saveChatLog", this.propertyPrefix, SAVE_CHATLOG_ID);
	public TCSettingBool timeStampEnable = new TCSettingBool(false, "timeStampEnable", this.propertyPrefix, TIMESTAMP_ENABLE_ID);
	public TCSettingEnum timeStampStyle = new TCSettingEnum(TimeStampEnum.MILITARY, "timeStampStyle", this.propertyPrefix, TIMESTAMP_STYLE_ID, FormatCodeEnum.ITALIC);
	public TCSettingEnum timeStampColor = new TCSettingEnum(ColorCodeEnum.DEFAULT, "timeStampColor", this.propertyPrefix, TIMESTAMP_COLOR_ID, FormatCodeEnum.ITALIC);
	public TCSettingBool groupSpam = new TCSettingBool(false, "groupSpam", this.propertyPrefix, GROUP_SPAM_ID);
	public TCSettingBool unreadFlashing = new TCSettingBool(true, "unreadFlashing", this.propertyPrefix, UNREAD_FLASHING_ID);
	public TCSettingBool spellCheckEnable = new TCSettingBool(true, "spellCheckEnable", this.propertyPrefix, SPELL_CHECK_ENABLE);
	
	public TCSettingsGeneral(TabbyChat _tc) {
		super(_tc);
		this.name = TabbyChat.translator.getString("settings.general.name");
		this.settingsFile = new File(tabbyChatDir, "general.cfg");
		this.bgcolor = 0x664782be;
		this.defineDrawableSettings();
	}
	
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
		case TABBYCHAT_ENABLE_ID:
			if (tc.enabled())
				tc.disable();
			else {
				tc.enable();
			}
			break;	
		}
		super.actionPerformed(button);
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
	
	public void defineDrawableSettings() {
		this.buttonList.add(this.tabbyChatEnable);
		this.buttonList.add(this.saveChatLog);
		this.buttonList.add(this.timeStampEnable);
		this.buttonList.add(this.timeStampStyle);
		this.buttonList.add(this.timeStampColor);
		this.buttonList.add(this.groupSpam);
		this.buttonList.add(this.unreadFlashing);
		this.buttonList.add(this.spellCheckEnable);
	}
	
	public void initDrawableSettings() {
		int effRight = (this.width + DISPLAY_WIDTH)/2;
		int col1x = (this.width - DISPLAY_WIDTH)/2 + 55;
		
		int buttonColor = (this.bgcolor & 0x00ffffff) + 0xff000000;
	
		this.tabbyChatEnable.setButtonLoc(col1x, this.rowY(1));
		this.tabbyChatEnable.setLabelLoc(col1x + 19);
		this.tabbyChatEnable.buttonColor = buttonColor;
		
		this.saveChatLog.setButtonLoc(col1x, this.rowY(2));
		this.saveChatLog.setLabelLoc(col1x + 19);
		this.saveChatLog.buttonColor = buttonColor;
		
		this.timeStampEnable.setButtonLoc(col1x,  this.rowY(3));
		this.timeStampEnable.setLabelLoc(col1x + 19);
		this.timeStampEnable.buttonColor = buttonColor;

		this.timeStampStyle.setButtonDims(80, 11);
		this.timeStampStyle.setButtonLoc(effRight - 80, this.rowY(4));
		this.timeStampStyle.setLabelLoc(this.timeStampStyle.xPosition - 10 - mc.fontRenderer.getStringWidth(this.timeStampStyle.description));
		
		this.timeStampColor.setButtonDims(80, 11);
		this.timeStampColor.setButtonLoc(effRight - 80, this.rowY(5));
		this.timeStampColor.setLabelLoc(this.timeStampColor.xPosition - 10 - mc.fontRenderer.getStringWidth(this.timeStampColor.description));
		
		this.groupSpam.setButtonLoc(col1x, this.rowY(6));
		this.groupSpam.setLabelLoc(col1x + 19);
		this.groupSpam.buttonColor = buttonColor;
		
		this.unreadFlashing.setButtonLoc(col1x, this.rowY(7));
		this.unreadFlashing.setLabelLoc(col1x + 19);
		this.unreadFlashing.buttonColor = buttonColor;
		
		this.spellCheckEnable.setButtonLoc(col1x, this.rowY(8));
		this.spellCheckEnable.setLabelLoc(col1x + 19);
		this.spellCheckEnable.buttonColor = buttonColor;
	}

	public Properties loadSettingsFile() { 
		super.loadSettingsFile();
		this.applyTimestampPattern();
		return null;
	}
	
	public void storeTempVars() {
		super.storeTempVars();
		this.applyTimestampPattern();
	}
	
	public void validateButtonStates() {
		this.timeStampColor.enabled = this.timeStampEnable.getTempValue();
	}
}
