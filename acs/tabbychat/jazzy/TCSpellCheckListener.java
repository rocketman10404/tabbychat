package acs.tabbychat.jazzy;

import java.io.File;

import acs.tabbychat.core.TabbyChat;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

public class TCSpellCheckListener implements SpellCheckListener {

	//private static File dictPath;
	private SpellChecker spellCheck = null;
	
	public TCSpellCheckListener() {
		try {
			File d = new File(TCSpellCheckListener.class.getResource("/english.0").toURI());
			SpellDictionary dictionary = new SpellDictionaryHashMap(d);
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
		System.out.println("MISSPELLING: "+event.getInvalidWord());		
	}
	
	public void checkSpelling(String line) {
		this.spellCheck.checkSpelling(new StringWordTokenizer(line));
	}

}
