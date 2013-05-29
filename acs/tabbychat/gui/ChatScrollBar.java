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
			//this.offsetX = MathHelper.floor_float(mc.fontRenderer.getStringWidth(maxTime) * TabbyChat.gnc.getScaleSetting());
			mc.fontRenderer.setUnicodeFlag(oldVal);
		}
	}
	
	public static void handleMouse() {
		int adjX = 0;
		int adjY = 0;
		
		if (Mouse.getEventButton() == 0 && Mouse.isButtonDown(0)) {
			adjX = Mouse.getEventX() * gc.width / mc.displayWidth;
			adjY = gc.height - Mouse.getEventY() * gc.height / mc.displayHeight - 1;
			if (Math.abs(adjX - barX) <= barWidth && adjY <= barMaxY && adjY >= barMinY)
				scrolling = true;
			else
				scrolling = false;
		} else if (!Mouse.isButtonDown(0))
			scrolling = false;
		
        int aY = gc.height - Mouse.getEventY() * gc.height / mc.displayHeight - 1;
		if (Math.abs(aY - lastY) > 1 && scrolling)
			scrollBarMouseDrag(aY);
	}

	private static void update() {
		int maxlines = TabbyChat.gnc.getHeightSetting() / 9;
		int clines = Math.min(TabbyChat.gnc.GetChatHeight(), maxlines);

		barHeight = MathHelper.floor_float((float)5 * TabbyChat.gnc.getScaleSetting());
		barWidth = MathHelper.floor_float((float)5 * TabbyChat.gnc.getScaleSetting());

		barX = ChatBox.current.width;
		barBottomY = 0;
		barTopY = barBottomY - ChatBox.getChatHeight();
		
		barMaxY = barBottomY - barHeight/2 - 1;
		barMinY = barTopY + barHeight/2 + 1;
		scrollBarCenter = Math.round(mouseLoc*barMinY + (1.0f-mouseLoc)*barMaxY);
	}
	
	public static void drawScrollBar() {
		update();
		int minX = barX + 1;
		int maxlines = gnc.getHeightSetting() / 9;
		float chatOpacity = mc.gameSettings.chatOpacity * 0.9f + 0.1f;
		int currentOpacity = (int)((float)180 * chatOpacity);
		gnc.drawRect(barX, barTopY, barX+barWidth+2, barBottomY, currentOpacity << 24);
		if (gnc.GetChatHeight() > maxlines) {
			gnc.drawRect(minX, scrollBarCenter - barHeight/2, minX + barWidth, scrollBarCenter + barHeight/2, 0xffffff + (currentOpacity / 2 << 24));
			gnc.drawRect(minX + 1, scrollBarCenter - barHeight/2 - 1, minX + barWidth - 1, scrollBarCenter + barHeight/2 + 1, 0xffffff + (currentOpacity / 2 << 24));
		}
	}
	
	public static void scrollBarMouseWheel() {
		update();
		int maxlines = gnc.getHeightSetting() / 9;
		int blines = gnc.GetChatHeight();
		if (blines > maxlines)
			mouseLoc = (float)gnc.chatLinesTraveled()/(blines-maxlines);
		else
			mouseLoc = 0f;
		   
		scrollBarCenter = Math.round(mouseLoc*barMinY + (1.0f-mouseLoc)*barMaxY);
	}
	
	public static void scrollBarMouseDrag(int _absY) {
		int maxlines = gnc.getHeightSetting() / 9;
		int blines = gnc.GetChatHeight();
		if (blines <= maxlines) {
			mouseLoc = 0f;
			return;
		}
		
		if (_absY < barMinY)
			mouseLoc = 1.0f;
		else if (_absY > barMaxY)
			mouseLoc = 0.0f;
		else
			mouseLoc = ((float)(barMaxY - _absY))/(barMaxY - barMinY);
 
		float moveInc = 1.0f / (blines - maxlines);
		
		int moveLines = (int) Math.floor(mouseLoc / moveInc);
		if (moveLines > blines - maxlines)
			moveLines = blines - maxlines;
		
		gnc.setVisChatLines(moveLines);
		mouseLoc = moveInc * moveLines;
		scrollBarCenter = Math.round(mouseLoc*barMinY + (1.0f-mouseLoc)*barMaxY);		
		lastY = _absY;	
	}
	
	public static void setOffset(int _x, int _y) {
		int maxlines = gnc.getHeightSetting() / 9;
		int clines = (gnc.GetChatHeight() < maxlines) ? gnc.GetChatHeight() : maxlines;
		barX = 324 + _x;
		barMinY = mc.currentScreen.height - ((clines-1) * 9 + 8) - 35 + _y;
		barTopY = barMinY + barHeight/2 + _y;
		barMaxY = mc.currentScreen.height - 45 + _y;
		barBottomY = barMaxY - barHeight/2 + _y;
	}
}
