package com.yxl.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(
        validatedBy = {ListValueValidated.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValue {
    /**
     * 必须满校验注解的三个规范：
     * 1，必须要有message，即出错的信息
     * 2，group，也要支持分组校验的公告
     * 3，payload，也要支持一些负载信息
     */
    String message() default "{com.yxl.common.valid.ListValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    int[] vals() default { };

}
