package acs.tabbychat.lang;

import java.util.Properties;

public class TCLanguageSpanish extends TCLanguage {
	protected static String provides;
	protected final static Properties defaults = new Properties();
	static {
		provides = "es_ES";
		defaults.clear();
		
		// Espa&ntilde;ol, from juanjose920
		// SPANISH STRINGS FOR DELIMITERS
		defaults.setProperty("delims.angles", "<\u00C1ngulos>");
		defaults.setProperty("delims.braces", "{Llaves}");
		defaults.setProperty("delims.brackets", "[Corchetes]");
		defaults.setProperty("delims.parenthesis", "(Par\u00E9ntesis)");
		defaults.setProperty("delims.anglesparenscombo", "<(Combo)Y>");
		defaults.setProperty("delims.anglesbracketscombo", "<[Combo]Y>");
		//
		// SPANISH STRINGS FOR COLORS
		defaults.setProperty("colors.default", "Normal");
		defaults.setProperty("colors.darkblue", "Azul Oscuro");
		defaults.setProperty("colors.darkgreen", "Verde Oscuro");
		defaults.setProperty("colors.darkaqua", "Agua Oscuro");
		defaults.setProperty("colors.darkred", "Rojo Oscuro");
		defaults.setProperty("colors.purple", "Morado");
		defaults.setProperty("colors.gold", "Oro");
		defaults.setProperty("colors.gray", "Gris");
		defaults.setProperty("colors.darkgray", "Gris Oscuro");
		defaults.setProperty("colors.indigo", "A\u00F1il");
		defaults.setProperty("colors.brightgreen", "Verde Claro");
		defaults.setProperty("colors.aqua", "Agua");
		defaults.setProperty("colors.red", "Rojo");
		defaults.setProperty("colors.pink", "Rosa");
		defaults.setProperty("colors.yellow", "Amarillo");
		defaults.setProperty("colors.white", "Blanco");
		//
		// SPANISH STRINGS FOR FORMATS
		defaults.setProperty("formats.default", "Normal");
		defaults.setProperty("formats.bold", "Negrita");
		defaults.setProperty("formats.striked", "Tachado");
		defaults.setProperty("formats.underline", "Subrayado");
		defaults.setProperty("formats.italic", "Cursiva");
		//
		// SPANISH STRINGS FOR SOUNDS
		defaults.setProperty("sounds.orb", "Experiencia");
		defaults.setProperty("sounds.anvil", "Yunque");
		defaults.setProperty("sounds.bowhit", "Flecha");
		defaults.setProperty("sounds.break", "Romper");
		defaults.setProperty("sounds.click", "Click");
		defaults.setProperty("sounds.glass", "Cristal");
		defaults.setProperty("sounds.bass", "Bajo");
		defaults.setProperty("sounds.harp", "Arpa");
		defaults.setProperty("sounds.pling", "Pling");
		defaults.setProperty("sounds.cat", "Gato");
		defaults.setProperty("sounds.blast", "Explosi\u00F3n");
		defaults.setProperty("sounds.splash", "Salpicadura");
		defaults.setProperty("sounds.swim", "Nadar");
		defaults.setProperty("sounds.bat", "Murci\u00E9lago");
		defaults.setProperty("sounds.blaze", "Llama");
		defaults.setProperty("sounds.chicken", "Gallina");
		defaults.setProperty("sounds.cow", "Vaca");
		defaults.setProperty("sounds.dragon", "Drag\u00F3n");
		defaults.setProperty("sounds.endermen", "Enderman");
		defaults.setProperty("sounds.ghast", "Ghast");
		defaults.setProperty("sounds.pig", "Cerdo");
		defaults.setProperty("sounds.wolf", "Lobo");
		//
		// SPANISH STRINGS FOR SETTINGS - COMMON
		defaults.setProperty("settings.save", "Guardar");
		defaults.setProperty("settings.cancel", "Cancelar");
		defaults.setProperty("settings.new", "Nuevo");
		defaults.setProperty("settings.delete", "Borrar");
		//
		// SPANISH STRINGS FOR SETTINGS - 'GENERAL CONFIG'
		defaults.setProperty("settings.general.name", "General");
		defaults.setProperty("settings.general.tabbychatenable", "TabbyChat Activado");
		defaults.setProperty("settings.general.savechatlog", "Copiar chat a un archivo");
		defaults.setProperty("settings.general.timestampenable", "Mostrar hora");
		defaults.setProperty("settings.general.timestampstyle", "Estilo de hora");
		defaults.setProperty("settings.general.timestampcolor", "Color de hora");
		defaults.setProperty("settings.general.groupspam", "Juntar mensajes iguales");
		defaults.setProperty("settings.general.unreadflashing", "Notificaci\u00F3n de mensajes no le\u00EDdos");
		//
		// SPANISH STRING FOR SETTINGS - 'SERVER CONFIG'
		defaults.setProperty("settings.server.name", "Server");
		defaults.setProperty("settings.server.autochannelsearch", "Buscar canales automaticamente");
		defaults.setProperty("settings.server.delimiterchars", "Separadores de Chat-Canal");
		defaults.setProperty("settings.server.delimcolorbool", "Separadores coloreados");
		defaults.setProperty("settings.server.delimformatbool", "Separadores con formato");
		defaults.setProperty("settings.server.defaultchannels", "Canales Predeterminados");
		defaults.setProperty("settings.server.ignoredchannels", "Canales Ignorados");
		//
		// SPANISH STRING FOR SETTINGS - 'CUSTOM FILTERS'
		defaults.setProperty("settings.filters.name", "Filtros");
		defaults.setProperty("settings.filters.inversematch", "Coincidencia inversa");
		defaults.setProperty("settings.filters.casesensitive", "Sensible a may\u00FAsculas");
		defaults.setProperty("settings.filters.highlightbool", "Destacar coincidencias");
		defaults.setProperty("settings.filters.highlightcolor", "Color");
		defaults.setProperty("settings.filters.highlightformat", "Formato");
		defaults.setProperty("settings.filters.audionotificationbool", "Notificacion de sonido");
		defaults.setProperty("settings.filters.audionotificationsound", "Sonido");
		defaults.setProperty("settings.filters.filtername", "Filtrar Nombre");
		defaults.setProperty("settings.filters.sendtotabbool", "Enviar coincidencias a la pesta\u00F1a");
		defaults.setProperty("settings.filters.sendtotabname", "Nombre de pesta\u00F1a");
		defaults.setProperty("settings.filters.sendtoalltabs", "Todas las pesta\u00F1as");
		defaults.setProperty("settings.filters.removematches", "Ocultar coincidencias del chat");
		defaults.setProperty("settings.filters.expressionstring", "Expresi\u00F3n");
		//
		// SPANISH STRINGS FOR SETTINGS - 'ADVANCED SETTINGS'
		defaults.setProperty("settings.advanced.name", "Avanzada");
		defaults.setProperty("settings.advanced.chatscrollhistory", "Historial del chat (l\u00EDneas)");
		defaults.setProperty("settings.advanced.maxlengthchannelname", "Maxima longitud de un canal");
		defaults.setProperty("settings.advanced.multichatdelay", "Tiempo de espera para Multi-Chat (ms)");
		defaults.setProperty("settings.advanced.chatboxunfocheight", "Altura desenfocada");
		defaults.setProperty("settings.advanced.chatfadeticks", "Tiempo de desaparici\u00F3n (ticks)");
		defaults.setProperty("settings.advanced.forceunicode", "Forzar renderizado Unicode");
		//
		// SPANISH STRINGS FOR SETTINGS - CHAT CHANNEL
		defaults.setProperty("settings.channel.notificationson", "Notificaciones sin leer");
		defaults.setProperty("settings.channel.alias", "Alias");
		defaults.setProperty("settings.channel.cmdprefix", "Prefijo del comando");
		defaults.setProperty("settings.channel.position", "Posici\u00F3n:");
		defaults.setProperty("settings.channel.of", "de");
		//
		// SPANISH STRINGS FOR MESSAGES
		defaults.setProperty("messages.update1", "\u00A1Una nueva actualizaci\u00F3n! (La versi\u00F3n actual es");
		defaults.setProperty("messages.update2", ", la nueva es");
		defaults.setProperty("messages.update3", "Visita el foro de TabbyChat en minecraftforum.net para descargar");
	}
}
