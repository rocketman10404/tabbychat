package acs.tabbychat.gui;

import java.util.List;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.ChannelDelimEnum;
import acs.tabbychat.settings.ColorCodeEnum;
import acs.tabbychat.settings.FormatCodeEnum;
import acs.tabbychat.settings.TCSettingBool;
import acs.tabbychat.settings.TCSettingEnum;
import acs.tabbychat.settings.TCSettingTextBox;
import acs.tabbychat.util.TabbyChatUtils;

import net.minecraft.src.Minecraft;
import net.minecraft.src.ServerData;

public class TCSettingsServer extends TCSettingsGUI {
	private static final int AUTO_CHANNEL_SEARCH_ID = 9201;
	private static final int CHATCHANNEL_DELIMS_ID = 9202;
	private static final int DELIM_COLOR_BOOL_ID = 9203;
	private static final int DELIM_COLOR_ENUM_ID = 9204;
	private static final int DELIM_FORMAT_BOOL_ID = 9205;
	private static final int DELIM_FORMAT_ENUM_ID = 9206;
	private static final int DEFAULT_CHANNELS_ID = 9207;
	private static final int IGNORED_CHANNELS_ID = 9208;
	private static final int AUTO_PM_SEARCH_ID = 9209;
	
	{
		this.propertyPrefix = "settings.server";
	}
	
	public TCSettingBool autoChannelSearch = new TCSettingBool(true, "autoChannelSearch", this.propertyPrefix, AUTO_CHANNEL_SEARCH_ID);
	public TCSettingBool autoPMSearch = new TCSettingBool(true, "autoPMSearch", this.propertyPrefix, AUTO_PM_SEARCH_ID);
	public TCSettingEnum delimiterChars = new TCSettingEnum(ChannelDelimEnum.BRACKETS, "delimiterChars", this.propertyPrefix, CHATCHANNEL_DELIMS_ID);
	public TCSettingBool delimColorBool = new TCSettingBool(false, "delimColorBool", this.propertyPrefix, DELIM_COLOR_BOOL_ID, FormatCodeEnum.ITALIC);
	public TCSettingEnum delimColorCode = new TCSettingEnum(ColorCodeEnum.DEFAULT, "delimColorCode", "", DELIM_COLOR_ENUM_ID);
	public TCSettingBool delimFormatBool = new TCSettingBool(false, "delimFormatBool", this.propertyPrefix, DELIM_FORMAT_BOOL_ID, FormatCodeEnum.ITALIC);
	public TCSettingEnum delimFormatCode = new TCSettingEnum(FormatCodeEnum.DEFAULT, "delimFormatCode", "", DELIM_FORMAT_ENUM_ID);
	public TCSettingTextBox defaultChannels = new TCSettingTextBox("", "defaultChannels", this.propertyPrefix, DEFAULT_CHANNELS_ID);
	public TCSettingTextBox ignoredChannels = new TCSettingTextBox("", "ignoredChannels", this.propertyPrefix, IGNORED_CHANNELS_ID);
	
	public List<String> defaultChanList = new ArrayList();
	public List<String> ignoredChanList = new ArrayList();
	
	public String serverIP = "";
	
	public TCSettingsServer(TabbyChat _tc) {
		super(_tc);
		this.name = TabbyChat.translator.getString("settings.server.name");
		this.settingsFile = new File(TabbyChatUtils.getServerDir(), "settings.cfg");
		this.bgcolor = 0x66d6d643;
		this.defaultChannels.setCharLimit(300);
		this.ignoredChannels.setCharLimit(300);
		this.defineDrawableSettings();
	}
	
	public void defineDrawableSettings() {
		this.buttonList.add(this.autoChannelSearch);
		this.buttonList.add(this.autoPMSearch);
		this.buttonList.add(this.delimiterChars);
		this.buttonList.add(this.delimColorBool);
		this.buttonList.add(this.delimColorCode);
		this.buttonList.add(this.delimFormatBool);
		this.buttonList.add(this.delimFormatCode);
		this.buttonList.add(this.defaultChannels);
		this.buttonList.add(this.ignoredChannels);
	}
	
	public void initDrawableSettings() {
		int effRight = (this.width + DISPLAY_WIDTH)/2;
		int col1x = (this.width - DISPLAY_WIDTH)/2 + 55;
		
		int buttonColor = (this.bgcolor & 0x00ffffff) + 0xff000000;
		
		this.autoChannelSearch.setButtonLoc(col1x, this.rowY(1));
		this.autoChannelSearch.setLabelLoc(col1x + 19);
		this.autoChannelSearch.buttonColor = buttonColor;
		
		this.autoPMSearch.setButtonLoc(col1x, this.rowY(2));
		this.autoPMSearch.setLabelLoc(col1x + 19);
		this.autoPMSearch.buttonColor = buttonColor;
		
		this.delimiterChars.setLabelLoc(col1x);
		this.delimiterChars.setButtonLoc(col1x + 20 + mc.fontRenderer.getStringWidth(this.delimiterChars.description), this.rowY(3));
		this.delimiterChars.setButtonDims(80, 11);

		this.delimColorBool.setButtonLoc(col1x + 20, this.rowY(4));
		this.delimColorBool.setLabelLoc(col1x + 49);
		this.delimColorBool.buttonColor = buttonColor;
		
		this.delimColorCode.setButtonLoc(effRight - 70, this.rowY(4));
		this.delimColorCode.setButtonDims(70, 11);
		
		this.delimFormatBool.setButtonLoc(col1x + 20, this.rowY(5));
		this.delimFormatBool.setLabelLoc(col1x + 39);
		this.delimFormatBool.buttonColor = buttonColor;
		
		this.delimFormatCode.setButtonLoc(this.delimColorCode.xPosition, this.rowY(5));
		this.delimFormatCode.setButtonDims(70, 11);
		
		this.defaultChannels.setLabelLoc(col1x);
		this.defaultChannels.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.defaultChannels.description), this.rowY(6));
		this.defaultChannels.setButtonDims(effRight - this.defaultChannels.xPosition, 11);
		
		this.ignoredChannels.setLabelLoc(col1x);
		this.ignoredChannels.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.ignoredChannels.description), this.rowY(7));
		this.ignoredChannels.setButtonDims(effRight - this.ignoredChannels.xPosition, 11);
	}

	public Properties loadSettingsFile() {
		if(this.settingsFile != null) {
			super.loadSettingsFile();
			this.defaultChanList = Arrays.asList(Pattern.compile("[ ]?,[ ]?").split(this.defaultChannels.getValue()));
			this.ignoredChanList = Arrays.asList(Pattern.compile("[ ]?,[ ]?").split(this.ignoredChannels.getValue()));
		}
		return null;
	}

	public void storeTempVars() {
		super.storeTempVars();
		this.defaultChanList = Arrays.asList(Pattern.compile("[ ]?,[ ]?").split(this.defaultChannels.getValue()));
		this.ignoredChanList = Arrays.asList(Pattern.compile("[ ]?,[ ]?").split(this.ignoredChannels.getValue()));
	}

	public void updateForServer() {
		this.serverIP = TabbyChatUtils.getServerIp();
		this.settingsFile = new File(TabbyChatUtils.getServerDir(), "settings.cfg");
	}
	
	public void validateButtonStates() {
		this.delimColorBool.enabled = this.autoChannelSearch.getTempValue();
		this.delimFormatBool.enabled = this.autoChannelSearch.getTempValue();
		this.delimColorCode.enabled = this.delimColorBool.getTempValue() && this.autoChannelSearch.getTempValue();
		this.delimFormatCode.enabled = this.delimFormatBool.getTempValue() && this.autoChannelSearch.getTempValue();
		this.delimiterChars.enabled = this.autoChannelSearch.getTempValue();
	}
}
