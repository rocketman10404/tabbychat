package acs.tabbychat;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import net.minecraft.src.ServerData;

import net.minecraft.client.Minecraft;

public class ServerSettings {
	private ServerData server;
	private static File settingsFile;
	private File oldSettingsFile;
	protected ChannelDelimEnum chanDelims = ChannelDelimEnum.BRACKETS;
	protected ChatColorEnum chanDelimColor = ChatColorEnum.DEFAULTCOLOR;
	protected ChatColorEnum chanDelimFormat = ChatColorEnum.DEFAULTFORMAT;
	protected ArrayList<CustomChatFilter> customFilters = new ArrayList<CustomChatFilter>();
	protected String[] defaultChans = new String[0];
	protected String[] ignoredChans = new String[0];
	public String ip;
	public String name;
	
	public ServerSettings() {}
	
	protected void loadSettings() {
		File source;
		if (this.settingsFile != null && !this.settingsFile.exists()) {
			if (this.oldSettingsFile != null && !this.oldSettingsFile.exists()) {
				saveSettings();
				return;
			} else
				source = this.oldSettingsFile;
		} else
			source = this.settingsFile;
		
		try {
			FileInputStream settingsStream = new FileInputStream(source);
			ObjectInputStream ssObjStream = new ObjectInputStream(settingsStream);
			this.chanDelims = (ChannelDelimEnum)ssObjStream.readObject();
			this.defaultChans = (String[])ssObjStream.readObject();
			this.ignoredChans = (String[])ssObjStream.readObject();
			int numFilters = ssObjStream.readInt();
			this.customFilters.clear();
			for (int i = 0; i < numFilters; i++) {
				this.customFilters.add((CustomChatFilter)ssObjStream.readObject());
				if (this.customFilters.get(i).highlightColor == null)
					this.customFilters.get(i).highlightColor = ChatColorEnum.RED;
				if (this.customFilters.get(i).highlightFormat == null)
					this.customFilters.get(i).highlightFormat = ChatColorEnum.BOLD;
			}
			this.chanDelimColor = (ChatColorEnum)ssObjStream.readObject();
			this.chanDelimFormat = (ChatColorEnum)ssObjStream.readObject();
			ssObjStream.close();
			settingsStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("An error occurred while loading the server settings : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}
	
	protected void saveSettings() {
		try {
			FileOutputStream ssOutStream = new FileOutputStream(settingsFile);
			ObjectOutputStream ssObjStream = new ObjectOutputStream(ssOutStream);
			ssObjStream.writeObject(this.chanDelims);
			ssObjStream.writeObject(this.defaultChans);
			ssObjStream.writeObject(this.ignoredChans);
			ssObjStream.writeInt(this.customFilters.size());
			for (int i = 0; i < this.customFilters.size(); i++) {
				ssObjStream.writeObject(this.customFilters.get(i));
			}
			ssObjStream.writeObject(this.chanDelimColor);
			ssObjStream.writeObject(this.chanDelimFormat);
			ssObjStream.close();
			ssOutStream.close();
		} catch (IOException e) {
			TabbyChat.printErr("Unable to write to server settings file : '" + e.getLocalizedMessage() + "' : " + e.toString());
		}
	}
	
	public boolean filterMatchesChannel(int index, int _id) {
		return (this.customFilters.get(index).chanID == _id);
	}
	
	public boolean filterSentToTab(int index) {
		return this.customFilters.get(index).sendToTab;
	}
	
	public void filterSentToTab(int index, boolean setto) {
		this.customFilters.get(index).sendToTab = setto;
	}
	
	
	public int numFilters() {
		return this.customFilters.size();
	}
	
	public void updateForServer() {
		boolean clear = false;
		
		
		if (Minecraft.getMinecraft().isSingleplayer() || Minecraft.getMinecraft().getServerData() == null) {
			this.server = null;
			this.settingsFile = null;
			this.name = "";
			this.ip = "";
		} else {
			this.server = Minecraft.getMinecraft().getServerData();
			this.name = server.serverName;
			this.ip = server.serverIP;
		
			File settingsDir = new File(GlobalSettings.tabbyChatDir, "servers");
		
			if (!settingsDir.exists())
				settingsDir.mkdirs();
			if (!settingsDir.isDirectory())
				TabbyChat.printErr("Unable to create TabbyChat Settings folder");
		
			settingsFile = new File(settingsDir, this.ip+".cfg");
			this.oldSettingsFile = new File(new File(GlobalSettings.oldTabbyChatDir, "servers"), this.ip+".cfg");
		}
	}
}