package com.pay.wechat;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.Security;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.net.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.afan.tool.date.DateUtil;
import com.afan.tool.json.JsonUtil;

/**
 * ΢��ǩ�������㷨 
 * https://mp.weixin.qq.com/debug/wxadoc/dev/api/signature.html
 * 
 * @author afan
 * 
 */
public class WeChatSignUtil {
	private static final Logger logger = LoggerFactory.getLogger(WeChatSignUtil.class);
	public static boolean initialized = false;
	
	private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS5Padding";
	
	/**
	 * ΢�Żص�������ticket����
	 * @param decryptData
	 * @param msgSignature
	 * @param timeStamp
	 * @param nonce
	 * @param appId
	 * @param token
	 * @param aesKey
	 * @return
	 * @throws Exception
	 */
	private static String decryptVerifyTicket(String decryptData, String msgSignature, String timeStamp, String nonce, String appId, String token, String aesKey) throws Exception {
		if(msgSignature!=null && !msgSignature.equals(signSHA1(token, timeStamp, nonce, decryptData))){
			throw new Exception("msgSignature error!"); 
		}
		byte[] aesByte = Base64.decodeBase64(aesKey+"=");
		byte[] original = null;
		try {
			// ���ý���ģʽΪAES��CBCģʽ
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec key_spec = new SecretKeySpec(aesByte, "AES");
			IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(aesByte, 0, 16));
			cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);
			byte[] encrypted = Base64.decodeBase64(decryptData);
			original = cipher.doFinal(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String xmlContent = null,fromAppId = null;
		try {
			byte[] bytes = WxPKCS7Encoder.decode(original);
			byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
			int xmlLength = recoverNetworkBytesOrder(networkOrder);
			xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), "utf-8");
			fromAppId = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(appId!=null && !appId.equals(fromAppId)){
			throw new Exception("from appId:"+fromAppId+" error!"); 
		}
		return xmlContent;
	}
	
	/**
	 * SHAǩ��
	 * @param token
	 * @param timestamp
	 * @param nonce
	 * @param encrypt
	 * @return
	 */
	private static String signSHA1(String token, String timestamp, String nonce, String encrypt){
		try {
			String[] array = new String[] { token, timestamp, nonce, encrypt };
			StringBuffer sb = new StringBuffer();
			// �ַ�������
			Arrays.sort(array);
			for (int i = 0; i < 4; i++) {
				sb.append(array[i]);
			}
			String str = sb.toString();
			// SHA1ǩ������
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(str.getBytes());
			byte[] digest = md.digest();

			StringBuffer hexstr = new StringBuffer();
			String shaHex = "";
			for (int i = 0; i < digest.length; i++) {
				shaHex = Integer.toHexString(digest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexstr.append(0);
				}
				hexstr.append(shaHex);
			}
			return hexstr.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static int recoverNetworkBytesOrder(byte[] orderBytes) {
		int sourceNumber = 0;
		for (int i = 0; i < 4; i++) {
			sourceNumber <<= 8;
			sourceNumber |= orderBytes[i] & 0xff;
		}
		return sourceNumber;
	}
	
	/**
	 * ��֤΢��ǩ��
	 * @param encryptedData
	 * @param sessionKey
	 * @param iv
	 * @param appId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> checkSign(String encryptedData, String sessionKey, String iv, String appId) {
		logger.debug("check sign --> encryptedData:{} sessionKey:{} iv:{} appId:{}",new Object[]{encryptedData, sessionKey, iv, appId});
		String token = signature(encryptedData, sessionKey, iv);
		if (token != null) {
			try {
				Map<String, Object> tokenMap = (Map<String, Object>) JsonUtil.toMapObject(token);
				Map<String, Object> watermark = (LinkedHashMap<String, Object>)tokenMap.get("watermark");
				if (watermark != null) {
					if(appId.equals(watermark.get("appid"))){
						int timestamp = (Integer)watermark.get("timestamp");
						int leaveTime = DateUtil.getTimestamp() - timestamp;
						if(leaveTime > -5*60 && leaveTime < 86400){
							return tokenMap;
						}else{
							logger.warn("timestamp:{} is too long time ago", DateUtil.getDateStr(timestamp*1000L));
						}
					}else{
						logger.warn("appid:{} is error", watermark.get("appid"));
					}
				}else{
					logger.warn("watermark is error");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.warn("encryptedData:{} is error",token);
		}
		return null;
	}

	/**
	 * ǩ��
	 * @param encryptedData
	 * @param sessionKey
	 * @param iv
	 * @return
	 */
	public static String signature(String encryptedData, String sessionKey, String iv) {
		// �����ܵ�����
        byte[] dataByte = Base64.decodeBase64(encryptedData);
        // ������Կ
        byte[] keyByte = Base64.decodeBase64(sessionKey);
        // ƫ����
        byte[] ivByte = Base64.decodeBase64(iv);
        try {
            // �����Կ����16λ����ô�Ͳ���.  ���if �е����ݺ���Ҫ
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // ��ʼ��
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// ��ʼ��
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
            	return new String(resultByte, "UTF-8");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}

	
	public static String decryptData(String base64Data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);  
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(md5(key).toLowerCase().getBytes(), "AES"));  
        return new String(cipher.doFinal(Base64.decodeBase64(base64Data)), "UTF-8");
    }

	public static void initialize() {
		if (initialized)
			return;
		Security.addProvider(new BouncyCastleProvider());
		initialized = true;
	}

	public static AlgorithmParameters generateIV(byte[] iv) throws Exception {
		AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
		params.init(new IvParameterSpec(iv));
		return params;
	}

	/**
	 * ͳһ�µ�ǩ��
	 * @param params
	 * @param appSecret
	 * @return
	 */
	public static String signPay(Map<String, String> params, String appSecret) {
		Map<String, String> sortMap = new TreeMap<String, String>(
				new Comparator<String>() {
					@Override
					public int compare(String str1, String str2) {
						return str1.compareTo(str2);
					}
				});
		sortMap.putAll(params);

		String sign = "";
		for (String key : sortMap.keySet()) {
			if (sign.length() > 0) {
				sign += "&";
			}
			sign += key + "=" + sortMap.get(key);
		}
		return md5(sign + "&key=" + appSecret).toUpperCase();
	}
	
	private static String md5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		try {
			byte[] md5Bytes = md5.digest(inStr.getBytes("UTF-8"));//ǩ��ָ�����룬����ǩ����ʧ��
			StringBuffer hexValue = new StringBuffer();
			for (int i = 0; i < md5Bytes.length; i++) {
				int val = ((int) md5Bytes[i]) & 0xff;
				if (val < 16)
					hexValue.append("0");
				hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getXmlProp(String data, String tag){
		Pattern pattern  = Pattern.compile("<"+tag+">([\\s\\S]*?)</"+tag+">");
		Matcher matcher = pattern.matcher(data);
		String result = "";
		if(matcher.find()){
			result = matcher.group(1).replace("<![CDATA[", "").replaceAll("]]>", "");
		}
		return result;
	}
}

class WxPKCS7Encoder {
	private static final int BLOCK_SIZE = 32;

	/**
	 * ��ö����Ľ��в�λ�����ֽ�.
	 * 
	 * @param count ��Ҫ������䲹λ�����������ֽڸ���
	 * @return �����õ��ֽ�����
	 */
	public static byte[] encode(int count) {
		// ������Ҫ����λ��
		int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);
		if (amountToPad == 0) {
			amountToPad = BLOCK_SIZE;
		}
		// ��ò�λ���õ��ַ�
		char padChr = chr(amountToPad);
		String tmp = new String();
		for (int index = 0; index < amountToPad; index++) {
			tmp += padChr;
		}
		//return tmp.getBytes("utf-8");
		return tmp.getBytes();
	}

	/**
	 * ɾ�����ܺ����ĵĲ�λ�ַ�
	 * 
	 * @param decrypted���ܺ������
	 * @return ɾ����λ�ַ��������
	 */
	public static byte[] decode(byte[] decrypted) {
		int pad = decrypted[decrypted.length - 1];
		if (pad < 1 || pad > 32) {
			pad = 0;
		}
		return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
	}

	/**
	 * ������ת����ASCII���Ӧ���ַ������ڶ����Ľ��в���
	 * 
	 * @param a��Ҫת��������
	 * @return ת���õ����ַ�
	 */
	public static char chr(int a) {
		byte target = (byte) (a & 0xFF);
		return (char) target;
	}
}