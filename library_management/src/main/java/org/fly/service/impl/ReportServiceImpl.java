package org.fly.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.fly.entity.CountNum;
import org.fly.mapper.*;
import org.fly.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private BookAdminMapper bookAdminMapper;
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookReaderMapper bookReaderMapper;
    @Autowired
    private FineRecordMapper fineRecordMapper;

    //统计用户性别
    @Override
    public CountNum getGenderData() {
        //1.查询所有数据
        List<Map<String, Object>> list = userMapper.selectGenderData();
        //2.分别封装性别和数量
        List<Object> nameList = list.stream().map(item -> item.get("name")).toList();
        List<Object> numList = list.stream().map(item -> item.get("num")).toList();
        //3.封装成CountNum对象返回
        return new CountNum(nameList, numList);
    }
    //统计读者类型
    @Override
    public CountNum getReaderTypeCount() {
        //1.查询所有数据
        List<Map<String, Object>> list = userMapper.selectUserTypeData();
        //2.分别封装类型和数量
        List<Object> nameList = list.stream().map(item -> item.get("name")).toList();
        List<Object> numList = list.stream().map(item -> item.get("num")).toList();
        //3.封装成CountNum对象返回
        return new CountNum(nameList, numList);
    }
    //统计图书类型
    @Override
    public CountNum getBookTypeCount() {
        //1.查询所有数据
        List<Map<String, Object>> list = bookReaderMapper.selectBookTypeData();
        //2.分别封装类型和数量
        List<Object> nameList = list.stream().map(item -> item.get("name")).toList();
        List<Object> numList = list.stream().map(item -> item.get("num")).toList();
        //3.封装成CountNum对象返回
        return new CountNum(nameList, numList);
    }

    @Override
    public CountNum getBookStatusCount() {
        //1.查询所有数据
        List<Map<String, Object>> list = bookAdminMapper.selectBookStatusData();
        //2.分别封装类型和数量
        List<Object> nameList = list.stream().map(item -> item.get("name")).toList();
        List<Object> numList = list.stream().map(item -> item.get("num")).toList();
        //3.封装成CountNum对象返回
        return new CountNum(nameList, numList);
    }

    @Override
    public CountNum getFineReasonCount() {
        //1.查询所有数据
        List<Map<String, Object>> list = fineRecordMapper.selectFineReasonData();
        //2.分别封装类型和数量
        List<Object> nameList = list.stream().map(item -> item.get("name")).toList();
        List<Object> numList = list.stream().map(item -> item.get("num")).toList();
        //3.封装成CountNum对象返回
        return new CountNum(nameList, numList);
    }

    @Override
    public CountNum getPayStatusCount() {
        //1.查询所有数据
        List<Map<String, Object>> list = fineRecordMapper.selectPayStatusData();
        //2.分别封装类型和数量
        List<Object> nameList = list.stream().map(item -> item.get("name")).toList();
        List<Object> numList = list.stream().map(item -> item.get("num")).toList();
        //3.封装成CountNum对象返回
        return new CountNum(nameList, numList);
    }
}
