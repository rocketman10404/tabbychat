package acs.tabbychat.settings;

import acs.tabbychat.core.TabbyChat;

public enum FormatCodeEnum {
	DEFAULT(TabbyChat.translator.getString("formats.default"), ""),
	BOLD(TabbyChat.translator.getString("formats.bold"), "\u00A7l"),
	STRIKED(TabbyChat.translator.getString("formats.striked"), "\u00A7m"),
	UNDERLINE(TabbyChat.translator.getString("formats.underline"), "\u00A7n"),
	ITALIC(TabbyChat.translator.getString("formats.italic"), "\u00A7o");
	
	private String title;
	private String code;
	
	private FormatCodeEnum(String _name, String _code) {
		this.title = _name;
		this.code = _code;
	}
	
	public String toString() {
		return this.code + this.title + "\u00A7r";
	}
	
	public String toCode() {
		return this.code;
	}
	
	public String color() {
		return this.title;
	}
	
	public static FormatCodeEnum cleanValueOf(String name) {
		try {
			return FormatCodeEnum.valueOf(name);
		} catch (Exception e) {
			return FormatCodeEnum.DEFAULT;
		}
	}
}
