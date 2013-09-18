package acs.tabbychat.core;

import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import acs.tabbychat.compat.EmoticonsCompat;
import acs.tabbychat.compat.MacroKeybindCompat;
import acs.tabbychat.gui.ChatBox;
import acs.tabbychat.gui.ChatButton;
import acs.tabbychat.gui.ChatChannelGUI;
import acs.tabbychat.gui.ChatScrollBar;
import acs.tabbychat.gui.PrefsButton;
import acs.tabbychat.util.TabbyChatUtils;

import net.minecraft.src.Minecraft;
import net.minecraft.src.ChatClickData;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiConfirmOpenLink;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Packet19EntityAction;
import net.minecraft.src.Packet203AutoComplete;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.StatCollector;
import net.minecraft.src.StringTranslate;

public class GuiChatTC extends GuiChat {
	public String historyBuffer = "";
	public String defaultInputFieldText = "";
	public int sentHistoryCursor2 = -1;
	private boolean playerNamesFound = false;
	private boolean waitingOnPlayerNames = false;
	private int playerNameIndex = 0;
	private List foundPlayerNames = new ArrayList();
	private URI clickedURI2 = null;	
	public GuiTextField inputField2;
	public List<GuiTextField> inputList = new ArrayList<GuiTextField>(3);
	public ChatScrollBar scrollBar;
	public GuiButton selectedButton2 = null;
	public int eventButton2 = 0;
    public long field_85043_c2 = 0L;
    public int field_92018_d2 = 0;
    public float zLevel2 = 0.0F;
    private static ScaledResolution sr;
    private int spellCheckCounter = 0;
	public TabbyChat tc;
	public GuiNewChatTC gnc;
	
	public GuiChatTC() {
		super();
		this.mc = Minecraft.getMinecraft();
		sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		this.fontRenderer = this.mc.fontRenderer;
		this.gnc = GuiNewChatTC.getInstance();
		this.tc = this.gnc.tc;
		EmoticonsCompat.load();
		MacroKeybindCompat.load();
	}

	public GuiChatTC(String par1Str) {
		this();
		this.defaultInputFieldText = par1Str;
	}
	
	public void actionPerformed(GuiButton par1GuiButton) {
		// Attempt Emoticons actionPerformed if present
		EmoticonsCompat.actionPerformed(par1GuiButton, this.buttonList, this.inputField2);

		if(par1GuiButton instanceof PrefsButton && par1GuiButton.id == 1) this.playerWakeUp();
		
		if(!ChatButton.class.isInstance(par1GuiButton)) return;
		ChatButton _button = (ChatButton)par1GuiButton;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && tc.channelMap.get("*") == _button.channel) {
			this.mc.displayGuiScreen(TabbyChat.generalSettings);
			return;
		}
		if (!this.tc.enabled()) return;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			this.buttonList.remove(_button);
			this.tc.channelMap.remove(_button.channel.getTitle());
		} else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			if (!_button.channel.active) {
				this.gnc.mergeChatLines(_button.channel);
				_button.channel.unread = false;
			}
			_button.channel.active = !_button.channel.active;
			if (!_button.channel.active)
				this.tc.resetDisplayedChat();
		} else {
			List<String> preActiveTabs = this.tc.getActive();
			for (ChatChannel chan : this.tc.channelMap.values()) {
				if (!_button.equals(chan.tab))
					chan.active = false;
			}
			if (!_button.channel.active) {
				this.scrollBar.scrollBarMouseWheel();
				if(preActiveTabs.size() == 1) {
					this.checkCommandPrefixChange(this.tc.channelMap.get(preActiveTabs.get(0)), _button.channel);
				} else {
					_button.channel.active = true;
					_button.channel.unread = false;
				}
			}
			this.tc.resetDisplayedChat();
		}
	}
	
	protected void addChannelLive(ChatChannel brandNewChan) {
		if(!this.buttonList.contains(brandNewChan.tab)) {
			this.buttonList.add(brandNewChan.tab);
		}
	}
	
	public void checkCommandPrefixChange(ChatChannel oldChan, ChatChannel newChan) {
		String oldPrefix = oldChan.cmdPrefix.trim();
		String currentInput = this.inputField2.getText().trim();
		if(currentInput.equals(oldPrefix) || currentInput.length() == 0) {
			String newPrefix = newChan.cmdPrefix.trim();
			if(newPrefix.length() > 0 && !newChan.hidePrefix) this.inputField2.setText(newPrefix + " ");
			else this.inputField2.setText("");
		}
		oldChan.active = false;
		newChan.active = true;
		newChan.unread = false;
	}

	public @Override void completePlayerName() {
		String textBuffer;
		if(this.playerNamesFound) {
			this.inputField2.deleteFromCursor(this.inputField2.func_73798_a(-1, this.inputField2.getCursorPosition(), false) - this.inputField2.getCursorPosition());
			if(this.playerNameIndex >= this.foundPlayerNames.size()) {
				this.playerNameIndex = 0;
			}
		} else {
			int prevWordIndex = this.inputField2.func_73798_a(-1, this.inputField2.getCursorPosition(), false);
			this.foundPlayerNames.clear();
			this.playerNameIndex = 0;
			String nameStart = this.inputField2.getText().substring(prevWordIndex).toLowerCase();
			textBuffer = this.inputField2.getText().substring(0, this.inputField2.getCursorPosition());
			this.func_73893_a(textBuffer, nameStart);
			if(this.foundPlayerNames.isEmpty()) {
				return;
			}

			this.playerNamesFound = true;
			this.inputField2.deleteFromCursor(prevWordIndex - this.inputField2.getCursorPosition());
		}

		if(this.foundPlayerNames.size() > 1) {
			StringBuilder _sb = new StringBuilder();

			for(Iterator _iter = this.foundPlayerNames.iterator(); _iter.hasNext(); _sb.append(textBuffer)) {
				textBuffer = (String)_iter.next();
				if(_sb.length() > 0) {
					_sb.append(", ");
				}
			}

			this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(_sb.toString(), 1);
		}

		this.inputField2.writeText((String)this.foundPlayerNames.get(this.playerNameIndex++));
	}

	public @Override void confirmClicked(boolean zeroId, int worldNum) {
		if(worldNum == 0) {
			if(zeroId) this.func_73896_a(this.clickedURI2);
			this.clickedURI2 = null;
			this.mc.displayGuiScreen(this);
		}
	}
	
    public @Override void drawScreen(int cursorX, int cursorY, float pointless) {
		if (this.tc.enabled() && TabbyChat.advancedSettings.forceUnicode.getValue()) this.fontRenderer.setUnicodeFlag(true);
		sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		this.width = sr.getScaledWidth();
		this.height = sr.getScaledHeight();
		
		// Calculate positions of currently-visible input fields
		int inputHeight = 0;
		for(int i=0; i<this.inputList.size(); i++) {
			if(this.inputList.get(i).getVisible()) inputHeight += 12;
		}
		
		// Draw text fields and background
		int bgWidth = (MacroKeybindCompat.present) ? this.width - 24 : this.width - 2; 
		drawRect(2, this.height-2-inputHeight, bgWidth, this.height-2, Integer.MIN_VALUE);
		for(GuiTextField field : this.inputList) {
			if(field.getVisible()) field.drawTextBox();
		}
		
		// Draw current message length indicator
		if(this.tc.enabled()) {
			String requiredSends = ((Integer)this.getCurrentSends()).toString();
			int sendsX = sr.getScaledWidth() - 12;
			if(MacroKeybindCompat.present) sendsX -= 22; 
			this.fontRenderer.drawStringWithShadow(requiredSends, sendsX, this.height-inputHeight, 0x707070);
		}
		
		// Update & draw spell check data
		if(TabbyChat.generalSettings.spellCheckEnable.getValue() && this.inputField2.getText().length() > 0) {
			TabbyChat.spellChecker.drawErrors(this, this.inputList);
			if(this.spellCheckCounter == 200) {
				TabbyChat.spellChecker.update(this.inputList);
				this.spellCheckCounter = 0;
			}
			this.spellCheckCounter++;
		}
		
		// Update chat tabs (add to buttonlist)
		ChatBox.updateTabs(this.tc.channelMap);

		// Determine appropriate scaling for chat tab size and location
		float scaleSetting = this.gnc.getScaleSetting();
		GL11.glPushMatrix();
		float scaleOffsetX = ChatBox.current.x * (1.0f - scaleSetting);
		float scaleOffsetY = (this.gnc.sr.getScaledHeight() + ChatBox.current.y) * (1.0f - scaleSetting);
		GL11.glTranslatef(scaleOffsetX, scaleOffsetY, 1.0f);
		GL11.glScalef(scaleSetting, scaleSetting, 1.0f);
		
		// Draw chat tabs
		GuiButton _button;
		for(int i=0; i<this.buttonList.size(); i++) {
			_button = (GuiButton)this.buttonList.get(i);
			if(_button instanceof PrefsButton && _button.id == 1) {
				if(mc.thePlayer != null && !mc.thePlayer.isPlayerSleeping()) {
					this.buttonList.remove(_button);
					continue;
				}
			}
			if(EmoticonsCompat.present) {
				if(!EmoticonsCompat.emoteButtonClass.isInstance(_button) && _button.id != 54 && _button.id != 53)
					_button.drawButton(this.mc, cursorX, cursorY);
			} else {
				_button.drawButton(this.mc, cursorX, cursorY);
			}				
		}
		
		GL11.glPopMatrix();
		this.fontRenderer.setUnicodeFlag(TabbyChat.defaultUnicode);
		
// Attempt Macro/Keybind drawScreen if present
		MacroKeybindCompat.drawScreen(cursorX, cursorY, this);		
// Attempt Emoticons drawScreen if present
		EmoticonsCompat.drawScreen(cursorX, cursorY, pointless, this, this.buttonList);
	}
	
	public void func_73893_a(String nameStart, String buffer) {
		if(nameStart.length() >= 1) {
			this.mc.thePlayer.sendQueue.addToSendQueue(new Packet203AutoComplete(nameStart));
			this.waitingOnPlayerNames = true;
		}
	}
	
	public @Override void func_73894_a(String[] par1ArrayOfStr) {
		if(this.waitingOnPlayerNames) {
			this.foundPlayerNames.clear();
			String[] _copy = par1ArrayOfStr;
			int _len = par1ArrayOfStr.length;
			
			for(int i=0; i<_len; ++i) {
				String name = _copy[i];
				if(name.length() > 0) {
					this.foundPlayerNames.add(name);
					TabbyChat.spellChecker.addToIgnoredWords(name);
				}
			}
			
			if(this.foundPlayerNames.size() > 0) {
				this.playerNamesFound = true;
				this.completePlayerName();
			}
		}
	}
	
	public void func_73896_a(URI _uri) {
		try {
			Class desktop = Class.forName("java.awt.Desktop");
			Object theDesktop = desktop.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
			desktop.getMethod("browse", new Class[]{URI.class}).invoke(theDesktop, new Object[]{_uri});
		} catch (Throwable t) {
			t.printStackTrace();
		}
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
			return (lng + 100 - 1) / 100;
	}

	public int getFocusedFieldInd() {
		int _s = this.inputList.size();
		for (int i=0; i<_s; i++) {
			if (this.inputList.get(i).isFocused() && this.inputList.get(i).getVisible())
				return i;
		}
		return 0;
	}
	
	public @Override void getSentHistory(int _dir) {
		int loc = this.sentHistoryCursor2 + _dir;
		int historyLength = this.gnc.getSentMessages().size();
		loc = Math.max(0, loc);
		loc = Math.min(historyLength, loc);
		if(loc == this.sentHistoryCursor2) return;
		if(loc == historyLength) {
			this.sentHistoryCursor2 = historyLength;
			this.setText(new StringBuilder(""), 1);
		} else {
			if(this.sentHistoryCursor2 == historyLength) this.historyBuffer = this.inputField2.getText();
			StringBuilder _sb = new StringBuilder((String)this.gnc.getSentMessages().get(loc));
			this.setText(_sb, _sb.length());
			this.sentHistoryCursor2 = loc;
		}
	}
	
	public @Override void handleMouseInput() {
		// Allow chatbox dragging    
	    if(ChatBox.resizing) {
	    	if(!Mouse.isButtonDown(0)) ChatBox.resizing = false;
	    	else ChatBox.handleMouseResize(Mouse.getEventX(), Mouse.getEventY());
	    	return;
	    } else if(ChatBox.dragging) {
	    	if(!Mouse.isButtonDown(0)) ChatBox.dragging = false;
	    	else ChatBox.handleMouseDrag(Mouse.getEventX(), Mouse.getEventY());
			return;
		}
	    
		if(Mouse.getEventButton() == 0 && Mouse.isButtonDown(0)) {
			if(ChatBox.resizeHovered() && !ChatBox.dragging) {
				ChatBox.startResizing(Mouse.getEventX(), Mouse.getEventY());
			} else if(ChatBox.pinHovered()) {
				ChatBox.pinned = !ChatBox.pinned;
			} else if(ChatBox.tabTrayHovered(Mouse.getEventX(), Mouse.getEventY()) && !ChatBox.resizing) {
				ChatBox.startDragging(Mouse.getEventX(), Mouse.getEventY());	
			}
		}
		
		int wheelDelta = Mouse.getEventDWheel();
		if(wheelDelta != 0) {
			wheelDelta = Math.min(1, wheelDelta);
			wheelDelta = Math.max(-1, wheelDelta);
			if(!isShiftKeyDown()) wheelDelta *= 7;
			
			if(ChatBox.anchoredTop) this.gnc.scroll(-wheelDelta);
			else this.gnc.scroll(wheelDelta);
			if(this.tc.enabled()) this.scrollBar.scrollBarMouseWheel();
		} else if(this.tc.enabled()) this.scrollBar.handleMouse();
		
		if(mc.currentScreen.getClass() != GuiChat.class) super.handleMouseInput();
	}
	
	public @Override void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.clear();
		this.inputList.clear();
		sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		this.width = sr.getScaledWidth();
		this.height = sr.getScaledHeight();
		this.tc.checkServer();
		if(this.tc.enabled()) {
			if(this.scrollBar == null) this.scrollBar = new ChatScrollBar();
			for(ChatChannel chan : this.tc.channelMap.values()) {
				this.buttonList.add(chan.tab);
			}
		} else {
			this.buttonList.add(this.tc.channelMap.get("*").tab);
		}
		
		this.sentHistoryCursor2 = this.gnc.getSentMessages().size();
		int textFieldWidth = (MacroKeybindCompat.present) ? this.width - 26 : this.width - 4; 
		this.inputField2 = new GuiTextField(this.fontRenderer, 4, this.height - 12, textFieldWidth, 12);
		this.inputField2.setMaxStringLength(500);
		this.inputField2.setEnableBackgroundDrawing(false);
		this.inputField2.setFocused(true);
		this.inputField2.setText(this.defaultInputFieldText);
		this.inputField2.setCanLoseFocus(true);
		this.inputList.add(0, this.inputField2);
		if(!tc.enabled()) return;
		
		GuiTextField placeholder;
		for(int i=1; i<3; i++) {
			placeholder = new GuiTextField(this.fontRenderer, 4, this.height - 12*(i+1), textFieldWidth, 12);
			placeholder.setMaxStringLength(500);
			placeholder.setEnableBackgroundDrawing(false);
			placeholder.setFocused(false);
			placeholder.setText("");
			placeholder.setCanLoseFocus(true);
			placeholder.setVisible(false);
			this.inputList.add(i,placeholder);
		}
		
		if(this.tc.enabled()) {
			List<String> activeTabs = this.tc.getActive();
			if(activeTabs.size() != 1) {
				this.inputField2.setText("");
			} else {
				String thePrefix = this.tc.channelMap.get(activeTabs.get(0)).cmdPrefix.trim();
				boolean prefixHidden =  this.tc.channelMap.get(activeTabs.get(0)).hidePrefix;
				if(thePrefix.length() > 0 && !prefixHidden) this.inputField2.setText(this.tc.channelMap.get(activeTabs.get(0)).cmdPrefix.trim() + " ");
			}
			ChatBox.enforceScreenBoundary(ChatBox.current);
		}
		
		if(mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.isPlayerSleeping()) {
			PrefsButton leaveBed = new PrefsButton(1, this.width/2 - 100, this.height - 50, 200, 14, StatCollector.translateToLocal("multiplayer.stopSleeping"), 0x55ffffff);
			this.buttonList.add(leaveBed);
		}
		
		// Initialize Emoticons screen if present
		EmoticonsCompat.initGui(this.buttonList);
	}
	
	public void insertCharsAtCursor(String _chars) {
		StringBuilder msg = new StringBuilder();
		int cPos = 0;
		boolean cFound = false;
		for (int i=this.inputList.size()-1; i>=0; i--) {
			msg.append(this.inputList.get(i).getText());
			if (this.inputList.get(i).isFocused()) {
				cPos += this.inputList.get(i).getCursorPosition();
				cFound = true;
			} else if (!cFound) {
				cPos += this.inputList.get(i).getText().length();
			}			
		}
		if (this.fontRenderer.getStringWidth(msg.toString()) + this.fontRenderer.getStringWidth(_chars) < (sr.getScaledWidth()-20)*this.inputList.size()) {
			msg.insert(cPos, _chars);
			this.setText(msg, cPos+_chars.length());
		}
	}

	public @Override void keyTyped(char _char, int _code) {
		this.waitingOnPlayerNames = false;
		
		if(_code != Keyboard.KEY_TAB) this.playerNamesFound = false;
		switch (_code) {
		// TAB: execute vanilla name completion
		case Keyboard.KEY_TAB:
			if(GuiScreen.isCtrlKeyDown()) {
				// CTRL+SHIFT+TAB: switch active tab to previous
				if(GuiScreen.isShiftKeyDown()) {
					tc.activatePrev();
				// CTRL+TAB: switch active tab to next
				} else tc.activateNext();
				break;
			}
			this.completePlayerName();
			break;
		// ESCAPE: close the chat interface
		case Keyboard.KEY_ESCAPE:
			this.mc.displayGuiScreen((GuiScreen)null);
			break;
		// RETURN: send chat to server
		case Keyboard.KEY_RETURN:
			StringBuilder _msg = new StringBuilder(1500);
			for(int i=this.inputList.size()-1; i>=0; i--) _msg.append(this.inputList.get(i).getText());
			if(_msg.toString().length() > 0) {
				TabbyChatUtils.writeLargeChat(_msg.toString());
				for(int i=1; i<this.inputList.size(); i++) {
					this.inputList.get(i).setText("");
					this.inputList.get(i).setVisible(false);
				}
			}
			if(!tc.enabled() || !ChatBox.pinned) this.mc.displayGuiScreen((GuiScreen)null);
			else {
				this.resetInputFields();
			}
			break;
		// UP: if currently in multi-line chat, move into the above textbox.  Otherwise, go back one in the sent history (forced by Ctrl)
		case Keyboard.KEY_UP:
			if(GuiScreen.isCtrlKeyDown()) this.getSentHistory(-1);
			else {
				int foc = this.getFocusedFieldInd();
				if(foc+1 < this.inputList.size() && this.inputList.get(foc+1).getVisible()) {
					int gcp = this.inputList.get(foc).getCursorPosition();
					int lng = this.inputList.get(foc+1).getText().length();
					int newPos = Math.min(gcp, lng);
					this.inputList.get(foc).setFocused(false);
					this.inputList.get(foc+1).setFocused(true);
					this.inputList.get(foc+1).setCursorPosition(newPos);
				} else this.getSentHistory(-1);
			}
			break;
		// DOWN: if currently in multi-line chat, move into the below textbox.  Otherwise, go forward one in the sent history (force by Ctrl)
		case Keyboard.KEY_DOWN:
			if(GuiScreen.isCtrlKeyDown()) this.getSentHistory(1);
			else {
				int foc = this.getFocusedFieldInd();
				if(foc-1 >= 0 && this.inputList.get(foc-1).getVisible()) {
					int gcp = this.inputList.get(foc).getCursorPosition();
					int lng = this.inputList.get(foc-1).getText().length();
					int newPos = Math.min(gcp, lng);
					this.inputList.get(foc).setFocused(false);
					this.inputList.get(foc-1).setFocused(true);
					this.inputList.get(foc-1).setCursorPosition(newPos);
				} else this.getSentHistory(1);
			}
			break;
		// PAGE UP: scroll up through chat
		case Keyboard.KEY_PRIOR:
			this.gnc.scroll(19);
			if(this.tc.enabled()) this.scrollBar.scrollBarMouseWheel();
			break;
		// PAGE DOWN: scroll down through chat
		case Keyboard.KEY_NEXT:
			this.gnc.scroll(-19);
			if(this.tc.enabled()) this.scrollBar.scrollBarMouseWheel();
			break;
		// BACKSPACE: delete previous character, minding potential contents of other input fields
		case Keyboard.KEY_BACK:
			if(this.inputField2.isFocused() && this.inputField2.getCursorPosition() > 0) this.inputField2.textboxKeyTyped(_char, _code);
			else this.removeCharsAtCursor(-1);
			break;
		// DELETE: delete next character, minding potential contents of other input fields
		case Keyboard.KEY_DELETE:
			if(this.inputField2.isFocused()) this.inputField2.textboxKeyTyped(_char, _code);
			else this.removeCharsAtCursor(1);
			break;
		// LEFT/RIGHT: move the cursor
		case Keyboard.KEY_LEFT:
		case Keyboard.KEY_RIGHT:
			this.inputList.get(this.getFocusedFieldInd()).textboxKeyTyped(_char, _code);
			break;
		default:
			// CTRL + NUM1-9: Make the numbered tab active
			if(GuiScreen.isCtrlKeyDown() && !Keyboard.isKeyDown(Keyboard.KEY_LMENU) && !Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
				if(_code > 1 && _code < 12) {
					tc.activateIndex(_code-1);
				// CTRL+O: open options
				} else if(_code == Keyboard.KEY_O) {
					this.mc.displayGuiScreen(this.tc.generalSettings);
				} else {
					this.inputField2.textboxKeyTyped(_char, _code);
				}
			// Keypress will not trigger overflow, send to default input field
			} else if(this.inputField2.isFocused() && this.fontRenderer.getStringWidth(this.inputField2.getText()) < sr.getScaledWidth()-20) {
				this.inputField2.textboxKeyTyped(_char, _code);
			// Keypress will trigger overflow, send through helper function
			} else {
				this.insertCharsAtCursor(Character.toString(_char));
			}
		}
	}

	public @Override void mouseClicked(int _x, int _y, int _button) {		
		if(_button == 0 && this.mc.gameSettings.chatLinks) {
			ChatClickData ccd = this.gnc.func_73766_a(Mouse.getX(), Mouse.getY());
			if(ccd != null) {
				URI url = ccd.getURI();
				if(url != null) {
					if(this.mc.gameSettings.chatLinksPrompt) {
						this.clickedURI2 = url;
						this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, ccd.getClickedUrl(), 0, false));
					} else this.func_73896_a(url);
					return;
				}
			}
		}
		
		for(int i=0; i<this.inputList.size(); i++) {
			if(_y>=this.height-12*(i+1) && this.inputList.get(i).getVisible()) {
				this.inputList.get(i).setFocused(true);
				for(GuiTextField field : this.inputList) {
					if(field != this.inputList.get(i)) field.setFocused(false);
				}
				this.inputList.get(i).mouseClicked(_x, _y, _button);
				break;
			}
		}
		
		// Pass click info to Macro/Keybind mod if present
		if(MacroKeybindCompat.contextMenuClicked(_x, _y, _button, this)) return;
		if(MacroKeybindCompat.controlClicked(_x, _y, _button, this)) return;
		
		// Replicating GuiScreen's mouseClicked method since 'super' won't work
		for(GuiButton _guibutton : (List<GuiButton>)this.buttonList) {
			if(ChatButton.class.isInstance(_guibutton) || _guibutton.id <= 2) {
				if(_guibutton.mousePressed(this.mc, _x, _y)) {
					if(_button == 0) {
						this.selectedButton2 = _guibutton;
						this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
						this.actionPerformed(_guibutton);
						return;
					} else if (_button == 1) {
						ChatButton _cb = (ChatButton)_guibutton;
						if(_cb.channel == this.tc.channelMap.get("*")) return;
						this.mc.displayGuiScreen(new ChatChannelGUI(_cb.channel));
						return;
					}
				}
			} else {
				if(_guibutton.mousePressed(this.mc, _x-EmoticonsCompat.emoteOffsetX, _y) && _button == 0) {
					this.selectedButton2 = _guibutton;
					this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
					this.actionPerformed(_guibutton);
					return;
				}
			}
		}
	}

	public @Override void mouseMovedOrUp(int _x, int _y, int _button)
    {
        if (this.selectedButton2 != null && _button == 0)
        {
            this.selectedButton2.mouseReleased(_x, _y);
            this.selectedButton2 = null;
        }
    }
	
	public @Override void onGuiClosed() {
		ChatBox.dragging = false;
		ChatBox.resizing = false;
	}
	
	private void playerWakeUp() {
		NetClientHandler var1 = this.mc.thePlayer.sendQueue;
		var1.addToSendQueue(new Packet19EntityAction(this.mc.thePlayer, 3));
	}

	public void removeCharsAtCursor(int _del) {
		StringBuilder msg = new StringBuilder();
		int cPos = 0;
		boolean cFound = false;
		for (int i=this.inputList.size()-1; i>=0; i--) {
			msg.append(this.inputList.get(i).getText());
			if (this.inputList.get(i).isFocused()) {
				cPos += this.inputList.get(i).getCursorPosition();
				cFound = true;
			} else if (!cFound) {
				cPos += this.inputList.get(i).getText().length();
			}
		}
		int other = cPos + _del;
		other = Math.min(msg.length()-1, other);
		other = Math.max(0, other);
		if (other < cPos) {
			msg.replace(other, cPos, "");
			this.setText(msg, other);
		} else if (other > cPos) {
			msg.replace(cPos, other, "");
			this.setText(msg, cPos);
		} else
			return;
	}
	
	public void resetInputFields() {
		for(GuiTextField gtf : this.inputList) {
			gtf.setText("");
			gtf.setFocused(false);
			gtf.setVisible(false);
		}
		this.inputField2.setFocused(true);
		this.inputField2.setVisible(true);
		
		List<String> actives = tc.getActive();
		if(actives.size() == 1) {
			ChatChannel current = tc.channelMap.get(actives.get(0));
			String pre = current.cmdPrefix.trim();
			boolean hidden = current.hidePrefix;
			if(pre.length() > 0 && !hidden) {
				this.inputField2.setText(pre + " ");
			}
		}
		this.inputField2.setCursorPositionEnd();
		this.sentHistoryCursor2 = this.gnc.getSentMessages().size();
	}

	public void setText(StringBuilder txt, int pos) {
		List<String> txtList = this.stringListByWidth(txt, sr.getScaledWidth()-20);

		int strings = Math.min(txtList.size()-1, this.inputList.size()-1);
		for (int i=strings; i>=0; i--) {
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
			this.inputField2.setCursorPositionEnd();
		}
		if (this.inputList.size() > txtList.size()) {
			for (int j=txtList.size(); j<this.inputList.size(); j++) {
				this.inputList.get(j).setText("");
				this.inputList.get(j).setFocused(false);
				this.inputList.get(j).setVisible(false);
			}
		}
		if (!this.inputField2.getVisible()) {
			this.inputField2.setVisible(true);
			this.inputField2.setFocused(true);
		}
	}

	public List<String> stringListByWidth(StringBuilder _sb, int _w) {
		List<String> result = new ArrayList<String>(5);
		int _len = 0;
		int _cw;
		StringBuilder bucket = new StringBuilder(_sb.length());
		for (int ind=0; ind<_sb.length(); ind++) {
			_cw = this.fontRenderer.getCharWidth(_sb.charAt(ind));
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

	public @Override void updateScreen() {
		this.inputField2.updateCursorCounter();
	}
}
