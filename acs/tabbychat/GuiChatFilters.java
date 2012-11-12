package acs.tabbychat;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ScaledResolution;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiTextField;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiChatFilters extends net.minecraft.src.GuiScreen {
	private static Minecraft mc;
	private static TabbyChat tc;
	private final int margin = 10;
	private final int line_height = 16;
	public final int displayWidth = 325;
	public final int displayHeight = 170;
	public final int fColor = 0x6622ee22;
	
	private int fInd = 0;
	protected ArrayList<CustomChatFilter> tmp_customFilters = new ArrayList<CustomChatFilter>();
	
	private static final int saveButton = 9520;
	private static final int cancelButton = 9521;
	private static final int settingsWindowButton = 9522;
	private static final int prevFilterButton = 9523;
	private static final int nextFilterButton = 9524;
	private static final int newFilterButton = 9525;
	private static final int invertFilterButton = 9526;
	private static final int caseSenseFilterButton = 9527;
	private static final int sendToTabButton = 9528;
	private static final int highlightButton = 9529;
	private static final int dingButton = 9530;
	private static final int remFilterButton = 9531;
	private static final int highlightStyleButton = 9532;
	
	private PrefsButton prevFilterPrefs;
	private PrefsButton nextFilterPrefs;
	private GuiTextField filterNamePrefs;
	private PrefsButton newFilterPrefs;
	private PrefsButton remFilterPrefs;
	private PrefsToggleButton invertFilterPrefs;
	private PrefsToggleButton caseSenseFilterPrefs;
	private PrefsToggleButton sendToTabPrefs;
	private PrefsToggleButton highlightPrefs;
	private PrefsToggleButton dingPrefs;
	private GuiTextField filterExpPrefs;
	private PrefsButton highlightStylePrefs;
	
	public GuiChatFilters() {
		mc = Minecraft.getMinecraft();
	}
	
	protected GuiChatFilters(TabbyChat _tc) {
		this();
		tc = _tc;
	}
	
	protected void mouseClicked(int par1, int par2, int par3) {
		if (Mouse.getEventButton() == 1) {
			if (par1 >= this.highlightStylePrefs.xPosition && par1 < this.highlightStylePrefs.xPosition + this.highlightStylePrefs.width() && par2 >= this.highlightStylePrefs.yPosition && par2 < this.highlightStylePrefs.yPosition + this.highlightStylePrefs.height()) {
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
					this.tmp_customFilters.get(fInd).highlightFormat = this.tmp_customFilters.get(fInd).highlightFormat.prevFormat();
				else
					this.tmp_customFilters.get(fInd).highlightColor = this.tmp_customFilters.get(fInd).highlightColor.prevColor();
				this.highlightStylePrefs.displayString = this.tmp_customFilters.get(fInd).getHighlightDisplay();
			}
		}
		this.filterNamePrefs.mouseClicked(par1, par2, par3);
		this.filterExpPrefs.mouseClicked(par1, par2, par3);
		super.mouseClicked(par1, par2, par3);
	}
	
	protected void keyTyped(char par1, int par2) {
		this.filterNamePrefs.textboxKeyTyped(par1, par2);
		this.filterExpPrefs.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}
	
	public void saveCurrentFilter() {
		if (this.controlList.size() < 1)
			return;
		try {
			this.tmp_customFilters.get(fInd).name = this.filterNamePrefs.getText();
			this.tmp_customFilters.get(fInd).updateExpression(this.filterExpPrefs.getText());
		} catch (IndexOutOfBoundsException e1) {
			TabbyChat.printErr("ERROR: Filter GUI indices are off track.");
		} catch (PatternSyntaxException e) {
			TabbyChat.printErr("Unable to create chat filter: invalid filter expression.");
			this.tmp_customFilters.get(fInd).updateExpression(mc.thePlayer.username);
		}
	}
	
	public void reloadCurrentFilter() {
		this.filterNamePrefs.setText(this.tmp_customFilters.get(fInd).name);
		this.invertFilterPrefs.updateTo(this.tmp_customFilters.get(fInd).invert);
		this.caseSenseFilterPrefs.updateTo(this.tmp_customFilters.get(fInd).caseSensitive);
		this.sendToTabPrefs.updateTo(this.tmp_customFilters.get(fInd).sendToTab);
		this.highlightPrefs.updateTo(this.tmp_customFilters.get(fInd).highlight);
		this.dingPrefs.updateTo(this.tmp_customFilters.get(fInd).ding);
		this.filterExpPrefs.setText(this.tmp_customFilters.get(fInd).filter.toString());
		this.highlightStylePrefs.displayString = this.tmp_customFilters.get(fInd).getHighlightDisplay();
	}
	
	public void applySettings() {
		this.saveCurrentFilter();
		tc.serverPrefs.customFilters = CustomChatFilter.copyList(this.tmp_customFilters);
		tc.updateFilters();
	}
	
	public void actionPerformed(GuiButton button) {
		PrefsButton myButton = (PrefsButton)button;
		switch (myButton.id) {
		case cancelButton:
			this.tmp_customFilters = CustomChatFilter.copyList(tc.serverPrefs.customFilters);
			mc.displayGuiScreen((GuiScreen)null);
			if (tc.globalPrefs.TCenabled)
				tc.resetDisplayedChat();
			break;
		case saveButton:
			this.applySettings();
			tc.prefsWindow.applySettings();
			tc.globalPrefs.saveSettings();
			tc.serverPrefs.saveSettings();
			mc.displayGuiScreen((GuiScreen)null);
			if (tc.globalPrefs.TCenabled)
				tc.resetDisplayedChat();
			break;
		case settingsWindowButton:
			this.saveCurrentFilter();
			mc.displayGuiScreen((GuiScreen)tc.prefsWindow);
			break;
		case prevFilterButton:
			this.saveCurrentFilter();
			if (this.fInd == 0)
				this.fInd = this.tmp_customFilters.size()-1;
			else
				this.fInd = this.fInd - 1;
			this.reloadCurrentFilter();
			break;
		case nextFilterButton:
			this.saveCurrentFilter();
			if (this.fInd == this.tmp_customFilters.size() - 1)
				this.fInd = 0;
			else
				this.fInd++;
			this.reloadCurrentFilter();
			break;
		case newFilterButton:
			this.saveCurrentFilter();
			this.tmp_customFilters.add(new CustomChatFilter());
			this.fInd = this.tmp_customFilters.size()-1;
			this.reloadCurrentFilter();
			break;
		case remFilterButton:
			this.tmp_customFilters.remove(this.fInd);
			if (this.tmp_customFilters.size() < 1) {
				this.tmp_customFilters.add(new CustomChatFilter());
			}
			this.fInd = (this.fInd > 0 && this.fInd <= this.tmp_customFilters.size()) ? this.fInd - 1 : 0;
			this.reloadCurrentFilter();
			break;
		case invertFilterButton:
			this.tmp_customFilters.get(this.fInd).invert = !this.tmp_customFilters.get(this.fInd).invert;
			this.invertFilterPrefs.toggle();
			if (this.tmp_customFilters.get(this.fInd).invert) {
				this.tmp_customFilters.get(this.fInd).highlight = false;
				this.highlightPrefs.updateTo(false);
				this.highlightPrefs.enabled = false;
			} else {
				this.highlightPrefs.enabled = true;
			}
			break;
		case caseSenseFilterButton:
			this.tmp_customFilters.get(this.fInd).caseSensitive = !this.tmp_customFilters.get(this.fInd).caseSensitive;
			this.caseSenseFilterPrefs.toggle();
			break;
		case sendToTabButton:
			this.tmp_customFilters.get(this.fInd).sendToTab = !this.tmp_customFilters.get(this.fInd).sendToTab;
			if (!this.tmp_customFilters.get(this.fInd).sendToTab)
				this.tmp_customFilters.get(this.fInd).chanID = 0;
			this.sendToTabPrefs.toggle();
			break;
		case highlightButton:
			this.tmp_customFilters.get(this.fInd).highlight = !this.tmp_customFilters.get(this.fInd).highlight;
			this.highlightPrefs.toggle();
			if (this.tmp_customFilters.get(this.fInd).highlight) {
				this.tmp_customFilters.get(this.fInd).invert = false;
				this.invertFilterPrefs.updateTo(false);
				this.invertFilterPrefs.enabled = false;
			} else {
				this.invertFilterPrefs.enabled = true;
			}
			break;
		case highlightStyleButton:
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				this.tmp_customFilters.get(this.fInd).highlightFormat = this.tmp_customFilters.get(this.fInd).highlightFormat.nextFormat();
			} else {
				this.tmp_customFilters.get(this.fInd).highlightColor = this.tmp_customFilters.get(this.fInd).highlightColor.nextColor();
			}
			this.highlightStylePrefs.displayString = this.tmp_customFilters.get(this.fInd).getHighlightDisplay();
			break;
		case dingButton:
			this.tmp_customFilters.get(this.fInd).ding = !this.tmp_customFilters.get(this.fInd).ding;
			tc.ding();
			this.dingPrefs.toggle();
			break;
		}
	}
	
	public void prepareTempFilters() {
		this.tmp_customFilters.clear();
		this.tmp_customFilters = CustomChatFilter.copyList(tc.serverPrefs.customFilters);
	}
	
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.controlList.clear();
		this.fInd = 0;
		
		if (this.tmp_customFilters.size() <= 0)
			this.tmp_customFilters.add(new CustomChatFilter());
		
		
		int effWidth = this.displayWidth;
		int effHeight = this.displayHeight;
		int iMargin = (this.line_height - mc.fontRenderer.FONT_HEIGHT)/2;
		
		int col1x = this.width/2 - this.margin;
		int col2x = (this.width+effWidth)/2 - this.margin;
		int row1y = (this.height-effHeight)/2;
		int row2y = row1y + this.line_height + this.margin;
		int row3y = row2y + this.line_height + this.margin;
		int row4y = row3y + this.line_height + this.margin;
		int row5y = row4y + this.line_height + this.margin;
		int row6y = row5y + this.line_height + this.margin;
		int row7y = row6y + this.line_height + this.margin;	
		
		int bW = 40;
		int bH = this.line_height;
		PrefsButton savePrefs = new PrefsButton(saveButton, (this.width + effWidth)/2 - bW, (this.height + effHeight)/2 - bH, bW, bH, "Save");
		this.controlList.add(savePrefs);
		PrefsButton cancelPrefs = new PrefsButton(cancelButton, (this.width + effWidth)/2 - 2*bW - 2, (this.height + effHeight)/2 - bH, bW, bH, "Cancel");
		this.controlList.add(cancelPrefs);
		PrefsButton settingsWindowPrefs = new PrefsButton(settingsWindowButton, (this.width + effWidth)/2 - 2*bW-54, (this.height + effHeight)/2 - bH, 50, bH, "Settings...", 0x66a5e7e4);
		this.controlList.add(settingsWindowPrefs);
		
		bW = 20;
		this.prevFilterPrefs = new PrefsButton(prevFilterButton, (this.width/2-20), row2y, bW, bH, "<", this.fColor);
		this.filterNamePrefs = new GuiTextField(mc.fontRenderer, (this.width/2-20)+22, row2y, 60, bH);
		this.nextFilterPrefs = new PrefsButton(nextFilterButton, (this.width/2-20) + 24 + 60, row2y, bW, bH, ">", this.fColor);
		this.filterNamePrefs.setText(this.tmp_customFilters.get(this.fInd).name);
		this.controlList.add(this.prevFilterPrefs);
		this.controlList.add(this.nextFilterPrefs);
		
		bW = 30;
		this.newFilterPrefs = new PrefsButton(newFilterButton, (this.width+effWidth)/2-42-bW, row2y, bW, bH, "Add", this.fColor);
		this.controlList.add(this.newFilterPrefs);
		bW = 40;
		this.remFilterPrefs = new PrefsButton(remFilterButton, (this.width+effWidth)/2-bW, row2y, bW, bH, "Remove", this.fColor);
		this.controlList.add(this.remFilterPrefs);
		
		bW = 25;
		this.invertFilterPrefs = new PrefsToggleButton(invertFilterButton, col1x - bW, row3y, bW, bH);
		this.invertFilterPrefs.onColor = this.fColor;
		this.invertFilterPrefs.updateTo(this.tmp_customFilters.get(this.fInd).invert);
		this.controlList.add(this.invertFilterPrefs);
		
		this.caseSenseFilterPrefs = new PrefsToggleButton(caseSenseFilterButton, col2x - bW, row3y, bW, bH);
		this.caseSenseFilterPrefs.onColor = this.fColor;
		this.caseSenseFilterPrefs.updateTo(this.tmp_customFilters.get(this.fInd).caseSensitive);
		this.controlList.add(this.caseSenseFilterPrefs);
		
		this.sendToTabPrefs = new PrefsToggleButton(sendToTabButton, col1x-bW, row4y, bW, bH);
		this.sendToTabPrefs.onColor = this.fColor;
		this.sendToTabPrefs.updateTo(this.tmp_customFilters.get(this.fInd).sendToTab);
		this.controlList.add(this.sendToTabPrefs);
		
		this.highlightPrefs = new PrefsToggleButton(highlightButton, col2x-bW, row4y, bW, bH);
		this.highlightPrefs.onColor = this.fColor;
		if (this.tmp_customFilters.get(this.fInd).invert) {
			this.highlightPrefs.updateTo(false);
			this.highlightPrefs.enabled = false;
		} else {
			this.highlightPrefs.updateTo(this.tmp_customFilters.get(this.fInd).highlight);
			this.highlightPrefs.enabled = true;
		}
		this.controlList.add(this.highlightPrefs);
		
		bW = 70;
		this.highlightStylePrefs = new PrefsButton(highlightStyleButton, col2x-bW, row5y, bW, bH, this.tmp_customFilters.get(this.fInd).getHighlightDisplay(), 0x66ffffff);
		this.highlightStylePrefs.hasControlCodes = true;
		this.controlList.add(this.highlightStylePrefs);
		
		bW = 25;
		this.dingPrefs = new PrefsToggleButton(dingButton, col1x-bW, row5y, bW, bH);
		this.dingPrefs.onColor = this.fColor;
		this.dingPrefs.updateTo(this.tmp_customFilters.get(this.fInd).ding);
		this.controlList.add(this.dingPrefs);
		
		bW = 220;
		this.filterExpPrefs = new GuiTextField(mc.fontRenderer, (this.width-effWidth)/2 + 95, row6y, bW, bH);
		this.filterExpPrefs.setMaxStringLength(200);
		this.filterExpPrefs.setText(this.tmp_customFilters.get(this.fInd).filter.toString());
	}
	
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	public void drawScreen(int x, int y, float f) {
		int effWidth = this.displayWidth;
		int effHeight = this.displayHeight;
		int iMargin = (this.line_height - mc.fontRenderer.FONT_HEIGHT)/2;
		
		int col1x = (this.width-effWidth)/2;
		int col2x = this.width/2 + this.margin;
		int row1y = (this.height-effHeight)/2 + 4;
		int row2y = row1y + this.line_height + this.margin;
		int row3y = row2y + this.line_height + this.margin;
		int row4y = row3y + this.line_height + this.margin;
		int row5y = row4y + this.line_height + this.margin;
		int row6y = row5y + this.line_height + this.margin;
		int row7y = row6y + this.line_height + this.margin;
		
		drawRect(this.width/2 - effWidth/2 - this.margin, this.height/2 - effHeight/2 - this.margin, this.width/2 + effWidth/2 + this.margin, this.height/2 + effHeight/2 + this.margin, 0x88000000);
		drawRect(this.width/2 - effWidth/2 - this.margin, row1y-4, this.width/2 + effWidth/2 + this.margin, row1y-4+this.line_height, this.fColor);
		this.drawString(mc.fontRenderer, "FILTER CONFIGURATION", col1x, row1y, 0xffffff);
		String myStat = Integer.toString(this.fInd+1) + " of " + Integer.toString(this.tmp_customFilters.size());
		int myLength = mc.fontRenderer.getStringWidth(myStat);
		this.drawString(mc.fontRenderer, myStat, (this.width+effWidth)/2-myLength, row1y, 0xffffff);
		
		this.drawString(mc.fontRenderer, "Currently-defined filters:", col1x, row2y, 0xffffff);
		this.filterNamePrefs.drawTextBox();
		
		this.drawString(mc.fontRenderer, "Inverse match", col1x, row3y, 0xffffff);
		this.drawString(mc.fontRenderer, "Case-sensitive", col2x, row3y, 0xffffff);
		
		this.drawString(mc.fontRenderer, "Filter to new tab", col1x, row4y, 0xffffff);
		
		this.drawString(mc.fontRenderer, "Highlight matching", col2x, row4y-iMargin-1, 0xffffff);
		this.drawString(mc.fontRenderer, "text as", col2x, row4y+iMargin+1, 0xffffff);
		
		this.drawString(mc.fontRenderer, "Audio notification", col1x, row5y, 0xffffff);
		
		this.drawString(mc.fontRenderer, "Filter Expression:", col1x, row6y, 0xffffff);
		this.filterExpPrefs.drawTextBox();
		
		super.drawScreen(x, y, f);
	}
}
