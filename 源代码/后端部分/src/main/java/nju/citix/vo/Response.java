package nju.citix.vo;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author DW
 * @date 2019/8/12
 */
@Data
public class Response {
    private static final Logger LOGGER = LoggerFactory.getLogger(Response.class);
    /**
     * 是否成功
     */
    private Boolean status;
    /**
     * 成功：需要的数据
     * 失败：失败原因
     */
    private Object result;

    /**
     * 没有返回值的成功信息
     */
    @NotNull
    public static Response buildSuccess() {
        Response r = new Response();
        r.setStatus(true);
        r.setResult(null);
        return r;
    }

    /**
     * 返回成功信息
     *
     * @param o 要返回的json数据
     */
    @NotNull
    public static Response buildSuccess(@NotNull Object o) {
        Response r = new Response();
        r.setStatus(true);
        r.setResult(o);
        return r;
    }

    /**
     * 返回失败消息
     *
     * @param s 错误原因/内容
     */
    @NotNull
    public static Response buildFailure(String s) {
        Response r = new Response();
        r.setStatus(false);
        r.setResult(s);
        LOGGER.info("返回错误信息:" + s);
        return r;
    }
}
