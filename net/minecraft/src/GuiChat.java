package net.minecraft.src;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import acs.tabbychat.ChatChannel;
import acs.tabbychat.GuiChatTC;
import acs.tabbychat.TabbyChat;
import acs.tabbychat.TabbyChatUtils;
import acs.tabbychat.ChatScrollBar;
import acs.tabbychat.ChatButton;

public class GuiChat extends GuiScreen {

	protected GuiTextField inputField;
	protected GuiChatTC wrapper;
	public String defaultInputFieldText = "";
	
	public GuiChat() { }

	public GuiChat(String par1Str) {
		this.defaultInputFieldText = par1Str;
	}

	public void initGui() {
		this.wrapper = new GuiChatTC();
		this.wrapper.defaultInputFieldText = this.defaultInputFieldText;
		this.wrapper.initGui();
		this.inputField = this.wrapper.inputField;
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		this.mc.ingameGUI.getChatGUI().resetScroll();
	}

	public void updateScreen() {
		this.wrapper.updateScreen();
	}

	protected void keyTyped(char par1, int par2) {
		this.wrapper.keyTyped(par1, par2);
	}

	public void handleMouseInput() {
		if(this.wrapper != null) this.wrapper.handleMouseInput();
		super.handleMouseInput();
	}

	protected void mouseClicked(int par1, int par2, int par3) {
		this.wrapper.mouseClicked(par1, par2, par3);
	}

	public void confirmClicked(boolean par1, int par2) {
		this.wrapper.confirmClicked(par1, par2);
	}

	private void func_73896_a(URI par1URI) {
		this.wrapper.func_73896_a(par1URI);
	}

	public void completePlayerName() {
		this.wrapper.completePlayerName();
	}

	private void func_73893_a(String par1Str, String par2Str) {
		this.wrapper.func_73893_a(par1Str, par2Str);
	}

	public void getSentHistory(int par1) {
		this.wrapper.getSentHistory(par1);
	}

	public void drawScreen(int par1, int par2, float par3) {
		this.wrapper.drawScreen(par1, par2, par3);
	}

	public void func_73894_a(String[] par1ArrayOfStr) {
		this.wrapper.func_73894_a(par1ArrayOfStr);
	}

	public boolean doesGuiPauseGame() {
		return false;
	}
}
