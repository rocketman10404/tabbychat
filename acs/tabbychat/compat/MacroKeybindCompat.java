package acs.tabbychat.compat;

import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;

public class MacroKeybindCompat {
	private static Constructor mkGuiConstructor = null;
	public static Object mkInChatLayout = null;
	public static Object mkInChatGUI = null;
	public static Object mkBtnGui = null;
	private static Class mkGuiCustomGui = null;
	private static Constructor mkCreateDesignerScreen = null;
	private static Method mkDraw = null;
	private static Method mkControlClicked = null;
	private static Method mkDrawBtnGui = null;
	private static Method mkLayoutTick = null;
	public static boolean present = true;
	public static boolean hovered = false;

	public static void load() {
		if(present) {
			if(mkDraw == null || mkControlClicked == null || mkGuiConstructor == null || mkGuiCustomGui == null || mkBtnGui == null
					|| mkDrawBtnGui == null || mkCreateDesignerScreen == null || mkLayoutTick == null) {
				try {
					// net.eq2online.macros.gui.designable.LayoutManager.getBoundLayout(String slotName, boolean canBeNull)
					Class[] cArgs = new Class[2];
					cArgs[0] = String.class;
					cArgs[1] = boolean.class;

					Object[] oArgs = new Object[2];
					oArgs[0] = new String("inchat");
					oArgs[1] = false;

					// net.eq2online.macros.gui.designable.DesignableGuiLayout.draw(Rectangle bounds, int mouseX, int mouseY)
					Class[] cArgs2 = new Class[3];
					cArgs2[0] = Rectangle.class;
					cArgs2[1] = int.class;
					cArgs2[2] = int.class;

					// net.eq2online.macros.gui.screens.GuiCustomGui.controlClicked(int mouseX, int mouseY, int button)
					Class[] cArgs3 = new Class[3];
					cArgs3[0] = int.class;
					cArgs3[1] = int.class;
					cArgs3[2] = int.class;
					
					// net.eq2online.macros.gui.controls.GuiMiniToolbarButton constructor(Minecraft minecraft, int controlId, int u, int v)
					Class[] cArgs4 = new Class[4];
					cArgs4[0] = Minecraft.class;
					cArgs4[1] = int.class;
					cArgs4[2] = int.class;
					cArgs4[3] = int.class;
					Object[] oArgs4 = new Object[4];
					oArgs4[0] = Minecraft.getMinecraft();
					oArgs4[1] = 4;
					oArgs4[2] = 104;
					oArgs4[3] = 64;
					
					// net.eq2online.macros.gui.controls.GuiMiniToolbarButton.drawControlAt(Minecraft minecraft, int mouseX, int mouseY, 
					// int xPos, int yPos, int colour, int backColour)
					Class[] cArgs5 = new Class[7];
					cArgs5[0] = Minecraft.class;
					cArgs5[1] = int.class;
					cArgs5[2] = int.class;
					cArgs5[3] = int.class;
					cArgs5[4] = int.class;
					cArgs5[5] = int.class;
					cArgs5[6] = int.class;
					
					// net.eq2online.macros.gui.screens.GuiDesigner constructor(String guiSlotName, GuiScreen parentScreen, boolean allowBinding)
					Class[] cArgs6 = new Class[3];
					cArgs6[0] = String.class;
					cArgs6[1] = GuiScreen.class;
					cArgs6[2] = boolean.class;

					Class mkLayoutManager = Class.forName("net.eq2online.macros.gui.designable.LayoutManager");
					Class mkDesignableGuiLayout = Class.forName("net.eq2online.macros.gui.designable.DesignableGuiLayout");
					mkGuiCustomGui = Class.forName("net.eq2online.macros.gui.screens.GuiCustomGui");
					Method mkgetBoundLayout = mkLayoutManager.getDeclaredMethod("getBoundLayout", cArgs);
					mkInChatLayout = mkgetBoundLayout.invoke(null, oArgs);
					mkLayoutTick = mkDesignableGuiLayout.getDeclaredMethod("onTick", (Class[])null);

					Class[] cArgsTmp = new Class[2];
					cArgsTmp[0] = mkDesignableGuiLayout;
					cArgsTmp[1] = GuiScreen.class;
					mkGuiConstructor = mkGuiCustomGui.getConstructor(cArgsTmp);

					Class mkButtonClass = Class.forName("net.eq2online.macros.gui.controls.GuiMiniToolbarButton");
					Constructor mkButtonConstructor = mkButtonClass.getDeclaredConstructor(cArgs4);
					mkBtnGui = mkButtonConstructor.newInstance(oArgs4);
					mkDrawBtnGui = mkButtonClass.getDeclaredMethod("drawControlAt", cArgs5);
					
					Class mkGuiMacroEdit = Class.forName("net.eq2online.macros.gui.screens.GuiDesigner");
					mkCreateDesignerScreen = mkGuiMacroEdit.getDeclaredConstructor(cArgs6);
					
					Object[] oArgsTmp = new Object[2];
					oArgsTmp[0] = mkInChatLayout;
					oArgsTmp[1] = null;				
					mkInChatGUI = mkGuiConstructor.newInstance(oArgsTmp);		
					mkDraw = mkDesignableGuiLayout.getDeclaredMethod("draw", cArgs2);
					mkControlClicked = mkGuiCustomGui.getDeclaredMethod("controlClicked", cArgs3);
					mkControlClicked.setAccessible(true);		
				} catch (Exception e) { 
					present = false;
				}
			} else {
				try {
					Object[] oArgsTmp = new Object[2];
					oArgsTmp[0] = mkInChatLayout;
					oArgsTmp[1] = null;	
					mkInChatGUI = mkGuiConstructor.newInstance(oArgsTmp);
				} catch (Exception e) {
					present = false;
				}
			}
		}
	}
	
	public static boolean controlClicked(int par1, int par2, int par3, GuiScreen par4) {
		if(!present) return false;
		Object[] args = new Object[3];
		args[0] = par1;
		args[1] = par2;
		args[2] = par3;
		boolean clicked = false;
		try {
			Field fBBox = mkGuiCustomGui.getDeclaredField("boundingBox");
			fBBox.setAccessible(true);
			fBBox.set(mkInChatGUI, new Rectangle(0, 0, par4.width, par4.height-14));
			clicked = ((Boolean)mkControlClicked.invoke(mkInChatGUI, args)).booleanValue();
			if(clicked) return true;
			if(hovered) {
				Object[] dArgs = new Object[3];
				dArgs[0] = "inchat";
				dArgs[1] = par4;
				dArgs[2] = true;
				GuiScreen designerScreen = (GuiScreen)mkCreateDesignerScreen.newInstance(dArgs);
				Minecraft.getMinecraft().displayGuiScreen(designerScreen);
				return true;
			}
		} catch (Exception e) {
			present = false;
		}
		return clicked;
	}
	
	public static void drawScreen(int par1, int par2, GuiScreen par3) {
		if(!present) return;
		Object[] args = new Object[3];
		args[0] = new Rectangle(0, 0, par3.width, par3.height - 14);
		args[1] = par1;
		args[2] = par2;
		
		Object[] args2 = new Object[7];
		args2[0] = Minecraft.getMinecraft();
		args2[1] = par1;
		args2[2] = par2;
		args2[3] = par3.width - 20;
		args2[4] = par3.height - 14;
		args2[5] = 0xff1200;
		args2[6] = 0x80000000;
		
		try {
			mkLayoutTick.invoke(mkInChatLayout, (Object[])null);
			mkDraw.invoke(mkInChatLayout, args);
			Object isHovered = mkDrawBtnGui.invoke(mkBtnGui, args2);
			hovered = ((Boolean)isHovered).booleanValue();
		} catch (Exception e) {
			present = false;
		}
		return;
	}
}
