/**
 * 收益相关工具函数
 */

/**
 * 从收益明细记录列表中计算今日收入
 * @param {Array} records - IncomeRecordVO 数组，包含 amount 和 createTime 字段
 * @returns {string} 今日收入，保留两位小数的字符串
 */
function sumTodayIncome(records) {
  if (!records || records.length === 0) return '0.00';
  const todayStart = new Date();
  todayStart.setHours(0, 0, 0, 0);
  const total = (records || []).reduce((sum, r) => {
    if (!r.createTime) return sum;
    // 兼容 "2026-04-23T15:00:00" 和 "2026-04-23 15:00:00" 两种格式
    const t = new Date(String(r.createTime).replace('T', ' '));
    return t >= todayStart ? sum + Number(r.amount || 0) : sum;
  }, 0);
  return total.toFixed(2);
}

module.exports = { sumTodayIncome };
