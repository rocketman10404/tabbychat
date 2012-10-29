package acs.tabbychat;

import org.lwjgl.input.Mouse;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiChat;

public class ChatScrollBar {
	private static Minecraft mc;
	private static GuiChat gc;
	private float mouseLoc = 0.0f;
	private int scrollBarCenter = 0;
	private int barBottomY = 0;
	private int barTopY = 0;
	private int barX = 320;
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
		int clines = (mc.ingameGUI.getChatGUI().GetChatHeight() < 20) ? mc.ingameGUI.getChatGUI().GetChatHeight() : 20;
		int oX = 0;
		if (TabbyChat.instance.globalPrefs.timestampsEnabled)
			oX = mc.fontRenderer.getStringWidth(TabbyChat.instance.globalPrefs.timestampStyle.maxTime);
		this.barX = 320 + this.offsetX + oX;
		this.barMinY = mc.currentScreen.height - ((clines-1) * 9 + 8) - 35 + this.offsetY;
		this.barMaxY = mc.currentScreen.height - 45 + this.offsetY;
		this.barTopY = this.barMinY + barHeight/2;
		this.barBottomY = this.barMaxY - barHeight/2;
		this.scrollBarCenter = Math.round(this.mouseLoc*this.barTopY + (1.0f-this.mouseLoc)*this.barBottomY);
	}
	
	public void drawScrollBar() {
		this.update();
		int minX = this.barX - (barWidth-1)/2;
		if (mc.ingameGUI.getChatGUI().GetChatHeight() > 20)
			gc.drawRect(minX, this.scrollBarCenter - barHeight/2, minX + barWidth, this.scrollBarCenter + barHeight/2, 0x55ffffff);
		gc.drawRect(this.barX, this.barMinY, this.barX+1, this.barMaxY, 0x99ffffff);
	}
	
	public void scrollBarMouseWheel() {
		this.update();
		int blines = mc.ingameGUI.getChatGUI().GetChatHeight();
		if (blines > 20)
			this.mouseLoc = (float)mc.ingameGUI.getChatGUI().chatLinesTraveled()/(blines-20);
		else
			this.mouseLoc = 0f;
		   
		this.scrollBarCenter = Math.round(this.mouseLoc*this.barTopY + (1.0f-this.mouseLoc)*this.barBottomY);
	}
	
	public void scrollBarMouseDrag(int _absY) {
		int blines = mc.ingameGUI.getChatGUI().GetChatHeight();
		if (blines <= 20) {
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
		if (moveLines > blines - 20)
			moveLines = blines - 20;
		
		mc.ingameGUI.getChatGUI().setVisChatLines(moveLines);
		this.mouseLoc = settleInc * moveLines;
		this.scrollBarCenter = Math.round(this.mouseLoc*this.barTopY + (1.0f-this.mouseLoc)*this.barBottomY);		
		this.lastY = _absY;	
	}
	
	public void setOffset(int _x, int _y) {
		this.offsetX = _x;
		this.offsetY = _y;
		int clines = (mc.ingameGUI.getChatGUI().GetChatHeight() < 20) ? mc.ingameGUI.getChatGUI().GetChatHeight() : 20;
		this.barX = 320 + _x;
		this.barMinY = mc.currentScreen.height - ((clines-1) * 9 + 8) - 35 + _y;
		this.barTopY = this.barMinY + barHeight/2 + _y;
		this.barMaxY = mc.currentScreen.height - 45 + _y;
		this.barBottomY = this.barMaxY - barHeight/2 + _y;
	}
}
