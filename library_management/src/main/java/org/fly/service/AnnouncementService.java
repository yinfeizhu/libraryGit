package org.fly.service;

import org.fly.entity.Announcement;

import java.util.List;

public interface AnnouncementService {
    List<Announcement> getAll();

    Announcement getById(Integer id);

    void add(Announcement announcement);

    void update(Announcement announcement);

    void delete(Integer id);

    List<Announcement> getLatestThree();
}
