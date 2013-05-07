package acs.tabbychat;

import java.lang.reflect.Array;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;

public class TCSettingEnum extends TCSetting {
	protected volatile Enum value;
	protected Enum tempValue;
	
	public TCSettingEnum(String theLabel, int theID) {
		super(theLabel, theID);
		this.value = null;
		this.tempValue = null;
	}
	
	public TCSettingEnum(Enum theVar, String theLabel, int theID) {
		super(theLabel, theID);
		mc = Minecraft.getMinecraft();
		this.value = theVar;
		this.tempValue = theVar;
		this.description = theLabel;
		this.type = "enum";
		this.labelX = 0;
		this.width = 30;
		this.height = 11;
	}
	
	public void next() {
		Enum[] E = this.tempValue.getClass().getEnumConstants();
		Enum tmp;
		if (this.tempValue.ordinal() == E.length - 1)
			tmp = Enum.valueOf(this.tempValue.getClass(), E[0].name());
		else {
			tmp = Enum.valueOf(this.tempValue.getClass(), E[this.tempValue.ordinal()+1].name());
		}
		this.tempValue = tmp;
	}
	
	public void previous() {
		Enum E[] = this.tempValue.getClass().getEnumConstants();
		if (this.tempValue.ordinal() == 0)
			this.tempValue = Enum.valueOf(this.tempValue.getClass(), E[E.length-1].name());
		else {
			this.tempValue = Enum.valueOf(this.tempValue.getClass(), E[this.tempValue.ordinal()-1].name());
		}
	}

	public void save() {
		this.value = Enum.valueOf(this.tempValue.getClass(), this.tempValue.name());
	}
	
	public void reset() {
		this.tempValue = Enum.valueOf(this.value.getClass(), this.value.name());
	}
	
	public void setValue(Enum theVal) {
		this.value = Enum.valueOf(theVal.getClass(), theVal.name());
	}
	
	public void setTempValue(Enum theVal) {
		this.tempValue = Enum.valueOf(theVal.getClass(), theVal.name());
	}
	
	public Enum getValue() {
		return this.value;
	}
	
	public Enum getTempValue() {
		return this.tempValue;
	}
	
	public void mouseClicked(int par1, int par2, int par3) {
		if (this.hovered(par1, par2) && this.enabled) {
			if (par3 == 1)
				this.previous();
			else if (par3 == 0)
				this.next();
		}
	}
	
	public void actionPerformed() {
	}
	
	public void drawButton(Minecraft par1, int cursorX, int cursorY) {
		int centerX = this.xPosition + this.width / 2;
		int centerY = this.yPosition + this.height / 2;
		int fgcolor = 0x99a0a0a0;
		if(!this.enabled) {
			fgcolor = -0x995f5f60;
		} else if(this.hovered(cursorX, cursorY)) {
			fgcolor = 0x99ffffa0;
		}
		
		int labelColor = (this.enabled) ? 0xffffff : 0x666666;
		
		drawRect(this.xPosition+1, this.yPosition, this.xPosition+this.width-1, this.yPosition+1, fgcolor);
		drawRect(this.xPosition+1, this.yPosition+this.height-1, this.xPosition+this.width-1, this.yPosition+this.height, fgcolor);
		drawRect(this.xPosition, this.yPosition+1, this.xPosition+1, this.yPosition+this.height-1, fgcolor);
		drawRect(this.xPosition+this.width-1, this.yPosition+1, this.xPosition+this.width, this.yPosition+this.height-1, fgcolor);
		drawRect(this.xPosition+1, this.yPosition+1, this.xPosition+this.width-1, this.yPosition+this.height-1, 0xff000000);

		this.drawCenteredString(mc.fontRenderer, this.tempValue.toString(), centerX, this.yPosition+2, labelColor);
		this.drawCenteredString(mc.fontRenderer, this.description, this.labelX + mc.fontRenderer.getStringWidth(this.description)/2, this.yPosition + (this.height - 6) / 2,  labelColor);
	}

}
