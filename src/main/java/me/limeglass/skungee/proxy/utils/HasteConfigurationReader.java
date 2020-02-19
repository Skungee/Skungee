package me.limeglass.skungee.proxy.utils;

import java.io.BufferedReader;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import me.limeglass.skungee.bungeecord.SkungeeBungee;

public class HasteConfigurationReader {

	private final Stream<String> stream;
	private final SkungeeBungee instance;
	private String product;
	
	public HasteConfigurationReader(Stream<String> stream) {
		this.instance = SkungeeBungee.getInstance();
		this.stream = stream;
	}
	
	public HasteConfigurationReader addReplacer(Replacer replacer) {
		replacers.add(replacer);
		return this;
	}
	
	public HasteConfigurationReader read(Collection<String> keys) {
		product = stream.map(line -> {
			for (String key : keys) {
				for (Replacer replacer : replacers) {
					if (replacer.accepts(key))
						line = line.replace(replacer.getSyntax(), replacer.replacement());
				}
			}
			return line;
		}).collect(Collectors.joining("\n"));
		return this;
	}
	
	public HasteConfigurationReader add(String string) {
		product = product + "\n" + string;
		return this;
	}
	
	public HasteConfigurationReader add(BufferedReader reader) {
		product = product + "\n" + reader.lines().collect(Collectors.joining("\n"));
		return this;
	}
	
	public String finish() {
		return product;
	}
	
	private Set<Replacer> replacers = Sets.newHashSet(
			new Replacer("Bungeecord", "$os") {
				@Override
				public String replacement() {
					return System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version");
				}
			},
			new Replacer("Bungeecord", "$java") {
				@Override
				public String replacement() {
					return System.getProperty("java.version") + " (" + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + ")";
				}
			},
			new Replacer("Bungeecord", "$version") {
				@Override
				public String replacement() {
					return instance.getProxy().getVersion();
				}
			},
			new Replacer("Bungeecord", "$protocol") {
				@SuppressWarnings("deprecation")
				@Override
				public String replacement() {
					return instance.getProxy().getProtocolVersion() + "";
				}
			}
		);
	
	public abstract class Replacer {
		
		private final String syntax, section;
		
		public Replacer(String section, String syntax) {
			this.section = section;
			this.syntax = syntax;
		}
		
		public boolean accepts(String section) {
			return section.equals(this.section);
		}
		
		public abstract String replacement();
		
		public String getSyntax() {
			return syntax;
		}

	}
	
}

