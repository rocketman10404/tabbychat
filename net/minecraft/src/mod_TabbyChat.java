package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiIngame;
import acs.tabbychat.core.GuiChatTC;
import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TCChatLine;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;

public class mod_TabbyChat extends BaseMod {
	private static GuiNewChatTC gnc;
	public static final String version = TabbyChatUtils.version;
	
	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void load() {
		ModLoader.setInGUIHook(this, true, true);
	}
	
	@Override
	public void clientChat(String var1) {
		if(gnc == null) {
			gnc = GuiNewChatTC.getInstance();
			gnc.tc.modLoaded = true;
		}
	}
	
	@Override
	public boolean onTickInGUI(float var1, Minecraft var2, GuiScreen var3)
    {
		TabbyChatUtils.chatGuiTick(var2);
        return true;
    }
}
