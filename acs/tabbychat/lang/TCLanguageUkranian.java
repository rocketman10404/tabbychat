package acs.tabbychat.lang;

import java.util.Properties;

public class TCLanguageUkranian extends TCLanguage {
	protected static String provides;
	protected final static Properties defaults = new Properties();
	static {
		provides = "uk_UA";
		defaults.clear();
		
		//  Ukranian, from eXtendedZero
		//  UKRANIAN STRINGS FOR DELIMITERS
		defaults.setProperty("delims.angles", "<Кути>");
		defaults.setProperty("delims.braces", "{Фігурні}");
		defaults.setProperty("delims.brackets", "[Квадратні]");
		defaults.setProperty("delims.parenthesis", "(Круглі)");
		defaults.setProperty("delims.anglesparenscombo", "<(Комбо)Pl.>");
		defaults.setProperty("delims.anglesbracketscombo", "<(Комбо)Pl.>");
		// 
		//  UKRANIAN STRINGS FOR COLORS
		defaults.setProperty("colors.default", "По замовч.");
		defaults.setProperty("colors.darkblue", "Темно Синій");
		defaults.setProperty("colors.darkgreen", "Темно Зелений");
		defaults.setProperty("colors.darkaqua", "Темно Голубий");
		defaults.setProperty("colors.darkred", "Темно Червоний");
		defaults.setProperty("colors.purple", "Фіолетовий");
		defaults.setProperty("colors.gold", "Золотий");
		defaults.setProperty("colors.gray", "Сірий");
		defaults.setProperty("colors.darkgray", "Темно Сірий");
		defaults.setProperty("colors.indigo", "Індіго");
		defaults.setProperty("colors.brightgreen", "Сітло Зелений");
		defaults.setProperty("colors.aqua", "Голубий");
		defaults.setProperty("colors.red", "Червоний");
		defaults.setProperty("colors.pink", "Рожевий");
		defaults.setProperty("colors.yellow", "Жовтий");
		defaults.setProperty("colors.white", "Білий");
		// 
		//  UKRANIAN STRINGS FOR FORMATS
		defaults.setProperty("formats.default", "По замовч.");
		defaults.setProperty("formats.bold", "Жирний");
		defaults.setProperty("formats.striked", "Закреслений");
		defaults.setProperty("formats.underline", "Підскреслений");
		defaults.setProperty("formats.italic", "Курсив");
		// 
		//  UKRANIAN STRINGS FOR SOUNDS
		defaults.setProperty("sounds.orb", "Досвід");
		defaults.setProperty("sounds.anvil", "Ковадло");
		defaults.setProperty("sounds.bowhit", "Стріла");
		defaults.setProperty("sounds.break", "Зламав");
		defaults.setProperty("sounds.click", "Клік");
		defaults.setProperty("sounds.glass", "Скло");
		defaults.setProperty("sounds.bass", "Бас");
		defaults.setProperty("sounds.harp", "Арфа");
		defaults.setProperty("sounds.pling", "Pling");
		defaults.setProperty("sounds.cat", "Кіт");
		defaults.setProperty("sounds.blast", "Вибух");
		defaults.setProperty("sounds.splash", "Сплеск");
		defaults.setProperty("sounds.swim", "Спрут");
		defaults.setProperty("sounds.bat", "Летюча Миша");
		defaults.setProperty("sounds.blaze", "Блейз");
		defaults.setProperty("sounds.chicken", "Курка");
		defaults.setProperty("sounds.cow", "Корова");
		defaults.setProperty("sounds.dragon", "Дракон");
		defaults.setProperty("sounds.endermen", "Ендерман");
		defaults.setProperty("sounds.ghast", "Гаст");
		defaults.setProperty("sounds.pig", "Свиня");
		defaults.setProperty("sounds.wolf", "Вовк");
		// 
		//  UKRANIAN STRINGS FOR SETTINGS - COMMON
		defaults.setProperty("settings.save", "Зберегти");
		defaults.setProperty("settings.cancel", "Відміна");
		defaults.setProperty("settings.new", "Новий");
		defaults.setProperty("settings.delete", "Видалити");
		// 
		//  UKRANIAN STRINGS FOR SETTINGS - 'GENERAL CONFIG'
		defaults.setProperty("settings.general.name", "Головне");
		defaults.setProperty("settings.general.tabbychatenable", "TabbyChat включений");
		defaults.setProperty("settings.general.savechatlog", "Лог чату у файл");
		defaults.setProperty("settings.general.timestampenable", "Відмітка часу чату");
		defaults.setProperty("settings.general.timestampstyle", "Стиль відмітки часу");
		defaults.setProperty("settings.general.timestampcolor", "Колір відмітки часу");
		defaults.setProperty("settings.general.groupspam", "Групувати однакові повідомлення");
		defaults.setProperty("settings.general.unreadflashing", "Повідомлення про непрочитане");
		// 
		//  UKRANIAN STRING FOR SETTINGS - 'SERVER CONFIG'
		defaults.setProperty("settings.server.name", "Сервер");
		defaults.setProperty("settings.server.autochannelsearch", "Авто пошук нових каналів");
		defaults.setProperty("settings.server.delimiterchars", "Роздільник каналів");
		defaults.setProperty("settings.server.delimcolorbool", "Кольорові роздільники");
		defaults.setProperty("settings.server.delimformatbool", "Форматовані роздільники");
		defaults.setProperty("settings.server.defaultchannels", "Канали по замовчуванні");
		defaults.setProperty("settings.server.ignoredchannels", "Канали, що ігноруються");
		// 
		//  UKRANIAN STRING FOR SETTINGS - 'CUSTOM FILTERS'
		defaults.setProperty("settings.filters.name", "Фільтри");
		defaults.setProperty("settings.filters.inversematch", "Інвертувати");
		defaults.setProperty("settings.filters.casesensitive", "Враховувати регістр");
		defaults.setProperty("settings.filters.highlightbool", "Підсвічувати співпадання");
		defaults.setProperty("settings.filters.highlightcolor", "Колір");
		defaults.setProperty("settings.filters.highlightformat", "Формат");
		defaults.setProperty("settings.filters.audionotificationbool", "Аудіо сповіщення");
		defaults.setProperty("settings.filters.audionotificationsound", "Звук");
		defaults.setProperty("settings.filters.filtername", "Ім'я Фільтру");
		defaults.setProperty("settings.filters.sendtotabbool", "Надсилати співпадання у вкладку");
		defaults.setProperty("settings.filters.sendtotabname", "Ім'я Вкладки");
		defaults.setProperty("settings.filters.sendtoalltabs", "Всі вкладки");
		defaults.setProperty("settings.filters.removematches", "Ховати співпадання з чату");
		defaults.setProperty("settings.filters.expressionstring", "Вираз");
		// 
		//  UKRANIAN STRINGS FOR SETTINGS - 'ADVANCED SETTINGS'
		defaults.setProperty("settings.advanced.name", "Додатково");
		defaults.setProperty("settings.advanced.chatscrollhistory", "Історія чату (лінії)");
		defaults.setProperty("settings.advanced.maxlengthchannelname", "Макс. довжина імені каналу");
		defaults.setProperty("settings.advanced.multichatdelay", "Затримка відправлення у мультичат (Мс)");
		defaults.setProperty("settings.advanced.chatboxunfocheight", "Висота неактивного чату");
		defaults.setProperty("settings.advanced.chatfadeticks", "Час зникнення (тік)");
		defaults.setProperty("settings.advanced.forceunicode", "Юнікод рендеринг");
		// 
		//  UKRANIAN STRINGS FOR SETTINGS - CHAT CHANNEL
		defaults.setProperty("settings.channel.notificationson", "Непрочитані повідомлення");
		defaults.setProperty("settings.channel.alias", "Аліаси");
		defaults.setProperty("settings.channel.cmdprefix", "Префікс команд чату");
		defaults.setProperty("settings.channel.position", "Позиція:");
		defaults.setProperty("settings.channel.of", "з");
		// 
		//  UKRANIAN STRINGS FOR MESSAGES
		defaults.setProperty("messages.update1", "Знайдено оновлення! (Поточна версія ");
		defaults.setProperty("messages.update2", ", нова ");
		defaults.setProperty("messages.update3", "Відвідайте форум TabbyChat на minecraftforum.net для оновлення.");
	}
}
