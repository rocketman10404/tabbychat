package acs.tabbychat.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import acs.tabbychat.util.TCChatLineFake;

import net.minecraft.src.ChatLine;

public class TCChatLine extends TCChatLineFake implements Serializable {
	private static final long serialVersionUID = 646162627943686174L;
	protected boolean statusMsg = false;
	protected String timeStamp = "";
		
	public TCChatLine(int _counter, String _string, int _id) {
		super(_counter, _string, _id);
	}
	
	public TCChatLine(ChatLine _cl) {
		super(_cl.getUpdatedCounter(), _cl.getChatLineString(), _cl.getChatLineID());
	}
	
	public TCChatLine(int _counter, String _string, int _id, boolean _stat) {
		this(_counter, _string, _id);
		this.statusMsg = _stat;
	}
	
	protected void setChatLineString(String newLine) {
		this.lineString = newLine;
	}
		
	private void writeObject(ObjectOutputStream _write) throws IOException {
		_write.writeUTF(this.getChatLineString());
		_write.writeBoolean(this.statusMsg);
		_write.writeUTF(this.timeStamp);
	}
	
	private void readObject(ObjectInputStream _read) throws IOException, ClassNotFoundException {
		this.updateCounterCreated = -1;
		this.lineString = _read.readUTF();
		this.statusMsg = _read.readBoolean();
		this.timeStamp = _read.readUTF();
	}
}
