package acs.tabbychat.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiButton;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.TCSetting;
import acs.tabbychat.settings.TCSettingTextBox;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TCSettingsGUI extends net.minecraft.src.GuiScreen {
	protected static TabbyChat tc;
	protected static Minecraft mc;
	protected final int margin = 4;
	protected final int line_height = 14;
	public final int displayWidth = 325;
	public final int displayHeight = 180;
	protected int lastOpened = 0;
	protected String name = "";
	protected int bgcolor = 0x66a5e7e4;
	protected int id = 9000;
	protected static List<TCSettingsGUI> ScreenList = new ArrayList<TCSettingsGUI>();
	public static File tabbyChatDir = new File(Minecraft.getMinecraftDir(), new StringBuilder().append("config").append(File.separatorChar).append("tabbychat").toString());
	protected File settingsFile;
	
	private static final int saveButton = 8901;
	private static final int cancelButton = 8902;
	
	public TCSettingsGUI() {
		mc = Minecraft.getMinecraft();
		ScreenList.add(this);
	}
	
	public TCSettingsGUI(TabbyChat _tc) {
		this();
		tc = _tc;
	}
	
	public void actionPerformed(GuiButton button) {
		if (TCSetting.class.isInstance(button) && ((TCSetting)button).type != "textbox") {
			((TCSetting)button).actionPerformed();
		} else if (button.id == saveButton) {
			for (TCSettingsGUI screen : ScreenList) {
				screen.storeTempVars();
				screen.saveSettingsFile();
			}
			tc.reloadSettingsData(true);
			mc.displayGuiScreen((GuiScreen)null);
			if (TabbyChat.generalSettings.tabbyChatEnable.getValue())
				tc.resetDisplayedChat();
		} else if (button.id == cancelButton) {
			for (TCSettingsGUI screen : ScreenList)
				screen.resetTempVars();
			mc.displayGuiScreen((GuiScreen)null);
			if (TabbyChat.generalSettings.tabbyChatEnable.getValue())
				tc.resetDisplayedChat();
		} else {
			for (int i = 0; i < ScreenList.size(); i++) {
				if (button.id == ScreenList.get(i).id) {
					mc.displayGuiScreen((GuiScreen)ScreenList.get(i));
				}
			}
		}
		this.validateButtonStates();
	}
	
	public void drawScreen(int x, int y, float f) {
		if(TabbyChat.generalSettings.tabbyChatEnable.getValue() && tc.advancedSettings.forceUnicode.getValue()) mc.fontRenderer.setUnicodeFlag(true);
		int iMargin = (this.line_height - mc.fontRenderer.FONT_HEIGHT)/2;
		int effLeft = (this.width - this.displayWidth)/2;
		int absLeft = effLeft - this.margin;
		int effTop = (this.height - this.displayHeight)/2;
		int absTop = effTop - this.margin;
		
		drawRect(absLeft, absTop, absLeft + this.displayWidth + 2*this.margin, absTop + this.displayHeight + 2*this.margin, 0x88000000);
		
		for (int i = 0; i < ScreenList.size(); i++) {
			if (ScreenList.get(i) == this) {
				int delta = mc.ingameGUI.getUpdateCounter() - this.lastOpened;
				int curWidth;
				int tabDist = mc.fontRenderer.getStringWidth(ScreenList.get(i).name) + 2*this.margin - 40;
				if (delta <= 5)
					curWidth = 45 + (delta * tabDist) / 5;
				else
					curWidth = tabDist + 45;
				drawRect(absLeft, effTop + 30*i, absLeft + curWidth, effTop + 30*i + 20, ScreenList.get(i).bgcolor);
				drawRect(absLeft + 45, absTop, absLeft + 46, effTop + 30*i, 0x66ffffff);
				drawRect(absLeft + 45, effTop + 30*i + 20, absLeft + 46, absTop + this.displayHeight, 0x66ffffff);
				this.drawString(mc.fontRenderer, mc.fontRenderer.trimStringToWidth(ScreenList.get(i).name, curWidth), effLeft, effTop + 6 + 30 * i, 0xffffff);
			} else {
				drawRect(absLeft, effTop + 30*i, absLeft + 45, effTop + 30*i + 20, ScreenList.get(i).bgcolor);
			}
		}
		for (int i = 0; i < this.buttonList.size(); i++) {
			((GuiButton)this.buttonList.get(i)).drawButton(mc, x, y);
		}
		mc.fontRenderer.setUnicodeFlag(TabbyChat.defaultUnicode);
	}
	
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		
		int effLeft = (this.width - this.displayWidth)/2;
		int absLeft = effLeft - this.margin;
		int effTop = (this.height - this.displayHeight)/2;
		int absTop = effTop - this.margin;
		
		this.lastOpened = mc.ingameGUI.getUpdateCounter();
		int effRight = (this.width + this.displayWidth)/2;
		int bW = 40;
		int bH = this.line_height;
		PrefsButton savePrefs = new PrefsButton(saveButton, effRight - bW, (this.height + this.displayHeight)/2 - bH, bW, bH, "Save");
		this.buttonList.add(savePrefs);
		PrefsButton cancelPrefs = new PrefsButton(cancelButton, effRight - 2*bW - 2, (this.height + this.displayHeight)/2 - bH, bW, bH, "Cancel");
		this.buttonList.add(cancelPrefs);
		
		for (int i = 0; i < ScreenList.size(); i++) {
			ScreenList.get(i).id = 9000+i;
			if (ScreenList.get(i) != this) {
				this.buttonList.add(new PrefsButton(ScreenList.get(i).id, effLeft, effTop + 30*i, 45, 20, mc.fontRenderer.trimStringToWidth(ScreenList.get(i).name,35)+"..."));
				((PrefsButton)this.buttonList.get(this.buttonList.size()-1)).bgcolor = 0x00000000;
			}
		}
	}
	
	public void keyTyped(char par1, int par2) {
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
	
	public void loadSettingsFile() { }
	
	public void mouseClicked(int par1, int par2, int par3) {
		for (int i = 0; i < this.buttonList.size(); i++) {
			if (TCSetting.class.isInstance(this.buttonList.get(i))) {
				TCSetting tmp = (TCSetting)this.buttonList.get(i);
				if (tmp.type == "textbox" || tmp.type == "enum" || tmp.type == "slider") {
					tmp.mouseClicked(par1, par2, par3);
				}
			}
		}
		super.mouseClicked(par1, par2, par3);
	}
	
	protected void resetTempVars() {}
	
	protected int rowY(int rowNum) {
		return (this.height - this.displayHeight)/2 + (rowNum - 1) * (this.line_height + this.margin);
	}
	
	protected void saveSettingsFile() { }
	
	protected void storeTempVars() {}
	
	public void validateButtonStates() { }
}
