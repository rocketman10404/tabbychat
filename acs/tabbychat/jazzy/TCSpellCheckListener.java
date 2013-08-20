package acs.tabbychat.jazzy;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import acs.tabbychat.core.TabbyChat;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

public class TCSpellCheckListener implements SpellCheckListener {
	protected SpellChecker spellCheck = null;
	
	public TCSpellCheckListener() {
		try {
			InputStream in = TCSpellCheckListener.class.getResourceAsStream("/english.0");
			SpellDictionary dictionary = new SpellDictionaryHashMap(new InputStreamReader(in));
			this.spellCheck = new SpellChecker(dictionary);
			this.spellCheck.addSpellCheckListener(this);
		} catch (Exception e) {
			TabbyChat.printException("", e);
		}
		
	}
	
	public TCSpellCheckListener(File dict) {
		try {
			SpellDictionary dictionary = new SpellDictionaryHashMap(dict);
			this.spellCheck = new SpellChecker(dictionary);
			this.spellCheck.addSpellCheckListener(this);
		} catch (Exception e) {
			TabbyChat.printException("Error instantiating spell checker", e);
		}
	}
	
	@Override
	public void spellingError(SpellCheckEvent event) {
		TabbyChat.spellChecker.handleListenerEvent(event);		
	}
	
	public void checkSpelling(String line) {
		this.spellCheck.checkSpelling(new StringWordTokenizer(line));
	}

}
