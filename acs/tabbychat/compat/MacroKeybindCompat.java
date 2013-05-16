package acs.tabbychat.compat;

import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.src.GuiScreen;

public class MacroKeybindCompat {
	private static Constructor mkGuiConstructor = null;
	public static Object mkInChatLayout = null;
	public static Object mkInChatGUI = null;
	private static Class mkGuiCustomGui = null;
	private static Method mkDraw = null;
	private static Method mkControlClicked = null;
	public static boolean present = true;

	public static void load() {
		if(present) {
			if(mkDraw == null || mkControlClicked == null || mkGuiConstructor == null || mkGuiCustomGui == null) {
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

					Class mkLayoutManager = Class.forName("net.eq2online.macros.gui.designable.LayoutManager");
					Class mkDesignableGuiLayout = Class.forName("net.eq2online.macros.gui.designable.DesignableGuiLayout");
					mkGuiCustomGui = Class.forName("net.eq2online.macros.gui.screens.GuiCustomGui");
					Method mkgetBoundLayout = mkLayoutManager.getDeclaredMethod("getBoundLayout", cArgs);
					mkInChatLayout = mkgetBoundLayout.invoke(null, oArgs);

					Class[] cArgsTmp = new Class[2];
					cArgsTmp[0] = mkDesignableGuiLayout;
					cArgsTmp[1] = GuiScreen.class;
					mkGuiConstructor = mkGuiCustomGui.getConstructor(cArgsTmp);

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
		try {
			mkDraw.invoke(mkInChatLayout, args);
		} catch (Exception e) {
			present = false;
		}
		return;
	}
}
