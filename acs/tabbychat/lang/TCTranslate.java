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
	private HashMap<String, String> dict = new HashMap();
	protected static String provides = null;
	protected static final HashMap<String, Class> langSupport = new HashMap();
	static {
		langSupport.put("en_US", TCLanguageEnglish.class);
		langSupport.put("ru_RU", TCLanguageRussian.class);
		langSupport.put("et_EE", TCLanguageEstonian.class);
		langSupport.put("es_ES", TCLanguageSpanish.class);
		langSupport.put("de_DE", TCLanguageGerman.class);
		langSupport.put("uk_UA", TCLanguageUkranian.class);
		langSupport.put("sv_SE", TCLanguageSwedish.class);
		langSupport.put("fr_FR", TCLanguageFrench.class);
		langSupport.put("fi_FI", TCLanguageFinnish.class);
	}
	
	public TCTranslate(String _lang) {
		this.dict.clear();
		provides = _lang;
		if(this.loadDictionary()) return;
		
		Class dictClass;
		if(langSupport.containsKey(_lang)) {
			try {
				dictClass = langSupport.get(_lang);
				provides = (String)dictClass.getDeclaredField("provides").get(null);
				this.dict.putAll((Map)dictClass.getDeclaredField("defaults").get(null));
			} catch (Exception e) {
				TabbyChat.printException("Error occurred loading language:", e);
				provides = "en_us";
				this.dict.putAll((Map)TCLanguageEnglish.defaults);
			}
		} else {
			provides = "en_us";
			this.dict.putAll((Map)TCLanguageEnglish.defaults);
		}
		
	}
	
	private boolean loadDictionary() {		
		File languageDir = new File(ITCSettingsGUI.tabbyChatDir, "lang");
		File languageFile = new File(languageDir, "tabbychat.dictionary."+provides);
		if(!languageFile.canRead()) {
			this.dict.clear();
			this.dict.putAll((Map)TCLanguage.defaults);
			return false;
		}
		
		Properties dictTable = new Properties(TCLanguage.defaults);
		try {
			FileInputStream fInStream = new FileInputStream(languageFile);
			BufferedInputStream bInStream = new BufferedInputStream(fInStream);
			dictTable.load(bInStream);
			bInStream.close();
		} catch (Exception e) {
			TabbyChat.printErr("Unable to load translation for "+provides);
			return false;
		}
		
		this.dict.clear();
		this.dict.putAll((Map)dictTable);
		return true;
	}
	
	public String getCurrentLang() {
		return provides;
	}
	
	public String getString(String field) {
		String translated = this.dict.get(field);
		if(translated == null) {
			translated = TCLanguageEnglish.defaults.getProperty(field);
			if(translated == null) return " ";
		}
		return translated;
	}
}
