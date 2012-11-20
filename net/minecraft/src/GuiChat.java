package net.minecraft.src;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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

   public GuiChat() {}

   public GuiChat(String par1Str) {
      this.defaultInputFieldText = par1Str;
   }

   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      /**** modded here ****/
      TabbyChat.instance.checkServer();
      if (TabbyChat.instance.enabled()) {
    	  this.drawChatTabs();
    	  if (this.scrollBar == null)
    		  this.scrollBar = new ChatScrollBar(this);
    	  this.scrollBar.drawScrollBar();
      } else if (!Minecraft.getMinecraft().isSingleplayer()) {
    	  TabbyChat.instance.updateButtonLocations();
    	  this.controlList.add(TabbyChat.instance.channels.get(0).tab);
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
    	  /* TODO: Need to handle maximum input length much better */
    	  placeholder.setMaxStringLength(500);
    	  placeholder.setEnableBackgroundDrawing(false);
    	  placeholder.setFocused(false);
    	  placeholder.setText(this.defaultInputFieldText);
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
    		 msg.append(this.inputList.get(i).getText().trim());
    	 
         if(msg.toString().length() > 0) {
        	 TabbyChatUtils.writeLargeChat(msg.toString());
        	 for (int j=1; j<this.inputList.size(); j++) {
        		 this.inputList.get(j).setText("");
        		 this.inputList.get(j).setVisible(false);
        	 }
         }
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if(par2 == 200) {
         this.getSentHistory(-1);
      } else if(par2 == 208) {
         this.getSentHistory(1);
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
    	  /// Backspace key has been pressed
    	  if (par2 == 14) {
    		  int foc = this.getFocusedFieldInd();
    		  /// If cursor is at the beginning of the field...
    		  if (this.inputList.get(foc).getCursorPosition() == 0) {
    			  /// If this field is the topmost displayed in the list...
    			  if (foc == this.inputList.size()-1 || !this.inputList.get(foc+1).getVisible())
    				  return;
    			  this.inputList.get(foc).setFocused(false);
    			  this.inputList.get(foc+1).setFocused(true);
    			  this.inputList.get(foc+1).setCursorPositionEnd();
    		  }
    		  /// If cursor is not at the beginning of the field...
   			  String moveMe = "";
   			  int keepPos = this.inputList.get(foc).getCursorPosition();
   			  for (int i=foc; i>= 1; i-=1) {
   				  if (this.inputList.get(i-1).getText().length() == 0)
   					  break;
   				  moveMe = this.inputList.get(i-1).getText().substring(0, 1);
   				  this.inputList.get(i).setText(this.inputList.get(i).getText().concat(moveMe));
   				  this.inputList.get(i-1).setText(this.inputList.get(i-1).getText().substring(1));
   			  }
   			  this.inputList.get(foc).setCursorPosition(keepPos);
   			  this.inputList.get(foc).textboxKeyTyped(par1, par2);
   			  /// May have created a blank row at the bottom, thus needing input fields to be shifted
   			  if (this.inputField.getText().length() == 0) {
   				  for (int j=0; j<this.inputList.size()-1; j++) {
   					  if (this.inputList.get(j+1).getText().length() == 0) {
   						  if (j > 0)
   							  this.inputList.get(j).setVisible(false);
   						  break;
   					  }
   					  this.inputList.get(j).setText(this.inputList.get(j+1).getText());
   					  keepPos = this.inputList.get(j+1).getCursorPosition();
   					  this.inputList.get(j).setCursorPosition(keepPos);
   					  this.inputList.get(j+1).setText("");
   				  }
   				  if (foc > 0) {
   					  this.inputList.get(foc).setFocused(false);
   					  this.inputList.get(foc-1).setFocused(true);
   				  }
   			  }
    	  } else if (mc.fontRenderer.getStringWidth(this.inputField.getText()) >= mc.currentScreen.width-20) {
    		  /* TODO: what if we're typing in the middle of the inputfield? */
   			  if (this.inputList.get(this.inputList.size()-1).getText().length() == 0) {
   				   for (int i=this.inputList.size()-1; i>=1; i-=1) {
   					  if (this.inputList.get(i-1).getText().length() > 0) {
   						  this.inputList.get(i).setText(this.inputList.get(i-1).getText());
   						  this.inputList.get(i-1).setText("");
   						  this.inputList.get(i).setVisible(true);
   					  }
   				   }
   			  }
   			  this.inputField.textboxKeyTyped(par1, par2);
   		  } else {
   			  int foc = this.getFocusedFieldInd();
   			  this.inputList.get(foc).textboxKeyTyped(par1, par2);
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
					   this.mc.displayGuiScreen(new GuiChatConfirmLink(this, this, var4.getClickedUrl(), 0, var4));
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
            this.inputField.setText(this.field_73898_b);
         } else {
            if(this.sentHistoryCursor == var3) {
               this.field_73898_b = this.inputField.getText();
            }

            this.inputField.setText((String)this.mc.ingameGUI.getChatGUI().getSentMessages().get(var2));
            this.sentHistoryCursor = var2;
         }
      }
   }

   public void drawScreen(int par1, int par2, float par3) {
	  int tBoxHeight = 0;
	  for (int i=0; i<this.inputList.size(); i++) {
		  if (this.inputList.get(i).getVisible())
			  tBoxHeight += 12;
	  }
      drawRect(2, this.height - 2 - tBoxHeight, this.width - 2, this.height - 2, Integer.MIN_VALUE);
      for (GuiTextField field : this.inputList)
    	  field.drawTextBox();
      super.drawScreen(par1, par2, par3);
      
      /*** modded here ***/
      if (!Minecraft.getMinecraft().isSingleplayer())
    	  this.drawChatTabs();
      if (TabbyChat.instance.enabled()) {
    	  this.scrollBar.drawScrollBar();
      }
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
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && tc.channels.get(0) == _button.channel) {
			tc.prefsWindow.prepareTempVars();
			tc.filtersWindow.prepareTempFilters();
			this.mc.displayGuiScreen(tc.prefsWindow);
			return;
		}
		if (!tc.globalPrefs.TCenabled) return;
		int n = tc.channels.size();
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {						
			int a = tc.serverPrefs.numFilters();
			for (int z = 0; z < a; z++) {
				if (tc.serverPrefs.filterMatchesChannel(z, (_button.channel.getID())))
						tc.serverPrefs.filterSentToTab(z, false);
			}
			tc.channels.remove(_button.channel);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			if (!_button.channel.active) {
				mc.ingameGUI.getChatGUI().mergeChatLines(_button.channel.chatLog);
				_button.channel.unread = false;
			}
			_button.channel.active = !_button.channel.active;
			if (!_button.channel.active)
				tc.resetDisplayedChat();
		} else {
			for (int i=0; i<n; i++) {
				if (!_button.equals(tc.channels.get(i).tab))
					tc.channels.get(i).active = false;
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
		this.controlList.clear();
		tc.updateButtonLocations();
		for (ChatChannel _chan : tc.channels) {
			this.controlList.add(_chan.tab);
		}		
	}

	public void reflowChat() {
		StringBuilder msg = new StringBuilder();
		for (int i=this.inputList.size()-1; i>=0; i-=1)
			msg.append(this.inputList.get(i).getText());
		List newmsg = mc.fontRenderer.listFormattedStringToWidth(msg.toString(), mc.currentScreen.width-20);
		
		for (int j=newmsg.size()-1; j>=0; j-=1) {
			if (j >= this.inputList.size())
				j = this.inputList.size()-1;
			this.inputList.get(j).setText((String)newmsg.get(j));
		}
		
		if (this.inputList.size() > newmsg.size()) {
			for (int k=newmsg.size(); k<this.inputList.size(); k++)
				this.inputList.get(k).setText("");
		}
	}

	public int getFocusedFieldInd() {
		for (int i=0; i<this.inputList.size(); i++) {
			if (this.inputList.get(i).isFocused() && this.inputList.get(i).getVisible())
				return i;
		}
		return 0;
	}
}
