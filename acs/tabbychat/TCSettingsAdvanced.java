package acs.tabbychat;

public class TCSettingsAdvanced extends TCSettingsGUI {

	public TCSettingsAdvanced() {
		super();
		this.name = "Advanced Settings";
		this.bgcolor = 0x66802e94;
	}
	
	protected TCSettingsAdvanced(TabbyChat _tc) {
		this();
		tc = _tc;
	}
	
}
