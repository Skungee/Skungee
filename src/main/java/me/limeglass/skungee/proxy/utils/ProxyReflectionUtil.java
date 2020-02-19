package me.limeglass.skungee.proxy.utils;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.md_5.bungee.api.plugin.Plugin;

public class ProxyReflectionUtil {

	public static Set<Class<?>> getClasses(JarFile jar, String... packages) {
		Set<Class<?>> classes = new HashSet<>();
		try {
			for (Enumeration<JarEntry> jarEntry = jar.entries(); jarEntry.hasMoreElements();) {
				String name = jarEntry.nextElement().getName().replace("/", ".");
				if (name.length() >= 6) {
					String className = name.substring(0, name.length() - 6);
					className = className.replace('/', '.');
					for (String packageName : packages) {
						if (!name.startsWith(packageName) || !name.endsWith(".class"))
							continue;
						classes.add(Class.forName(className));
					}
				}
			}
			jar.close();
		} catch (IOException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return classes;
	}

	public static Set<Class<?>> getClasses(Plugin instance, String... packages) {
		try {
			JarFile jar = new JarFile(instance.getFile());
			return getClasses(jar, packages);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
