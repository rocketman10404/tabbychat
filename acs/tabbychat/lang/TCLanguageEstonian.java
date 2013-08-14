package acs.tabbychat.lang;

import java.util.Properties;

public class TCLanguageEstonian extends TCLanguage {
	protected static String provides;
	protected final static Properties defaults = new Properties();
	static {
		provides = "et_EE";
		defaults.clear();
		
		// Estonian, from robotkoer
		// ESTONIAN STRINGS FOR DELIMITERS
		defaults.setProperty("delims.angles", "<Nurksulud>");
		defaults.setProperty("delims.braces", "{Looksulud}");
		defaults.setProperty("delims.brackets", "[Kantsulud]");
		defaults.setProperty("delims.parenthesis", "(\u00DCmarsulud)");
		defaults.setProperty("delims.anglesparenscombo", "<(Kombinatsioon)M\u00E4.>");
		defaults.setProperty("delims.anglesbracketscombo", "<[Kombinatsioon]M\u00E4.>");
		//
		// ESTONIAN STRINGS FOR COLORS
		defaults.setProperty("colors.default", "Tavaline");
		defaults.setProperty("colors.darkblue", "Tumesinine");
		defaults.setProperty("colors.darkgreen", "Tumeroheline");
		defaults.setProperty("colors.darkaqua", "Tume meresinine");
		defaults.setProperty("colors.darkred", "Tumepunane");
		defaults.setProperty("colors.purple", "Lilla");
		defaults.setProperty("colors.gold", "Kuldne");
		defaults.setProperty("colors.gray", "Hall");
		defaults.setProperty("colors.darkgray", "Tumehall");
		defaults.setProperty("colors.indigo", "Indigosinine");
		defaults.setProperty("colors.brightgreen", "Erkroheline");
		defaults.setProperty("colors.aqua", "Meresinine");
		defaults.setProperty("colors.red", "Punane");
		defaults.setProperty("colors.pink", "Roosa");
		defaults.setProperty("colors.yellow", "Kollane");
		defaults.setProperty("colors.white", "Valge");
		//
		// ESTONIAN STRINGS FOR FORMATS
		defaults.setProperty("formats.default", "Tavaline");
		defaults.setProperty("formats.bold", "Paks");
		defaults.setProperty("formats.striked", "L\u00E4bikriipsutus");
		defaults.setProperty("formats.underline", "Allakriipsutus");
		defaults.setProperty("formats.italic", "Kursiiv");
		//
		// ESTONIAN STRINGS FOR SOUNDS
		defaults.setProperty("sounds.orb", "XP ring");
		defaults.setProperty("sounds.anvil", "Alasi");
		defaults.setProperty("sounds.bowhit", "Vibu tabamus");
		defaults.setProperty("sounds.break", "Purunemine");
		defaults.setProperty("sounds.click", "Kl\u00F5ps");
		defaults.setProperty("sounds.glass", "Klaas");
		defaults.setProperty("sounds.bass", "Bass");
		defaults.setProperty("sounds.harp", "Harf");
		defaults.setProperty("sounds.pling", "Pl\u00F5nn");
		defaults.setProperty("sounds.cat", "Kass");
		defaults.setProperty("sounds.blast", "Plahvatus");
		defaults.setProperty("sounds.splash", "Sulpsatus");
		defaults.setProperty("sounds.swim", "Ujumine");
		defaults.setProperty("sounds.bat", "Nahkhiir");
		defaults.setProperty("sounds.blaze", "Leek");
		defaults.setProperty("sounds.chicken", "Kana");
		defaults.setProperty("sounds.cow", "Lehm");
		defaults.setProperty("sounds.dragon", "L\u00F5pudraakon");
		defaults.setProperty("sounds.endermen", "L\u00F5pumees");
		defaults.setProperty("sounds.ghast", "Kammitus");
		defaults.setProperty("sounds.pig", "Siga");
		defaults.setProperty("sounds.wolf", "Hunt");
		//
		// ESTONIAN STRINGS FOR SETTINGS - COMMON
		defaults.setProperty("settings.save", "Salvesta");
		defaults.setProperty("settings.cancel", "Loobu");
		defaults.setProperty("settings.new", "Uus");
		defaults.setProperty("settings.delete", "Kustuta");
		//
		// ESTONIAN STRINGS FOR SETTINGS - 'GENERAL CONFIG'
		defaults.setProperty("settings.general.name", "P\u00F5hilised s\u00E4tted");
		defaults.setProperty("settings.general.tabbychatenable", "TabbyChat lubatud");
		defaults.setProperty("settings.general.savechatlog", "Logi vestlus faili");
		defaults.setProperty("settings.general.timestampenable", "Ajatembelda vestlus");
		defaults.setProperty("settings.general.timestampstyle", "Ajatempli vorming");
		defaults.setProperty("settings.general.timestampcolor", "Ajatempli v\u00E4rv");
		defaults.setProperty("settings.general.groupspam", "\u00DChenda korratud s\u00F5numid");
		defaults.setProperty("settings.general.unreadflashing", "Lugemata teadete korral vilguta");
		//
		// ESTONIAN STRING FOR SETTINGS - 'SERVER CONFIG'
		defaults.setProperty("settings.server.name", "Serveri s\u00E4tted");
		defaults.setProperty("settings.server.autochannelsearch", "Otsi automaatselt uusi kanaleid");
		defaults.setProperty("settings.server.autopmsearch", "Otsi automaatselt uusi privaatsõnumeid");
		defaults.setProperty("settings.server.delimiterchars", "Vestluskanalite eraldajad");
		defaults.setProperty("settings.server.delimcolorbool", "Eraldajate v\u00E4rv");
		defaults.setProperty("settings.server.delimformatbool", "Eraldajate vorming");
		defaults.setProperty("settings.server.defaultchannels", "Vaikimisi kanalid");
		defaults.setProperty("settings.server.ignoredchannels", "Ignoreeritud kanalid");
		//
		// ESTONIAN STRING FOR SETTINGS - 'CUSTOM FILTERS'
		defaults.setProperty("settings.filters.name", "Kohandatud filtrid");
		defaults.setProperty("settings.filters.inversematch", "Vastandtulemused");
		defaults.setProperty("settings.filters.casesensitive", "T\u00F5stutundlik");
		defaults.setProperty("settings.filters.highlightbool", "T\u00F5sta tulemused esile");
		defaults.setProperty("settings.filters.highlightcolor", "V\u00E4rv");
		defaults.setProperty("settings.filters.highlightformat", "Vorming");
		defaults.setProperty("settings.filters.audionotificationbool", "Heliteade");
		defaults.setProperty("settings.filters.audionotificationsound", "Heli");
		defaults.setProperty("settings.filters.filtername", "Filtri nimi");
		defaults.setProperty("settings.filters.sendtotabbool", "Saada tulemused vahekaardile");
		defaults.setProperty("settings.filters.sendtotabname", "Vahekaardi nimi");
		defaults.setProperty("settings.filters.sendtoalltabs", "K\u00F5ik vahekaardid");
		defaults.setProperty("settings.filters.removematches", "Peida tulemused vestlusest");
		defaults.setProperty("settings.filters.expressionstring", "Regular Expression");
		//
		// ESTONIAN STRINGS FOR SETTINGS - 'ADVANCED SETTINGS'
		defaults.setProperty("settings.advanced.name", "T\u00E4psemad s\u00E4tted");
		defaults.setProperty("settings.advanced.chatscrollhistory", "S\u00E4ilitatav vestluse ajalugu (rida)");
		defaults.setProperty("settings.advanced.maxlengthchannelname", "Kanali nime maksimaalne pikkus (m\u00E4rki)");
		defaults.setProperty("settings.advanced.multichatdelay", "Multis\u00F5numi saatmise viivitus (ms)");
		defaults.setProperty("settings.advanced.chatboxunfocheight", "Fokuseerimata vestluse k\u00F5rgus");
		defaults.setProperty("settings.advanced.chatfadeticks", "Vestluse hajumisaeg (tick'i)");
		defaults.setProperty("settings.advanced.forceunicode", "Sunni teksti kuvamine Unicode vormingus");
		//
		// ESTONIAN STRINGS FOR SETTINGS - CHAT CHANNEL
		defaults.setProperty("settings.channel.notificationson", "Lugemata s\u00F5numite korral teata");
		defaults.setProperty("settings.channel.alias", "H\u00FC\u00FCdnimi");
		defaults.setProperty("settings.channel.cmdprefix", "Vestluse k\u00E4su eesliide");
		defaults.setProperty("settings.channel.position", "Asukoht:");
		defaults.setProperty("settings.channel.of", "/");
		defaults.setProperty("settings.channel.hideprefix", "Peida eesliide kirjutamise ajal");
		//
		// ESTONIAN STRINGS FOR MESSAGES
		defaults.setProperty("messages.update1", "Uuendus on saadaval! (Praegune versioon on ");
		defaults.setProperty("messages.update2", ", uusim on ");
		defaults.setProperty("messages.update3", "K\u00FClasta TabbyChat'i foorumi teemat aadressil minecraftforum.net, et alla laadida.");
	}
}
