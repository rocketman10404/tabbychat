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
	private static Object inChatLayout = null;
	private static Object inChatGUI = null;
	private static Object btnGui = null;
	private static Object dropDownMenu = null;
	private static Class guiCustomGui = null;
	private static Constructor createDesignerScreen = null;
	private static Constructor macroEdit = null;
	private static Constructor macroDesign = null;
	private static Method draw = null;
	private static Method controlClicked = null;
	private static Method drawBtnGui = null;
	private static Method layoutTick = null;
	private static Method drawDropDown = null;
	private static Method dropDownSize = null;
	private static Method mousePressed = null;
	private static Method onControlClicked = null;
	private static Method displayScreen = null;
	private static Field menuLocation;	
	private static Field dropDownVisible;
	private static Field clickedControl;
	private static Field boundingBox;
	public static boolean present = true;
	private static boolean hovered = false;

	public static void load() {
		if(present) {
			if(inChatLayout == null
					|| inChatGUI == null
					|| btnGui == null
					|| dropDownMenu == null
					|| guiCustomGui == null
					|| createDesignerScreen == null
					|| draw == null
					|| controlClicked == null
					|| drawBtnGui == null
					|| layoutTick == null
					|| drawDropDown == null
					|| dropDownSize == null
					|| menuLocation == null
					|| dropDownVisible == null
					|| clickedControl == null
					|| mousePressed == null
					|| onControlClicked == null
					|| displayScreen == null
					|| macroEdit == null
					|| macroDesign == null
					|| boundingBox == null) {
				try {
					// Classes
					Class layoutManager = Class.forName("net.eq2online.macros.gui.designable.LayoutManager");
					Class designableGuiLayout = Class.forName("net.eq2online.macros.gui.designable.DesignableGuiLayout");
					Class buttonClass = Class.forName("net.eq2online.macros.gui.controls.GuiMiniToolbarButton");
					Class guiDesigner = Class.forName("net.eq2online.macros.gui.screens.GuiDesigner");
					Class guiDropDownMenu = Class.forName("net.eq2online.macros.gui.controls.GuiDropDownMenu");
					Class guiControl = Class.forName("net.eq2online.macros.gui.designable.DesignableGuiControl");
					Class abstractionLayer = Class.forName("net.eq2online.macros.compatibility.AbstractionLayer");
					Class guiMacroEdit = Class.forName("net.eq2online.macros.gui.screens.GuiMacroEdit");
					//Class guiMacroBind = Class.forName("net.eq2online.macros.gui.screens.GuiMacroBind");
					guiCustomGui = Class.forName("net.eq2online.macros.gui.screens.GuiCustomGui");
					Class localisationProvider = Class.forName("net.eq2online.macros.compatibility.LocalisationProvider");
					
					// Constructors
					Constructor mkButtonConstructor = buttonClass.getDeclaredConstructor(new Class[]{Minecraft.class, int.class, int.class, int.class});
					Constructor guiConstructor = guiCustomGui.getConstructor(new Class[]{designableGuiLayout, GuiScreen.class});
					createDesignerScreen = guiDesigner.getDeclaredConstructor(new Class[]{String.class, GuiScreen.class, boolean.class});
					macroEdit = guiMacroEdit.getDeclaredConstructor(new Class[]{int.class, GuiScreen.class});
					macroDesign = guiDesigner.getDeclaredConstructor(new Class[]{String.class, GuiScreen.class, boolean.class});
					
					// Methods
					Method mkgetBoundLayout = layoutManager.getDeclaredMethod("getBoundLayout", new Class[]{String.class, boolean.class});
					layoutTick = designableGuiLayout.getDeclaredMethod("onTick", (Class[])null);
					drawBtnGui = buttonClass.getDeclaredMethod("drawControlAt", new Class[]{Minecraft.class, int.class, int.class, int.class, int.class, int.class, int.class});
					drawDropDown = guiDropDownMenu.getDeclaredMethod("drawControlAt", new Class[]{int.class, int.class, int.class, int.class});
					draw = designableGuiLayout.getDeclaredMethod("draw", new Class[]{Rectangle.class, int.class, int.class});
					controlClicked = guiCustomGui.getDeclaredMethod("controlClicked", new Class[]{int.class, int.class, int.class});
					dropDownSize = guiDropDownMenu.getDeclaredMethod("getSize", (Class[])null);
					mousePressed = guiDropDownMenu.getDeclaredMethod("mousePressed", new Class[]{int.class, int.class});
					onControlClicked = guiCustomGui.getDeclaredMethod("onControlClicked", new Class[]{guiControl});
					displayScreen = abstractionLayer.getDeclaredMethod("displayGuiScreen", new Class[]{GuiScreen.class});
					Method dropDownAdd = guiDropDownMenu.getDeclaredMethod("addItem", new Class[]{String.class, String.class, int.class, int.class});
					Method getLocalisedString = localisationProvider.getDeclaredMethod("getLocalisedString", new Class[]{String.class});
					controlClicked.setAccessible(true);
					onControlClicked.setAccessible(true);
					
					// Fields
					Field mkContextMenu = guiCustomGui.getDeclaredField("contextMenu");
					menuLocation = guiCustomGui.getDeclaredField("contextMenuLocation");
					clickedControl = guiCustomGui.getDeclaredField("clickedControl");
					dropDownVisible = guiDropDownMenu.getDeclaredField("dropDownVisible");
					boundingBox = guiCustomGui.getDeclaredField("boundingBox");
					mkContextMenu.setAccessible(true);
					menuLocation.setAccessible(true);
					clickedControl.setAccessible(true);
					dropDownVisible.setAccessible(true);
					boundingBox.setAccessible(true);
					
					// Objects
					inChatLayout = mkgetBoundLayout.invoke(null, new Object[]{"inchat", false});
					btnGui = mkButtonConstructor.newInstance(new Object[]{Minecraft.getMinecraft(), 4, 104, 64});
					inChatGUI = guiConstructor.newInstance(new Object[]{inChatLayout, null});
					dropDownMenu = mkContextMenu.get(inChatGUI);
					dropDownAdd.invoke(dropDownMenu, new Object[]{"design", "\247e" + getLocalisedString.invoke(null, new Object[]{"tooltip.guiedit"}), 26, 16});
					
				} catch (Exception e) {
					present = false;
				}
			} else {
				try {
					Class designableGuiLayout = Class.forName("net.eq2online.macros.gui.designable.DesignableGuiLayout");
					Constructor guiConstructor = guiCustomGui.getConstructor(new Class[]{designableGuiLayout, GuiScreen.class});
					inChatGUI = guiConstructor.newInstance(new Object[]{inChatLayout, null});
				} catch (Exception e) {
					present = false;
				}
			}
		}
	}
	
	public static boolean contextMenuClicked(int par1, int par2, int par3, GuiScreen par4) {
		if(!present) return false;
		try {			
			String menuItem = (String)mousePressed.invoke(dropDownMenu, new Object[]{par1, par2});
			Object control = clickedControl.get(inChatGUI);
			if(control == null) return false;
			Class controlClass = control.getClass();
			Method isBindable = controlClass.getMethod("getWidgetIsBindable", (Class[])null);
			Object oBindable = isBindable.invoke(control, (Object[])null);
			boolean bindable = ((Boolean)oBindable).booleanValue();
			
			if(menuItem != null && control != null) {
				if(menuItem.equals("execute")) {
					if(bindable) {						
						onControlClicked.invoke(inChatGUI, new Object[]{control});
					}					
				} else if(menuItem.equals("edit")) {
					if(bindable) {
						Field id = controlClass.getField("id");
						displayScreen.invoke(null, macroEdit.newInstance(new Object[]{id.get(control), par4}));
					}					
				} else if(menuItem.equals("design")) {
					displayScreen.invoke(null, macroDesign.newInstance(new Object[]{"inchat", par4, true}));
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
			boundingBox.set(inChatGUI, new Rectangle(0, 0, par4.width, par4.height-14));
			clicked = ((Boolean)controlClicked.invoke(inChatGUI, new Object[]{par1, par2, par3})).booleanValue();
			if(clicked && par3 == 1) {
				dropDownVisible.set(dropDownMenu, true);
				Dimension contextMenuSize = (Dimension)dropDownSize.invoke(dropDownMenu, (Object[])null);
				menuLocation.set(inChatGUI, new Point(Math.min(par1, par4.width - contextMenuSize.width), Math.min(par2 - 8, par4.height - contextMenuSize.height)));
			}
			if(clicked) return true;
			if(hovered) {
				GuiScreen designerScreen = (GuiScreen)createDesignerScreen.newInstance(new Object[]{"inchat", par4, true});
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
			layoutTick.invoke(inChatLayout, (Object[])null);
			draw.invoke(inChatLayout, args);
			Point loc = (Point)menuLocation.get(inChatGUI);
			drawDropDown.invoke(dropDownMenu, new Object[]{loc.x, loc.y, par1, par2});
			Object isHovered = drawBtnGui.invoke(btnGui, args2);
			hovered = ((Boolean)isHovered).booleanValue();
		} catch (Exception e) {
			present = false;
		}
		return;
	}
}
