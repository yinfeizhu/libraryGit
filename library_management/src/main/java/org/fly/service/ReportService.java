package org.fly.service;

import org.fly.entity.CountNum;

public interface ReportService {
    //统计用户性别
    CountNum getGenderData();

    //统计读者类型
    CountNum getReaderTypeCount();
    //统计图书类型
    CountNum getBookTypeCount();
    //统计所有书籍状态
    CountNum getBookStatusCount();

    CountNum getFineReasonCount();

    CountNum getPayStatusCount();
}
