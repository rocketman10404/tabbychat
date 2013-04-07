package acs.tabbychat;

public enum FormatCodeEnum {
	DEFAULT("Default", ""),
	BOLD("Bold", "\u00A7l"),
	STRIKED("Striked", "\u00A7m"),
	UNDERLINE("Underlined", "\u00A7n"),
	ITALIC("Italic", "\u00A7o");
	
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
}
