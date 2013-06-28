package acs.tabbychat.lang;

import java.util.Properties;

public class TCLanguageFinnish extends TCLanguage {
	protected static String provides;
	protected final static Properties defaults = new Properties();
	static {
		provides = "fi_FI";
		defaults.clear();
		
		// Finnish, from CutePanda
		//  FINNISH STRINGS FOR DELIMITERS
		defaults.setProperty("delims.angles", "<Kulmat>");
		defaults.setProperty("delims.braces", "{Aaltosulkeet}");
		defaults.setProperty("delims.brackets", "[Sulkeet]");
		defaults.setProperty("delims.parenthesis", "(Kaarisulkeet)");
		defaults.setProperty("delims.anglesparenscombo", "<(Yhdistelmä)>");
		defaults.setProperty("delims.anglesbracketscombo", "<[Yhdistelmä]>");
		// 
		//  FINNISH STRINGS FOR COLORS
		defaults.setProperty("colors.default", "Tavallinen");
		defaults.setProperty("colors.darkblue", "Tummansininen");
		defaults.setProperty("colors.darkgreen", "Tummanvihreä");
		defaults.setProperty("colors.darkaqua", "Meriveden Tumma");
		defaults.setProperty("colors.darkred", "Tummanpunainen");
		defaults.setProperty("colors.purple", "Purppura");
		defaults.setProperty("colors.gold", "Kultainen");
		defaults.setProperty("colors.gray", "Harmaa");
		defaults.setProperty("colors.darkgray", "Tummanharmaa");
		defaults.setProperty("colors.indigo", "Syvän Sininen");
		defaults.setProperty("colors.brightgreen", "Vaaleanvihreä");
		defaults.setProperty("colors.aqua", "Meriveden Sininen");
		defaults.setProperty("colors.red", "Punainen");
		defaults.setProperty("colors.pink", "Vaaleanpunainen");
		defaults.setProperty("colors.yellow", "Keltainen");
		defaults.setProperty("colors.white", "Valkoinen");
		// 
		//  FINNISH STRINGS FOR FORMATS
		defaults.setProperty("formats.default", "Tavallinen");
		defaults.setProperty("formats.bold", "Lihava");
		defaults.setProperty("formats.striked", "Yliviivattu");
		defaults.setProperty("formats.underline", "Alleviivattu");
		defaults.setProperty("formats.italic", "Kursivoitu");
		// 
		//  FINNISH STRINGS FOR SOUNDS
		defaults.setProperty("sounds.orb", "Xp");
		defaults.setProperty("sounds.anvil", "Alasin");
		defaults.setProperty("sounds.bowhit", "Jousen Osuma");
		defaults.setProperty("sounds.break", "Rikkuminen");
		defaults.setProperty("sounds.click", "Napsahdus");
		defaults.setProperty("sounds.glass", "Lasi");
		defaults.setProperty("sounds.bass", "Basso");
		defaults.setProperty("sounds.harp", "Harppu");
		defaults.setProperty("sounds.pling", "Pling");
		defaults.setProperty("sounds.cat", "Kissa");
		defaults.setProperty("sounds.blast", "Räjähdys");
		defaults.setProperty("sounds.splash", "Roiske");
		defaults.setProperty("sounds.swim", "Uinti");
		defaults.setProperty("sounds.bat", "Lepakko");
		defaults.setProperty("sounds.blaze", "Roihu");
		defaults.setProperty("sounds.chicken", "Kana");
		defaults.setProperty("sounds.cow", "Lehmä");
		defaults.setProperty("sounds.dragon", "Lohikäärme");
		defaults.setProperty("sounds.endermen", "Ääreläinen");
		defaults.setProperty("sounds.ghast", "Hornanhenki");
		defaults.setProperty("sounds.pig", "Sika");
		defaults.setProperty("sounds.wolf", "Susi");
		// 
		//  FINNISH STRINGS FOR SETTINGS - COMMON
		defaults.setProperty("settings.save", "Tallenna");
		defaults.setProperty("settings.cancel", "Peruuta");
		defaults.setProperty("settings.new", "Uusi");
		defaults.setProperty("settings.delete", "Poista");
		// 
		//  FINNISH STRINGS FOR SETTINGS - 'GENERAL CONFIG'
		defaults.setProperty("settings.general.name", "Yleiset");
		defaults.setProperty("settings.general.tabbychatenable", "TabbyChat päällä");
		defaults.setProperty("settings.general.savechatlog", "Merkitse keskustelu tiedostoon");
		defaults.setProperty("settings.general.timestampenable", "Aikaleima juttelussa");
		defaults.setProperty("settings.general.timestampstyle", "Aikaleiman tyyli");
		defaults.setProperty("settings.general.timestampcolor", "Aikaleiman väri");
		defaults.setProperty("settings.general.groupspam", "Tallenna spämmätty keskustelu");
		defaults.setProperty("settings.general.unreadflashing", "Tavallinen lukemattoman viestin välkkyminen");
		// 
		//  FINNISH STRING FOR SETTINGS - 'SERVER CONFIG'
		defaults.setProperty("settings.server.name", "Palvelin");
		defaults.setProperty("settings.server.autochannelsearch", "Etsi automaattisesti uusia kanavia");
		defaults.setProperty("settings.server.delimiterchars", "Keskustelu kanavien eroittelu");
		defaults.setProperty("settings.server.delimcolorbool", "Värilliset eroittimet");
		defaults.setProperty("settings.server.delimformatbool", "Muotoillut eroittimet");
		defaults.setProperty("settings.server.defaultchannels", "Tavalliset kanavat");
		defaults.setProperty("settings.server.ignoredchannels", "Sivuutetut kanavat");
		// 
		//  FINNISH STRING FOR SETTINGS - 'CUSTOM FILTERS'
		defaults.setProperty("settings.filters.name", "Suodattimet");
		defaults.setProperty("settings.filters.inversematch", "Käänteinen osuma");
		defaults.setProperty("settings.filters.casesensitive", "Yhteensopiva");
		defaults.setProperty("settings.filters.highlightbool", "Korosta osumat");
		defaults.setProperty("settings.filters.highlightcolor", "Väri");
		defaults.setProperty("settings.filters.highlightformat", "Muotoilu");
		defaults.setProperty("settings.filters.audionotificationbool", "Ääni huomautus");
		defaults.setProperty("settings.filters.audionotificationsound", "Ääni");
		defaults.setProperty("settings.filters.filtername", "Suodattimen nimi");
		defaults.setProperty("settings.filters.sendtotabbool", "Lähetä osumat välilehteen");
		defaults.setProperty("settings.filters.sendtotabname", "Välilehden nimi");
		defaults.setProperty("settings.filters.sendtoalltabs", "Kaikki välilehdet");
		defaults.setProperty("settings.filters.removematches", "Piilota osumat keskustelusta");
		defaults.setProperty("settings.filters.expressionstring", "Ilmaisu");
		// 
		//  FINNISH STRINGS FOR SETTINGS - 'ADVANCED SETTINGS'
		defaults.setProperty("settings.advanced.name", "Edistynyt");
		defaults.setProperty("settings.advanced.chatscrollhistory", "Keskustelun säilytetty historia (rivit)");
		defaults.setProperty("settings.advanced.maxlengthchannelname", "Kanavan nimen maksimi pituus");
		defaults.setProperty("settings.advanced.multichatdelay", "Monen viestin välissä oleva aika (ms)");
		defaults.setProperty("settings.advanced.chatboxunfocheight", "Huomioimattomien korkeus (rivit)");
		defaults.setProperty("settings.advanced.chatfadeticks", "Keskustelun haalistumis aika (tickit)");
		defaults.setProperty("settings.advanced.forceunicode", "Pakota Unicode Keskustelun näyttäminen");
		// 
		//  FINNISH STRINGS FOR SETTINGS - CHAT CHANNEL
		defaults.setProperty("settings.channel.notificationson", "Lukemattomat muistiinpanot");
		defaults.setProperty("settings.channel.alias", "Toiselta nimeltään");
		defaults.setProperty("settings.channel.cmdprefix", "Keskustellun komennon etuliite");
		defaults.setProperty("settings.channel.position", "Sijainti:");
		defaults.setProperty("settings.channel.of", "");
		// 
		//  FINNISH STRINGS FOR MESSAGES
		defaults.setProperty("messages.update1", "Uusi päivitys on saatavissa! (Tämän hetkinen versio on");
		defaults.setProperty("messages.update2", ", uusin on");
		defaults.setProperty("messages.update3", "Käy katsomassa TabbyChat forumi minecraftforum.net:ssä lataaksesi sen");
	}
}
