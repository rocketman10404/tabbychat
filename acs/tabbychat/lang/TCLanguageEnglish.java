package acs.tabbychat.lang;

import java.util.Properties;

public class TCLanguageEnglish extends TCLanguage {
	protected static String provides;
	protected final static Properties defaults = new Properties();
	static {
		provides = "en_US";
		defaults.clear();
		
		// ENGLISH STRINGS FOR DELIMITERS
		defaults.setProperty("delims.angles", "<Angles>");
		defaults.setProperty("delims.braces", "{Braces}");
		defaults.setProperty("delims.brackets", "[Brackets]");
		defaults.setProperty("delims.parenthesis", "(Parenthesis)");
		defaults.setProperty("delims.anglesparenscombo", "<(Combo)Pl.>");
		defaults.setProperty("delims.anglesbracketscombo", "<[Combo]Pl.>");
		
		// ENGLISH STRINGS FOR COLORS
		defaults.setProperty("colors.default", "Default");
		defaults.setProperty("colors.darkblue", "Dark Blue");
		defaults.setProperty("colors.darkgreen", "Dark Green");
		defaults.setProperty("colors.darkaqua", "Dark Aqua");
		defaults.setProperty("colors.darkred", "Dark Red");
		defaults.setProperty("colors.purple", "Purple");
		defaults.setProperty("colors.gold", "Gold");
		defaults.setProperty("colors.gray", "Gray");
		defaults.setProperty("colors.darkgray", "Dark Gray");
		defaults.setProperty("colors.indigo", "Indigo");
		defaults.setProperty("colors.brightgreen", "Bright Green");
		defaults.setProperty("colors.aqua", "Aqua");
		defaults.setProperty("colors.red", "Red");
		defaults.setProperty("colors.pink", "Pink");
		defaults.setProperty("colors.yellow", "Yellow");
		defaults.setProperty("colors.white", "White");
		
		// ENGLISH STRINGS FOR FORMATS
		defaults.setProperty("formats.default", "Default");
		defaults.setProperty("formats.bold", "Bold");
		defaults.setProperty("formats.striked", "Striked");
		defaults.setProperty("formats.underline", "Underlined");
		defaults.setProperty("formats.italic", "Italic");
		
		// ENGLISH STRINGS FOR SOUNDS
		defaults.setProperty("sounds.orb", "Orb");
		defaults.setProperty("sounds.anvil", "Anvil");
		defaults.setProperty("sounds.bowhit", "Bow Hit");
		defaults.setProperty("sounds.break", "Break");
		defaults.setProperty("sounds.click", "Click");
		defaults.setProperty("sounds.glass", "Glass");
		defaults.setProperty("sounds.bass", "Bass");
		defaults.setProperty("sounds.harp", "Harp");
		defaults.setProperty("sounds.pling", "Pling");
		defaults.setProperty("sounds.cat", "Cat");
		defaults.setProperty("sounds.blast", "Blast");
		defaults.setProperty("sounds.splash", "Splash");
		defaults.setProperty("sounds.swim", "Swim");
		defaults.setProperty("sounds.bat", "Bat");
		defaults.setProperty("sounds.blaze", "Blaze");
		defaults.setProperty("sounds.chicken", "Chicken");
		defaults.setProperty("sounds.cow", "Cow");
		defaults.setProperty("sounds.dragon", "Dragon");
		defaults.setProperty("sounds.endermen", "Endermen");
		defaults.setProperty("sounds.ghast", "Ghast");
		defaults.setProperty("sounds.pig", "Pig");
		defaults.setProperty("sounds.wolf", "Wolf");
		
		// ENGLISH STRINGS FOR SETTINGS - COMMON
		defaults.setProperty("settings.save", "Save");
		defaults.setProperty("settings.cancel", "Cancel");
		defaults.setProperty("settings.new", "New");
		defaults.setProperty("settings.delete", "Delete");
		
		// ENGLISH STRINGS FOR SETTINGS - 'GENERAL CONFIG'
		defaults.setProperty("settings.general.name", "General");
		defaults.setProperty("settings.general.tabbychatenable", "TabbyChat Enabled");
		defaults.setProperty("settings.general.savechatlog", "Log chat to file");
		defaults.setProperty("settings.general.timestampenable", "Timestamp chat");
		defaults.setProperty("settings.general.timestampstyle", "Timestamp style");
		defaults.setProperty("settings.general.timestampcolor", "Timestamp color");
		defaults.setProperty("settings.general.groupspam", "Consolidate spammed chat");
		defaults.setProperty("settings.general.unreadflashing", "Default unread notification flashing");
		defaults.setProperty("settings.general.spellcheckenable", "Enable Spell-checking");
		
		// ENGLISH STRING FOR SETTINGS - 'SERVER CONFIG'
		defaults.setProperty("settings.server.name", "Server");
		defaults.setProperty("settings.server.autochannelsearch", "Auto-search for new channels");
		defaults.setProperty("settings.server.autopmsearch", "Auto-search for new PMs");
		defaults.setProperty("settings.server.delimiterchars", "Chat-channel delimiters");
		defaults.setProperty("settings.server.delimcolorbool", "Colored delimiters");
		defaults.setProperty("settings.server.delimformatbool", "Formatted delimiters");
		defaults.setProperty("settings.server.defaultchannels", "Default channels");
		defaults.setProperty("settings.server.ignoredchannels", "Ignored channels");
		
		// ENGLISH STRING FOR SETTINGS - 'CUSTOM FILTERS'
		defaults.setProperty("settings.filters.name", "Filters");
		defaults.setProperty("settings.filters.inversematch", "Inverse match");
		defaults.setProperty("settings.filters.casesensitive", "Case sensitive");
		defaults.setProperty("settings.filters.highlightbool", "Highlight matches");
		defaults.setProperty("settings.filters.highlightcolor", "Color");
		defaults.setProperty("settings.filters.highlightformat", "Format");
		defaults.setProperty("settings.filters.audionotificationbool", "Audio notification");
		defaults.setProperty("settings.filters.audionotificationsound", "Sound");
		defaults.setProperty("settings.filters.filtername", "Filter Name");
		defaults.setProperty("settings.filters.sendtotabbool", "Send matches to tab");
		defaults.setProperty("settings.filters.sendtotabname", "Tab Name");
		defaults.setProperty("settings.filters.sendtoalltabs", "All tabs");
		defaults.setProperty("settings.filters.removematches", "Hide matches from chat");
		defaults.setProperty("settings.filters.expressionstring", "Expression");
		
		// ENGLISH STRINGS FOR SETTINGS - 'ADVANCED SETTINGS'
		defaults.setProperty("settings.advanced.name", "Advanced");
		defaults.setProperty("settings.advanced.chatscrollhistory", "Chat history to retain (lines)");
		defaults.setProperty("settings.advanced.maxlengthchannelname", "Channel name max. length");
		defaults.setProperty("settings.advanced.multichatdelay", "Multi-chat send delay (ms)");
		defaults.setProperty("settings.advanced.chatboxunfocheight", "Unfocused Height");
		defaults.setProperty("settings.advanced.chatfadeticks", "Chat fade time (ticks)");
		defaults.setProperty("settings.advanced.forceunicode", "Force Unicode Chat Rendering");
		defaults.setProperty("settings.advanced.textignoreopacity", "Text ignores opacity setting");
		
		// ENGLISH STRINGS FOR SETTINGS - CHAT CHANNEL
		defaults.setProperty("settings.channel.notificationson", "Unread Notifications");
		defaults.setProperty("settings.channel.alias", "Alias");
		defaults.setProperty("settings.channel.cmdprefix", "Chat command prefix");
		defaults.setProperty("settings.channel.position", "Position:");
		defaults.setProperty("settings.channel.of", "of");
		defaults.setProperty("settings.channel.hideprefix", "Hide prefix while typing");
		
		// ENGLISH STRINGS FOR MESSAGES
		defaults.setProperty("messages.update1", "An update is available! (Current version is ");
		defaults.setProperty("messages.update2", ", newest is ");
		defaults.setProperty("messages.update3", "Visit the TabbyChat forum thread at minecraftforum.net to download");
	}
}
