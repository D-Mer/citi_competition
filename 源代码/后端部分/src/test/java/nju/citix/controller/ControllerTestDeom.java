package nju.citix.controller;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ControllerTestDeom {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private MockHttpSession session;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        session = new MockHttpSession();
//        加入session
//        UserVO user = new UserVO();
//        user.setId(8);
//        user.setIdentity(Identity.root);
//        session.setAttribute("user", user);
    }

    @Test
    public void register() throws Exception {
//        post 参数准备
//        String param = JSON.toJSONString(u);  //其中u是VO对象

//        指定url和方式
        mvc.perform(MockMvcRequestBuilders.get("/user/register")
                        //get 参数  ，其实是RequestParam
                        .param("rows", "1")
//                向post传入参数 ，其实是RequestBody
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .content(param)

//                接受json数据
                        .accept(MediaType.APPLICATION_JSON_UTF8)
//                加入session
                        .session(session)
        );
//                期望返回的状态码
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                判断json数据
//                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(1))
//数据json
//                .andDo(MockMvcResultHandlers.print());
    }
}