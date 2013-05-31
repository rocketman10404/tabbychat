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
import net.minecraft.src.MathHelper;
import net.minecraft.src.ScaledResolution;

public class ChatBox {
	public static Rectangle current = new Rectangle(2, -216, 320, 180);
	public static Rectangle desired = new Rectangle(current);
	private static int absMinX = 0;
	private static int absMinY = -36;
	private static int absMinW = 200;
	private static int absMinH = 24;
	private static int tabHeight = 14;
	protected static int tabTrayHeight = 14;
	private static int chatHeight = 165;
	public static int unfocusedHeight = 180;
	public static boolean dragging = false;
	private static Point dragStart =  new Point(0,0);
	public static boolean resizing = false;
	public static boolean anchoredTop = false;
	
	public static void addRowToTray(ScaledResolution sr) {
		// TODO: test this better
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
			overlay.drawRect(-1, -current.height-1, current.width+1, -current.height, borderColor);
			overlay.drawRect(-1, -current.height, 0, 0, borderColor);
			overlay.drawRect(current.width, -current.height, current.width+1, 0, borderColor);
			overlay.drawRect(-1, 0, current.width+1, 1, borderColor);
			
			if(!anchoredTop) {
				// 	Draw border between focused chatbox and tab tray
				overlay.drawRect(0, -current.height+tabTrayHeight, current.width, -current.height+tabTrayHeight+1, borderColor);

				// 	Add shading to tab tray
				overlay.drawRect(0, -current.height, current.width, -current.height+tabTrayHeight, trayColor);
				
				// Draw filler for extra chat space
				overlay.drawRect(0, -current.height+tabTrayHeight+1, current.width-ChatScrollBar.barWidth-2, -chatHeight, opacity / 2 << 24);

				// 	Draw handle for mouse drag
				overlay.drawRect(current.width-7, -current.height+2, current.width-2, -current.height+3, handleColor);
				overlay.drawRect(current.width-3, -current.height+3, current.width-2, -current.height+7, handleColor);
			} else {
				//	Draw border between focused chatbox and tab tray
				overlay.drawRect(0, -tabTrayHeight-1, current.width, -tabTrayHeight, borderColor);

				// 	Add shading to tab tray
				overlay.drawRect(0, -tabTrayHeight, current.width, 0, trayColor);
				
				// Draw filler for extra chat space
				overlay.drawRect(0, -current.height+chatHeight, current.width-ChatScrollBar.barWidth-2, -tabTrayHeight-1, opacity / 2 << 24);

				// 	Draw handle for mouse drag
				overlay.drawRect(current.width-7, -2, current.width-2, -3, handleColor);
				overlay.drawRect(current.width-3, -3, current.width-2, -7, handleColor);	
			}
		} else {
			if(!anchoredTop) {
				// Draw border around unfocused chatbox
				overlay.drawRect(-1, -unfocusedHeight+tabTrayHeight, current.width+1, -unfocusedHeight+tabTrayHeight+1, borderColor);
				overlay.drawRect(-1, -unfocusedHeight+tabTrayHeight+1, 0, 0, borderColor);
				overlay.drawRect(current.width, -unfocusedHeight+tabTrayHeight+1, current.width+1, 0, borderColor);
				overlay.drawRect(-1, 0, current.width+1, 1, borderColor);
				
				// Draw filler for scrollbar
				overlay.drawRect(current.width-ChatScrollBar.barWidth-2, -unfocusedHeight+tabTrayHeight+1, current.width, 0, opacity / 2 << 24);
			} else {
				// Draw border around unfocused chatbox
				overlay.drawRect(-1, -current.height, current.width+1, -current.height+1, borderColor);
				overlay.drawRect(-1, -current.height+1, 0, -current.height+unfocusedHeight-tabTrayHeight-1, borderColor);
				overlay.drawRect(current.width, -current.height+1, current.width+1, -current.height+unfocusedHeight-tabTrayHeight-1, borderColor);
				overlay.drawRect(-1, -current.height+unfocusedHeight-tabTrayHeight-1, current.width+1, -current.height+unfocusedHeight-tabTrayHeight, borderColor);
				
				// Draw filler for scrollbar
				overlay.drawRect(current.width-ChatScrollBar.barWidth-2, -current.height+1, current.width, -current.height+unfocusedHeight-tabTrayHeight-1, opacity / 2 << 24);
			}
			
		}
	}
	
	public static void enforceScreenBoundary(Rectangle newBounds) {
		// Grow virtual screen width/height to counter reduced size due to chat scaling
		float scaleSetting = TabbyChat.gnc.getScaleSetting();
		int scaledWidth = (int)((TabbyChat.gnc.sr.getScaledWidth() - current.x) / scaleSetting + current.x);
		int scaledHeight = (int)((TabbyChat.gnc.sr.getScaledHeight() + current.y + current.height) / scaleSetting - current.y - current.height);
		
		System.out.print("scaleSetting: "+scaleSetting+" -- scaled height: "+TabbyChat.gnc.sr.getScaledHeight()+" -- current y: "+current.y+" -- current height: "+current.height);
		System.out.print(" -- float result: "+((TabbyChat.gnc.sr.getScaledHeight() + current.y + current.height) / scaleSetting - current.y - current.height));
		System.out.println(" -- int result: "+scaledHeight);
		
		// Apply desired position
		current.setBounds(newBounds);
		
		// Enforce minimum width/height
		if(current.height < absMinH) current.height = absMinH;
		if(current.width < absMinW) current.width = absMinW;
		
		// Enforce minimum x position
		if(current.x < absMinX + 1) current.x = absMinX + 1;
		
		// Enforce minimum y position (top of screen)
		if(current.y - 1 < -scaledHeight) current.y = -scaledHeight + 1;
		
		// Enforce maximum x position (including width)
		if(current.x + current.width + 1> scaledWidth) {
			// If resizing, reduce width to compensate
			if(resizing) current.width = scaledWidth - current.x - 1;
			// Otherwise, reduce x-location to compensate
			else current.x = scaledWidth - current.width - 1;
		}
		
		// Enforce maximum y position (including height) (bottom of screen)
		if(current.y + current.height + 1 > absMinY) {
			// If resizing, reduce height to compensate
			if(resizing) current.height = absMinY - current.y - 1;
			// Otherwise, increase y-location to compensate
			else current.y = absMinY - current.height - 1;
		}
	}
	
	public static int getChatHeight() {
		return current.height - tabTrayHeight - 1;
	}
	
	public static int getChatWidth() {
		return current.width - ChatScrollBar.barWidth-2;
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
		int scaledHeight = (int)((TabbyChat.gnc.sr.getScaledHeight() + current.y + current.height) / scaleSetting - current.y - current.height);
				
		desired.x = current.x + click.x - dragStart.x;
		desired.y = current.y + click.y - dragStart.y;
		
		if(desired.y < -scaledHeight + 1 && !anchoredTop) {
			anchoredTop = true;
			dragging = false;
		} else if(desired.y + current.height + 1 > absMinY && anchoredTop) {
			anchoredTop = false;
			dragging = false;
		}
		
		desired.setSize(current.width, current.height);
		enforceScreenBoundary(desired);
						
		dragStart = click;
	}
	
	public static void handleMouseResize(int _curX, int _curY) {
		if(!resizing) return;
		
		Point click = scaleMouseCoords(_curX, _curY, true);
		if(Math.abs(click.x - dragStart.x) < 3 && Math.abs(click.y - dragStart.y) < 3) return;
		
		desired.width = current.width + click.x - dragStart.x;
		desired.x = current.x;
		if(!anchoredTop) {
			desired.height = current.height - click.y + dragStart.y;
			desired.y = current.y + click.y - dragStart.y;
		} else {
			desired.height = current.height + click.y - dragStart.y;
			desired.y = current.y;
		}
		
		enforceScreenBoundary(desired);
		dragStart = click;
	}
	
	public static boolean resizeHovered() {
		// Check for mouse cursor over resize handle

		Point cursor = scaleMouseCoords(Mouse.getX(), Mouse.getY());
		if(cursor == null) return false;

		int rX = current.x + current.width - 8;
		int rY;
		if(anchoredTop) rY = current.y + current.height - 8;
		else rY = current.y;	
		
		return (cursor.x > rX && cursor.x < rX + 8 && cursor.y > rY && cursor.y < rY + 8);
	}
	
	public static void setChatSize(int height) {
		chatHeight = height;
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
		if(theScreen == null) return null;
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
