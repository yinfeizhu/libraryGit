import request from '@/utils/request'

//统计图书种类分布
export const queryBookTypeCountApi=() =>{
  return request.get('/report/getBookTypeCount');
}
//统计图书状态分布
export const queryBookStatusCountApi=() =>{
  return request.get('/report/getBookStatusCount');
}

//统计用户性别分布
export const queryUserGenderCountApi=() =>{
  return request.get('/report/getGenderCount');
}
//统计读者类型分布
export const queryReaderTypeCountApi=() =>{
  return request.get('/report/getReaderTypeCount');
}

//罚款原因分布
export const queryFineReasonCountApi=() =>{
  return request.get('/report/getFineReasonCount');
}
//支付状态分布
export const queryPayStatusCountApi=() =>{
  return request.get('/report/getPayStatusCount');
}