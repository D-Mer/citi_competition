package nju.citix.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

/**
 * @author jiang hui
 * @date 2019/8/24
 */
@Component
public class AlipayConfig {

    public static final String URL = "http://localhost:8081";
    public static final String FRONT_URL = "http://localhost:8080";
    public static final String FROM_EMAIL = "1121057486@qq.com";
    public static final String ALIPAY_DEMO = "alipay_demo";
    public static final String ALIPAY_DEMO_VERSION = "alipay_demo_JAVA_20180907104657";

    private static AlipayClient alipayClient;

    /**
     * 配置文件加载
     */
    private static Properties prop;

    /**
     * 配置文件名称
     */
    public static String CONFIG_FILE = "Alipay-Config.properties";

    /**
     * 配置文件相对路径
     */
    public static String ALIPAY_CONFIG_PATH = File.separator + "etc" + File.separator + CONFIG_FILE;

    /**
     * 项目路径
     */
    public static String PROJECT_PATH = "";

    private static Log logger = LogFactory.getLog(AlipayConfig.class);


    /**
     * 初始化配置值
     */
    public static void initProperties() {
        prop = new Properties();
        try {
            synchronized (prop) {
                InputStream inputStream = AlipayConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
                prop.load(inputStream);
                inputStream.close();
            }
            String address = getUrl();
            prop.setProperty("NOTIFY_URL", address + "/finance/notify");
            prop.setProperty("RETURN_URL", address + "/finance/returnHandler");
        } catch (IOException e) {
            logger.error("日志 =============》： 配置文件Alipay-Config.properties找不到");
            e.printStackTrace();
        }
    }

    private static String getUrl() {
        return URL;
    }

    /**
     * 获取配置文件信息
     *
     * @return 配置文件信息
     */
    public static Properties getProperties() {
        if (prop == null) {
            initProperties();
        }
        return prop;
    }


    /**
     * 配置信息写入配置文件
     */
    public static void writeConfig() {
        System.out.println("ALIPAY_CONFIG_PATH:" + ALIPAY_CONFIG_PATH);
        File file = new File(ALIPAY_CONFIG_PATH);
        if (file.exists()) {
            file.setReadable(true);
            file.setWritable(true);
        }
        String lineText = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bw = null;
        StringBuilder stringBuffer = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new FileReader(ALIPAY_CONFIG_PATH));
            while ((lineText = bufferedReader.readLine()) != null) {

                if (lineText.startsWith("APP_ID")) {
                    lineText = "APP_ID = " + prop.getProperty("APP_ID");
                } else if (lineText.startsWith("RSA2_PRIVATE_KEY")) {
                    lineText = "RSA2_PRIVATE_KEY = " + prop.getProperty("RSA2_PRIVATE_KEY");
                } else if (lineText.startsWith("RSA2_PUBLIC_KEY")) {
                    lineText = "RSA2_PUBLIC_KEY = " + prop.getProperty("RSA2_PUBLIC_KEY");
                } else if (lineText.startsWith("ALIPAY_RSA2_PUBLIC_KEY")) {
                    lineText = "ALIPAY_RSA2_PUBLIC_KEY = " + prop.getProperty("ALIPAY_RSA2_PUBLIC_KEY");
                } else if (lineText.startsWith("NOTIFY_URL")) {
                    lineText = "NOTIFY_URL = " + prop.getProperty("NOTIFY_URL");
                } else if (lineText.startsWith("RETURN_URL")) {
                    lineText = "RETURN_URL = " + prop.getProperty("RETURN_URL");
                } else if (lineText.startsWith("SANDBOX_BUYER_EMAIL")) {
                    lineText = "SANDBOX_BUYER_EMAIL = " + prop.getProperty("SANDBOX_BUYER_EMAIL");
                } else if (lineText.startsWith("SANBOX_BUYER_LOGON_PWD")) {
                    lineText = "SANBOX_BUYER_LOGON_PWD = " + prop.getProperty("SANBOX_BUYER_LOGON_PWD");
                } else if (lineText.startsWith("SANBOX_BUYER_PAY_PWD")) {
                    lineText = "SANBOX_BUYER_PAY_PWD = " + prop.getProperty("SANBOX_BUYER_PAY_PWD");
                } else if (lineText.startsWith("SANDBOX_SELLER_ID")) {
                    lineText = "SANDBOX_SELLER_ID = " + prop.getProperty("SANDBOX_SELLER_ID");
                } else if (lineText.startsWith("SANDBOX_SELLER_EMAIL")) {
                    lineText = "SANDBOX_SELLER_EMAIL = " + prop.getProperty("SANDBOX_SELLER_EMAIL");
                } else if (lineText.startsWith("SANDBOX_SELLER_LOGON_PWD")) {
                    lineText = "SANDBOX_SELLER_LOGON_PWD = " + prop.getProperty("SANDBOX_SELLER_LOGON_PWD");
                } else if (lineText.startsWith("ALIPAY_GATEWAY_URL")) {
                    lineText = "ALIPAY_GATEWAY_URL = " + prop.getProperty("ALIPAY_GATEWAY_URL");
                } else if (lineText.startsWith("CHARSET")) {
                    lineText = "CHARSET = " + prop.getProperty("CHARSET");
                } else if (lineText.startsWith("FORMAT")) {
                    lineText = "FORMAT = " + prop.getProperty("FORMAT");
                } else if (lineText.startsWith("SIGNTYPE")) {
                    lineText = "SIGNTYPE = " + prop.getProperty("SIGNTYPE");
                }

                stringBuffer.append(lineText).append("\r\n");
            }
            bufferedReader.close();
            bw = new BufferedWriter(new FileWriter(ALIPAY_CONFIG_PATH));
            bw.write(stringBuffer.toString());
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 封装公共请求参数
     *
     * @return AlipayClient
     */
    public static AlipayClient getAlipayClient() {
        if (alipayClient != null) {
            return alipayClient;
        }
        initProperties();
        // 网关
        String URL = prop.getProperty("ALIPAY_GATEWAY_URL");
        // 商户APP_ID
        String APP_ID = prop.getProperty("APP_ID");
        // 商户RSA 私钥
        String APP_PRIVATE_KEY = prop.getProperty("RSA2_PRIVATE_KEY");
        // 请求方式 json
        String FORMAT = prop.getProperty("FORMAT");
        // 编码格式，目前只支持UTF-8
        String CHARSET = prop.getProperty("CHARSET");
        // 支付宝公钥
        String ALIPAY_PUBLIC_KEY = prop.getProperty("ALIPAY_RSA2_PUBLIC_KEY");
        // 签名方式
        String SIGN_TYPE = prop.getProperty("SIGN_TYPE");
        alipayClient = new DefaultAlipayClient(URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        return alipayClient;
    }

}