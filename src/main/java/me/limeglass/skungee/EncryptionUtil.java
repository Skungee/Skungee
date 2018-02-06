package me.limeglass.skungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import me.limeglass.skungee.spigot.Skungee;

public class EncryptionUtil {

	private String algorithm = "AES/CTS/PKCS5Padding";
	private Boolean spigot, printErrors;
	private me.limeglass.skungee.bungeecord.Skungee bungeeInstance;
	private Skungee spigotInstance;
	
	public EncryptionUtil(me.limeglass.skungee.bungeecord.Skungee skungee, Boolean spigot) {
		this.bungeeInstance = skungee;
		this.spigot = spigot;
		if (spigot) {
			algorithm = Skungee.getInstance().getConfig().getString("security.encryption.cipherAlgorithm", "AES/CTS/PKCS5Padding");
			printErrors = Skungee.getInstance().getConfig().getBoolean("security.encryption.printEncryptionErrors", true);
		} else {
			algorithm = me.limeglass.skungee.bungeecord.Skungee.getConfig().getString("security.encryption.cipherAlgorithm", "AES/CTS/PKCS5Padding");
			printErrors = me.limeglass.skungee.bungeecord.Skungee.getConfig().getBoolean("security.encryption.printEncryptionErrors", true);
		}
		hashFile();
	}
	
	public EncryptionUtil(Skungee skungee, Boolean spigot) {
		this.spigotInstance = skungee;
		this.spigot = spigot;
		if (spigot) {
			algorithm = Skungee.getInstance().getConfig().getString("security.encryption.cipherAlgorithm", "AES/CTS/PKCS5Padding");
			printErrors = Skungee.getInstance().getConfig().getBoolean("security.encryption.printEncryptionErrors", true);
		} else {
			algorithm = me.limeglass.skungee.bungeecord.Skungee.getConfig().getString("security.encryption.cipherAlgorithm", "AES/CTS/PKCS5Padding");
			printErrors = me.limeglass.skungee.bungeecord.Skungee.getConfig().getBoolean("security.encryption.printEncryptionErrors", true);
		}
		hashFile();
	}

	public final byte[] encrypt(byte[] input) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, keyGenerator.generateKey());
			return cipher.doFinal(input);
		} catch (NoSuchAlgorithmException e) {
			exception(e, "The algorithm `" + algorithm + "` does not exist for your system. Please use a different algorithm.");
		} catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			if (printErrors) {
				exception(e, "There was an error encrypting.");
			}
		}
		return null;
	}
	
	public final void hashFile() {
		if (spigot) {
			if (Skungee.getInstance().getConfig().getBoolean("security.password.enabled", false) && Skungee.getInstance().getConfig().getBoolean("security.password.hash", true)
			&& Skungee.getInstance().getConfig().getBoolean("security.password.hashFile", false) && !Skungee.getInstance().getConfig().getString("security.password.password").equals("hashed")) {
				try {
					File hashedFile = new File(Skungee.getInstance().getDataFolder(), "hashed.txt");
					if (!hashedFile.exists()) hashedFile.createNewFile();
					else hashedFile.delete();
					FileOutputStream out = new FileOutputStream(hashedFile);
					out.write(hash());
					out.close();
					Skungee.consoleMessage("You're now safe to set the `password` option to \"hashed\"");
				} catch (IOException e) {
					exception(e, "There was an error writting the hash to file.");
				}
			}
			if (isFileHashed()) {
				Skungee.infoMessage("Password is succefully hashed to file!");
			}
		} else {
			if (me.limeglass.skungee.bungeecord.Skungee.getConfig().getBoolean("security.password.enabled", false) && me.limeglass.skungee.bungeecord.Skungee.getConfig().getBoolean("security.password.hash", true)
			&& me.limeglass.skungee.bungeecord.Skungee.getConfig().getBoolean("security.password.hashFile", false) && !me.limeglass.skungee.bungeecord.Skungee.getConfig().getString("security.password.password").equals("hashed")) {
				try {
					File hashedFile = new File(me.limeglass.skungee.bungeecord.Skungee.getInstance().getDataFolder(), "hashed.txt");
					if (!hashedFile.exists()) hashedFile.createNewFile();
					else hashedFile.delete();
					FileOutputStream out = new FileOutputStream(hashedFile);
					out.write(hash());
					out.close();
					me.limeglass.skungee.bungeecord.Skungee.consoleMessage("You're now safe to set the `password` option to \"hashed\"");
				} catch (IOException e) {
					exception(e, "There was an error writting the hash to file.");
				}
			}
			if (isFileHashed()) {
				me.limeglass.skungee.bungeecord.Skungee.infoMessage("Password is succefully hashed to file!");
			}
		}
	}
	
	public final Boolean isFileHashed() {
		if (spigot) {
			return (Skungee.getInstance().getConfig().getBoolean("security.password.enabled", false) && Skungee.getInstance().getConfig().getBoolean("security.password.hash", true)
			&& Skungee.getInstance().getConfig().getBoolean("security.password.hashFile", false) && Skungee.getInstance().getConfig().getString("security.password.password").equals("hashed") && getHashFromFile() != null);
		} else {
			return (me.limeglass.skungee.bungeecord.Skungee.getConfig().getBoolean("security.password.enabled", false) && me.limeglass.skungee.bungeecord.Skungee.getConfig().getBoolean("security.password.hash", true)
			&& me.limeglass.skungee.bungeecord.Skungee.getConfig().getBoolean("security.password.hashFile", false) && me.limeglass.skungee.bungeecord.Skungee.getConfig().getString("security.password.password").equals("hashed") && getHashFromFile() != null);
		}
	}
	
	public final byte[] getHashFromFile() {
		File hashedFile;
		if (spigot) {
			hashedFile = new File(Skungee.getInstance().getDataFolder(), "hashed.txt");
		} else {
			hashedFile = new File(me.limeglass.skungee.bungeecord.Skungee.getInstance().getDataFolder(), "hashed.txt");
		}
		try {
			return Files.readAllBytes(hashedFile.toPath());
		} catch (IOException e) {
			exception(e, "There was an error reading the hash from file.");
		}
		return null;
	}
	
	public final byte[] hash() {
		if (spigot) {
			try {
				byte[] base64 = Base64.getEncoder().encode(Skungee.getInstance().getConfig().getString("security.password.password").getBytes(StandardCharsets.UTF_8));
				return MessageDigest.getInstance(Skungee.getInstance().getConfig().getString("security.password.hashAlgorithm", "SHA-256")).digest(base64);
			} catch (NoSuchAlgorithmException e) {
				exception(e, "The algorithm `" + algorithm + "` does not exist for your system. Please use a different algorithm.");
			}
		} else {
			try {
				byte[] base64 = Base64.getEncoder().encode(me.limeglass.skungee.bungeecord.Skungee.getConfig().getString("security.password.password").getBytes(StandardCharsets.UTF_8));
				return MessageDigest.getInstance(me.limeglass.skungee.bungeecord.Skungee.getConfig().getString("security.password.hashAlgorithm", "SHA-256")).digest(base64);
			} catch (NoSuchAlgorithmException e) {
				exception(e, "The algorithm `" + algorithm + "` does not exist for your system. Please use a different algorithm.");
			}
		}
		return null;
	}
	
	public final byte[] decrypt(byte[] input) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, keyGenerator.generateKey());
			return cipher.doFinal(input);
		} catch (NoSuchAlgorithmException e) {
			exception(e, "The algorithm `" + algorithm + "` does not exist for your system. Please use a different algorithm.");
		} catch (NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			if (printErrors) {
				exception(e, "There was an error decrypting.");
			}
		}	
		return null;
	}
	
	public byte[] serialize(Object object) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(out);
			outputStream.writeObject(object);
			return out.toByteArray();
		} catch (IOException e) {
			exception(e, "Error happened when serializing.");
		}
		return null;
	}

	public Object deserialize(byte[] input) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(input);
			ObjectInputStream inputStream = new ObjectInputStream(in);
			return inputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			exception(e, "Error happened when deserializing.");
		}
		return null;
	}
	
	public Boolean isSpigot() {
		return spigot;
	}
	
	public me.limeglass.skungee.bungeecord.Skungee getBungeeInstance() {
		return bungeeInstance;
	}
	
	public Skungee getSpigotInstance() {
		return spigotInstance;
	}
	
	private void exception(Throwable e, String reason) {
		if (spigot) Skungee.exception(e, reason);
		else me.limeglass.skungee.bungeecord.Skungee.exception(e, reason);
	}
}
