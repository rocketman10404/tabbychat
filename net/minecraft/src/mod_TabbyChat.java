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
	private static TabbyChat tc;
	public static final String version = TabbyChatUtils.version;
	
	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void load() {
		tc = TabbyChat.instance.postInit();
		ModLoader.setInGameHook(this, true, true);
		ModLoader.setInGUIHook(this, true, true);
	}
	
	@Override
	public boolean onTickInGame(float f, Minecraft mc) {
		if(mc.ingameGUI.getChatGUI().getClass() == GuiNewChat.class) {
			try {
				// Grab any current GuiNewChat.chatLines
				Class newChatGui = GuiNewChat.class;
				Field chatLineField = newChatGui.getDeclaredFields()[3]; // field_96134_d
				chatLineField.setAccessible(true);
				List<ChatLine> missedChats = (ArrayList<ChatLine>)chatLineField.get(mc.ingameGUI.getChatGUI());
				
				// Replace pointer to GuiNewChat
				TabbyChatUtils.hookIntoChat(GuiNewChatTC.me);
				
				// Convert missed ChatLines to TCChatLines
				if(missedChats.size() > 0) {
					List<TCChatLine> addChats = new ArrayList<TCChatLine>(missedChats.size());
					for(ChatLine cl : missedChats) {
						addChats.add(new TCChatLine(cl.getUpdatedCounter(), cl.getChatLineString(), cl.getChatLineID()));
					}
				
					//Add any missed chatLines to replacement class
					GuiNewChatTC.me.addChatLines(0, addChats);
				}
			} catch (Throwable e) {
				e.printStackTrace();
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
		if(var3 != null && var3.getClass() == GuiChat.class) {
				String defText = ((GuiChat)var3).inputField.getText();
				var2.displayGuiScreen(new GuiChatTC(defText));
		}
        return true;
    }
}
