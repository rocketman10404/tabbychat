package acs.tabbychat.compat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.lwjgl.opengl.GL11;

import acs.tabbychat.core.GuiChatTC;
import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.gui.ChatButton;

import net.minecraft.src.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;

public class EmoticonsCompat {
	public static Object emoteObject = null;
	public static Class emoteButtonClass = null;
	private static Constructor emoteConstructor = null;
	private static Method emoteActionPerformed = null;
	private static Method emoteInitGui = null;
	private static Method emoteDrawScreen = null;
	public static int emoteOffsetX = 0;
	public static boolean present = true;
	
	public static void load() {
		if(present) {
			if(emoteConstructor == null || emoteActionPerformed == null || emoteInitGui == null || emoteDrawScreen == null || emoteButtonClass == null) {
				try {
					// Load new Emoticons object
					Class EmoticonsClass = Class.forName("mudbill.Emoticons");
					emoteButtonClass = Class.forName("mudbill.GuiSimpleButton");
					emoteConstructor = EmoticonsClass.getConstructor((Class[])null);
					emoteObject = emoteConstructor.newInstance((Object[])null);
					// Assign Emoticons actionPerformed Method
					Class[] cArgsAP = new Class[3];
					cArgsAP[0] = GuiButton.class;
					cArgsAP[1] = List.class;
					cArgsAP[2] = GuiTextField.class;
					emoteActionPerformed = EmoticonsClass.getDeclaredMethod("actionPerformed", cArgsAP);
					// Assign Emoticons initGui Method;
					Class[] cArgsIG = new Class[1];
					cArgsIG[0] = List.class;
					emoteInitGui = EmoticonsClass.getDeclaredMethod("initGui", cArgsIG);
					// Assign Emoticons drawScreen Method;
					Class[] cArgsDS = new Class[4];
					cArgsDS[0] = int.class;
					cArgsDS[1] = int.class;
					cArgsDS[2] = float.class;
					cArgsDS[3] = GuiScreen.class;
					emoteDrawScreen = EmoticonsClass.getDeclaredMethod("drawScreen", cArgsDS);
				} catch (Exception e) {
					present = false;
				}
			} else {
				try {
					emoteObject = emoteConstructor.newInstance((Object[])null);
				} catch (Exception e) {
					present = false;
				}
			}
		}
	}
	
	public static void actionPerformed(GuiButton par1, List par2, GuiTextField par3) {
		if(!present) return;
		Object[] args = new Object[3];
		args[0] = par1;
		args[1] = par2;
		args[2] = par3;
		try {
			emoteActionPerformed.invoke(emoteObject, args);
		} catch (Exception e) {
			present = false;
		}
	}
	
	public static void drawScreen(int par1, int par2, float par3, GuiChatTC par4, List buttonList) {
		if(!present) return;
		Object[] args = new Object[4];
		args[0] = par1;
		args[1] = par2;
		args[2] = par3;
		args[3] = (GuiScreen)par4;
		emoteOffsetX = Math.max(par4.width - 427, 0);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)emoteOffsetX, 0.0f, 0.0f);
		try {
			emoteDrawScreen.invoke(emoteObject, args);
			for(GuiButton _button : (List<GuiButton>)buttonList) {
				if(!ChatButton.class.isInstance(_button) && _button.id > 2) _button.drawButton(Minecraft.getMinecraft(), par1-emoteOffsetX, par2);
			}
		} catch (Exception e) {
			present = false;
		} finally {
			GL11.glPopMatrix();
		}
	}
	
	public static void initGui(List par1) {
		if(!present) return;
		Object[] args = new Object[1];
		args[0] = par1;
		try {
			emoteInitGui.invoke(emoteObject, args);
		} catch (Exception e) {
			present = false;
		}
	}
}
