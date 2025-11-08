import request from '@/utils/request'


//查询用户列表
export const queryPageApi=(page,pageSize,name ,readerType) =>{
  return request.get(`/user?page=${page}&pageSize=${pageSize}&name=${name}&readerType=${readerType}`);
 
}

//新增用户
export const addApi=(user) =>{
  return request.post('/user',user);
}

//删除用户
export const deleteApi=(ids) =>{
  return request.delete(`/user?ids=${ids}`);
}

//查询用户
export const queryByIdApi=(id) =>{
  return request.get(`/user/${id}`);
}

//修改用户
export const updateApi=(user) =>{
  return request.put('/user',user);
}
//根据id修改密码
export const updatePasswordApi = (id, oldPassword, newPassword) => {
  return request.put(`/user/password`, {
    id,
    oldPassword,
    newPassword
  });
}