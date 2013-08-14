package acs.tabbychat.core;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import acs.tabbychat.gui.ChatBox;
import acs.tabbychat.gui.ChatButton;

import net.minecraft.src.Minecraft;
import net.minecraft.src.ChatLine;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiButton;
import net.minecraft.src.StringUtils;

public class ChatChannel implements Serializable {
	protected static int nextID = 3600;
	private static final long serialVersionUID = 546162627943686174L;
	private String title;
	public transient ChatButton tab;
	private ArrayList<TCChatLine> chatLog;
	private final ReentrantReadWriteLock chatListLock = new ReentrantReadWriteLock(true);
	private final Lock chatReadLock = this.chatListLock.readLock();
	private final Lock chatWriteLock = this.chatListLock.writeLock();
	protected int chanID = nextID + 1;
	public boolean unread = false;
	public boolean active = false;
	protected boolean hasSpam = false;
	protected int spamCount = 1;
	public boolean notificationsOn = false;
	public boolean hidePrefix = false;
	private String alias;
	public String cmdPrefix = "";
	
	public ChatChannel() {
		this.chanID = nextID;
		nextID++;
		this.chatLog = new ArrayList<TCChatLine>();
		this.notificationsOn = TabbyChat.generalSettings.unreadFlashing.getValue();
	}
	
	public ChatChannel(int _x, int _y, int _w, int _h, String _title) {
		this();
		this.tab = new ChatButton(this.chanID, _x, _y, _w, _h, _title);
		this.title = _title;
		this.alias = this.title;
		this.tab.channel = this;
		this.tab.width(TabbyChat.mc.fontRenderer.getStringWidth(this.alias + "<>")+8);
	}
	
	public ChatChannel(String _title) {
		this(3, 3, Minecraft.getMinecraft().fontRenderer.getStringWidth("<"+_title+">") + 8, 14, _title);
	}
	
	public void addChat(TCChatLine newChat, boolean visible) {
		this.chatWriteLock.lock();
		try {
			this.chatLog.add(0, newChat);
		} finally {
			this.chatWriteLock.unlock();
		}
		if(!this.title.equals("*") && this.notificationsOn && !visible) this.unread = true; 
	}
	
	public boolean doesButtonEqual(GuiButton btnObj) {
		return (this.tab.id == btnObj.id);
	}
	
	public String getAlias() {
		return this.alias;
	}
	
	public int getButtonEnd() {
		return this.tab.xPosition + this.tab.width();
	}
	
	public TCChatLine getChatLine(int index) {
		TCChatLine retVal = null;
		this.chatReadLock.lock();
		try  {
			retVal = this.chatLog.get(index);
		} finally {
			this.chatReadLock.unlock();
		}
		return retVal;
	}
	
	public List<TCChatLine> getChatLogSublistCopy(int fromInd, int toInd) {
		List<TCChatLine> retVal = new ArrayList<TCChatLine>(toInd-fromInd);
		this.chatReadLock.lock();
		try {
			for(int i=toInd-1; i>=fromInd; i--) {
				retVal.add(this.chatLog.get(i));
			}
		} finally {
			this.chatReadLock.unlock();
		}
		return retVal;
	}
	
	public int getChatLogSize() {
		int mySize = 0;
		this.chatReadLock.lock();
		try {
			mySize = this.chatLog.size();
		} finally {
			this.chatReadLock.unlock();
		}
		return mySize;
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
	
	public String getTitle() {
		return this.title;
	}
	
	public void setButtonObj(ChatButton btnObj) {
		this.tab = btnObj;
		this.tab.channel = this;
	}
	
	public void setAlias(String _alias) {
		this.alias = _alias;
		this.tab.width(TabbyChat.mc.fontRenderer.getStringWidth(_alias+"<>") + 8);
	}
	
	public String toString() {
		return this.getDisplayTitle();
	}

	public void clear() {
		this.chatWriteLock.lock();
		try {
			this.chatLog.clear();
		} finally {
			this.chatWriteLock.unlock();
		}
		this.tab = null;
	}

	public void setButtonLoc(int _x, int _y) {
		this.tab.xPosition = _x;
		this.tab.yPosition = _y;
	}
	
	protected void setChatLogLine(int ind, TCChatLine newLine) {
		this.chatWriteLock.lock();
		try {
			if(ind < this.chatLog.size()) this.chatLog.set(ind, newLine);
			else this.chatLog.add(newLine);
		} finally {
			this.chatWriteLock.unlock();
		}
	}
	
	public void trimLog() {
		TabbyChat tc = GuiNewChatTC.getInstance().tc;
		if(tc == null || tc.serverDataLock.availablePermits() < 1) return;
		int maxChats = tc.enabled() ? Integer.parseInt(TabbyChat.advancedSettings.chatScrollHistory.getValue()) : 100;
		this.chatWriteLock.lock();
		try {
			while(this.chatLog.size() > maxChats) {
				this.chatLog.remove(this.chatLog.size()-1);
			}
		} finally {
			this.chatWriteLock.unlock();
		}
	}

	public void unreadNotify(Gui _gui, int _opacity) {
		Minecraft mc = Minecraft.getMinecraft();
		GuiNewChatTC gnc = GuiNewChatTC.getInstance();
		float scaleSetting = gnc.getScaleSetting();
		int tabY = this.tab.yPosition - gnc.sr.getScaledHeight() - ChatBox.current.y;
		tabY = ChatBox.anchoredTop ? tabY - ChatBox.getChatHeight() + ChatBox.getUnfocusedHeight(): tabY + ChatBox.getChatHeight() - ChatBox.getUnfocusedHeight() + 1;
		
		mc.ingameGUI.getChatGUI().drawRect(this.tab.xPosition, tabY, this.tab.xPosition + this.tab.width(), tabY + this.tab.height(), 0x720000 + (_opacity/2 << 24));
		GL11.glEnable(GL11.GL_BLEND);
		mc.ingameGUI.getChatGUI().drawCenteredString(mc.fontRenderer, this.getDisplayTitle(), this.tab.xPosition + this.tab.width()/2, tabY + 4, 16711680 + (_opacity << 24));
	}
	
	protected void importOldChat(ChatChannel oldChan) {
		if(oldChan == null || oldChan.chatLog.isEmpty()) return;
		this.chatWriteLock.lock();
		try {
			for(TCChatLine oldChat : oldChan.chatLog) {
				if(oldChat == null || oldChat.statusMsg) continue;
				this.chatLog.add(new TCChatLine(-1, StringUtils.stripControlCodes(oldChat.getChatLineString()), 0));
			}
		} finally {
			this.chatWriteLock.unlock();
		}
		this.trimLog();
	}
}
