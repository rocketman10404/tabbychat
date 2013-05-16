package acs.tabbychat.gui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.TCSetting;
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
	
	public TCSettingTextBox chatScrollHistory = new TCSettingTextBox("100", "Chat history to retain (lines)", chatScrollHistoryID);
	public TCSettingTextBox maxLengthChannelName = new TCSettingTextBox("10", "Channel name max. length", maxLengthChannelNameID);
	public TCSettingTextBox multiChatDelay = new TCSettingTextBox("100", "Multi-chat send delay (ms)", multiChatDelayID);
	public TCSettingBool customChatBoxSize = new TCSettingBool(false, "Custom Chatbox size (screen %)", customChatBoxSizeID);
	public TCSettingSlider chatBoxWidth = new TCSettingSlider(50.0f, "Width", chatBoxWidthID, 20.0f, 100.0f);
	public TCSettingSlider chatBoxFocHeight = new TCSettingSlider(50.0f, "Focused Height", chatBoxFocHeightID, 20.0f, 100.0f);
	public TCSettingSlider chatBoxUnfocHeight = new TCSettingSlider(20.0f, "Unfocused Height", chatBoxUnfocHeightID, 20.0f, 100.0f);
	public TCSettingSlider chatFadeTicks = new TCSettingSlider(200.0f, "Chat fade time (ticks)", chatFadeTicksID, 10.0f, 2000.0f);
	public TCSettingBool forceUnicode = new TCSettingBool(false, "Force Unicode Chat Rendering", forceUnicodeID);
	
	public TCSettingsAdvanced() {
		super();
		this.name = "Advanced Settings";
		this.bgcolor = 0x66802e94;
		this.chatScrollHistory.setCharLimit(3);
		this.maxLengthChannelName.setCharLimit(2);
		this.multiChatDelay.setCharLimit(4);
	}
	
	public TCSettingsAdvanced(TabbyChat _tc) {
		this();
		tc = _tc;
	}
	
	public void handleMouseInput() {
		super.handleMouseInput();
		for (int i = 0; i < this.buttonList.size(); i++) {
			if (TCSetting.class.isInstance(this.buttonList.get(i))) {
				TCSetting tmp = (TCSetting)this.buttonList.get(i);
				if (tmp.type == "slider") {
					((TCSettingSlider)tmp).handleMouseInput();
				}
			}
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
		
		this.customChatBoxSize.setButtonLoc(col1x, this.rowY(4));
		this.customChatBoxSize.setLabelLoc(col1x + 19);
		this.customChatBoxSize.buttonColor = buttonColor;
		this.buttonList.add(this.customChatBoxSize);
		
		this.chatBoxWidth.setLabelLoc(col1x+10);
		this.chatBoxWidth.setButtonLoc(col1x + 15 + mc.fontRenderer.getStringWidth(this.chatBoxWidth.description), this.rowY(5));
		this.chatBoxWidth.buttonColor = buttonColor;
		this.buttonList.add(this.chatBoxWidth);
		
		this.chatBoxFocHeight.setLabelLoc(col1x+10);
		this.chatBoxFocHeight.setButtonLoc(col1x + 15 + mc.fontRenderer.getStringWidth(this.chatBoxFocHeight.description), this.rowY(6));
		this.chatBoxFocHeight.buttonColor = buttonColor;
		this.buttonList.add(this.chatBoxFocHeight);
		
		this.chatBoxUnfocHeight.setLabelLoc(col1x+10);
		this.chatBoxUnfocHeight.setButtonLoc(col1x + 15 + mc.fontRenderer.getStringWidth(this.chatBoxUnfocHeight.description), this.rowY(7));
		this.chatBoxUnfocHeight.buttonColor = buttonColor;
		this.buttonList.add(this.chatBoxUnfocHeight);
		
		this.chatFadeTicks.setLabelLoc(col1x);
		this.chatFadeTicks.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.chatFadeTicks.description), this.rowY(8));
		this.chatFadeTicks.buttonColor = buttonColor;
		this.chatFadeTicks.units = "";
		this.buttonList.add(this.chatFadeTicks);
		
		this.forceUnicode.setButtonLoc(col1x, this.rowY(9));
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
		this.customChatBoxSize.setCleanValue(settingsTable.getProperty("customChatBoxSize"));
		this.chatBoxWidth.setCleanValue(TabbyChatUtils.parseFloat(settingsTable.getProperty("chatBoxWidth"), 20.0f, 100.0f));
		this.chatBoxFocHeight.setCleanValue(TabbyChatUtils.parseFloat(settingsTable.getProperty("chatBoxFocHeight"), 20.0f, 100.0f));
		this.chatBoxUnfocHeight.setCleanValue(TabbyChatUtils.parseFloat(settingsTable.getProperty("chatBoxUnfocHeight"), 20.0f, 100.0f));
		this.chatFadeTicks.setCleanValue(TabbyChatUtils.parseFloat(settingsTable.getProperty("chatFadeTicks"), 10.0f, 2000.0f));
		this.forceUnicode.setCleanValue(settingsTable.getProperty("forceUnicode"));
		this.resetTempVars();
		return;
	}
		
	protected void resetTempVars() {
		this.chatScrollHistory.reset();
		this.maxLengthChannelName.reset();
		this.multiChatDelay.reset();
		this.customChatBoxSize.reset();
		this.chatBoxWidth.reset();
		this.chatBoxFocHeight.reset();
		this.chatBoxUnfocHeight.reset();
		this.chatFadeTicks.reset();
		this.forceUnicode.reset();
	}
	
	protected void saveSettingsFile() {
		if (!tabbyChatDir.exists())
			tabbyChatDir.mkdirs();
		Properties settingsTable = new Properties();
		settingsTable.put("chatScrollHistory", this.chatScrollHistory.getValue());
		settingsTable.put("maxLengthChannelName", this.maxLengthChannelName.getValue());
		settingsTable.put("multiChatDelay", this.multiChatDelay.getValue());
		settingsTable.put("customChatBoxSize", this.customChatBoxSize.getValue().toString());
		settingsTable.put("chatBoxWidth", this.chatBoxWidth.getValue().toString());
		settingsTable.put("chatBoxFocHeight", this.chatBoxFocHeight.getValue().toString());
		settingsTable.put("chatBoxUnfocHeight", this.chatBoxUnfocHeight.getValue().toString());
		settingsTable.put("chatFadeTicks", this.chatFadeTicks.getValue().toString());
		settingsTable.put("forceUnicode", this.forceUnicode.getValue().toString());
		
		try {
			FileOutputStream fOutStream = new FileOutputStream(this.settingsFile);
			settingsTable.store(fOutStream, "Advanced settings");
			fOutStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to write to advanced settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}
	
	protected void storeTempVars() {
		this.chatScrollHistory.save();
		this.maxLengthChannelName.save();
		this.multiChatDelay.save();
		this.customChatBoxSize.save();
		this.chatBoxWidth.save();
		this.chatBoxFocHeight.save();
		this.chatBoxUnfocHeight.save();
		this.chatFadeTicks.save();
		this.forceUnicode.save();
	}

	public void validateButtonStates() {
		if (!this.customChatBoxSize.getTempValue()) {
			this.chatBoxWidth.disable();
			this.chatBoxFocHeight.disable();
			this.chatBoxUnfocHeight.disable();
		} else {
			this.chatBoxWidth.enable();
			this.chatBoxFocHeight.enable();
			this.chatBoxUnfocHeight.enable();
		}
	}
}
