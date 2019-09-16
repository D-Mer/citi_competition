package nju.citix.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import nju.citix.po.Customer;
import nju.citix.po.Manager;
import nju.citix.service.customer.CustomerService;
import nju.citix.service.manager.ManagerService;
import org.springframework.stereotype.Service;


/**
 * @author jiang hui
 * @date 2019/8/15
 */
@Service
public class JWTUtil {

    public static final String ALL = "所有人";
    public static final String LOGIN = "已登录";
    public static final String MANAGER = "管理员";
    public static final String CUSTOMER = "消费者";
    public static final String SUPER = "超级管理员";
    public static final String NON_TOKEN_ERROR = "无token，请登录";
    public static final String CANT_DECODE_ERROR = "无法解析token，token语法不对或消息头、有效负载不是JSONs";
    public static final String MANAGER_CALL_CUSTOMER_ERROR = "管理员不允许发送该消费者请求";
    public static final String NO_AUTHORITY_ERROR = "权限不足";
    public static final String USER_NONEXISTENT_ERROR = "用户不存在，请重新登录";
    public static final String INVALID_TOKEN_ERROR = "非法token，请登录";
    public static final String UNVERIFIED_TOKEN_ERROR = "token无效，请重新登录获取token";
    /**
     * 非法token
     * 用户名：root
     * 密码：123456
     * 身份：hhh
     */
    public static final String INVALID_TOKEN_MANAGER_SAMPLE = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiMSIsImhoaCJdfQ.veF2QMJKQ-Id0AuUfOlb9BiAzS13xkhvjWJCg0-RLwA";
    /**
     * 密码验证失败的token
     * 用户名：root
     * 密码：qwer
     * 身份：管理员
     */
    public static final String UNVERIFIED_TOKEN_MANAGER_SAMPLE = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiMSIsIueuoeeQhuWRmCJdfQ.1M2YhYuX1vgj1DEp6FqTih0FEQzeFd3vaJuGMyHeB78";
    /**
     * 用户不存在的token
     * 用户名：nonexistence
     * 密码：non
     * 身份：管理员
     */
    public static final String NONEXISTENT_TOKEN_MANAGER_SAMPLE = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOlsiMTAwIiwi566h55CG5ZGYIl19.MrjtKqvLx-lDMSiCnbDZKC2iTykoJTdLC16E69xZv8A";
    private static CustomerService customerService;
    private static ManagerService managerService;

    public JWTUtil(CustomerService customerService, ManagerService managerService) {
        JWTUtil.customerService = customerService;
        JWTUtil.managerService = managerService;
    }

    /**
     * 根据输入的对象的id、密码、身份生成相应的token
     *
     * @param manager 需要生成token的管理员对象
     * @return 由管理员的属性生成的token
     */
    public static String getToken(Manager manager) {
        String token;
        token = JWT.create().withAudience("" + manager.getManagerId(), MANAGER)// 将 manager id 保存到 token 里面，参数的顺序与拦截器中解析的顺序相同
                .sign(Algorithm.HMAC256(manager.getPassword()));// 以 password 作为 token 的密钥
        return token;
    }

    /**
     * 根据输入的对象的id、密码、身份生成相应的token
     *
     * @param customer 需要生成token的消费者对象
     * @return 由消费者的属性生成的token
     */
    public static String getToken(Customer customer) {
        String token;
        token = JWT.create().withAudience("" + customer.getCustomerId(), CUSTOMER)// 将 customer id 保存到 token 里面
                .sign(Algorithm.HMAC256(customer.getPassword()));// 以 password 作为 token 的密钥
        return token;
    }

    /**
     * 根据输入的对象的id、密码、身份生成相应的token
     *
     * @param userId   需要生成token的用户id
     * @param identify 用户身份
     * @param key      密钥，默认使用用户密码
     * @return 由消费者的属性生成的token
     */
    public static String getToken(int userId, String identify, String key) {
        String token;
        token = JWT.create().withAudience("" + userId, identify)// 将用户 id、身份 保存到 token 里面
                .sign(Algorithm.HMAC256(key));// 默认以 password 作为 token 的密钥
        return token;
    }

    /**
     * 基于JWT进行安全验证
     *
     * @param token     用户请求头中的token
     * @param authority 请求的方法的权限要求
     * @throws TokenException 安全认证失败原因
     */
    public static void securityCheck(String token, String authority) throws TokenException {
        if (authority.equals(ALL)) return;
        if (token == null) throw new TokenException(JWTUtil.NON_TOKEN_ERROR);
        int id;
        String identity;
        String key;
        try {
            id = Integer.parseInt(JWT.decode(token).getAudience().get(0));
            identity = JWT.decode(token).getAudience().get(1);
        } catch (JWTDecodeException e) {
            throw new TokenException(CANT_DECODE_ERROR);
        } catch (Exception e) {
            throw new TokenException(INVALID_TOKEN_ERROR);
        }
        if (!(identity.equals(MANAGER) || identity.equals(CUSTOMER))) {
            throw new TokenException(INVALID_TOKEN_ERROR);
        }
        try {
            key = getUserKey(id, identity);
            signatureVerify(token, key);
        } catch (NullPointerException e) {
            throw new TokenException(USER_NONEXISTENT_ERROR);
        } catch (JWTVerificationException e) {
            throw new TokenException(UNVERIFIED_TOKEN_ERROR);
        }
        switch (authority) {
            case CUSTOMER:
                customerVerify(id, identity, key);
                break;
            case MANAGER:
                managerVerify(id, identity, key);
                break;
            case SUPER:
                superVerify(id, identity, key);
                break;
            default:
                break;
        }
    }

    /**
     * 根据id和身份获取用户密钥，即用户密码
     *
     * @param id       用户id
     * @param identity 用户身份
     * @return 用户密钥，即密码
     * @throws NullPointerException 如果id对应的用户不存在则返回抛出空指针异常
     */
    private static String getUserKey(int id, String identity) throws NullPointerException {
        String key = null;
        switch (identity) {
            case CUSTOMER:
                Customer customer = customerService.findCustomerById(id);
                key = customer.getPassword();
                break;
            case MANAGER:
                Manager manager = managerService.findManagerById(id);
                key = manager.getPassword();
                break;
            default:
                break;
        }
        return key;
    }

    /**
     * 仅消费者可访问的权限认证
     *
     * @param id       用户id
     * @param identity 用户身份
     * @param key      用户密钥密钥
     */
    private static void customerVerify(int id, String identity, String key) {
        if (!identity.equals(CUSTOMER)) throw new TokenException(MANAGER_CALL_CUSTOMER_ERROR);
    }

    /**
     * 仅管理员可访问的权限认证
     *
     * @param id       用户id
     * @param identity 用户身份
     * @param key      用户密钥密钥
     */
    private static void managerVerify(int id, String identity, String key) {
        if (!identity.equals(MANAGER)) throw new TokenException(NO_AUTHORITY_ERROR);
    }

    /**
     * 仅管超级理员可访问的权限认证
     *
     * @param id       用户id
     * @param identity 用户身份
     * @param key      用户密钥密钥
     */
    private static void superVerify(int id, String identity, String key) {
        if (id != 1 || !identity.equals(MANAGER) || !key.equals("123456")) throw new TokenException(NO_AUTHORITY_ERROR);
    }

    /**
     * 验证签名是否能由密钥解析
     * 验证成功则不处理
     * 验证失败则抛出异常
     *
     * @param token 请求头中的token
     * @param key   用来解析token的签名部分的密钥
     */
    private static void signatureVerify(String token, String key) throws JWTVerificationException {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(key)).build();
        jwtVerifier.verify(token);
    }

    /**
     * 在用token进行身份验证时出现的异常
     */
    public static class TokenException extends RuntimeException {
        public TokenException() {
            super();
        }
        public TokenException(String message) {
            super(message);
        }
    }

}
