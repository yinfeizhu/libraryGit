package org.fly.entity;


import lombok.Data;
import lombok.ToString;

@Data
@ToString//toString()方法用于打印对象时显示对象的属性
public class Result {

        private String code;
        private String msg;
        private Object data;

        public Result() {
        }

        //定义为静态方法，因为创建对象时，需要设置属性值，而属性值可能为空
        public static Result success() {
            Result result = new Result();
            result.setCode("200");
            result.setMsg("请求成功");
            return result;
        }

        public static Result success(Object data) {
            Result result = success();
            result.setData(data);
            return result;
        }

        public static Result error() {
            Result result = new Result();
            result.setCode("500");
            result.setMsg("请求失败");
            return result;
        }

        public static Result error(String code, String msg) {
            Result result = new Result();
            result.setCode(code);
            result.setMsg(msg);
            return result;
        }

        public static Result error(String msg) {
            Result result = new Result();
            result.setCode("500");
            result.setMsg(msg);
            return result;
        }



}
