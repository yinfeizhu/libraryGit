package org.fly.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class PageResult<T>  {
    //T是泛型，可以是任意类型
    private Long total;//总记录数
    private List<T> rows;//当前页数据
}
