package me.limeglass.skungee.common.objects;

import java.io.Serializable;

public class SkungeeTitle implements Serializable {

	protected static final long serialVersionUID = -7377209366283539512L;
	protected int fadeIn = 2, stay = 2, fadeOut = 2;
	protected String title, subtitle;

	public SkungeeTitle(String title) {
		this.title = title;
	}
	
	public SkungeeTitle(String title, int stay) {
		this.title = title;
		this.stay = stay;
	}
	
	public SkungeeTitle(String title, int fadeIn, int stay, int fadeOut) {
		this.fadeOut = fadeOut;
		this.fadeIn = fadeIn;
		this.title = title;
		this.stay = stay;
	}
	
	public SkungeeTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		this.subtitle = subtitle;
		this.fadeOut = fadeOut;
		this.fadeIn = fadeIn;
		this.title = title;
		this.stay = stay;
	}
	
	public void setFadeIn(int fadeIn) {
		this.fadeIn = fadeIn;
	}
	
	public int getFadeIn() {
		return fadeIn;
	}
	
	public void setFadeOut(int fadeOut) {
		this.fadeOut = fadeOut;
	}
	
	public int getFadeOut() {
		return fadeOut;
	}

	public int getStay() {
		return stay;
	}

	public void setStay(int stay) {
		this.stay = stay;
	}

	public String getTitleText() {
		return title;
	}
	
	public void setTitleText(String title) {
		this.title = title;
	}

	public String getSubtitleText() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
	public String toString() {
		String output = "SkungeeTitle ";
		if (subtitle != null) output = output + " subtitle=" + subtitle + " ";
		if (fadeOut >= 0) output = output + " fadeOut=" + fadeOut + " ";
		if (title != null) output = output + " title=" + title + " ";
		if (fadeIn >= 0) output = output + " fadeIn=" + fadeIn + " ";
		if (stay >= 0) output = output + " stay=" + stay + " ";
		return output;
	}
}
