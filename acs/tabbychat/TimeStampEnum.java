package acs.tabbychat;

public enum TimeStampEnum {

	MILITARY("[HHmm]",0, "[2359]"),
	MILITARYWITHCOLON("[HH:mm]",0, "[23:59]"),
	STANDARD("[hh:mm]",0, "[12:00]"),
	STANDARDWITHMARKER("[hh:mma]",0, "[12:00PM]");
	
	private String code;
	private int type;
	public String maxTime;
	
	private static final int format = 0;
	private static final int style = 1;
	
	private TimeStampEnum(String _code, int _type, String _maxTime) {
		this.type = _type;
		this.code = _code;
		this.maxTime = _maxTime;
	}
	

	public String toString() {
		return this.maxTime;
	}
	
	public String toCode() {
		return this.code;
	}

	public TimeStampEnum nextFormat() {
		if (this.type != format) {
			return TimeStampEnum.MILITARY;
		} else {
			TimeStampEnum[] theList = TimeStampEnum.values();
			int i = this.ordinal();
			i++;
			if (i<theList.length && theList[i].type == format)
				return theList[i];
			else
				return TimeStampEnum.MILITARY;
		}
	}
	
	public TimeStampEnum prevFormat() {
		if (this.type != format) {
			return TimeStampEnum.STANDARDWITHMARKER;
		} else {
			TimeStampEnum[] theList = TimeStampEnum.values();
			int i = this.ordinal();
			i--;
			if (i>=0 && theList[i].type == format)
				return theList[i];
			else
				return TimeStampEnum.STANDARDWITHMARKER;
		}
	}
}
