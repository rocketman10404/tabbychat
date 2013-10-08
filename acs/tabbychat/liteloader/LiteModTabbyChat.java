package acs.tabbychat.liteloader;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.src.Minecraft;
import net.minecraft.src.GuiChat;
import net.minecraft.src.GuiTextField;
import acs.tabbychat.core.GuiChatTC;
import acs.tabbychat.core.GuiNewChatTC;
import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.core.LiteLoader;

public class LiteModTabbyChat implements InitCompleteListener {
	private static GuiNewChatTC gnc;
	
	@Override
	public String getName() {
		return "TabbyChat";
	}

	@Override
	public String getVersion() {
		return TabbyChatUtils.version;
	}

	@Override
	public void onInitCompleted(Minecraft var1, LiteLoader var2) {
		TabbyChat.liteLoaded = true;
		gnc = GuiNewChatTC.getInstance();
	}

	@Override
	public void onTick(Minecraft var1, float var2, boolean var3, boolean var4) {
		TabbyChatUtils.chatGuiTick(var1);
	}

	@Override
	public void init(File configPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void upgradeSettings(String version, File configPath,
			File oldConfigPath) {
		// TODO Auto-generated method stub
		
	}
}
