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
}
