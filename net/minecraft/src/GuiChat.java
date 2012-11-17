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
      this.inputField.setMaxStringLength(100);
      this.inputField.setEnableBackgroundDrawing(false);
      this.inputField.setFocused(true);
      this.inputField.setText(this.defaultInputFieldText);
      this.inputField.setCanLoseFocus(false);
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
         String var3 = this.inputField.getText().trim();
         if(var3.length() > 0) {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(var3);
            if(!this.mc.handleClientCommand(var3)) {
               this.mc.thePlayer.sendChatMessage(var3);
            }
         }

         this.mc.displayGuiScreen((GuiScreen)null);
      } else if(par2 == 200) {
         this.getSentHistory(-1);
      } else if(par2 == 208) {
         this.getSentHistory(1);
      } else if(par2 == 201) {
         this.mc.ingameGUI.getChatGUI().scroll(19);
      } else if(par2 == 209) {
         this.mc.ingameGUI.getChatGUI().scroll(-19);
      } else {
         this.inputField.textboxKeyTyped(par1, par2);
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

      this.inputField.mouseClicked(par1, par2, par3);
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
      drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
      this.inputField.drawTextBox();
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
			this.mc.ingameGUI.getChatGUI().clearChatLines();
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
}
