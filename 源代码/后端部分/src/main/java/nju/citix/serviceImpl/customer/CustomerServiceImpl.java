package nju.citix.serviceImpl.customer;

import nju.citix.config.AlipayConfig;
import nju.citix.config.ServerConfiguration;
import nju.citix.dao.customer.CustomerMapper;
import nju.citix.dao.customer.FindKeyMapper;
import nju.citix.po.Customer;
import nju.citix.po.FindKey;
import nju.citix.po.Questionnaire;
import nju.citix.service.customer.CustomerForService;
import nju.citix.service.customer.CustomerService;
import nju.citix.service.fund.FundForService;
import nju.citix.utils.Md5Utils;
import nju.citix.vo.QuestionnaireForm;
import nju.citix.vo.QuestionnaireVO;
import nju.citix.vo.UserForm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static nju.citix.controller.customer.CustomerController.INFO_ERROR;

/**
 * @author DW
 */
@Service
public class CustomerServiceImpl implements CustomerService, CustomerForService {
    private static final String SALT = "GCTSwlEXdm8j";
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private FindKeyMapper findKeyMapper;
    @Resource
    private FundForService fundForService;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private ServerConfiguration serverConfiguration;
    @Resource
    private Environment environment;

    @Override
    @NotNull
    @Transactional(rollbackFor = DuplicateKeyException.class)
    public Customer register(UserForm userForm) {
        Customer record = new Customer();
        record.setUsername(userForm.getUsername());
        record.setPassword(userForm.getPassword());
        record.setEmail(userForm.getEmail());
        customerMapper.insert(record);
        return Objects.requireNonNull(customerMapper.selectByPrimaryKey(record.getCustomerId()));
    }

    @Override
    public String forget(String email) throws MessagingException {
        Customer c = customerMapper.selectByEmail(email);
        if (c == null || c.getBanned() || !c.getEmailValid()) {
            LOGGER.debug("用户状态校验失败：不存在，被禁用或者邮箱未验证");
            return null;
        }
        //强制过期未使用的验证
        FindKey findKey = findKeyMapper.selectByEmail(email);
        if (findKey != null) {
            findKeyMapper.updateUsedByFindId(findKey.getFindId());
        }

        Calendar calendar = Calendar.getInstance();
        findKey = new FindKey();
        findKey.setEmail(c.getEmail());
        findKey.setRequestTime(String.valueOf(calendar.getTimeInMillis()));
        findKeyMapper.insert(findKey);
        String code = Md5Utils.encode(email, String.valueOf(calendar.getTimeInMillis()).substring(0, 12));
        String resetUrl = serverConfiguration.getUrl() + "/user/findPassword?findId=" + findKey.getFindId() +
                "&email=" + findKey.getEmail() + "&code=" + code;

        if ("prod".equals(environment.getProperty("spring.profiles"))) {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setTo(email);
            messageHelper.setFrom(Objects.requireNonNull(environment.getProperty("spring.mail.username")));
            messageHelper.setSubject("找回您的密码");
            messageHelper.setText("<html><body>您好:<br>\n" +
                    "您正在进行<span style='color:red;'>找回密码</span>操作,请点击如下链接重设您的密码。<br>\n" +
                    "<a href='" + resetUrl + "'>重置密码</a>,<br>" +
                    "如果您之前已经发送过密码更改请求，则以本邮件内的链接为准。本链接在<span style='color:red;'>30分钟</span>内有效。</body></html>", true);
            javaMailSender.send(message);
        } else {
            try {
                File f = new File("forgetTemp");
                FileWriter fileWriter = new FileWriter(f, false);
                fileWriter.write(resetUrl);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                LOGGER.error("文件写入异常");
            }
        }
        return resetUrl;
    }

    @Override
    public boolean findPassword(int findId, String email, String code, UserForm userForm) {
        FindKey findKey = findKeyMapper.selectByEmail(email);
        if (findKey == null || findKey.getFindId() != findId) {
            LOGGER.debug("id或使用状态校验失败");
            return false;
        }
        if (!Md5Utils.check(code, email, findKey.getRequestTime().substring(0, 12))) {
            LOGGER.debug("校验码校验失败");
            return false;
        }

        findKeyMapper.updateUsedByFindId(findId);
        customerMapper.updatePasswordByEmail(userForm.getPassword(), email);

        return true;
    }

    @Override
    public String verify(int customId) throws MessagingException {
        Customer c = customerMapper.selectByPrimaryKey(customId);
        Assert.notNull(c, INFO_ERROR);
        Assert.isTrue(!c.getBanned(), INFO_ERROR);

        if (c.getEmailValid()) {
            LOGGER.debug("邮箱已经被验证");
            return null;
        }

        String code = Md5Utils.encode(c.getEmail(), SALT);
        String verifyUrl = AlipayConfig.FRONT_URL +"/app/verify?email=" +
                c.getEmail() + "&code=" + code;

        if ("prod".equals(environment.getProperty("spring.profiles"))) {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setTo(c.getEmail());
            messageHelper.setFrom(AlipayConfig.FROM_EMAIL);
            messageHelper.setSubject("验证您的邮箱");
            messageHelper.setText("<html><body>您好:<br>\n" +
                    "您正在进行<span style='color:red;'>验证邮箱</span>操作,请点击如下链接验证您的邮箱。<br>\n" +
                    "<a href='" +
                    verifyUrl +
                    "'>验证邮箱</a></body></html>", true);
            javaMailSender.send(message);
        } else {
            try {
                File f = new File("verifyTemp");
                FileWriter fileWriter = new FileWriter(f, false);
                fileWriter.write(verifyUrl);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                LOGGER.error("文件写入异常");
            }
        }

        return verifyUrl;
    }

    @Override
    @Nullable
    public Customer login(String username, String password, String ip) {
        Customer customer = customerMapper.selectByUsername(username);
        if (customer != null && password.equals(customer.getPassword()) && !customer.getBanned()) {
            customer.setLastLogin(LocalDateTime.now());
            customer.setIp(ip);
            customerMapper.updateByPrimaryKeySelective(customer);
            return customer;
        } else {
            return null;
        }
    }

    @Override
    @Nullable
    public Customer findCustomerById(int id) {
        return customerMapper.selectByPrimaryKey(id);
    }

    @Override
    public QuestionnaireVO addQuestionnaire(QuestionnaireForm form) {
        Customer customer = customerMapper.selectByPrimaryKey(form.getCustomerId());
        if (customer == null) {
            return null;
        }
        Questionnaire questionnaire = new Questionnaire(form);
        customerMapper.insertQuestionnaire(questionnaire);
        return new QuestionnaireVO(questionnaire);
    }

    @Override
    public QuestionnaireVO updateQuestionnaire(QuestionnaireForm form) {
        Questionnaire questionnaire = customerMapper.selectQuestionnaireByCustomerId(form.getCustomerId());
        if (questionnaire == null) {
            return null;
        }
        questionnaire.updateAnswersByForm(form);
        customerMapper.updateQuestionnaire(questionnaire);
        return new QuestionnaireVO(questionnaire);
    }

    @Override
    public QuestionnaireVO findQuestionnaireByCustomerId(Integer customerId) {
        Customer customer = customerMapper.selectByPrimaryKey(customerId);
        if (customer == null) {
            return null;
        }
        Questionnaire questionnaire = customerMapper.selectQuestionnaireByCustomerId(customerId);
        if (questionnaire == null) {
            return null;
        }
        return new QuestionnaireVO(questionnaire);
    }

    @Override
    public boolean verifyUsername(String username) {
        Customer customer = customerMapper.selectByUsername(username);
        return customer == null;
    }

    @Override
    public int verifyEmail(String email, String code) {
        if (email == null || code == null || !Md5Utils.check(code, email, SALT)) {
            LOGGER.debug("验证失败");
            return 2;
        }
        Customer customer = customerMapper.selectByEmail(email);
        Assert.notNull(customer, "该邮箱不存在");

        if (customer.getEmailValid()) {
            LOGGER.debug("邮箱已经被验证过");
            return 1;
        }
        customer.setEmailValid(true);
        customerMapper.updateByPrimaryKeySelective(customer);

        return 0;
    }

    @Override
    public int changePasswordById(int id, String newPassword, String oldPassword) {
        Customer newCustomer = customerMapper.selectByPrimaryKey(id);

        if (!oldPassword.equals(newCustomer.getPassword())) {
            return -1;
        }
        newCustomer.setPassword(newPassword);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        return id;
    }

    @Override
    public int changeEmailById(int id, String newEmail) {

        Customer newCustomer = customerMapper.selectByPrimaryKey(id);

//        邮箱已经注册
        if (customerMapper.selectByEmail(newEmail) != null) {
            return -2;
        }
        newCustomer.setEmail(newEmail);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        return id;
    }

    @Override
    public Boolean judgeOldCustomer(Customer customer) {
        return !fundForService.getRecommendListByUserId(customer.getCustomerId()).isEmpty();
    }

    @Override
    public Customer viewCustomerInfo(Integer customerId) {
        return customerMapper.selectByPrimaryKey(customerId);
    }

    @Override
    public boolean banUser(Integer customerId) {
        Customer customer = customerMapper.selectByPrimaryKey(customerId);
        if (customer == null)
            return false;
        customer.setBanned(true);
        return customerMapper.updateByPrimaryKeySelective(customer) != 0;
    }

    @Override
    public boolean releaseUser(Integer customerId) {
        Customer customer = customerMapper.selectByPrimaryKey(customerId);
        if (customer == null)
            return false;
        customer.setBanned(false);
        return customerMapper.updateByPrimaryKeySelective(customer) != 0;
    }

    @Override
    public List<Customer> viewAllCustomers(Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        Integer rowsNum = pageSize;
        return customerMapper.selectAllByPage(offset, rowsNum);
    }
}
