package acs.tabbychat.gui;

import java.util.List;
import java.io.BufferedInputStream;
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

import net.minecraft.client.Minecraft;
import net.minecraft.src.ServerData;

public class TCSettingsServer extends TCSettingsGUI {

	private static final int autoChannelSearchID = 9201;
	private static final int chatChannelDelimsID = 9202;
	private static final int delimColorBoolID = 9203;
	private static final int delimColorEnumID = 9204;
	private static final int delimFormatBoolID = 9205;
	private static final int delimFormatEnumID = 9206;
	private static final int defaultChansID = 9207;
	private static final int ignoredChansID = 9208;
	
	public TCSettingBool autoChannelSearch = new TCSettingBool(true, "Auto-search for new channels", autoChannelSearchID);
	public TCSettingEnum delimiterChars = new TCSettingEnum(ChannelDelimEnum.BRACKETS, "Chat-channel delimiters", chatChannelDelimsID);
	public TCSettingBool delimColorBool = new TCSettingBool(false,"\u00A7oColored delimiters\u00A7r", delimColorBoolID);
	public TCSettingEnum delimColorCode = new TCSettingEnum(ColorCodeEnum.DEFAULT, "", delimColorEnumID);
	public TCSettingBool delimFormatBool = new TCSettingBool(false,"\u00A7oFormatted delimiters\u00A7r", delimFormatBoolID);
	public TCSettingEnum delimFormatCode = new TCSettingEnum(FormatCodeEnum.DEFAULT, "", delimFormatEnumID);
	public TCSettingTextBox defaultChannels = new TCSettingTextBox("Default channels", defaultChansID);
	public TCSettingTextBox ignoredChannels = new TCSettingTextBox("Ignored channels", ignoredChansID);
	
	public List<String> defaultChanList = new ArrayList();
	public List<String> ignoredChanList = new ArrayList();
	
	public ServerData server = null;
	public String serverName = null;
	public String serverIP = null;
	
	public TCSettingsServer() {
		super();
		this.name = "Server Config";
		this.bgcolor = 0x66d6d643;
		this.defaultChannels.setCharLimit(300);
		this.ignoredChannels.setCharLimit(300);
		this.updateForServer();
	}
	
	public TCSettingsServer(TabbyChat _tc) {
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
		
		this.autoChannelSearch.setButtonLoc(col1x, this.rowY(1));
		this.autoChannelSearch.setLabelLoc(col1x + 19);
		this.autoChannelSearch.buttonColor = buttonColor;
		this.buttonList.add(this.autoChannelSearch);
		
		this.delimiterChars.setLabelLoc(col1x);
		this.delimiterChars.setButtonLoc(col1x + 20 + mc.fontRenderer.getStringWidth(this.delimiterChars.description), this.rowY(2));
		this.delimiterChars.setButtonDims(80, 11);
		this.buttonList.add(this.delimiterChars);
		
		this.delimColorBool.setButtonLoc(col1x + 20, this.rowY(3));
		this.delimColorBool.setLabelLoc(col1x + 39);
		this.delimColorBool.buttonColor = buttonColor;
		this.buttonList.add(this.delimColorBool);
		
		this.delimColorCode.setButtonLoc(effRight - 70, this.rowY(3));
		this.delimColorCode.setButtonDims(70, 11);
		this.buttonList.add(this.delimColorCode);
		
		this.delimFormatBool.setButtonLoc(col1x + 20, this.rowY(4));
		this.delimFormatBool.setLabelLoc(col1x + 39);
		this.delimFormatBool.buttonColor = buttonColor;
		this.buttonList.add(this.delimFormatBool);
		
		this.delimFormatCode.setButtonLoc(this.delimColorCode.xPosition, this.rowY(4));
		this.delimFormatCode.setButtonDims(70, 11);
		this.buttonList.add(this.delimFormatCode);
		
		this.defaultChannels.setLabelLoc(col1x);
		this.defaultChannels.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.defaultChannels.description), this.rowY(5));
		this.defaultChannels.setButtonDims(effRight - this.defaultChannels.xPosition, 11);
		this.buttonList.add(this.defaultChannels);
		
		this.ignoredChannels.setLabelLoc(col1x);
		this.ignoredChannels.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.ignoredChannels.description), this.rowY(6));
		this.ignoredChannels.setButtonDims(effRight - this.ignoredChannels.xPosition, 11);
		this.buttonList.add(this.ignoredChannels);
		
		this.validateButtonStates();
	}

	public void loadSettingsFile() {
		if (this.server == null)
			return;
		String ip = this.serverIP;

		if (ip.contains(":")) {
			ip = ip.replaceAll(":", "(") + ")";
		}

		File settingsDir = new File(tabbyChatDir, ip);
		this.settingsFile = new File(settingsDir, "settings.cfg");

		if (!this.settingsFile.exists())
			return;		
		Properties settingsTable = new Properties();

		try {
			FileInputStream fInStream = new FileInputStream(this.settingsFile);
			BufferedInputStream bInStream = new BufferedInputStream(fInStream);
			settingsTable.load(bInStream);
			bInStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to read from server settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}

		this.autoChannelSearch.setCleanValue(settingsTable.getProperty("autoChannelSearch"));
		this.delimiterChars.setCleanValue(TabbyChatUtils.parseDelimiters(settingsTable.getProperty("delimiterChars")));
		this.delimColorBool.setCleanValue(settingsTable.getProperty("delimColorBool"));
		this.delimColorCode.setCleanValue(TabbyChatUtils.parseColor(settingsTable.getProperty("delimColorCode")));
		this.delimFormatBool.setCleanValue(settingsTable.getProperty("delimFormatBool"));
		this.delimFormatCode.setCleanValue(TabbyChatUtils.parseFormat(settingsTable.getProperty("delimFormatCode")));
		this.defaultChannels.setCleanValue(settingsTable.getProperty("defaultChannels"));
		this.ignoredChannels.setCleanValue(settingsTable.getProperty("ignoredChannels"));
		
		this.defaultChanList = Arrays.asList(Pattern.compile("[ ]?,[ ]?").split(this.defaultChannels.getValue()));
		this.ignoredChanList = Arrays.asList(Pattern.compile("[ ]?,[ ]?").split(this.ignoredChannels.getValue()));

		this.resetTempVars();		
		return;
	}

	protected void resetTempVars() {
		this.autoChannelSearch.reset();
		this.delimiterChars.reset();
		this.delimColorBool.reset();
		this.delimColorCode.reset();
		this.delimFormatBool.reset();
		this.delimFormatCode.reset();
		this.defaultChannels.reset();
		this.ignoredChannels.reset();
	}

	protected void saveSettingsFile() {	
		String ip = this.serverIP;
		if (ip.contains(":")) {
			ip = ip.replaceAll(":", "(") + ")";
		}

		File settingsDir = new File(tabbyChatDir, ip);

		if (!settingsDir.exists())
			settingsDir.mkdirs();
		settingsFile = new File(settingsDir, "settings.cfg");
		Properties settingsTable = new Properties();
		
		settingsTable.put("autoChannelSearch", this.autoChannelSearch.getValue().toString());
		settingsTable.put("delimiterChars", this.delimiterChars.getValue().name());
		settingsTable.put("delimColorBool", this.delimColorBool.getValue().toString());
		settingsTable.put("delimColorCode", this.delimColorCode.getValue().name());
		settingsTable.put("delimFormatBool", this.delimFormatBool.getValue().toString());
		settingsTable.put("delimFormatCode", this.delimFormatCode.getValue().name());
		settingsTable.put("defaultChannels", this.defaultChannels.getValue());
		settingsTable.put("ignoredChannels", this.ignoredChannels.getValue());
		
		try {
			FileOutputStream fOutStream = new FileOutputStream(settingsFile);
			settingsTable.store(fOutStream, "Server config");
			fOutStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to write to server config file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}

	protected void storeTempVars() {
		this.autoChannelSearch.save();
		this.delimiterChars.save();
		this.delimColorBool.save();
		this.delimColorCode.save();
		this.delimFormatBool.save();
		this.delimFormatCode.save();
		this.defaultChannels.save();
		this.ignoredChannels.save();
		
		this.defaultChanList = Arrays.asList(Pattern.compile("[ ]?,[ ]?").split(this.defaultChannels.getValue()));
		this.ignoredChanList = Arrays.asList(Pattern.compile("[ ]?,[ ]?").split(this.ignoredChannels.getValue()));
	}

	public void updateForServer() {	
		if (Minecraft.getMinecraft().isSingleplayer() || Minecraft.getMinecraft().getServerData() == null) {
			this.server = null;
			this.settingsFile = null;
			this.serverName = "";
			this.serverIP = "";
		} else {
			this.server = Minecraft.getMinecraft().getServerData();
			this.serverName = this.server.serverName;
			this.serverIP = this.server.serverIP;
		}
	}
	
	public void validateButtonStates() {
		this.delimColorBool.enabled = this.autoChannelSearch.getTempValue();
		this.delimFormatBool.enabled = this.autoChannelSearch.getTempValue();
		this.delimColorCode.enabled = this.delimColorBool.getTempValue() && this.autoChannelSearch.getTempValue();
		this.delimFormatCode.enabled = this.delimFormatBool.getTempValue() && this.autoChannelSearch.getTempValue();
		this.delimiterChars.enabled = this.autoChannelSearch.getTempValue();
	}
}
