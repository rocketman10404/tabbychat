package acs.tabbychat.lang;

import java.util.Properties;

public class TCLanguageFrench extends TCLanguage {
	protected static String provides;
	protected final static Properties defaults = new Properties();
	static {
		provides = "fr_FR";
		defaults.clear();
		// French, from Plumeex
		// FRENCH STRINGS FOR DELIMITERS
		defaults.setProperty("delims.angles", "<Chevrons>");
		defaults.setProperty("delims.braces", "{Accolades}");
		defaults.setProperty("delims.brackets", "[Crochets]");
		defaults.setProperty("delims.parenthesis", "(Parenthèses)");
		defaults.setProperty("delims.anglesparenscombo", "<(Combo)Pl.>");
		defaults.setProperty("delims.anglesbracketscombo", "<[Combo]Pl.>");
		//
		// FRENCH STRINGS FOR COLORS
		defaults.setProperty("colors.default", "Normal");
		defaults.setProperty("colors.darkblue", "Bleu outremer");
		defaults.setProperty("colors.darkgreen", "Vert foncé");
		defaults.setProperty("colors.darkaqua", "Bleu foncé");
		defaults.setProperty("colors.darkred", "Rouge foncé");
		defaults.setProperty("colors.purple", "Violet");
		defaults.setProperty("colors.gold", "Or");
		defaults.setProperty("colors.gray", "Gris");
		defaults.setProperty("colors.darkgray", "Gris foncé");
		defaults.setProperty("colors.indigo", "Indigo");
		defaults.setProperty("colors.brightgreen", "Vert clair");
		defaults.setProperty("colors.aqua", "Bleu clair");
		defaults.setProperty("colors.red", "Rouge");
		defaults.setProperty("colors.pink", "Rose <3");
		defaults.setProperty("colors.yellow", "Jaune");
		defaults.setProperty("colors.white", "Blanc");
		//
		// FRENCH STRINGS FOR FORMATS
		defaults.setProperty("formats.default", "Normal");
		defaults.setProperty("formats.bold", "Gras");
		defaults.setProperty("formats.striked", "Barré");
		defaults.setProperty("formats.underline", "Souligné");
		defaults.setProperty("formats.italic", "Italique");
		//
		// FRENCH STRINGS FOR SOUNDS
		defaults.setProperty("sounds.orb", "Orbe");
		defaults.setProperty("sounds.anvil", "Enclume");
		defaults.setProperty("sounds.bowhit", "Flຌhe");
		defaults.setProperty("sounds.break", "Brisement");
		defaults.setProperty("sounds.click", "Clic");
		defaults.setProperty("sounds.glass", "Verre");
		defaults.setProperty("sounds.bass", "Basse");
		defaults.setProperty("sounds.harp", "Harpe");
		defaults.setProperty("sounds.pling", "Pling");
		defaults.setProperty("sounds.cat", "Chat");
		defaults.setProperty("sounds.blast", "Explosion");
		defaults.setProperty("sounds.splash", "Plouf");
		defaults.setProperty("sounds.swim", "Nage");
		defaults.setProperty("sounds.bat", "Chauve-souris");
		defaults.setProperty("sounds.blaze", "Blaze");
		defaults.setProperty("sounds.chicken", "Poule");
		defaults.setProperty("sounds.cow", "Vache");
		defaults.setProperty("sounds.dragon", "Dragon");
		defaults.setProperty("sounds.endermen", "Enderman");
		defaults.setProperty("sounds.ghast", "Ghast");
		defaults.setProperty("sounds.pig", "Cochon");
		defaults.setProperty("sounds.wolf", "Loup");
		//
		// FRENCH STRINGS FOR SETTINGS - COMMON
		defaults.setProperty("settings.save", "Sauvegarger");
		defaults.setProperty("settings.cancel", "Annuler");
		defaults.setProperty("settings.new", "Nouveau");
		defaults.setProperty("settings.delete", "Supprimer");
		//
		// FRENCH STRINGS FOR SETTINGS - 'GENERAL CONFIG'
		defaults.setProperty("settings.general.name", "Config générale");
		defaults.setProperty("settings.general.tabbychatenable", "TabbyChat Activé");
		defaults.setProperty("settings.general.savechatlog", "Enregistrer le chat dans un fichier");
		defaults.setProperty("settings.general.timestampenable", "Horodatage du chat");
		defaults.setProperty("settings.general.timestampstyle", "Style de l'horodatage");
		defaults.setProperty("settings.general.timestampcolor", "Couleur de l'horodatage");
		defaults.setProperty("settings.general.groupspam", "Consolider la protection du spam");
		defaults.setProperty("settings.general.unreadflashing", "Notifications clignotantes par dut");
		//
		// FRENCH STRING FOR SETTINGS - 'SERVER CONFIG'
		defaults.setProperty("settings.server.name", "Config serveur");
		defaults.setProperty("settings.server.autochannelsearch", "Recherche automatique de canaux");
		defaults.setProperty("settings.server.delimiterchars", "Délimiteurs de canaux");
		defaults.setProperty("settings.server.delimcolorbool", "Délimiteurs colorés");
		defaults.setProperty("settings.server.delimformatbool", "Délimiteurs formattés");
		defaults.setProperty("settings.server.defaultchannels", "Canaux par dut");
		defaults.setProperty("settings.server.ignoredchannels", "Canaux ignorés");
		//
		// FRENCH STRING FOR SETTINGS - 'CUSTOM FILTERS'
		defaults.setProperty("settings.filters.name", "Filtres persos");
		defaults.setProperty("settings.filters.inversematch", "Exclure l'expression");
		defaults.setProperty("settings.filters.casesensitive", "Sensible à la casse");
		defaults.setProperty("settings.filters.highlightbool", "Surligner les correspondances");
		defaults.setProperty("settings.filters.highlightcolor", "Couleur");
		defaults.setProperty("settings.filters.highlightformat", "Format");
		defaults.setProperty("settings.filters.audionotificationbool", "Notification audio");
		defaults.setProperty("settings.filters.audionotificationsound", "Son");
		defaults.setProperty("settings.filters.filtername", "Nom du filtre");
		defaults.setProperty("settings.filters.sendtotabbool", "Envoyer les correspondances à l'onglet");
		defaults.setProperty("settings.filters.sendtotabname", "Nom de l'onglet");
		defaults.setProperty("settings.filters.sendtoalltabs", "Tous les onglets");
		defaults.setProperty("settings.filters.removematches", "Cacher les correspondances du tchat");
		defaults.setProperty("settings.filters.expressionstring", "Expression");
		//
		// FRENCH STRINGS FOR SETTINGS - 'ADVANCED SETTINGS'
		defaults.setProperty("settings.advanced.name", "Paramètres avancés");
		defaults.setProperty("settings.advanced.chatscrollhistory", "Historique du chat à retenir (lignes)");
		defaults.setProperty("settings.advanced.maxlengthchannelname", "Longueur max. du nom des canaux");
		defaults.setProperty("settings.advanced.multichatdelay", "Délai de multiples messages (ms)");
		defaults.setProperty("settings.advanced.chatboxunfocheight", "Hauteur du chat non-actif");
		defaults.setProperty("settings.advanced.chatfadeticks", "Disparition du chat (ticks)");
		defaults.setProperty("settings.advanced.forceunicode", "Forcer le rendu Unicode");
		//
		// FRENCH STRINGS FOR SETTINGS - CHAT CHANNEL
		defaults.setProperty("settings.channel.notificationson", "Notifications non lues");
		defaults.setProperty("settings.channel.alias", "Alias");
		defaults.setProperty("settings.channel.cmdprefix", "Prຟixe de la commande");
		defaults.setProperty("settings.channel.position", "Position :");
		defaults.setProperty("settings.channel.of", "sur");
		//
		// FRENCH STRINGS FOR MESSAGES
		defaults.setProperty("messages.update1", "Mise à jour disponible ! (Votre version est la");
		defaults.setProperty("messages.update2", ", la dernière est la");
		defaults.setProperty("messages.update3", "Visitez le topic de TabbyChat sur minecraftforum.net pour la télຜharger.");
	}
}
