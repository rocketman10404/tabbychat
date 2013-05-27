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
	public static int tabHeight = 20;
	public static int tabTrayHeight = 20;
	
	public static void setChatSize(int width, int height) {
		Minecraft mc = TabbyChat.mc;
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		
		width = Math.min(width + current.x, sr.getScaledWidth());
		
		if(width + current.x > sr.getScaledWidth()) width = sr.getScaledWidth() - current.x;
		
		if(-20 - current.y < height) height = -20 - current.y;
		height = Math.min(height + current.y, sr.getScaledHeight());
		
		current.y = current.y - (height - current.height);
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
	
	public static void updateTabs(LinkedHashMap<String, ChatChannel> chanObjs) {
		int tabWidth = 0;
		int tabX = current.x + 1;
		
		for(ChatChannel chan : chanObjs.values()) {
			tabWidth = TabbyChat.mc.fontRenderer.getStringWidth(chan.getDisplayTitle()) + 8;
			if(tabX + tabWidth > current.width - 5) {
				addRowToTray();
				int delta = current.y + tabTrayHeight - 19;
				for(ChatChannel chan2 : chanObjs.values()) {
					if(chan2 == chan) break;
					chan2.tab.yPosition = delta;
				}
			}
			
			if(chan.tab == null) {
				chan.setButtonObj(new ChatButton(chan.getID(), tabX, current.y + 1, tabWidth, 20, chan.getDisplayTitle()));
			} else {
				chan.tab.id = chan.getID();
				chan.tab.xPosition = tabX;
				chan.tab.yPosition = current.y + 1;
				chan.tab.width(tabWidth);
				chan.tab.displayString = chan.getDisplayTitle();
			}
			
			tabX = tabX + tabWidth + 1;
		}
	}
	
	public static void drawChatBoxBorder(Gui overlay) {
		overlay.drawRect(current.x, current.y, current.x+current.width, current.y+1, 0x55ffffff);
		overlay.drawRect(current.x, current.y+1, current.x+1, current.y+current.height-1, 0x55ffffff);
		overlay.drawRect(current.x+current.width-1, current.y+1, current.x+current.width, current.y+current.height-1, 0x55ffffff);
		overlay.drawRect(current.x, current.y+current.height-1, current.x+current.width, current.y+current.height, 0x55ffffff);
	}

}
