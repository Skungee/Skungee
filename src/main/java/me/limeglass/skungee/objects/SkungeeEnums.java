package me.limeglass.skungee.objects;

public class SkungeeEnums {

	public enum HandSetting {
		LEFT,
		RIGHT;
	}
	
	public enum ChatMode {
		//The player will only see everything except messages marked as chat.
		COMMANDS_ONLY,
		//The chat is completely disabled, the player won't see anything.
		HIDDEN,
		//The player will see all chat.
		SHOWN;
	}
	
	//Used internally
	public enum State {
		VALUE,
		NODES,
		LIST;
	}
	
	public enum SkriptChangeMode {
		ADD,
		SET,
		REMOVE,
		REMOVE_ALL,
		DELETE,
		RESET;
	}

}
