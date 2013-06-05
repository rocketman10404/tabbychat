package acs.tabbychat.liteloader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiTextField;
import acs.tabbychat.core.GuiChatTC;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.liteloader.TabbyChatMod;
import acs.tabbychat.util.TabbyChatUtils;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.core.LiteLoader;

public class TabbyChatInitListener implements InitCompleteListener {

	@Override
	public String getName() {
		return "TabbyChat";
	}

	@Override
	public String getVersion() {
		return TabbyChatUtils.version;
	}

	@Override
	public void init() {}

	@Override
	public void onInitCompleted(Minecraft var1, LiteLoader var2) {
		TabbyChat.instance.postInit();
		TabbyChatUtils.hookIntoChat(TabbyChat.gnc);
	}

	@Override
	public void onTick(Minecraft var1, float var2, boolean var3, boolean var4) {
		if(var1.currentScreen == null) return;
		else if(var1.currentScreen.getClass() != GuiChat.class) return;
		
		String defText = "";
		try {
			for(Field fields : GuiChat.class.getDeclaredFields()) {
				if(fields.getType() == GuiTextField.class) {
					fields.setAccessible(true);
					Method getInputText = GuiTextField.class.getMethod("getText", (Class[]) null);
					defText = (String)getInputText.invoke(fields, (Object[])null);
				}
			}
		} catch (Exception e) {
			TabbyChat.printException("Error loading chat hook.", e);
		}
		var1.displayGuiScreen(new GuiChatTC(defText));
	}

}
