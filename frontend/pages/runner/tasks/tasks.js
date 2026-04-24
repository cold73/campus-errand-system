const app = getApp();
const { request } = require('../../../utils/request');
const { API } = require('../../../config/api');
const { getStatus, getUrgency, getOrderTypeLabel, formatTime } = require('../../../utils/orderMeta');

// 3 步简化流程（跑腿员侧：status 1→2→3）
const TASK_STEPS = [
  { en: 'TAKEN', cn: '已接单' },
  { en: 'DOING', cn: '进行中' },
  { en: 'DONE',  cn: '已完成' },
];

function buildTaskSteps(status) {
  const activeIndex = Number(status) - 1; // 1→0, 2→1, 3→2
  return TASK_STEPS.map((step, i) => ({
    ...step,
    state: i < activeIndex ? 'state-past' : (i === activeIndex ? 'state-active' : 'state-future'),
    isFirst: i === 0,
    isLast: i === TASK_STEPS.length - 1,
  }));
}

Page({
  data: {
    tasks: [],
    loading: true,
    submitting: false,
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
      const list = await request({ url: API.ORDER_MY_TASKS, method: 'GET', data: { runnerId } });
      this.setData({ tasks: (list || []).map(this.decorate) });
    } catch (e) {
      // request.js 已 toast
    } finally {
      this.setData({ loading: false });
    }
  },

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
      taskSteps: buildTaskSteps(task.status),
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
