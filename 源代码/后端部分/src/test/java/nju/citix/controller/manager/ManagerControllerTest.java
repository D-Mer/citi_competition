package nju.citix.controller.manager;

import com.alibaba.fastjson.JSON;
import nju.citix.dao.customer.CustomerMapper;
import nju.citix.dao.manager.ManagerMapper;
import nju.citix.po.Customer;
import nju.citix.po.Manager;
import nju.citix.service.manager.ManagerService;
import nju.citix.utils.JWTUtil;
import nju.citix.vo.ManagerForm;
import org.junit.Assert;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

/**
 * @author jiang hui
 * @date 2019/8/16
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ManagerControllerTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private MockHttpSession session;

    @Autowired
    private ManagerService managerService;
    @Resource
    private ManagerMapper managerMapper;
    @Resource
    private CustomerMapper customerMapper;

    private String supperToken;
    private Manager supperManager;
    private String existedTokenJH;
    private Manager existedManagerJH;
    private Customer newCustomer;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        session = new MockHttpSession();
        supperManager = managerService.findManagerById(1);
        supperToken = JWTUtil.getToken(supperManager);
        ManagerForm form = new ManagerForm();
        form.setUsername("jh");
        form.setPassword("666666");
        form.setBanned(false);
        existedManagerJH = managerService.addManager(form);
        existedTokenJH = JWTUtil.getToken(existedManagerJH);

        newCustomer = new Customer();
        newCustomer.setUsername("123abc");
        newCustomer.setPassword("123adsfA");
        newCustomer.setEmail("814775538@qq.com");
        customerMapper.insert(newCustomer);
        newCustomer = customerMapper.selectByPrimaryKey(newCustomer.getCustomerId());
//        加入session
//        UserVO user = new UserVO();
//        user.setId(8);
//        user.setIdentity(Identity.root);
//        session.setAttribute("user", user);
    }

    //    =================================================   //

    @Test
    public void loginSuccessTest() throws Exception {
//        参数准备
//        String param = "{'rows':1, 'page':1,'startLine':1,'endLine':10}";
        ManagerForm form = new ManagerForm();
        form.setUsername("root");
        form.setPassword("123456");
        mvc.perform(MockMvcRequestBuilders.post("/manager/login")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(JSON.toJSONString(form))
//        向post传入参数
//        .contentType(MediaType.APPLICATION_JSON_UTF8)
//        .content(param)
//        接受json数据
                        .accept(MediaType.APPLICATION_JSON_UTF8)
//        加入session
                        .session(session)
        )
//        期望返回的状态码
                .andExpect(MockMvcResultMatchers.status().isOk())
//        判断token
                .andExpect(MockMvcResultMatchers.header().string("token", supperToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.managerId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value("root"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.banned").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.lastLogin").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true));
        Assert.assertNotEquals(supperManager.getLastLogin(), managerMapper.selectByPrimaryKey(1).getLastLogin());
    }

    @Test
    public void loginFailureNamePwdErrorTest() throws Exception {
        ManagerForm form = new ManagerForm();
        form.setUsername("root2");
        form.setPassword("123456");
        mvc.perform(MockMvcRequestBuilders.post("/manager/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(form))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.NAME_PWD_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    //    =================================================   //

    @Test
    public void addManagerSuccessTest() throws Exception {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setBanned(false);
        managerForm.setUsername("newManager");
        managerForm.setPassword("1234562");
        mvc.perform(MockMvcRequestBuilders.post("/manager/addManager")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(managerForm))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", supperToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value("newManager"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.password").value("1234562"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.banned").value("false"));
    }

    @Test
    public void addManagerFailureNameExistedErrorTest() throws Exception {
        ManagerForm managerForm = new ManagerForm();
        managerForm.setBanned(false);
        managerForm.setUsername("root");
        managerForm.setPassword("1234562");
        mvc.perform(MockMvcRequestBuilders.post("/manager/addManager")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(managerForm))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", supperToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.NAME_EXISTED_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andDo(MockMvcResultHandlers.print());
    }

    //    =================================================   //

    @Test
    public void updateManagerPSDSuccessTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/manager/updatePassword")
                .param("managerId", existedManagerJH.getManagerId().toString())
                .param("newPassword", "2000")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", supperToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true));
    }

    @Test
    public void updateManagerPSDFailureNonexistentTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/manager/updatePassword")
                .param("managerId", "1000")
                .param("newPassword", "2000")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", supperToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.ID_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    //    =================================================   //

    @Test
    public void updateOwnPSDSuccessTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/manager/updateOwnPassword")
                .param("managerId", existedManagerJH.getManagerId().toString())
                .param("password", "666666")
                .param("newPassword", "2000")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", existedTokenJH)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true));
    }

    @Test
    public void updateOwnPSDFailurePwdErrorTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/manager/updateOwnPassword")
                .param("managerId", existedManagerJH.getManagerId().toString())
                .param("password", "6666661")
                .param("newPassword", "2000")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", existedTokenJH)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.PWD_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    //    =================================================   //

    @Test
    public void banManagerSuccessTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/manager/ban")
                .param("managerId", existedManagerJH.getManagerId().toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", supperToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true));
    }

    @Test
    public void banManagerFailureNonexistentTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/manager/ban")
                .param("managerId", "1000")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", supperToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.ID_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }

    //    =================================================   //

    @Test
    public void unbanManagerSuccessTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/manager/unban")
                .param("managerId", existedManagerJH.getManagerId().toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", supperToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true));
    }

    @Test
    public void unbanManagerFailureNonexistentTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/manager/unban")
                .param("managerId", "1000")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", supperToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.ID_ERROR))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false));
    }


    @Test
    public void viewAllCustomersSuccessTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/viewAllCustomers")
                .param("pageNum", "1")
                .param("pageSize", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", existedTokenJH)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true));
    }

    @Test
    public void viewAllCustomersWithWrongPageNum() throws Exception {
        Integer[] pageNums = {-1, 0, Integer.MAX_VALUE};
        for (Integer pageNum : pageNums) {
            mvc.perform(MockMvcRequestBuilders.get("/manager/viewAllCustomers")
                    .param("pageNum", pageNum.toString())
                    .param("pageSize", "1")
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .session(session)
                    .header("token", existedTokenJH))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.PARAMETER_ERROR));
        }
    }
    //    =================================================   //

    @Test
    public void viewAllManagersSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/viewAllManagers")
                .param("pageNum", "1")
                .param("pageSize", "1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", supperToken)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result[0].username").value("root"));
    }

    @Test
    public void viewAllManagersWithWrongPageNum() throws Exception {
        Integer[] pageNums = {-1, 0, Integer.MAX_VALUE};
        for (Integer pageNum : pageNums) {
            mvc.perform(MockMvcRequestBuilders.get("/manager/viewAllManagers")
                    .param("pageNum", pageNum.toString())
                    .param("pageSize", "1")
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .session(session)
                    .header("token", supperToken)
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.PARAMETER_ERROR));
        }
    }

    @Test
    public void viewAllCustomersWithWrongPageSize() throws Exception {
        Integer[] pageSizes = {-1, 0};
        for (Integer pageSize : pageSizes) {
            mvc.perform(MockMvcRequestBuilders.get("/manager/viewAllCustomers")
                    .param("pageNum", "1")
                    .param("pageSize", pageSize.toString())
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .session(session)
                    .header("token", existedTokenJH))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.PARAMETER_ERROR));
        }
    }

    @Test
    public void viewAllManagersWithWrongPageSize() throws Exception {
        Integer[] pageSizes = {-1, 0};
        for (Integer pageSize : pageSizes) {
            mvc.perform(MockMvcRequestBuilders.get("/manager/viewAllManagers")
                    .param("pageNum", "1")
                    .param("pageSize", pageSize.toString())
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .session(session)
                    .header("token", supperToken)
            )
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.PARAMETER_ERROR));
        }
    }

    //    =================================================   //

    @Test
    public void viewCustomerInfoSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/viewCustomerInfo")
                .param("customerId", newCustomer.getCustomerId().toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", existedTokenJH)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.username").value(newCustomer.getUsername()));
    }

    @Test
    public void viewCustomerInfoWrongId() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/viewCustomerInfo")
                .param("customerId", String.valueOf(Integer.MAX_VALUE))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", existedTokenJH)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.PARAMETER_ERROR));
    }

    //    =================================================   //

    @Test
    public void banUserSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/banCustomer")
                .param("customerId", newCustomer.getCustomerId().toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", existedTokenJH)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isEmpty());
    }

    @Test
    public void banUserWrongId() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/banCustomer")
                .param("customerId", String.valueOf(Integer.MAX_VALUE))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", existedTokenJH)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.BAN_FAILURE));
    }

    //    =================================================   //

    @Test
    public void releaseUserSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/releaseCustomer")
                .param("customerId", newCustomer.getCustomerId().toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", existedTokenJH)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").isEmpty());
    }

    @Test
    public void releaseUserWrongId() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/manager/releaseCustomer")
                .param("customerId", String.valueOf(Integer.MAX_VALUE))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .header("token", existedTokenJH)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value(ManagerController.RELEASE_FAILURE));
    }
}
