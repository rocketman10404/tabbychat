package acs.tabbychat.lang;

import java.util.Properties;

public class TCLanguageSwedish extends TCLanguage {
	protected static String provides;
	protected final static Properties defaults = new Properties();
	static {
		provides = "sv_SE";
		defaults.clear();
		
		// Swedish, from Fluffy_lol
		// SWEDISH STRINGS FOR DELIMITERS
		defaults.setProperty("delims.angles", "<Vinklar>");
		defaults.setProperty("delims.braces", "{Klammrar}");
		defaults.setProperty("delims.brackets", "[Konsoler]");
		defaults.setProperty("delims.parenthesis", "(Parenteser)");
		defaults.setProperty("delims.anglesparenscombo", "<(Kombo)Pl.>");
		defaults.setProperty("delims.anglesbracketscombo", "<[Kombo]Pl.>");
		//
		// SWEDISH STRINGS FOR COLORS
		defaults.setProperty("colors.default", "Standard");
		defaults.setProperty("colors.darkblue", "Mörkblå");
		defaults.setProperty("colors.darkgreen", "Mörkgrön");
		defaults.setProperty("colors.darkaqua", "Mörk Aqua");
		defaults.setProperty("colors.darkred", "Mörkröd");
		defaults.setProperty("colors.purple", "Lila");
		defaults.setProperty("colors.gold", "Guld");
		defaults.setProperty("colors.gray", "Grå");
		defaults.setProperty("colors.darkgray", "Mörkgrå");
		defaults.setProperty("colors.indigo", "Indigo");
		defaults.setProperty("colors.brightgreen", "Ljusgrön");
		defaults.setProperty("colors.aqua", "Aqua");
		defaults.setProperty("colors.red", "Röd");
		defaults.setProperty("colors.pink", "Rosa");
		defaults.setProperty("colors.yellow", "Gul");
		defaults.setProperty("colors.white", "Vit");
		//
		// SWEDISH STRINGS FOR FORMATS
		defaults.setProperty("formats.default", "Standard");
		defaults.setProperty("formats.bold", "Fetstil");
		defaults.setProperty("formats.striked", "Genomstrykt");
		defaults.setProperty("formats.underline", "Understruket");
		defaults.setProperty("formats.italic", "Kursiv");
		//
		// SWEDISH STRINGS FOR SOUNDS
		// you should really just load this from the language files
		defaults.setProperty("sounds.orb", "Erfarenhetsklot");
		defaults.setProperty("sounds.anvil", "Städ");
		defaults.setProperty("sounds.bowhit", "Pilbågsträff");
		defaults.setProperty("sounds.break", "Bryning");
		defaults.setProperty("sounds.click", "Klick");
		defaults.setProperty("sounds.glass", "Glas");
		defaults.setProperty("sounds.bass", "Bas");
		defaults.setProperty("sounds.harp", "Harpa");
		defaults.setProperty("sounds.pling", "Pling");
		defaults.setProperty("sounds.cat", "Katt");
		defaults.setProperty("sounds.blast", "Explosion");
		defaults.setProperty("sounds.splash", "Skvätt");
		defaults.setProperty("sounds.swim", "Simma");
		defaults.setProperty("sounds.bat", "Fladdermus");
		defaults.setProperty("sounds.blaze", "Brännare");
		defaults.setProperty("sounds.chicken", "Kyckling");
		defaults.setProperty("sounds.cow", "Ko");
		defaults.setProperty("sounds.dragon", "Drake");
		defaults.setProperty("sounds.endermen", "Endermen");
		defaults.setProperty("sounds.ghast", "Ghast");
		defaults.setProperty("sounds.pig", "Gris");
		defaults.setProperty("sounds.wolf", "Varg");
		//
		// SWEDISH STRINGS FOR SETTINGS - COMMON
		defaults.setProperty("settings.save", "Spara");
		defaults.setProperty("settings.cancel", "Avbryt");
		defaults.setProperty("settings.new", "Ny");
		defaults.setProperty("settings.delete", "Ta bort");
		//
		// SWEDISH STRINGS FOR SETTINGS - 'GENERAL CONFIG'
		defaults.setProperty("settings.general.name", "Allmänt");
		defaults.setProperty("settings.general.tabbychatenable", "TabbyChat Aktiverad");
		defaults.setProperty("settings.general.savechatlog", "Logga chatt till fil");
		defaults.setProperty("settings.general.timestampenable", "Tidsstämpla chatt");
		defaults.setProperty("settings.general.timestampstyle", "Tidsstämpelstil");
		defaults.setProperty("settings.general.timestampcolor", "Tidsstämpelfärg");
		defaults.setProperty("settings.general.groupspam", "Konsolidera spammad chat");
		defaults.setProperty("settings.general.unreadflashing", "Blinkade olästa meddelanden som standard");
		//
		// SWEDISH STRING FOR SETTINGS - 'SERVER CONFIG'
		defaults.setProperty("settings.server.name", "Server");
		defaults.setProperty("settings.server.autochannelsearch", "Sök automatiskt efter nya kanaler");
		defaults.setProperty("settings.server.autopmsearch", "Sök automatiskt efter nya PMs");
		defaults.setProperty("settings.server.delimiterchars", "Chatt-kanals avgränsare");
		defaults.setProperty("settings.server.delimcolorbool", "Färgare avgränsare");
		defaults.setProperty("settings.server.delimformatbool", "Formaterade avgränsare");
		defaults.setProperty("settings.server.defaultchannels", "Standard kanaler");
		defaults.setProperty("settings.server.ignoredchannels", "Ignorerade kanaler");
		//
		// SWEDISH STRING FOR SETTINGS - 'CUSTOM FILTERS'
		defaults.setProperty("settings.filters.name", "Filter");
		defaults.setProperty("settings.filters.inversematch", "Invers match");
		defaults.setProperty("settings.filters.casesensitive", "Skiftlägeskänsligt");
		defaults.setProperty("settings.filters.highlightbool", "Markera matcher");
		defaults.setProperty("settings.filters.highlightcolor", "Färg");
		defaults.setProperty("settings.filters.highlightformat", "Format");
		defaults.setProperty("settings.filters.audionotificationbool", "Ljudnotifiering");
		defaults.setProperty("settings.filters.audionotificationsound", "Ljud");
		defaults.setProperty("settings.filters.filtername", "Filter Namn");
		defaults.setProperty("settings.filters.sendtotabbool", "Skicka matcher till flik");
		defaults.setProperty("settings.filters.sendtotabname", "Fliknamn");
		defaults.setProperty("settings.filters.sendtoalltabs", "Alla flikar");
		defaults.setProperty("settings.filters.removematches", "Göm matcher från chatten");
		defaults.setProperty("settings.filters.expressionstring", "Uttryck");
		//
		// SWEDISH STRINGS FOR SETTINGS - 'ADVANCED SETTINGS'
		defaults.setProperty("settings.advanced.name", "Avancerat");
		defaults.setProperty("settings.advanced.chatscrollhistory", "Chatt historik att spara (rader)");
		defaults.setProperty("settings.advanced.maxlengthchannelname", "Kanalnamn max. längd");
		defaults.setProperty("settings.advanced.multichatdelay", "Multi-chatt skickningsfördröjning (ms)");
		defaults.setProperty("settings.advanced.chatboxunfocheight", "Ofokuserad höjd");
		defaults.setProperty("settings.advanced.chatfadeticks", "Chatt borttonings tid (ticks)");
		defaults.setProperty("settings.advanced.forceunicode", "Tvinga Unicode Chatt Rendering");
		//
		// SWEDISH STRINGS FOR SETTINGS - CHAT CHANNEL
		defaults.setProperty("settings.channel.notificationson", "Olästa meddelanden");
		defaults.setProperty("settings.channel.alias", "Alias");
		defaults.setProperty("settings.channel.cmdprefix", "Chatt kommando prefix");
		defaults.setProperty("settings.channel.position", "Position:");
		defaults.setProperty("settings.channel.of", "av");
		defaults.setProperty("settings.channel.hideprefix", "Göm prefix medans du skriver");
		//
		// SWEDISH STRINGS FOR MESSAGES
		defaults.setProperty("messages.update1", "En uppdatering är tillgänglig! (Nuvarande version är");
		defaults.setProperty("messages.update2", ", nyaste är");
		defaults.setProperty("messages.update3", "Besök TabbyChat forum tråden på minecraftforum.net för att ladda ner");
	}

}
