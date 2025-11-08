package org.fly.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.fly.entity.Announcement;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
    //查询所有公告
    @Select("select a.*,u.name as admin_name from announcement a " +
            "left join user u on a.admin_id=u.id order by a.update_time desc")
    List<Announcement> getAll();
    //根据id查询公告
    @Select("select * from announcement where id=#{id}")
    Announcement getById(Integer id);
    //查询最新的三条公告
    @Select("select * from announcement order by update_time desc limit 3")
    List<Announcement> getNewThree();
    //添加公告
    @Select("insert into announcement(subject,content,admin_id,create_time,update_time) values(#{subject},#{content},#{adminId},#{createTime},#{updateTime})")
    void add(Announcement announcement);

    @Select("update announcement set subject=#{subject},content=#{content},admin_id=#{adminId},update_time=#{updateTime} where id=#{id}")
    void update(Announcement announcement);

    @Select("delete from announcement where id= #{id}")
    void delete(Integer id);
}
