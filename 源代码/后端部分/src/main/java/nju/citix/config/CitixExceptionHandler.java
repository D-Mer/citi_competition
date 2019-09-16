package nju.citix.config;

import nju.citix.utils.AlipayUtil;
import nju.citix.utils.JWTUtil;
import nju.citix.vo.Response;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author DW
 * @date 2019/8/13
 */
@RestControllerAdvice
public class CitixExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response validateErrorException(MethodArgumentNotValidException ex) {
        final FieldError error = ex.getBindingResult().getFieldError();
        String errorMessage;
        if (error != null) {
            errorMessage = error.getDefaultMessage();
        } else {
            errorMessage = ex.getMessage();
        }
        return Response.buildFailure(errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Response assertErrorException(IllegalArgumentException ex) {
        return Response.buildFailure(ex.getMessage());
    }

    @ExceptionHandler(JWTUtil.TokenException.class)
    public Response tokenException(JWTUtil.TokenException ex) {
        return Response.buildFailure(ex.getMessage());
    }

    @ExceptionHandler(AlipayUtil.AlipayException.class)
    public Response tokenException(AlipayUtil.AlipayException ex) {
        return Response.buildFailure(ex.getMessage());
    }
}
