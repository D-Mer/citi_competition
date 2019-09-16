package nju.citix.utils;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import nju.citix.dao.manager.ManagerMapper;
import nju.citix.po.Customer;
import nju.citix.po.Manager;
import nju.citix.service.customer.CustomerService;
import nju.citix.service.manager.ManagerService;
import nju.citix.vo.ManagerForm;
import nju.citix.vo.UserForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author jiang hui
 * @date 2019/8/17
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class JWTUtilTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private MockHttpSession session;

    @Autowired
    private ManagerService managerService;
    @Autowired
    private CustomerService customerService;
    @Resource
    private ManagerMapper managerMapper;

    private Manager supperManager;
    private String supperManagerToken;

    private Manager newManager;

    private UserForm newUser = new UserForm();
    private Customer newCustomer;
    private String newCustomerToken;

    private String requestIp = "192.168.10.12";
    private LocalDateTime testStartTime = LocalDateTime.now();

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        session = new MockHttpSession();

        supperManager = managerService.findManagerById(1);
        supperManagerToken = JWTUtil.getToken(supperManager);

        String username = "123abc";
        String password = "123adsfA";
        String email = "814775538@qq.com";
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newCustomer = customerService.register(newUser);
        newCustomer = customerService.login(username, password, requestIp);
        newCustomerToken = JWTUtil.getToken(newCustomer);

        newManager = new Manager();
        newManager.setUsername("newManager");
        newManager.setPassword("newManagerPwd");
        newManager.setBanned(false);
        managerMapper.insert(newManager);
//        加入session
//        UserVO user = new UserVO();
//        user.setId(8);
//        user.setIdentity(Identity.root);
//        session.setAttribute("user", user);
    }

    @Test
    public void addManagerWithTokenSuccessTest() throws Exception {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setBanned(false);
        managerForm.setUsername("nonexistent");
        managerForm.setPassword("1234562");
        mvc.perform(MockMvcRequestBuilders.post("/manager/addManager")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(managerForm))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", supperManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value("nonexistent"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.password").value("1234562"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.banned").value("false"));
    }

    @Test
    public void nonTokenFailureTest() throws Exception {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setBanned(false);
        managerForm.setUsername("nonexistence");
        managerForm.setPassword("1234562");
        mvc.perform(MockMvcRequestBuilders.post("/manager/addManager")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(managerForm))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.NON_TOKEN_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    @Test
    public void cantDecodeTokenFailureTest() throws Exception {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setBanned(false);
        managerForm.setUsername("nonexistence");
        managerForm.setPassword("1234562");
        mvc.perform(MockMvcRequestBuilders.post("/manager/addManager")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(managerForm))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", "it's a bad token")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.CANT_DECODE_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    @Test
    public void invalidManagerTokenFailureTest() throws Exception {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setBanned(false);
        managerForm.setUsername("nonexistence");
        managerForm.setPassword("1234562");
        mvc.perform(MockMvcRequestBuilders.post("/manager/addManager")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(managerForm))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", JWTUtil.INVALID_TOKEN_MANAGER_SAMPLE)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.INVALID_TOKEN_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    @Test
    public void invalidCustomerTokenFailureTest() throws Exception {
        String token = JWTUtil.getToken(newCustomer.getCustomerId(), JWTUtil.CUSTOMER + "hh", newCustomer.getPassword());
        mvc.perform(MockMvcRequestBuilders.get("/user/questionnaire/get")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newCustomer.getCustomerId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", token)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.INVALID_TOKEN_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    @Test
    public void invalidTokenUndefinedTest() throws Exception {
        String token = JWT.create().withAudience("id", "identify")// 将用户 id、身份 保存到 token 里面
                .sign(Algorithm.HMAC256("key"));
        mvc.perform(MockMvcRequestBuilders.get("/user/questionnaire/get")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newCustomer.getCustomerId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", token)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.INVALID_TOKEN_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    @Test
    public void unverifiedManagerTokenFailureTest() throws Exception {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setBanned(false);
        managerForm.setUsername("nonexistence");
        managerForm.setPassword("1234562");
        mvc.perform(MockMvcRequestBuilders.post("/manager/addManager")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(managerForm))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                //这个token为root在密码为"qwer"时生成的，就是验证失败的token
                .header("token", JWTUtil.UNVERIFIED_TOKEN_MANAGER_SAMPLE)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.UNVERIFIED_TOKEN_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    @Test
    public void unverifiedCustomerTokenFailureTest() throws Exception {
        newCustomer.setPassword("it's an error pwd");
        mvc.perform(MockMvcRequestBuilders.post("/user/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newCustomer.getCustomerId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", JWTUtil.getToken(newCustomer))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.UNVERIFIED_TOKEN_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    @Test
    public void nonexistenceManagerTokenFailureTest() throws Exception {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setBanned(false);
        managerForm.setUsername("nonexistence");
        managerForm.setPassword("1234562");
        mvc.perform(MockMvcRequestBuilders.post("/manager/addManager")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(managerForm))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                //这个token为"nonexistence"在密码为"non"身份为"管理员"时生成的，就是用户不存在的token
                .header("token", JWTUtil.NONEXISTENT_TOKEN_MANAGER_SAMPLE)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.USER_NONEXISTENT_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    @Test
    public void nonexistenceCustomerTokenFailureTest() throws Exception {
        newCustomer.setCustomerId(-1);
        mvc.perform(MockMvcRequestBuilders.post("/user/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(-1))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                //这个token为id为-1时生成，是不存在的用户
                .header("token", JWTUtil.getToken(newCustomer))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.USER_NONEXISTENT_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    @Test
    public void managerCallCustomerLimit() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newCustomer.getCustomerId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", supperManagerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.MANAGER_CALL_CUSTOMER_ERROR));
    }

    @Test
    public void customerCallManagerLimit() throws Exception {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setBanned(false);
        managerForm.setUsername("nonexistence");
        managerForm.setPassword("1234562");
        mvc.perform(MockMvcRequestBuilders.post("/manager/addManager")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(managerForm))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.NO_AUTHORITY_ERROR));
    }

    @Test
    public void noSuperAuthority() throws Exception {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setBanned(false);
        managerForm.setUsername("nonexistence");
        managerForm.setPassword("1234562");
        mvc.perform(MockMvcRequestBuilders.post("/manager/addManager")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(managerForm))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", JWTUtil.getToken(newManager))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(JWTUtil.NO_AUTHORITY_ERROR));
    }
}