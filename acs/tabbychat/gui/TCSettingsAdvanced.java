package acs.tabbychat.gui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.ITCSetting;
import acs.tabbychat.settings.TCSettingBool;
import acs.tabbychat.settings.TCSettingSlider;
import acs.tabbychat.settings.TCSettingTextBox;
import acs.tabbychat.util.TabbyChatUtils;

public class TCSettingsAdvanced extends TCSettingsGUI {

	private static final int chatScrollHistoryID = 9401;
	private static final int maxLengthChannelNameID = 9402;
	private static final int multiChatDelayID = 9403;
	private static final int chatBoxWidthID = 9404;
	private static final int chatBoxFocHeightID = 9405;
	private static final int chatBoxUnfocHeightID = 9406;
	private static final int customChatBoxSizeID = 9407;
	private static final int chatFadeTicksID = 9408;
	private static final int forceUnicodeID = 9409;
	
	public TCSettingTextBox chatScrollHistory = new TCSettingTextBox("100", TabbyChat.translator.getString("settings.advanced.chatscrollhistory"), chatScrollHistoryID);
	public TCSettingTextBox maxLengthChannelName = new TCSettingTextBox("10", TabbyChat.translator.getString("settings.advanced.maxlengthchannelname"), maxLengthChannelNameID);
	public TCSettingTextBox multiChatDelay = new TCSettingTextBox("100", TabbyChat.translator.getString("settings.advanced.multichatdelay"), multiChatDelayID);
	public TCSettingSlider chatBoxUnfocHeight = new TCSettingSlider(20.0f, TabbyChat.translator.getString("settings.advanced.chatboxunfocheight"), chatBoxUnfocHeightID, 20.0f, 100.0f);
	public TCSettingSlider chatFadeTicks = new TCSettingSlider(200.0f, TabbyChat.translator.getString("settings.advanced.chatfadeticks"), chatFadeTicksID, 10.0f, 2000.0f);
	public TCSettingBool forceUnicode = new TCSettingBool(false, TabbyChat.translator.getString("settings.advanced.forceunicode"), forceUnicodeID);
	
	public TCSettingsAdvanced() {
		super();
		this.name = TabbyChat.translator.getString("settings.advanced.name");
		this.bgcolor = 0x66802e94;
		this.chatScrollHistory.setCharLimit(3);
		this.maxLengthChannelName.setCharLimit(2);
		this.multiChatDelay.setCharLimit(4);
	}
	
	public TCSettingsAdvanced(TabbyChat _tc) {
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
		
		this.chatScrollHistory.setLabelLoc(col1x);
		this.chatScrollHistory.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.chatScrollHistory.description), this.rowY(1));
		this.chatScrollHistory.setButtonDims(30, 11);
		this.buttonList.add(this.chatScrollHistory);
		
		this.maxLengthChannelName.setLabelLoc(col1x);
		this.maxLengthChannelName.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.maxLengthChannelName.description), this.rowY(2));
		this.maxLengthChannelName.setButtonDims(20, 11);
		this.buttonList.add(this.maxLengthChannelName);
		
		this.multiChatDelay.setLabelLoc(col1x);
		this.multiChatDelay.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.multiChatDelay.description), this.rowY(3));
		this.multiChatDelay.setButtonDims(40,11);
		this.buttonList.add(this.multiChatDelay);
				
		this.chatBoxUnfocHeight.setLabelLoc(col1x);
		this.chatBoxUnfocHeight.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.chatBoxUnfocHeight.description), this.rowY(4));
		this.chatBoxUnfocHeight.buttonColor = buttonColor;
		this.buttonList.add(this.chatBoxUnfocHeight);
		
		this.chatFadeTicks.setLabelLoc(col1x);
		this.chatFadeTicks.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.chatFadeTicks.description), this.rowY(5));
		this.chatFadeTicks.buttonColor = buttonColor;
		this.chatFadeTicks.units = "";
		this.buttonList.add(this.chatFadeTicks);
		
		this.forceUnicode.setButtonLoc(col1x, this.rowY(6));
		this.forceUnicode.setLabelLoc(col1x + 19);
		this.forceUnicode.buttonColor = buttonColor;
		this.buttonList.add(this.forceUnicode);
		
		this.validateButtonStates();		
	}

	public void loadSettingsFile() {
		this.settingsFile = new File(tabbyChatDir, "advanced.cfg");
		if (!this.settingsFile.exists())
			return;		
		Properties settingsTable = new Properties();

		try {
			FileInputStream fInStream = new FileInputStream(this.settingsFile);
			BufferedInputStream bInStream = new BufferedInputStream(fInStream);
			settingsTable.load(bInStream);
			bInStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to read from advanced settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}

		this.chatScrollHistory.setCleanValue(settingsTable.getProperty("chatScrollHistory"));
		this.maxLengthChannelName.setCleanValue(settingsTable.getProperty("maxLengthChannelName"));
		this.multiChatDelay.setCleanValue(settingsTable.getProperty("multiChatDelay"));
		this.chatBoxUnfocHeight.setCleanValue(TabbyChatUtils.parseFloat(settingsTable.getProperty("chatBoxUnfocHeight"), 20.0f, 100.0f));
		this.chatFadeTicks.setCleanValue(TabbyChatUtils.parseFloat(settingsTable.getProperty("chatFadeTicks"), 10.0f, 2000.0f));
		this.forceUnicode.setCleanValue(settingsTable.getProperty("forceUnicode"));
		ChatBox.current.x = TabbyChatUtils.parseInteger(settingsTable.getProperty("chatbox.x"), ChatBox.absMinX, 10000, ChatBox.absMinX);
		ChatBox.current.y = TabbyChatUtils.parseInteger(settingsTable.getProperty("chatbox.y"), -10000, ChatBox.absMinY, ChatBox.absMinY);
		ChatBox.current.width = TabbyChatUtils.parseInteger(settingsTable.getProperty("chatbox.width"), ChatBox.absMinW, 10000, 320);
		ChatBox.current.height = TabbyChatUtils.parseInteger(settingsTable.getProperty("chatbox.height"), ChatBox.absMinH, 10000, 180);
		ChatBox.anchoredTop = Boolean.parseBoolean(settingsTable.getProperty("chatbox.anchoredtop"));		
		this.resetTempVars();
		return;
	}
		
	protected void resetTempVars() {
		this.chatScrollHistory.reset();
		this.maxLengthChannelName.reset();
		this.multiChatDelay.reset();
		this.chatBoxUnfocHeight.reset();
		this.chatFadeTicks.reset();
		this.forceUnicode.reset();
	}
	
	public void saveSettingsFile() {
		if (!tabbyChatDir.exists())
			tabbyChatDir.mkdirs();
		Properties settingsTable = new Properties();
		settingsTable.put("chatScrollHistory", this.chatScrollHistory.getValue());
		settingsTable.put("maxLengthChannelName", this.maxLengthChannelName.getValue());
		settingsTable.put("multiChatDelay", this.multiChatDelay.getValue());
		settingsTable.put("chatBoxUnfocHeight", this.chatBoxUnfocHeight.getValue().toString());
		settingsTable.put("chatFadeTicks", this.chatFadeTicks.getValue().toString());
		settingsTable.put("forceUnicode", this.forceUnicode.getValue().toString());
		settingsTable.put("chatbox.x", Integer.toString(ChatBox.current.x));
		settingsTable.put("chatbox.y", Integer.toString(ChatBox.current.y));
		settingsTable.put("chatbox.width", Integer.toString(ChatBox.current.width));
		settingsTable.put("chatbox.height", Integer.toString(ChatBox.current.height));
		settingsTable.put("chatbox.anchoredtop", Boolean.toString(ChatBox.anchoredTop));
		
		try {
			FileOutputStream fOutStream = new FileOutputStream(this.settingsFile);
			BufferedOutputStream bOutStream = new BufferedOutputStream(fOutStream);
			settingsTable.store(bOutStream, "Advanced settings");
			bOutStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to write to advanced settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}
	
	protected void storeTempVars() {
		this.chatScrollHistory.save();
		this.maxLengthChannelName.save();
		this.multiChatDelay.save();
		this.chatBoxUnfocHeight.save();
		this.chatFadeTicks.save();
		this.forceUnicode.save();
	}

	public void validateButtonStates() {
	}
}
