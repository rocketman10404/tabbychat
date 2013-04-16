package acs.tabbychat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

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
	
	protected TCSettingBool autoChannelSearch = new TCSettingBool(true, "Auto-search for new channels", autoChannelSearchID);
	protected TCSettingEnum delimiterChars = new TCSettingEnum(ChannelDelimEnum.BRACKETS, "Chat-channel delimiters", chatChannelDelimsID);
	protected TCSettingBool delimColorBool = new TCSettingBool(false,"\u00A7oColored delimiters\u00A7r", delimColorBoolID);
	protected TCSettingEnum delimColorCode = new TCSettingEnum(ColorCodeEnum.DEFAULT, "", delimColorEnumID);
	protected TCSettingBool delimFormatBool = new TCSettingBool(false,"\u00A7oFormatted delimiters\u00A7r", delimFormatBoolID);
	protected TCSettingEnum delimFormatCode = new TCSettingEnum(FormatCodeEnum.DEFAULT, "", delimFormatEnumID);
	protected TCSettingTextBox defaultChannels = new TCSettingTextBox("Default channels", defaultChansID);
	protected TCSettingTextBox ignoredChannels = new TCSettingTextBox("Ignored channels", ignoredChansID);
	
	private ServerData server;
	public String serverName;
	public String serverIP;
	
	public TCSettingsServer() {
		super();
		this.name = "Server Config";
		this.bgcolor = 0x66d6d643;
		this.updateForServer();
	}
	
	protected TCSettingsServer(TabbyChat _tc) {
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
		this.autoChannelSearch.labelX = col1x + 19;
		this.autoChannelSearch.buttonOnColor = buttonColor;
		this.buttonList.add(this.autoChannelSearch);
		
		this.delimiterChars.labelX = col1x;
		this.delimiterChars.setButtonLoc(col1x + 20 + mc.fontRenderer.getStringWidth(this.delimiterChars.description), this.rowY(2));
		this.delimiterChars.setButtonDims(80, 11);
		this.buttonList.add(this.delimiterChars);
		
		this.delimColorBool.setButtonLoc(col1x + 20, this.rowY(3));
		this.delimColorBool.labelX = col1x + 39;
		this.delimColorBool.buttonOnColor = buttonColor;
		this.buttonList.add(this.delimColorBool);
		
		this.delimColorCode.setButtonLoc(effRight - 70, this.rowY(3));
		this.delimColorCode.setButtonDims(70, 11);
		this.buttonList.add(this.delimColorCode);
		
		this.delimFormatBool.setButtonLoc(col1x + 20, this.rowY(4));
		this.delimFormatBool.labelX = col1x + 39;
		this.delimFormatBool.buttonOnColor = buttonColor;
		this.buttonList.add(this.delimFormatBool);
		
		this.delimFormatCode.setButtonLoc(this.delimColorCode.xPosition, this.rowY(4));
		this.delimFormatCode.setButtonDims(70, 11);
		this.buttonList.add(this.delimFormatCode);
		
		this.defaultChannels.labelX = col1x;
		this.defaultChannels.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.defaultChannels.description), this.rowY(5));
		this.defaultChannels.setButtonDims(effRight - this.defaultChannels.xPosition, 11);
		this.defaultChannels.setCharLimit(300);
		this.buttonList.add(this.defaultChannels);
		
		this.ignoredChannels.labelX = col1x;
		this.ignoredChannels.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.ignoredChannels.description), this.rowY(6));
		this.ignoredChannels.setButtonDims(effRight - this.ignoredChannels.xPosition, 11);
		this.ignoredChannels.setCharLimit(300);
		this.buttonList.add(this.ignoredChannels);
		
		this.validateButtonStates();
	}

	public void validateButtonStates() {
		this.delimColorBool.enabled = this.autoChannelSearch.getTempValue();
		this.delimFormatBool.enabled = this.autoChannelSearch.getTempValue();
		this.delimColorCode.enabled = this.delimColorBool.getTempValue() && this.autoChannelSearch.getTempValue();
		this.delimFormatCode.enabled = this.delimFormatBool.getTempValue() && this.autoChannelSearch.getTempValue();
		this.delimiterChars.enabled = this.autoChannelSearch.getTempValue();
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

	protected void importSettings() {
		this.autoChannelSearch.setValue(tc.globalPrefs.autoSearchEnabled);
		this.delimiterChars.setValue(tc.serverPrefs.chanDelims);
		this.delimColorCode.setValue(TabbyChatUtils.parseColor(tc.serverPrefs.chanDelimColor.name()));
		this.delimFormatCode.setValue(TabbyChatUtils.parseFormat(tc.serverPrefs.chanDelimFormat.name()));
		this.defaultChannels.setValue(TabbyChatUtils.join(tc.serverPrefs.defaultChans, ","));
		this.ignoredChannels.setValue(TabbyChatUtils.join(tc.serverPrefs.ignoredChans, ","));
		this.resetTempVars();
	}
	
	protected boolean loadSettingsFile() {
		boolean loaded = false;
		if (this.server == null)
			return loaded;
		String ip = this.serverIP;
			
		if (ip.contains(":")) {
			ip = ip.replaceAll(":", "(") + ")";
		}
		
		File settingsDir = new File(tabbyChatDir, ip);
		this.settingsFile = new File(settingsDir, "settings.cfg");
	
		if (!this.settingsFile.exists())
			return loaded;		
		Properties settingsTable = new Properties();
		
		try {
			FileInputStream fInStream = new FileInputStream(this.settingsFile);
			settingsTable.load(fInStream);
			fInStream.close();
			loaded = true;
		} catch (Exception e) {
			TabbyChat.printErr("Unable to read from server settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
			loaded = false;
		}
		
		this.autoChannelSearch.setValue(Boolean.parseBoolean((String)settingsTable.getProperty("autoChannelSearch")));
		this.delimiterChars.setValue(TabbyChatUtils.parseDelimiters((String)settingsTable.getProperty("delimiterChars")));
		this.delimColorBool.setValue(Boolean.parseBoolean((String)settingsTable.getProperty("delimColorBool")));
		this.delimColorCode.setValue(TabbyChatUtils.parseColor((String)settingsTable.getProperty("delimColorCode")));
		this.delimFormatBool.setValue(Boolean.parseBoolean((String)settingsTable.getProperty("delimFormatBool")));
		this.delimFormatCode.setValue(TabbyChatUtils.parseFormat((String)settingsTable.getProperty("delimFormatCode")));
		this.defaultChannels.setValue((String)settingsTable.getProperty("defaultChannels"));
		this.ignoredChannels.setValue((String)settingsTable.getProperty("ignoredChannels"));

		this.resetTempVars();
		return loaded;
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
			settingsTable.store(fOutStream, "Custom filters");
			fOutStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to write to filter settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
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
}
