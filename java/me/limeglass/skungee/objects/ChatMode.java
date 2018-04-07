package me.limeglass.skungee.objects;

public enum ChatMode {

	//The player will only see everything except messages marked as chat.
	COMMANDS_ONLY,
	//The chat is completely disabled, the player won't see anything.
	HIDDEN,
	//The player will see all chat.
	SHOWN;
}
