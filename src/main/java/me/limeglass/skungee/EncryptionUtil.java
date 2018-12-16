package me.limeglass.skungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import me.limeglass.skungee.spigot.Skungee;

public class EncryptionUtil {

	private me.limeglass.skungee.bungeecord.Skungee bungeeInstance;
	private String algorithm = "AES/CTS/PKCS5Padding";
	private Boolean spigot, printErrors;
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
				Skungee.infoMessage("Password is successfully hashed to file!");
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
				me.limeglass.skungee.bungeecord.Skungee.infoMessage("Password is successfully hashed to file!");
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
	
	public byte[] encrypt(String keyString, String algorithm, byte[] packet) {
		try {
			byte[] serializedKey = keyString.getBytes(Charset.forName("UTF-8"));
			if (serializedKey.length != 16) {
				Skungee.infoMessage("The cipher key length is invalid. The length needs to be 16 but was: " + serializedKey.length);
				return null;
			}
			SecretKeySpec key = new SecretKeySpec(serializedKey, "AES");
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
			return Base64.getEncoder().encode(cipher.doFinal(packet));
		} catch (NoSuchAlgorithmException e) {
			exception(e, "The algorithm `" + algorithm + "` does not exist for your system. Please use a different algorithm.");
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			if (printErrors)
				exception(e, "There was an error encrypting.");
		}
		return null;
	}
	
	public Object decrypt(String keyString, String algorithm, byte[] input) {
		try {
			byte[] serializedKey = keyString.getBytes(Charset.forName("UTF-8"));
			if (serializedKey.length != 16)
				Skungee.exception(new IllegalArgumentException(), "Invalid key size.");
			SecretKeySpec key = new SecretKeySpec(serializedKey, "AES");
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
			byte[] decoded = Base64.getDecoder().decode((byte[]) input);
			return deserialize(cipher.doFinal(decoded));
		} catch (NoSuchAlgorithmException e) {
			exception(e, "The algorithm `" + algorithm + "` does not exist for your system. Please use a different algorithm.");
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			if (printErrors)
				exception(e, "There was an error decrypting.");
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
