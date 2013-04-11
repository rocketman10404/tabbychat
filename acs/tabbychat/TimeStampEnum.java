package acs.tabbychat;

public enum TimeStampEnum {

	MILITARY("[HHmm]","[2359]"),
	MILITARYWITHCOLON("[HH:mm]","[23:59]"),
	STANDARD("[hh:mm]","[12:00]"),
	STANDARDWITHMARKER("[hh:mma]","[12:00PM]");
	
	private String code;
	public String maxTime;
	
	private TimeStampEnum(String _code, String _maxTime) {
		this.code = _code;
		this.maxTime = _maxTime;
	}
	

	public String toString() {
		return this.maxTime;
	}
	
	public String toCode() {
		return this.code;
	}
}
