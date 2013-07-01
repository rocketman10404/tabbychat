package acs.tabbychat.gui;

import java.awt.Point;

import org.lwjgl.input.Mouse;

import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.TimeStampEnum;
import net.minecraft.src.Minecraft;
import net.minecraft.src.GuiChat;
import net.minecraft.src.MathHelper;

public class ChatScrollBar {
	private static Minecraft mc;
	private static GuiNewChatTC gnc = GuiNewChatTC.getInstance();
	private static float mouseLoc = 0.0f;
	private static int scrollBarCenter = 0;
	private static int barBottomY = 0;
	private static int barTopY = 0;
	private static int barX = 326;
	private static int barMinY = 0;
	private static int barMaxY = 0;
	private static int lastY = 0;
	protected static int barHeight = 5;
	protected static int barWidth = 5;
	private static boolean scrolling = false;
	
	public ChatScrollBar() {
		mc = Minecraft.getMinecraft();
		if(TabbyChat.generalSettings.timeStampEnable.getValue()) {
			String maxTime = ((TimeStampEnum)TabbyChat.generalSettings.timeStampStyle.getValue()).maxTime;
			boolean oldVal = mc.fontRenderer.getUnicodeFlag();
			if(TabbyChat.advancedSettings.forceUnicode.getValue()) {
				mc.fontRenderer.setUnicodeFlag(true);
			}
			mc.fontRenderer.setUnicodeFlag(oldVal);
		}
	}
	
	public static void handleMouse() {		
		Point cursor = ChatBox.scaleMouseCoords(Mouse.getEventX(), Mouse.getEventY());
		
		if (Mouse.getEventButton() == 0 && Mouse.isButtonDown(0)) {
			int offsetX = barX + ChatBox.current.x;
			int offsetY = ChatBox.current.y;
			if (cursor.x - offsetX > 0 && cursor.x - offsetX <= barWidth && cursor.y <= barMaxY + offsetY && cursor.y >= barMinY + offsetY) {
				scrolling = true;
			} else {
				scrolling = false;
			}
		} else if (!Mouse.isButtonDown(0)) {
			scrolling = false;
		}
		
		if (Math.abs(cursor.y - lastY) > 1 && scrolling) {
			scrollBarMouseDrag(cursor.y);
		}
	}

	private static void update() {
		int maxlines = gnc.getHeightSetting() / 9;
		int clines = Math.min(gnc.GetChatSize(), maxlines);

		barHeight = MathHelper.floor_float((float)5 * gnc.getScaleSetting());
		barWidth = MathHelper.floor_float((float)5 * gnc.getScaleSetting());

		barX = ChatBox.current.width - barWidth - 2;
		barBottomY = 0;
		
		if(ChatBox.anchoredTop) {
			barBottomY = ChatBox.current.height - ChatBox.tabTrayHeight;
			barTopY = 0;
		} else {
			barBottomY = 0;
			barTopY = -ChatBox.current.height + ChatBox.tabTrayHeight;
		}
		
		barMaxY = barBottomY - barHeight/2 - 1;
		barMinY = barTopY + barHeight/2 + 1;
		if(!ChatBox.anchoredTop) scrollBarCenter = Math.round(mouseLoc*barMinY + (1.0f-mouseLoc)*barMaxY);
		else scrollBarCenter = Math.round(mouseLoc*barMaxY + (1.0f-mouseLoc)*barMinY);
	}
	
	public static void drawScrollBar() {
		update();
		int minX = barX + 1;
		int maxlines = gnc.getHeightSetting() / 9;
		//float chatOpacity = mc.gameSettings.chatOpacity * 0.9f + 0.1f;
		float chatOpacity = gnc.tc.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
		int currentOpacity = (int)((float)180 * chatOpacity);
		gnc.drawRect(barX, barTopY, barX+barWidth+2, barBottomY, currentOpacity << 24);
		if (gnc.GetChatSize() > maxlines) {
			gnc.drawRect(minX, scrollBarCenter - barHeight/2, minX + barWidth, scrollBarCenter + barHeight/2, 0xffffff + (currentOpacity / 2 << 24));
			gnc.drawRect(minX + 1, scrollBarCenter - barHeight/2 - 1, minX + barWidth - 1, scrollBarCenter + barHeight/2 + 1, 0xffffff + (currentOpacity / 2 << 24));
		}
	}
	
	public static void scrollBarMouseWheel() {
		update();
		int maxlines = gnc.getHeightSetting() / 9;
		int blines = gnc.GetChatSize();
		if (blines > maxlines)
			mouseLoc = (float)gnc.chatLinesTraveled()/(blines-maxlines);
		else
			mouseLoc = 0f;
		   
		if(!ChatBox.anchoredTop) scrollBarCenter = Math.round(mouseLoc*barMinY + (1.0f-mouseLoc)*barMaxY);
		else scrollBarCenter = Math.round(mouseLoc*barMaxY + (1.0f-mouseLoc)*barMinY);
	}
	
	public static void scrollBarMouseDrag(int _absY) {
		int maxlines = gnc.getHeightSetting() / 9;
		int blines = gnc.GetChatSize();
		if (blines <= maxlines) {
			mouseLoc = 0f;
			return;
		}
		
		int adjBarMin = barMinY + ChatBox.current.y;
		int adjBarMax = barMaxY + ChatBox.current.y;

		
		if (_absY < adjBarMin)
			mouseLoc = ChatBox.anchoredTop ? 0.0f : 1.0f;
		else if (_absY > adjBarMax)
			mouseLoc = ChatBox.anchoredTop ? 1.0f : 0.0f;
		else {
			if(!ChatBox.anchoredTop) mouseLoc = Math.abs((float)(adjBarMax - _absY))/(adjBarMax - adjBarMin);
			else mouseLoc = Math.abs((float)(adjBarMin - _absY))/(adjBarMax - adjBarMin);
		}
		float moveInc = 1.0f / (blines - maxlines);
		
		int moveLines = (int)(mouseLoc / moveInc);
		if (moveLines > blines - maxlines)
			moveLines = blines - maxlines;
		
		gnc.setVisChatLines(moveLines);
		mouseLoc = moveInc * moveLines;
		if(!ChatBox.anchoredTop) scrollBarCenter = Math.round(mouseLoc*(barMinY-barMaxY) + barMaxY);
		else scrollBarCenter = Math.round(mouseLoc*(barMaxY-barMinY)+barMinY);
		lastY = _absY;	
	}
	
	public static void setOffset(int _x, int _y) {
		int maxlines = gnc.getHeightSetting() / 9;
		int clines = (gnc.GetChatSize() < maxlines) ? gnc.GetChatSize() : maxlines;
		barX = 324 + _x;
		barMinY = mc.currentScreen.height - ((clines-1) * 9 + 8) - 35 + _y;
		barTopY = barMinY + barHeight/2 + _y;
		barMaxY = mc.currentScreen.height - 45 + _y;
		barBottomY = barMaxY - barHeight/2 + _y;
	}
}
