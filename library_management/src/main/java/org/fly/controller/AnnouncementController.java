package org.fly.controller;

import lombok.extern.slf4j.Slf4j;
import org.fly.entity.Announcement;
import org.fly.entity.Result;
import org.fly.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    @Autowired
    private AnnouncementService announcementService;

    @GetMapping
    public Result getAll() {
        log.info("获取所有公告");
        List<Announcement> announcementList = announcementService.getAll();
        return Result.success(announcementList);
    }

    // 根据id查询公告
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        log.info("查询公告信息：{}", id);
        Announcement announcement = announcementService.getById(id);
        return Result.success(announcement);
    }

    // 添加公告
    @PostMapping
    public Result add(@RequestBody Announcement announcement) {
        log.info("添加公告信息：{}", announcement);
        announcementService.add(announcement);
        return Result.success();
    }
    // 修改公告
    @PutMapping
    public Result update(@RequestBody Announcement announcement) {
        log.info("修改公告信息：{}", announcement);
        announcementService.update(announcement);
        return Result.success();
    }
    // 删除公告
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        log.info("删除公告信息：{}", id);
        announcementService.delete(id);
        return Result.success();
    }
    //查询最新的三条公告
    @GetMapping("/latest")
    public Result getLatestThree() {
        log.info("查询最新的三条公告");
        List<Announcement> threeList = announcementService.getLatestThree();
        return Result.success(threeList);
    }
}
