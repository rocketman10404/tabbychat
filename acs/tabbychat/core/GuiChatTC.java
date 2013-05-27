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
import acs.tabbychat.util.TabbyChatUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ChatClickData;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiConfirmOpenLink;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.Packet203AutoComplete;
import net.minecraft.src.ScaledResolution;

public class GuiChatTC extends GuiChat {
	public String historyBuffer = "";
	public String defaultInputFieldText = "";
	public int sentHistoryCursor = -1;
	private boolean playerNamesFound = false;
	private boolean waitingOnPlayerNames = false;
	private int playerNameIndex = 0;
	private List foundPlayerNames = new ArrayList();
	private URI clickedURI = null;	
	public GuiTextField inputField;
	public List<GuiTextField> inputList = new ArrayList<GuiTextField>(3);
	public ChatScrollBar scrollBar;
	public GuiButton selectedButton = null;
	public int eventButton = 0;
    public long field_85043_c = 0L;
    public int field_92018_d = 0;
    public float zLevel = 0.0F;
    public ScaledResolution sr;    
	public static GuiChatTC me;
	public static final TabbyChat tc = TabbyChat.instance;
	
	public GuiChatTC() {
		super();
		this.mc = Minecraft.getMinecraft();
		this.fontRenderer = this.mc.fontRenderer;
		me = this;
		this.sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
		EmoticonsCompat.load();
		MacroKeybindCompat.load();
	}

	public GuiChatTC(String par1Str) {
		this();
		this.defaultInputFieldText = par1Str;
	}
	
	public void actionPerformed(GuiButton par1GuiButton) {
		// Attempt Emoticons actionPerformed if present
		EmoticonsCompat.actionPerformed(par1GuiButton, this.buttonList, this.inputField);

		if(!ChatButton.class.isInstance(par1GuiButton)) return;
		ChatButton _button = (ChatButton)par1GuiButton;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && tc.channelMap.get("*") == _button.channel) {
			this.mc.displayGuiScreen(TabbyChat.generalSettings);
			return;
		}
		if (!tc.enabled()) return;
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {						
			tc.channelMap.remove(_button.channel.title);
		} else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			if (!_button.channel.active) {
				tc.gnc.mergeChatLines(_button.channel.chatLog);
				_button.channel.unread = false;
			}
			_button.channel.active = !_button.channel.active;
			if (!_button.channel.active)
				tc.resetDisplayedChat();
		} else {
			List<String> preActiveTabs = tc.getActive();
			for (ChatChannel chan : tc.channelMap.values()) {
				if (!_button.equals(chan.tab))
					chan.active = false;
			}
			if (!_button.channel.active) {
				this.scrollBar.scrollBarMouseWheel();
				if(preActiveTabs.size() == 1) {
					String oldPrefix = tc.channelMap.get(preActiveTabs.get(0)).cmdPrefix.trim();
					if(this.inputField.getText().trim().equals(oldPrefix)) {
						String newPrefix = _button.channel.cmdPrefix.trim();
						if(newPrefix.length() > 0) this.inputField.setText(_button.channel.cmdPrefix.trim() + " ");
						else this.inputField.setText("");
					}
				}
				_button.channel.active = true;
				_button.channel.unread = false;
			}
			tc.resetDisplayedChat();
		}
	}

	public @Override void completePlayerName() {
		String textBuffer;
		if(this.playerNamesFound) {
			this.inputField.deleteFromCursor(this.inputField.func_73798_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
			if(this.playerNameIndex >= this.foundPlayerNames.size()) {
				this.playerNameIndex = 0;
			}
		} else {
			int prevWordIndex = this.inputField.func_73798_a(-1, this.inputField.getCursorPosition(), false);
			this.foundPlayerNames.clear();
			this.playerNameIndex = 0;
			String nameStart = this.inputField.getText().substring(prevWordIndex).toLowerCase();
			textBuffer = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
			this.func_73893_a(textBuffer, nameStart);
			if(this.foundPlayerNames.isEmpty()) {
				return;
			}

			this.playerNamesFound = true;
			this.inputField.deleteFromCursor(prevWordIndex - this.inputField.getCursorPosition());
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

		this.inputField.writeText((String)this.foundPlayerNames.get(this.playerNameIndex++));
	}

	public @Override void confirmClicked(boolean zeroId, int worldNum) {
		if(worldNum == 0) {
			if(zeroId) this.func_73896_a(this.clickedURI);
			this.clickedURI = null;
			this.mc.displayGuiScreen(this);
		}
	}
	
	public void drawChatTabs() {
		// Store non-TabbyChat buttons in external list
		List<GuiButton> tmpBin = new ArrayList();
		for(GuiButton _button : (List<GuiButton>)this.buttonList) {
			if(!ChatButton.class.isInstance(_button)) tmpBin.add(_button);
		}
		// Re-create buttonList of tab buttons
		this.buttonList.clear();
		//tc.updateButtonLocations(this.sr);
		ChatBox.updateTabs(TabbyChat.instance.channelMap, this.sr);
		for (ChatChannel _chan : tc.channelMap.values()) {
			this.buttonList.add(_chan.tab);
		}
		// Add external buttons back on button list
		this.buttonList.addAll(tmpBin);
	}
	
    public @Override void drawScreen(int cursorX, int cursorY, float pointless) {
		boolean unicodeStore = this.fontRenderer.getUnicodeFlag();
		if (tc.enabled() && TabbyChat.advancedSettings.forceUnicode.getValue()) this.fontRenderer.setUnicodeFlag(true);
		this.width = this.sr.getScaledWidth();
		this.height = this.sr.getScaledHeight();
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
		if(tc.enabled()) {
			String requiredSends = ((Integer)this.getCurrentSends()).toString();
			int sendsX = this.sr.getScaledWidth() - 12;
			if(MacroKeybindCompat.present) sendsX -= 22; 
			this.fontRenderer.drawStringWithShadow(requiredSends, sendsX, this.height-inputHeight, 0x707070);
		}
		// Draw chat tabs (add to buttonlist) & scroll bar if necessary
		if(!this.mc.isSingleplayer()) this.drawChatTabs();
		if(tc.enabled()) this.scrollBar.drawScrollBar();
		// Determine appropriate scaling for chat tab size and location
		float scaleSetting = tc.gnc.getScaleSetting();
		GL11.glPushMatrix();
		float scaleOffset = (float)(this.sr.getScaledHeight() - 28) * (1.0f - scaleSetting);
		GL11.glTranslatef(0.0f, scaleOffset, 1.0f);
		GL11.glScalef(scaleSetting, scaleSetting, 1.0f);
		// Draw chat tabs
		for(GuiButton _button : (List<GuiButton>)this.buttonList) {
			if(ChatButton.class.isInstance(_button)) _button.drawButton(this.mc, cursorX, cursorY);
			else if(_button.id == 0 || _button.id == 2) _button.drawButton(this.mc, cursorX, cursorY);
		}
		GL11.glPopMatrix();
		this.fontRenderer.setUnicodeFlag(unicodeStore);
		
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
				if(name.length() > 0) this.foundPlayerNames.add(name);
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
		int loc = this.sentHistoryCursor + _dir;
		int historyLength = tc.gnc.getSentMessages().size();
		loc = Math.max(0, loc);
		loc = Math.min(historyLength, loc);
		if(loc == this.sentHistoryCursor) return;
		if(loc == historyLength) {
			this.sentHistoryCursor = historyLength;
			this.setText(new StringBuilder(""), 1);
		} else {
			if(this.sentHistoryCursor == historyLength) this.historyBuffer = this.inputField.getText();
			StringBuilder _sb = new StringBuilder((String)tc.gnc.getSentMessages().get(loc));
			this.setText(_sb, _sb.length());
			this.sentHistoryCursor = loc;
		}
	}
	
	public @Override void handleMouseInput() {
		int wheelDelta = Mouse.getEventDWheel();
		if(wheelDelta != 0) {
			wheelDelta = Math.min(1, wheelDelta);
			wheelDelta = Math.max(-1, wheelDelta);
			if(!isShiftKeyDown()) wheelDelta *= 7;
			
			tc.gnc.scroll(wheelDelta);
			if(tc.enabled()) this.scrollBar.scrollBarMouseWheel();
		} else if(tc.enabled()) this.scrollBar.handleMouse();
		if(mc.currentScreen.getClass() != GuiChat.class) super.handleMouseInput();
	}
	
	public @Override void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.sr = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
		this.buttonList.clear();
		this.inputList.clear();
		this.width = this.sr.getScaledWidth();
		this.height = this.sr.getScaledHeight();
		tc.checkServer();
		if(tc.enabled()) {
			//this.drawChatTabs();
			if(this.scrollBar == null) this.scrollBar = new ChatScrollBar(this);
			//this.scrollBar.drawScrollBar();
		} else if(!Minecraft.getMinecraft().isSingleplayer()) {
			//tc.updateButtonLocations(this.sr);
			this.buttonList.add(tc.channelMap.get("*").tab);
		}
		
		this.sentHistoryCursor = tc.gnc.getSentMessages().size();
		int textFieldWidth = (MacroKeybindCompat.present) ? this.width - 26 : this.width - 4; 
		this.inputField = new GuiTextField(this.fontRenderer, 4, this.height - 12, textFieldWidth, 12);
		this.inputField.setMaxStringLength(500);
		this.inputField.setEnableBackgroundDrawing(false);
		this.inputField.setFocused(true);
		this.inputField.setText(this.defaultInputFieldText);
		this.inputField.setCanLoseFocus(true);
		this.inputList.add(0, this.inputField);
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
		
		if(tc.enabled()) {
			List<String> activeTabs = tc.getActive();
			if(activeTabs.size() != 1) {
				this.inputField.setText("");
			} else {
				String thePrefix = tc.channelMap.get(activeTabs.get(0)).cmdPrefix.trim();
				if(thePrefix.length() > 0) this.inputField.setText(tc.channelMap.get(activeTabs.get(0)).cmdPrefix.trim() + " ");
			}
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
		if (this.fontRenderer.getStringWidth(msg.toString()) + this.fontRenderer.getStringWidth(_chars) < (this.sr.getScaledWidth()-20)*this.inputList.size()) {
			msg.insert(cPos, _chars);
			this.setText(msg, cPos+_chars.length());
		}
	}

	public @Override void keyTyped(char _char, int _code) {
		this.waitingOnPlayerNames = false;
		if(_code == Keyboard.KEY_TAB) this.completePlayerName();
		else this.playerNamesFound = false;
		
		if(_code == Keyboard.KEY_ESCAPE) this.mc.displayGuiScreen((GuiScreen)null);
		else if(_code == Keyboard.KEY_RETURN) {
			StringBuilder _msg = new StringBuilder(1500);
			for(int i=this.inputList.size()-1; i>=0; i--)
				_msg.append(this.inputList.get(i).getText());
			
			if(_msg.toString().length() > 0) {
				TabbyChatUtils.writeLargeChat(_msg.toString());
				for(int j=1; j<this.inputList.size(); j++) {
					this.inputList.get(j).setText("");
					this.inputList.get(j).setVisible(false);
				}
			}
			this.mc.displayGuiScreen((GuiScreen)null);
		} else if(_code == Keyboard.KEY_UP) {
			if(GuiScreen.isCtrlKeyDown()) this.getSentHistory(-1);
			else {
				int foc = this.getFocusedFieldInd();
				if(foc+1<this.inputList.size() && this.inputList.get(foc+1).getVisible()) {
					int gcp = this.inputList.get(foc).getCursorPosition();
					int lng = this.inputList.get(foc+1).getText().length();
					int newPos = Math.min(gcp, lng);
					this.inputList.get(foc).setFocused(false);
					this.inputList.get(foc+1).setFocused(true);
					this.inputList.get(foc+1).setCursorPosition(newPos);
				} else this.getSentHistory(-1);
			}
		} else if(_code == Keyboard.KEY_DOWN) { 
			if(GuiScreen.isCtrlKeyDown()) this.getSentHistory(1);
			else {
				int foc = this.getFocusedFieldInd();
				if(foc-1>=0 && this.inputList.get(foc-1).getVisible()) {
					int gcp = this.inputList.get(foc).getCursorPosition();
					int lng = this.inputList.get(foc-1).getText().length();
					int newPos = Math.min(gcp, lng);
					this.inputList.get(foc).setFocused(false);
					this.inputList.get(foc-1).setFocused(true);
					this.inputList.get(foc-1).setCursorPosition(newPos);
				} else this.getSentHistory(1);
			}
		} else if(_code == Keyboard.KEY_PRIOR) {
			tc.gnc.scroll(19);
			if(tc.enabled()) this.scrollBar.scrollBarMouseWheel();
		} else if(_code == Keyboard.KEY_NEXT) {
			tc.gnc.scroll(-19);
			if(tc.enabled()) this.scrollBar.scrollBarMouseWheel();
		} else if(_code == Keyboard.KEY_BACK) {
			if(this.inputField.isFocused() && this.inputField.getCursorPosition() > 0) this.inputField.textboxKeyTyped(_char, _code);
			else this.removeCharsAtCursor(-1);
		} else if(_code == Keyboard.KEY_DELETE) {
			if(this.inputField.isFocused()) this.inputField.textboxKeyTyped(_char, _code);
			else this.removeCharsAtCursor(1);
		} else if(_code == Keyboard.KEY_LEFT || _code == Keyboard.KEY_RIGHT) {
			this.inputList.get(this.getFocusedFieldInd()).textboxKeyTyped(_char, _code);
		} else if(this.inputField.isFocused() && this.fontRenderer.getStringWidth(this.inputField.getText()) < this.sr.getScaledWidth()-20) {
			this.inputField.textboxKeyTyped(_char, _code);
		} else
			this.insertCharsAtCursor(Character.toString(_char));
	}

	public @Override void mouseClicked(int _x, int _y, int _button) {
		if(_button == 0 && this.mc.gameSettings.chatLinks) {
			ChatClickData ccd = tc.gnc.func_73766_a(Mouse.getX(), Mouse.getY());
			if(ccd != null) {
				URI url = ccd.getURI();
				if(url != null) {
					if(this.mc.gameSettings.chatLinksPrompt) {
						this.clickedURI = url;
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
		if(MacroKeybindCompat.controlClicked(_x, _y, _button, this)) return;
		
		// Replicating GuiScreen's mouseClicked method since 'super' won't work
		for(GuiButton _guibutton : (List<GuiButton>)this.buttonList) {
			if(ChatButton.class.isInstance(_guibutton) || _guibutton.id <= 2) {
				if(_guibutton.mousePressed(this.mc, _x, _y)) {
					if(_button == 0) {
						this.selectedButton = _guibutton;
						this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
						this.actionPerformed(_guibutton);
						return;
					} else if (_button == 1) {
						ChatButton _cb = (ChatButton)_guibutton;
						if(_cb.channel == tc.channelMap.get("*")) return;
						this.mc.displayGuiScreen(new ChatChannelGUI(_cb.channel));
						return;
					}
				}
			} else {
				if(_guibutton.mousePressed(this.mc, _x-EmoticonsCompat.emoteOffsetX, _y) && _button == 0) {
					this.selectedButton = _guibutton;
					this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
					this.actionPerformed(_guibutton);
					return;
				}
			}
		}
	}

	public @Override void mouseMovedOrUp(int _x, int _y, int _button)
    {
        if (this.selectedButton != null && _button == 0)
        {
            this.selectedButton.mouseReleased(_x, _y);
            this.selectedButton = null;
        }
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

	public void setText(StringBuilder txt, int pos) {
		List<String> txtList = this.stringListByWidth(txt, this.sr.getScaledWidth()-20);

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
			this.inputField.setCursorPositionEnd();
		}
		if (this.inputList.size() > txtList.size()) {
			for (int j=txtList.size(); j<this.inputList.size(); j++) {
				this.inputList.get(j).setText("");
				this.inputList.get(j).setFocused(false);
				this.inputList.get(j).setVisible(false);
			}
		}
		if (!this.inputField.getVisible()) {
			this.inputField.setVisible(true);
			this.inputField.setFocused(true);
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
		this.inputField.updateCursorCounter();
	}
}