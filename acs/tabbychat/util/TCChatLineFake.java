package acs.tabbychat.util;

import net.minecraft.src.ChatLine;

public class TCChatLineFake extends ChatLine {
	protected int updateCounterCreated;
	protected String lineString;
	protected int chatLineID;
	
	public TCChatLineFake() {
		super(-1, "", 0);
	}
	
	public TCChatLineFake(int _counter, String _string, int _id) {
		super(_counter, _string, _id);
		this.updateCounterCreated = _counter;
		if(_string == null) this.lineString = "";
		else this.lineString = _string;
		this.chatLineID = _id;
	}
	
	public String getChatLineString() {
		return this.lineString;
	}
	public int getUpdatedCounter() {
		return this.updateCounterCreated;
	}

	public int getChatLineID() {
		return this.chatLineID;
	}
}
