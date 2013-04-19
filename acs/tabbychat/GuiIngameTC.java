package acs.tabbychat;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.GuiNewChat;

public class GuiIngameTC extends GuiIngame {

	public static Minecraft mc;
	public static GuiNewChatTC persistantChatGUI;

	public GuiIngameTC(Minecraft par1Minecraft) {
		super(par1Minecraft);
		this.mc = par1Minecraft;
		this.persistantChatGUI = GuiNewChatTC.me;
	}

	public @Override GuiNewChat getChatGUI() {
		return this.persistantChatGUI;
	}
}
