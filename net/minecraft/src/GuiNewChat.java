package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import acs.tabbychat.TabbyChat;
import acs.tabbychat.ChatChannel;
import acs.tabbychat.TimeStampEnum;

public class GuiNewChat extends Gui {
	private final Minecraft mc;
	private final List sentMessages = new ArrayList();
	private final List chatLines = new ArrayList();
	private final List field_96134_d = new ArrayList();
	private int field_73768_d = 0;
	private boolean field_73769_e = false;
	public int screenWidth;
	public int screenHeight;
	public int chatWidth = 320;
	public int chatHeight = 0;

	public GuiNewChat(Minecraft par1Minecraft) {
		this.mc = par1Minecraft;
		ScaledResolution sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
		this.screenWidth = sr.getScaledWidth();
		this.screenHeight = sr.getScaledHeight();
	}

   public void drawChat(int par1) {
	   if(this.mc.gameSettings.chatVisibility != 2) {
		   /*** Modded here ***/    	  
		   ScaledResolution sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
		   this.screenWidth = sr.getScaledWidth();
		   this.screenHeight = sr.getScaledHeight();
		   this.chatHeight = 0;
		   this.chatWidth = 320;
		   int var2;
		   if (TabbyChat.instance.enabled() && TabbyChat.instance.advancedSettings.customChatBoxSize.getValue()) {
			   float scaleFactor;
			   if(this.getChatOpen())
				   scaleFactor = TabbyChat.instance.advancedSettings.chatBoxFocHeight.getValue() / 100.0f;
			   else
				   scaleFactor = TabbyChat.instance.advancedSettings.chatBoxUnfocHeight.getValue() / 100.0f;
			   var2 = (int)Math.floor((float)(this.screenHeight - 51) * scaleFactor / 9.0f);
		   } else
			   var2 = this.func_96127_i();
		   boolean var3 = false;
		   int var4 = 0;
		   int _y = 0;
		   int var5 = this.field_96134_d.size();
		   float var6 = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
		   if(var5 > 0) {
			   if(this.getChatOpen()) {
				   var3 = true;
			   }

			   float var7 = this.func_96131_h();
			   int xOf = 0;
			   if (TabbyChat.instance.enabled() && TabbyChat.instance.generalSettings.timeStampEnable.getValue()) {
				   xOf = mc.fontRenderer.getStringWidth(((TimeStampEnum)TabbyChat.instance.generalSettings.timeStampStyle.getValue()).maxTime);
			   }
			   /*** Modded here ***/
			   int var8;
			   if (TabbyChat.instance.enabled() && TabbyChat.instance.advancedSettings.customChatBoxSize.getValue()) {
				   int curWidth = this.screenWidth - 14 - xOf;
				   float screenWidthScale = TabbyChat.instance.advancedSettings.chatBoxWidth.getValue() / 100.0f;
				   var8 = MathHelper.ceiling_float_int(screenWidthScale * curWidth / var7);
			   } else
				   var8 = MathHelper.ceiling_float_int((float)this.func_96126_f() / var7);
			   this.chatWidth = var8;
			   GL11.glPushMatrix();
			   GL11.glTranslatef(2.0F, 20.0F, 0.0F);
			   GL11.glScalef(var7, var7, 1.0F);

			   int var9;
			   int var11;
			   int var14;
			   int fadeTicks;
			   if (TabbyChat.instance.enabled())
				   fadeTicks = TabbyChat.instance.advancedSettings.chatFadeTicks.getValue().intValue();
			   else
				   fadeTicks = 200;
			   int _size = this.field_96134_d.size();
			   for(var9 = 0; var9 + this.field_73768_d < _size && var9 < var2; ++var9) {
				   this.chatHeight = var9 * 9;
				   ChatLine var10 = (ChatLine)this.field_96134_d.get(var9 + this.field_73768_d);
				   if(var10 != null) {
					   var11 = par1 - var10.getUpdatedCounter();
					   if(var11 < fadeTicks || var3) {
						   double var12 = (double)var11 / (double)fadeTicks;
						   var12 = 1.0D - var12;
						   var12 *= 10.0D;
						   if(var12 < 0.0D) {
							   var12 = 0.0D;
						   }

						   if(var12 > 1.0D) {
							   var12 = 1.0D;
						   }

						   var12 *= var12;
						   var14 = (int)(255.0D * var12);
						   if(var3) {
							   var14 = 255;
						   }

						   var14 = (int)((float)var14 * var6);
						   ++var4;
						   if(var14 > 3) {
							   byte var15 = 3;
							   int var16 = -var9 * 9;
							   /**** modded here ****/
							   _y = var16 - 9;
							   drawRect(var15, var16 - 9, var15 + var8 + 4 + xOf, var16, var14 / 2 << 24);
							   GL11.glEnable(GL11.GL_BLEND);
							   String var17 = var10.getChatLineString();
							   if(!this.mc.gameSettings.chatColours) {
								   var17 = StringUtils.stripControlCodes(var17);
							   }

							   this.mc.fontRenderer.drawStringWithShadow(var17, var15, var16 - 8, 16777215 + (var14 << 24));
						   }
					   }
				   }
			   }

			   if(var3) {
				   var9 = this.mc.fontRenderer.FONT_HEIGHT;
				   GL11.glTranslatef(-3.0F, 0.0F, 0.0F);
				   int var18 = var5 * var9 + var5;
				   var11 = var4 * var9 + var4;
				   int var20 = this.field_73768_d * var11 / var5;
				   int var13 = var11 * var11 / var18;
				   if(var18 != var11) {
					   var14 = var20 > 0?170:96;
					   int var19 = this.field_73769_e?13382451:3355562;
					   drawRect(0, -var20, 2, -var20 - var13, var19 + (var14 << 24));
					   drawRect(2, -var20, 1, -var20 - var13, 13421772 + (var14 << 24));
				   }
			   }

			   GL11.glPopMatrix();
		   }

		   /**** modded here ****/
		   if (TabbyChat.instance.enabled() && !this.getChatOpen() && TabbyChat.instance.generalSettings.unreadFlashing.getValue()) {
			   TabbyChat.instance.pollForUnread(this, _y, par1); // can probably replace _y with this.chatHeight-9
		   }
	   }
   }

   public void clearChatMessages() {
	   this.field_96134_d.clear();
	   this.chatLines.clear();
	   this.sentMessages.clear();
   }

   public void printChatMessage(String par1Str) {
      this.printChatMessageWithOptionalDeletion(par1Str, 0);
   }
   
   public void printChatMessageWithOptionalDeletion(String par1Str, int par2) {
	   this.func_96129_a(par1Str, par2, this.mc.ingameGUI.getUpdateCounter(), false);
	   this.mc.getLogAgent().logInfo("[CHAT] " + par1Str);
   }

   public void func_96129_a(String par1Str, int par2, int par3, boolean par4) {
      boolean var5 = this.getChatOpen();
      boolean var6 = true;
      if(par2 != 0) {
         this.deleteChatLine(par2);
      }
   
      int maxWidth;
      if (TabbyChat.instance.advancedSettings.customChatBoxSize.getValue()) {
    	  maxWidth = this.chatWidth;
      } else {
    	  maxWidth = MathHelper.floor_float((float)this.func_96126_f() / this.func_96131_h());
      }
      Iterator var7 = this.mc.fontRenderer.listFormattedStringToWidth(par1Str, maxWidth).iterator();
   
      /**** modded here ****/
      TabbyChat tc = TabbyChat.instance;
      tc.checkServer();
      List<ChatLine> multiLineChat = new ArrayList<ChatLine>();

      while(var7.hasNext()) {
         String var8 = (String)var7.next();
         if(var5 && this.field_73768_d > 0) {
            this.field_73769_e = true;
            this.scroll(1);
         }

         if(!var6) {
            var8 = " " + var8;
         }
         multiLineChat.add(new ChatLine(this.mc.ingameGUI.getUpdateCounter(), var8, par2));
         var6 = false;
      }
 
      /**** modded here ****/
      int n;
      if (tc.enabled()) {
    	  int ret = tc.processChat(multiLineChat);
      } else {
    	  n = multiLineChat.size();
    	  for (int d=0; d<n; d++) {
    		  this.field_96134_d.add(0, multiLineChat.get(d));
    		  if (!par4) {
    			  this.chatLines.add(0, multiLineChat.get(d));
    		  }
    	  }
      }
      multiLineChat = null;
      int maxChats = tc.enabled() ? Integer.parseInt(tc.advancedSettings.chatScrollHistory.getValue()) : 100;
      if (this.field_96134_d.size() >= maxChats + 5) {
    	  this.field_96134_d.subList(this.field_96134_d.size()-11, this.field_96134_d.size()-1).clear();
      }
      if (!par4) {
    	  if (this.chatLines.size() >= maxChats + 5) {
    		  this.chatLines.subList(this.chatLines.size()-11,  this.chatLines.size()-1).clear();
    	  }
      }
   }

   public void func_96132_b() {
	   this.field_96134_d.clear();
	   this.resetScroll();

	   for(int var1 = this.chatLines.size() - 1; var1 >= 0; --var1) {
	         ChatLine var2 = (ChatLine)this.chatLines.get(var1);
	         this.func_96129_a(var2.getChatLineString(), var2.getChatLineID(), var2.getUpdatedCounter(), true);
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
	   /*** Modded ***/
	   int var3;
	   if (TabbyChat.instance.enabled()
			   && TabbyChat.instance.advancedSettings.customChatBoxSize.getValue()) {
		   float scaleFactor;
		   if (this.getChatOpen())
			   scaleFactor = TabbyChat.instance.advancedSettings.chatBoxFocHeight.getValue() / 100.0f;
		   else
			   scaleFactor = TabbyChat.instance.advancedSettings.chatBoxUnfocHeight.getValue() / 100.0f;
		   var3 = (int) Math.floor((float) (this.screenHeight - 51) * scaleFactor / 9.0f);
	   } else
		   var3 = this.func_96127_i();

	   this.field_73768_d += par1;
	   int var2 = this.field_96134_d.size();
	   if (this.field_73768_d > var2 - var3) {
		   this.field_73768_d = var2 - var3;
	   }

	   if (this.field_73768_d <= 0) {
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
		   float var5 = this.func_96131_h();
		   int var6 = par1 / var4 - 3;
		   //int var7 = par2 / var4 - 25;
		   int var7 = par2 / var4 - 28;
		   var6 = MathHelper.floor_float((float)var6 / var5);  // these appear to be fine
		   var7 = MathHelper.floor_float((float)var7 / var5);
		   if(var7 >= 0 && var6 >= 0) {
			   //int var8 = Math.min(this.func_96127_i(), this.field_96134_d.size());
			   int var8 = Math.min(this.getHeightSetting() / 9, this.field_96134_d.size());
			   //if(var6 <= MathHelper.floor_float((float)this.func_96126_f() / this.func_96131_h()) && var7 < this.mc.fontRenderer.FONT_HEIGHT * var8 + var8) {
			   if(var6 <= MathHelper.floor_float((float)this.chatWidth / this.func_96131_h()) && var7 < this.mc.fontRenderer.FONT_HEIGHT * var8 + var8) {
				   //int var9 = var7 / (this.mc.fontRenderer.FONT_HEIGHT + 1) + this.field_73768_d;
				   int var9 = var7 / this.mc.fontRenderer.FONT_HEIGHT + this.field_73768_d;
				   return new ChatClickData(this.mc.fontRenderer, (ChatLine)this.field_96134_d.get(var9), var6, var7 - (var9 - this.field_73768_d) * this.mc.fontRenderer.FONT_HEIGHT + var9);
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
      Iterator var2 = this.field_96134_d.iterator();

      ChatLine var3;
      do {
         if(!var2.hasNext()) {
            var2 = this.chatLines.iterator();
            
            do {
            	if(!var2.hasNext()) {
            		return;
            	}
            
            	var3 = (ChatLine)var2.next();
            } while(var3.getChatLineID() != par1);
            
            var2.remove();
            return;
         }

         var3 = (ChatLine)var2.next();
      } while(var3.getChatLineID() != par1);

      var2.remove();
   }
   
   public int func_96126_f() {
	   return func_96128_a(this.mc.gameSettings.chatWidth);
   }

   public int func_96133_g() {
	   return func_96130_b(this.getChatOpen()?this.mc.gameSettings.chatHeightFocused:this.mc.gameSettings.chatHeightUnfocused);
   }

   public float func_96131_h() {
	   return this.mc.gameSettings.chatScale;
   }

   public static final int func_96128_a(float par0) {
	   short var1 = 320;
	   byte var2 = 40;
	   return MathHelper.floor_float(par0 * (float)(var1 - var2) + (float)var2);
   }

   public static final int func_96130_b(float par0) {
	   short var1 = 180;
	   byte var2 = 20;
	   return MathHelper.floor_float(par0 * (float)(var1 - var2) + (float)var2);
   }

   public int func_96127_i() {
	   return this.func_96133_g() / 9;
   }
   
   /**** modded below ****/
   
   public int getHeightSetting() {
 	  if (TabbyChat.instance.enabled() && TabbyChat.instance.advancedSettings.customChatBoxSize.getValue()) {
		  float scaleFactor = TabbyChat.instance.advancedSettings.chatBoxFocHeight.getValue() / 100.0f;
		  return (int)Math.floor((float)(this.screenHeight - 51) * scaleFactor);
	  } else
		  return func_96130_b(this.mc.gameSettings.chatHeightFocused);
   }
   
   public int getWidthSetting() {
	   return this.chatWidth;
   }

   public float getScaleSetting() {
	   return this.func_96131_h();
   }
   
   public int GetChatHeight() {
	   return this.field_96134_d.size();
   }
   
   public void addChatLines(List _add) {
	   this.field_96134_d.addAll(_add);
	   //this.field_73768_d = 0;
   }
   
   public void addChatLines(int _pos, List _add) {
	   this.field_96134_d.addAll(_pos, _add);
	   //this.field_73768_d = 0;
   }
   
   public void setChatLines(int _pos, List _add) {
	   for (int i=0; i < _add.size(); i++)
		   this.field_96134_d.set(_pos+i, _add.get(i));
	   //this.field_73768_d = 0;
   }
   
   public void clearChatLines() {
	   this.resetScroll();
	   this.field_96134_d.clear();
   }
   
   public int chatLinesTraveled() {
	   return this.field_73768_d;
   }
   
   public void setVisChatLines(int _move) {
	   this.field_73768_d = _move;
   }
   
   public int lastUpdate() {
	   return ((ChatLine)this.field_96134_d.get(this.field_96134_d.size()-1)).getUpdatedCounter();
   }

public void mergeChatLines(List<ChatLine> _new) {
	   ArrayList<ChatLine> _current = (ArrayList<ChatLine>)this.field_96134_d;
	   if (_new == null || _new.size() <= 0) return;
	   
	   int _c = 0;
	   int _n = 0;
	   int dt = 0;
	   int max = _new.size();
	   while (_n < max && _c < _current.size()) {
		   dt = _new.get(_n).getUpdatedCounter() - _current.get(_c).getUpdatedCounter();
		   if (dt > 0) {
			   _current.add(_c, _new.get(_n));
			   _n++;
		   } else if (dt == 0) {
			   if (_current.get(_c).equals(_new.get(_n))) {
				   _c++;
				   _n++;
			   } else
				   _c++;
		   } else
			   _c++;
	   }
	   
	   while (_n < max) {
		   _current.add(_current.size(), _new.get(_n));
		   _n++;
	   }
   }
}
