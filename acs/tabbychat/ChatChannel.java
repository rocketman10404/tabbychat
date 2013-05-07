package acs.tabbychat;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ChatLine;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiButton;
import net.minecraft.src.StringUtils;

public class ChatChannel implements Serializable {
	protected static int nextID = 3600;
	private static final long serialVersionUID = 546162627943686174L;
	public String title;
	public transient ChatButton tab;
	public CopyOnWriteArrayList<TCChatLine> chatLog;
	protected int chanID = nextID + 1;
	public boolean unread = false;
	public boolean active = false;
	protected boolean hasSpam = false;
	protected int spamCount = 1;
	protected boolean notificationsOn = false;
	protected String alias;
	protected String cmdPrefix = "";
	
	public ChatChannel() {
		this.chanID = nextID;
		nextID++;
		this.chatLog = new CopyOnWriteArrayList<TCChatLine>();
		this.notificationsOn = TabbyChat.generalSettings.unreadFlashing.getValue();
	}
	
	public ChatChannel(int _x, int _y, int _w, int _h, String _title) {
		this();
		this.tab = new ChatButton(this.chanID, _x, _y, _w, _h, _title);
		this.title = _title;
		this.alias = this.title;
		this.tab.channel = this;
	}
	
	public ChatChannel(String _title) {
		this(3, 3, Minecraft.getMinecraft().fontRenderer.getStringWidth("<"+_title+">") + 8, 14, _title);
	}
	
	public boolean doesButtonEqual(GuiButton btnObj) {
		return (this.tab.id == btnObj.id);
	}
	
	public int getButtonEnd() {
		return this.tab.xPosition + this.tab.width();
	}

	public int getID() {
		return this.chanID;
	}
	
	public String getDisplayTitle() {
		if (this.active)
			return "[" + this.alias + "]";
		else if (this.unread)
			return "<" + this.alias + ">";
		else
			return this.alias;
	}
	
	public void setButtonObj(ChatButton btnObj) {
		this.tab = btnObj;
		this.tab.channel = this;
	}
	
	public String toString() {
		return this.getDisplayTitle();
	}

	public void clear() {
		this.chatLog.clear();
		this.tab.clear();
		this.tab = null;
	}

	public void setButtonLoc(int _x, int _y) {
		this.tab.xPosition = _x;
		this.tab.yPosition = _y;
	}
	
	public void trimLog() {
		int maxChats = Integer.parseInt(TabbyChat.advancedSettings.chatScrollHistory.getValue()) + 5;
		if(TabbyChat.instance != null && this.chatLog.size() >= maxChats) {
			this.chatLog.subList(this.chatLog.size()-11, this.chatLog.size()-1).clear();
		}
	}

	public void unreadNotify(Gui _gui, int _y, int _opacity) {
		float scaleSetting = TabbyChat.gnc.getScaleSetting();
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0F, 20.0F, 0.0F);
		GL11.glScalef(scaleSetting, scaleSetting, 1.0F);
		
		TabbyChat.instance.mc.ingameGUI.getChatGUI().drawRect(this.tab.xPosition, -this.tab.height() + _y, this.tab.xPosition + this.tab.width(), _y, 0x720000 + (_opacity/2 << 24));
		GL11.glEnable(GL11.GL_BLEND);
		TabbyChat.instance.mc.ingameGUI.getChatGUI().drawCenteredString(TabbyChat.instance.mc.fontRenderer, this.getDisplayTitle(), this.tab.xPosition + this.tab.width()/2, -(this.tab.height()+8)/2 + _y, 16711680 + (_opacity << 24));
		
		GL11.glPopMatrix();	
	}
	
	protected void importOldChat(List<TCChatLine> oldList) {
		if(oldList == null || oldList.isEmpty()) return;
		for(TCChatLine oldChat : oldList) {
			if(oldChat == null || oldChat.statusMsg) continue;
			this.chatLog.add(new TCChatLine(-1, StringUtils.stripControlCodes(oldChat.getChatLineString()), 0));
		}
		this.trimLog();
	}
}
