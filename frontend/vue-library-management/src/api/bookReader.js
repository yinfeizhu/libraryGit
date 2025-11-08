import request from '@/utils/request';

//查询展示书籍
export const queryPageApi=(page,pageSize,bookTitle ,category) =>{
  return request.get(`/bookReader?page=${page}&pageSize=${pageSize}&bookTitle=${bookTitle}&category=${category}`);
}

//根据id查询书籍
export const queryByIdApi=(id) =>{
  return request.get(`/bookReader/${id}`);
}

//添加书籍
export const addApi=(bookReader) =>{
  return request.post('/bookReader',bookReader);
}

//修改书籍
export const updateApi=(bookReader) =>{
  return request.put('/bookReader',bookReader);
}

//删除书籍
export const deleteApi=(ids) =>{
  return request.delete(`/bookReader?ids=${ids}`);
}

