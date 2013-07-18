package acs.tabbychat.gui;

import java.io.File;
import java.util.Properties;

import acs.tabbychat.util.TabbyChatUtils;

import net.minecraft.src.Minecraft;
import net.minecraft.src.GuiButton;

public interface ITCSettingsGUI {
	int SAVEBUTTON = 8901;
	int CANCELBUTTON = 8902;
	int MARGIN = 4;
	int LINE_HEIGHT = 14;
	int DISPLAY_WIDTH = 300;
	int DISPLAY_HEIGHT = 180;
	File tabbyChatDir = TabbyChatUtils.getTabbyChatDir();
	
	public void actionPerformed(GuiButton button);
	
	public void defineDrawableSettings();
	
	public void drawScreen(int x, int y, float f);
	
	public void handleMouseInput();
	
	public void initDrawableSettings();
	
	public void initGui();
	
	public void keyTyped(char par1, int par2);
	
	public Properties loadSettingsFile();
	
	public void mouseClicked(int par1, int par2, int par3);
	
	abstract void resetTempVars();
	
	abstract int rowY(int rowNum);
	
	abstract void saveSettingsFile();
	
	abstract void saveSettingsFile(Properties preProps);
	
	abstract void storeTempVars();
	
	public void validateButtonStates();	
}
