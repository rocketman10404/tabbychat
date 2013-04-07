package acs.tabbychat;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;

public class TCSetting extends GuiButton {

	protected int labelX;
	protected String description;
	protected String type;
	protected Object value;
	protected Object tempValue;
	protected static Minecraft mc;
		
	public TCSetting(String theLabel, int theID) {
		super(theID, 0, 0, "");
	}
	
	public TCSetting(int theID, int theX, int theY, String theLabel) {
		super(theID, theX, theY, theLabel);
	}
		
	protected void setValue(Object updateVal) {
		this.value = updateVal;
	}
	
	protected void setTempValue(Object updateVal) {
		this.tempValue = updateVal;
	}
	
	protected Object getValue() {
		return this.value;
	}
	
	protected Object getTempValue() {
		return this.tempValue;
	}
		
	public void enable() {
		this.enabled = true;
	}
		
	public void disable() {
		this.enabled = false;
	}
		
	public void setButtonLoc(int bx, int by) {
		this.xPosition = bx;
		this.yPosition = by;
	}
		
	public void setButtonDims(int wide, int tall) {
		this.width = wide;
		this.height = tall;
	}
		
	public void save() {
	}
		
	public void reset() {
	}
		
	protected Boolean hovered(int cursorX, int cursorY) {
		return cursorX >= this.xPosition && cursorY >= this.yPosition && cursorX < this.xPosition + this.width && cursorY < this.yPosition + this.height;
	}
		
	public void mouseClicked(int par1, int par2, int par3) { }
		
	public void actionPerformed() { }
		
	public void drawButton(Minecraft mc, int cursorX, int cursorY) { }

}
