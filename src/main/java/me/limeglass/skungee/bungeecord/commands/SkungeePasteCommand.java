package me.limeglass.skungee.bungeecord.commands;

import me.limeglass.skungee.bungeecord.Skungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SkungeePasteCommand extends Command {

	public SkungeePasteCommand() {
		super("skungee");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "Console only."));
			return;
		}
		if (args.length == 0) {
			sender.sendMessage(new TextComponent(ChatColor.LIGHT_PURPLE + "/skungee paste - create a paste to send to developers."));
			return;
		}
		if (args[0].equalsIgnoreCase("paste")) {
			TextComponent component = new TextComponent(ChatColor.YELLOW + "Paste: " + Skungee.getInstance().postSkungeeHaste());
			sender.sendMessage(component);
		}
	}

}
