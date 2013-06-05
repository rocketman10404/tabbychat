package acs.tabbychat.liteloader;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.util.TabbyChatUtils;

import com.mumfrey.liteloader.LiteMod;

public class TabbyChatMod implements LiteMod {
	private static TabbyChat tc;
	
	@Override
    public String getName() {
    	return "TabbyChat";
    }

	@Override
    public String getVersion() {
    	return TabbyChatUtils.version;
    }

	@Override
    public void init() {
    	tc = TabbyChat.instance;
    }
}
