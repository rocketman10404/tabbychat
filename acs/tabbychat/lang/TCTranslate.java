package acs.tabbychat.lang;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import acs.tabbychat.core.TabbyChat;
import acs.tabbychat.gui.ITCSettingsGUI;

public class TCTranslate {
	private final HashMap<String, String> dict = new HashMap();
	private String provides = null;
	
	public TCTranslate(String _lang) {
		this.provides = _lang;
		if(_lang.equals("en_US")) this.dict.putAll((Map)TCEnglishDefault.defaults);
		else if(!this.loadDictionary()) {
			if(_lang.equals("ru_RU")) this.dict.putAll((Map)TCRussianDefault.defaults);
			else if(_lang.equals("de_DE")) this.dict.putAll((Map)TCGermanDefault.defaults);
			else if(_lang.equals("es_ES")) this.dict.putAll((Map)TCSpanishDefault.defaults);
			else if(_lang.equals("et_EE")) this.dict.putAll((Map)TCEstonianDefault.defaults);
			else this.dict.putAll((Map)TCEnglishDefault.defaults);
		}
	}
	
	private boolean loadDictionary() {
		if(this.provides == null) return false;
		
		File languageDir = new File(ITCSettingsGUI.tabbyChatDir, "lang");
		File languageFile = new File(languageDir, "tabbychat.dictionary."+this.provides);
		if(!languageFile.canRead()) {
			this.dict.clear();
			this.dict.putAll((Map)TCEnglishDefault.defaults);
			return false;
		}
		
		Properties dictTable = new Properties(TCEnglishDefault.defaults);
		try {
			FileInputStream fInStream = new FileInputStream(languageFile);
			BufferedInputStream bInStream = new BufferedInputStream(fInStream);
			dictTable.load(bInStream);
			bInStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to load translation for "+this.provides);
			return false;
		}
		
		this.dict.clear();
		this.dict.putAll((Map)dictTable);
		return true;
	}
	
	public String getString(String field) {
		String translated = this.dict.get(field);
		if(translated == null) return " ";
		else return translated;
	}
}
