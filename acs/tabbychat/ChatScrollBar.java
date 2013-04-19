package acs.tabbychat;

import org.lwjgl.input.Mouse;
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
	}
	
	public void handleMouse() {
		int adjX = 0;
		int adjY = 0;
		
		if (Mouse.getEventButton() == 0 && Mouse.isButtonDown(0)) {
			adjX = Mouse.getEventX() * this.gc.width / this.mc.displayWidth;
			adjY = this.gc.height - Mouse.getEventY() * this.gc.height / this.mc.displayHeight - 1;
			if (Math.abs(adjX - this.barX) <= barWidth/2 && adjY <= this.barMaxY && adjY >= this.barMinY)
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
		int clines = (TabbyChat.gnc.GetChatHeight() < maxlines) ? TabbyChat.gnc.GetChatHeight() : maxlines;
		int oX = 0;
		if (TabbyChat.instance.generalSettings.timeStampEnable.getValue())
			oX = MathHelper.floor_float((float)mc.fontRenderer.getStringWidth(((TimeStampEnum)TabbyChat.instance.generalSettings.timeStampStyle.getValue()).maxTime) * TabbyChat.gnc.getScaleSetting());
		barHeight = MathHelper.floor_float((float)8 * TabbyChat.gnc.getScaleSetting());
		barWidth = MathHelper.floor_float((float)5 * TabbyChat.gnc.getScaleSetting());

		this.barX = 4 + this.offsetX + oX + (int)(TabbyChat.gnc.getWidthSetting() * TabbyChat.gnc.getScaleSetting());
				
		this.barMaxY = this.gc.height - 34 + this.offsetY;
		this.barMinY = this.barMaxY + 2 - MathHelper.floor_float((float)((clines - 1) * 9) * TabbyChat.gnc.getScaleSetting());		
		
		this.barTopY = this.barMinY + barHeight/2;
		this.barBottomY = this.barMaxY - barHeight/2;
		this.scrollBarCenter = Math.round(this.mouseLoc*this.barTopY + (1.0f-this.mouseLoc)*this.barBottomY);
	}
	
	public void drawScrollBar() {
		this.update();
		int minX = this.barX - (barWidth-1)/2;
		int maxlines = TabbyChat.gnc.getHeightSetting() / 9;
		if (TabbyChat.gnc.GetChatHeight() > maxlines)
			gc.drawRect(minX, this.scrollBarCenter - barHeight/2, minX + barWidth, this.scrollBarCenter + barHeight/2, 0x55ffffff);
		gc.drawRect(this.barX, this.barMinY, this.barX+1, this.barMaxY, 0x99ffffff);
	}
	
	public void scrollBarMouseWheel() {
		this.update();
		int maxlines = TabbyChat.gnc.getHeightSetting() / 9;
		int blines = TabbyChat.gnc.GetChatHeight();
		if (blines > maxlines)
			this.mouseLoc = (float)TabbyChat.gnc.chatLinesTraveled()/(blines-maxlines);
		else
			this.mouseLoc = 0f;
		   
		this.scrollBarCenter = Math.round(this.mouseLoc*this.barTopY + (1.0f-this.mouseLoc)*this.barBottomY);
	}
	
	public void scrollBarMouseDrag(int _absY) {
		int maxlines = TabbyChat.gnc.getHeightSetting() / 9;
		int blines = TabbyChat.gnc.GetChatHeight();
		if (blines <= maxlines) {
			this.mouseLoc = 0f;
			return;
		}
		
		if (_absY < this.barTopY)
			this.mouseLoc = 1.0f;
		else if (_absY > this.barBottomY)
			this.mouseLoc = 0.0f;
		else
			this.mouseLoc = ((float)(this.barBottomY - _absY))/(this.barBottomY - this.barTopY);
 
		float moveInc = 1.0f / (blines - 19);
		float settleInc = 1.0f / (blines - 20);
		
		int moveLines = (int) Math.floor(this.mouseLoc / moveInc);
		if (moveLines > blines - maxlines)
			moveLines = blines - maxlines;
		
		TabbyChat.gnc.setVisChatLines(moveLines);
		this.mouseLoc = settleInc * moveLines;
		this.scrollBarCenter = Math.round(this.mouseLoc*this.barTopY + (1.0f-this.mouseLoc)*this.barBottomY);		
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
