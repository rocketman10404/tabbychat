package acs.tabbychat.gui;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import acs.tabbychat.core.ChatChannel;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.TCSetting;
import acs.tabbychat.settings.TCSettingBool;
import acs.tabbychat.settings.TCSettingTextBox;
import acs.tabbychat.util.TabbyChatUtils;

import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;

public class ChatChannelGUI extends GuiScreen {
	protected ChatChannel channel;
	public final int displayWidth = 255;
	public final int displayHeight = 95;
	private String title;
	private int position;
	
	private static final int saveButton = 8981;
	private static final int cancelButton = 8982;
	private static final int notificationsOnID = 8983;
	private static final int aliasID = 8984;
	private static final int cmdPrefixID = 8985;
	private static final int prevButtonID = 8986;
	private static final int nextButtonID = 8987;
	
	private TCSettingBool notificationsOn = new TCSettingBool(false, "Unread Notifications", notificationsOnID);
	private TCSettingTextBox alias = new TCSettingTextBox("Alias", aliasID);
	private TCSettingTextBox cmdPrefix = new TCSettingTextBox("Chat command prefix", cmdPrefixID);
	
	public ChatChannelGUI(ChatChannel _c) {
		this.channel = _c;
		this.notificationsOn.setValue(_c.notificationsOn);
		this.alias.setCharLimit(20);
		this.alias.setValue(_c.alias);
		this.cmdPrefix.setCharLimit(100);
		this.cmdPrefix.setValue(_c.cmdPrefix);
		this.resetTempVars();
		this.title = _c.title;
	}
	
	public void actionPerformed(GuiButton _button) {
		switch(_button.id) {
		case saveButton:
			this.channel.notificationsOn = this.notificationsOn.getTempValue();
			this.channel.alias = this.alias.getTempValue().trim();
			this.channel.cmdPrefix = this.cmdPrefix.getTempValue().trim();
			TabbyChat.instance.storeChannelData();
		case cancelButton:
			mc.displayGuiScreen((GuiScreen)null);
			break;
		case notificationsOnID:
			this.notificationsOn.actionPerformed();
			break;
		case prevButtonID:
			if(this.position<=2) return;
			LinkedHashMap<String, ChatChannel> newMap = TabbyChatUtils.swapChannels(TabbyChat.instance.channelMap, this.position-2, this.position-1);
			TabbyChat.instance.channelMap.clear();
			TabbyChat.instance.channelMap = newMap;
			this.position--;
			break;
		case nextButtonID:
			if(this.position>=TabbyChat.instance.channelMap.size()) return;
			LinkedHashMap<String, ChatChannel> newMap2 = TabbyChatUtils.swapChannels(TabbyChat.instance.channelMap, this.position-1, this.position); 
			TabbyChat.instance.channelMap.clear();
			TabbyChat.instance.channelMap = newMap2;
			this.position++;
			break;
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
		this.drawString(mc.fontRenderer, "Position:", rightX-55-mc.fontRenderer.getStringWidth("Position:"), topY+22, 0xffffff);
		this.drawString(mc.fontRenderer, "of "+TabbyChat.instance.channelMap.size(), rightX-34, topY+35, 0xffffff);
		
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
		PrefsButton savePrefs = new PrefsButton(saveButton, rightX - 45, botY - 19, 40, 14, "Save");
		this.buttonList.add(savePrefs);
		PrefsButton cancelPrefs = new PrefsButton(cancelButton, rightX - 90, botY - 19, 40, 14, "Cancel");
		this.buttonList.add(cancelPrefs);
		PrefsButton nextButton = new PrefsButton(nextButtonID, rightX - 20, topY+20, 15, 14, ">>");
		this.buttonList.add(nextButton);
		PrefsButton prevButton = new PrefsButton(prevButtonID, rightX - 50, topY+20, 15, 14, "<<");
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
		
		// Determine tab position
		position = 1;
		int numTabs = TabbyChat.instance.channelMap.size();
		Iterator _chanPtr = TabbyChat.instance.channelMap.keySet().iterator();
		while(_chanPtr.hasNext()) {
			if(this.channel.title.equals(_chanPtr.next())) break;
			position++;
		}		
	}
	
	protected void keyTyped(char par1, int par2) {
		for (int i = 0; i < this.buttonList.size(); i++) {
			if (TCSetting.class.isInstance(this.buttonList.get(i))) {
				TCSetting tmp = (TCSetting)this.buttonList.get(i);
				if (tmp.type == "textbox") {
					((TCSettingTextBox)tmp).keyTyped(par1, par2);
				}
			}
		}
		super.keyTyped(par1, par2);
	}
	
	public void mouseClicked(int par1, int par2, int par3) {
		for (int i = 0; i < this.buttonList.size(); i++) {
			if (TCSetting.class.isInstance(this.buttonList.get(i))) {
				TCSetting tmp = (TCSetting)this.buttonList.get(i);
				if (tmp.type == "textbox") {
					tmp.mouseClicked(par1, par2, par3);
				}
			}
		}
		super.mouseClicked(par1, par2, par3);
	}
	
	public void resetTempVars() {
		this.notificationsOn.reset();
		this.alias.reset();
		this.cmdPrefix.reset();
	}
}
