package acs.tabbychat.settings;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import acs.tabbychat.util.TabbyChatUtils;


import net.minecraft.src.Minecraft;
import net.minecraft.src.GuiTextField;

public class TCSettingSlider extends TCSetting implements ITCSetting {
	protected float minValue;
	protected float maxValue;
	protected float sliderValue;
	private int sliderX;
	private int buttonOffColor = 0x44ffffff;
	public String units = "%";
	private boolean dragging = false;
	{
		this.type = "slider";
	}
	
	private TCSettingSlider(Object theSetting, String theProperty, String theCategory, int theID) {
		super(theSetting, theProperty, theCategory, theID);
		this.width = 100;
		this.height = 11;
		this.sliderValue = ((Float)this.tempValue - this.minValue) / (this.maxValue - this.minValue);
	}
	
	public TCSettingSlider(Float theSetting, String theProperty, String theCategory, int theID, float minVal, float maxVal) {
		this(theSetting, theProperty, theCategory, theID);
		this.minValue = minVal;
		this.maxValue = maxVal;
		this.sliderValue = ((Float)this.tempValue - this.minValue) / (this.maxValue - this.minValue);
	}
	
	public void clear() {
		super.clear();
		this.sliderValue = ((Float)this.tempValue - this.minValue) / (this.maxValue - this.minValue);
	}
		
	public void drawButton(Minecraft par1, int cursorX, int cursorY) {
		int fgcolor = 0x99a0a0a0;
		if(!this.enabled) {
			fgcolor = -0x995f5f60;
		} else if(this.hovered(cursorX, cursorY)) {
			fgcolor = 0x99ffffa0;
			if(this.dragging) {
				this.sliderX = cursorX - 1;
				this.sliderValue = (float)(this.sliderX - (this.xPosition + 1)) / (this.width - 5);
				if (this.sliderValue < 0.0f)
					this.sliderValue = 0.0f;
				else if (this.sliderValue > 1.0f)
					this.sliderValue = 1.0f;
			}
		}
		int labelColor = (this.enabled) ? 0xffffff : 0x666666;
		int buttonColor = (this.enabled) ? this.buttonColor : this.buttonOffColor; 
		
		this.drawRect(this.xPosition, this.yPosition+1, this.xPosition+1, this.yPosition + this.height-1, fgcolor);
		this.drawRect(this.xPosition+1, this.yPosition, this.xPosition+this.width-1, this.yPosition+1, fgcolor);
		this.drawRect(this.xPosition+1, this.yPosition+this.height-1, this.xPosition+this.width-1, this.yPosition+this.height, fgcolor);
		this.drawRect(this.xPosition+this.width-1, this.yPosition+1, this.xPosition+this.width, this.yPosition+this.height-1, fgcolor);
		this.drawRect(this.xPosition+1, this.yPosition+1, this.xPosition+this.width-1, this.yPosition+this.height-1, 0xff000000);
		
		this.sliderX = Math.round(this.sliderValue * (this.width - 5)) + this.xPosition + 1;
		this.drawRect(this.sliderX, this.yPosition+1, this.sliderX+1, this.yPosition+2, buttonColor & 0x88ffffff);
		this.drawRect(this.sliderX+1, this.yPosition+1, this.sliderX+2, this.yPosition+2, buttonColor);
		this.drawRect(this.sliderX+2, this.yPosition+1, this.sliderX+3, this.yPosition+2, buttonColor & 0x88ffffff);
		this.drawRect(this.sliderX, this.yPosition+2, this.sliderX+1, this.yPosition+this.height-2, buttonColor);
		this.drawRect(this.sliderX+1, this.yPosition+2, this.sliderX+2, this.yPosition+this.height-2, buttonColor & 0x88ffffff);
		this.drawRect(this.sliderX+2, this.yPosition+2, this.sliderX+3, this.yPosition+this.height-2, buttonColor);
		this.drawRect(this.sliderX, this.yPosition+this.height-2, this.sliderX+1, this.yPosition+this.height-1, buttonColor & 0x88ffffff);
		this.drawRect(this.sliderX+1, this.yPosition+this.height-2, this.sliderX+2, this.yPosition+this.height-1, buttonColor);
		this.drawRect(this.sliderX+2, this.yPosition+this.height-2, this.sliderX+3, this.yPosition+this.height-1, buttonColor & 0x88ffffff);
		
		int valCenter = 0;
		if (this.sliderValue < 0.5f)
			valCenter = Math.round(0.7f * this.width);
		else
			valCenter = Math.round(0.2f * this.width);
		
		String valLabel = Integer.toString(Math.round(this.sliderValue * (this.maxValue - this.minValue) + this.minValue)) + this.units;	
		this.drawCenteredString(mc.fontRenderer, valLabel, valCenter+this.xPosition, this.yPosition+2, buttonColor);
		
		this.drawCenteredString(mc.fontRenderer, this.description, this.labelX + mc.fontRenderer.getStringWidth(this.description)/2, this.yPosition + (this.height-6)/2,  labelColor);
	}
	
	public Float getTempValue() {
		this.tempValue = this.sliderValue * (this.maxValue - this.minValue) + this.minValue;
		return (Float)this.tempValue;
	}
	
	public Float getValue() {
		return (Float)this.value;
	}
	
	public void handleMouseInput() {
		if (mc.currentScreen == null)
			return;
		int mX = Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth;
		int mY = mc.currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1;
		if (!this.hovered(mX, mY))
			return;
		
		int var1 = Mouse.getEventDWheel();
		if(var1 != 0) {
			if(var1 > 1) {
				var1 = 3;
			}

			if(var1 < -1) {
				var1 = -3;
			}

			if(Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) {
				var1 *= -7;
			}
		}
		this.sliderValue += (float)var1/100;
		if (this.sliderValue < 0.0f)
			this.sliderValue = 0.0f;
		else if (this.sliderValue > 1.0f)
			this.sliderValue = 1.0f;
		this.tempValue = this.sliderValue * (this.maxValue - this.minValue) + this.minValue;
	}
	
	public void mouseClicked(int par1, int par2, int par3) {
		if (par3 == 0 && this.hovered(par1, par2) && this.enabled) {
			this.sliderX = par1 - 1;
			this.sliderValue = (float)(this.sliderX - (this.xPosition + 1)) / (this.width - 5);
			if (this.sliderValue < 0.0f)
				this.sliderValue = 0.0f;
			else if (this.sliderValue > 1.0f)
				this.sliderValue = 1.0f;
			
			if (!this.dragging)
				this.dragging = true;
		}
	}
	
	public void mouseReleased(int par1, int par2) {
		this.dragging = false;
	}
	
	public void reset() {
		super.reset();
		this.sliderValue = ((Float)this.tempValue - this.minValue) / (this.maxValue - this.minValue);
	}
	
	public void save() {
		this.tempValue = this.sliderValue * (this.maxValue - this.minValue) + this.minValue;
		super.save();
	}
	
	public void setCleanValue(Object updateVal) {
		if(updateVal == null) this.clear();
		else this.value = TabbyChatUtils.median(this.minValue, this.maxValue, Float.valueOf((String)updateVal));
	}
	
	public void setRange(float theMin, float theMax) {
		this.minValue = theMin;
		this.maxValue = theMax;
		this.sliderValue = ((Float)this.tempValue - this.minValue) / (this.maxValue - this.minValue);
	}
	
	public void setTempValue(Object theVal) {
		super.setTempValue(theVal);
		this.sliderValue = ((Float)this.tempValue - this.minValue) / (this.maxValue - this.minValue);
	}
}
