package acs.tabbychat.jazzy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.swabunga.spell.event.SpellCheckEvent;

import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.Minecraft;

public class TCSpellCheckManager {
	private static TCSpellCheckListener listener;
	private static TCSpellCheckManager instance = null;
	private static HashMap<Integer, String> errorCache = new HashMap<Integer, String>();
	private static final ReentrantReadWriteLock errorLock = new ReentrantReadWriteLock();
	private static final Lock errorReadLock = errorLock.readLock();
	private static final Lock errorWriteLock = errorLock.writeLock();
	
	private TCSpellCheckManager() {
		listener = new TCSpellCheckListener();
	}
	
	public void drawErrors(GuiScreen screen, List<GuiTextField> inputFields) {
		String inputTemp = inputFields.get(0).getText();
		errorReadLock.lock();
		try {
			Iterator<Map.Entry<Integer, String>> errors = errorCache.entrySet().iterator();
			Map.Entry<Integer, String> error;
			while(errors.hasNext()) {
				error = errors.next();
				int y = screen.height - 4;
				int x = 4;
				int wordIndex = error.getKey();
				for(GuiTextField field : inputFields) {
					if(field.getVisible()) {
						if(wordIndex > field.getText().length()) {
							wordIndex -= field.getText().length();
							y -= 12;
						} else {
							inputTemp = field.getText();
						}
					}
				}
				x += Minecraft.getMinecraft().fontRenderer.getStringWidth(inputTemp.substring(0, wordIndex));
				int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(error.getValue());
				System.out.println("Underlining '"+error.getValue()+"' @ ("+x+", "+y+"), width="+width);
				this.drawUnderline(screen, x, y, width);
			}
		} finally {
			errorReadLock.unlock();
		}
	}
	
	private void drawUnderline(GuiScreen screen, int x, int y, int width) {
		int next = x + 2;
		while(next - x < width) {
			screen.drawRect(next-2, y, next, y+1, 0xffff0000);
			next+=3;
		}
	}
	
	public static TCSpellCheckManager getInstance() {
		if(instance == null) {
			instance = new TCSpellCheckManager();
		}
		return  instance;
	}
	
	protected void handleListenerEvent(SpellCheckEvent event) {
		errorWriteLock.lock();
		try {
			errorCache.put(event.getWordContextPosition(), event.getInvalidWord());
		} finally {
			errorWriteLock.unlock();
		}
	}
	
	public void update(List<GuiTextField> inputFields) {
		// Clear stored error words and locations
		errorWriteLock.lock();
		try {
			errorCache.clear();
		} finally {
			errorWriteLock.unlock();
		}
		// Clear and re-populate contents of input fields, initiate spell checker
		String inputCache = "";
		for(GuiTextField inputField : inputFields) {
			String text = inputField.getText();
			if(!inputField.getVisible() || text.length() == 0) break;
			inputCache = inputCache + text;
		}
		listener.checkSpelling(inputCache);
	}
}
