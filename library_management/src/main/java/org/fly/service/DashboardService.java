package org.fly.service;

import org.fly.entity.HotBook;

import java.util.List;

public interface DashboardService {
    Object getBookCount();

    Object getSoonExpire();

    List<HotBook> getHotBookData();

    List<HotBook> getRecommendBookData();

    Object getOverdue();

    Object getNewUser();

    Object getNewBook();
}
