package acs.tabbychat;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;

public class TCSettingBool extends net.minecraft.src.Gui {
	protected int buttonOnColor = 0x66a5e7e4;
	protected int buttonOffColor = 0x99000000;
	protected int buttonWidth;
	protected int buttonHeight;
	protected int buttonX;
	protected int buttonY;
	protected int labelX;
	protected int id;
	protected String buttonOnTitle;
	protected String buttonOffTitle;
	protected String description;
	protected Boolean value;
	protected Boolean tempValue;
	private Boolean enabled;
	private static Minecraft mc;
	
	
	public TCSettingBool(Boolean theSetting, String theLabel, int theID) {
		this(theSetting, theLabel, "On", "Off", theID);
	}
	
	public TCSettingBool(Boolean theSetting, String theLabel, String onTitle, String offTitle, int theID) {
		mc = Minecraft.getMinecraft();
		this.value = theSetting;
		this.tempValue = this.value;
		this.description = theLabel;
		this.buttonOnTitle = onTitle;
		this.buttonOffTitle = offTitle;
		this.buttonX = 0;
		this.buttonY = 0;
		this.labelX = 0;
		this.buttonWidth = Math.max(mc.fontRenderer.getStringWidth(this.buttonOnTitle), mc.fontRenderer.getStringWidth(this.buttonOffTitle)) + 4;
		this.buttonHeight = mc.fontRenderer.FONT_HEIGHT + 4;
		this.id = theID;
	}
	
	protected void setValue(Boolean updateVal) {
		this.value = updateVal;
		this.tempValue = this.value;
	}
	
	public void enable() {
		this.enabled = true;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public void setButtonLoc(int bx, int by) {
		this.buttonX = bx;
		this.buttonY = by;
	}
	
	public void save() {
		this.value = this.tempValue;
	}
	
	public void reset() {
		this.tempValue = this.value;
	}
	
	public void toggle() {
		this.tempValue = !this.tempValue;
	}
	
	private Boolean hovered(int cursorX, int cursorY) {
		return cursorX >= this.buttonX && cursorY >= this.buttonY && cursorX < this.buttonX + this.buttonWidth && cursorY < this.buttonY + this.buttonHeight;
	}
	
	public void mouseClicked(int par1, int par2, int par3) {
		if (this.hovered(par1, par2))
			this.toggle();
	}
	
	public void actionPerformed() { }
	
	public void drawButton(int cursorX, int cursorY) {
		int bgcolor = (this.tempValue) ? this.buttonOnColor : this.buttonOffColor;
		String dispString = (this.tempValue) ? this.buttonOnTitle : this.buttonOffTitle;
		
		drawRect(this.buttonX, this.buttonY, this.buttonX + this.buttonWidth, this.buttonY + this.buttonHeight, bgcolor);

		int fgcolor = 0xa0a0a0;
		if(!this.enabled) {
			fgcolor = -0x5f5f60;
		} else if(this.hovered(cursorX, cursorY)) {
			fgcolor = 0xffffa0;
		}

		this.drawCenteredString(mc.fontRenderer, this.description, this.labelX + mc.fontRenderer.getStringWidth(this.description)/2, this.buttonY + (this.buttonHeight - 8) / 2,  0xffffff);
		this.drawCenteredString(mc.fontRenderer, dispString, this.buttonX + this.buttonWidth / 2, this.buttonY + (this.buttonHeight - 8) / 2, fgcolor);
	
	}
}
