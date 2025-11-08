import request from '@/utils/request';

//分页查询
export const queryPageApi = (page, pageSize) => {
  return request.get(`/operatorLog?page=${page}&pageSize=${pageSize}`)
}
// 删除指定天数前的日志
export const deleteBeforeApi = (day) => {
  return request.delete(`/operatorLog/${day}`)
}