package acs.tabbychat;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiIngame;
import net.minecraft.src.BaseMod;
import net.minecraft.src.ModLoader;
import acs.tabbychat.GuiChatTC;
import acs.tabbychat.GuiNewChatTC;
import acs.tabbychat.TabbyChat;

public class mod_TabbyChat extends BaseMod {

	@Override
	public String getVersion() {
		return TabbyChat.instance.version;
	}

	@Override
	public void load() {
		ModLoader.setInGameHook(this, true, true);
	}
	
	@Override
	public boolean onTickInGame(float f, Minecraft mc) {
		if(mc.ingameGUI.getClass() == GuiIngame.class) {
			mc.ingameGUI = new GuiIngameTC(mc);			
		} else if(mc.ingameGUI.getClass() != GuiIngameTC.class) {
			ModLoader.throwException(new String("The current GUI mods are incompatible with TabbyChat"), new Throwable());
		}
		return false;
	}
}
