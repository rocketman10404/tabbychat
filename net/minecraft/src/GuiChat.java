package net.minecraft.src;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import acs.tabbychat.ChatChannel;
import acs.tabbychat.TabbyChat;
import acs.tabbychat.TabbyChatUtils;
import acs.tabbychat.ChatScrollBar;
import acs.tabbychat.ChatButton;

public class GuiChat extends GuiScreen {
   private String field_73898_b = "";
   private int sentHistoryCursor = -1;
   private boolean field_73897_d = false;
   private boolean field_73905_m = false;
   private int field_73903_n = 0;
   private List field_73904_o = new ArrayList();
   private URI clickedURI = null;
   protected GuiTextField inputField;
   private List<GuiTextField> inputList = new ArrayList<GuiTextField>(3);
   private String defaultInputFieldText = "";
   public ChatScrollBar scrollBar;

   public GuiChat() { }

   public GuiChat(String par1Str) {
      this.defaultInputFieldText = par1Str;
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      /**** modded here ****/
      this.buttonList.clear();
      this.inputList.clear();
      TabbyChat.instance.checkServer();
      if (TabbyChat.instance.enabled()) {
    	  this.drawChatTabs();
    	  if (this.scrollBar == null)
    		  this.scrollBar = new ChatScrollBar(this);
    	  this.scrollBar.drawScrollBar();
      } else if (!Minecraft.getMinecraft().isSingleplayer()) {
    	  TabbyChat.instance.updateButtonLocations();
    	  this.buttonList.add(TabbyChat.instance.channelMap.get("*").tab);
      }
      
      this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      this.inputField = new GuiTextField(this.fontRenderer, 4, this.height - 12, this.width - 4, 12);
      this.inputField.setMaxStringLength(500);
      this.inputField.setEnableBackgroundDrawing(false);
      this.inputField.setFocused(true);
      this.inputField.setText(this.defaultInputFieldText);
      this.inputField.setCanLoseFocus(true);
      this.inputList.add(0, this.inputField);
      
      GuiTextField placeholder;
      for (int i=1; i<3; i++) {
    	  placeholder = new GuiTextField(this.fontRenderer, 4, this.height - 12*(i+1), this.width, 12);
    	  placeholder.setMaxStringLength(500);
    	  placeholder.setEnableBackgroundDrawing(false);
    	  placeholder.setFocused(false);
    	  placeholder.setText("");
    	  placeholder.setCanLoseFocus(true);
    	  placeholder.setVisible(false);
    	  this.inputList.add(i, placeholder);
      }
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      this.mc.ingameGUI.getChatGUI().resetScroll();
   }

   public void updateScreen() {
      this.inputField.updateCursorCounter();
   }
   
   protected void keyTyped(char par1, int par2) {
      this.field_73905_m = false;
      if(par2 == 15) {
         this.completePlayerName();
      } else {
         this.field_73897_d = false;
      }
      
      if(par2 == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if(par2 == 28) {
    	  /**** modded here *****/
    	 StringBuilder msg = new StringBuilder();
    	 for (int i=this.inputList.size()-1; i>=0; i-=1)
    		 msg.append(this.inputList.get(i).getText());
    	 
         if(msg.toString().length() > 0) {
        	 TabbyChatUtils.writeLargeChat(msg.toString());
        	 for (int j=1; j<this.inputList.size(); j++) {
        		 this.inputList.get(j).setText("");
        		 this.inputList.get(j).setVisible(false);
        	 }
         }
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if(par2 == Keyboard.KEY_UP) {
    	  if (GuiScreen.isCtrlKeyDown())
    		  this.getSentHistory(-1);
    	  /*** modded here ***/
    	  else {
    		  int foc = this.getFocusedFieldInd();
    		  if (foc+1<this.inputList.size() && this.inputList.get(foc+1).getVisible()) {
    			  int gcp = this.inputList.get(foc).getCursorPosition();
    			  int lng = this.inputList.get(foc+1).getText().length();
    			  int newPos = (gcp >= lng) ? lng : gcp;
    			  this.inputList.get(foc).setFocused(false);
    			  this.inputList.get(foc+1).setFocused(true);
    			  this.inputList.get(foc+1).setCursorPosition(newPos);
    		  } else
    			  this.getSentHistory(-1);
    	  }
      } else if(par2 == Keyboard.KEY_DOWN) {
    	  if (GuiScreen.isCtrlKeyDown())
        	 this.getSentHistory(1);
    	  /*** modded here ***/
    	  else {
    		  int foc = this.getFocusedFieldInd();
    		  if (foc-1>=0 && this.inputList.get(foc-1).getVisible()) {
    			  int gcp = this.inputList.get(foc).getCursorPosition();
    			  int lng = this.inputList.get(foc-1).getText().length();
    			  int newPos = (gcp >= lng) ? lng : gcp;
    			  this.inputList.get(foc).setFocused(false);
    			  this.inputList.get(foc-1).setFocused(true);
    			  this.inputList.get(foc-1).setCursorPosition(newPos);
    		  } else
    			  this.getSentHistory(1);
    	  }
      } else if(par2 == 201) {
         this.mc.ingameGUI.getChatGUI().scroll(19);
         if (TabbyChat.instance.enabled()) {
        	 this.scrollBar.scrollBarMouseWheel();
         }
      } else if(par2 == 209) {
         this.mc.ingameGUI.getChatGUI().scroll(-19);
         if (TabbyChat.instance.enabled()) {
        	 this.scrollBar.scrollBarMouseWheel();
         }
      } else {
    	  if (par2 == Keyboard.KEY_BACK) {
    		  if (this.inputField.isFocused() && this.inputField.getCursorPosition() > 0)
    			  this.inputField.textboxKeyTyped(par1, par2);
    		  else
    			  this.removeCharsAtCursor(-1);
    	  } else if (par2 == Keyboard.KEY_DELETE) {
    		  if (this.inputField.isFocused())
    			  this.inputField.textboxKeyTyped(par1, par2);
    		  else
    			  this.removeCharsAtCursor(1);
    	  } else if (par2 == Keyboard.KEY_LEFT || par2 == Keyboard.KEY_RIGHT) {
    		  this.inputList.get(this.getFocusedFieldInd()).textboxKeyTyped(par1, par2);    		  
    	  } else if (this.inputField.isFocused() && mc.fontRenderer.getStringWidth(this.inputField.getText()) < mc.ingameGUI.getChatGUI().me.sr.getScaledWidth()-20) {
   			  this.inputField.textboxKeyTyped(par1, par2);
   		  } else {
   			  this.insertCharsAtCursor(((Character)Keyboard.getEventCharacter()).toString());
   		  }   		  
      }
   }
   
   public void handleMouseInput() {
      super.handleMouseInput();
      int var1 = Mouse.getEventDWheel();
      if(var1 != 0) {
         if(var1 > 1) {
            var1 = 1;
         }

         if(var1 < -1) {
            var1 = -1;
         }

         if(!isShiftKeyDown()) {
            var1 *= 7;
         }

         this.mc.ingameGUI.getChatGUI().scroll(var1);
         /**** modded here ****/
         if (TabbyChat.instance.enabled())
        	 this.scrollBar.scrollBarMouseWheel();
      } else if (TabbyChat.instance.enabled())
    	  this.scrollBar.handleMouse();
   }

   protected void mouseClicked(int par1, int par2, int par3) {
	   if(par3 == 0 && this.mc.gameSettings.chatLinks) {
		   ChatClickData var4 = this.mc.ingameGUI.getChatGUI().func_73766_a(Mouse.getX(), Mouse.getY());
		   if(var4 != null) {
			   URI var5 = var4.getURI();
			   if(var5 != null) {
				   if(this.mc.gameSettings.chatLinksPrompt) {
					   this.clickedURI = var5;
					   this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, var4.getClickedUrl(), 0));
				   } else {
					   this.func_73896_a(var5);
				   }
				   
				   return;
			   }
		   }
	   }
	   
	   for (int i=0; i<this.inputList.size(); i++) {
		   if (par2 >= this.height - 12 * (i+1) && this.inputList.get(i).getVisible()) {
			   this.inputList.get(i).setFocused(true);
			   for (GuiTextField field : this.inputList) {
				   if (field != this.inputList.get(i))
					   field.setFocused(false);
			   }
			   this.inputList.get(i).mouseClicked(par1, par2, par3);
			   break;
		   }
	   }
	   super.mouseClicked(par1, par2, par3);
   }

   public void confirmClicked(boolean par1, int par2) {
      if(par2 == 0) {
         if(par1) {
            this.func_73896_a(this.clickedURI);
         }

         this.clickedURI = null;
         this.mc.displayGuiScreen(this);
      }
   }

   private void func_73896_a(URI par1URI) {
      try {
         Class var2 = Class.forName("java.awt.Desktop");
         Object var3 = var2.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
         var2.getMethod("browse", new Class[]{URI.class}).invoke(var3, new Object[]{par1URI});
      } catch (Throwable var4) {
         var4.printStackTrace();
      }
   }

   public void completePlayerName() {
      String var3;
      if(this.field_73897_d) {
         this.inputField.deleteFromCursor(this.inputField.func_73798_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
         if(this.field_73903_n >= this.field_73904_o.size()) {
            this.field_73903_n = 0;
         }
      } else {
         int var1 = this.inputField.func_73798_a(-1, this.inputField.getCursorPosition(), false);
         this.field_73904_o.clear();
         this.field_73903_n = 0;
         String var2 = this.inputField.getText().substring(var1).toLowerCase();
         var3 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
         this.func_73893_a(var3, var2);
         if(this.field_73904_o.isEmpty()) {
            return;
         }

         this.field_73897_d = true;
         this.inputField.deleteFromCursor(var1 - this.inputField.getCursorPosition());
      }

      if(this.field_73904_o.size() > 1) {
         StringBuilder var4 = new StringBuilder();

         for(Iterator var5 = this.field_73904_o.iterator(); var5.hasNext(); var4.append(var3)) {
            var3 = (String)var5.next();
            if(var4.length() > 0) {
               var4.append(", ");
            }
         }

         this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(var4.toString(), 1);
      }

      this.inputField.writeText((String)this.field_73904_o.get(this.field_73903_n++));
   }

   private void func_73893_a(String par1Str, String par2Str) {
      if(par1Str.length() >= 1) {
         this.mc.thePlayer.sendQueue.addToSendQueue(new Packet203AutoComplete(par1Str));
         this.field_73905_m = true;
      }
   }

   public void getSentHistory(int par1) {
      int var2 = this.sentHistoryCursor + par1;
      int var3 = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      if(var2 < 0) {
         var2 = 0;
      }

      if(var2 > var3) {
         var2 = var3;
      }

      if(var2 != this.sentHistoryCursor) {
         if(var2 == var3) {
            this.sentHistoryCursor = var3;
            //this.inputField.setText(this.field_73898_b);
            /*** modded here ***/
            this.setText(new StringBuilder(""), 1);
         } else {
            if(this.sentHistoryCursor == var3) {
               this.field_73898_b = this.inputField.getText();
            }

            //this.inputField.setText((String)this.mc.ingameGUI.getChatGUI().getSentMessages().get(var2));
            /*** modded here ***/
            StringBuilder _sb = new StringBuilder((String)this.mc.ingameGUI.getChatGUI().getSentMessages().get(var2));
            this.setText(_sb, _sb.length());
            
            this.sentHistoryCursor = var2;
         }
      }
   }

   public void drawScreen(int par1, int par2, float par3) {
	  int tBoxHeight = 0;
	  int _s = this.inputList.size();
	  for (int i=0; i<_s; i++) {
		  if (this.inputList.get(i).getVisible())
			  tBoxHeight += 12;
	  }
      drawRect(2, this.height - 2 - tBoxHeight, this.width - 2, this.height - 2, Integer.MIN_VALUE);
      for (GuiTextField field : this.inputList) {
    	  if (field.getVisible())
    		  field.drawTextBox();
      }
      String sends = ((Integer)this.getCurrentSends()).toString();
      int sendsX = mc.ingameGUI.getChatGUI().me.sr.getScaledWidth() - mc.fontRenderer.getStringWidth(sends) - 2;
      mc.fontRenderer.drawStringWithShadow(sends, sendsX, this.height - tBoxHeight, 7368816);
        
      /*** modded here ***/
      if (!Minecraft.getMinecraft().isSingleplayer())
    	  this.drawChatTabs();
      if (TabbyChat.instance.enabled()) {
    	  this.scrollBar.drawScrollBar();
      }
      float scaleSetting = mc.ingameGUI.getChatGUI().me.getScaleSetting();
      GL11.glPushMatrix();
      float scaleOffset = (float)(mc.ingameGUI.getChatGUI().me.sr.getScaledHeight() - 28) * (1.0F - scaleSetting);
      GL11.glTranslatef(0.0F, scaleOffset, 0.0F);
      GL11.glScalef(scaleSetting, scaleSetting, 1.0F);
      for(int var4 = 0; var4 < this.buttonList.size(); ++var4) {
          GuiButton var5 = (GuiButton)this.buttonList.get(var4);
          var5.drawButton(this.mc, par1, par2);
       }
      GL11.glPopMatrix();
   }

   public void func_73894_a(String[] par1ArrayOfStr) {
      if(this.field_73905_m) {
         this.field_73904_o.clear();
         String[] var2 = par1ArrayOfStr;
         int var3 = par1ArrayOfStr.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            if(var5.length() > 0) {
               this.field_73904_o.add(var5);
            }
         }

         if(this.field_73904_o.size() > 0) {
            this.field_73897_d = true;
            this.completePlayerName();
         }
      }
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
   
	/*** modded below ***/
   
	protected void actionPerformed(GuiButton par1GuiButton) {
		TabbyChat tc = TabbyChat.instance;
		ChatButton _button = (ChatButton)par1GuiButton;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && tc.channelMap.get("*") == _button.channel) {
			this.mc.displayGuiScreen(tc.generalSettings);
			return;
		}
		if (!tc.generalSettings.tabbyChatEnable.getValue()) return;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {						
			tc.channelMap.remove(_button.channel.title);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			if (!_button.channel.active) {
				mc.ingameGUI.getChatGUI().me.mergeChatLines(_button.channel.chatLog);
				_button.channel.unread = false;
			}
			_button.channel.active = !_button.channel.active;
			if (!_button.channel.active)
				tc.resetDisplayedChat();
		} else {
			for (ChatChannel chan : tc.channelMap.values()) {
				if (!_button.equals(chan.tab))
					chan.active = false;
			}
			if (!_button.channel.active) {
				this.scrollBar.scrollBarMouseWheel();
				_button.channel.active = true;
				_button.channel.unread = false;
			}
			tc.resetDisplayedChat();
		}
	}
	
	public void drawChatTabs() {
		TabbyChat tc = TabbyChat.instance;
		this.buttonList.clear();
		tc.updateButtonLocations();
		for (ChatChannel _chan : tc.channelMap.values()) {
			this.buttonList.add(_chan.tab);
		}		
	}

	public int getFocusedFieldInd() {
		int _s = this.inputList.size();
		for (int i=0; i<_s; i++) {
			if (this.inputList.get(i).isFocused() && this.inputList.get(i).getVisible())
				return i;
		}
		return 0;
	}

	public void removeCharsAtCursor(int _del) {
		StringBuilder msg = new StringBuilder();
		int cPos = 0;
		boolean cFound = false;
		for (int i=this.inputList.size()-1; i>=0; i-=1) {
			msg.append(this.inputList.get(i).getText());
			if (this.inputList.get(i).isFocused()) {
				cPos += this.inputList.get(i).getCursorPosition();
				cFound = true;
			} else if (!cFound) {
				cPos += this.inputList.get(i).getText().length();
			}
		}
		int other = cPos + _del;
		other = (other >= msg.length()) ? msg.length()-1 : other;
		other = (other < 0) ? 0 : other;
		if (other < cPos) {
			msg.replace(other, cPos, "");
			this.setText(msg, other);
		} else if (other > cPos) {
			msg.replace(cPos, other, "");
			this.setText(msg, cPos);
		} else
			return;
	}
	
	public void insertCharsAtCursor(String _chars) {
		StringBuilder msg = new StringBuilder();
		int cPos = 0;
		boolean cFound = false;
		for (int i=this.inputList.size()-1; i>=0; i-=1) {
			msg.append(this.inputList.get(i).getText());
			if (this.inputList.get(i).isFocused()) {
				cPos += this.inputList.get(i).getCursorPosition();
				cFound = true;
			} else if (!cFound) {
				cPos += this.inputList.get(i).getText().length();
			}			
		}
		if (mc.fontRenderer.getStringWidth(msg.toString()) + mc.fontRenderer.getStringWidth(_chars) < (mc.currentScreen.width-20)*this.inputList.size()) {
			msg.insert(cPos, _chars);
			this.setText(msg, cPos+_chars.length());
		}
	}
	
	public void setText(StringBuilder txt, int pos) {
		List<String> txtList = this.stringListByWidth(txt, mc.currentScreen.width-20);

		int strings = txtList.size() < this.inputList.size() ? txtList.size()-1 : this.inputList.size()-1;
		for (int i=strings; i>=0; i-=1) {
			this.inputList.get(i).setText(txtList.get(strings-i));
			if (pos > txtList.get(strings-i).length()) {
				pos -= txtList.get(strings-i).length();
				this.inputList.get(i).setVisible(true);
				this.inputList.get(i).setFocused(false);
			} else if (pos >= 0) {
				this.inputList.get(i).setFocused(true);
				this.inputList.get(i).setVisible(true);
				this.inputList.get(i).setCursorPosition(pos);
				pos = -1;
			} else {
				this.inputList.get(i).setVisible(true);
				this.inputList.get(i).setFocused(false);
			}
		}
		if (pos > 0) {
			this.inputField.setCursorPositionEnd();
		}
		if (this.inputList.size() > txtList.size()) {
			for (int j=txtList.size(); j<this.inputList.size(); j++) {
				this.inputList.get(j).setText("");
				this.inputList.get(j).setFocused(false);
				this.inputList.get(j).setVisible(false);
			}
		}
		if (!this.inputField.getVisible()) {
			this.inputField.setVisible(true);
			this.inputField.setFocused(true);
		}
	}
	
	public List<String> stringListByWidth(StringBuilder _sb, int _w) {
		List<String> result = new ArrayList<String>(5);
		int _len = 0;
		int _cw;
		StringBuilder bucket = new StringBuilder(_sb.length());
		for (int ind=0; ind<_sb.length(); ind++) {
			_cw = mc.fontRenderer.getCharWidth(_sb.charAt(ind));
			if (_len + _cw > _w) {
				result.add(bucket.toString());
				bucket = new StringBuilder(_sb.length());
				_len = 0;
			}
			_len += _cw;
			bucket.append(_sb.charAt(ind));
		}
		if (bucket.length() > 0)
			result.add(bucket.toString());
		return result;
	}

	public int getCurrentSends() {
		int lng = 0;
		int _s = this.inputList.size() - 1;
		for (int i=_s; i>=0; i-=1) {
			lng += this.inputList.get(i).getText().length();
		}
		if (lng == 0)
			return 0;
		else
			return (int) Math.ceil(lng / 100.0f);
	}
}
