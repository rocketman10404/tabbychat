package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import acs.tabbychat.core.ChatChannel;
import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.TimeStampEnum;

public class GuiNewChat extends Gui {
	private final Minecraft mc;
    private final List sentMessages = new ArrayList();
    private final List chatLines = new ArrayList();
    private final List field_96134_d = new ArrayList();

	public GuiNewChat(Minecraft par1Minecraft) {
		this.mc = par1Minecraft;
	}

	public void drawChat(int par1) {
		GuiNewChatTC.getInstance().drawChat(par1);
	}

	public void clearChatMessages() {
		GuiNewChatTC.getInstance().clearChatMessages();
	}

	public void printChatMessage(String par1Str) {
		this.printChatMessageWithOptionalDeletion(par1Str, 0);
	}

	public void printChatMessageWithOptionalDeletion(String par1Str, int par2) {
		this.func_96129_a(par1Str, par2, this.mc.ingameGUI.getUpdateCounter(), false);
		this.mc.getLogAgent().logInfo("[CHAT] " + par1Str);
	}

	public void func_96129_a(String par1Str, int par2, int par3, boolean par4) {
		GuiNewChatTC.getInstance().func_96129_a(par1Str, par2, par3, par4);
	}

   public void func_96132_b() {
	   GuiNewChatTC.getInstance().func_96132_b();
   }
   
   public List getSentMessages() {
	   return this.sentMessages;
   }

   public void addToSentMessages(String par1Str) {
	   if(this.sentMessages.isEmpty() || !((String)this.sentMessages.get(this.sentMessages.size() - 1)).equals(par1Str)) {
		   this.sentMessages.add(par1Str);
	   }
   }

   public void resetScroll() {
	   GuiNewChatTC.getInstance().resetScroll();
   }

   public void scroll(int par1) {
	   GuiNewChatTC.getInstance().scroll(par1);
   }

   public ChatClickData func_73766_a(int par1, int par2) {
	   return GuiNewChatTC.getInstance().func_73766_a(par1, par2);
   }

   public void addTranslatedMessage(String par1Str, Object ... par2ArrayOfObj) {
	   this.printChatMessage(StringTranslate.getInstance().translateKeyFormat(par1Str, par2ArrayOfObj));
   }

   public boolean getChatOpen() {
	   return this.mc.currentScreen instanceof GuiChat;
   }

   public void deleteChatLine(int par1) {
	   GuiNewChatTC.getInstance().deleteChatLine(par1);
   }
   
   public int func_96126_f() {
	   return func_96128_a(this.mc.gameSettings.chatWidth);
   }

   public int func_96133_g() {
	   return func_96130_b(this.getChatOpen()?this.mc.gameSettings.chatHeightFocused:this.mc.gameSettings.chatHeightUnfocused);
   }

   public float func_96131_h() {
	   return this.mc.gameSettings.chatScale;
   }

   public static final int func_96128_a(float par0) {
	   short var1 = 320;
	   byte var2 = 40;
	   return MathHelper.floor_float(par0 * (float)(var1 - var2) + (float)var2);
   }

   public static final int func_96130_b(float par0) {
	   short var1 = 180;
	   byte var2 = 20;
	   return MathHelper.floor_float(par0 * (float)(var1 - var2) + (float)var2);
   }

   public int func_96127_i() {
	   return this.func_96133_g() / 9;
   }
}