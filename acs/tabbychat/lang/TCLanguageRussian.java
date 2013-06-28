package acs.tabbychat.lang;

import java.util.Properties;

public class TCLanguageRussian extends TCLanguage {
	protected static String provides;
	protected final static Properties defaults = new Properties();
	static {
		provides = "ru_RU";
		defaults.clear();
		
		// Russian, from Stalkerdeni971
		// RUSSIAN STRINGS FOR DELIMITERS
		defaults.setProperty("delims.angles", "<\u0423\u0433\u043B\u044B>");
		defaults.setProperty("delims.braces", "{\u0424\u0438\u0433\u0443\u0440\u043D\u044B\u0435}");
		defaults.setProperty("delims.brackets", "[\u041A\u0432\u0430\u0434\u0440\u0430\u0442\u043D\u044B\u0435]");
		defaults.setProperty("delims.parenthesis", "(\u041A\u0440\u0443\u0433\u043B\u044B\u0435)");
		defaults.setProperty("delims.anglesparenscombo", "<(\u041A\u043E\u043C\u0431\u043E)Pl.>");
		defaults.setProperty("delims.anglesbracketscombo", "<[\u041A\u043E\u043C\u0431\u043E]Pl.>");
		//
		// RUSSIAN STRINGS FOR COLORS
		defaults.setProperty("colors.default", "\u041E\u0431\u044B\u0447\u043D\u044B\u0439");
		defaults.setProperty("colors.darkblue", "\u0422\u0435\u043C\u043D\u043E-\u0441\u0438\u043D\u0438\u0439");
		defaults.setProperty("colors.darkgreen", "\u0422\u0435\u043C\u043D\u043E-\u0437\u0435\u043B\u0435\u043D\u044B\u0439");
		defaults.setProperty("colors.darkaqua", "\u0422\u0435\u043C\u043D\u043E-\u0433\u043E\u043B\u0443\u0431\u043E\u0439");
		defaults.setProperty("colors.darkred", "\u0422\u0435\u043C\u043D\u043E-\u043A\u0440\u0430\u0441\u043D\u044B\u0439");
		defaults.setProperty("colors.purple", "\u0424\u0438\u043E\u043B\u0435\u0442\u043E\u0432\u044B\u0439");
		defaults.setProperty("colors.gold", "\u0417\u043E\u043B\u043E\u0442\u043E\u0439");
		defaults.setProperty("colors.gray", "\u0421\u0435\u0440\u044B\u0439");
		defaults.setProperty("colors.darkgray", "\u0422\u0435\u043C\u043D\u043E-\u0441\u0435\u0440\u044B\u0439");
		defaults.setProperty("colors.indigo", "\u0418\u043D\u0434\u0438\u0433\u043E");
		defaults.setProperty("colors.brightgreen", "\u041B\u0430\u0439\u043C\u043E\u0432\u044B\u0439");
		defaults.setProperty("colors.aqua", "\u0413\u043E\u043B\u0443\u0431\u043E\u0439");
		defaults.setProperty("colors.red", "\u041A\u0440\u0430\u0441\u043D\u044B\u0439");
		defaults.setProperty("colors.pink", "\u0420\u043E\u0437\u043E\u0432\u044B\u0439");
		defaults.setProperty("colors.yellow", "\u0416\u0435\u043B\u0442\u044B\u0439");
		defaults.setProperty("colors.white", "\u0411\u0435\u043B\u044B\u0439");
		//
		// RUSSIAN STRINGS FOR FORMATS
		defaults.setProperty("formats.default", "\u041E\u0431\u044B\u0447\u043D\u044B\u0439");
		defaults.setProperty("formats.bold", "\u0416\u0438\u0440\u043D\u044B\u0439");
		defaults.setProperty("formats.striked", "\u0417\u0430\u0447\u0435\u043A\u0440\u043D\u0443\u0442\u044B\u0439");
		defaults.setProperty("formats.underline", "\u041F\u043E\u0434\u0447\u0435\u0440\u043A\u043D\u0443\u0442\u044B\u0439");
		defaults.setProperty("formats.italic", "\u041A\u0443\u0440\u0441\u0438\u0432");
		//
		// RUSSIAN STRINGS FOR SOUNDS
		defaults.setProperty("sounds.orb", "\u041E\u043F\u044B\u0442");
		defaults.setProperty("sounds.anvil", "\u041D\u0430\u043A\u043E\u0432\u0430\u043B\u044C\u043D\u044F");
		defaults.setProperty("sounds.bowhit", "\u0421\u0442\u0440\u0435\u043B\u0430");
		defaults.setProperty("sounds.break", "\u0421\u043B\u043E\u043C\u0430\u043B");
		defaults.setProperty("sounds.click", "\u041A\u043B\u0438\u043A");
		defaults.setProperty("sounds.glass", "\u0421\u0442\u0435\u043A\u043B\u043E");
		defaults.setProperty("sounds.bass", "\u0411\u0430\u0441\u0441");
		defaults.setProperty("sounds.harp", "\u0410\u0440\u0444\u0430");
		defaults.setProperty("sounds.pling", "Pling");
		defaults.setProperty("sounds.cat", "\u041A\u043E\u0442");
		defaults.setProperty("sounds.blast", "\u0412\u0437\u0440\u044B\u0432");
		defaults.setProperty("sounds.splash", "\u0412\u0441\u043F\u043B\u0435\u0441\u043A");
		defaults.setProperty("sounds.swim", "\u0421\u043F\u0440\u0443\u0442");
		defaults.setProperty("sounds.bat", "\u041B\u0435\u0442\u0443\u0447\u0430\u044F \u043C\u044B\u0448\u044C");
		defaults.setProperty("sounds.blaze", "\u0418\u0444\u0440\u0438\u0442");
		defaults.setProperty("sounds.chicken", "\u041A\u0443\u0440\u0438\u0446\u0430");
		defaults.setProperty("sounds.cow", "\u041A\u043E\u0440\u043E\u0432\u0430");
		defaults.setProperty("sounds.dragon", "\u0414\u0440\u0430\u043A\u043E\u043D");
		defaults.setProperty("sounds.endermen", "\u042D\u043D\u0434\u0435\u0440\u043C\u0430\u043D");
		defaults.setProperty("sounds.ghast", "\u0413\u0430\u0441\u0442");
		defaults.setProperty("sounds.pig", "\u0421\u0432\u0438\u043D\u044C\u044F");
		defaults.setProperty("sounds.wolf", "\u0412\u043E\u043B\u043A");
		//
		// RUSSIAN STRINGS FOR SETTINGS - COMMON
		defaults.setProperty("settings.save", "\u0421\u043E\u0445\u0440.");
		defaults.setProperty("settings.cancel", "\u041E\u0442\u043C\u0435\u043D\u0430");
		defaults.setProperty("settings.new", "\u041D\u043E\u0432\u044B\u0439");
		defaults.setProperty("settings.delete", "\u0423\u0434\u0430\u043B\u0438\u0442\u044C");
		//
		// RUSSIAN STRINGS FOR SETTINGS - 'GENERAL'
		defaults.setProperty("settings.general.name", "\u0413\u043B\u0430\u0432\u043D\u043E\u0435");
		defaults.setProperty("settings.general.tabbychatenable", "TabbyChat \u0432\u043A\u043B\u044E\u0447\u0435\u043D");
		defaults.setProperty("settings.general.savechatlog", "\u041B\u043E\u0433\u0438\u0440\u043E\u0432\u0430\u0442\u044C \u0447\u0430\u0442");
		defaults.setProperty("settings.general.timestampenable", "\u041F\u043E\u043C\u0435\u0442\u043A\u0430 \u0432\u0440\u0435\u043C\u0435\u043D\u0438");
		defaults.setProperty("settings.general.timestampstyle", "\u0421\u0442\u0438\u043B\u044C");
		defaults.setProperty("settings.general.timestampcolor", "\u0426\u0432\u0435\u0442");
		defaults.setProperty("settings.general.groupspam", "\u0413\u0440\u0443\u043F\u043F\u0438\u0440\u043E\u0432\u0430\u0442\u044C \u043E\u0434\u0438\u043D\u0430\u043A\u043E\u0432\u044B\u0435 \u0441\u043E\u043E\u0431\u0449\u0435\u043D\u0438\u044F");
		defaults.setProperty("settings.general.unreadflashing", "\u0423\u0432\u0435\u0434\u043E\u043C\u043B\u0435\u043D\u0438\u044F \u043F\u043E-\u0443\u043C\u043E\u043B\u0447\u0430\u043D\u0438\u044E");
		//
		// RUSSIAN STRINGS FOR SETTINGS - 'SERVER'
		defaults.setProperty("settings.server.name", "\u0421\u0435\u0440\u0432\u0435\u0440");
		defaults.setProperty("settings.server.autochannelsearch", "\u0410\u0432\u0442\u043E-\u043F\u043E\u0438\u0441\u043A \u043D\u043E\u0432\u044B\u0445 \u043A\u0430\u043D\u0430\u043B\u043E\u0432");
		defaults.setProperty("settings.server.delimiterchars", "\u0420\u0430\u0437\u0434\u0435\u043B\u0438\u0442\u0435\u043B\u0438 \u043A\u0430\u043D\u0430\u043B\u043E\u0432");
		defaults.setProperty("settings.server.delimcolorbool", "\u0426\u0432\u0435\u0442");
		defaults.setProperty("settings.server.delimformatbool", "\u0424\u043E\u0440\u043C\u0430\u0442");
		defaults.setProperty("settings.server.defaultchannels", "\u041A\u0430\u043D\u0430\u043B\u044B \u043F\u043E-\u0443\u043C\u043E\u043B\u0447\u0430\u043D\u0438\u044E");
		defaults.setProperty("settings.server.ignoredchannels", "\u0418\u0433\u043D\u043E\u0440\u0438\u0440\u0443\u0435\u043C\u044B\u0435 \u043A\u0430\u043D\u0430\u043B\u044B");
		//
		// RUSSIAN STRINGS FOR SETTINGS - 'FILTERS'
		defaults.setProperty("settings.filters.name", "\u0424\u0438\u043B\u044C\u0442\u0440\u044B");
		defaults.setProperty("settings.filters.inversematch", "\u0418\u043D\u0432\u0435\u043D\u0442\u0438\u0440\u043E\u0432\u0430\u0442\u044C");
		defaults.setProperty("settings.filters.casesensitive", "\u0423\u0447\u0438\u0442\u044B\u0432\u0430\u0442\u044C \u0440\u0435\u0433\u0438\u0441\u0442\u0440");
		defaults.setProperty("settings.filters.highlightbool", "\u041F\u043E\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0442\u044C \u0441\u043E\u0432\u043F\u0430\u0434\u0435\u043D\u0438\u044F");
		defaults.setProperty("settings.filters.highlightcolor", "\u0426\u0432\u0435\u0442");
		defaults.setProperty("settings.filters.highlightformat", "\u0424\u043E\u0440\u043C\u0430\u0442");
		defaults.setProperty("settings.filters.audionotificationbool", "\u0410\u0443\u0434\u0438\u043E \u0443\u0432\u0435\u0434\u043E\u043C\u043B\u0435\u043D\u0438\u044F");
		defaults.setProperty("settings.filters.audionotificationsound", "\u0417\u0432\u0443\u043A");
		defaults.setProperty("settings.filters.filtername", "\u0418\u043C\u044F \u0444\u0438\u043B\u044C\u0442\u0440\u0430");
		defaults.setProperty("settings.filters.sendtotabbool", "\u041F\u043E\u043A\u0430\u0437\u044B\u0432\u0430\u0442\u044C \u0432\u043E \u0432\u043A\u043B\u0430\u0434\u043A\u0430\u0445");
		defaults.setProperty("settings.filters.sendtotabname", "\u041E\u0442\u0434\u0435\u043B\u044C\u043D\u0430\u044F");
		defaults.setProperty("settings.filters.sendtoalltabs", "\u0412\u043E \u0432\u0441\u0435\u0445");
		defaults.setProperty("settings.filters.removematches", "\u0421\u043A\u0440\u044B\u0432\u0430\u0442\u044C \u0441\u043E\u0432\u043F\u0430\u0434\u0435\u043D\u0438\u044F");
		defaults.setProperty("settings.filters.expressionstring", "\u0421\u043B\u043E\u0432\u0430 \u0434\u043B\u044F \u043F\u043E\u0438\u0441\u043A\u0430");
		//
		// RUSSIAN STRINGS FOR SETTINGS - 'ADVANCED'
		defaults.setProperty("settings.advanced.name", "\u0414\u043E\u043F\u043E\u043B\u043D\u0438\u0442\u0435\u043B\u044C\u043D\u043E");
		defaults.setProperty("settings.advanced.chatscrollhistory", "\u0418\u0441\u0442\u043E\u0440\u0438\u044F \u0447\u0430\u0442\u0430 (\u043B\u0438\u043D\u0438\u0438)");
		defaults.setProperty("settings.advanced.maxlengthchannelname", "\u041C\u0430\u043A\u0441. \u0434\u043B\u0438\u043D\u0430 \u0438\u043C\u0435\u043D\u0438 \u043A\u0430\u043D\u0430\u043B\u0430");
		defaults.setProperty("settings.advanced.multichatdelay", "\u041E\u0442\u043F\u0440\u0430\u0432\u043A\u0430 \u0441\u043E\u043E\u0431\u0449\u0435\u043D\u0438\u0439, \u043C\u0443\u043B\u044C\u0442\u0438\u0447\u0430\u0442 (\u041C\u0441)");
		defaults.setProperty("settings.advanced.chatboxunfocheight", "\u0412\u044B\u0441\u043E\u0442\u0430 \u043D\u0435\u0430\u043A\u0442\u0438\u0432\u043D\u043E\u0433\u043E \u0447\u0430\u0442\u0430");
		defaults.setProperty("settings.advanced.chatfadeticks", "\u0412\u0440\u0435\u043C\u044F \u0438\u0441\u0447\u0435\u0437\u043D\u043E\u0432\u0435\u043D\u0438\u044F (\u0422\u0438\u043A)");
		defaults.setProperty("settings.advanced.forceunicode", "\u042E\u043D\u0438\u043A\u043E\u0434 \u0440\u0435\u043D\u0434\u0435\u0440\u0438\u043D\u0433");
		//
		// RUSSIAN STRINGS FOR SETTINGS - 'CHAT CHANNEL'
		defaults.setProperty("settings.channel.notificationson", "\u0423\u0432\u0435\u0434\u043E\u043C\u043B\u0435\u043D\u0438\u044F");
		defaults.setProperty("settings.channel.alias", "\u0410\u043B\u0438\u0430\u0441");
		defaults.setProperty("settings.channel.cmdprefix", "\u041F\u0440\u0435\u0444\u0438\u043A\u0441 \u0432 \u0447\u0430\u0442\u0435");
		defaults.setProperty("settings.channel.position", "\u041F\u043E\u0437\u0438\u0446\u0438\u044F:");
		defaults.setProperty("settings.channel.of", "\u0438\u0437");
		//
		// RUSSIAN STRINGS FOR MESSAGES
		defaults.setProperty("messages.update1", "\u041D\u0430\u0439\u0434\u0435\u043D\u043E \u043E\u0431\u043D\u043E\u0432\u043B\u0435\u043D\u0438\u0435 ! (\u0422\u0435\u043A\u0443\u0449\u0430\u044F \u0432\u0435\u0440\u0441\u0438\u044F ");
		defaults.setProperty("messages.update2", ", \u043D\u043E\u0432\u0430\u044F ");
		defaults.setProperty("messages.update3", "\u041F\u043E\u0441\u0435\u0442\u0438\u0442\u0435 \u0442\u0435\u043C\u0443 \u043D\u0430 minecrafforum.net \u0434\u043B\u044F \u0441\u043A\u0430\u0447\u043A\u0438 \u043E\u0431\u043D\u043E\u0432\u043B\u0435\u043D\u0438\u044F.");
	}
}
