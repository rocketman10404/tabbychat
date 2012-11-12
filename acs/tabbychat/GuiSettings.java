package acs.tabbychat;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiTextField;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;

public class GuiSettings extends net.minecraft.src.GuiScreen {
	private static TabbyChat tc;
	private final int margin = 10;
	private final int line_height = 16;
	public final int displayWidth = 325;
	public final int displayHeight = 170;
	private SimpleDateFormat timeFormat = new SimpleDateFormat();
	
	// Global Settings temp vars
	private boolean tmp_TCenabled;
	private int tmp_retainedChats;
	private int tmp_maxChannelNameLength;
	private boolean tmp_autoSearchEnabled;
	private boolean tmp_timestampsEnabled;
	private boolean tmp_saveLocalLogEnabled;
	// Server Settings temp vars
	private ChannelDelimEnum tmp_chanDelims;
	private String[] tmp_defaultChannels;
	private String[] tmp_ignoredChannels;
	private ChatColorEnum tmp_chanDelimColor;
	private ChatColorEnum tmp_chanDelimFormat;
	private TimeStampEnum tmp_timeStamp;
	
	// Global Settings Button IDs
	private static final int saveButton = 9120;
	private static final int cancelButton = 9121;
	private static final int filtersWindowButton = 9129;
	private static final int TCenabledButton = 9122;
	private static final int autoSearchEnabledButton = 9123;
	private static final int timestampsEnabledButton = 9124;
	private static final int saveLocalLogEnabledButton = 9125;
	// Server Settings Buttons IDs
	private static final int chanDelimsButton = 9126;
	private static final int defaultChannelsButton = 9127;
	private static final int ignoredChannelsButton = 9128;
	// ** Note: 9129 taken
	private static final int chanDelimStyleButton = 9130;
	private static final int timeStampButton = 9131;
	
	// Global Settings Button Objects
	private PrefsToggleButton TCenabledPrefs;
	private GuiTextField retainedChatPrefs;
	private GuiTextField maxChannelNameLengthPrefs;
	private PrefsToggleButton autoSearchEnabledPrefs;
	private PrefsToggleButton timestampsEnabledPrefs;
	private PrefsToggleButton saveLocalLogEnabledPrefs;
	// Server Settings Button Objects
	private PrefsButton chanDelimsPrefs;
	private GuiTextField defaultChannelsPrefs;
	private GuiTextField ignoredChannelsPrefs;
	private PrefsButton chanDelimStylePrefs;
	private PrefsButton timeStampPrefs;

	public GuiSettings() {
		this.mc = Minecraft.getMinecraft();
	}
	
	protected GuiSettings(TabbyChat _tc) {
		this();
		tc = _tc;
	}
	
	private String delimButtonText() {
		return this.tmp_chanDelimColor.getCode() + this.tmp_chanDelimFormat.getCode() + this.tmp_chanDelimColor.toString() + ChatColorEnum.RESET.getCode();
	}
	
	public void prepareTempVars() {
		// Initialize temp global vars
		this.tmp_TCenabled = tc.globalPrefs.TCenabled;
		this.tmp_retainedChats = tc.globalPrefs.retainedChats;
		this.tmp_maxChannelNameLength = tc.globalPrefs.maxChannelNameLength;
		this.tmp_autoSearchEnabled = tc.globalPrefs.autoSearchEnabled;
		this.tmp_timestampsEnabled = tc.globalPrefs.timestampsEnabled;
		this.tmp_saveLocalLogEnabled = tc.globalPrefs.saveLocalLogEnabled;
		this.tmp_timeStamp = tc.globalPrefs.timestampStyle;
		// Initialize temp server vars
		this.tmp_chanDelims = tc.serverPrefs.chanDelims;
		this.tmp_defaultChannels = tc.serverPrefs.defaultChans;
		this.tmp_ignoredChannels = tc.serverPrefs.ignoredChans;
		this.tmp_chanDelimColor = tc.serverPrefs.chanDelimColor;
		this.tmp_chanDelimFormat = tc.serverPrefs.chanDelimFormat;
	}
	
	protected void applySettings() {
		if (tc.globalPrefs.TCenabled != this.tmp_TCenabled) {
			if (this.tmp_TCenabled)
				tc.enable();
			else
				tc.disable();
			tc.globalPrefs.TCenabled = this.tmp_TCenabled;
		}
		this.applyTextFields();			
		
		tc.globalPrefs.retainedChats = this.tmp_retainedChats;
		tc.globalPrefs.maxChannelNameLength = this.tmp_maxChannelNameLength;
		tc.globalPrefs.autoSearchEnabled = this.tmp_autoSearchEnabled;
		tc.globalPrefs.timestampsEnabled = this.tmp_timestampsEnabled;
		tc.globalPrefs.saveLocalLogEnabled = this.tmp_saveLocalLogEnabled;
		tc.serverPrefs.chanDelims = this.tmp_chanDelims;
		tc.serverPrefs.defaultChans = this.tmp_defaultChannels;
		tc.serverPrefs.ignoredChans = this.tmp_ignoredChannels;
		tc.serverPrefs.chanDelimColor = this.tmp_chanDelimColor;
		tc.serverPrefs.chanDelimFormat = this.tmp_chanDelimFormat;
		tc.globalPrefs.timestampStyle = this.tmp_timeStamp;
		tc.globalPrefs.timeStamp.applyPattern(this.tmp_timeStamp.toString());
		tc.loadPatterns();
		tc.updateDefaults();
	}
	
	protected void mouseClicked(int par1, int par2, int par3) {
		if (Mouse.getEventButton() == 1) {
			if (par1 >= this.chanDelimStylePrefs.xPosition && par1 < this.chanDelimStylePrefs.xPosition + this.chanDelimStylePrefs.width() && par2 >= this.chanDelimStylePrefs.yPosition && par2 < this.chanDelimStylePrefs.yPosition + this.chanDelimStylePrefs.height()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
					this.tmp_chanDelimFormat = this.tmp_chanDelimFormat.prevFormat();
				else
					this.tmp_chanDelimColor = this.tmp_chanDelimColor.prevColor();
				this.chanDelimStylePrefs.title(delimButtonText());
			} else if (par1 >= this.timeStampPrefs.xPosition && par1 < this.timeStampPrefs.xPosition + this.timeStampPrefs.width() && par2 >= this.timeStampPrefs.yPosition && par2 < this.timeStampPrefs.yPosition + this.timeStampPrefs.height()) {
				this.tmp_timeStamp = this.tmp_timeStamp.prevFormat();
				this.timeFormat.applyPattern(this.tmp_timeStamp.toString());
				this.timeStampPrefs.displayString = this.timeFormat.format(tc.cal.getTime());
			}
		}
		this.retainedChatPrefs.mouseClicked(par1, par2, par3);
		this.maxChannelNameLengthPrefs.mouseClicked(par1, par2, par3);
		this.defaultChannelsPrefs.mouseClicked(par1, par2, par3);
		this.ignoredChannelsPrefs.mouseClicked(par1, par2, par3);
		super.mouseClicked(par1, par2, par3);
	}
	
	protected void keyTyped(char par1, int par2) {
		this.retainedChatPrefs.textboxKeyTyped(par1, par2);
		this.maxChannelNameLengthPrefs.textboxKeyTyped(par1, par2);
		this.defaultChannelsPrefs.textboxKeyTyped(par1, par2);
		this.ignoredChannelsPrefs.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}
	
	public void applyTextFields() {
		int tmpInt;
		try {
			tmpInt = Integer.parseInt(this.retainedChatPrefs.getText());
			if (tmpInt > 20) this.tmp_retainedChats = tmpInt;
		} catch (NumberFormatException e) {
			TabbyChat.printErr("Invalid value for retained chats found in global configuration file.");
		}
		try {
			tmpInt = Integer.parseInt(this.maxChannelNameLengthPrefs.getText());
			if (tmpInt > 0 && tmpInt < 100) this.tmp_maxChannelNameLength = tmpInt;	
		} catch (NumberFormatException e) {
			TabbyChat.printErr("Invalid value for max channel name length found in global configuration file.");
		}
		
		String tmpDefault = this.defaultChannelsPrefs.getText();
		String tmpIgnored = this.ignoredChannelsPrefs.getText();
		tmpDefault.replaceAll("(?<!,)[ ](?!,)", ", ");
		tmpIgnored.replaceAll("(?<!,)[ ](?!,)", ", ");
		
		Pattern splitPattern = Pattern.compile("[ ]?,[ ]?");
		this.tmp_defaultChannels = splitPattern.split(tmpDefault);
		this.tmp_ignoredChannels = splitPattern.split(tmpIgnored);
	}
	
	public void actionPerformed(GuiButton button) {
		PrefsButton myButton = (PrefsButton)button;
		switch (myButton.id) {
		case cancelButton:
			tc.filtersWindow.tmp_customFilters = CustomChatFilter.copyList(tc.serverPrefs.customFilters);
			this.mc.displayGuiScreen((GuiScreen)null);
			if (tc.globalPrefs.TCenabled)
				tc.resetDisplayedChat();
			break;
		case saveButton:		
			this.applySettings();
			tc.filtersWindow.applySettings();
			tc.globalPrefs.saveSettings();
			tc.serverPrefs.saveSettings();
			this.mc.displayGuiScreen((GuiScreen)null);
			if (tc.globalPrefs.TCenabled)
				tc.resetDisplayedChat();
			break;
		case filtersWindowButton:
			applyTextFields();
			this.mc.displayGuiScreen((GuiScreen)tc.filtersWindow);
			break;
		case TCenabledButton:
			this.tmp_TCenabled = !this.tmp_TCenabled;
			this.TCenabledPrefs.toggle();
			break;
		case autoSearchEnabledButton:
			this.tmp_autoSearchEnabled = !this.tmp_autoSearchEnabled;
			this.autoSearchEnabledPrefs.toggle();
			break;
		case timestampsEnabledButton:
			this.tmp_timestampsEnabled = !this.tmp_timestampsEnabled;
			this.timestampsEnabledPrefs.toggle();
			break;
		case saveLocalLogEnabledButton:
			this.tmp_saveLocalLogEnabled = !this.tmp_saveLocalLogEnabled;
			this.saveLocalLogEnabledPrefs.toggle();
			break;
		case chanDelimsButton:
			this.tmp_chanDelims = this.tmp_chanDelims.next();
			this.chanDelimsPrefs.title(this.tmp_chanDelims.getTitle());
			break;
		case chanDelimStyleButton:
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				this.tmp_chanDelimFormat = this.tmp_chanDelimFormat.nextFormat();
			} else {
				this.tmp_chanDelimColor = this.tmp_chanDelimColor.nextColor();
			}
			this.chanDelimStylePrefs.title(delimButtonText());
			break;
		case timeStampButton:
			this.tmp_timeStamp = this.tmp_timeStamp.nextFormat();
			this.timeFormat.applyPattern(this.tmp_timeStamp.toString());
			this.timeStampPrefs.displayString = this.timeFormat.format(tc.cal.getTime());
			break;
		}
	}
	
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		
		this.controlList.clear();

		int effWidth = this.displayWidth;
		int effHeight = this.displayHeight;
		int iMargin = (this.line_height - mc.fontRenderer.FONT_HEIGHT)/2;
		
		int col1x = this.width/2 - this.margin;
		int col2x = (this.width+effWidth)/2 - this.margin;
		int row1y = (this.height-effHeight)/2;
		int row2y = row1y + this.line_height + this.margin;
		int row3y = row2y + this.line_height + this.margin;
		int row4y = row3y + this.line_height + this.margin;
		int row5y = row4y + this.line_height + this.margin;
		int row6y = row5y + this.line_height + this.margin;
		int row7y = row6y + this.line_height + this.margin;		
		
		int bW = 40;
		int bH = this.line_height;
		PrefsButton savePrefs = new PrefsButton(saveButton, (this.width + effWidth)/2 - bW, (this.height + effHeight)/2 - bH, bW, bH, "Save");
		this.controlList.add(savePrefs);
		PrefsButton cancelPrefs = new PrefsButton(cancelButton, (this.width + effWidth)/2 - 2*bW - 2, (this.height + effHeight)/2 - bH, bW, bH, "Cancel");
		this.controlList.add(cancelPrefs);
		PrefsButton filterWindowPrefs = new PrefsButton(filtersWindowButton, (this.width + effWidth)/2 - 2*bW-54, (this.height + effHeight)/2 - bH, 50, bH, "Filters...", 0x6622ee22);
		this.controlList.add(filterWindowPrefs);
		
		bW = 25; // Smaller width for toggles
		
		this.TCenabledPrefs = new PrefsToggleButton(TCenabledButton, col1x - bW, row2y, bW, bH);
		this.TCenabledPrefs.updateTo(this.tmp_TCenabled);	
		this.controlList.add(this.TCenabledPrefs);
		
		this.autoSearchEnabledPrefs = new PrefsToggleButton(autoSearchEnabledButton, col1x - bW, row3y, bW, bH);
		this.autoSearchEnabledPrefs.updateTo(this.tmp_autoSearchEnabled);
		this.controlList.add(this.autoSearchEnabledPrefs);
		
		bW = 70;
		
		this.chanDelimStylePrefs = new PrefsButton(chanDelimStyleButton, col2x - bW, row3y, bW, bH, delimButtonText(), 0x66ffffff);
		this.chanDelimStylePrefs.hasControlCodes = true;
		this.controlList.add(this.chanDelimStylePrefs);
		
		bW = 35; // Bit larger for text entry
		
		this.retainedChatPrefs = new GuiTextField(this.mc.fontRenderer, col1x - bW, row4y, bW, bH);
		this.retainedChatPrefs.writeText(Integer.toString(this.tmp_retainedChats));
		this.retainedChatPrefs.setMaxStringLength(4);
		
		this.maxChannelNameLengthPrefs = new GuiTextField(this.mc.fontRenderer, col1x - bW, row5y, bW, bH);
		this.maxChannelNameLengthPrefs.writeText(Integer.toString(this.tmp_maxChannelNameLength));
		this.maxChannelNameLengthPrefs.setMaxStringLength(2);
		
		bW = 50;
		this.timeFormat.applyPattern(this.tmp_timeStamp.toString());
		this.timeStampPrefs = new PrefsButton(timeStampButton, col1x - bW, row6y, bW, bH, this.timeFormat.format(tc.cal.getTime()),  0x66ffffff);
		this.controlList.add(this.timeStampPrefs);
		
		bW = 25; // Back to toggle width
		
		this.timestampsEnabledPrefs = new PrefsToggleButton(timestampsEnabledButton, col1x - bW - 52, row6y, bW, bH);
		this.timestampsEnabledPrefs.updateTo(this.tmp_timestampsEnabled);
		this.controlList.add(this.timestampsEnabledPrefs);
		
		this.saveLocalLogEnabledPrefs = new PrefsToggleButton(saveLocalLogEnabledButton, col1x - bW, row7y, bW, bH);
		this.saveLocalLogEnabledPrefs.updateTo(this.tmp_saveLocalLogEnabled);
		this.controlList.add(this.saveLocalLogEnabledPrefs);
		
		bW = 70; // Wider for Delimiter Names
		
		this.chanDelimsPrefs = new PrefsButton(chanDelimsButton, col2x - bW, row2y, bW, bH, this.tmp_chanDelims.getTitle(), 0x99b6b765);
		this.controlList.add(this.chanDelimsPrefs);
		
		this.defaultChannelsPrefs = new GuiTextField(this.mc.fontRenderer, this.width/2 + this.margin, row4y+iMargin+6, effWidth/2 - 2*this.margin, mc.fontRenderer.FONT_HEIGHT);
		this.defaultChannelsPrefs.setMaxStringLength(150);
		this.defaultChannelsPrefs.setText(TabbyChatUtils.join(tmp_defaultChannels, ", "));
		
		ignoredChannelsPrefs = new GuiTextField(this.mc.fontRenderer, this.width/2 + this.margin, row5y+iMargin+6, effWidth/2 - 2*this.margin, mc.fontRenderer.FONT_HEIGHT);
		ignoredChannelsPrefs.setMaxStringLength(200);
		ignoredChannelsPrefs.setText(TabbyChatUtils.join(this.tmp_ignoredChannels, ", "));
	}	
	
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	public void drawScreen(int x, int y, float f) {
		int effWidth = this.displayWidth;
		int effHeight = this.displayHeight;
		int iMargin = (this.line_height - mc.fontRenderer.FONT_HEIGHT)/2;
		
		int col1x = (this.width-effWidth)/2;
		int col2x = this.width/2 + this.margin;
		int row1y = (this.height-effHeight)/2 + 4;
		int row2y = row1y + this.line_height + this.margin;
		int row3y = row2y + this.line_height + this.margin;
		int row4y = row3y + this.line_height + this.margin;
		int row5y = row4y + this.line_height + this.margin;
		int row6y = row5y + this.line_height + this.margin;
		int row7y = row6y + this.line_height + this.margin;
		
		this.drawString(this.mc.fontRenderer, tc.serverPrefs.ip, col1x, (this.height-effHeight)/2-this.margin-this.line_height+iMargin, 0xffffa0);
		drawRect(this.width/2 - effWidth/2 - this.margin, this.height/2 - effHeight/2 - this.margin, this.width/2 + effWidth/2 + this.margin, this.height/2 + effHeight/2 + this.margin, 0x88000000);
		drawRect(this.width/2, (this.height - effHeight)/2, this.width/2+1, (this.height+effHeight)/2, 0x66ffffff);
		
		drawRect(col1x - this.margin, row1y-4, this.width/2 - this.margin, row1y-4+this.line_height, 0x66a5e7e4);
		this.drawString(this.mc.fontRenderer, "GLOBAL SETTINGS", col1x, row1y, 0xffffff);
		drawRect(this.width/2, row1y-4, (this.width+effWidth)/2, row1y-4+this.line_height, 0x99b6b765);
		this.drawString(this.mc.fontRenderer, "SERVER SETTINGS", col2x, row1y, 0xffffff);
		
		this.drawString(this.mc.fontRenderer, "TabbyChat Enabled?", col1x, row2y, 0xffffff);
		this.drawString(this.mc.fontRenderer, "Chat-Channel", col2x, row2y-iMargin-1, 0xffffff);
		this.drawString(this.mc.fontRenderer, "delimiters", col2x, row2y+iMargin+1,0xffffff);
		
		this.drawString(this.mc.fontRenderer, "Auto search for", col1x, row3y-iMargin-1, 0xffffff);
		this.drawString(this.mc.fontRenderer, "new channels", col1x, row3y+iMargin+1, 0xffffff);
		
		this.drawString(this.mc.fontRenderer, "Delimiter", col2x, row3y-iMargin-1, 0xffffff);
		this.drawString(this.mc.fontRenderer, "formatting", col2x, row3y+iMargin+1, 0xffffff);
		
		this.drawString(this.mc.fontRenderer, "Chat scroll history", col1x, row4y, 0xffffff);
		this.retainedChatPrefs.drawTextBox();

		this.drawString(this.mc.fontRenderer, "Default Channels", col2x, row4y-iMargin-2, 0xffffff);
		this.defaultChannelsPrefs.drawTextBox();
		
		this.drawString(this.mc.fontRenderer, "Max length of", col1x, row5y - iMargin - 1, 0xffffff);
		this.drawString(this.mc.fontRenderer, "channel name search", col1x, row5y + iMargin + 1, 0xffffff);
		this.maxChannelNameLengthPrefs.drawTextBox();
		
		this.drawString(this.mc.fontRenderer, "Ignored Channels", col2x, row5y-iMargin-2, 0xffffff);
		this.ignoredChannelsPrefs.drawTextBox();
		
		this.drawString(this.mc.fontRenderer, "Timestamps", col1x, row6y, 0xffffff);
		
		this.drawString(this.mc.fontRenderer, "Save chat to local log", col1x, row7y, 0xffffff);
		
		super.drawScreen(x, y, f);
	}
	
	
}
