package acs.tabbychat.gui;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import acs.tabbychat.core.ChatChannel;
import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.lang.TCTranslate;
import acs.tabbychat.settings.ITCSetting;
import acs.tabbychat.settings.TCSettingBool;
import acs.tabbychat.settings.TCSettingTextBox;
import acs.tabbychat.util.TabbyChatUtils;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

public class ChatChannelGUI extends GuiScreen {
	protected ChatChannel channel;
	public final int displayWidth = 255;
	public final int displayHeight = 120;
	private String title;
	private int position;
	private TabbyChat tc;
	
	private static final int SAVE_ID = 8981;
	private static final int CANCEL_ID = 8982;
	private static final int NOTIFICATIONS_ON_ID = 8983;
	private static final int ALIAS_ID = 8984;
	private static final int CMD_PREFIX_ID = 8985;
	private static final int PREV_ID = 8986;
	private static final int NEXT_ID = 8987;
	private static final int HIDE_PREFIX = 8988;
	
	private TCSettingBool hidePrefix = new TCSettingBool(false, "hidePrefix", "settings.channel", HIDE_PREFIX);
	private TCSettingBool notificationsOn = new TCSettingBool(false, "notificationsOn", "settings.channel", NOTIFICATIONS_ON_ID);
	private TCSettingTextBox alias = new TCSettingTextBox("", "alias", "settings.channel", ALIAS_ID);
	private TCSettingTextBox cmdPrefix = new TCSettingTextBox("", "cmdPrefix", "settings.channel", CMD_PREFIX_ID);
	
	public ChatChannelGUI(ChatChannel _c) {
		this.tc = GuiNewChatTC.getInstance().tc;
		this.channel = _c;
		this.hidePrefix.setValue(_c.hidePrefix);
		this.notificationsOn.setValue(_c.notificationsOn);
		this.alias.setCharLimit(20);
		this.alias.setValue(_c.getAlias());
		this.cmdPrefix.setCharLimit(100);
		this.cmdPrefix.setValue(_c.cmdPrefix);
		this.resetTempVars();
		this.title = _c.getTitle();
	}
	
	public void actionPerformed(GuiButton _button) {
		switch(_button.id) {
		case SAVE_ID:
			this.channel.notificationsOn = this.notificationsOn.getTempValue();
			this.channel.setAlias(this.alias.getTempValue().trim());
			this.channel.cmdPrefix = this.cmdPrefix.getTempValue().trim();
			this.channel.hidePrefix = this.hidePrefix.getTempValue();
			this.tc.storeChannelData();
		case CANCEL_ID:
			mc.displayGuiScreen((GuiScreen)null);
			break;
		case NOTIFICATIONS_ON_ID:
			this.notificationsOn.actionPerformed();
			break;
		case PREV_ID:
			if(this.position<=2) return;
			LinkedHashMap<String, ChatChannel> newMap = TabbyChatUtils.swapChannels(this.tc.channelMap, this.position-2, this.position-1);
			this.tc.channelMap.clear();
			this.tc.channelMap = newMap;
			this.position--;
			break;
		case NEXT_ID:
			if(this.position>=this.tc.channelMap.size()) return;
			LinkedHashMap<String, ChatChannel> newMap2 = TabbyChatUtils.swapChannels(this.tc.channelMap, this.position-1, this.position); 
			this.tc.channelMap.clear();
			this.tc.channelMap = newMap2;
			this.position++;
			break;
		case HIDE_PREFIX:
			this.hidePrefix.actionPerformed();
		}
	}
	
	public void drawScreen(int _x, int _y, float _f) {
		int leftX = (this.width - this.displayWidth)/2;
		int topY = (this.height - this.displayHeight)/2;
		int rightX = leftX + this.displayWidth;
		int botY = topY + this.displayHeight;
		if(TabbyChat.generalSettings.tabbyChatEnable.getValue() && TabbyChat.advancedSettings.forceUnicode.getValue()) mc.fontRenderer.setUnicodeFlag(true);
		
		// Draw main background and title
		drawRect(leftX, topY, leftX + this.displayWidth, topY + this.displayHeight, 0x88000000);
		drawRect(leftX, topY + 14, leftX + this.displayWidth, topY + 15, 0x88ffffff);
		this.drawString(mc.fontRenderer, this.title, leftX + 3, topY + 3, 0xaaaaaa);
		
		// Draw tab position info
		this.drawString(mc.fontRenderer, Integer.toString(this.position), rightX-34, topY+22, 0xffffff);
		this.drawString(mc.fontRenderer, TabbyChat.translator.getString("settings.channel.position"), rightX-55-mc.fontRenderer.getStringWidth(TabbyChat.translator.getString("settings.channel.position")), topY+22, 0xffffff);
		this.drawString(mc.fontRenderer, TabbyChat.translator.getString("settings.channel.of")+" "+this.tc.channelMap.size(), rightX-34, topY+35, 0xffffff);
		
		// Draw buttons
		for (int i = 0; i < this.buttonList.size(); i++) {
			((GuiButton)this.buttonList.get(i)).drawButton(mc, _x, _y);
		}
		mc.fontRenderer.setUnicodeFlag(TabbyChat.defaultUnicode);
	}
	
	public void initGui() {
		int leftX = (this.width - this.displayWidth)/2;
		int topY = (this.height - this.displayHeight)/2;
		int rightX = leftX + this.displayWidth;
		int botY = topY + this.displayHeight;
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		
		// Define generic buttons
		PrefsButton savePrefs = new PrefsButton(SAVE_ID, rightX - 45, botY - 19, 40, 14, TabbyChat.translator.getString("settings.save"));
		this.buttonList.add(savePrefs);
		PrefsButton cancelPrefs = new PrefsButton(CANCEL_ID, rightX - 90, botY - 19, 40, 14, TabbyChat.translator.getString("settings.cancel"));
		this.buttonList.add(cancelPrefs);
		PrefsButton nextButton = new PrefsButton(NEXT_ID, rightX - 20, topY+20, 15, 14, ">>");
		this.buttonList.add(nextButton);
		PrefsButton prevButton = new PrefsButton(PREV_ID, rightX - 50, topY+20, 15, 14, "<<");
		this.buttonList.add(prevButton);
		
		// Define settings buttons
		this.alias.setLabelLoc(leftX + 15);
		this.alias.setButtonLoc(leftX+20+mc.fontRenderer.getStringWidth(this.alias.description), topY+20);
		this.alias.setButtonDims(70, 11);
		this.buttonList.add(this.alias);
		
		this.notificationsOn.setButtonLoc(leftX+15, topY+40);
		this.notificationsOn.setLabelLoc(leftX+34);
		this.buttonList.add(this.notificationsOn);
		
		this.cmdPrefix.setLabelLoc(leftX+15);
		this.cmdPrefix.setButtonLoc(leftX+20+mc.fontRenderer.getStringWidth(this.cmdPrefix.description), topY+57);
		this.cmdPrefix.setButtonDims(100, 11);
		this.buttonList.add(this.cmdPrefix);
		
		this.hidePrefix.setButtonLoc(leftX+15, topY+78);
		this.hidePrefix.setLabelLoc(leftX+34);
		this.buttonList.add(this.hidePrefix);
		
		
		// Determine tab position
		position = 1;
		int numTabs = this.tc.channelMap.size();
		Iterator _chanPtr = this.tc.channelMap.keySet().iterator();
		while(_chanPtr.hasNext()) {
			if(this.channel.getTitle().equals(_chanPtr.next())) break;
			position++;
		}
		if(!TabbyChat.translator.getCurrentLang().equals(mc.gameSettings.language)) {
			TabbyChat.translator = new TCTranslate(mc.gameSettings.language);
		}
		for(Object drawable : this.buttonList) {
			if(drawable instanceof ITCSetting) ((ITCSetting)drawable).resetDescription();
		}
	}
	
	protected void keyTyped(char par1, int par2) {
		for (int i = 0; i < this.buttonList.size(); i++) {
			if (ITCSetting.class.isInstance(this.buttonList.get(i))) {
				ITCSetting tmp = (ITCSetting)this.buttonList.get(i);
				if (tmp.getType() == "textbox") {
					((TCSettingTextBox)tmp).keyTyped(par1, par2);
				}
			}
		}
		super.keyTyped(par1, par2);
	}
	
	public void mouseClicked(int par1, int par2, int par3) {
		for (int i = 0; i < this.buttonList.size(); i++) {
			if (ITCSetting.class.isInstance(this.buttonList.get(i))) {
				ITCSetting tmp = (ITCSetting)this.buttonList.get(i);
				if (tmp.getType() == "textbox") {
					tmp.mouseClicked(par1, par2, par3);
				}
			}
		}
		super.mouseClicked(par1, par2, par3);
	}
	
	public void resetTempVars() {
		this.hidePrefix.reset();
		this.notificationsOn.reset();
		this.alias.reset();
		this.cmdPrefix.reset();
	}
}
