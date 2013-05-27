package acs.tabbychat.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.lwjgl.opengl.GL11;

import acs.tabbychat.gui.ChatBox;
import acs.tabbychat.settings.TimeStampEnum;
import acs.tabbychat.util.TabbyChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiNewChat;
import net.minecraft.src.ILogAgent;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.ChatLine;
import net.minecraft.src.StringTranslate;
import net.minecraft.src.StringUtils;
import net.minecraft.src.ChatClickData;
import net.minecraft.src.GuiDisconnected;
import net.minecraft.src.GuiIngameMenu;

public class GuiNewChatTC extends GuiNewChat {
	public final Minecraft mc;
	public ScaledResolution sr;
	protected int chatWidth = 320;
	public int chatHeight = 0;
	protected List<String> sentMessages = new ArrayList<String>();
	public List<TCChatLine> chatLines;
	public List<TCChatLine> backupLines;
	private static final ReentrantReadWriteLock chatListLock = new ReentrantReadWriteLock(true);
	private static final Lock chatReadLock = chatListLock.readLock();
	private static final Lock chatWriteLock = chatListLock.writeLock();
	private int scrollOffset = 0;
	private boolean chatScrolled = false;
	protected boolean saveNeeded = true;
	public static GuiNewChatTC me = new GuiNewChatTC();
	private final static TabbyChat tc = TabbyChat.instance;
	
	public GuiNewChatTC() {
		this(Minecraft.getMinecraft());
		TabbyChatUtils.hookIntoChat(this);
	}
	
	public GuiNewChatTC(Minecraft par1Minecraft) {
		super(par1Minecraft);
		this.mc = par1Minecraft;
		this.sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
	}
	
	public void addChatLines(int _pos, List<TCChatLine> _add) {
		chatReadLock.lock();
		try {
			for(int i=_add.size()-1;i>=0;i--) {
				this.chatLines.add(_pos,_add.get(i));
				this.backupLines.add(_pos, _add.get(i));
			}
		} finally {
			chatReadLock.unlock();
		}
	}

	public void addChatLines(List<TCChatLine> _add) {
		chatReadLock.lock();
		try {
			for(int i=0; i<_add.size();i++)	{
				this.chatLines.add(_add.get(i));
				this.backupLines.add(_add.get(i));
			}
		} finally {
			chatReadLock.unlock();
		}
	}
	
	public @Override void addToSentMessages(String _msg) {
		if(this.sentMessages.isEmpty() || !(this.sentMessages.get(this.sentMessages.size()-1)).equals(_msg)) {
			this.sentMessages.add(_msg);
		}
	}
	
	public @Override void addTranslatedMessage(String par1Str, Object ... par2ArrayOfObj) {
		this.printChatMessage(StringTranslate.getInstance().translateKeyFormat(par1Str, par2ArrayOfObj));
	}

	public int chatLinesTraveled() {
		return this.scrollOffset;
	}

	public void clearChatLines() {
		this.resetScroll();
		chatWriteLock.lock();
		try {
			this.chatLines.clear();
		} finally {
			chatWriteLock.unlock();
		}
	}

	public @Override void clearChatMessages() {
		chatWriteLock.lock();
		try {
			this.chatLines.clear();
			this.backupLines.clear();
		} finally {
			chatWriteLock.unlock();
		}
		this.sentMessages.clear();
	}
	
	public @Override void deleteChatLine(int _id) {
		ChatLine chatLineRemove = null;
		ChatLine backupLineRemove = null;
		chatReadLock.lock();
		try {
			Iterator _iter = this.chatLines.iterator();
			ChatLine _cl;
			do {
				if(!_iter.hasNext()) {
					_iter = this.backupLines.iterator();
					do {
						if(!_iter.hasNext()) {
							return;
						}
						_cl = (ChatLine)_iter.next();
					} while(_cl.getChatLineID() != _id);
					backupLineRemove = _cl;
					break;
				}
				_cl = (ChatLine)_iter.next();
			} while(_cl.getChatLineID() != _id);
			chatLineRemove = _cl;
		} finally {
			chatReadLock.unlock();
		}
		
		chatWriteLock.lock();
		try {		
			if(chatLineRemove != null && chatLineRemove.getChatLineID() == _id) {
				this.chatLines.remove(chatLineRemove);
			}
			if(backupLineRemove != null && backupLineRemove.getChatLineID() == _id) this.backupLines.remove(backupLineRemove);
		} finally {
			chatWriteLock.unlock();
		}
	}

	public @Override void drawChat(int currentTick) {
		
		// Save channel data if at main menu or disconnect screen, use flag so it's only saved once
		if(mc.currentScreen != null) {
			if(this.mc.currentScreen instanceof GuiDisconnected || this.mc.currentScreen instanceof GuiIngameMenu) {
				if(this.saveNeeded) tc.storeChannelData();
				this.saveNeeded = false;
			} else {
				this.saveNeeded = true;
			}
		}
		
		boolean unicodeStore = this.mc.fontRenderer.getUnicodeFlag();
		int lineCounter = 0;
		int visLineCounter = 0;
		if(TabbyChat.generalSettings.tabbyChatEnable.getValue() && TabbyChat.advancedSettings.forceUnicode.getValue()) this.mc.fontRenderer.setUnicodeFlag(true);
		if(this.mc.gameSettings.chatVisibility != 2) {			
			this.sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
			this.chatHeight = 0;
			this.chatWidth = 320;
			int maxDisplayedLines = 0;
			boolean chatOpen = false;
			int validLinesDisplayed = 0;
			float chatOpacity = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
			float chatScaling = this.func_96131_h();
			int timeStampOffset = 0;;
			int fadeTicks = 200;
			
			int numLinesTotal = 0;
			chatReadLock.lock();
			try {
				numLinesTotal = this.chatLines.size();
			} finally {
				chatReadLock.unlock();
			}
			if(numLinesTotal == 0) return;
			chatOpen = this.getChatOpen();
			
			if(TabbyChat.instance.enabled()) {
				if(TabbyChat.generalSettings.timeStampEnable.getValue())
					timeStampOffset = mc.fontRenderer.getStringWidth(((TimeStampEnum)TabbyChat.generalSettings.timeStampStyle.getValue()).maxTime);
				if(TabbyChat.advancedSettings.customChatBoxSize.getValue()) {
					float scaleFactor;
					if(chatOpen)
						scaleFactor = TabbyChat.advancedSettings.chatBoxFocHeight.getValue() / 100.0f;
					else
						scaleFactor = TabbyChat.advancedSettings.chatBoxUnfocHeight.getValue() / 100.0f;
					maxDisplayedLines = (int)Math.floor((float)(this.sr.getScaledHeight() - 51) * scaleFactor / 9.0f);
					
					int curWidth = this.sr.getScaledWidth() - 14 - timeStampOffset;
					float screenWidthScale = TabbyChat.advancedSettings.chatBoxWidth.getValue() / 100.0f;
					this.chatWidth = MathHelper.ceiling_float_int(screenWidthScale * curWidth / chatScaling);
				} else {
					maxDisplayedLines = this.func_96127_i();
					this.chatWidth = MathHelper.ceiling_float_int((float)this.func_96126_f() / chatScaling);
				}
				this.chatWidth -= 7;
				fadeTicks = TabbyChat.advancedSettings.chatFadeTicks.getValue().intValue();
			} else {
				maxDisplayedLines = this.func_96127_i();
				this.chatWidth = MathHelper.ceiling_float_int((float)this.func_96126_f() / chatScaling);
			}
			
			GL11.glPushMatrix();
			GL11.glTranslatef((float)ChatBox.current.x, 48.0f + (float)(ChatBox.current.y + ChatBox.current.height), 0.0f);
			GL11.glScalef(chatScaling, chatScaling, 1.0f);
			
			int lineAge;
			int currentOpacity = 0;
			TCChatLine _line = null;
			
			// Display valid chat lines
			for(lineCounter = 0; lineCounter + this.scrollOffset  < numLinesTotal && lineCounter < maxDisplayedLines; ++lineCounter) {
				this.chatHeight = lineCounter * 9;
				_line = null;
				chatReadLock.lock();
				try {
					_line = this.chatLines.get(lineCounter + this.scrollOffset);
				} finally {
					chatReadLock.unlock();
				}
				if(_line == null) continue;
				lineAge = currentTick - _line.getUpdatedCounter(); 
				if(lineAge < fadeTicks || chatOpen) {
					if(!chatOpen) {
						double agePercent = (double)currentTick / (double)fadeTicks;
						agePercent = 10.0D * (1.0D - agePercent);
						agePercent = Math.min(0.0D, agePercent);
						agePercent = Math.max(1.0D, agePercent);
						agePercent *= agePercent;
						currentOpacity = (int)(255.0D * agePercent);
					} else {
						currentOpacity = 255;
					}
					currentOpacity = (int)((float)currentOpacity * chatOpacity);
					++validLinesDisplayed;
					if(currentOpacity > 3) {
						visLineCounter++;
						byte xOrigin = 0;
						int yOrigin = -lineCounter * 9;
						drawRect(xOrigin, yOrigin-9, xOrigin + this.chatWidth + timeStampOffset, yOrigin, currentOpacity / 2 << 24);
						GL11.glEnable(GL11.GL_BLEND);
						String _chat = _line.getChatLineString();
						if(!this.mc.gameSettings.chatColours)
							_chat = StringUtils.stripControlCodes(_chat);
						if(_line.getUpdatedCounter() < 0) {
							this.mc.fontRenderer.drawStringWithShadow(_chat, xOrigin, yOrigin-8, 0x888888 + (currentOpacity << 24));
						} else this.mc.fontRenderer.drawStringWithShadow(_chat, xOrigin, yOrigin-8, 0xffffff + (currentOpacity << 24));
					}
				}
			}
			ChatBox.setChatSize(this.chatWidth+timeStampOffset, this.chatHeight+9);
			ChatBox.drawChatBoxBorder((Gui)this, chatOpen, currentOpacity);
			GL11.glPopMatrix();
		}
		if(TabbyChat.instance.enabled() && !this.getChatOpen())
			TabbyChat.instance.pollForUnread(this, -visLineCounter * 9, currentTick);
		this.mc.fontRenderer.setUnicodeFlag(unicodeStore);
	}
	
	public @Override ChatClickData func_73766_a(int clickX, int clickY) {
		if(!this.getChatOpen()) return null;
		else {
			ChatClickData returnMe = null;
			ScaledResolution _sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
			int scaleFactor = _sr.getScaleFactor();
			float scaleSetting = this.func_96131_h();
			int clickXRel = clickX / scaleFactor - 3;
			int clickYRel = clickY / scaleFactor - 28;
			clickXRel = MathHelper.floor_float((float)clickXRel / scaleSetting);
			clickYRel = MathHelper.floor_float((float)clickYRel / scaleSetting);
			if(clickXRel >= 0 && clickYRel >= 0) {
				chatReadLock.lock();
				try {
					int displayedLines = Math.min(this.getHeightSetting() / 9, this.chatLines.size());
					if(clickXRel <= MathHelper.floor_float((float)this.chatWidth / scaleSetting)
							&& clickYRel < this.mc.fontRenderer.FONT_HEIGHT * displayedLines + displayedLines) {
						int lineIndex = clickYRel / this.mc.fontRenderer.FONT_HEIGHT + this.scrollOffset;
						if(lineIndex < displayedLines + this.scrollOffset && this.chatLines.get(lineIndex) != null) {
							returnMe = new ChatClickData(this.mc.fontRenderer, this.chatLines.get(lineIndex), clickXRel, clickYRel - (lineIndex - this.scrollOffset) * this.mc.fontRenderer.FONT_HEIGHT + lineIndex); 
						}
					}
				} finally {
					chatReadLock.unlock();
				}				
			}
			return returnMe;
		}
	}

	public void func_96129_a(String _msg, int id, int tick, boolean backupFlag) {
		boolean chatOpen = this.getChatOpen();
		boolean isLineOne = true;
		boolean optionalDeletion = false;
		List<TCChatLine> multiLineChat = new ArrayList<TCChatLine>();
		// Delete message if requested
		if(id != 0) {
			optionalDeletion = true;
			this.deleteChatLine(id);
		}			
		// Split message by available chatbox space
		int maxWidth = MathHelper.floor_float((float)this.func_96126_f() / this.func_96131_h());
		if(TabbyChat.instance.enabled()) {
			TabbyChat.instance.checkServer();
			if(TabbyChat.advancedSettings.customChatBoxSize.getValue())
				maxWidth = this.chatWidth;
		}
		Iterator lineIter = this.mc.fontRenderer.listFormattedStringToWidth(_msg, maxWidth).iterator();

		// Prepare list of chatlines
		while(lineIter.hasNext()) {
			String _line = (String)lineIter.next();
			if(chatOpen && this.scrollOffset > 0) {
				this.chatScrolled = true;
				this.scroll(1);
			}
			if(!isLineOne) {
				_line = " " + _line;
			}
			multiLineChat.add(new TCChatLine(tick, _line, id));
			isLineOne = false;
		}
		
		// Add chatlines to appropriate lists
		if(TabbyChat.instance.enabled() && !optionalDeletion) {
			int ret = TabbyChat.instance.processChat(multiLineChat);
		} else {
			int _len = multiLineChat.size();
			chatWriteLock.lock();
			try {
				for(int i=0; i<_len; i++) {
					this.chatLines.add(0, multiLineChat.get(i));
					if (!backupFlag)
						this.backupLines.add(0, multiLineChat.get(i));
				}
			} finally {
				chatWriteLock.unlock();
			}
		}
		
		// Trim lists to size as needed
		if(TabbyChat.instance.serverDataLock.availablePermits() < 1) return;
		int maxChats = TabbyChat.instance.enabled() ? Integer.parseInt(TabbyChat.advancedSettings.chatScrollHistory.getValue()) : 100;
		int numChats = 0;
		chatReadLock.lock();
		try {
			numChats = this.chatLines.size();
		} finally {
			chatReadLock.unlock();
		}
		if(numChats <= maxChats) return;
		
		chatWriteLock.lock();
		try {
			while(this.chatLines.size() > maxChats) {
				this.chatLines.remove(this.chatLines.size()-1);
			}
			if(!backupFlag) {
				while(this.backupLines.size() > maxChats) {
					this.backupLines.remove(this.backupLines.size()-1);
				}
			}
		} finally {
			chatWriteLock.unlock();
		}
	}

	public @Override void func_96132_b() {
		// Chat settings have changed
		int backupChats = 0;
		chatWriteLock.lock();
		try {
			this.chatLines.clear();
			backupChats = this.backupLines.size();
		} finally {
			chatWriteLock.unlock();
		}
		
		this.resetScroll();
		for(int i=backupChats-1; i>=0; --i) {
			chatReadLock.lock();
			ChatLine _cl = null;
			try {
				_cl = this.backupLines.get(i);
			} finally {
				chatReadLock.unlock();
			}
			if(_cl != null) this.func_96129_a(_cl.getChatLineString(), _cl.getChatLineID(), _cl.getUpdatedCounter(), true);
		}
	}
	
	public int GetChatHeight() {
		int theSize = 0;
		chatReadLock.lock();
		try {
			theSize = this.chatLines.size();
		} finally {
			chatReadLock.unlock();
		}
		return theSize;
	}
	
	public @Override boolean getChatOpen() {
		return (this.mc.currentScreen instanceof GuiChat || this.mc.currentScreen instanceof GuiChatTC);
	}
	
	public int getHeightSetting() {
		if (TabbyChat.instance.enabled() && TabbyChat.advancedSettings.customChatBoxSize.getValue()) {
			float scaleFactor = TabbyChat.advancedSettings.chatBoxFocHeight.getValue() / 100.0f;
			return (int)Math.floor((float)(this.sr.getScaledHeight() - 51) * scaleFactor);
		} else
			return func_96130_b(this.mc.gameSettings.chatHeightFocused);
	}

	public float getScaleSetting() {
		return this.func_96131_h();
	}

	public @Override List getSentMessages() {
		return this.sentMessages;
	}

	public int getWidthSetting() {
		return this.chatWidth;
	}

	public void mergeChatLines(List<TCChatLine> _new) {
		chatWriteLock.lock();
		try {
			List<TCChatLine> _current = this.chatLines;
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
					if (_current.get(_c).equals(_new.get(_n)) || _current.get(_c).getChatLineString().equals(_new.get(_n).getChatLineString())) {
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
		} finally {
			chatWriteLock.unlock();
		}
	}

	public @Override void printChatMessage(String _msg) {
		this.printChatMessageWithOptionalDeletion(_msg, 0);
	}

	public @Override void printChatMessageWithOptionalDeletion(String _msg, int flag) {
		this.func_96129_a(_msg, flag, this.mc.ingameGUI.getUpdateCounter(), false);
		this.mc.getLogAgent().logInfo("[CHAT] " + _msg);
	}

	public @Override void resetScroll() {
		this.scrollOffset = 0;
		this.chatScrolled = false;
	}

	public @Override void scroll(int _lines) {
		int maxLineDisplay;
		if(TabbyChat.instance.enabled() && TabbyChat.advancedSettings.customChatBoxSize.getValue()) {
			float scaleFactor;
			if(this.getChatOpen())
				scaleFactor = TabbyChat.advancedSettings.chatBoxFocHeight.getValue() / 100.0f;
			else
				scaleFactor = TabbyChat.advancedSettings.chatBoxUnfocHeight.getValue() / 100.0f;
			maxLineDisplay = (int)Math.floor((float)(this.sr.getScaledHeight() - 51)*scaleFactor / 9.0f);
		} else
			maxLineDisplay = this.func_96127_i();
		
		this.scrollOffset += _lines;
		int numLines = 0;
		chatReadLock.lock();
		try {
			numLines = this.chatLines.size();
		} finally {
			chatReadLock.unlock();
		}
		this.scrollOffset = Math.min(this.scrollOffset, numLines - maxLineDisplay);
		if(this.scrollOffset <= 0) {
			this.scrollOffset = 0;
			this.chatScrolled = false;
		}
	}

	public void setChatLines(int _pos, List<TCChatLine> _add) {
		chatWriteLock.lock();
		try {
			for (int i=0; i < _add.size(); i++) {
				this.chatLines.set(_pos+i, _add.get(i));
				this.backupLines.set(_pos+i, _add.get(i));
			}
		} finally {
			chatWriteLock.unlock();
		}

	}

	public void setVisChatLines(int _move) {
		this.scrollOffset = _move;
	}
}