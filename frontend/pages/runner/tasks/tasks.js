const app = getApp();
const { request } = require('../../../utils/request');
const { API } = require('../../../config/api');
const { getStatus, getUrgency, getOrderTypeLabel, formatTime } = require('../../../utils/orderMeta');

Page({
  data: {
    tasks: [],
    loading: true,
    submitting: false,
    income: null, // { totalIncome, settledIncome, orderCount }
    incomeRecords: [],
    showIncomeDetail: false,
  },

  onLoad() {
    this.loadAll();
  },

  // 从详情页/大厅回来自动刷新
  onShow() {
    this.loadAll();
  },

  onPullDownRefresh() {
    this.loadAll().finally(() => wx.stopPullDownRefresh());
  },

  async loadAll() {
    this.setData({ loading: true });
    const runnerId = app.globalData.runnerId;
    try {
      const [list, summary, records] = await Promise.all([
        request({ url: API.ORDER_MY_TASKS, method: 'GET', data: { runnerId } }),
        request({ url: API.INCOME_SUMMARY, method: 'GET', data: { runnerId } }),
        request({ url: API.INCOME_LIST, method: 'GET', data: { runnerId } }),
      ]);
      this.setData({
        tasks: (list || []).map(this.decorate),
        income: summary || null,
        incomeRecords: (records || []).map(this.decorateIncome),
      });
    } catch (e) {
      // request.js 已 toast
    } finally {
      this.setData({ loading: false });
    }
  },

  decorateIncome(r) {
    const statusMap = { 0: '待结算', 1: '已结算' };
    const d = r.createTime ? r.createTime.replace('T', ' ').substring(0, 16) : '-';
    return {
      ...r,
      statusLabel: statusMap[r.status] || '-',
      displayAmount: Number(r.amount || 0).toFixed(2),
      displayTime: d,
    };
  },

  toggleIncomeDetail() {
    this.setData({ showIncomeDetail: !this.data.showIncomeDetail });
  },

  // 兼容旧调用
  loadTasks() {
    return this.loadAll();
  },

  decorate(task) {
    const status = getStatus(task.status);
    const urgency = getUrgency(task.urgencyLevel);
    const level = Number(task.urgencyLevel) || 0;
    const tipNum = Number(task.tip || 0);
    // 1=已接单 → 待取货；2=进行中 → 可完成；其它状态无操作
    let action = null;
    if (task.status === 1) action = { label: 'PICKUP →', type: 'pickup' };
    else if (task.status === 2) action = { label: 'FINISH →', type: 'finish' };
    return {
      ...task,
      statusLabel: status.label,
      statusEn: status.en,
      statusColor: status.color,
      statusBg: status.bg,
      showUrgency: level > 0,
      urgencyLabel: urgency.label,
      urgencyColor: urgency.color,
      urgencyBg: urgency.bg,
      urgencyClass: level === 2 ? 'is-asap' : (level === 1 ? 'is-urgent' : ''),
      typeLabel: getOrderTypeLabel(task.orderType),
      displayPrice: Number(task.price || 0).toFixed(2),
      displayTip: tipNum.toFixed(2),
      hasTip: tipNum > 0,
      hasContent: !!(task.content && String(task.content).trim()),
      displayTakeTime: formatTime(task.takeTime),
      displayCreateTime: formatTime(task.createTime),
      action,
    };
  },

  goDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/order/detail/detail?id=${id}` });
  },

  onAction(e) {
    const { id, type, title } = e.currentTarget.dataset;
    const isFinish = type === 'finish';
    wx.showModal({
      title: isFinish ? '确认完成' : '确认取货',
      content: isFinish
        ? `确定已送达「${title}」并完成订单吗？`
        : `确定已取到「${title}」的货品吗？`,
      confirmText: isFinish ? '完成' : '已取货',
      confirmColor: '#0A0A0A',
      success: (res) => {
        if (res.confirm) this.doAction(id, type);
      },
    });
  },

  async doAction(orderId, type) {
    if (this.data.submitting) return;
    this.setData({ submitting: true });
    const isFinish = type === 'finish';
    const url = isFinish ? API.ORDER_FINISH : API.ORDER_PICKUP;
    wx.showLoading({ title: isFinish ? '提交中...' : '处理中...', mask: true });
    try {
      await request({
        url,
        method: 'POST',
        data: { orderId, runnerId: app.globalData.runnerId },
      });
      wx.hideLoading();
      wx.showToast({ title: isFinish ? '订单已完成' : '取货成功', icon: 'success' });
      this.loadTasks();
    } catch (e) {
      wx.hideLoading();
      // request.js 已 toast
    } finally {
      this.setData({ submitting: false });
    }
  },
});
