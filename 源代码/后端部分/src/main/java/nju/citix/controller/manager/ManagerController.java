package nju.citix.controller.manager;

import nju.citix.annotation.Authority;
import nju.citix.annotation.UserLoginToken;
import nju.citix.po.Customer;
import nju.citix.po.Manager;
import nju.citix.service.manager.ManagerService;
import nju.citix.utils.JWTUtil;
import nju.citix.vo.ManagerForm;
import nju.citix.vo.Response;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author jiang hui
 * @date 2019/8/12
 */
@UserLoginToken
@RestController
@RequestMapping("/manager")
public class ManagerController {

    public static final String NAME_PWD_ERROR = "用户名或密码错误";
    public static final String NAME_EXISTED_ERROR = "用户名已存在";
    public static final String NAME_SHOULD_NOT_EMPTY = "用户名不得为空";
    public static final String PWD_SHOULD_NOT_EMPTY = "密码不得为空";
    public static final String SECURITY_ANSWER = "服务器错误";
    public static final String ID_ERROR = "用户ID不存在";
    public static final String PWD_ERROR = "密码错误";
    public static final String PARAMETER_ERROR = "参数无效或不合法";
    public static final String BAN_FAILURE = "禁用用户失败";
    public static final String RELEASE_FAILURE = "恢复用户失败";


    private ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @Authority(JWTUtil.ALL)
    @PostMapping("/login")
    public Response login(@RequestBody ManagerForm managerForm, HttpServletResponse response) {
        Assert.notNull(managerForm.getUsername(), NAME_SHOULD_NOT_EMPTY);
        Assert.notNull(managerForm.getPassword(), PWD_SHOULD_NOT_EMPTY);
        Assert.isNull(managerForm.getBanned(), SECURITY_ANSWER);

        Manager manager = managerService.login(managerForm.getUsername(), managerForm.getPassword());
        if (manager == null) {
            return Response.buildFailure(NAME_PWD_ERROR);
        } else {
            response.setHeader("token", JWTUtil.getToken(manager));
            manager.setPassword(null);
        }
        return Response.buildSuccess(manager);
    }

    @Authority(JWTUtil.SUPER)
    @PostMapping("/addManager")
    public Response addManager(@RequestBody ManagerForm managerForm) {
        Response r;
        try {
            r = Response.buildSuccess(managerService.addManager(managerForm));
        } catch (DuplicateKeyException e) {
            r = Response.buildFailure(NAME_EXISTED_ERROR);
        }
        return r;
    }

    @Authority(JWTUtil.SUPER)
    @PostMapping("/updatePassword")
    public Response updateManagerPassword(@RequestParam("managerId") Integer managerId, @RequestParam("newPassword") String newPassword) {
        Assert.notNull(newPassword, PWD_SHOULD_NOT_EMPTY);
        boolean result = managerService.updateManagerPassword(managerId, newPassword);
        return result ? Response.buildSuccess() : Response.buildFailure(ID_ERROR);
    }

    @Authority(JWTUtil.MANAGER)
    @PostMapping("/updateOwnPassword")
    public Response updateOwnPassword(@RequestParam("managerId") Integer managerId, @RequestParam("password") String password,
                                      @RequestParam("newPassword") String newPassword) {
        Assert.notNull(password, PWD_SHOULD_NOT_EMPTY);
        Assert.notNull(newPassword, PWD_SHOULD_NOT_EMPTY);
        boolean result = managerService.updateOwnPassword(managerId, password, newPassword);
        return result ? Response.buildSuccess() : Response.buildFailure(PWD_ERROR);
    }

    @Authority(JWTUtil.SUPER)
    @PostMapping("/ban")
    public Response banManager(@RequestParam Integer managerId) {
        boolean result = managerService.banManager(managerId);
        return result ? Response.buildSuccess() : Response.buildFailure(ID_ERROR);
    }

    @Authority(JWTUtil.SUPER)
    @PostMapping("/unban")
    public Response unbanManager(@RequestParam Integer managerId) {
        boolean result = managerService.unbanManager(managerId);
        return result ? Response.buildSuccess() : Response.buildFailure(ID_ERROR);
    }

    @Authority(JWTUtil.SUPER)
    @GetMapping("/viewAllManagers")
    public Response viewAllManagers(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        Assert.notNull(pageNum, PARAMETER_ERROR);
        Assert.notNull(pageSize, PARAMETER_ERROR);
        Assert.isTrue(pageNum > 0, PARAMETER_ERROR);
        Assert.isTrue(pageSize > 0, PARAMETER_ERROR);

        List<Manager> managers = managerService.viewAllManagers(pageNum, pageSize);
        if (managers == null || managers.isEmpty()) {
            return Response.buildFailure(PARAMETER_ERROR);
        }

        return Response.buildSuccess(managers);
    }

    @Authority(JWTUtil.MANAGER)
    @GetMapping("/viewCustomerInfo")
    public Response viewCustomerInfo(@RequestParam("customerId") Integer customerId) {
        Assert.notNull(customerId, PARAMETER_ERROR);

        Customer customer = managerService.viewCustomerInfo(customerId);
        if (customer == null) {
            return Response.buildFailure(PARAMETER_ERROR);
        }

        return Response.buildSuccess(customer);
    }

    @Authority(JWTUtil.MANAGER)
    @GetMapping("/banCustomer")
    public Response banUser(@RequestParam("customerId") Integer customerId) {
        Assert.notNull(customerId, PARAMETER_ERROR);

        if (!managerService.banUser(customerId)) {
            return Response.buildFailure(BAN_FAILURE);
        }

        return Response.buildSuccess();
    }

    @Authority(JWTUtil.MANAGER)
    @GetMapping("/releaseCustomer")
    public Response releaseUser(@RequestParam("customerId") Integer customerId) {
        Assert.notNull(customerId, PARAMETER_ERROR);

        if (!managerService.releaseUser(customerId)) {
            return Response.buildFailure(RELEASE_FAILURE);
        }

        return Response.buildSuccess();
    }

    @GetMapping("/viewAllCustomers")
    public Response viewAllCustomers(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize) {
        Assert.notNull(pageNum, PARAMETER_ERROR);
        Assert.notNull(pageSize, PARAMETER_ERROR);
        Assert.isTrue(pageNum > 0, PARAMETER_ERROR);
        Assert.isTrue(pageSize > 0, PARAMETER_ERROR);

        List<Customer> customers = managerService.viewAllCustomers(pageNum, pageSize);
        if (customers.isEmpty()) {
            return Response.buildFailure(PARAMETER_ERROR);
        }
        return Response.buildSuccess(customers);
    }
}
