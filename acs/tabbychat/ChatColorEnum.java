package acs.tabbychat;

public enum ChatColorEnum {
	DEFAULTCOLOR("Any", 0, ""),
	BLACK("Black", 0, "\u00A70"),
	DARKBLUE("Dark Blue", 0, "\u00A71"),
	DARKGREEN("Dark Green", 0, "\u00A72"),
	DARKAQUA("Dark Aqua", 0, "\u00A73"),
	DARKRED("Dark Red", 0, "\u00A74"),
	PURPLE("Purple", 0, "\u00A75"),
	GOLD("Gold", 0, "\u00A76"),
	GRAY("Gray", 0, "\u00A77"),
	DARKGRAY("Dark Gray", 0, "\u00A78"),
	INDIGO("Indigo", 0, "\u00A79"),
	BRIGHTGREEN("Bright Green", 0, "\u00A7a"),
	AQUA("Aqua", 0, "\u00A7b"),
	RED("Red", 0, "\u00A7c"),
	PINK("Pink", 0, "\u00A7d"),
	YELLOW("Yellow", 0, "\u00A7e"),
	WHITE("White", 0, "\u00A7f"),
	DEFAULTFORMAT("Any", 1, ""),
	BOLD("Bold", 1, "\u00A7l"),
	STRIKED("Striked", 1, "\u00A7m"),
	UNDERLINE("Underlined", 1, "\u00A7n"),
	ITALIC("Italic", 1, "\u00A7o"),
	RESET("Reset", 2, "\u00A7r");
	
	private String title;
	private int type;
	private String code;
	
	private static final int color = 0;
	private static final int format = 1;
	private static final int normal = 2;
	
	private ChatColorEnum(String _name, int _type, String _code) {
		this.title = _name;
		this.type = _type;
		this.code = _code;
	}
	
	public String toString() {
		return this.title;
	}
	
	public String getCode() {
		return this.code;
	}

	public ChatColorEnum nextColor() {
		if (this.type != color) {
			return ChatColorEnum.DEFAULTCOLOR;
		} else {
			ChatColorEnum[] theList = ChatColorEnum.values();
			int i = this.ordinal();
			i++;
			if (i<theList.length && theList[i].type == color)
				return theList[i];
			else
				return ChatColorEnum.RESET.nextColor();
		}
	}

	public ChatColorEnum prevColor() {
		if (this.type != color) {
			return ChatColorEnum.WHITE;
		} else {
			ChatColorEnum[] theList = ChatColorEnum.values();
			int i = this.ordinal();
			i--;
			if (i>=0 && theList[i].type == color)
				return theList[i];
			else
				return ChatColorEnum.RESET.prevColor();
		}
	}
	
	public ChatColorEnum nextFormat() {
		if (this.type != format) {
			return ChatColorEnum.DEFAULTFORMAT;
		} else {
			ChatColorEnum[] theList = ChatColorEnum.values();
			int i = this.ordinal();
			i++;
			if (i<theList.length && theList[i].type == format)
				return theList[i];
			else
				return ChatColorEnum.RESET.nextFormat();
		}
	}
	
	public ChatColorEnum prevFormat() {
		if (this.type != format) {
			return ChatColorEnum.ITALIC;
		} else {
			ChatColorEnum[] theList = ChatColorEnum.values();
			int i = this.ordinal();
			i--;
			if (i>=0 && theList[i].type == format)
				return theList[i];
			else
				return ChatColorEnum.RESET.prevFormat();
		}
	}
}
