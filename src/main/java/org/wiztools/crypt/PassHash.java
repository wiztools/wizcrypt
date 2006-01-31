package org.wiztools.crypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PassHash{
	public static byte[] passHash(String password) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(password.getBytes());
		byte[] raw = md.digest();
		return raw;
	}
}
