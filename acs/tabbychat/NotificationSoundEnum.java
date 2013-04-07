package acs.tabbychat;

public enum NotificationSoundEnum {
	ORB("Orb", "random.orb"),
	ANVIL("Anvil", "random.anvil_land"),
	BOWHIT("Bow Hit", "random.bowhit"),
	BREAK("Break", "random.break"),
	CLICK("Click", "random.click"),
	GLASS("Glass", "random.glass"),
	BASS("Bass", "note.bassattack"),
	HARP("Harp", "note.harp"),
	PLING("Pling", "note.pling"),
	CAT("Cat", "mob.cat.meow");
	
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
