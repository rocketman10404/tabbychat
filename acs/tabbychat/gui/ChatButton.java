package acs.tabbychat.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import acs.tabbychat.core.ChatChannel;
import acs.tabbychat.core.TabbyChat;

import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiButton;

public class ChatButton extends net.minecraft.src.GuiButton {
	public ChatChannel channel;
	
	public ChatButton() {
		super(9999, 0, 0, 1, 1, "");
	}
	
	public ChatButton(int _id, int _x, int _y, int _w, int _h, String _title) {
		super(_id, _x, _y, _w, _h, _title);
	}
	
	public int width() {
		return this.width;
	}
	
	public void width(int _w) {
		this.width = _w;
	}
	
	public int height() {
		return this.height;
	}
	
	public void height(int _h) {
		this.height = _h;
	}
	
	public void clear() {
		this.channel = null;
	}
	
	public boolean mousePressed(Minecraft mc, int par2, int par3) {

        float scaleSetting = TabbyChat.gnc.getScaleSetting();
        int adjY = (int)((float)(mc.currentScreen.height - this.yPosition - 28) * (1.0F - scaleSetting)) + this.yPosition;
        int adjX = (int)((float)(this.xPosition - 5) * scaleSetting) + 5;
        int adjW = (int)((float)this.width * scaleSetting);
        int adjH = (int)((float)this.height * scaleSetting);
		
		return this.enabled && this.drawButton && par2 >= adjX && par3 >= adjY && par2 < adjX + adjW && par3 < adjY + adjH;
	}
	
	public void drawButton(Minecraft mc, int cursorX, int cursorY) {
	      if(this.drawButton) {
	          FontRenderer fr = mc.fontRenderer;
	          float _mult = mc.gameSettings.chatOpacity * 0.9F + 0.1F;
	          int _opacity = (int)((float)255 * _mult);
	          
	          float scaleSetting = TabbyChat.gnc.getScaleSetting();
	          int adjY = (int)((float)(mc.currentScreen.height - this.yPosition - 28) * (1.0F - scaleSetting)) + this.yPosition;
	          int adjX = (int)((float)(this.xPosition - 5) * scaleSetting) + 5;
	          int adjW = (int)((float)this.width * scaleSetting);
	          int adjH = (int)((float)this.height * scaleSetting);
	          
	          boolean hovered = cursorX >= adjX && cursorY >= adjY && cursorX < adjX + adjW && cursorY < adjY + adjH;

	          int var7 = 0xa0a0a0;
	          int var8 = 0;
	          if(!this.enabled) {
	        	  var7 = -0x5f5f60;
	          } else if(hovered) {
	             var7 = 0xffffa0;
	             var8 = 0x7f8052;
	          } else if(this.channel.active) {
	        	  var7 = 0xa5e7e4;
	        	  var8 = 0x5b7c7b;
	          } else if(this.channel.unread) {
	        	  var7 = 0xff0000;
	        	  var8 = 0x720000;
	          }
	          drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, var8 + (_opacity / 2 << 24));
	          GL11.glEnable(GL11.GL_BLEND);
	          if(hovered && Keyboard.isKeyDown(42)) {
	        	  String special = (this.channel.getTitle().equalsIgnoreCase("*") ? "\u2611" : "X");
	        	  this.drawCenteredString(fr, special, this.xPosition + this.width / 2, this.yPosition + (this.height-8) / 2, var7 + (_opacity << 24));
	          } else {
	        	  this.drawCenteredString(fr, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height-8) / 2, var7 + (_opacity << 24));
	          }
	       }		
	}
}
