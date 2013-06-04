package acs.tabbychat.settings;

import acs.tabbychat.core.TabbyChat;

public enum ChannelDelimEnum {
	ANGLES(TabbyChat.translator.getString("delims.angles"), "<", ">"),
	BRACES(TabbyChat.translator.getString("delims.braces"), "{", "}"),
	BRACKETS(TabbyChat.translator.getString("delims.brackets"), "[", "]"),
	PARENTHESIS(TabbyChat.translator.getString("delims.parenthesis"), "(", ")"),
	ANGLESPARENSCOMBO(TabbyChat.translator.getString("delims.anglesparenscombo"), "<\\(", ")(?: |\u00A7r)?[A-Za-z0-9_]{1,16}>"),
	ANGLESBRACKETSCOMBO(TabbyChat.translator.getString("delims.anglesbracketscombo"), "<\\[", "](?: |\u00A7r)?[A-Za-z0-9_]{1,16}>");
	
	private String title;
	private String open;
	private String close;
	
	private ChannelDelimEnum(String title, String open, String close) {
		this.title = title;
		this.open = open;
		this.close = close;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String toString() {
		return this.title;
	}
	
	public void setValue(String _title) {
		for (ChannelDelimEnum tmp : ChannelDelimEnum.values()) {
			if (_title.equals(tmp.title)) {
				this.title = tmp.title;
				this.open = tmp.open;
				this.close = tmp.close;
				break;
			}
		}
	}
	
	public String open() {
		return this.open;
	}
	
	public String close() {
		return this.close;
	}
}
