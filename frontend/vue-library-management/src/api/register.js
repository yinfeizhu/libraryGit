import request from '@/utils/request';

// 注册
export const registerApi=(registerForm) =>{
  return request.post('/register',registerForm);
}