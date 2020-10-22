package com.yxl.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueValidated implements ConstraintValidator<ListValue,Integer> {



        private Set<Integer> set  = new HashSet<>();
    /**
     * 初始化方法
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        for(int value : vals){
            set.add(value);
        }
    }

    /**
     * 判断是否校验成功
     * @param integer  需要校验的值
     * @param constraintValidatorContext 整个校验的上下文信息
     * @return
     */

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {

        return set.contains(integer);
    }
}
