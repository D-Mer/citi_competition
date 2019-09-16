package nju.citix.serviceImpl.manager;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nju.citix.dao.manager.ManagerMapper;
import nju.citix.po.Manager;
import nju.citix.service.manager.ManagerService;
import nju.citix.vo.ManagerForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author jiang hui
 * @date 2019/8/16
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ManagerServiceTest {
    @Autowired
    private ManagerService managerService;
    @Resource
    private ManagerMapper managerMapper;

    private Manager supperManager;
    private Manager existedManagerJH;//用户名为jh，没被ban
    private Manager existedManagerHXD;//用户名为hxd，被ban
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime testStartTime = LocalDateTime.now();

    @Before
    public void setUp() throws Exception {
        ManagerForm form = new ManagerForm();
        form.setUsername("jh");
        form.setPassword("666666");
        form.setBanned(false);
        existedManagerJH = managerService.addManager(form);
        form.setUsername("hxd");
        form.setPassword("222222");
        form.setBanned(true);
        existedManagerHXD = managerService.addManager(form);
        supperManager = managerMapper.selectByPrimaryKey(1);
    }

    //    =================================================   //

    @Test
    public void addManagerSuccessTest() {
        Assert.assertEquals("jh", existedManagerJH.getUsername());
        Assert.assertEquals("666666", existedManagerJH.getPassword());
        Assert.assertEquals(false, existedManagerJH.getBanned());
        Assert.assertNull(existedManagerJH.getLastLogin());
        Assert.assertEquals("hxd", existedManagerHXD.getUsername());
        Assert.assertEquals("222222", existedManagerHXD.getPassword());
        Assert.assertEquals(true, existedManagerHXD.getBanned());
        Assert.assertNull(existedManagerHXD.getLastLogin());
    }

    //    =================================================   //

    @Test
    public void findByIdSuccessTest() {
        Assert.assertEquals(existedManagerJH, managerService.findManagerById(existedManagerJH.getManagerId()));
        Assert.assertEquals(existedManagerHXD, managerService.findManagerById(existedManagerHXD.getManagerId()));
        Manager manager = managerService.findManagerById(1);
        Assert.assertEquals(supperManager.getUsername(), manager.getUsername());
        Assert.assertEquals(supperManager.getPassword(), manager.getPassword());
        Assert.assertEquals(supperManager.getBanned(), manager.getBanned());
    }

    @Test
    public void findByIdFailureNonexistentTest() {
        assertNull(managerService.findManagerById(1000));
    }

    @Test
    public void findByIdFailureNonPositiveNumTest() {
        assertNull(managerService.findManagerById(-1));
    }

    //    =================================================   //

    @Test
    public void loginSuccessTest() {
        Manager manager = managerService.login(existedManagerJH.getUsername(), existedManagerJH.getPassword());
        Assert.assertEquals(existedManagerJH.getManagerId(), manager.getManagerId());
        Assert.assertEquals(existedManagerJH.getUsername(), manager.getUsername());
        Assert.assertEquals(existedManagerJH.getPassword(), manager.getPassword());
        Assert.assertEquals(existedManagerJH.getBanned(), manager.getBanned());
        Assert.assertTrue(Duration.between(testStartTime, manager.getLastLogin()).toMinutes() < 10);
    }

    @Test
    public void loginFailureNonexistentTest() {
        Manager manager = managerService.login("nonexistence", "123456");
        Assert.assertNull(manager);
    }

    @Test
    public void loginFailureNamePwdErrorTest() {
        Manager manager = managerService.login(existedManagerJH.getUsername(), existedManagerJH.getPassword() + "222");
        Assert.assertNull(manager);
    }

    @Test
    public void loginFailureBannedTest() {
        Manager manager = managerService.login(existedManagerHXD.getUsername(), existedManagerHXD.getPassword());
    }

    //    =================================================   //

    @Test
    public void updateManagerPSDSuccessTest(){
        boolean result = managerService.updateManagerPassword(existedManagerJH.getManagerId(),"2000");
        Assert.assertTrue(result);
        Assert.assertEquals("2000",managerMapper.selectByPrimaryKey(existedManagerJH.getManagerId()).getPassword());
    }

    @Test
    public void updateManagerPSDFailureNonexistentTest(){
        boolean result = managerService.updateManagerPassword(1000,"2000");
        Assert.assertFalse(result);
    }

    @Test
    public void updateOwnPSDSuccessTest(){
        boolean result = managerService.updateOwnPassword(existedManagerJH.getManagerId(),"666666","2000");
        Assert.assertTrue(result);
        Assert.assertEquals("2000",managerMapper.selectByPrimaryKey(existedManagerJH.getManagerId()).getPassword());
    }

    @Test
    public void updateOwnPSDFailurePwdErrorTest(){
        boolean result = managerService.updateOwnPassword(existedManagerJH.getManagerId(),"6666661","2000");
        Assert.assertFalse(result);
    }

    //    =================================================   //

    @Test
    public void banManagerSuccessTest(){
        boolean result = managerService.banManager(existedManagerJH.getManagerId());
        Assert.assertTrue(result);
        Assert.assertTrue(managerMapper.selectByPrimaryKey(existedManagerJH.getManagerId()).getBanned());
    }

    @Test
    public void banManagerFailureNonexistentTest(){
        boolean result = managerService.banManager(1000);
        Assert.assertFalse(result);
    }

    //    =================================================   //

    @Test
    public void unbanManagerSuccessTest(){
        boolean result = managerService.unbanManager(existedManagerHXD.getManagerId());
        Assert.assertTrue(result);
        Assert.assertFalse(managerMapper.selectByPrimaryKey(existedManagerHXD.getManagerId()).getBanned());
    }

    @Test
    public void unbanManagerFailureNonexistentTest(){
        boolean result = managerService.unbanManager(1000);
        Assert.assertFalse(result);
    }

    //    =================================================   //

    @Test
    public void viewAllManagersSuccess() {
        List<Manager> managers = managerService.viewAllManagers(1,1);
        Assert.assertEquals(managers.size(), 1);
    }

    @Test
    public void viewAllManagersWithWrongPageNum() {
        int[] pageNums = {-1, 0, Integer.MAX_VALUE};
        for (int pageNum: pageNums) {
            List<Manager> managers = managerService.viewAllManagers(pageNum,1);
            Assert.assertTrue(managers==null || managers.isEmpty());
        }
    }

    @Test
    public void viewAllManagersWithWrongPageSize() {
        int[] pageSizes = {-1, 0};
        for (int pageSize: pageSizes) {
            List<Manager> managers = managerService.viewAllManagers(1,pageSize);
            Assert.assertTrue(managers==null || managers.isEmpty());
        }
    }
}
