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
	private Calendar logDay = Calendar.getInstance();
	private File logFile;
	private SimpleDateFormat logNameFormat = new SimpleDateFormat("'TabbyChatLog_'MM-dd-yyyy'.txt'");
	private File settingsFile;
	protected static File tabbyChatDir = new File(Minecraft.getMinecraftDir(), new StringBuilder().append("mods").append(File.separatorChar).append("tabbychat").toString());
	protected SimpleDateFormat timeStamp = new SimpleDateFormat();
	public boolean autoSearchEnabled = true;
	public int maxChannelNameLength = 10;
	public boolean saveLocalLogEnabled = false;
	public boolean TCenabled = true;
	public boolean timestampsEnabled = false;
	public TimeStampEnum timestampStyle = TimeStampEnum.MILITARYWITHCOLON;
	public int retainedChats = 100;
	
	public GlobalSettings() {
		File settingsDir = tabbyChatDir;
		File logDir = new File(tabbyChatDir, "logs");
		
		if (!settingsDir.exists())
			settingsDir.mkdirs();
		if (!settingsDir.isDirectory())
			TabbyChat.printErr("Unable to create TabbyChat Settings folder");
		
		if (!logDir.exists())
			logDir.mkdirs();
		if (!logDir.isDirectory())
			TabbyChat.printErr("Unable to create TabbyChat Log folder");
		
		this.settingsFile = new File(settingsDir, "global_v2.cfg");
		this.logFile = new File(logDir, this.logNameFormat.format(this.logDay.getTime()));
	}
	
	protected void loadSettings() {
		if (!this.settingsFile.exists()) {
			saveSettings();
			return;
		}
		
		try {
			FileInputStream settingsStream = new FileInputStream(this.settingsFile);
			ObjectInputStream gsObjStream = new ObjectInputStream(settingsStream);
			this.TCenabled = gsObjStream.readBoolean();
			this.autoSearchEnabled = gsObjStream.readBoolean();
			this.retainedChats = gsObjStream.readInt();
			this.maxChannelNameLength = gsObjStream.readInt();
			this.timestampsEnabled = gsObjStream.readBoolean();
			this.saveLocalLogEnabled = gsObjStream.readBoolean();
			this.timestampStyle = (TimeStampEnum)gsObjStream.readObject();
			this.timeStamp.applyPattern(this.timestampStyle.toString());
			gsObjStream.close();
			settingsStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("An error occurred while loading the global settings : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}

	protected void saveSettings() {
		try {
			FileOutputStream settingsStream = new FileOutputStream(settingsFile);
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
			TabbyChat.printErr("Unable to write to global settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}
	
	public void logChat(String theChat) {
		Calendar tmpcal = Calendar.getInstance();
		if (tmpcal.get(Calendar.DAY_OF_YEAR) != this.logDay.get(Calendar.DAY_OF_YEAR)) {
			this.logDay = tmpcal;
			this.logFile = new File(tabbyChatDir, new StringBuilder().append("logs").append(File.separatorChar).append(this.logNameFormat.format(this.logDay.getTime())).toString());
		}
		
		if (!this.logFile.exists()) {
			try {
				this.logFile.createNewFile();
			} catch (Exception e) {
				TabbyChat.printErr("Cannot create log file : '" + e.getLocalizedMessage() + "' : " + e.toString());
				return;
			}
		}
		
		try {
			FileOutputStream logStream = new FileOutputStream(this.logFile, true);
			PrintStream logPrint = new PrintStream(logStream);
			logPrint.println(theChat);
			logPrint.close();
		} catch (Exception e) {
			TabbyChat.printErr("Cannot write to log file : '" + e.getLocalizedMessage() + "' : " + e.toString());
			return;
		}
	}
}
