package acs.tabbychat.gui;

import java.awt.Rectangle;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import acs.tabbychat.core.ChatChannel;
import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;

import net.minecraft.src.Minecraft;
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
	
	private static Rectangle translateButtonDims(Rectangle unscaled) {
		float scaleSetting = GuiNewChatTC.getInstance().getScaleSetting();
		int adjX = Math.round((unscaled.x - ChatBox.current.x) * scaleSetting + ChatBox.current.x);
		
		int adjY = Math.round((TabbyChat.mc.currentScreen.height - unscaled.y + ChatBox.current.y) * (1.0f - scaleSetting)) + unscaled.y;
		
		int adjW = Math.round(unscaled.width * scaleSetting);
		int adjH = Math.round(unscaled.height * scaleSetting);
		return new Rectangle(adjX, adjY, adjW, adjH);		
	}
	
	public boolean mousePressed(Minecraft mc, int par2, int par3) {		
		Rectangle cursor = translateButtonDims(new Rectangle(this.xPosition, this.yPosition, this.width, this.height));		
		return this.enabled && this.drawButton && par2 >= cursor.x && par3 >= cursor.y && par2 < cursor.x + cursor.width && par3 < cursor.y + cursor.height;
	}
	
	public void drawButton(Minecraft mc, int cursorX, int cursorY) {
	      if(this.drawButton) {
	          FontRenderer fr = mc.fontRenderer;
	          float _mult = mc.gameSettings.chatOpacity * 0.9F + 0.1F;
	          int _opacity = (int)((float)255 * _mult);
	          int textOpacity = (TabbyChat.advancedSettings.textIgnoreOpacity.getValue() ? 255 : _opacity);
	          
	          Rectangle cursor = translateButtonDims(new Rectangle(this.xPosition, this.yPosition, this.width, this.height));
	          
	          boolean hovered = cursorX >= cursor.x && cursorY >= cursor.y && cursorX < cursor.x + cursor.width && cursorY < cursor.y + cursor.height;

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
	        	  String special = (this.channel.getTitle().equalsIgnoreCase("*") ? "\u2398" : "\u26A0");
	        	  this.drawCenteredString(fr, special, this.xPosition + this.width / 2, this.yPosition + (this.height-8) / 2, var7 + (textOpacity << 24));
	          } else {
	        	  this.drawCenteredString(fr, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height-8) / 2, var7 + (textOpacity << 24));
	          }
	       }		
	}
}
