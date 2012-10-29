package acs.tabbychat;

import java.util.List;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ChatLine;
import net.minecraft.src.GuiButton;

public class ChatChannel {
	private static int nextID = 3600;
	protected String title;
	public ChatButton tab;
	protected List<ChatLine> chatLog;
	protected int chanID = nextID + 1;
	public boolean unread = false;
	public boolean active = false;
	protected boolean hasFilter = false;
	
	public ChatChannel() {
		this.chanID = nextID;
		nextID++;
		this.chatLog = new ArrayList<ChatLine>(100);
	}
	
	public ChatChannel(int _x, int _y, int _w, int _h, String _title) {
		this();
		this.tab = new ChatButton(this.chanID, _x, _y, _w, _h, _title);
		this.title = _title;
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
			return this.title;
		else if (this.unread)
			return "<" + this.title + ">";
		else
			return "[" + this.title + "]";
	}
	
	public void setButtonObj(ChatButton btnObj) {
		this.tab = btnObj;
		this.tab.channel = this;
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
		if (this.chatLog.size() >= TabbyChat.instance.globalPrefs.retainedChats + 5) {
			for (int i = 0; i <= 10; i++) {
				this.chatLog.remove(this.chatLog.size()-1);
			}
		}
	}
}