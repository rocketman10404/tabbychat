package acs.tabbychat.settings;

public enum ChannelDelimEnum {
	ANGLES("<Angles>", "<", ">"),
	BRACES("{Braces}", "{", "}"),
	BRACKETS("[Brackets]", "[", "]"),
	PARENTHESIS("(Parenthesis)", "(", ")"),
	ANGLESPARENSCOMBO("<(Combo)Pl.>", "<\\(", ")(?: |\u00A7r)?[A-Za-z0-9_]{1,16}>"),
	ANGLESBRACKETSCOMBO("<[Combo]Pl.>", "<\\[", "](?: |\u00A7r)?[A-Za-z0-9_]{1,16}>");
	
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
