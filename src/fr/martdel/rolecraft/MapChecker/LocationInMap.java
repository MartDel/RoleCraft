package fr.martdel.rolecraft.MapChecker;

public enum LocationInMap {
	
	OWNED("votre terrain"),
	HOUSE("la maison d'un joueur"),
	FARM("la ferme d'un fermier"),
	SHOP("le magasin d'un joueur"),
	BUILD("le terrain de construction d'un builder"),
	PROTECTED_MAP("le village ou dans la périphérie"),
	FREE_PLACE("une zone libre");

	private final String description;

	LocationInMap(String desc) {
		this.description = desc;
	}
	
	public String getDescription() {
		return this.description;
	}
}
