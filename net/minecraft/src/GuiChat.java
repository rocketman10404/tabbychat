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
	
	public GuiChat() { }

	public GuiChat(String par1Str) { }

	public void initGui() {
		GuiChatTC.me.initGui();
		this.inputField = GuiChatTC.me.inputField;
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		this.mc.ingameGUI.getChatGUI().resetScroll();
	}

	public void updateScreen() {
		GuiChatTC.me.updateScreen();
	}

	protected void keyTyped(char par1, int par2) {
		GuiChatTC.me.keyTyped(par1, par2);
	}

	public void handleMouseInput() {
		super.handleMouseInput();
		GuiChatTC.me.handleMouseInput();  // this will cause issues in Forge
	}

	protected void mouseClicked(int par1, int par2, int par3) {
		GuiChatTC.me.mouseClicked(par1, par2, par3);
	}

	public void confirmClicked(boolean par1, int par2) {
		GuiChatTC.me.confirmClicked(par1, par2);
	}

	private void func_73896_a(URI par1URI) {
		GuiChatTC.me.func_73896_a(par1URI);
	}

	public void completePlayerName() {
		GuiChatTC.me.completePlayerName();
	}

	private void func_73893_a(String par1Str, String par2Str) {
		GuiChatTC.me.func_73893_a(par1Str, par2Str);
	}

	public void getSentHistory(int par1) {
		GuiChatTC.me.getSentHistory(par1);
	}

	public void drawScreen(int par1, int par2, float par3) {
		GuiChatTC.me.drawScreen(par1, par2, par3);
	}

	public void func_73894_a(String[] par1ArrayOfStr) {
		GuiChatTC.me.func_73894_a(par1ArrayOfStr);
	}

	public boolean doesGuiPauseGame() {
		return false;
	}
}
