package com.lyy.yyaddressbook.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.util.Log;

/**
 * 字符串工具集合
 *
 * @author LIMING
 */
public class DESUtils {

	private static final String PASSWORD_CRYPT_KEY = "ORIFOUND";
	private final static String DES = "DES";

	private static final String TAG = "lyy-des加密工具";
	private static final boolean D = true;

	/**
	 * 加密
	 *
	 * @param src
	 *            数据源
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * @return 返回加密后的数据
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		// 现在，获取数据并加密
		// 正式执行加密操作
		return cipher.doFinal(src);
	}

	/**
	 * 解密
	 *
	 * @param src
	 *            数据源
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * @return 返回解密后的原始数据
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		// 现在，获取数据并解密
		// 正式执行解密操作
		return cipher.doFinal(src);
	}

	/**
	 * 密码解密
	 *
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public final static String decrypt(String data) {
		// long startTime = System.currentTimeMillis();
		// if (D)
		// Log.i(TAG, "解密：" + data);
		// try {
		// byte[] dd = decrypt(hex2byte(data.getBytes("utf-8")),
		// PASSWORD_CRYPT_KEY.getBytes());
		// if (D)
		// Log.i(TAG, "时间：" + (System.currentTimeMillis() - startTime));
		// return new String(dd);
		// } catch (Exception e) {
		// // if (D)
		// // Log.i(TAG, "解密异常：" + e.toString());
		// // e.printStackTrace();
		// }
		// return "";
		// // return data.replaceAll(PASSWORD_CRYPT_KEY, "");
		return data;

	}

	/**
	 * 密码加密
	 *
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public final static String encrypt(String password) {
		// long startTime = System.currentTimeMillis();
		// if (D)
		// Log.i(TAG, "加密：" + password);
		// try {
		//
		// byte[] aa = password.getBytes("utf-8");
		// byte[] aa1 = new byte[aa.length - 2];
		// for (int i = 2; i < aa.length; i++)
		// aa1[i - 2] = aa[i];
		// byte[] temp = encrypt(aa1, PASSWORD_CRYPT_KEY.getBytes());
		//
		// if (D)
		// Log.i(TAG, "时间：" + (System.currentTimeMillis() - startTime));
		//
		// return byte2hex(temp);
		// } catch (Exception e) {
		// if (D)
		// Log.i(TAG, "加密异常:" + e.toString());
		// e.printStackTrace();
		// }
		// return null;
		// // return password + PASSWORD_CRYPT_KEY;
		return password;

	}

	/**
	 *
	 * 二行制转字符串
	 *
	 * @param b
	 *
	 * @return
	 */

	public static String byte2hex(byte[] b) {

		String hs = "";

		String stmp = "";

		for (int n = 0; n < b.length; n++) {

			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));

			if (stmp.length() == 1)

				hs = hs + "0" + stmp;

			else

				hs = hs + stmp;

		}

		return hs.toUpperCase();

	}

	public static byte[] hex2byte(byte[] b) {

		if ((b.length % 2) != 0)

			throw new IllegalArgumentException("长度不是偶数");

		byte[] b2 = new byte[b.length / 2];

		for (int n = 0; n < b.length; n += 2) {

			String item = new String(b, n, 2);

			b2[n / 2] = (byte) Integer.parseInt(item, 16);

		}

		return b2;
	}
}
