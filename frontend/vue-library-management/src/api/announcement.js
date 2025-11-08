import request from '@/utils/request'

//查看公告
export const queryAnnouncementApi=() =>{
  return request.get('/announcement');
}

//新增公告
export const addAnnouncementApi=(announcement) =>{
  return request.post('/announcement',announcement);
}

//删除公告
export const deleteAnnouncementApi=(id) =>{
  return request.delete(`/announcement/${id}`);
}
//修改公告
export const updateAnnouncementApi=(announcement) =>{
  return request.put(`/announcement`,announcement);
}

//根据id查看公告
export const queryAnnouncementByIdApi=(id) =>{
  return request.get(`/announcement/${id}`);
}
//查看最近三条公告
export const queryRecentAnnouncementsApi=() =>{
  return request.get('/announcement/latest');
}
