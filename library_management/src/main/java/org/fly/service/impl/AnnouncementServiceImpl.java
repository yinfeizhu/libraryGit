package org.fly.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.fly.entity.Announcement;
import org.fly.mapper.AnnouncementMapper;
import org.fly.service.AnnouncementService;
import org.fly.utils.CurrentHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;
    @Override
    public List<Announcement> getAll() {
        return announcementMapper.getAll();
    }

    @Override
    public Announcement getById(Integer id) {
        return announcementMapper.getById(id);
    }

    @Override
    public void add(Announcement announcement) {
        //1.设置创建时间和更新时间
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        announcement.setAdminId(CurrentHolder.getCurrentUserId());
        //2.添加
        announcementMapper.add(announcement);
    }

    @Override
    public void update(Announcement announcement) {
        //1.设置更新时间
        announcement.setUpdateTime(LocalDateTime.now());
        //2.获取当前登陆的管理员ID
        announcement.setAdminId(CurrentHolder.getCurrentUserId());
        //2.更新
        announcementMapper.update(announcement);
    }

    @Override
    public void delete(Integer id) {
        announcementMapper.delete(id);
    }

    @Override
    public List<Announcement> getLatestThree() {
        return announcementMapper.getNewThree();
    }
}
