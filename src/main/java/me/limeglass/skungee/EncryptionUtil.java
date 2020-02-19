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

import me.limeglass.skungee.common.wrappers.SecurityConfiguration;
import me.limeglass.skungee.common.wrappers.SkungeePlatform;
import me.limeglass.skungee.spigot.SkungeeSpigot;

public class EncryptionUtil {

	private final SecurityConfiguration security;
	private final SkungeePlatform platform;

	public EncryptionUtil(SkungeePlatform platform) {
		this.security = platform.getConfiguration().getSecurityConfiguration();
		this.platform = platform;
		if (!security.isPasswordEnabled())
			return;
		if (!security.isPasswordHashed())
			return;
		if (!security.isPasswordFileHashed())
			return;
		if (!security.getPassword().equalsIgnoreCase("hashed")) {
			try {
				File hashedFile = new File(platform.getDataFolder(), "hashed.txt");
				if (!hashedFile.exists())
					hashedFile.createNewFile();
				FileOutputStream out = new FileOutputStream(hashedFile);
				out.write(hashPassword());
				out.close();
				platform.consoleMessage("You're now safe to set the `password` option to \"hashed\"");
			} catch (IOException e) {
				platform.exception(e, "There was an error writting the hash to file.");
			}
		}
		if (isFileHashed())
			platform.consoleMessage("Password was successfully hashed to file!");
	}

	public byte[] hashPassword() {
		try {
			byte[] base64 = Base64.getEncoder().encode(security.getPassword().getBytes(StandardCharsets.UTF_8));
			return MessageDigest.getInstance(security.getPasswordAlgorithm()).digest(base64);
		} catch (NoSuchAlgorithmException e) {
			platform.exception(e, "The algorithm `" + security.getPasswordAlgorithm() + "` does not exist for your system. Please use a different algorithm.");
			return null;
		}
	}

	public boolean isFileHashed() {
		if (!security.isPasswordEnabled())
			return false;
		if (!security.isPasswordHashed())
			return false;
		if (!security.isPasswordFileHashed())
			return false;
		return security.getPassword().equalsIgnoreCase("hashed") && getHashFromFile() != null;
	}

	public byte[] getHashFromFile() {
		File hashedFile = new File(platform.getDataFolder(), "hashed.txt");
		try {
			return Files.readAllBytes(hashedFile.toPath());
		} catch (IOException e) {
			platform.exception(e, "There was an error reading the hash from file.");
			return null;
		}
	}

	public byte[] encrypt(String keyString, String algorithm, byte[] packet) {
		try {
			byte[] serializedKey = keyString.getBytes(Charset.forName("UTF-8"));
			if (serializedKey.length != 16) {
				SkungeeSpigot.infoMessage("The cipher key length is invalid. The length needs to be 16 but was: " + serializedKey.length);
				return null;
			}
			SecretKeySpec key = new SecretKeySpec(serializedKey, "AES");
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
			return Base64.getEncoder().encode(cipher.doFinal(packet));
		} catch (NoSuchAlgorithmException e) {
			platform.exception(e, "The algorithm `" + algorithm + "` does not exist for your system. Please use a different algorithm.");
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			if (security.canPrintErrors())
				platform.exception(e, "There was an error encrypting.");
		}
		return null;
	}

	public Object decrypt(String keyString, String algorithm, byte[] input) {
		try {
			byte[] serializedKey = keyString.getBytes(Charset.forName("UTF-8"));
			if (serializedKey.length != 16)
				platform.exception(new IllegalArgumentException(), "Invalid key size.");
			SecretKeySpec key = new SecretKeySpec(serializedKey, "AES");
			Cipher cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
			byte[] decoded = Base64.getDecoder().decode((byte[]) input);
			return deserialize(cipher.doFinal(decoded));
		} catch (NoSuchAlgorithmException e) {
			platform.exception(e, "The algorithm `" + algorithm + "` does not exist for your system. Please use a different algorithm.");
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			if (security.canPrintErrors())
				platform.exception(e, "There was an error decrypting.");
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
			platform.exception(e, "Error happened when serializing.");
		}
		return null;
	}

	public Object deserialize(byte[] input) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(input);
			ObjectInputStream inputStream = new ObjectInputStream(in);
			return inputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			platform.exception(e, "Error happened when deserializing.");
		}
		return null;
	}

}
