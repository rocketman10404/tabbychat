package acs.tabbychat.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.LinkedHashMap;

import acs.tabbychat.core.ChatChannel;
import acs.tabbychat.core.TabbyChat;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Gui;
import net.minecraft.src.ScaledResolution;

public class ChatBox {
	public static Rectangle current = new Rectangle(5, -200, 320, 180);
	public static Rectangle desired = new Rectangle(current);
	public static int tabHeight = 14;
	public static int tabTrayHeight = 14;
	
	public static void setChatSize(int width, int height) {
		Minecraft mc = TabbyChat.mc;
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		
		if(width + current.x > sr.getScaledWidth()) width = sr.getScaledWidth() - current.x;
		
		if(-20 - current.y < height) height = -20 - current.y;
		
		if(height + 20 + tabTrayHeight > sr.getScaledHeight()) {
			height = sr.getScaledHeight() - 21 - tabTrayHeight;
			current.y = -sr.getScaledHeight();
		} else {
			current.y = current.y - (height + tabTrayHeight + 1) + current.height;
		}
		
		current.setSize(width, height + tabTrayHeight + 1);
	}
	
	public static void addRowToTray() {
		Minecraft mc = TabbyChat.mc;
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		
		if(current.y > 20) {
			current.y -= 20;
			current.height += 20;
		} else {
			int extra = 20 - current.y - 1;
			current.y = 1;
			current.height = Math.min(current.height + 20, sr.getScaledHeight() - 20);
		}
		tabTrayHeight += 20;
	}
	
	public static void updateTabs(LinkedHashMap<String, ChatChannel> chanObjs, ScaledResolution sr) {
		int tabWidth = 0;
		int tabX = current.x;
		
		for(ChatChannel chan : chanObjs.values()) {
			tabWidth = TabbyChat.mc.fontRenderer.getStringWidth(chan.getDisplayTitle()) + 8;
			if(tabX + tabWidth > current.width - 5) {
				addRowToTray();
				int delta = sr.getScaledHeight() + current.y + tabTrayHeight - tabHeight;
				for(ChatChannel chan2 : chanObjs.values()) {
					if(chan2 == chan) break;
					chan2.tab.yPosition = delta;
				}
			}
			
			if(chan.tab == null) {
				chan.setButtonObj(new ChatButton(chan.getID(), tabX, sr.getScaledHeight() + current.y, tabWidth, 20, chan.getDisplayTitle()));
			} else {
				chan.tab.id = chan.getID();
				chan.tab.xPosition = tabX;
				chan.tab.yPosition = sr.getScaledHeight() + current.y;
				chan.tab.width(tabWidth);
				chan.tab.displayString = chan.getDisplayTitle();
			}			
			tabX = tabX + tabWidth + 1;
		}
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

}
