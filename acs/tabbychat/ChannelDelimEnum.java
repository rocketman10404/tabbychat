package acs.tabbychat;

public enum ChannelDelimEnum {
	ANGLES("Angles", "<", ">"),
	BRACES("Braces", "{", "}"),
	BRACKETS("Brackets", "[", "]"),
	PARENTHESIS("Parenthesis", "(", ")");
	
	private String title;
	private char open;
	private char close;
	
	private ChannelDelimEnum(String title, String open, String close) {
		this.title = title;
		this.open = open.charAt(0);
		this.close = close.charAt(0);
	}
	
	public String getTitle() {
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
		return Character.toString(this.open);
	}
	
	public String close() {
		return Character.toString(this.close);
	}

	protected ChannelDelimEnum next() {
		if (this.equals(ANGLES)) {
			return BRACES;
		} else if (this.equals(BRACES)) {
			return BRACKETS;
		} else if (this.equals(BRACKETS)) {
			return PARENTHESIS;
		} else {
			return ANGLES;
		}
	}
}
