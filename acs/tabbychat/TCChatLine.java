package acs.tabbychat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.minecraft.src.ChatLine;

public class TCChatLine extends TCChatLineFake implements Serializable {
	private static final long serialVersionUID = 646162627943686174L;
	protected boolean statusMsg = false;
		
	public TCChatLine(int _counter, String _string, int _id) {
		super(_counter, _string, _id);
	}
	
	public TCChatLine(int _counter, String _string, int _id, boolean _stat) {
		this(_counter, _string, _id);
		this.statusMsg = _stat;
	}
	
	private void writeObject(ObjectOutputStream _write) throws IOException {
		_write.writeUTF(this.getChatLineString());
		_write.writeBoolean(this.statusMsg);
	}
	
	private void readObject(ObjectInputStream _read) throws IOException, ClassNotFoundException {
		this.updateCounterCreated = -1;
		this.lineString = _read.readUTF();
		this.statusMsg = _read.readBoolean();
	}
}
