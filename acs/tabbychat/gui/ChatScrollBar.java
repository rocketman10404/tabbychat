package acs.tabbychat.gui;

import org.lwjgl.input.Mouse;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.settings.TimeStampEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiChat;
import net.minecraft.src.MathHelper;

public class ChatScrollBar {
	private static Minecraft mc;
	private static GuiChat gc;
	private float mouseLoc = 0.0f;
	private int scrollBarCenter = 0;
	private int barBottomY = 0;
	private int barTopY = 0;
	private int barX = 326;
	private int barMinY = 0;
	private int barMaxY = 0;
	private int lastY = 0;
	private int offsetX = 0;
	private int offsetY = 0;
	private static int barHeight = 8;
	private static int barWidth = 5;
	private boolean scrolling = false;
	
	public ChatScrollBar(GuiChat _gc) {
		mc = Minecraft.getMinecraft();
		gc = _gc;
		
		if(TabbyChat.generalSettings.timeStampEnable.getValue()) {
			String maxTime = ((TimeStampEnum)TabbyChat.generalSettings.timeStampStyle.getValue()).maxTime;
			this.offsetX = MathHelper.floor_float(mc.fontRenderer.getStringWidth(maxTime) * TabbyChat.gnc.getScaleSetting());
		}
	}
	
	public void handleMouse() {
		int adjX = 0;
		int adjY = 0;
		
		if (Mouse.getEventButton() == 0 && Mouse.isButtonDown(0)) {
			adjX = Mouse.getEventX() * this.gc.width / this.mc.displayWidth;
			adjY = this.gc.height - Mouse.getEventY() * this.gc.height / this.mc.displayHeight - 1;
			if (Math.abs(adjX - this.barX) <= barWidth && adjY <= this.barMaxY && adjY >= this.barMinY)
				this.scrolling = true;
			else
				this.scrolling = false;
		} else if (!Mouse.isButtonDown(0))
			this.scrolling = false;
		
        int aY = this.gc.height - Mouse.getEventY() * this.gc.height / this.mc.displayHeight - 1;
		if (Math.abs(aY - this.lastY) > 1 && this.scrolling)
			this.scrollBarMouseDrag(aY);
	}

	private void update() {
		int maxlines = TabbyChat.gnc.getHeightSetting() / 9;
		int clines = Math.min(TabbyChat.gnc.GetChatHeight(), maxlines);

		barHeight = MathHelper.floor_float((float)5 * TabbyChat.gnc.getScaleSetting());
		barWidth = MathHelper.floor_float((float)5 * TabbyChat.gnc.getScaleSetting());

		this.barX = 5 + this.offsetX + (int)(TabbyChat.gnc.getWidthSetting() * TabbyChat.gnc.getScaleSetting());
		this.barBottomY = this.gc.height - 28 + this.offsetY;
		this.barTopY = this.barBottomY - MathHelper.floor_float((TabbyChat.instance.gnc.chatHeight+9) * TabbyChat.gnc.getScaleSetting());
		
		this.barMaxY = this.barBottomY - barHeight/2 - 1;
		this.barMinY = this.barTopY + barHeight/2 + 1;
		this.scrollBarCenter = Math.round(this.mouseLoc*this.barMinY + (1.0f-this.mouseLoc)*this.barMaxY);
	}
	
	public void drawScrollBar() {
		this.update();
		int minX = this.barX + 1;
		int maxlines = TabbyChat.gnc.getHeightSetting() / 9;
		float chatOpacity = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
		int currentOpacity = (int)((float)180 * chatOpacity);
		if (TabbyChat.gnc.GetChatHeight() > maxlines) {
			gc.drawRect(this.barX, this.barTopY, this.barX+7, this.barBottomY, currentOpacity / 2 << 24);
			gc.drawRect(minX, this.scrollBarCenter - barHeight/2, minX + barWidth, this.scrollBarCenter + barHeight/2, 0xffffff + (currentOpacity / 2 << 24));
			gc.drawRect(minX + 1, this.scrollBarCenter - barHeight/2 - 1, minX + barWidth - 1, this.scrollBarCenter + barHeight/2 + 1, 0xffffff + (currentOpacity / 2 << 24));
		}
	}
	
	public void scrollBarMouseWheel() {
		this.update();
		int maxlines = TabbyChat.gnc.getHeightSetting() / 9;
		int blines = TabbyChat.gnc.GetChatHeight();
		if (blines > maxlines)
			this.mouseLoc = (float)TabbyChat.gnc.chatLinesTraveled()/(blines-maxlines);
		else
			this.mouseLoc = 0f;
		   
		this.scrollBarCenter = Math.round(this.mouseLoc*this.barMinY + (1.0f-this.mouseLoc)*this.barMaxY);
	}
	
	public void scrollBarMouseDrag(int _absY) {
		int maxlines = TabbyChat.gnc.getHeightSetting() / 9;
		int blines = TabbyChat.gnc.GetChatHeight();
		if (blines <= maxlines) {
			this.mouseLoc = 0f;
			return;
		}
		
		if (_absY < this.barMinY)
			this.mouseLoc = 1.0f;
		else if (_absY > this.barMaxY)
			this.mouseLoc = 0.0f;
		else
			this.mouseLoc = ((float)(this.barMaxY - _absY))/(this.barMaxY - this.barMinY);
 
		float moveInc = 1.0f / (blines - maxlines);
		
		int moveLines = (int) Math.floor(this.mouseLoc / moveInc);
		if (moveLines > blines - maxlines)
			moveLines = blines - maxlines;
		
		TabbyChat.gnc.setVisChatLines(moveLines);
		this.mouseLoc = moveInc * moveLines;
		this.scrollBarCenter = Math.round(this.mouseLoc*this.barMinY + (1.0f-this.mouseLoc)*this.barMaxY);		
		this.lastY = _absY;	
	}
	
	public void setOffset(int _x, int _y) {
		this.offsetX = _x;
		this.offsetY = _y;
		int maxlines = TabbyChat.gnc.getHeightSetting() / 9;
		int clines = (TabbyChat.gnc.GetChatHeight() < maxlines) ? TabbyChat.gnc.GetChatHeight() : maxlines;
		this.barX = 324 + _x;
		this.barMinY = mc.currentScreen.height - ((clines-1) * 9 + 8) - 35 + _y;
		this.barTopY = this.barMinY + barHeight/2 + _y;
		this.barMaxY = mc.currentScreen.height - 45 + _y;
		this.barBottomY = this.barMaxY - barHeight/2 + _y;
	}
}
