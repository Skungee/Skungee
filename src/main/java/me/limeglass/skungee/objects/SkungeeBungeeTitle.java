package me.limeglass.skungee.objects;

import java.util.Set;

import me.limeglass.skungee.bungeecord.Skungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SkungeeBungeeTitle extends SkungeeTitle {
	
	private static final long serialVersionUID = -7814841274381307683L;

	public SkungeeBungeeTitle(String title) {
		super(title);
	}
	
	public SkungeeBungeeTitle(String title, int stay) {
		super(title, stay);
	}
	
	public SkungeeBungeeTitle(String title, int fadeIn, int stay, int fadeOut) {
		super(title, fadeIn, stay, fadeOut);
	}
	
	public SkungeeBungeeTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		super(title, subtitle, fadeIn, stay, fadeOut);
	}
	
	public SkungeeBungeeTitle(SkungeeTitle title) {
		super(title.getTitleText(), title.getSubtitleText(), title.getFadeIn(), title.getStay(), title.getFadeOut());
	}
	
	private Title getTitle() {
		Title title = Skungee.getInstance().getProxy().createTitle();
		if (this.title != null)
			title.title(new TextComponent(this.title));
		if (this.subtitle != null)
			title.subTitle(new TextComponent(this.subtitle));
		if (this.fadeIn >= 0)
			title.fadeIn(this.fadeIn);
		if (this.stay >= 0)
			title.stay(this.stay);
		if (this.fadeOut >= 0)
			title.fadeOut(this.fadeOut);
		return title;
	}
	
	public void send(Set<ProxiedPlayer> players) {
		for (ProxiedPlayer player : players) {
			getTitle().send(ProxyServer.getInstance().getPlayer(player.getUniqueId()));
		}
	}

}
