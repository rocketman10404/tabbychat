package acs.tabbychat.lang;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.gui.TCSettingsGUI;

public class TCTranslate {
	private final HashMap<String, String> dict = new HashMap();
	private String provides = null;
	
	public TCTranslate(String _lang) {
		this.provides = _lang;
		if(_lang.contentEquals("en_US")) this.dict.putAll((Map)TCEnglishDefault.defaults);
		else this.loadDictionary();
	}
	
	private void initEnglishDefaults() {
		Properties defaults = new Properties();
	}
	
	private void loadDictionary() {
		if(this.provides == null) return;
		
		File languageDir = new File(TCSettingsGUI.tabbyChatDir, "lang");
		File languageFile = new File(languageDir, "tabbychat.dictionary."+this.provides);
		if(!languageFile.canRead()) {
			this.dict.clear();
			this.dict.putAll((Map)TCEnglishDefault.defaults);
			return;
		}
		
		Properties dictTable = new Properties(TCEnglishDefault.defaults);
		try {
			FileInputStream fInStream = new FileInputStream(languageFile);
			BufferedInputStream bInStream = new BufferedInputStream(fInStream);
			dictTable.load(bInStream);
			bInStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to load translation for "+this.provides);
			return;
		}
		
		this.dict.clear();
		this.dict.putAll((Map)dictTable);
	}
	
	public String getString(String field) {
		String translated = this.dict.get(field);
		if(translated == null) return " ";
		else return translated;
	}
}
