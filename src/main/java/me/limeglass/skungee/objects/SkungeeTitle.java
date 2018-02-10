package me.limeglass.skungee.objects;

import java.io.Serializable;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;

public class SkungeeTitle implements Serializable {

	private static final long serialVersionUID = -7377209366283539512L;
	private Boolean created = false, initialized = false;
	private int fadeIn = 2, stay = 2, fadeOut = 2;
	private String string, subtitle;
	private Title title;

	public SkungeeTitle(String string) {
		this.string = string;
	}
	
	public SkungeeTitle(String string, long stay) {
		this.string = string;
		this.stay = (int)stay;
	}
	
	public SkungeeTitle(String string, int fadeIn, int stay, int fadeOut) {
		this.fadeOut = fadeOut;
		this.string = string;
		this.fadeIn = fadeIn;
		this.stay = stay;
	}
	
	public SkungeeTitle(String string, String subtitle, int fadeIn, int stay, int fadeOut) {
		this.subtitle = subtitle;
		this.fadeOut = fadeOut;
		this.string = string;
		this.fadeIn = fadeIn;
		this.stay = stay;
	}
 	
	public void clear() {
		this.title.clear();
	}
	
	public void reset() {
		this.title.reset();
		this.created = false;
	}
	
	public void send(SkungeePlayer... players) {
		for (SkungeePlayer player : players) {
			getTitle().send(ProxyServer.getInstance().getPlayer(player.getUUID()));
		}
	}
	
 	public void setFadeIn(int fadeIn) {
		this.fadeIn = fadeIn;
		this.created = false;
	}
	
	public int getFadeIn() {
		return fadeIn;
	}
	
	public void setFadeOut(int fadeOut) {
		this.fadeOut = fadeOut;
		this.created = false;
	}
	
	public int getFadeOut() {
		return fadeOut;
	}

	public int getStay() {
		return stay;
	}

	public void setStay(int stay) {
		this.stay = stay;
		this.created = false;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
		this.created = false;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
		this.created = false;
	}
	
	public void setTitle(Title title) {
		this.initialized = true;
		this.created = false;
		this.title = title;
	}
	
	public Boolean isCreated() {
		return created;
	}
	
	public Boolean isInitialized() {
		return initialized;
	}
	
	public Title getTitle() {
		if (!isInitialized()) return null;
		if (!isCreated()) {
			this.title.fadeIn(fadeIn);
			this.title.stay(stay);
			this.title.fadeOut(fadeOut);
			if (string != null) this.title.title(new TextComponent(string));
			if (subtitle != null) this.title.subTitle(new TextComponent(subtitle));
			this.created = true;
		}
		return title;
	}
}
