package acs.tabbychat.settings;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiTextField;

public class TCSettingTextBox extends TCSetting implements ITCSetting {
	protected GuiTextField textBox;
	protected int charLimit = 32;
	{
		this.type = "textbox";
	}
	
	public TCSettingTextBox(String theLabel, int theID) {
		this("", theLabel, theID);
	}
	
	public TCSettingTextBox(String theSetting, String theLabel, int theID) {
		super(theID, 0, 0, "");
		this.value = theSetting;
		this.description = theLabel;
		this.labelX = 0;
		this.width = 50;
		this.height = 11;
		this.textBox = new GuiTextField(mc.fontRenderer, 0, 0, this.width, this.height);
		this.textBox.setText((String)this.value);
		this.theDefault = theSetting;
	}
	
	public void clear() {
		super.clear();
		this.textBox.setText((String)this.theDefault);
	}
	
	public void disable() {
		super.disable();
		this.textBox.setEnabled(false);
	}
	
	public void drawButton(Minecraft par1, int cursorX, int cursorY) {
		int labelColor = (this.enabled) ? 0xffffff : 0x666666;
		
		this.textBox.drawTextBox();
		this.drawCenteredString(mc.fontRenderer, this.description, this.labelX + mc.fontRenderer.getStringWidth(this.description)/2, this.yPosition + (this.height - 6) / 2,  labelColor);
	}
	
	public void enable() {
		super.enable();
		this.textBox.setEnabled(true);
	}
	
	public void setEnabled(boolean val) {
		this.enabled = val;
		this.textBox.setEnabled(val);
	}
	
	public String getTempValue() {
		return this.textBox.getText().trim();
	}
	
	public String getValue() {
		return (String)this.value;
	}
	
	public void keyTyped(char par1, int par2) {
		this.textBox.textboxKeyTyped(par1, par2);
	}
	
	public void mouseClicked(int par1, int par2, int par3) {
		this.textBox.mouseClicked(par1, par2, par3);
	}
	
	private void reassignField() {
		String tmp = this.textBox.getText();
		this.textBox = new GuiTextField(mc.fontRenderer, this.xPosition, this.yPosition+1, this.width, this.height+1);
		this.textBox.setMaxStringLength(this.charLimit);
		this.textBox.setText(tmp);
	}
	
	public void reset() {
		if(this.value == null) this.value = "";
		this.textBox.setText((String)this.value);
	}
	
	public void save() {
		this.value = this.textBox.getText().trim();
	}
	
	public void setButtonDims(int wide, int tall) {
		super.setButtonDims(wide, tall);
		this.reassignField();
	}
	
	public void setButtonLoc(int bx, int by) {
		super.setButtonLoc(bx, by);
		this.reassignField();
	}
	
	public void setCharLimit(int newLimit) {
		this.charLimit = newLimit;
		this.textBox.setMaxStringLength(newLimit);
	}
	
	public void setTempValue(String theVal) {
		this.textBox.setText(theVal);
	}	
}
