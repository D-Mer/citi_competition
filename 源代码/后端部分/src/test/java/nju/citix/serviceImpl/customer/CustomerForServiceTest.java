package nju.citix.serviceImpl.customer;

import nju.citix.dao.customer.CustomerMapper;
import nju.citix.po.Customer;
import nju.citix.service.customer.CustomerForService;
import nju.citix.service.customer.CustomerService;
import nju.citix.vo.UserForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CustomerForServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Resource
    private CustomerService customerService;
    @Resource
    private CustomerForService customerForService;
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
    public void viewCustomerInfoSuccess() throws Exception {
        Assert.assertNotNull(customerForService.viewCustomerInfo(newCustomer.getCustomerId()));
    }

    @Test
    public void viewCustomerInfoWrongId() throws Exception {
        Assert.assertNull(customerForService.viewCustomerInfo(Integer.MAX_VALUE));
    }

    //    =================================================   //

    @Test
    public void banUserSuccess() throws Exception {
        newCustomer.setBanned(false);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        customerForService.banUser(newCustomer.getCustomerId());
        Customer customer = customerMapper.selectByPrimaryKey(newCustomer.getCustomerId());
        Assert.assertNotNull(customer);
        Assert.assertTrue(customer.getBanned());
    }

    @Test
    public void banUserWrongId() throws Exception {
        Assert.assertFalse(customerForService.banUser(Integer.MAX_VALUE));
    }

    //    =================================================   //

    @Test
    public void releaseUserSuccess() throws Exception {
        newCustomer.setBanned(true);
        customerMapper.updateByPrimaryKeySelective(newCustomer);
        customerForService.releaseUser(newCustomer.getCustomerId());
        Customer customer = customerMapper.selectByPrimaryKey(newCustomer.getCustomerId());
        Assert.assertNotNull(customer);
        Assert.assertFalse(customer.getBanned());
    }

    @Test
    public void releaseUserWrongId() throws Exception {
        Assert.assertFalse(customerForService.releaseUser(Integer.MAX_VALUE));
    }
}
