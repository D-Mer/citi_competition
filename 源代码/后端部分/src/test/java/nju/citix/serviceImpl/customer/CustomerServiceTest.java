package nju.citix.serviceImpl.customer;

import nju.citix.controller.customer.CustomerController;
import nju.citix.dao.customer.CustomerMapper;
import nju.citix.po.Customer;
import nju.citix.service.customer.CustomerService;
import nju.citix.vo.QuestionnaireForm;
import nju.citix.vo.QuestionnaireVO;
import nju.citix.vo.UserForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CustomerServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerMapper customerMapper;
    private UserForm newUser = new UserForm();
    private Customer newCustomer;
    private String requestIp = "192.168.10.12";
    private LocalDateTime testStartTime = LocalDateTime.now();

    @Before
    public void setUp() throws Exception {
        String username = "123abc";
        String password = "123adsfA";
        String email = "814775538@qq.com";
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newCustomer = customerService.register(newUser);
        newCustomer = customerService.login(username, password, requestIp);
    }

    //    =================================================   //

    @Test
    public void verifyUsernameSuccess() {
        Assert.assertTrue(customerService.verifyUsername(newCustomer.getUsername() + "_success_"));
    }

    @Test
    public void verifyDuplicateUsername() {
        Assert.assertFalse(customerService.verifyUsername(newCustomer.getUsername()));
    }

    //    =================================================   //

    @Test
    public void registerCorrect() {
        assertEquals(newUser.getUsername(), newCustomer.getUsername());
        assertEquals(newUser.getPassword(), newCustomer.getPassword());
        assertEquals(newUser.getEmail(), newCustomer.getEmail());
        assertEquals(false, newCustomer.getEmailValid());
        assertEquals(newCustomer.getBalance(), new BigDecimal("0.00"));
        assertEquals(newCustomer.getBonus(), new BigDecimal("0.00"));
        assertNotNull(newCustomer.getJoinTime());
        assertNotNull(newCustomer.getLastLogin());
        assertEquals(-1L, newCustomer.getQuestionId().longValue());
        assertFalse(newCustomer.getBanned());
        Customer actual = customerMapper.selectByPrimaryKey(newCustomer.getCustomerId());
        assertEquals(newCustomer, actual);
    }

    @Test(expected = DuplicateKeyException.class)
    public void registerDuplicateUsername() {
        UserForm u = new UserForm();
        u.setUsername(newUser.getUsername());
        u.setPassword("123adsfA");
        u.setEmail("21341234@test.mail.com");
        customerService.register(u);
    }

    @Test(expected = DuplicateKeyException.class)
    public void registerDuplicateMail() {
        UserForm u = new UserForm();
        u.setUsername("aaaaa");
        u.setPassword("123adsfA");
        u.setEmail(newUser.getEmail());
        customerService.register(u);
    }

    //    =================================================   //

    @Test
    public void loginSuccess() {
        Customer customer = customerService.login(newUser.getUsername(), newUser.getPassword(), requestIp);
        Assert.assertEquals(newCustomer.getCustomerId(), customer.getCustomerId());
        Assert.assertEquals(newCustomer.getUsername(), customer.getUsername());
        Assert.assertEquals(newCustomer.getPassword(), customer.getPassword());
        Assert.assertEquals(newCustomer.getEmail(), customer.getEmail());
        Assert.assertEquals(newCustomer.getEmailValid(), customer.getEmailValid());
        Assert.assertEquals(newCustomer.getBalance(), customer.getBalance());
        Assert.assertEquals(newCustomer.getBonus(), customer.getBonus());
        Assert.assertEquals(newCustomer.getQuestionId(), customer.getQuestionId());
        Assert.assertEquals(newCustomer.getJoinTime(), customer.getJoinTime());
        Assert.assertEquals(requestIp, customer.getIp());
        Assert.assertEquals(newCustomer.getBanned(), customer.getBanned());
        Assert.assertTrue(Duration.between(testStartTime, customer.getLastLogin()).toMinutes() < 10);
    }

    @Test
    public void loginFailureNonexistentTest() {
        Customer customer = customerService.login("nonexistence", "123456", requestIp);
        Assert.assertNull(customer);
    }

    @Test
    public void loginFailureNamePwdErrorTest1() {
        Customer customer = customerService.login(newUser.getUsername() + "123", newUser.getPassword(), requestIp);
        Assert.assertNull(customer);
    }

    @Test
    public void loginFailureNamePwdErrorTest2() {
        Customer customer = customerService.login(newUser.getUsername(), newUser.getPassword() + "123", requestIp);
        Assert.assertNull(customer);
    }

    @Test
    public void loginFailureBannedTest() {
        newCustomer.setBanned(true);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        Customer customer = customerService.login(newUser.getUsername(), newUser.getPassword(), requestIp);
        Assert.assertNull(customer);
    }

    //    =================================================   //

    @Test
    public void verifyCorrect() throws MessagingException {
        assertNotNull(customerService.verify(newCustomer.getCustomerId()));
    }

    @Test
    public void verifyNoUser() throws MessagingException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(CustomerController.INFO_ERROR);

        customerService.verify(-10);
    }

    @Test
    public void verifyBanned() throws MessagingException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(CustomerController.INFO_ERROR);

        newCustomer.setBanned(true);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        customerService.verify(newCustomer.getCustomerId());
    }

    @Test
    public void verified() throws MessagingException {
        newCustomer.setEmailValid(true);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        assertNull(customerService.verify(newCustomer.getCustomerId()));
    }

    //    =================================================   //

    @Test
    public void verifyEmailCorrect() throws MessagingException, MalformedURLException, FileNotFoundException {
        final String url = customerService.verify(newCustomer.getCustomerId());
        String[] params = new URL(url).getQuery().split("&");
        String email = params[0].split("=")[1];
        String code = params[1].split("=")[1];
        customerService.verifyEmail(email, code);
        assertTrue(Objects.requireNonNull(customerMapper.selectByPrimaryKey(newCustomer.getCustomerId())).getEmailValid());
    }

    @Test
    public void verifyEmailMultipleTimes() throws MessagingException, MalformedURLException, FileNotFoundException {
        final String url = customerService.verify(newCustomer.getCustomerId());
        String[] params = new URL(url).getQuery().split("&");
        String email = params[0].split("=")[1];
        String code = params[1].split("=")[1];
        customerService.verifyEmail(email, code);
        assertEquals(1, customerService.verifyEmail(email, code));
    }

    @Test
    public void verifyEmailWrongEmail() throws MessagingException, MalformedURLException, FileNotFoundException {
        final String url = customerService.verify(newCustomer.getCustomerId());
        String[] params = new URL(url).getQuery().split("&");
        String email = params[0].split("=")[1];
        String code = params[1].split("=")[1];
        customerService.verifyEmail(email + "_wrong_", code);
        assertFalse(Objects.requireNonNull(customerMapper.selectByPrimaryKey(newCustomer.getCustomerId())).getEmailValid());
    }

    @Test
    public void verifyEmailWrongCode() throws MessagingException, MalformedURLException, FileNotFoundException {
        final String url = customerService.verify(newCustomer.getCustomerId());
        String[] params = new URL(url).getQuery().split("&");
        String email = params[0].split("=")[1];
        String code = params[1].split("=")[1];
        assertEquals(2, customerService.verifyEmail(email, "a" + code.substring(1)));
    }

    //    =================================================   //

    @Test
    public void findCorrect() throws MessagingException {
        newCustomer.setEmailValid(true);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        customerService.forget(newCustomer.getEmail());
    }

    @Test
    public void findNoUser() throws MessagingException {
        String email = "1234@test.mail.com";
        customerService.forget(email);
    }

    @Test
    public void findBannedUser() throws MessagingException {
        newCustomer.setBanned(true);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        customerService.forget(newCustomer.getEmail());
    }

    @Test
    public void findNoValidUser() throws MessagingException {
        customerService.forget(newCustomer.getEmail());
    }

    //    =================================================   //

    @Test
    public void findPasswordCorrect() throws MessagingException, MalformedURLException {
        newCustomer.setEmailValid(true);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        final String url = customerService.forget(newCustomer.getEmail());
        String[] params = new URL(url).getQuery().split("&");
        int findId = Integer.parseInt(params[0].split("=")[1]);
        String email = params[1].split("=")[1];
        String code = params[2].split("=")[1];

        newUser.setPassword("111111");
        newCustomer.setPassword("111111");
        assertTrue(customerService.findPassword(findId, email, code, newUser));
        Customer actual = customerMapper.selectByPrimaryKey(newCustomer.getCustomerId());
        assertEquals(newCustomer, actual);
    }

    @Test
    public void findPasswordNoUser() {
        assertFalse(customerService.findPassword(newCustomer.getCustomerId(), "2340792653@test.mail.com", "361575356d1e65689c9528643e6764581532057f887a", newUser));
    }

    @Test
    public void findPasswordUsed() throws MessagingException, MalformedURLException {
        newCustomer.setEmailValid(true);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        final String url = customerService.forget(newCustomer.getEmail());
        String[] params = new URL(url).getQuery().split("&");
        int findId = Integer.parseInt(params[0].split("=")[1]);
        String email = params[1].split("=")[1];
        String code = params[2].split("=")[1];

        newUser.setPassword("111111");
        newCustomer.setPassword("111111");
        customerService.findPassword(findId, email, code, newUser);
        Assert.assertFalse(customerService.findPassword(findId, email, code, newUser));
    }

    @Test
    public void findPasswordErrorId() {
        assertFalse(customerService.findPassword(-1, newCustomer.getEmail(), "361575356d1e65689c9528643e6764581532057f887a", newUser));
    }
//    =================================================   //

    @Test
    public void changePasswordByIdSuccess() {
        int id = customerService.changePasswordById(newCustomer.getCustomerId(), "123", "123adsfA");
        Customer c = customerMapper.selectByPrimaryKey(id);
        assertEquals("123", c.getPassword());
    }

    @Test
    public void changePasswordByIdIncorrectPassword() {
        assertEquals(-1, customerService.changePasswordById(newCustomer.getCustomerId(), "123456", "123adsf"));
    }


    //    =================================================   //

    @Test
    public void changeEmailByIdSuccess() {
        int id = customerService.changeEmailById(newCustomer.getCustomerId(), "981395882@qq.com");
        Customer c = customerMapper.selectByPrimaryKey(id);
        assertEquals("981395882@qq.com", c.getEmail());
    }

    @Test
    public void changeEmailByIdEmailAlreadyExits() {
        assertEquals(-2, customerService.changeEmailById(newCustomer.getCustomerId(), "814775538@qq.com"));
    }

    //    =================================================   //

    @Test
    public void addQuestionnaireSuccess() {
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        QuestionnaireForm form = new QuestionnaireForm();
        QuestionnaireVO vo = new QuestionnaireVO();
        ArrayList<Character> answerList = new ArrayList<>();
        for (char c : chars) {
            answerList.add(c);
        }
        form.setCustomerId(newCustomer.getCustomerId());
        form.setAnswerList(answerList);
        vo.setCustomerId(newCustomer.getCustomerId());
        vo.setAnswers(answerList);
        vo.setScore(25);
        vo.setLastUpdate(LocalDateTime.now());
        QuestionnaireVO result = customerService.addQuestionnaire(form);
        assertNotNull(result);
        assertNotNull(result.getQuestionnaireId());
        assertEquals(vo.getCustomerId(), result.getCustomerId());
        assertEquals(vo.getAnswers(), result.getAnswers());
        assertEquals(vo.getScore(), result.getScore());
        assertTrue(Duration.between(vo.getLastUpdate(), result.getLastUpdate()).toMinutes() < 1);
    }

    @Test
    public void addQuestionnaireNonexistentCustomer() {
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        QuestionnaireForm form = new QuestionnaireForm();
        ArrayList<Character> answerList = new ArrayList<>();
        for (char c : chars) {
            answerList.add(c);
        }
        form.setCustomerId(-1);
        form.setAnswerList(answerList);
        customerService.addQuestionnaire(form);
        QuestionnaireVO result = customerService.addQuestionnaire(form);
        assertNull(result);
    }

    //    =================================================   //

    @Test
    public void updateQuestionnaireSuccess() {
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        QuestionnaireForm form = new QuestionnaireForm();
        QuestionnaireVO vo = new QuestionnaireVO();
        ArrayList<Character> answerList = new ArrayList<>();
        for (char c : chars) {
            answerList.add(c);
        }
        form.setCustomerId(newCustomer.getCustomerId());
        form.setAnswerList(answerList);
        customerService.addQuestionnaire(form);
        answerList.remove(3);
        answerList.add(3, 'C');
        vo.setCustomerId(newCustomer.getCustomerId());
        vo.setAnswers(answerList);
        vo.setScore(27);
        vo.setLastUpdate(LocalDateTime.now());
        QuestionnaireVO result = customerService.updateQuestionnaire(form);
        assertNotNull(result);
        assertNotNull(result.getQuestionnaireId());
        assertEquals(vo.getCustomerId(), result.getCustomerId());
        assertEquals(vo.getAnswers(), result.getAnswers());
        assertEquals(vo.getScore(), result.getScore());
        assertTrue(Duration.between(vo.getLastUpdate(),result.getLastUpdate()).toMinutes() < 5);
    }

    @Test
    public void updateQuestionnaireNonexistentCustomer() {
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        QuestionnaireForm form = new QuestionnaireForm();
        ArrayList<Character> answerList = new ArrayList<>();
        for (char c : chars) {
            answerList.add(c);
        }
        form.setCustomerId(-1);
        form.setAnswerList(answerList);
        customerService.addQuestionnaire(form);
        QuestionnaireVO result = customerService.addQuestionnaire(form);
        assertNull(result);
    }

    //    =================================================   //

    @Test
    public void findQuestionnaireSuccess() {
        char[] chars = new char[]{'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A', 'A', 'B', 'A'};
        QuestionnaireForm form = new QuestionnaireForm();
        QuestionnaireVO vo = new QuestionnaireVO();
        ArrayList<Character> answerList = new ArrayList<>();
        for (char c : chars) {
            answerList.add(c);
        }
        form.setCustomerId(newCustomer.getCustomerId());
        form.setAnswerList(answerList);
        vo.setCustomerId(newCustomer.getCustomerId());
        vo.setAnswers(answerList);
        vo.setScore(25);
        vo.setLastUpdate(LocalDateTime.now());
        customerService.addQuestionnaire(form);
        QuestionnaireVO result = customerService.findQuestionnaireByCustomerId(newCustomer.getCustomerId());
        assertNotNull(result);
        assertNotNull(result.getQuestionnaireId());
        assertEquals(vo.getCustomerId(), result.getCustomerId());
        assertEquals(vo.getAnswers(), result.getAnswers());
        assertEquals(vo.getScore(), result.getScore());
        assertTrue(Duration.between(vo.getLastUpdate(), result.getLastUpdate()).toMinutes() < 1);
    }

    @Test
    public void findQuestionnaireNonexistentCustomer() {
        QuestionnaireVO result = customerService.findQuestionnaireByCustomerId(-1);
        assertNull(result);
    }

    @Test
    public void findQuestionnaireNonexistent() {
        QuestionnaireVO result = customerService.findQuestionnaireByCustomerId(newCustomer.getCustomerId());
        assertNull(result);
    }

}