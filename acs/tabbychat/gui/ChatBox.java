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
	private static int absMinW = 100;
	private static int absMinH = 23;
	public static int tabHeight = 14;
	public static int tabTrayHeight = 14;
	public static int unfocusedHeight = 180;
	public static boolean dragging = false;
	public static Point dragStart =  new Point(0,0);
	public static boolean resizing = false;
	
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
			
			// Draw border between focused chatbox and tab tray
			overlay.drawRect(0, -current.height+tabTrayHeight, current.width+ChatScrollBar.barWidth+2, -current.height+tabTrayHeight+1, borderColor);
			
			// Add shading to tab tray
			overlay.drawRect(0, -current.height, current.width+ChatScrollBar.barWidth+2, -current.height+tabTrayHeight, trayColor);
			
			// Draw handle for mouse drag
			overlay.drawRect(current.width+ChatScrollBar.barWidth-5, -current.height+2, current.width+ChatScrollBar.barWidth, -current.height+3, handleColor);
			overlay.drawRect(current.width+ChatScrollBar.barWidth-1, -current.height+3, current.width+ChatScrollBar.barWidth, -current.height+7, handleColor);
			
		} else {
			// Draw border around unfocused chatbox
			overlay.drawRect(-1, -unfocusedHeight+tabTrayHeight, current.width+1, -unfocusedHeight+tabTrayHeight+1, borderColor);
			overlay.drawRect(-1, -unfocusedHeight+tabTrayHeight+1, 0, 0, borderColor);
			overlay.drawRect(current.width, -unfocusedHeight+tabTrayHeight+1, current.width+1, 0, borderColor);
			overlay.drawRect(-1, 0, current.width+1, 1, borderColor);	
			
		}
	}
	
	public static int getChatHeight() {
		return current.height - tabTrayHeight - 1;
	}
	
	public static int getChatWidth() {
		return current.width;
	}
	
	public static void handleMouseDrag(int _curX, int _curY, ScaledResolution sr) {
		if(!dragging) return;
		if(Math.abs(_curX - dragStart.x) < 3 && Math.abs(_curY - dragStart.y) < 3) return;
		
		float scaleSetting = TabbyChat.gnc.getScaleSetting();
		int scaledWidth = (int)(sr.getScaledWidth() / scaleSetting);
		int scaledHeight = (int)(sr.getScaledHeight() / scaleSetting);
				
		desired.x = current.x + _curX - dragStart.x;
		desired.y = current.y + _curY - dragStart.y;
		
		if(desired.x < absMinX+1) current.x = absMinX+1;
		else if(desired.x + current.width + ChatScrollBar.barWidth + 3 > scaledWidth) current.x = scaledWidth - current.width - ChatScrollBar.barWidth - 3;
		else current.x = desired.x;
		
		if(desired.y < -scaledHeight + 1) current.y = -scaledHeight + 1;
		else if(desired.y + current.height + 1 >= absMinY) {
			current.y = absMinY - current.height - 1;
		} else current.y = desired.y;		
		
		System.out.print("Actual width: "+sr.getScaledWidth()+" -- Virtual width: "+scaledWidth+" -- Right border: "+(current.x+current.width));
		System.out.println(" -- Screen width: "+TabbyChat.mc.currentScreen.width+" -- Scaled sWidth: "+(TabbyChat.mc.currentScreen.width / scaleSetting)+" -- Scale factor: "+sr.getScaleFactor());
		
		dragStart.setLocation(_curX, _curY);
	}
	
	public static void handleMouseResize(int _curX, int _curY, ScaledResolution sr) {
		if(!resizing) return;
		if(Math.abs(_curX - dragStart.x) < 3 && Math.abs(_curY - dragStart.y) < 3) return;
		
		float scaleSetting = TabbyChat.gnc.getScaleSetting();
		int scaledWidth = (int)(sr.getScaledWidth() / scaleSetting);
		int scaledHeight = (int)(sr.getScaledHeight() / scaleSetting);
		
		desired.width = current.width + _curX - dragStart.x;
		desired.height = current.height - _curY + dragStart.y;
		desired.y = current.y + _curY - dragStart.y;
	
		if(desired.x + desired.width + ChatScrollBar.barWidth + 3 > scaledWidth) {
			current.width = scaledWidth - current.x - ChatScrollBar.barWidth - 3;
		} else {
			current.width = Math.max(desired.width, absMinW);
		}
	
		if(desired.y < -scaledHeight + 1) {
			current.height += current.y + scaledHeight - 1;
			current.y = -scaledHeight + 1;
		} else {
			current.y -= Math.max(desired.height, absMinH) - current.height;
			current.height = Math.max(desired.height, absMinH);
		}
		
		dragStart.setLocation(_curX, _curY);
	}
	
	public static boolean resizeHovered() {
		boolean resizeHovered = false;
		boolean chatOpen = TabbyChat.gnc.getChatOpen();
		
		// Check for mouse cursor over resize handle
		GuiScreen theScreen = TabbyChat.mc.currentScreen;
		if(chatOpen && theScreen != null) {
		    int mx = Mouse.getX() * theScreen.width / TabbyChat.mc.displayWidth;
		    int my =  -Mouse.getY() * theScreen.height / TabbyChat.mc.displayHeight - 1;
		    
		    Rectangle scaled = getScaledBounds(new Rectangle(current.x+current.width+ChatScrollBar.barWidth-6, current.y, 8, 8));
		    if(mx > scaled.x && mx < scaled.x + scaled.width && my > scaled.y && my < scaled.y + scaled.height) {
		    	resizeHovered = true;
		    }
		}
		return resizeHovered;
	}
	
	public static void setChatSize(int width, int height) {
		Minecraft mc = TabbyChat.mc;
		ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int bottomY = current.y + current.height;
		
		float scaleSetting = TabbyChat.gnc.getScaleSetting();
		int scaledWidth = (int)(sr.getScaledWidth() / scaleSetting);
		int scaledHeight = (int)(sr.getScaledHeight() / scaleSetting);
		
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
			current.y = bottomY - height - tabTrayHeight - 1;
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
		dragStart = new Point(atX, atY);
	}
	
	public static void startResizing(int atX, int atY) {
		dragging = false;
		resizing = true;
		dragStart = new Point(atX, atY);
	}
	
	public static boolean tabTrayHovered(int mx, int my) {
		boolean chatOpen = TabbyChat.gnc.getChatOpen();
		GuiScreen theScreen = TabbyChat.mc.currentScreen;
		if(!chatOpen || theScreen == null) return false;
		
		my = my - theScreen.height;
		
		Rectangle scaled = getScaledBounds(new Rectangle(current.x, current.y, current.width, tabTrayHeight));
		
		return (mx > scaled.x && mx < scaled.x + scaled.width && my > scaled.y && my < scaled.y + scaled.height);
	}

	private static Rectangle getScaledBounds(Rectangle unscaled) {
		Rectangle scaled = new Rectangle();
		float scaleSetting = TabbyChat.gnc.getScaleSetting();
		
		scaled.x = Math.round((unscaled.x - current.x) * scaleSetting) + current.x;
		scaled.y = Math.round((unscaled.y - current.y - current.height) * scaleSetting) + current.y + current.height;
		scaled.width = Math.round(unscaled.width * scaleSetting);
		scaled.height = Math.round(unscaled.height * scaleSetting);
		
		return scaled;
	}
	
	public static void updateTabs(LinkedHashMap<String, ChatChannel> chanObjs, ScaledResolution sr) {
		int tabWidth = 0;
		float scaleSetting = TabbyChat.gnc.getScaleSetting();
		//int tabX = Math.round(current.x / scaleSetting);
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
				chan.setButtonObj(new ChatButton(chan.getID(), tabX+tabDx, sr.getScaledHeight()+current.y, tabWidth, tabHeight, chan.getDisplayTitle()));
			} else {
				chan.tab.id = chan.getID();
				chan.tab.xPosition = tabX + tabDx;
				chan.tab.yPosition = sr.getScaledHeight() + current.y;
				chan.tab.width(tabWidth);
				chan.tab.height(tabHeight);
				chan.tab.displayString = chan.getDisplayTitle();
			}
			tabDx += tabWidth + 1;
		}
	}
}
