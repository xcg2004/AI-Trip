// import axios from 'axios';

// const request = axios.create({
//   baseURL: 'http://localhost:8080',
//   timeout: 5000
// });

// // 请求拦截器
// request.interceptors.request.use(
//   config => {
//     const token = localStorage.getItem('token');
//     if (token) {
//       config.headers['token'] = token;
//     }else{
//       alert("token不存在")
//     }
//     return config;
//   },
//   error => {
//     return Promise.reject(error);
//   }
// );

// // 响应拦截器
// request.interceptors.response.use(
//   response => response,
//   error => {
//     return Promise.reject(error);
//   }
// );

// export default request;