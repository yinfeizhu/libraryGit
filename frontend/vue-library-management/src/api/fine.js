import request from '@/utils/request';

//查询展示罚款记录
export const queryPageApi=(page,pageSize,readerName,readerPhone,bookBarcode) =>{
  return request.get(`/fineRecord?page=${page}&pageSize=${pageSize}&readerName=${readerName}&readerPhone=${readerPhone}&bookBarcode=${bookBarcode}`);
}

//查询ById
export const queryByIdApi=(id) =>{
  return request.get(`/fineRecord/${id}`);
}
//添加罚款记录
export const addApi=(fineRecord) =>{
  return request.post(`/fineRecord`,fineRecord);
}
//修改罚款记录
export const updateApi=(fineRecord) =>{
  return request.put(`/fineRecord`,fineRecord);
}
//删除罚款记录
export const deleteApi=(ids) =>{
  return request.delete(`/fineRecord?ids=${ids}`);
}