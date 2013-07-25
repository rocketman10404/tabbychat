package acs.tabbychat.compat;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.src.Minecraft;
import net.minecraft.src.GuiScreen;

public class MacroKeybindCompat {
	private static Constructor mkGuiConstructor = null;
	public static Object mkInChatLayout = null;
	public static Object mkInChatGUI = null;
	public static Object mkBtnGui = null;
	public static Object mkDropDownMenu = null;
	private static Class mkGuiCustomGui = null;
	private static Constructor mkCreateDesignerScreen = null;
	private static Method mkDraw = null;
	private static Method mkControlClicked = null;
	private static Method mkContextClicked = null;
	private static Method mkDrawBtnGui = null;
	private static Method mkLayoutTick = null;
	private static Method mkDrawDropDown = null;
	private static Method mkDropDownSize = null;
	public static boolean present = true;
	public static boolean hovered = false;
	public static Field menuLocation;	
	public static Field dropdownvisible;

	public static void load() {
		if(present) {
			if(mkDraw == null || mkControlClicked == null || mkGuiConstructor == null || mkGuiCustomGui == null || mkBtnGui == null
					|| mkDrawBtnGui == null || mkCreateDesignerScreen == null || mkLayoutTick == null || mkDropDownMenu == null
					|| mkContextClicked == null || mkDropDownSize == null) {
				try {
					// Classes
					Class mkLayoutManager = Class.forName("net.eq2online.macros.gui.designable.LayoutManager");
					Class mkDesignableGuiLayout = Class.forName("net.eq2online.macros.gui.designable.DesignableGuiLayout");
					Class mkButtonClass = Class.forName("net.eq2online.macros.gui.controls.GuiMiniToolbarButton");
					Class mkGuiMacroEdit = Class.forName("net.eq2online.macros.gui.screens.GuiDesigner");
					Class mkGuiDropDownMenu = Class.forName("net.eq2online.macros.gui.controls.GuiDropDownMenu");
					mkGuiCustomGui = Class.forName("net.eq2online.macros.gui.screens.GuiCustomGui");
					
					// Constructors
					Constructor mkButtonConstructor = mkButtonClass.getDeclaredConstructor(new Class[]{Minecraft.class, int.class, int.class, int.class});
					mkGuiConstructor = mkGuiCustomGui.getConstructor(new Class[]{mkDesignableGuiLayout, GuiScreen.class});
					mkCreateDesignerScreen = mkGuiMacroEdit.getDeclaredConstructor(new Class[]{String.class, GuiScreen.class, boolean.class});
					
					// Methods
					Method mkgetBoundLayout = mkLayoutManager.getDeclaredMethod("getBoundLayout", new Class[]{String.class, boolean.class});
					mkLayoutTick = mkDesignableGuiLayout.getDeclaredMethod("onTick", (Class[])null);
					mkDrawBtnGui = mkButtonClass.getDeclaredMethod("drawControlAt", new Class[]{Minecraft.class, int.class, int.class, int.class, int.class, int.class, int.class});
					mkDrawDropDown = mkGuiDropDownMenu.getDeclaredMethod("drawControlAt", new Class[]{int.class, int.class, int.class, int.class});
					mkDraw = mkDesignableGuiLayout.getDeclaredMethod("draw", new Class[]{Rectangle.class, int.class, int.class});
					mkControlClicked = mkGuiCustomGui.getDeclaredMethod("controlClicked", new Class[]{int.class, int.class, int.class});
					mkControlClicked.setAccessible(true);
					mkContextClicked = mkGuiCustomGui.getDeclaredMethod("contextMenuClicked", new Class[]{int.class, int.class, int.class});
					mkContextClicked.setAccessible(true);
					mkDropDownSize = mkGuiDropDownMenu.getDeclaredMethod("getSize", (Class[])null);
					
					// Fields
					Field mkContextMenu = mkGuiCustomGui.getDeclaredField("contextMenu");
					mkContextMenu.setAccessible(true);
					menuLocation = mkGuiCustomGui.getDeclaredField("contextMenuLocation");
					menuLocation.setAccessible(true);
					
					dropdownvisible = mkGuiDropDownMenu.getDeclaredField("dropDownVisible");
					dropdownvisible.setAccessible(true);
					
					// Objects
					mkInChatLayout = mkgetBoundLayout.invoke(null, new Object[]{"inchat", false});
					mkBtnGui = mkButtonConstructor.newInstance(new Object[]{Minecraft.getMinecraft(), 4, 104, 64});
					mkInChatGUI = mkGuiConstructor.newInstance(new Object[]{mkInChatLayout, null});
					mkDropDownMenu = mkContextMenu.get(mkInChatGUI);
					
				} catch (Exception e) {
					present = false;
				}
			} else {
				try {
					mkInChatGUI = mkGuiConstructor.newInstance(new Object[]{mkInChatLayout, null});
				} catch (Exception e) {
					present = false;
				}
			}
		}
	}
	
	public static boolean contextMenuClicked(int par1, int par2, int par3, GuiScreen par4) {
		if(!present) return false;
		try {			
			Class ddm = Class.forName("net.eq2online.macros.gui.controls.GuiDropDownMenu");
			Method mp = ddm.getDeclaredMethod("mousePressed", new Class[]{int.class, int.class});
			String menuItem = (String)mp.invoke(mkDropDownMenu, new Object[]{par1, par2});
			Field controlField = mkGuiCustomGui.getDeclaredField("clickedControl");
			controlField.setAccessible(true);
			Object control = controlField.get(mkInChatGUI);
			if(control == null) return false;
			Class controlClass = control.getClass();
			Method isBindable = controlClass.getMethod("getWidgetIsBindable", (Class[])null);
			Object oBindable = isBindable.invoke(control, (Object[])null);
			boolean bindable = ((Boolean)oBindable).booleanValue();
			
			if(menuItem != null && control != null) {
				if(menuItem.equals("execute")) {
					if(bindable) {
						Method click = mkGuiCustomGui.getDeclaredMethod("onControlClicked", new Class[]{Class.forName("net.eq2online.macros.gui.designable.DesignableGuiControl")});
						click.setAccessible(true);
						click.invoke(mkInChatGUI, new Object[]{control});
					}					
				} else if(menuItem.equals("edit")) {
					if(bindable) {
						Method displayScreen = Class.forName("net.eq2online.macros.compatibility.AbstractionLayer").getDeclaredMethod("displayGuiScreen", new Class[]{GuiScreen.class});
						Constructor macroEdit = Class.forName("net.eq2online.macros.gui.screens.GuiMacroEdit").getDeclaredConstructor(new Class[]{int.class, GuiScreen.class});
						Field id = controlClass.getField("id");
						displayScreen.invoke(null, macroEdit.newInstance(new Object[]{id.get(control), par4}));
					}					
				} else if(menuItem.equals("design")) {
					Method displayScreen = Class.forName("net.eq2online.macros.compatibility.AbstractionLayer").getDeclaredMethod("displayGuiScreen", new Class[]{GuiScreen.class});
					Constructor macroBind = Class.forName("net.eq2online.macros.gui.screens.GuiMacroBind").getDeclaredConstructor(new Class[]{int.class, GuiScreen.class});
					displayScreen.invoke(null, macroBind.newInstance(new Object[]{2, par4}));
				}
				return true;
			}
			return false;			
		} catch(Exception e) {
			present = false;
		}
		return false;
	}
	
	public static boolean controlClicked(int par1, int par2, int par3, GuiScreen par4) {
		if(!present) return false;
		boolean clicked = false;
		try {
			Field fBBox = mkGuiCustomGui.getDeclaredField("boundingBox");
			fBBox.setAccessible(true);
			fBBox.set(mkInChatGUI, new Rectangle(0, 0, par4.width, par4.height-14));
			clicked = ((Boolean)mkControlClicked.invoke(mkInChatGUI, new Object[]{par1, par2, par3})).booleanValue();
			if(clicked && par3 == 1) {
				dropdownvisible.set(mkDropDownMenu, true);
				Dimension contextMenuSize = (Dimension)mkDropDownSize.invoke(mkDropDownMenu, (Object[])null);
				menuLocation.set(mkInChatGUI, new Point(Math.min(par1, par4.width - contextMenuSize.width), Math.min(par2 - 8, par4.height - contextMenuSize.height)));
			}
			if(clicked) return true;
			if(hovered) {
				GuiScreen designerScreen = (GuiScreen)mkCreateDesignerScreen.newInstance(new Object[]{"inchat", par4, true});
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
			Point loc = (Point)menuLocation.get(mkInChatGUI);
			mkDrawDropDown.invoke(mkDropDownMenu, new Object[]{loc.x, loc.y, par1, par2});
			Object isHovered = mkDrawBtnGui.invoke(mkBtnGui, args2);
			hovered = ((Boolean)isHovered).booleanValue();
		} catch (Exception e) {
			present = false;
		}
		return;
	}
}
