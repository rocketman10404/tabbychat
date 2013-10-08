package acs.tabbychat.core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.lwjgl.opengl.GL11;

import acs.tabbychat.gui.ChatBox;
import acs.tabbychat.gui.ChatScrollBar;
import acs.tabbychat.settings.TimeStampEnum;
import acs.tabbychat.util.TabbyChatUtils;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiNewChat;
import net.minecraft.src.ILogAgent;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.ChatLine;
import net.minecraft.src.StatCollector;
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
	public List<String> sentMessages;
	public List<TCChatLine> chatLines;
	public List<TCChatLine> backupLines;
	private static final ReentrantReadWriteLock chatListLock = new ReentrantReadWriteLock(true);
	private static final Lock chatReadLock = chatListLock.readLock();
	private static final Lock chatWriteLock = chatListLock.writeLock();
	private int scrollOffset = 0;
	private boolean chatScrolled = false;
	protected boolean saveNeeded = true;
	private static GuiNewChatTC instance = null;
	public static TabbyChat tc;
	
	private GuiNewChatTC(Minecraft par1Minecraft) {
		super(par1Minecraft);
		this.mc = par1Minecraft;
		this.sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
	}
	
	public void addChatLines(int _pos, List<TCChatLine> _add) {
		chatReadLock.lock();
		try {
			for(int i=0; i<_add.size(); i++) {
				this.chatLines.add(_pos,_add.get(i));
				this.backupLines.add(_pos, _add.get(i));
			}
		} finally {
			chatReadLock.unlock();
		}
	}

	public void addChatLines(ChatChannel _addChan) {
		chatReadLock.lock();
		try {
			for(int i=0; i<_addChan.getChatLogSize();i++)	{
				this.chatLines.add(_addChan.getChatLine(i));
				this.backupLines.add(_addChan.getChatLine(i));
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
		this.printChatMessage(StatCollector.translateToLocalFormatted(par1Str, par2ArrayOfObj));
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
		if(this.chatLines == null || this.backupLines == null) return;
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
		if(!tc.liteLoaded && !tc.modLoaded) TabbyChatUtils.chatGuiTick(mc);
		
		// Save channel data if at main menu or disconnect screen, use flag so it's only saved once
		if(mc.currentScreen != null) {
			if(this.mc.currentScreen instanceof GuiDisconnected || this.mc.currentScreen instanceof GuiIngameMenu) {
				if(this.saveNeeded) {
					tc.storeChannelData();
					tc.advancedSettings.saveSettingsFile();
				}
				this.saveNeeded = false;
				return;
			} else {
				this.saveNeeded = true;
			}
		}
		
		this.sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
		
		int lineCounter = 0;
		int visLineCounter = 0;
		if(TabbyChat.generalSettings.tabbyChatEnable.getValue() && TabbyChat.advancedSettings.forceUnicode.getValue()) this.mc.fontRenderer.setUnicodeFlag(true);
		if(this.mc.gameSettings.chatVisibility != 2) {			
			int maxDisplayedLines = 0;
			boolean chatOpen = false;
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
			chatOpen = this.getChatOpen();
			if(numLinesTotal == 0 && !chatOpen) {
				this.mc.fontRenderer.setUnicodeFlag(TabbyChat.defaultUnicode);
				return;
			}
			
			if(tc.enabled()) {
				if(TabbyChat.generalSettings.timeStampEnable.getValue())
					timeStampOffset = mc.fontRenderer.getStringWidth(((TimeStampEnum)TabbyChat.generalSettings.timeStampStyle.getValue()).maxTime);

				maxDisplayedLines = MathHelper.floor_float(ChatBox.getChatHeight() / 9.0f);
				if(!chatOpen) maxDisplayedLines = MathHelper.floor_float(TabbyChat.advancedSettings.chatBoxUnfocHeight.getValue() * ChatBox.getChatHeight() / 900.0f);
				this.chatWidth = ChatBox.getChatWidth() - timeStampOffset;
				fadeTicks = TabbyChat.advancedSettings.chatFadeTicks.getValue().intValue();
			} else {
				maxDisplayedLines = this.func_96127_i();
				this.chatWidth = MathHelper.ceiling_float_int((float)this.func_96126_f() / chatScaling);
			}
			GL11.glPushMatrix();
			if(tc.enabled()) {
				GL11.glTranslatef((float)ChatBox.current.x, 48.0f + (float)ChatBox.current.y, 0.0f);
			} else {
				GL11.glTranslatef(2.0f, 29.0f, 0.0f);
			}
			GL11.glScalef(chatScaling, chatScaling, 1.0f);
			
			int lineAge;
			int currentOpacity = 0;
			List<TCChatLine> msgList;
			
			// Display valid chat lines
			for(lineCounter = 0; lineCounter + this.scrollOffset  < numLinesTotal && lineCounter < maxDisplayedLines; ++lineCounter) {
				msgList = new ArrayList<TCChatLine>();
				chatReadLock.lock();
				try {
					msgList.add(this.chatLines.get(lineCounter + this.scrollOffset));
					if(msgList.get(0) != null && msgList.get(0).getChatLineString().startsWith(" ")) {
						for(int sameMsgCounter = 1; lineCounter + sameMsgCounter + this.scrollOffset < numLinesTotal && lineCounter + sameMsgCounter < maxDisplayedLines; ++sameMsgCounter) {
							TCChatLine checkLine = this.chatLines.get(lineCounter + sameMsgCounter + this.scrollOffset);
							if(checkLine.getUpdatedCounter() != msgList.get(0).getUpdatedCounter()) break;
							msgList.add(checkLine);
							if(!checkLine.getChatLineString().startsWith(" ")) break;
						}
					}
				} finally {
					chatReadLock.unlock();
				}
				if(msgList.isEmpty() || msgList.get(0) == null) continue;
				lineCounter += msgList.size() - 1;
				lineAge = currentTick - msgList.get(0).getUpdatedCounter(); 
				if(lineAge < fadeTicks || chatOpen) {
					if(!chatOpen) {
						double agePercent = (double)lineAge / (double)fadeTicks;
						agePercent = 10.0D * (1.0D - agePercent);
						if(agePercent < 0.0D) agePercent = 0.0D;
						else if(agePercent > 1.0D) agePercent = 1.0D;
						agePercent *= agePercent;
						currentOpacity = (int)(255.0D * agePercent);
					} else {
						currentOpacity = 255;
					}
					currentOpacity = (int)((float)currentOpacity * chatOpacity);
					if(currentOpacity > 3) {
						for(int i=0; i<msgList.size(); i++) {
							visLineCounter++;
							byte xOrigin = 0;
							int yOrigin = ChatBox.anchoredTop && tc.enabled() ? (visLineCounter-1)*9 : -visLineCounter * 9;
							drawRect(xOrigin, yOrigin, xOrigin + this.chatWidth + timeStampOffset, yOrigin+9, currentOpacity / 2 << 24);
							GL11.glEnable(GL11.GL_BLEND);
							String _chat;
							int idx = ChatBox.anchoredTop && tc.enabled() ? msgList.size() - i - 1 : i;
							if(tc.enabled() && tc.generalSettings.timeStampEnable.getValue()) {
								_chat = msgList.get(idx).timeStamp + msgList.get(idx).getChatLineString();
							} else {
								_chat = msgList.get(idx).getChatLineString();
							}
							if(!this.mc.gameSettings.chatColours)
								_chat = StringUtils.stripControlCodes(_chat);
							int textOpacity = (TabbyChat.advancedSettings.textIgnoreOpacity.getValue() ? 255 : currentOpacity);
							if(msgList.get(i).getUpdatedCounter() < 0) {
								this.mc.fontRenderer.drawStringWithShadow(_chat, xOrigin, yOrigin+1, 0x888888 + (textOpacity << 24));
							} else this.mc.fontRenderer.drawStringWithShadow(_chat, xOrigin, yOrigin+1, 0xffffff + (textOpacity << 24));
						}
					}
				}
			}
			this.chatHeight = visLineCounter * 9;
			if(tc.enabled()) {
				if(chatOpen) {
					ChatBox.setChatSize(this.chatHeight);
					ChatScrollBar.drawScrollBar();
					ChatBox.drawChatBoxBorder((Gui)this, true, (int)(255 * chatOpacity));
				} else {
					ChatBox.setUnfocusedHeight(this.chatHeight);
					ChatBox.drawChatBoxBorder((Gui)this, false, currentOpacity);
					tc.pollForUnread(this, currentTick);
				}
			}
			GL11.glPopMatrix();
		}
		this.mc.fontRenderer.setUnicodeFlag(TabbyChat.defaultUnicode);
	}
	
	public @Override ChatClickData func_73766_a(int clickX, int clickY) {
		if(!this.getChatOpen()) return null;
		else {
			ChatClickData returnMe = null;
			Point adjClick = ChatBox.scaleMouseCoords(clickX, clickY);
			int clickXRel = Math.abs(adjClick.x - ChatBox.current.x);
			int clickYRel = Math.abs(adjClick.y - ChatBox.current.y);
			if(clickXRel >= 0 && clickYRel >= 0) {
				chatReadLock.lock();
				try {
					int displayedLines = Math.min(this.getHeightSetting() / 9, this.chatLines.size());
					if(clickXRel <= ChatBox.getChatWidth()
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
		if(tc.enabled()) {
			if(!backupFlag) tc.checkServer();
			maxWidth = ChatBox.getMinChatWidth();
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
		if(tc.enabled() && !optionalDeletion && !backupFlag) {
			tc.processChat(multiLineChat);
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
		if(tc.serverDataLock.availablePermits() < 1) return;
		int maxChats = tc.enabled() ? Integer.parseInt(TabbyChat.advancedSettings.chatScrollHistory.getValue()) : 100;
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
	
	public int GetChatSize() {
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
		if (tc.enabled()) {
			return ChatBox.getChatHeight();
		} else
			return func_96130_b(this.mc.gameSettings.chatHeightFocused);
	}
	
	public static GuiNewChatTC getInstance() {
		if(instance == null) {
			instance = new GuiNewChatTC(Minecraft.getMinecraft());
			tc = TabbyChat.getInstance(instance);
			TabbyChatUtils.hookIntoChat(instance);
			if (!tc.enabled()) tc.disable();
			else tc.enable();
		}
		return instance;
	}

	public float getScaleSetting() {
		//return this.func_96131_h();
		float theSetting = this.func_96131_h();
		return Math.round(theSetting * 100.0f) / 100.0f;
	}

	public @Override List getSentMessages() {
		return this.sentMessages;
	}

	public void mergeChatLines(ChatChannel _new) {
		int newSize = _new.getChatLogSize();
		chatWriteLock.lock();
		try {
			List<TCChatLine> _current = this.chatLines;
			if (_new == null || newSize <= 0) return;

			int _c = 0;
			int _n = 0;
			int dt = 0;
			while (_n < newSize && _c < _current.size()) {
				dt = _new.getChatLine(_n).getUpdatedCounter() - _current.get(_c).getUpdatedCounter();
				if (dt > 0) {
					_current.add(_c, _new.getChatLine(_n));
					_n++;
				} else if (dt == 0) {
					if (_current.get(_c).equals(_new.getChatLine(_n)) || _current.get(_c).getChatLineString().equals(_new.getChatLine(_n).getChatLineString())) {
						_c++;
						_n++;
					} else
						_c++;
				} else
					_c++;
			}

			while (_n < newSize) {
				_current.add(_current.size(), _new.getChatLine(_n));
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
		if(tc.enabled()) {
			float scaleFactor = 1.0f;
			maxLineDisplay = Math.round(ChatBox.getChatHeight() / 9.0f);
			if(!this.getChatOpen()) maxLineDisplay = Math.round(maxLineDisplay * TabbyChat.advancedSettings.chatBoxUnfocHeight.getValue() / 100.0f);
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
		int clsize = 0;
		boolean addInstead = false;
		chatReadLock.lock();
		try {
			clsize = Math.min(this.chatLines.size(), this.backupLines.size());
		} finally {
			chatReadLock.unlock();
		}
		if(_pos + _add.size() > clsize) {
			addInstead = true;
		}
		
		chatWriteLock.lock();
		try {
			int j;
			for (int i=_add.size()-1; i>=0; i--) {
				j = _add.size() - i - 1;
				if(addInstead) {
					this.chatLines.add(_add.get(i));
					this.backupLines.add(_add.get(i));
				} else {
					this.chatLines.set(_pos+j, _add.get(i));
					this.backupLines.set(_pos+j, _add.get(i));
				}
			}
		} finally {
			chatWriteLock.unlock();
		}

	}

	public void setVisChatLines(int _move) {
		this.scrollOffset = _move;
	}
}
