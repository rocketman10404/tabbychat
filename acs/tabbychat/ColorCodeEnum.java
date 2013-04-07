package acs.tabbychat;

public enum ColorCodeEnum {
	DEFAULT("Default", ""),
	DARKBLUE("Dark Blue", "\u00A71"),
	DARKGREEN("Dark Green", "\u00A72"),
	DARKAQUA("Dark Aqua", "\u00A73"),
	DARKRED("Dark Red", "\u00A74"),
	PURPLE("Purple", "\u00A75"),
	GOLD("Gold", "\u00A76"),
	GRAY("Gray", "\u00A77"),
	DARKGRAY("Dark Gray", "\u00A78"),
	INDIGO("Indigo", "\u00A79"),
	BRIGHTGREEN("Bright Green", "\u00A7a"),
	AQUA("Aqua", "\u00A7b"),
	RED("Red", "\u00A7c"),
	PINK("Pink", "\u00A7d"),
	YELLOW("Yellow", "\u00A7e"),
	WHITE("White", "\u00A7f");
	
	
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

}
