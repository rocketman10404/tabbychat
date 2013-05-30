package acs.tabbychat.gui;

import org.lwjgl.input.Mouse;

import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.TimeStampEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiChat;
import net.minecraft.src.MathHelper;

public class ChatScrollBar {
	private static Minecraft mc;
	private static GuiChat gc;
	private static GuiNewChatTC gnc = TabbyChat.instance.gnc;
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
	
	public ChatScrollBar(GuiChat _gc) {
		mc = Minecraft.getMinecraft();
		gc = _gc;
		
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
		int adjX = 0;
		int adjY = -Mouse.getEventY() * gc.height / mc.displayHeight - 1;
		
		if (Mouse.getEventButton() == 0 && Mouse.isButtonDown(0)) {
			adjX = Mouse.getEventX() * gc.width / mc.displayWidth;
			int offsetX = barX + ChatBox.current.x;
			int offsetY = ChatBox.current.y + ChatBox.current.height;
			if (adjX - offsetX > 0 && adjX - offsetX <= barWidth && adjY <= barMaxY + offsetY && adjY >= barMinY + offsetY) {
				scrolling = true;
			} else {
				scrolling = false;
			}
		} else if (!Mouse.isButtonDown(0)) {
			scrolling = false;
		}
		
		if (Math.abs(adjY - lastY) > 1 && scrolling) {
			scrollBarMouseDrag(adjY);
		}
	}

	private static void update() {
		int maxlines = TabbyChat.gnc.getHeightSetting() / 9;
		int clines = Math.min(TabbyChat.gnc.GetChatSize(), maxlines);

		barHeight = MathHelper.floor_float((float)5 * TabbyChat.gnc.getScaleSetting());
		barWidth = MathHelper.floor_float((float)5 * TabbyChat.gnc.getScaleSetting());

		barX = ChatBox.current.width;
		barBottomY = 0;
		if(ChatBox.anchoredTop) barBottomY -= ChatBox.tabTrayHeight;
		barTopY = barBottomY - ChatBox.getChatHeight();
		if(ChatBox.anchoredTop) barTopY -= 1;
		
		barMaxY = barBottomY - barHeight/2 - 1;
		barMinY = barTopY + barHeight/2 + 1;
		if(!ChatBox.anchoredTop) scrollBarCenter = Math.round(mouseLoc*barMinY + (1.0f-mouseLoc)*barMaxY);
		else scrollBarCenter = Math.round(mouseLoc*barMaxY + (1.0f-mouseLoc)*barMinY);
	}
	
	public static void drawScrollBar() {
		update();
		int minX = barX + 1;
		int maxlines = gnc.getHeightSetting() / 9;
		float chatOpacity = mc.gameSettings.chatOpacity * 0.9f + 0.1f;
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
		
		int adjBarMin = barMinY + ChatBox.current.y + ChatBox.current.height;
		int adjBarMax = barMaxY + ChatBox.current.y + ChatBox.current.height;
		
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
