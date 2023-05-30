import com.hummerrisk.commons.constants.CloudTaskConstants;
import com.hummerrisk.commons.utils.LogUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class APITest {

    private static String accessKey = "4lgj2ItZaSyTCHhp";
    private static String secretKey = "RK90F8GfCOFvN8oy";
    private static final String UTF_8 = "UTF-8";

    /**
     * JAVA 签名方法, API 调用 DEMO
     * @throws Exception
     */
    @org.junit.Test
    public void test () throws Exception {
        try {
            URL url = new URL("http://localhost:8080/user/list/all");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json;charset=UTF-8");
            urlConnection.setRequestProperty("accessKey", accessKey);
            String str = accessKey + "|" + System.currentTimeMillis();
            String signature = aesEncrypt(str, secretKey, accessKey);
            urlConnection.setRequestProperty("signature", signature);
            InputStream is = urlConnection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while (-1 != (len = is.read(buffer))) {
                baos.write(buffer, 0, len);
                baos.flush();
            }
            System.out.println(urlConnection.getResponseCode());
            System.out.println(baos.toString("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * AES加密
     *
     * @param src       待加密字符串
     * @param secretKey 密钥
     * @param iv        向量
     * @return 加密后字符串
     */
    public static String aesEncrypt(String src, String secretKey, String iv) throws Exception {
        if (StringUtils.isBlank(secretKey)) {
            throw new Exception("secretKey is empty");
        }

        try {
            byte[] raw = secretKey.getBytes(UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
            // "算法/模式/补码方式" ECB
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv1 = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv1);
            byte[] encrypted = cipher.doFinal(src.getBytes(UTF_8));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            throw new Exception("AES encrypt error:", e);
        }

    }

    /**
     * AES 解密
     *
     * @param src       待解密字符串
     * @param secretKey 密钥
     * @param iv        向量
     * @return 解密后字符串
     */
    public static String aesDecrypt(String src, String secretKey, String iv) {
        if (StringUtils.isBlank(secretKey)) {
            throw new RuntimeException("secretKey is empty");
        }
        try {
            byte[] raw = secretKey.getBytes(UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv1 = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv1);
            byte[] encrypted1 = Base64.decodeBase64(src);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, UTF_8);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            // 解密的原字符串为非加密字符串，则直接返回原字符串
            return src;
        } catch (Exception e) {
            throw new RuntimeException("decrypt error，please check parameters", e);
        }
    }

    @org.junit.Test
    public void test3 () throws Exception {
        try {
            File file = new File(CloudTaskConstants.PROWLER_RESULT_FILE_PATH + "/" + CloudTaskConstants.PROWLER_RUN_RESULT_FILE);
            if (file.isFile() && file.exists()) { // 判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), "UTF-8");// 考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null)
                {
                    if(lineTxt.contains("FAIL!")){
                        System.out.println(lineTxt);
                    }
                }
                bufferedReader.close();
                read.close();

            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception error) {
            throw new Exception("读取文件内容出错:" + error.getMessage());
        }
    }

}
