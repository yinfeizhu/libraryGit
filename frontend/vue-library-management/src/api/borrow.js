import request from '@/utils/request';

//查询展示借阅记录
export const queryPageApi=(page,pageSize,readerName,bookBarcode,bookTitle) =>{
  return request.get('/borrowRecord?',{
    params:{
      page,pageSize,readerName,bookBarcode,bookTitle
    }
  });
}
//查询ById
export const queryByIdApi=(id) =>{
  return request.get(`/borrowRecord/${id}`);
}
//添加借阅记录
export const addApi=(borrowRecord) =>{
  return request.post(`/borrowRecord/borrow`,borrowRecord);
}
//还书操作
export const updateApi=(id) =>{
  return request.put(`/borrowRecord/return/${id}`);
}
//删除借阅记录
export const deleteBatchApi=(ids) =>{
  return request.delete(`/borrowRecord?ids=${ids}`);
}
