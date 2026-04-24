/**
 * 后端接口地址常量
 * 所有接口返回统一 Result<T> 结构：{ code, message, data }
 */

// 开发环境后端地址（真机调试需改为可访问的 IP 或 HTTPS 域名）
const BASE_URL = 'http://localhost:8080';

const API = {
  // 订单（用户侧）
  ORDER_CREATE: '/api/order/create',      // POST 创建订单
  ORDER_LIST: '/api/order/list',          // GET  我的订单列表 ?userId=
  ORDER_DETAIL: '/api/order/detail',      // GET  订单详情 /:id
  ORDER_CANCEL: '/api/order/cancel',      // POST 用户取消订单（仅状态为待接单）

  // 订单（跑腿员侧）
  ORDER_HALL: '/api/order/hall',          // GET  跑腿大厅（可接订单）
  ORDER_TAKE: '/api/order/take',          // POST 接单
  ORDER_PICKUP: '/api/order/pickup',      // POST 取货确认
  ORDER_FINISH: '/api/order/finish',      // POST 完成订单
  ORDER_MY_TASKS: '/api/order/my-tasks',  // GET  我的接单列表 ?runnerId=

  // 跑腿员收益（虚拟结算）
  INCOME_SUMMARY: '/api/runner/income/summary', // GET 收益汇总 ?runnerId=
  INCOME_LIST: '/api/runner/income/list',        // GET 收益明细 ?runnerId=
};

module.exports = {
  BASE_URL,
  API,
};
