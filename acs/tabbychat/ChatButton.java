package acs.tabbychat;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiButton;

public class ChatButton extends net.minecraft.src.GuiButton {
	protected ChatChannel channel;
	
	public ChatButton() {
		super(9999, 0, 0, 1, 1, "");
	}
	
	public ChatButton(int _id, int _x, int _y, int _w, int _h, String _title) {
		super(_id, _x, _y, _w, _h, _title);
	}
	
	protected int width() {
		return this.width;
	}
	
	protected void width(int _w) {
		this.width = _w;
	}
	
	protected int height() {
		return this.height;
	}
	
	protected void height(int _h) {
		this.height = _h;
	}
	
	public void clear() {
		this.channel = null;
	}
	
	public void drawButton(Minecraft mc, int cursorX, int cursorY) {
	      if(this.drawButton) {
	          FontRenderer fr = mc.fontRenderer;
	          // float var8 = mc.gameSettings.chatOpacity * 0.9F + 0.1F; // don't know what this is yet
	          // int var9 = mc.fontRenderer.FONT_HEIGHT; // probably want this too
              drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0x66000000);
	          boolean hovered = cursorX >= this.xPosition && cursorY >= this.yPosition && cursorX < this.xPosition + this.width && cursorY < this.yPosition + this.height;

	          int var7 = 0xa0a0a0;
	          if(!this.enabled) {
	        	  var7 = -0x5f5f60;
	          } else if(hovered) {
	             var7 = 0xffffa0;
	          } else if(this.channel.active) {
	        	  var7 = 0xa5e7e4;
	          } else if(this.channel.unread) {
	        	  var7 = 0xff0000;
	          }

	          this.drawCenteredString(fr, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, var7);
	       }		
	}
}
