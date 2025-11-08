import request  from '@/utils/request';

//即将到期图书数量
export const querySoonExpireApi=() =>{
  return request.get('/dashboard/soonExpire');
}
//热门借阅排行
export const queryHotBookApi=() =>{
  return request.get('/dashboard/hotBook');
}
//推荐图书
export const queryRecommendBookApi=() =>{
  return request.get('/dashboard/recommendBook');
}
//待办事项-逾期图书
export const queryOverdueApi=() =>{
  return request.get('/dashboard/overdue');
}
//待办事项-新增用户
export const queryNewUserApi=() =>{
  return request.get('/dashboard/newUser');
}
//待办事项-新增图书数量
export const queryNewBookApi=() =>{
  return request.get('/dashboard/newBook');
}