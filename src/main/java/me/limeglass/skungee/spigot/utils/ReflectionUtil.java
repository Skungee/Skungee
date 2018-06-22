package me.limeglass.skungee.spigot.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.netty.channel.Channel;
import net.md_5.bungee.api.plugin.Plugin;

public class ReflectionUtil {
	
	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
	}
	
	public static Class<?> getNMSClass(String classString) throws ClassNotFoundException {
		String name = "net.minecraft.server." + getVersion() + classString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}
	
	public static Class<?> getOBCClass(String classString) {
		String name = "org.bukkit.craftbukkit." + getVersion() + classString;
		@SuppressWarnings("rawtypes")
		Class obcClass = null;
		try {
			obcClass = Class.forName(name);
		}
		catch (ClassNotFoundException error) {
			error.printStackTrace();
			return null;
		}
		return obcClass;
	}
	
	public static Channel getChannel(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object connection = getConnection(player);
		Field connectionField = connection.getClass().getField("networkManager");
		Object networkManager = connectionField.get(connection);
		Field channelField = networkManager.getClass().getField("channel");
		return (Channel) channelField.get(networkManager);
	}
	
	public static Set<Class<?>> getClasses(JarFile jar, String... packages) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		try {
			for (Enumeration<JarEntry> jarEntry = jar.entries(); jarEntry.hasMoreElements();) {
				String name = jarEntry.nextElement().getName().replace("/", ".");
				String className = name.substring(0, name.length() - 6);
				className = className.replace('/', '.');
				for (String packageName : packages) {
					if (name.startsWith(packageName) && name.endsWith(".class")) {
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
	
	public static Set<Class<?>> getClasses(JavaPlugin instance, String... packages) {
		try {
			Method method = JavaPlugin.class.getDeclaredMethod("getFile");
			method.setAccessible(true);
			File file = (File) method.invoke(instance);
			JarFile jar = new JarFile(file);
			return getClasses(jar, packages);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IOException e) {
			e.printStackTrace();
		}
		return null;
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
	
	public static Object getConnection(Player player) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object nmsPlayer = getHandle(player);
		Field connectionField = nmsPlayer.getClass().getField("playerConnection");
		return connectionField.get(nmsPlayer);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Set<Class<? extends T>> getSubTypesOf(Plugin instance, Class<T> of, String... packages) {
		return getClasses(instance, packages).parallelStream()
			.filter(clazz -> clazz.isAssignableFrom(of))
			.map(clazz -> (Class<? extends T>)clazz)
			.collect(Collectors.toSet());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Set<Class<? extends T>> getSubTypesOf(JavaPlugin instance, Class<T> of, String... packages) {
		return getClasses(instance, packages).parallelStream()
			.filter(clazz -> clazz.isAssignableFrom(of))
			.map(clazz -> (Class<? extends T>)clazz)
			.collect(Collectors.toSet());
	}
	
	public static <T> boolean setField(Class<T> from, Object obj, String field, Object newValue){
		try {
			Field f = from.getDeclaredField(field);
			f.setAccessible(true);
			f.set(obj, newValue);
			return true;
		} catch (Exception e){}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getField(String field, Class<?> from, Object obj){
		try{
			Field f = from.getDeclaredField(field);
			f.setAccessible(true);
			return (T) f.get(obj);
		} catch (Exception e){}
		return null;	
	}
	
	public static Object getHandle(Object obj) {
		if (obj != null) {
			try {
				Method getHandle = obj.getClass().getMethod("getHandle");
				getHandle.setAccessible(true);
				return getHandle.invoke(obj);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void sendPacket(Object object, Player... players) throws NoSuchMethodException {
		try {
			for (Player player: players) {
				Method method = getConnection(player).getClass().getMethod("sendPacket", getNMSClass("Packet"));
				method.invoke(getConnection(player), object);
			}
		} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static Object getNMSBlock(Block block) {
		try {
			Method method = ReflectionUtil.getOBCClass("util.CraftMagicNumbers").getDeclaredMethod("getBlock", Block.class);
			method.setAccessible(true);
			return method.invoke(ReflectionUtil.getOBCClass("util.CraftMagicNumbers"), block);
		} catch (SecurityException | IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
