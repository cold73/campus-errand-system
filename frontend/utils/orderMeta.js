/**
 * 订单元信息映射（状态、类型、时间格式化）
 * 供订单列表 / 详情 / 跑腿大厅 / 我的接单 等页面统一复用
 */

// 订单状态 ↔ editorial 调色（paper / ink / orange / acid / purple / muted）
// 与后端 Order.status 对齐
// label 是中文（兼容旧页面），en 是 mono uppercase（供新 magazine 卡片 pill 使用）
const STATUS_MAP = {
  0: { label: '待接单', en: 'PENDING',     color: '#FF5B1F', bg: 'rgba(255, 91, 31, 0.12)' },
  1: { label: '已接单', en: 'TAKEN',       color: '#0A0A0A', bg: 'rgba(10, 10, 10, 0.08)' },
  2: { label: '进行中', en: 'IN-PROGRESS', color: '#7B5FE8', bg: 'rgba(123, 95, 232, 0.12)' },
  3: { label: '已完成', en: 'DONE',        color: '#5c7a00', bg: 'rgba(212, 255, 61, 0.35)' },
  4: { label: '已取消', en: 'CANCELLED',   color: '#8A847B', bg: 'rgba(138, 132, 123, 0.15)' },
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
