package acs.tabbychat;

public class TCSettingsGeneral extends TCSettingsGUI {

	private static final int tabbyChatEnableID = 9101;
	private static final int saveChatLogID = 9102;
	private static final int timeStampEnableID = 9103;
	private static final int timeStampStyleID = 9104;
	private static final int groupSpamID = 9105;
	private static final int unreadFlashingID = 9106;
	
	protected TCSettingBool tabbyChatEnable = new TCSettingBool("TabbyChat Enabled", tabbyChatEnableID);
	protected TCSettingBool saveChatLog = new TCSettingBool("Log chat to file", saveChatLogID);
	protected TCSettingBool timeStampEnable = new TCSettingBool("Timestamp chat", timeStampEnableID);
	protected TCSettingEnum timeStampStyle = new TCSettingEnum(TimeStampEnum.MILITARY, "\u00A7oTimestamp Style\u00A7r", timeStampStyleID);
	protected TCSettingBool groupSpam = new TCSettingBool("Consolidate spammed chat", groupSpamID);
	protected TCSettingBool unreadFlashing = new TCSettingBool("Unread notification flashing", unreadFlashingID);
	
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
	}
	
}
