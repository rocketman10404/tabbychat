package acs.tabbychat;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import net.minecraft.client.Minecraft;
import net.minecraft.src.StringUtils;

public class CustomChatFilter implements java.io.Serializable {
	protected String name;
	protected Pattern filter;
	protected boolean invert = false;
	protected boolean caseSensitive = true;
	protected boolean sendToTab = false;
	protected boolean highlight = false;
	protected ChatColorEnum highlightColor = ChatColorEnum.RED;
	protected ChatColorEnum highlightFormat = ChatColorEnum.BOLD;
	protected boolean ding = false;
	protected int chanID = 0;
	protected boolean active = true;
	private static final long serialVersionUID = 1245780L;
	private String lastMatch = "";
	
	protected CustomChatFilter() {
		this.name = "<New>";
		this.filter = Pattern.compile(Minecraft.getMinecraft().thePlayer.username);
	}
	
	protected CustomChatFilter copyOf() {
		CustomChatFilter obj = new CustomChatFilter();
		obj.name = this.name;
		obj.filter = this.filter;
		obj.invert = this.invert;
		obj.caseSensitive = this.caseSensitive;
		obj.chanID = this.chanID;
		obj.active = this.active;
		obj.sendToTab = this.sendToTab;
		obj.highlight = this.highlight;
		obj.highlightColor = this.highlightColor;
		obj.highlightFormat = this.highlightFormat;
		obj.ding = this.ding;
		return obj;
	}
	
	protected void updateExpression(String _exp) {
		if (this.caseSensitive)
			this.filter = Pattern.compile(_exp);
		else
			this.filter = Pattern.compile(_exp, Pattern.CASE_INSENSITIVE);
	}
	
	protected static ArrayList<CustomChatFilter> copyList(ArrayList<CustomChatFilter> list1) {
		ArrayList<CustomChatFilter> rtn = new ArrayList<CustomChatFilter>();
		for (int i = 0; i < list1.size(); i++) {
			rtn.add(list1.get(i).copyOf());
		}
		return rtn;
	}
	
	protected boolean applyFilter(String input) {
		Matcher m = this.filter.matcher(input);
		if (m.find()) {
			if (this.highlight) {
				StringBuilder sb = new StringBuilder(input);
				sb.insert(m.end(),"\u00A7r");
				sb.insert(m.start(), "\u00A7c\u00A7l");
				this.lastMatch = sb.toString();
			} else {
				this.lastMatch = input;
			}
			return true;
		} else if (this.invert) {
			this.lastMatch = input;
			return true;
		}
		return false;
	}
	
	protected boolean applyFilterToDirtyChat(String input) {
		int countCodes = input.replaceAll("[^\u00A7]", "").length(); 
		String[] removedCodes = new String[countCodes];
		int[] codeIndices = new int[countCodes];
		Pattern pullCodes = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
		int _start = 0;
		int _end = 0;
		int i = 0;
		
		StringBuilder result = new StringBuilder(input);
		Matcher matchCodes = pullCodes.matcher(result.toString());
		while(matchCodes.find()) {
			_start = matchCodes.start();
			_end = matchCodes.end();
			codeIndices[i] = _start;
			removedCodes[i] = result.substring(_start, _end);
			result.replace(_start, _end, "");		
			matchCodes = pullCodes.matcher(result.toString());
			i++;
		}
		Matcher matchFilter = this.filter.matcher(result.toString());
		boolean matched = false;
		while(matchFilter.find()) {
			matched = true;
			if (this.highlight) {
				_start = matchFilter.start();
				_end = matchFilter.end();
				int m;
				for (m = 0; m < codeIndices.length; m++) {
					if (codeIndices[m] >= _start)
						break;
				}
				String suffix = ChatColorEnum.RESET.getCode();

				if (codeIndices.length > 0 || m > 0) {
					if (m < codeIndices.length && codeIndices[m] <= _start)
						suffix = removedCodes[m];
					else
						suffix = removedCodes[m-1];
				}
				result.insert(_end, suffix);
				result.insert(_start, this.highlightColor.getCode()+this.highlightFormat.getCode());				
				for (int j = 0; j < codeIndices.length; j++) {
					if (codeIndices[j] > _start)
						codeIndices[j] = codeIndices[j] + 4;
					if (codeIndices[j] > _end)
						codeIndices[j] = codeIndices[j] + 2;
				}
			}
		}
		if (this.highlight) { 
			for (int k = codeIndices.length-1; k >= 0; k--) {
				result.insert(codeIndices[k], removedCodes[k]);
			}
			this.lastMatch = result.toString();
		} else
			this.lastMatch = input;
		if (!matched && this.invert)
			return true;
		else if (matched && !this.invert)
			return true;
		else if (matched && this.invert)
			return false;
		else if (!matched && !this.invert)
			return false;
		return false;
	}
	
	protected String getLastMatchPretty() {
		String tmp = new String(this.lastMatch);
		this.lastMatch = "";
		return tmp;
	}

	public String getHighlightDisplay() {
		return this.highlightColor.getCode() + this.highlightFormat.getCode() + this.highlightColor.toString() + ChatColorEnum.RESET.getCode();
	}
}
