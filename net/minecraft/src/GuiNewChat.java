package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import acs.tabbychat.TabbyChat;

public class GuiNewChat extends Gui {
   private final Minecraft mc;
   private final List sentMessages = new ArrayList();
   private final List chatLines = new ArrayList();
   private int field_73768_d = 0;
   private boolean field_73769_e = false;

   public GuiNewChat(Minecraft par1Minecraft) {
      this.mc = par1Minecraft;
   }

   public void drawChat(int par1) {
      if(this.mc.gameSettings.chatVisibility != 2) {
         byte var2 = 10;
         boolean var3 = false;
         int var4 = 0;
         int var5 = this.chatLines.size();
         float var6 = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
         if(var5 > 0) {
            if(this.getChatOpen()) {
               var2 = 20;
               var3 = true;
            }

            int var7;
            int var9;
            int var12;
            for(var7 = 0; var7 + this.field_73768_d < this.chatLines.size() && var7 < var2; ++var7) {
               ChatLine var8 = (ChatLine)this.chatLines.get(var7 + this.field_73768_d);
               if(var8 != null) {
                  var9 = par1 - var8.getUpdatedCounter();
                  if(var9 < 200 || var3) {
                     double var10 = (double)var9 / 200.0D;
                     var10 = 1.0D - var10;
                     var10 *= 10.0D;
                     if(var10 < 0.0D) {
                        var10 = 0.0D;
                     }

                     if(var10 > 1.0D) {
                        var10 = 1.0D;
                     }

                     var10 *= var10;
                     var12 = (int)(255.0D * var10);
                     if(var3) {
                        var12 = 255;
                     }

                     var12 = (int)((float)var12 * var6);
                     ++var4;
                     if(var12 > 3) {
                        byte var13 = 3;
                        int var14 = -var7 * 9;

                        int xOf = 0;
                        if (TabbyChat.instance.globalPrefs.TCenabled && TabbyChat.instance.globalPrefs.timestampsEnabled) {
                        	xOf = mc.fontRenderer.getStringWidth(TabbyChat.instance.globalPrefs.timestampStyle.maxTime);
                        }
                        
                        drawRect(var13, var14 - 1, var13 + 320 + 4 + xOf, var14 + 8, var12 / 2 << 24);
                        GL11.glEnable(GL11.GL_BLEND);
                        String var15 = var8.getChatLineString();
                        if(!this.mc.gameSettings.chatColours) {
                           var15 = StringUtils.stripControlCodes(var15);
                        }

                        this.mc.fontRenderer.drawStringWithShadow(var15, var13, var14, 16777215 + (var12 << 24));
                     }
                  }
               }
            }

            if(var3) {
               var7 = this.mc.fontRenderer.FONT_HEIGHT;
               GL11.glTranslatef(0.0F, (float)var7, 0.0F);
               int var16 = var5 * var7 + var5;
               var9 = var4 * var7 + var4;
               int var17 = this.field_73768_d * var9 / var5;
               int var11 = var9 * var9 / var16;
               if(var16 != var9) {
                  var12 = var17 > 0?170:96;
                  int var18 = this.field_73769_e?13382451:3355562;
                  drawRect(0, -var17, 2, -var17 - var11, var18 + (var12 << 24));
                  drawRect(2, -var17, 1, -var17 - var11, 13421772 + (var12 << 24));
               }
            }
         }
      }
   }

   public void func_73761_a() {
      this.chatLines.clear();
      this.sentMessages.clear();
   }

   public void printChatMessage(String par1Str) {
      this.printChatMessageWithOptionalDeletion(par1Str, 0);
   }

   public void printChatMessageWithOptionalDeletion(String par1Str, int par2) {
      boolean var3 = this.getChatOpen();
      boolean var4 = true;
      if(par2 != 0) {
         this.deleteChatLine(par2);
      }

      Iterator var5 = this.mc.fontRenderer.listFormattedStringToWidth(par1Str, 320).iterator();
      
      /**** modded here ****/
      TabbyChat tc = TabbyChat.instance;
      if (tc.enabled() && tc.serverPrefs.ip != this.mc.getServerData().serverIP)
    	  tc.reloadServerPrefs();
      ArrayList<ChatLine> multiLineChat = new ArrayList<ChatLine>();
      ChatLine[] ab = new ChatLine[0];
      multiLineChat.clear();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         if(var3 && this.field_73768_d > 0) {
            this.field_73769_e = true;
            this.scroll(1);
         }

         if(!var4) {
            var6 = " " + var6;
         }
         
         multiLineChat.add(new ChatLine(this.mc.ingameGUI.getUpdateCounter(), var6, par2));
         var4 = false;
         //this.chatLines.add(0, new ChatLine(this.mc.ingameGUI.getUpdateCounter(), var6, par2));
      }
      
      /**** modded here ****/
      if (tc.enabled()) {
    	  int ret = tc.processChat(multiLineChat.toArray(ab));
    	  if (ret > 0) {
    		  int _ind = tc.getActive();
    		  for (int c = multiLineChat.size()-1; c >= 0; c--) {
    			  this.chatLines.add(0, tc.getChatLine(_ind, c));
    		  }
    	  }
      } else {
    	  for (int d = 0; d < multiLineChat.size(); d++) {
    		  this.chatLines.add(0, multiLineChat.get(d));
    	  }
      }
      multiLineChat = null;
      ab = null;
      while(this.chatLines.size() > tc.globalPrefs.retainedChats) {
         this.chatLines.remove(this.chatLines.size() - 1);
      }
   }

   public List getSentMessages() {
      return this.sentMessages;
   }

   public void addToSentMessages(String par1Str) {
      if(this.sentMessages.isEmpty() || !((String)this.sentMessages.get(this.sentMessages.size() - 1)).equals(par1Str)) {
         this.sentMessages.add(par1Str);
      }
   }

   public void resetScroll() {
      this.field_73768_d = 0;
      this.field_73769_e = false;
   }

   public void scroll(int par1) {
      this.field_73768_d += par1;
      int var2 = this.chatLines.size();
      if(this.field_73768_d > var2 - 20) {
         this.field_73768_d = var2 - 20;
      }

      if(this.field_73768_d <= 0) {
         this.field_73768_d = 0;
         this.field_73769_e = false;
      }
   }

   public ChatClickData func_73766_a(int par1, int par2) {
      if(!this.getChatOpen()) {
         return null;
      } else {
         ScaledResolution var3 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
         int var4 = var3.getScaleFactor();
         int var5 = par1 / var4 - 3;
         int var6 = par2 / var4 - 40;
         if(var5 >= 0 && var6 >= 0) {
            int var7 = Math.min(20, this.chatLines.size());
            if(var5 <= 320 && var6 < this.mc.fontRenderer.FONT_HEIGHT * var7 + var7) {
               int var8 = var6 / (this.mc.fontRenderer.FONT_HEIGHT + 1) + this.field_73768_d;
               return new ChatClickData(this.mc.fontRenderer, (ChatLine)this.chatLines.get(var8), var5, var6 - (var8 - this.field_73768_d) * this.mc.fontRenderer.FONT_HEIGHT + var8);
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   public void addTranslatedMessage(String par1Str, Object ... par2ArrayOfObj) {
      this.printChatMessage(StringTranslate.getInstance().translateKeyFormat(par1Str, par2ArrayOfObj));
   }

   public boolean getChatOpen() {
      return this.mc.currentScreen instanceof GuiChat;
   }

   public void deleteChatLine(int par1) {
      Iterator var2 = this.chatLines.iterator();

      ChatLine var3;
      do {
         if(!var2.hasNext()) {
            return;
         }

         var3 = (ChatLine)var2.next();
      } while(var3.getChatLineID() != par1);

      var2.remove();
   }
   
   /**** modded below ****/
   
   public int GetChatHeight() {
	   return this.chatLines.size();
   }
   
   public void addChatLines(List _add) {
	   this.chatLines.addAll(_add);
	   this.field_73768_d = 0;
   }
   
   public void clearChatLines() {
	   this.chatLines.clear();
   }
   
   public int chatLinesTraveled() {
	   return this.field_73768_d;
   }
   
   public void setVisChatLines(int _move) {
	   this.field_73768_d = _move;
   }
   
   public int lastUpdate() {
	   return ((ChatLine)this.chatLines.get(this.chatLines.size()-1)).getUpdatedCounter();
   }
}
