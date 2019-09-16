package nju.citix.controller.customer;

import nju.citix.annotation.Authority;
import nju.citix.annotation.UserLoginToken;
import nju.citix.po.Customer;
import nju.citix.service.customer.CustomerService;
import nju.citix.utils.IPUtil;
import nju.citix.utils.JWTUtil;
import nju.citix.vo.QuestionnaireForm;
import nju.citix.vo.QuestionnaireVO;
import nju.citix.vo.Response;
import nju.citix.vo.UserForm;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.regex.Pattern;

/**
 * @author DW
 * @date 2019/8/12
 */
@UserLoginToken
@RestController
@RequestMapping("/user")
public class CustomerController {
    public static final String NO_QUESTIONNAIRE = "用户名无效";
    public static final String INVALID_NAME = "用户名无效";
    public static final String INVALID_PASS = "密码格式错误";
    public static final String INVALID_PASSWORD = "密码过于简单";
    public static final String USER_EXITS = "用户名或邮箱已经存在";
    public static final String INVALID_EMAIL = "邮箱格式错误";
    public static final String SHOULD_NO_EMPTY = "用户信息不得为空";
    public static final String SHOULD_EMPTY = "服务器错误，请重试";
    public static final String INFO_ERROR = "用户信息异常";
    public static final String UNKNOWN_ERROR = "服务器发生未知错误，请稍后再试";
    public static final String VERIFIED = "已经验证过，请勿重复验证";
    public static final String VERIFY_INFO_ERROR = "验证链接错误或者已经过期，请重新验证";
    public static final String NAME_PWD_ERROR = "用户名或密码错误";
    public static final String INVALID_AMOUNT = "充值金额不能为负数";
    private static final int NUM_OF_QUESTION = 18;
    private static final String OLD = "old";
    private static final String NEW = "new";

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * 在用户注册时动态的验证用户名是否可用
     */
    @Authority(JWTUtil.ALL)
    @GetMapping("/verifyUsername")
    public Response verifyUsername(@RequestParam("username") String username) {
        Assert.isTrue(!StringUtils.isEmpty(username), INVALID_NAME);
        Assert.isTrue(Pattern.matches("^[A-Za-z0-9][A-Za-z0-9_.]{4,14}$", username), INVALID_NAME);

        if (customerService.verifyUsername(username)) {
            return Response.buildSuccess();
        } else {
            return Response.buildFailure(USER_EXITS);
        }
    }

    /**
     * 用户注册
     */
    @Authority(JWTUtil.ALL)
    @PostMapping("/register")
    public Response register(@Valid @RequestBody UserForm userForm) {
        Assert.isTrue(!StringUtils.isEmpty(userForm.getUsername()), SHOULD_NO_EMPTY);
        Assert.isTrue(!StringUtils.isEmpty(userForm.getPassword()), SHOULD_NO_EMPTY);
        Assert.isTrue(!StringUtils.isEmpty(userForm.getEmail()), SHOULD_NO_EMPTY);

        try {
            return Response.buildSuccess(customerService.register(userForm));
        } catch (DuplicateKeyException e) {
            return Response.buildFailure(USER_EXITS);
        }
    }

    /**
     * 用户点击忘记密码，提供邮箱发送重置链接
     */
    @Authority(JWTUtil.ALL)
    @PostMapping("/forget")
    public Response forget(@Valid @RequestBody UserForm userForm) {
        Assert.isTrue(StringUtils.isEmpty(userForm.getUsername()), SHOULD_EMPTY);
        Assert.isTrue(StringUtils.isEmpty(userForm.getPassword()), SHOULD_EMPTY);

        try {
            customerService.forget(userForm.getEmail());
            return Response.buildSuccess();
        } catch (MessagingException e) {
            return Response.buildFailure(UNKNOWN_ERROR);
        }
    }

    /**
     * 用户点击邮箱中重置密码的链接重置密码
     */
    @Authority(JWTUtil.ALL)
    @PostMapping("/findPassword")
    public Response findPassword(@RequestParam("findId") Integer findId, @RequestParam("email") String email, @RequestParam("code") String code, @Valid @RequestBody UserForm userForm) {
        Assert.hasLength(email, VERIFY_INFO_ERROR);
        Assert.hasLength(code, VERIFY_INFO_ERROR);
        Assert.isTrue(code.length() == 44, VERIFY_INFO_ERROR);
        Assert.isNull(userForm.getUsername(), VERIFY_INFO_ERROR);

        if (customerService.findPassword(findId, email, code, userForm)) {
            return Response.buildSuccess();
        } else {
            return Response.buildFailure(VERIFY_INFO_ERROR);
        }
    }

    /**
     * 用户点击验证邮箱，发送验证链接到邮箱
     */
    @Authority(JWTUtil.CUSTOMER)
    @PostMapping("/verify")
    public Response verify(@RequestBody Integer customId) {
        Assert.notNull(customId, SHOULD_NO_EMPTY);

        try {
            if (customerService.verify(customId) != null) {
                return Response.buildSuccess();
            } else {
                return Response.buildFailure(VERIFIED);
            }
        } catch (MessagingException e) {
            return Response.buildFailure(UNKNOWN_ERROR);
        }
    }

    /**
     * 用户点击邮箱中收到的链接确认验证邮箱
     */
    @Authority(JWTUtil.ALL)
    @GetMapping("/verifyEmail")
    public Response verifyEmail(@RequestParam("email") String email, @RequestParam("code") String code) {
        Assert.hasLength(email, VERIFY_INFO_ERROR);
        Assert.hasLength(code, VERIFY_INFO_ERROR);
        Assert.isTrue(code.length() == 44, VERIFY_INFO_ERROR);

        int result = customerService.verifyEmail(email, code);
        if (result == 1) {
            return Response.buildFailure(VERIFIED);
        } else if (result == 2) {
            return Response.buildFailure(VERIFY_INFO_ERROR);
        }

        return Response.buildSuccess();
    }

    /**
     * 用户登录
     */
    @Authority(JWTUtil.ALL)
    @PostMapping("/login")
    public Response login(@RequestBody UserForm userForm, HttpServletRequest request, HttpServletResponse response) {
        Assert.isNull(userForm.getEmail(), SHOULD_EMPTY);
        Assert.notNull(userForm.getUsername(), SHOULD_NO_EMPTY);
        Assert.notNull(userForm.getPassword(), SHOULD_NO_EMPTY);

        String ip = IPUtil.getIpAddr(request);
        Assert.notNull(ip, SHOULD_NO_EMPTY);

        Customer customer = customerService.login(userForm.getUsername(), userForm.getPassword(), ip);
        if (customer == null) {
            return Response.buildFailure(NAME_PWD_ERROR);
        } else {
            response.setHeader("token", JWTUtil.getToken(customer));
            response.setHeader("isOldCustomer", customerService.judgeOldCustomer(customer) ? OLD : NEW);
            customer.setPassword(null);
        }
        return Response.buildSuccess(customer);
    }

    /**
     * 新用户填写问卷
     */
    @Authority(JWTUtil.CUSTOMER)
    @PostMapping("/questionnaire/add")
    public Response addQuestionnaire(@RequestBody QuestionnaireForm form) {
        Assert.isTrue(checkForm(form), INFO_ERROR);
        Assert.isNull(customerService.findQuestionnaireByCustomerId(form.getCustomerId()), INFO_ERROR);
        QuestionnaireVO questionnaire = customerService.addQuestionnaire(form);
        return questionnaire != null ? Response.buildSuccess(questionnaire) : Response.buildFailure(INFO_ERROR);
    }

    /**
     * 老用户重新填写问卷
     */
    @Authority(JWTUtil.CUSTOMER)
    @PostMapping("/questionnaire/update")
    public Response updateQuestionnaire(@RequestBody QuestionnaireForm form) {
        Assert.isTrue(checkForm(form), INFO_ERROR);
        QuestionnaireVO questionnaire = customerService.updateQuestionnaire(form);
        return questionnaire != null ? Response.buildSuccess(questionnaire) : Response.buildFailure(INFO_ERROR);
    }

    private boolean checkForm(QuestionnaireForm form) {
        if (form.getCustomerId() == null || customerService.findCustomerById(form.getCustomerId()) == null || form.getAnswerList().size() != NUM_OF_QUESTION) {
            return false;
        }
        for (int i = 0; i < NUM_OF_QUESTION; i++) {
            Character c = form.getAnswerList().get(i);
            if (!(c != null && c >= 'A' && c <= 'E')) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取用户填写的问卷
     */
    @Authority(JWTUtil.CUSTOMER)
    @GetMapping("/questionnaire/get")
    public Response getQuestionnaire(@RequestParam Integer customerId) {
        Assert.notNull(customerId, SHOULD_NO_EMPTY);
        Assert.notNull(customerService.findCustomerById(customerId), INFO_ERROR);
        QuestionnaireVO questionnaire = customerService.findQuestionnaireByCustomerId(customerId);
        return Response.buildSuccess(questionnaire != null ? questionnaire : NO_QUESTIONNAIRE);
    }

    /**
     * 用户修改密码
     */
    @Authority(JWTUtil.CUSTOMER)
    @PostMapping("/changePassword")
    public Response changePassword(@RequestParam("userId") Integer userId,
                                   @RequestParam("newPassword") String newPassword,
                                   @RequestParam("oldPassword") String oldPassword) {
        String pattern = "^(?![A-Za-z0-9_]+$)(?![a-z0-9\\W]+$)(?![A-Za-z\\W]+$)(?![A-Z0-9\\W]+$).{8,20}$";
        if (!Pattern.matches(pattern, newPassword)) {
            return Response.buildFailure(INVALID_PASS);
        }
        if (customerService.findCustomerById(userId) == null) {
            return Response.buildFailure(INFO_ERROR);
        } else if (!oldPassword.equals(customerService.findCustomerById(userId).getPassword())) {
            return Response.buildFailure(NAME_PWD_ERROR);
        }
        customerService.changePasswordById(userId, newPassword, oldPassword);
        return Response.buildSuccess();
    }

    /**
     * 用户修改邮箱
     */
    @Authority(JWTUtil.CUSTOMER)
    @PostMapping("/changeEmail")
    public Response changeEmail(@RequestParam("userId") Integer userId, @RequestParam("newEmail") String newEmail) {
        String pattern = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

//        邮箱格式错误
        if (!Pattern.matches(pattern, newEmail)) {
            return Response.buildFailure(INVALID_EMAIL);
        }

        if (customerService.findCustomerById(userId) == null) {
            return Response.buildFailure(INFO_ERROR);
        }

        int flag = customerService.changeEmailById(userId, newEmail);
        if (flag == -2) {
            return Response.buildFailure(USER_EXITS);
        }
        return Response.buildSuccess();
    }
}
