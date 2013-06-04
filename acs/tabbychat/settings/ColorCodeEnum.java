package acs.tabbychat.settings;

import acs.tabbychat.core.TabbyChat;

public enum ColorCodeEnum {
	DEFAULT(TabbyChat.translator.getString("colors.default"), ""),
	DARKBLUE(TabbyChat.translator.getString("colors.darkblue"), "\u00A71"),
	DARKGREEN(TabbyChat.translator.getString("colors.darkgreen"), "\u00A72"),
	DARKAQUA(TabbyChat.translator.getString("colors.darkaqua"), "\u00A73"),
	DARKRED(TabbyChat.translator.getString("colors.darkred"), "\u00A74"),
	PURPLE(TabbyChat.translator.getString("colors.purple"), "\u00A75"),
	GOLD(TabbyChat.translator.getString("colors.gold"), "\u00A76"),
	GRAY(TabbyChat.translator.getString("colors.gray"), "\u00A77"),
	DARKGRAY(TabbyChat.translator.getString("colors.darkgray"), "\u00A78"),
	INDIGO(TabbyChat.translator.getString("colors.indigo"), "\u00A79"),
	BRIGHTGREEN(TabbyChat.translator.getString("colors.brightgreen"), "\u00A7a"),
	AQUA(TabbyChat.translator.getString("colors.aqua"), "\u00A7b"),
	RED(TabbyChat.translator.getString("colors.red"), "\u00A7c"),
	PINK(TabbyChat.translator.getString("colors.pink"), "\u00A7d"),
	YELLOW(TabbyChat.translator.getString("colors.yellow"), "\u00A7e"),
	WHITE(TabbyChat.translator.getString("colors.white"), "\u00A7f");
	
	
	private String title;
	private String code;
	
	private ColorCodeEnum(String _name, String _code) {
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

	public static ColorCodeEnum cleanValueOf(String name) {
		try {
			return ColorCodeEnum.valueOf(name);
		} catch (Exception e) {
			return ColorCodeEnum.YELLOW;
		}
	}
}
