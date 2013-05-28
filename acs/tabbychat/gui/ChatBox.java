package acs.tabbychat.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedHashMap;

import acs.tabbychat.core.ChatChannel;
import acs.tabbychat.core.TabbyChat;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Gui;
import net.minecraft.src.ScaledResolution;

public class ChatBox {
	public static Rectangle current = new Rectangle(2, -216, 320, 180);
	public static Rectangle desired = new Rectangle(current);
	public static int absMinX = 2;
	public static int absMinY = -36;
	public static int tabHeight = 14;
	public static int tabTrayHeight = 14;
	public static boolean dragging = false;
	public static Point dragStart =  new Point(0,0);
	
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
		
		if(chatOpen) {
			// Draw border around entire chat area
			overlay.drawRect(-1, -current.height-1, current.width+ChatScrollBar.barWidth+3, -current.height, borderColor);
			overlay.drawRect(-1, -current.height, 0, 0, borderColor);
			overlay.drawRect(current.width+ChatScrollBar.barWidth+2, -current.height, current.width+ChatScrollBar.barWidth+3, 0, borderColor);
			overlay.drawRect(-1, 0, current.width+ChatScrollBar.barWidth+3, 1, borderColor);
			
			// Draw border between focused chatbox and tab tray
			overlay.drawRect(0, -current.height+tabTrayHeight, current.width+ChatScrollBar.barWidth+2, -current.height+tabTrayHeight+1, borderColor);
			
			// Add shading to tab tray
			overlay.drawRect(0, -current.height, current.width+ChatScrollBar.barWidth+2, -current.height+tabTrayHeight, trayColor);
		} else {
			// Draw border around unfocused chatbox
			overlay.drawRect(-1, -current.height+tabTrayHeight, current.width+1, -current.height+tabTrayHeight+1, borderColor);
			overlay.drawRect(-1, -current.height+tabTrayHeight+1, 0, 0, borderColor);
			overlay.drawRect(current.width, -current.height+tabTrayHeight+1, current.width+1, 0, borderColor);
			overlay.drawRect(-1, 0, current.width+1, 1, borderColor);	
			
		}
	}
	
	public static int getChatHeight() {
		return current.height - tabTrayHeight - 1;
	}
	
	public static void handleMouseDrag(int _curX, int _curY) {
		if(!dragging) return;
		if(Math.abs(_curX - dragStart.x) < 3 && Math.abs(_curY - dragStart.y) < 3 ) return;
		
		Minecraft mc = TabbyChat.mc;
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		
		desired.x = current.x + _curX - dragStart.x;
		desired.y = current.y + _curY - dragStart.y;
		
		if(desired.x < absMinX+1) current.x = absMinX+1;
		else if(desired.x + current.width + ChatScrollBar.barWidth + 3 > sr.getScaledWidth()) current.x = sr.getScaledWidth() - current.width - ChatScrollBar.barWidth - 3;
		else current.x = desired.x;
		
		if(desired.y < -sr.getScaledHeight() + 1) current.y = -sr.getScaledHeight() + 1;
		else if(desired.y + current.height + 1 >= absMinY) {
			current.y = absMinY - current.height - 1;
		} else current.y = desired.y;		
		
		dragStart.setLocation(_curX, _curY);
	}
	
	public static void setChatSize(int width, int height) {
		Minecraft mc = TabbyChat.mc;
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int bottomY = current.y + current.height;
		
		// Enforce min allowable width for chatbox
		width = Math.max(100, width);		
		// Enforce max allowable width for chatbox
		if(width + current.x > sr.getScaledWidth()) width = sr.getScaledWidth() - current.x;
		
		// Enforce min allowable height for chat area of chatbox
		height = Math.max(9, height);
		// Enforce max allowable height and y-origin for chatbox
		if(height - absMinY + tabTrayHeight + 3 > sr.getScaledHeight()) {
			height = sr.getScaledHeight() + absMinY - 3 - tabTrayHeight;
			current.y = -sr.getScaledHeight() + 1;
		} else {
			current.y = bottomY - height - tabTrayHeight - 1;
		}
		
		current.setSize(width, height + tabTrayHeight + 1);
	}
	
	public static void startDragging(int atX, int atY) {
		dragging = true;
		dragStart = new Point(atX, atY);
	}

	public static void updateTabs(LinkedHashMap<String, ChatChannel> chanObjs, ScaledResolution sr) {
		int tabWidth = 0;
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
				}
			}
			
			if(chan.tab == null) {
				chan.setButtonObj(new ChatButton(chan.getID(), current.x+tabDx, sr.getScaledHeight()+current.y, tabWidth, tabHeight, chan.getDisplayTitle()));
			} else {
				chan.tab.id = chan.getID();
				chan.tab.xPosition = current.x + tabDx;
				chan.tab.yPosition = sr.getScaledHeight() + current.y;
				chan.tab.width(tabWidth);
				chan.tab.height(tabHeight);
				chan.tab.displayString = chan.getDisplayTitle();
			}
			tabDx += tabWidth + 1;
		}
	}
}
