package nju.citix.controller.customer;

import com.alibaba.fastjson.JSON;
import nju.citix.dao.customer.CustomerMapper;
import nju.citix.po.Customer;
import nju.citix.po.Questionnaire;
import nju.citix.utils.JWTUtil;
import nju.citix.vo.QuestionnaireForm;
import nju.citix.vo.UserForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import static nju.citix.controller.customer.CustomerController.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CustomerControllerTest {
    @Resource
    private WebApplicationContext wac;
    @Resource
    private CustomerMapper customerMapper;
    private MockMvc mvc;
    private MockHttpSession session;
    private UserForm newUser = new UserForm();
    private Customer newCustomer;
    private String newCustomerToken;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        session = new MockHttpSession();

        String password = "123ad你好fA@";
        newUser.setPassword(password);
        String username = "2___sadf";
        String email = "814775538@qq.com";
        newUser.setUsername(username);
        newUser.setEmail(email);
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(newUser))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        );
        newCustomer = customerMapper.selectByEmail(email);
        assert newCustomer != null;
        newCustomerToken = JWTUtil.getToken(newCustomer);
    }

    //    =================================================   //

    @Test
    public void registerSuccess() throws Exception {
        Random r = new Random(0);
        UserForm u = new UserForm();
        String password = "123ad你好fA@";
        u.setPassword(password);
        String[] usernames = {"aabaa", "aabaa.", "aabaa2", "AABAC", "AAbca", "123AAx", "123456", "1_____", "354_sdfg"};
        for (String username : usernames) {
            String email = r.nextInt(10000000) + "34a_a@test.mail.com";
            u.setUsername(username);
            u.setEmail(email);
            mvc.perform(MockMvcRequestBuilders.post("/user/register")
                    .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(u))
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value(username))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result.password").value(password))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result.email").value(email))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result.emailValid").value(false))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result.balance").value("0.0"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result.bonus").value("0.0"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result.joinTime").value(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result.lastLogin").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result.questionId").value(-1))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result.banned").value(false));
        }
    }

    @Test
    public void registerDuplicatedName() throws Exception {
        Random r = new Random(0);
        UserForm u = new UserForm();
        String username = newUser.getUsername();
        String password = "123adsfA@";
        u.setUsername(username);
        u.setPassword(password);
        u.setEmail(r.nextInt(10000000) + "34a_a@test.mail.com");
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(u))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(USER_EXITS));
    }

    @Test
    public void registerDuplicatedMail() throws Exception {
        Random r = new Random(0);
        UserForm u = new UserForm();
        String username = "a12345";
        String password = "123adsfA@";
        u.setUsername(username);
        u.setPassword(password);
        u.setEmail(newUser.getEmail());
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(u))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(USER_EXITS));
    }

    @Test
    public void registerSimpleUsername() throws Exception {
        Random r = new Random(0);
        UserForm u = new UserForm();
        String[] usernames = {"a", "!aaaaa", "2", "A", "aaaaa$", "bbgdfgx=", "!", "        ", "______"};
        String password = "123adsfA@";
        u.setPassword(password);
        for (String username : usernames) {
            String email = r.nextInt(10000000) + "34a_a@test.mail.com";
            u.setUsername(username);
            u.setEmail(email);
            mvc.perform(MockMvcRequestBuilders.post("/user/register")
                    .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(u))
                    .accept(MediaType.APPLICATION_JSON_UTF8).session(session)
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INVALID_NAME));
        }
    }

    @Test
    public void registerSimplePassword() throws Exception {
        Random r = new Random(0);
        UserForm u = new UserForm();
        String username = "aa0_sd";
        u.setUsername(username);
        String[] passwords = {"", " ", "12", "123123123123", "aaaaaaaaa", "AAAAAAAAA", "123asdfcV", "123asd_cV", "先擦的zxc"};
        for (String password : passwords) {
            String email = r.nextInt(10000000) + "34a_a@test.mail.com";
            u.setEmail(email);
            u.setPassword(password);
            mvc.perform(MockMvcRequestBuilders.post("/user/register")
                    .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(u))
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INVALID_PASSWORD));
        }
    }

    @Test
    public void registerEmpty() throws Exception {
        Random r = new Random(0);
        UserForm u = new UserForm();

        u.setUsername(null);
        String password = "123ad你好fA@";
        String email = r.nextInt(10000000) + "34a_a@test.mail.com";
        u.setEmail(email);
        u.setPassword(password);
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(u))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(SHOULD_NO_EMPTY));

        String username = "asdf436ASD";
        u.setUsername(username);
        u.setPassword(null);
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(u))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(SHOULD_NO_EMPTY));
    }

    //    =================================================   //

    @Test
    public void loginSuccess() throws Exception {
        UserForm u = new UserForm();
        u.setUsername(newCustomer.getUsername());
        u.setPassword(newCustomer.getPassword());
        mvc.perform(MockMvcRequestBuilders.post("/user/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(u))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value(newCustomer.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.email").value(newCustomer.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.emailValid").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.balance").value("0.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.bonus").value("0.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.joinTime").value(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.lastLogin").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.questionId").value(-1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.banned").value(false))
                .andExpect(MockMvcResultMatchers.header().string("token", newCustomerToken));
    }

    @Test
    public void loginFailureNamePwdError1() throws Exception {
        UserForm f = new UserForm();
        f.setUsername(newCustomer.getUsername() + "123");
        f.setPassword(newCustomer.getPassword());
        mvc.perform(MockMvcRequestBuilders.post("/user/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(f))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(NAME_PWD_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    @Test
    public void loginFailureNamePwdError2() throws Exception {
        UserForm f = new UserForm();
        f.setUsername(newCustomer.getUsername());
        f.setPassword(newCustomer.getPassword() + "123");
        mvc.perform(MockMvcRequestBuilders.post("/user/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(f))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(NAME_PWD_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    //    =================================================   //

    @Test
    public void verifySuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newCustomer.getCustomerId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isEmpty());
    }

    @Test
    public void verifyNotValid() throws Exception {
        Integer customId = 0;
        mvc.perform(MockMvcRequestBuilders.post("/user/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(customId))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INFO_ERROR));
    }

    @Test
    public void verified() throws Exception {
        newCustomer.setEmailValid(true);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        mvc.perform(MockMvcRequestBuilders.post("/user/verify")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newCustomer.getCustomerId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(VERIFIED));
    }

    //    =================================================   //
    @Test
    public void verifyUsernameSuccess() throws Exception {
        String[] usernames = {"aabaa", "aabaa.", "aabaa2", "AABAC", "AAbca", "123AAx", "123456", "1_____", "354_sdfg"};
        for (String username : usernames) {
            mvc.perform(MockMvcRequestBuilders.get("/user/verifyUsername")
                    .param("username", username)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result").isEmpty());
        }
    }

    @Test
    public void verifyIllegalUsername() throws Exception {
        String[] usernames = {"", " ", "a", "!aaaaa", "2", "A", "aaaaa$", "bbgdfgx=", "!", "        ", "______"};
        for (String username : usernames) {
            mvc.perform(MockMvcRequestBuilders.get("/user/verifyUsername")
                    .param("username", username)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INVALID_NAME));
        }
    }

    @Test
    public void verifyDuplicateUsername() throws Exception {
        String username = newUser.getUsername();
        mvc.perform(MockMvcRequestBuilders.get("/user/verifyUsername")
                .param("username", username)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(USER_EXITS));
    }

    //    =================================================   //
    @Test
    public void verifyEmailSuccess() throws Exception {
        verifySuccess();

        Scanner s = new Scanner(new File("verifyTemp"));
        final String url = s.nextLine();
        String[] params = new URL(url).getQuery().split("&");
        String email = params[0].split("=")[1];
        String code = params[1].split("=")[1];

        mvc.perform(MockMvcRequestBuilders.get("/user/verifyEmail")
                .param("email", email)
                .param("code", code)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isEmpty());
    }

    @Test
    public void verifyEmailMultipleTimes() throws Exception {
        verifySuccess();

        Scanner s = new Scanner(new File("verifyTemp"));
        final String url = s.nextLine();
        String[] params = new URL(url).getQuery().split("&");
        String email = params[0].split("=")[1];
        String code = params[1].split("=")[1];

        mvc.perform(MockMvcRequestBuilders.get("/user/verifyEmail")
                .param("email", email)
                .param("code", code)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        );

        mvc.perform(MockMvcRequestBuilders.get("/user/verifyEmail")
                .param("email", email)
                .param("code", code)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(VERIFIED));
    }

    @Test
    public void verifyEmailWrongEmail() throws Exception {
        verifySuccess();

        Scanner s = new Scanner(new File("verifyTemp"));
        final String url = s.nextLine();
        String[] params = new URL(url).getQuery().split("&");
        String email = params[0].split("=")[1];
        String code = params[1].split("=")[1];

        mvc.perform(MockMvcRequestBuilders.get("/user/verifyEmail")
                .param("email", email + "_wrong_")
                .param("code", code)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(VERIFY_INFO_ERROR));
    }

    @Test
    public void verifyEmailWrongCode() throws Exception {
        verifySuccess();

        Scanner s = new Scanner(new File("verifyTemp"));
        final String url = s.nextLine();
        String[] params = new URL(url).getQuery().split("&");
        String email = params[0].split("=")[1];
        String code = params[1].split("=")[1];

        mvc.perform(MockMvcRequestBuilders.get("/user/verifyEmail")
                .param("email", email)
                .param("code", "a" + code.substring(1))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(VERIFY_INFO_ERROR));
    }


    //    =================================================   //

    @Test
    public void forgetSuccess() throws Exception {
        verifyEmailSuccess();

        newUser.setUsername(null);
        newUser.setPassword(null);
        mvc.perform(MockMvcRequestBuilders.post("/user/forget")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newUser))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true));

        Scanner s = new Scanner(new File("forgetTemp"));
        final String url = s.nextLine();
        String[] params = new URL(url).getQuery().split("&");
        String email = params[1].split("=")[1];
        Assert.assertEquals(newCustomer.getEmail(), email);
    }

    @Test
    public void forgetWithSTH() throws Exception {
        UserForm u = new UserForm();
        String username = "aabaa";
        u.setUsername(username);
        String password = "123ad你好fA@";
        u.setPassword(password);
        String email = "814775538@qq.com";
        u.setEmail(email);
        mvc.perform(MockMvcRequestBuilders.post("/user/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(u)));

        mvc.perform(MockMvcRequestBuilders.post("/user/forget")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(u))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(SHOULD_EMPTY));

        u.setPassword(null);
        mvc.perform(MockMvcRequestBuilders.post("/user/forget")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(u))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(SHOULD_EMPTY));

        u.setUsername(null);
        u.setPassword("123ad你好fA@");
        mvc.perform(MockMvcRequestBuilders.post("/user/forget")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(u))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(SHOULD_EMPTY));

    }

    //    =================================================   //

    @Test
    public void findPasswordSuccess() throws Exception {
        forgetSuccess();

        Scanner s = new Scanner(new File("forgetTemp"));
        final String url = s.nextLine();
        String[] params = new URL(url).getQuery().split("&");
        int findId = Integer.parseInt(params[0].split("=")[1]);
        String email = params[1].split("=")[1];
        String code = params[2].split("=")[1];

        newUser.setPassword("1@#AXCVFgdg");
        newUser.setEmail(email);
        mvc.perform(MockMvcRequestBuilders.post("/user/findPassword")
                .param("findId", String.valueOf(findId))
                .param("email", email)
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newUser))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isEmpty());

        newCustomer.setPassword(newUser.getPassword());
        Assert.assertEquals(newCustomer, customerMapper.selectByPrimaryKey(newCustomer.getCustomerId()));
    }

    @Test
    public void findPasswordTwice() throws Exception {
        forgetSuccess();

        Scanner s = new Scanner(new File("forgetTemp"));
        final String url = s.nextLine();
        String[] params = new URL(url).getQuery().split("&");
        int findId = Integer.parseInt(params[0].split("=")[1]);
        String email = params[1].split("=")[1];
        String code = params[2].split("=")[1];

        newUser.setPassword("1@#AXCVFgdg");
        newUser.setEmail(email);
        newCustomer.setPassword(newUser.getPassword());
        mvc.perform(MockMvcRequestBuilders.post("/user/findPassword")
                .param("findId", String.valueOf(findId))
                .param("email", email)
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newUser))
        );

        newUser.setPassword("436@#AXCVFgdg");
        mvc.perform(MockMvcRequestBuilders.post("/user/findPassword")
                .param("findId", String.valueOf(findId))
                .param("email", email)
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newUser))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(VERIFY_INFO_ERROR));

        Assert.assertEquals(newCustomer, customerMapper.selectByPrimaryKey(newCustomer.getCustomerId()));
    }

    @Test
    public void findPasswordErrorMail() throws Exception {
        forgetSuccess();

        Scanner s = new Scanner(new File("forgetTemp"));
        final String url = s.nextLine();
        String[] params = new URL(url).getQuery().split("&");
        int findId = Integer.parseInt(params[0].split("=")[1]);
        String email = "3245464@test.mail.com";
        String code = params[2].split("=")[1];

        newUser.setPassword("436@#AXCVFgdg");
        newUser.setEmail(email);
        mvc.perform(MockMvcRequestBuilders.post("/user/findPassword")
                .param("findId", String.valueOf(findId))
                .param("email", email)
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newUser))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(VERIFY_INFO_ERROR));
    }

    @Test
    public void findPasswordErrorId() throws Exception {
        forgetSuccess();

        Scanner s = new Scanner(new File("forgetTemp"));
        final String url = s.nextLine();
        String[] params = new URL(url).getQuery().split("&");
        int findId = Integer.parseInt(params[0].split("=")[1]) + 1;
        String email = params[1].split("=")[1];
        String code = params[2].split("=")[1];

        newUser.setPassword("436@#AXCVFgdg");
        newUser.setEmail(email);
        mvc.perform(MockMvcRequestBuilders.post("/user/findPassword")
                .param("findId", String.valueOf(findId))
                .param("email", email)
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newUser))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(VERIFY_INFO_ERROR));
    }

    @Test
    public void findPasswordErrorCode() throws Exception {
        forgetSuccess();

        Scanner s = new Scanner(new File("forgetTemp"));
        final String url = s.nextLine();
        String[] params = new URL(url).getQuery().split("&");
        int findId = Integer.parseInt(params[0].split("=")[1]);
        String email = params[1].split("=")[1];
        String code = "123456789";

        newUser.setPassword("436@#AXCVFgdg");
        newUser.setEmail(email);
        mvc.perform(MockMvcRequestBuilders.post("/user/findPassword")
                .param("findId", String.valueOf(findId))
                .param("email", email)
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newUser))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(VERIFY_INFO_ERROR));

        code = params[2].split("=")[1].substring(1) + "z";
        mvc.perform(MockMvcRequestBuilders.post("/user/findPassword")
                .param("findId", String.valueOf(findId))
                .param("email", email)
                .param("code", code)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(newUser))
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(VERIFY_INFO_ERROR));
    }

    //    =================================================   //

    @Test
    public void addQuestionnaireSuccess() throws Exception {
        QuestionnaireForm form = new QuestionnaireForm();
        form.setCustomerId(newCustomer.getCustomerId());
        ArrayList<Character> answerList = new ArrayList<>();
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        for (char c : chars) {
            answerList.add(c);
        }
        form.setAnswerList(answerList);
        Questionnaire questionnaire = new Questionnaire(form);
        mvc.perform(MockMvcRequestBuilders.post("/user/questionnaire/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(form))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.questionnaireId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.score").value(questionnaire.getScore()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.customerId").value(questionnaire.getCustomerId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.answers").value(JSON.parse(JSON.toJSONString(form.getAnswerList()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.lastUpdate").exists());
        Questionnaire questionnaire1 = customerMapper.selectQuestionnaireByCustomerId(newCustomer.getCustomerId());
        Assert.assertTrue(Duration.between(LocalDateTime.now(), questionnaire1.getLastUpdate()).toMinutes() < 10);
    }

    @Test
    public void addQuestionnaireExisted() throws Exception {
        QuestionnaireForm form = new QuestionnaireForm();
        form.setCustomerId(newCustomer.getCustomerId());
        ArrayList<Character> answerList = new ArrayList<>();
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        for (char c : chars) {
            answerList.add(c);
        }
        form.setAnswerList(answerList);
        Questionnaire questionnaire = new Questionnaire(form);
        customerMapper.insertQuestionnaire(questionnaire);
        mvc.perform(MockMvcRequestBuilders.post("/user/questionnaire/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(form))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INFO_ERROR));
    }

    @Test
    public void addQuestionnaireAnswersIncomplete() throws Exception {
        QuestionnaireForm form = new QuestionnaireForm();
        form.setCustomerId(newCustomer.getCustomerId());
        ArrayList<Character> answerList = new ArrayList<>();
        char[] chars = new char[]{'A', 'B', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        for (char c : chars) {
            answerList.add(c);
        }
        form.setAnswerList(answerList);
        Questionnaire questionnaire = new Questionnaire(form);
        customerMapper.insertQuestionnaire(questionnaire);
        mvc.perform(MockMvcRequestBuilders.post("/user/questionnaire/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(form))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INFO_ERROR));
    }

    @Test
    public void addQuestionnaireNoCustomerId() throws Exception {
        QuestionnaireForm form = new QuestionnaireForm();
        ArrayList<Character> answerList = new ArrayList<>();
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        for (char c : chars) {
            answerList.add(c);
        }
        form.setAnswerList(answerList);
        mvc.perform(MockMvcRequestBuilders.post("/user/questionnaire/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(form))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INFO_ERROR));
    }

    @Test
    public void addQuestionnaireCustomerNonexistent() throws Exception {
        QuestionnaireForm form = new QuestionnaireForm();
        form.setCustomerId(-1);
        ArrayList<Character> answerList = new ArrayList<>();
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        for (char c : chars) {
            answerList.add(c);
        }
        form.setAnswerList(answerList);
        mvc.perform(MockMvcRequestBuilders.post("/user/questionnaire/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(form))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INFO_ERROR));
    }

    @Test
    public void addQuestionnaireAnswersLowercase() throws Exception {
        QuestionnaireForm form = new QuestionnaireForm();
        form.setCustomerId(newCustomer.getCustomerId());
        ArrayList<Character> answerList = new ArrayList<>();
        char[] chars = new char[]{'A', 'b', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        for (char c : chars) {
            answerList.add(c);
        }
        form.setAnswerList(answerList);
        mvc.perform(MockMvcRequestBuilders.post("/user/questionnaire/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(form))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INFO_ERROR));
    }

    @Test
    public void addQuestionnaireAnswersOutOfRange1() throws Exception {
        QuestionnaireForm form = new QuestionnaireForm();
        form.setCustomerId(newCustomer.getCustomerId());
        ArrayList<Character> answerList = new ArrayList<>();
        char[] chars = new char[]{'A', 'b', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        for (char c : chars) {
            answerList.add(c);
        }
        form.setAnswerList(answerList);
        mvc.perform(MockMvcRequestBuilders.post("/user/questionnaire/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(form))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INFO_ERROR));
    }

    @Test
    public void addQuestionnaireAnswersOutOfRange2() throws Exception {
        QuestionnaireForm form = new QuestionnaireForm();
        form.setCustomerId(newCustomer.getCustomerId());
        ArrayList<Character> answerList = new ArrayList<>();
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', '&', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        for (char c : chars) {
            answerList.add(c);
        }
        form.setAnswerList(answerList);
        mvc.perform(MockMvcRequestBuilders.post("/user/questionnaire/add")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(form))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INFO_ERROR));
    }

    @Test
    public void getQuestionnaireSuccess() throws Exception {
        QuestionnaireForm form = new QuestionnaireForm();
        form.setCustomerId(newCustomer.getCustomerId());
        ArrayList<Character> answerList = new ArrayList<>();
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        for (char c : chars) {
            answerList.add(c);
        }
        form.setAnswerList(answerList);
        Questionnaire questionnaire = new Questionnaire(form);
        customerMapper.insertQuestionnaire(questionnaire);
        mvc.perform(MockMvcRequestBuilders.get("/user/questionnaire/get")
                .param("customerId", newCustomer.getCustomerId() + "")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.questionnaireId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.score").value(questionnaire.getScore()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.customerId").value(questionnaire.getCustomerId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.answers").value(JSON.parse(JSON.toJSONString(form.getAnswerList()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.lastUpdate").exists());
    }

    @Test
    public void getQuestionnaireCustomerNonexistent() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/questionnaire/get")
                .param("customerId", "-1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INFO_ERROR));
    }

    @Test
    public void getQuestionnaireNonexistent() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/questionnaire/get")
                .param("customerId", newCustomer.getCustomerId() + "")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(NO_QUESTIONNAIRE));
    }

    @Test
    public void updateQuestionnaireSuccess() throws Exception {
        QuestionnaireForm form = new QuestionnaireForm();
        form.setCustomerId(newCustomer.getCustomerId());
        ArrayList<Character> answerList = new ArrayList<>();
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        for (char c : chars) {
            answerList.add(c);
        }
        form.setAnswerList(answerList);
        Questionnaire questionnaire = new Questionnaire(form);
        customerMapper.insertQuestionnaire(questionnaire);
        answerList.remove(3);
        answerList.add(3, 'D');
        mvc.perform(MockMvcRequestBuilders.post("/user/questionnaire/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(form))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.questionnaireId").value(questionnaire.getQuestionnaireId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.score").value(28))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.customerId").value(questionnaire.getCustomerId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.answers").value(JSON.parse(JSON.toJSONString(form.getAnswerList()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.lastUpdate").exists());
        Questionnaire questionnaire1 = customerMapper.selectQuestionnaireByCustomerId(newCustomer.getCustomerId());
        Assert.assertTrue(Duration.between(LocalDateTime.now(), questionnaire1.getLastUpdate()).toMinutes() < 10);
    }

    @Test
    public void updateQuestionnaireNonexistent() throws Exception {
        QuestionnaireForm form = new QuestionnaireForm();
        form.setCustomerId(newCustomer.getCustomerId());
        ArrayList<Character> answerList = new ArrayList<>();
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        for (char c : chars) {
            answerList.add(c);
        }
        form.setAnswerList(answerList);
        mvc.perform(MockMvcRequestBuilders.post("/user/questionnaire/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(form))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INFO_ERROR));
    }

    //    =================================================   //
    @Test
    public void changePasswordSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/changePassword")
                .param("userId", newCustomer.getCustomerId().toString())
                .param("newPassword", "A123456789@aa")
                .param("oldPassword", "123ad你好fA@")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("true"));
    }

//    密码输入错误

    @Test
    public void changePasswordErrorPassword() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/changePassword")
                .param("userId", newCustomer.getCustomerId().toString())
                .param("newPassword", "123456789")
                .param("oldPassword", "123ad你好f")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INVALID_PASS));
    }

    @Test
    public void changePasswordInvalidPassword() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/changePassword")
                .param("userId", newCustomer.getCustomerId().toString())
                .param("newPassword", "123456789")
                .param("oldPassword", "123ad你好fA@")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INVALID_PASS));
    }

    //    =================================================   //
    @Test
    public void changeEmailSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/changeEmail")
                .param("userId", newCustomer.getCustomerId().toString())
                .param("newEmail", "123456789@qq.com")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("true"));
    }

    /**
     * @throws Exception 邮箱格式错误
     */
    @Test
    public void changeEmailInvalidEmail() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/changeEmail")
                .param("userId", newCustomer.getCustomerId().toString())
                .param("newEmail", "123456789")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(INVALID_EMAIL));
    }

    /**
     * @throws Exception 邮箱已经存在
     */
    @Test
    public void changeEmailExitsError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/changeEmail")
                .param("userId", newCustomer.getCustomerId().toString())
                .param("newEmail", "814775538@qq.com")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", newCustomerToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(USER_EXITS));
    }
}