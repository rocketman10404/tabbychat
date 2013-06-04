package acs.tabbychat.settings;

import acs.tabbychat.core.TabbyChat;

public enum NotificationSoundEnum {
	ORB(TabbyChat.translator.getString("sounds.orb"), "random.orb"),
	ANVIL(TabbyChat.translator.getString("sounds.anvil"), "random.anvil_land"),
	BOWHIT(TabbyChat.translator.getString("sounds.bowhit"), "random.bowhit"),
	BREAK(TabbyChat.translator.getString("sounds.break"), "random.break"),
	CLICK(TabbyChat.translator.getString("sounds.click"), "random.click"),
	GLASS(TabbyChat.translator.getString("sounds.glass"), "random.glass"),
	BASS(TabbyChat.translator.getString("sounds.bass"), "note.bassattack"),
	HARP(TabbyChat.translator.getString("sounds.harp"), "note.harp"),
	PLING(TabbyChat.translator.getString("sounds.pling"), "note.pling"),
	CAT(TabbyChat.translator.getString("sounds.cat"), "mob.cat.meow"),
	BLAST(TabbyChat.translator.getString("sounds.blast"), "fireworks.blast"),
	SPLASH(TabbyChat.translator.getString("sounds.splash"), "liquid.splash"),
	SWIM(TabbyChat.translator.getString("sounds.swim"), "liquid.swim"),
	BAT(TabbyChat.translator.getString("sounds.bat"), "mob.bat.hurt"),
	BLAZE(TabbyChat.translator.getString("sounds.blaze"), "mob.blaze.hit"),
	CHICKEN(TabbyChat.translator.getString("sounds.chicken"), "mob.chicken.hurt"),
	COW(TabbyChat.translator.getString("sounds.cow"), "mob.cow.hurt"),
	DRAGON(TabbyChat.translator.getString("sounds.dragon"), "mob.enderdragon.hit"),
	ENDERMEN(TabbyChat.translator.getString("sounds.endermen"), "mob.endermen.hit"),
	GHAST(TabbyChat.translator.getString("sounds.ghast"), "mob.ghast.moan"),
	PIG(TabbyChat.translator.getString("sounds.pig"), "mob.pig.say"),
	WOLF(TabbyChat.translator.getString("sounds.wolf"), "mob.wolf.bark");
	
	private String title;
	private String file;
	
	private NotificationSoundEnum(String _title, String _file) {
		this.title = _title;
		this.file = _file;
	}
	
	public String toString() {
		return this.title;
	}
	
	public String file() {
		return this.file;
	}
}
