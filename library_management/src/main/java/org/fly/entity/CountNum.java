package org.fly.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountNum {
    //第一个列表存统计的对象名
    private List nameList;
    //第二个列表存数量
    private List numList;
}
