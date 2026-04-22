/**
 * 订单元信息映射（状态、类型、时间格式化）
 * 供订单列表 / 详情 / 跑腿大厅 / 我的接单 等页面统一复用
 */

// 订单状态 ↔ 显示样式（与后端 Order.status 对齐）
const STATUS_MAP = {
  0: { label: '待接单', color: '#ff9f00', bg: '#fff7e6' },
  1: { label: '已接单', color: '#1989fa', bg: '#e8f3ff' },
  2: { label: '进行中', color: '#1989fa', bg: '#e8f3ff' },
  3: { label: '已完成', color: '#07c160', bg: '#e8f8ee' },
  4: { label: '已取消', color: '#999999', bg: '#f2f2f2' },
};

// 订单类型 ↔ 中文标签（与后端 Order.orderType 对齐）
const ORDER_TYPE_MAP = {
  0: '代拿快递',
  1: '代买商品',
  2: '代办事务',
  3: '其他',
};

function getStatus(status) {
  return STATUS_MAP[status] || STATUS_MAP[0];
}

function getOrderTypeLabel(type) {
  return ORDER_TYPE_MAP[type] || '未知';
}

// 后端 LocalDateTime 序列化为 "2026-04-22T10:35:12" 或 "2026-04-22 10:35:12"，精简为 "2026-04-22 10:35"
function formatTime(raw) {
  if (!raw) return '';
  const s = String(raw).replace('T', ' ');
  return s.length >= 16 ? s.slice(0, 16) : s;
}

module.exports = {
  STATUS_MAP,
  ORDER_TYPE_MAP,
  getStatus,
  getOrderTypeLabel,
  formatTime,
};
