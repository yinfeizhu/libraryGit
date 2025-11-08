import request from '@/utils/request';

//查询展示书籍
export const queryPageApi=(page,pageSize,bookTitle,barcode,status) =>{
  return request.get(`/bookAdmin?page=${page}&pageSize=${pageSize}&bookTitle=${bookTitle}&barcode=${barcode}&status=${status}`);
}

//根据id查询书籍
export const queryByIdApi=(id) =>{
  return request.get(`/bookAdmin/${id}`);
}
//添加书籍
export const addApi=(bookReader) =>{
  return request.post('/bookAdmin',bookReader);
}

//修改书籍
export const updateApi=(bookReader) =>{
  return request.put('/bookAdmin',bookReader);
}

//删除书籍
export const deleteApi=(ids) =>{
  return request.delete(`/bookAdmin?ids=${ids}`);
}