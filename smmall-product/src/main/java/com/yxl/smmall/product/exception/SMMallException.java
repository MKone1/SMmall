package com.yxl.smmall.product.exception;

import com.yxl.common.exception.BizCodeEnume;
import com.yxl.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**配置全局异常处理类，集中处理所有的异常
 * @author SADSADSD
 */
@Slf4j
//@ResponseBody
//@ControllerAdvice(basePackages = "com.yxl.smmall.product.controller")
@RestControllerAdvice(basePackages = "com.yxl.smmall.product.controller")
public class SMMallException {
//    处理数据校验的异常
    /* 可以处理多个异常
            *public @interface ExceptionHandler {
            Class<? extends Throwable>[] value() default {};
        }
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class )
  public R handleValidException(MethodArgumentNotValidException e){
        log.error("数据校验出现异常{},异常类型：{}",e.getMessage(),e,getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> map = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError)->{
         map.put(fieldError.getField(),fieldError.getDefaultMessage());

        });
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(),BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data",map);
  }
@ExceptionHandler(value = Throwable.class)
  public R handleException( Throwable throwable){
 return  R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
  }

}
