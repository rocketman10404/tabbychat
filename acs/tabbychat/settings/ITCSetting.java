package acs.tabbychat.settings;

import java.util.Properties;

import net.minecraft.src.Minecraft;

public interface ITCSetting {

	public void actionPerformed();
	
	public void clear();
	
	public void disable();
	
	public void drawButton(Minecraft mc, int cursorX, int cursorY);
	
	public void enable();
	
	public boolean enabled();
	
	public Object getDefault();
	
	public String getProperty();
	
	public Object getTempValue();
	
	public String getType();
	
	public Boolean hovered(int cursorX, int cursorY);
	
	public void loadSelfFromProps(Properties readProps);
	
	public void mouseClicked(int par1, int par2, int par3);
	
	public void reset();
	
	public void resetDescription();
	
	public void save();
	
	public void saveSelfToProps(Properties writeProps);
	
	public void setButtonDims(int wide, int tall);
	
	public void setButtonLoc(int bx, int by);
	
	public void setLabelLoc(int lx);
	
	public void setTempValue(Object updateVal);
	
	public void setCleanValue(Object uncleanVal);
	
	public void setValue(Object updateVal);
}
