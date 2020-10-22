package com.yxl.common.constant;

public class SearchTypeConstant {
    public enum SearchTypeEnum{
        SEARCH_TYPE_ENUM_IS(1,"可检索"),SEARCH_TYPE_ENUM_NOT(0,"不可检索");
        private int code;
        private String msg;
        SearchTypeEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
