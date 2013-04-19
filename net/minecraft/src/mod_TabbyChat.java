package net.minecraft.src;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiIngame;
import acs.tabbychat.GuiChatTC;
import acs.tabbychat.GuiNewChatTC;
import acs.tabbychat.TabbyChat;

public class mod_TabbyChat extends BaseMod {
	public GuiChatTC gc;
	public static final String version = "1.6.00";
	
	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void load() {
		ModLoader.setInGameHook(this, true, true);
		ModLoader.setInGUIHook(this, true, true);
	}
	
	@Override
	public boolean onTickInGame(float f, Minecraft mc) {
		if(mc.ingameGUI.getChatGUI().getClass() == GuiNewChat.class) {
			try {
				Class IngameGui = mc.ingameGUI.getClass();
				Field persistantGuiField = IngameGui.getDeclaredField("persistantChatGUI");
				persistantGuiField.setAccessible(true);
				persistantGuiField.set(mc.ingameGUI, GuiNewChatTC.me);
			} catch (Throwable e) {
				ModLoader.throwException("The current GUI mods are incompatible with TabbyChat", new Throwable());
			}
		} else if(mc.ingameGUI.getChatGUI().getClass() != GuiNewChatTC.class) {
			ModLoader.throwException("The current GUI mods are incompatible with TabbyChat", new Throwable());
		}
		
		return false;
	}
	
	@Override
	public boolean onTickInGUI(float var1, Minecraft var2, GuiScreen var3)
    {
		if(var3.getClass() == GuiChat.class) {
			var2.displayGuiScreen(new GuiChatTC());
		}
        return true;
    }
}
