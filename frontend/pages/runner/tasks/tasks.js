const app = getApp();
const { request } = require('../../../utils/request');
const { API } = require('../../../config/api');
const { getStatus, getOrderTypeLabel, formatTime } = require('../../../utils/orderMeta');

Page({
  data: {
    tasks: [],
    loading: true,
    submitting: false,
  },

  onLoad() {
    this.loadTasks();
  },

  // 从详情页/大厅回来自动刷新
  onShow() {
    this.loadTasks();
  },

  onPullDownRefresh() {
    this.loadTasks().finally(() => wx.stopPullDownRefresh());
  },

  async loadTasks() {
    this.setData({ loading: true });
    try {
      const list = await request({
        url: API.ORDER_MY_TASKS,
        method: 'GET',
        data: { runnerId: app.globalData.runnerId },
      });
      this.setData({ tasks: (list || []).map(this.decorate) });
    } catch (e) {
      // request.js 已 toast
    } finally {
      this.setData({ loading: false });
    }
  },

  decorate(task) {
    const status = getStatus(task.status);
    const tipNum = Number(task.tip || 0);
    // 1=已接单 → 待取货；2=进行中 → 可完成；其它状态无操作
    let action = null;
    if (task.status === 1) action = { label: '确认取货', type: 'pickup' };
    else if (task.status === 2) action = { label: '完成订单', type: 'finish' };
    return {
      ...task,
      statusLabel: status.label,
      statusColor: status.color,
      statusBg: status.bg,
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
      confirmColor: '#07c160',
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
