package acs.tabbychat.settings;

import acs.tabbychat.TabbyChat;

public class TCAutoChannelSearch extends acs.tabbychat.TCSettingBool {
	private static TabbyChat tc = TabbyChat.instance;

	public TCAutoChannelSearch() {
		super(tc.globalPrefs.autoSearchEnabled, "Auto-search for new channels", 1001);
	}	
}
