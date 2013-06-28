package acs.tabbychat.lang;

import java.util.Properties;

public class TCLanguageGerman extends TCLanguage {
	protected static String provides;
	protected final static Properties defaults = new Properties();
	static {
		provides = "de_DE";
		defaults.clear();
		
		// Deutsch, from Schoelle/Gleydar11
		// GERMAN STRINGS FOR DELIMITERS
		defaults.setProperty("delims.angles", "<Spitze Klammern>");
		defaults.setProperty("delims.braces", "{Geschweifte Klammern}");
		defaults.setProperty("delims.brackets", "[Eckige Klammern]");
		defaults.setProperty("delims.parenthesis", "(Runde Klammern)");
		defaults.setProperty("delims.anglesparenscombo", "<(Kombination)Pl.>");
		defaults.setProperty("delims.anglesbracketscombo", "<[Kombination]Pl.>");
		//
		// GERMAN STRINGS FOR COLORS
		defaults.setProperty("colors.default", "Standard");
		defaults.setProperty("colors.darkblue", "Dunkelblau");
		defaults.setProperty("colors.darkgreen", "Dunkelgr\u00FCn");
		defaults.setProperty("colors.darkaqua", "Dunkelaquamarin");
		defaults.setProperty("colors.darkred", "Dunkelrot");
		defaults.setProperty("colors.purple", "Violett");
		defaults.setProperty("colors.gold", "Gold");
		defaults.setProperty("colors.gray", "Grau");
		defaults.setProperty("colors.darkgray", "Dunkelgrau");
		defaults.setProperty("colors.indigo", "Indigo");
		defaults.setProperty("colors.brightgreen", "Hellgr\u00FCn");
		defaults.setProperty("colors.aqua", "Aquamarin");
		defaults.setProperty("colors.red", "Rot");
		defaults.setProperty("colors.pink", "Rosa");
		defaults.setProperty("colors.yellow", "Gelb");
		defaults.setProperty("colors.white", "Wei\u00DF");
		//
		// GERMAN STRINGS FOR FORMATS
		defaults.setProperty("formats.default", "Standard");
		defaults.setProperty("formats.bold", "Fett");
		defaults.setProperty("formats.striked", "Durchgestrichen");
		defaults.setProperty("formats.underline", "Unterstrichen");
		defaults.setProperty("formats.italic", "Kursiv");
		//
		// GERMAN STRINGS FOR SOUNDS
		defaults.setProperty("sounds.orb", "EP-Kugel");
		defaults.setProperty("sounds.anvil", "Amboss");
		defaults.setProperty("sounds.bowhit", "Bogen-Treffer");
		defaults.setProperty("sounds.break", "Zerbrechen");
		defaults.setProperty("sounds.click", "Klick");
		defaults.setProperty("sounds.glass", "Glas");
		defaults.setProperty("sounds.bass", "Bass");
		defaults.setProperty("sounds.harp", "Harfe");
		defaults.setProperty("sounds.pling", "Pling");
		defaults.setProperty("sounds.cat", "Katze");
		defaults.setProperty("sounds.blast", "Explosion");
		defaults.setProperty("sounds.splash", "Spritzer");
		defaults.setProperty("sounds.swim", "Schwimmen");
		defaults.setProperty("sounds.bat", "Fledermaus");
		defaults.setProperty("sounds.blaze", "Blaze");
		defaults.setProperty("sounds.chicken", "H\u00FChnchen");
		defaults.setProperty("sounds.cow", "Kuh");
		defaults.setProperty("sounds.dragon", "Drache");
		defaults.setProperty("sounds.endermen", "Enderman");
		defaults.setProperty("sounds.ghast", "Ghast");
		defaults.setProperty("sounds.pig", "Schwein");
		defaults.setProperty("sounds.wolf", "Wolf");
		//
		// GERMAN STRINGS FOR SETTINGS - COMMON
		defaults.setProperty("settings.save", "Speichern");
		defaults.setProperty("settings.cancel", "Abbrechen");
		defaults.setProperty("settings.new", "Neu");
		defaults.setProperty("settings.delete", "L\u00F6schen");
		//
		// GERMAN STRINGS FOR SETTINGS - 'GENERAL CONFIG'
		defaults.setProperty("settings.general.name", "Allgemeine");
		defaults.setProperty("settings.general.tabbychatenable", "TabbyChat aktiv");
		defaults.setProperty("settings.general.savechatlog", "Chat in Logdatei speichern");
		defaults.setProperty("settings.general.timestampenable", "Zeitstempel aktivieren");
		defaults.setProperty("settings.general.timestampstyle", "Zeitstempel - Stil");
		defaults.setProperty("settings.general.timestampcolor", "Zeitstempel - Farbe");
		defaults.setProperty("settings.general.groupspam", "Spam zusammenfassen");
		defaults.setProperty("settings.general.unreadflashing", "Standardhinweis bei ungelesenen Benachrichtigungen");
		//
		// GERMAN STRING FOR SETTINGS - 'SERVER CONFIG'
		defaults.setProperty("settings.server.name", "Server");
		defaults.setProperty("settings.server.autochannelsearch", "Automatische Suche nach neuen Channels");
		defaults.setProperty("settings.server.delimiterchars", "Channel Separator");
		defaults.setProperty("settings.server.delimcolorbool", "Separator - Farbe");
		defaults.setProperty("settings.server.delimformatbool", "Separator - Format");
		defaults.setProperty("settings.server.defaultchannels", "Standard Channel");
		defaults.setProperty("settings.server.ignoredchannels", "Ignorierte Channel");
		//
		// GERMAN STRING FOR SETTINGS - 'CUSTOM FILTERS'
		defaults.setProperty("settings.filters.name", "Filter");
		defaults.setProperty("settings.filters.inversematch", "Invertierter Treffer");
		defaults.setProperty("settings.filters.casesensitive", "Gro\u00DF-/Kleinschreibung");
		defaults.setProperty("settings.filters.highlightbool", "Treffer hervorheben");
		defaults.setProperty("settings.filters.highlightcolor", "Farbe");
		defaults.setProperty("settings.filters.highlightformat", "Formatierung");
		defaults.setProperty("settings.filters.audionotificationbool", "Audio Benachrichtigung");
		defaults.setProperty("settings.filters.audionotificationsound", "Ton");
		defaults.setProperty("settings.filters.filtername", "Filtername");
		defaults.setProperty("settings.filters.sendtotabbool", "Sende Treffer an Tabs");
		defaults.setProperty("settings.filters.sendtotabname", "Tab Name");
		defaults.setProperty("settings.filters.sendtoalltabs", "Alle Tabs");
		defaults.setProperty("settings.filters.removematches", "Treffer verbergen");
		defaults.setProperty("settings.filters.expressionstring", "Ausdruck");
		//
		// GERMAN STRINGS FOR SETTINGS - 'ADVANCED SETTINGS'");
		defaults.setProperty("settings.advanced.name", "Erweiterte");
		defaults.setProperty("settings.advanced.chatscrollhistory", "Chat Historie speichern (Zeilen)");
		defaults.setProperty("settings.advanced.maxlengthchannelname", "Maximale L\u00E4nge Channelname");
		defaults.setProperty("settings.advanced.multichatdelay", "Multi-Chat Sendeverz\u00F6gerung (ms)");
		defaults.setProperty("settings.advanced.chatboxunfocheight", "H\u00F6he ohne Fokus");
		defaults.setProperty("settings.advanced.chatfadeticks", "Anzeigezeit einer Nachricht (in Ticks)");
		defaults.setProperty("settings.advanced.forceunicode", "Erzwinge Unicode Chat Anzeige");
		//
		// GERMAN STRINGS FOR SETTINGS - CHAT CHANNEL
		defaults.setProperty("settings.channel.notificationson", "Unread Notificati");
		defaults.setProperty("settings.channel.alias", "Alias");
		defaults.setProperty("settings.channel.cmdprefix", "Chat-Commando Prefix");
		defaults.setProperty("settings.channel.position", "Position");
		defaults.setProperty("settings.channel.of", "von");
		//
		// GERMAN STRINGS FOR MESSAGES
		defaults.setProperty("messages.update1", "Ein Update ist verf\u00FCgbar (Momentane Version ist");
		defaults.setProperty("messages.update2", ", neueste ist");
		defaults.setProperty("messages.update3", "Besuche den TabbyChat Forum Thread unter minecraftforum.net zum Herunterladen des Updates");
	}
}
