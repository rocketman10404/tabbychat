package acs.tabbychat.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;

import org.lwjgl.input.Mouse;

import acs.tabbychat.core.ChatChannel;
import acs.tabbychat.core.TabbyChat;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ScaledResolution;

public class ChatBox {
	public static Rectangle current = new Rectangle(2, -216, 320, 180);
	public static Rectangle desired = new Rectangle(current);
	private static int absMinX = 0;
	private static int absMinY = -36;
	private static int absMinW = 200;
	private static int absMinH = 23;
	public static int tabHeight = 14;
	public static int tabTrayHeight = 14;
	public static int unfocusedHeight = 180;
	public static boolean dragging = false;
	public static Point dragStart =  new Point(0,0);
	public static boolean resizing = false;
	public static boolean anchoredTop = false;
	
	public static void addRowToTray(ScaledResolution sr) {
		if(current.y - tabHeight > -sr.getScaledHeight()) {
			current.y -= tabHeight;
			current.height += tabHeight;
		} else if(current.height + tabHeight > sr.getScaledHeight()) {
			current.y = -sr.getScaledHeight() + 1;
			current.height = sr.getScaledHeight() - 3;
		} else {
			current.y = -sr.getScaledHeight() + 1;
			current.height += tabHeight;
		}
		tabTrayHeight += tabHeight;
	}
	
	public static void drawChatBoxBorder(Gui overlay, boolean chatOpen, int opacity) {
		int borderColor = 0x000000 + (2*opacity/3 << 24);
		int trayColor = 0x000000 + (opacity/3 << 24);
		int handleColor = resizeHovered() ? 0xffffa0 + (2*opacity/3 << 24) : borderColor;
		
		if(chatOpen) {
			// Draw border around entire chat area
			overlay.drawRect(-1, -current.height-1, current.width+ChatScrollBar.barWidth+3, -current.height, borderColor);
			overlay.drawRect(-1, -current.height, 0, 0, borderColor);
			overlay.drawRect(current.width+ChatScrollBar.barWidth+2, -current.height, current.width+ChatScrollBar.barWidth+3, 0, borderColor);
			overlay.drawRect(-1, 0, current.width+ChatScrollBar.barWidth+3, 1, borderColor);
			
			if(!anchoredTop) {
				// 	Draw border between focused chatbox and tab tray
				overlay.drawRect(0, -current.height+tabTrayHeight, current.width+ChatScrollBar.barWidth+2, -current.height+tabTrayHeight+1, borderColor);

				// 	Add shading to tab tray
				overlay.drawRect(0, -current.height, current.width+ChatScrollBar.barWidth+2, -current.height+tabTrayHeight, trayColor);

				// 	Draw handle for mouse drag
				overlay.drawRect(current.width+ChatScrollBar.barWidth-5, -current.height+2, current.width+ChatScrollBar.barWidth, -current.height+3, handleColor);
				overlay.drawRect(current.width+ChatScrollBar.barWidth-1, -current.height+3, current.width+ChatScrollBar.barWidth, -current.height+7, handleColor);
			} else {
				//	Draw border between focused chatbox and tab tray
				overlay.drawRect(0, -tabTrayHeight-1, current.width+ChatScrollBar.barWidth+2, -tabTrayHeight, borderColor);

				// 	Add shading to tab tray
				overlay.drawRect(0, -tabTrayHeight, current.width+ChatScrollBar.barWidth+2, 0, trayColor);

				// 	Draw handle for mouse drag
				overlay.drawRect(current.width+ChatScrollBar.barWidth-5, -2, current.width+ChatScrollBar.barWidth, -3, handleColor);
				overlay.drawRect(current.width+ChatScrollBar.barWidth-1, -3, current.width+ChatScrollBar.barWidth, -7, handleColor);	
			}
		} else {
			if(!anchoredTop) {
				// Draw border around unfocused chatbox
				overlay.drawRect(-1, -unfocusedHeight+tabTrayHeight, current.width+1, -unfocusedHeight+tabTrayHeight+1, borderColor);
				overlay.drawRect(-1, -unfocusedHeight+tabTrayHeight+1, 0, 0, borderColor);
				overlay.drawRect(current.width, -unfocusedHeight+tabTrayHeight+1, current.width+1, 0, borderColor);
				overlay.drawRect(-1, 0, current.width+1, 1, borderColor);
			} else {
				// Draw border around unfocused chatbox
				overlay.drawRect(-1, -current.height, current.width+1, -current.height+1, borderColor);
				overlay.drawRect(-1, -current.height+1, 0, -current.height+unfocusedHeight-tabTrayHeight-1, borderColor);
				overlay.drawRect(current.width, -current.height+1, current.width+1, -current.height+unfocusedHeight-tabTrayHeight-1, borderColor);
				overlay.drawRect(-1, -current.height+unfocusedHeight-tabTrayHeight-1, current.width+1, -current.height+unfocusedHeight-tabTrayHeight, borderColor);
			}
			
		}
	}
	
	
	
	public static int getChatHeight() {
		return current.height - tabTrayHeight - 1;
	}
	
	public static int getChatWidth() {
		return current.width;
	}
	
	private static Rectangle scaleBoundedSpace(Rectangle unscaled) {
		Rectangle scaled = new Rectangle();
		float scaleSetting = TabbyChat.gnc.getScaleSetting();
		
		scaled.x = Math.round((unscaled.x - current.x) * scaleSetting) + current.x;
		scaled.y = Math.round((unscaled.y - current.y - current.height) * scaleSetting) + current.y + current.height;
		scaled.width = Math.round(unscaled.width * scaleSetting);
		scaled.height = Math.round(unscaled.height * scaleSetting);
		
		return scaled;
	}
	
	public static void handleMouseDrag(int _curX, int _curY) {
		if(!dragging) return;
		
		Point click = scaleMouseCoords(_curX, _curY, true);
		if(Math.abs(click.x - dragStart.x) < 3 && Math.abs(click.y - dragStart.y) < 3) return;
		
		float scaleSetting = TabbyChat.gnc.getScaleSetting();	
		int scaledWidth = (int)((TabbyChat.gnc.sr.getScaledWidth() - current.x) / scaleSetting + current.x);
		int scaledHeight = (int)((TabbyChat.gnc.sr.getScaledHeight() + current.y + current.height) / scaleSetting - current.y - current.height);
				
		desired.x = current.x + click.x - dragStart.x;
		desired.y = current.y + click.y - dragStart.y;
		
		if(desired.x < absMinX+1) current.x = absMinX+1;
		else if(desired.x + current.width + ChatScrollBar.barWidth + 3 > scaledWidth) current.x = scaledWidth - current.width - ChatScrollBar.barWidth - 3;
		else current.x = desired.x;
		
		if(desired.y < -scaledHeight + 1) {
			current.y = -scaledHeight + 1;
			if(!anchoredTop) {
				anchoredTop = true;
				dragging = false;
			}
		} else if(desired.y + current.height + 1 >= absMinY) {
			current.y = absMinY - current.height - 1;
			if(anchoredTop) {
				anchoredTop = false;
				dragging = false;
			}
		} else current.y = desired.y;		
				
		dragStart = click;
	}
	
	public static void handleMouseResize(int _curX, int _curY) {
		if(!resizing) return;
		
		Point click = scaleMouseCoords(_curX, _curY, true);
		if(Math.abs(click.x - dragStart.x) < 3 && Math.abs(click.y - dragStart.y) < 3) return;
		
		float scaleSetting = TabbyChat.gnc.getScaleSetting();		
		int scaledWidth = (int)((TabbyChat.gnc.sr.getScaledWidth() - current.x) / scaleSetting + current.x);
		int scaledHeight = (int)((TabbyChat.gnc.sr.getScaledHeight() + current.y + current.height) / scaleSetting - current.y - current.height);
		
		desired.width = current.width + click.x - dragStart.x;
		if(!anchoredTop) {
			desired.height = current.height - click.y + dragStart.y;
			desired.y = current.y + _curY - dragStart.y;
		} else {
			desired.y = current.y;
			desired.height = current.height + click.y - dragStart.y;
		}
	
		if(desired.x + desired.width + ChatScrollBar.barWidth + 3 > scaledWidth) {
			current.width = scaledWidth - current.x - ChatScrollBar.barWidth - 3;
		} else {
			current.width = Math.max(desired.width, absMinW);
		}
	
		if(desired.y < -scaledHeight + 1) {
			current.height += current.y + scaledHeight - 1;
			current.y = -scaledHeight + 1;
		} else {
			if(!anchoredTop) current.y -= Math.max(desired.height, absMinH) - current.height;
			current.height = Math.max(desired.height, absMinH);
		}
		
		dragStart = click;
	}
	
	public static boolean resizeHovered() {
		// Check for mouse cursor over resize handle

		Point cursor = scaleMouseCoords(Mouse.getX(), Mouse.getY());

		int rX = current.x + current.width + ChatScrollBar.barWidth - 6;
		int rY;
		if(anchoredTop) rY = current.y + current.height - 8;
		else rY = current.y;	
		
		return (cursor.x > rX && cursor.x < rX + 8 && cursor.y > rY && cursor.y < rY + 8);
	}
	
	public static void setChatSize(int width, int height) {
		Minecraft mc = TabbyChat.mc;
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int bottomY = current.y + current.height;
		
		float scaleSetting = TabbyChat.gnc.getScaleSetting();
		//int scaledWidth = (int)(sr.getScaledWidth() / scaleSetting);
		//int scaledHeight = (int)(sr.getScaledHeight() / scaleSetting);		
		int scaledWidth = (int)((sr.getScaledWidth() - current.x) / scaleSetting + current.x);
		int scaledHeight = (int)((sr.getScaledHeight() + current.y + current.height) / scaleSetting - current.y - current.height);
		
		// Enforce min allowable width for chatbox
		width = Math.max(100, width);		
		// Enforce max allowable width for chatbox
		if(width + current.x + ChatScrollBar.barWidth + 3 > scaledWidth) width = scaledWidth - current.x - ChatScrollBar.barWidth - 3;
		
		// Enforce min allowable height for chat area of chatbox
		height = Math.max(9, height);
		// Enforce max allowable height and y-origin for chatbox
		if(height - absMinY + tabTrayHeight + 3 > scaledHeight) {
			height = scaledHeight + absMinY - 3 - tabTrayHeight;
			current.y = -scaledHeight + 1;
		} else {
			if(!anchoredTop) current.y = Math.max(bottomY - height - tabTrayHeight - 1, -scaledHeight + 1);
		}
		
		current.setSize(width, height + tabTrayHeight + 1);
	}
	
	public static void setUnfocusedHeight(int uHeight) {
		uHeight = Math.max(9, uHeight);
		unfocusedHeight = uHeight + tabTrayHeight + 1;
	}
	
	public static void startDragging(int atX, int atY) {
		dragging = true;
		resizing = false;
		dragStart = scaleMouseCoords(atX, atY, true);
	}
	
	public static void startResizing(int atX, int atY) {
		dragging = false;
		resizing = true;
		dragStart = scaleMouseCoords(atX, atY, true);
	}

	public static boolean tabTrayHovered(int mx, int my) {
		boolean chatOpen = TabbyChat.gnc.getChatOpen();
		GuiScreen theScreen = TabbyChat.mc.currentScreen;
		if(!chatOpen || theScreen == null) return false;
		
		Point click = scaleMouseCoords(mx, my);
		
		if(!anchoredTop) {
			return (click.x > current.x && click.x < current.x + current.width && click.y > current.y && click.y < current.y + tabTrayHeight);
		} else {
			return (click.x > current.x && click.x < current.x + current.width && click.y > current.y + current.height - tabTrayHeight && click.y < current.y + current.height);
		}
	}
	
	public static Point scaleMouseCoords(int _x, int _y) {
		return scaleMouseCoords(_x, _y, false);
	}
	
	public static Point scaleMouseCoords(int _x, int _y, boolean forGuiScreen) {
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen theScreen = mc.currentScreen;
		
		/** transform LWJGL coordinate space (bottom-left) to Minecraft screen coordinate space (top-left, scaled to GUI settings) **/

		// transform to Minecraft GUI scale settings
		_x = _x * theScreen.width / mc.displayWidth;
		
		// transform to include chat scale setting and initial offset
		float chatScale = TabbyChat.gnc.getScaleSetting();
		_x = Math.round((_x - current.x) / chatScale) + current.x;

		if(!forGuiScreen) {
			// transform to GuiNewChat coordinate space (bottom-to-top is 0 to negative screen height)
			// including chat scale setting
			_y = -_y * theScreen.height /  mc.displayHeight;
			// Subtract offset, scale, add offset back
			_y = Math.round((_y - current.y - current.height) / chatScale) + current.y + current.height; 
		} else {
			// Apply set GUI scale, keep screen bottom as origin
			_y = _y * theScreen.height / mc.displayHeight;
			// Subtract offset, scale, add offset back
			_y = Math.round((_y + current.y + current.height) / chatScale) - current.y - current.height;
			// Flip to GuiScreen coordinate space - origin at top/left
			_y = theScreen.height - _y;
		}
		return new Point(_x, _y);
	}
	
	public static void updateTabs(LinkedHashMap<String, ChatChannel> chanObjs, ScaledResolution sr) {
		int tabWidth = 0;
		float scaleSetting = TabbyChat.gnc.getScaleSetting();
		int tabX = current.x;
		int tabDx = 0;
		int rows = 1;
		
		// Reset tab tray height
		int moveY = tabTrayHeight - tabHeight;
		tabTrayHeight = tabHeight;
		current.height -= moveY;
		current.y += moveY;
		
		for(ChatChannel chan : chanObjs.values()) {
			tabWidth = TabbyChat.mc.fontRenderer.getStringWidth(chan.getDisplayTitle()) + 8;
			if(tabDx + tabWidth > current.width - 6 && tabWidth < current.width - 6) {
				rows++;
				if(tabHeight * rows > tabTrayHeight) {
						addRowToTray(sr);
				}
				tabDx = 0;
				for(ChatChannel chan2 : chanObjs.values()) {
					if(chan2 == chan) break;
					chan2.tab.yPosition = sr.getScaledHeight() + current.y + tabHeight;
					if(anchoredTop) chan2.tab.yPosition += current.height - tabHeight;
				}
			}
			
			if(chan.tab == null) {
				chan.setButtonObj(new ChatButton(chan.getID(), tabX+tabDx, sr.getScaledHeight()+current.y, tabWidth, tabHeight, chan.getDisplayTitle()));
			} else {
				chan.tab.id = chan.getID();
				chan.tab.xPosition = tabX + tabDx;
				chan.tab.yPosition = sr.getScaledHeight() + current.y;
				if(anchoredTop) chan.tab.yPosition += current.height - tabTrayHeight;
				chan.tab.width(tabWidth);
				chan.tab.height(tabHeight);
				chan.tab.displayString = chan.getDisplayTitle();
			}
			tabDx += tabWidth + 1;
		}
	}
}
