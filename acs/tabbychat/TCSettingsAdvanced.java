package acs.tabbychat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class TCSettingsAdvanced extends TCSettingsGUI {

	private static final int chatScrollHistoryID = 9401;
	private static final int maxLengthChannelNameID = 9402;
	private static final int multiChatDelayID = 9403;
	private static final int chatBoxWidthID = 9404;
	private static final int chatBoxFocHeightID = 9405;
	private static final int chatBoxUnfocHeightID = 9406;
	private static final int customChatBoxSizeID = 9407;
	private static final int chatFadeTicksID = 9408;
	
	public TCSettingTextBox chatScrollHistory = new TCSettingTextBox("100", "Chat history to retain (lines)", chatScrollHistoryID);
	protected TCSettingTextBox maxLengthChannelName = new TCSettingTextBox("10", "Channel name max. length", maxLengthChannelNameID);
	protected TCSettingTextBox multiChatDelay = new TCSettingTextBox("100", "Multi-chat send delay (ms)", multiChatDelayID);
	public TCSettingBool customChatBoxSize = new TCSettingBool(false, "Custom Chatbox size (screen %)", customChatBoxSizeID);
	public TCSettingSlider chatBoxWidth = new TCSettingSlider(50.0f, "Width", chatBoxWidthID, 20.0f, 100.0f);
	public TCSettingSlider chatBoxFocHeight = new TCSettingSlider(50.0f, "Focused Height", chatBoxFocHeightID, 20.0f, 100.0f);
	public TCSettingSlider chatBoxUnfocHeight = new TCSettingSlider(20.0f, "Unfocused Height", chatBoxUnfocHeightID, 20.0f, 100.0f);
	public TCSettingSlider chatFadeTicks = new TCSettingSlider(200.0f, "Chat fade time (ticks)", chatFadeTicksID, 10.0f, 2000.0f);
	
	public TCSettingsAdvanced() {
		super();
		this.name = "Advanced Settings";
		this.bgcolor = 0x66802e94;
	}
	
	protected TCSettingsAdvanced(TabbyChat _tc) {
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
		
		this.chatScrollHistory.labelX = col1x;
		this.chatScrollHistory.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.chatScrollHistory.description), this.rowY(1));
		this.chatScrollHistory.setButtonDims(30, 11);
		this.chatScrollHistory.textBox.setMaxStringLength(3);
		this.buttonList.add(this.chatScrollHistory);
		
		this.maxLengthChannelName.labelX = col1x;
		this.maxLengthChannelName.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.maxLengthChannelName.description), this.rowY(2));
		this.maxLengthChannelName.setButtonDims(20, 11);
		this.maxLengthChannelName.textBox.setMaxStringLength(2);
		this.buttonList.add(this.maxLengthChannelName);
		
		this.multiChatDelay.labelX = col1x;
		this.multiChatDelay.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.multiChatDelay.description), this.rowY(3));
		this.multiChatDelay.setButtonDims(40,11);
		this.multiChatDelay.textBox.setMaxStringLength(4);
		this.buttonList.add(this.multiChatDelay);
		
		this.customChatBoxSize.setButtonLoc(col1x, this.rowY(4));
		this.customChatBoxSize.labelX = col1x + 19;
		this.customChatBoxSize.buttonOnColor = buttonColor;
		this.buttonList.add(this.customChatBoxSize);
		
		this.chatBoxWidth.labelX = col1x+10;
		this.chatBoxWidth.setButtonLoc(col1x + 15 + mc.fontRenderer.getStringWidth(this.chatBoxWidth.description), this.rowY(5));
		this.chatBoxWidth.buttonOnColor = buttonColor;
		this.buttonList.add(this.chatBoxWidth);
		
		this.chatBoxFocHeight.labelX = col1x+10;
		this.chatBoxFocHeight.setButtonLoc(col1x + 15 + mc.fontRenderer.getStringWidth(this.chatBoxFocHeight.description), this.rowY(6));
		this.chatBoxFocHeight.buttonOnColor = buttonColor;
		this.buttonList.add(this.chatBoxFocHeight);
		
		this.chatBoxUnfocHeight.labelX = col1x+10;
		this.chatBoxUnfocHeight.setButtonLoc(col1x + 15 + mc.fontRenderer.getStringWidth(this.chatBoxUnfocHeight.description), this.rowY(7));
		this.chatBoxUnfocHeight.buttonOnColor = buttonColor;
		this.buttonList.add(this.chatBoxUnfocHeight);
		
		this.chatFadeTicks.labelX = col1x;
		this.chatFadeTicks.setButtonLoc(col1x + 5 + mc.fontRenderer.getStringWidth(this.chatFadeTicks.description), this.rowY(8));
		this.chatFadeTicks.buttonOnColor = buttonColor;
		this.chatFadeTicks.units = "";
		this.buttonList.add(this.chatFadeTicks);
		
		this.validateButtonStates();
		
		System.out.println("CBW value: "+this.chatBoxWidth.getValue());
		System.out.println("CBW temp value: "+this.chatBoxWidth.getTempValue());
		System.out.println("CBW slider value: "+this.chatBoxWidth.sliderValue);
		System.out.println("CBW max value: "+this.chatBoxWidth.maxValue);
		System.out.println("CBW min value: "+this.chatBoxWidth.minValue);
		
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
		
	protected void storeTempVars() {
		this.chatScrollHistory.setValue(TabbyChatUtils.parseInteger(this.chatScrollHistory.getTempValue(), 1, 999, 100).toString());
		this.maxLengthChannelName.setValue(TabbyChatUtils.parseInteger(this.maxLengthChannelName.getTempValue(), 1, 99, 10).toString());
		this.multiChatDelay.setValue(TabbyChatUtils.parseInteger(this.multiChatDelay.getTempValue(), 1, 9999, 100));
		this.customChatBoxSize.save();
		this.chatBoxWidth.save();
		this.chatBoxFocHeight.save();
		this.chatBoxUnfocHeight.save();
		this.chatFadeTicks.save();
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
	}
	
	protected void importSettings() {
		this.chatScrollHistory.setValue(Integer.toString(tc.globalPrefs.retainedChats));
		this.maxLengthChannelName.setValue(Integer.toString(tc.globalPrefs.maxChannelNameLength));
		this.resetTempVars();
	}
	
	protected boolean loadSettingsFile() {
		this.settingsFile = new File(tabbyChatDir, "advanced.cfg");
		boolean loaded = false;
		if (!this.settingsFile.exists())
			return loaded;		
		Properties settingsTable = new Properties();
		
		try {
			FileInputStream fInStream = new FileInputStream(this.settingsFile);
			settingsTable.load(fInStream);
			fInStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to read from advanced settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
		
		this.chatScrollHistory.setValue((String)settingsTable.getProperty("chatScrollHistory"));
		this.maxLengthChannelName.setValue((String)settingsTable.getProperty("maxLengthChannelName"));
		this.multiChatDelay.setValue((String)settingsTable.getProperty("multiChatDelay"));
		this.customChatBoxSize.setValue(Boolean.parseBoolean((String)settingsTable.getProperty("customChatBoxSize")));
		this.chatBoxWidth.setValue(TabbyChatUtils.parseFloat((String)settingsTable.getProperty("chatBoxWidth"), 20.0f, 100.0f, 50.0f));
		this.chatBoxFocHeight.setValue(TabbyChatUtils.parseFloat((String)settingsTable.getProperty("chatBoxFocHeight"), 20.0f, 100.0f, 50.0f));
		this.chatBoxUnfocHeight.setValue(TabbyChatUtils.parseFloat((String)settingsTable.getProperty("chatBoxUnfocHeight"), 20.0f, 100.0f, 20.0f));
		this.chatFadeTicks.setValue(TabbyChatUtils.parseFloat((String)settingsTable.getProperty("chatFadeTicks"), 10.0f, 2000.0f, 200.0f));
		loaded = true;
		this.resetTempVars();
		return loaded;
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
		
		try {
			FileOutputStream fOutStream = new FileOutputStream(this.settingsFile);
			settingsTable.store(fOutStream, "Advanced settings");
			fOutStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to write to advanced settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}
	
}
