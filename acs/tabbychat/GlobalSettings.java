package acs.tabbychat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import net.minecraft.client.Minecraft;

public class GlobalSettings {
	private File settingsFile;
	protected static File tabbyChatDir = new File(Minecraft.getMinecraftDir(), new StringBuilder().append("config").append(File.separatorChar).append("tabbychat").toString());
	public boolean autoSearchEnabled = true;
	public int maxChannelNameLength = 10;
	public boolean saveLocalLogEnabled = false;
	public boolean TCenabled = true;
	public boolean timestampsEnabled = false;
	public TimeStampEnum timestampStyle = TimeStampEnum.MILITARYWITHCOLON;
	public int retainedChats = 100;
	
	public GlobalSettings() {	
		this.settingsFile = new File(tabbyChatDir, "global.cfg");
	}
	
	protected void loadSettings() {
		File source = this.settingsFile;;
		if (!this.settingsFile.exists())
			return;
		
		try {
			FileInputStream settingsStream = new FileInputStream(source);
			ObjectInputStream gsObjStream = new ObjectInputStream(settingsStream);
			this.TCenabled = gsObjStream.readBoolean();
			this.autoSearchEnabled = gsObjStream.readBoolean();
			this.retainedChats = gsObjStream.readInt();
			this.maxChannelNameLength = gsObjStream.readInt();
			this.timestampsEnabled = gsObjStream.readBoolean();
			this.saveLocalLogEnabled = gsObjStream.readBoolean();
			this.timestampStyle = (TimeStampEnum)gsObjStream.readObject();
			gsObjStream.close();
			settingsStream.close();
		} catch (Exception e) {
			//TabbyChat.printErr("An error occurred while loading the global settings : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}

	protected void saveSettings() {
		try {
			FileOutputStream settingsStream = new FileOutputStream(this.settingsFile);
			ObjectOutputStream gsObjStream = new ObjectOutputStream(settingsStream);
			gsObjStream.writeBoolean(this.TCenabled);
			gsObjStream.writeBoolean(this.autoSearchEnabled);
			gsObjStream.writeInt(this.retainedChats);
			gsObjStream.writeInt(this.maxChannelNameLength);
			gsObjStream.writeBoolean(this.timestampsEnabled);
			gsObjStream.writeBoolean(this.saveLocalLogEnabled);
			gsObjStream.writeObject(this.timestampStyle);
			gsObjStream.close();
			settingsStream.close();
		} catch (IOException e) {
			//TabbyChat.printErr("Unable to write to global settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}
}
