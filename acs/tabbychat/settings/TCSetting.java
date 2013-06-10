package acs.tabbychat.settings;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;

abstract class TCSetting extends GuiButton implements ITCSetting {
	public int buttonColor = 0xbba5e7e4;
	private int buttonOffColor = 0x99000000;
	protected int labelX;
	public String description;
	protected String type;
	protected Object value;
	protected Object tempValue;
	protected Object theDefault;
	protected static Minecraft mc = Minecraft.getMinecraft();
		
	public TCSetting(int theID, int theX, int theY, String theLabel) {
		super(theID, theX, theY, theLabel);
	}
	
	public TCSetting(String theLabel, int theID) {
		super(theID, 0, 0, "");
	}
		
	public void actionPerformed() { }
	
	public void clear() {
		this.value = this.theDefault;
		this.tempValue = this.theDefault;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public void drawButton(Minecraft mc, int cursorX, int cursorY) { }
	
	public void enable() {
		this.enabled = true;
	}
	
	public boolean enabled() {
		return this.enabled;
	}
		
	public Object getTempValue() {
		return this.tempValue;
	}
	
	public String getType() {
		return this.type;
	}
		
	protected Object getValue() {
		return this.value;
	}
		
	public Boolean hovered(int cursorX, int cursorY) {
		return cursorX >= this.xPosition && cursorY >= this.yPosition && cursorX < this.xPosition + this.width && cursorY < this.yPosition + this.height;
	}
		
	public void mouseClicked(int par1, int par2, int par3) { }
		
	public void reset() {
		this.tempValue = this.value;
	}
	
	public void save() {
		this.value = this.tempValue;
	}
		
	public void setButtonDims(int wide, int tall) {
		this.width = wide;
		this.height = tall;
	}
		
	public void setButtonLoc(int bx, int by) {
		this.xPosition = bx;
		this.yPosition = by;
	}
	
	public void setLabelLoc(int _x) {
		this.labelX = _x;
	}
			
	public void setTempValue(Object updateVal) {
		this.tempValue = updateVal;
	}
	
	public void setCleanValue(Object updateVal) {
		if(updateVal == null) this.clear();
		else this.value = updateVal;
	}
	
	public void setValue(Object updateVal) {
		this.value = updateVal;
	}

}
