const app = getApp();
const { request } = require('../../../utils/request');
const { API } = require('../../../config/api');
const { getStatus, getOrderTypeLabel, formatTime } = require('../../../utils/orderMeta');

Page({
  data: {
    orders: [],
    loading: true,
    taking: false,
  },

  onLoad() {
    this.loadHall();
  },

  // 每次回到大厅都刷新：别人抢走的订单会消失
  onShow() {
    this.loadHall();
  },

  onPullDownRefresh() {
    this.loadHall().finally(() => wx.stopPullDownRefresh());
  },

  async loadHall() {
    this.setData({ loading: true });
    try {
      const list = await request({
        url: API.ORDER_HALL,
        method: 'GET',
      });
      this.setData({ orders: (list || []).map(this.decorate) });
    } catch (e) {
      // request.js 已 toast
    } finally {
      this.setData({ loading: false });
    }
  },

  decorate(order) {
    const status = getStatus(order.status);
    const tipNum = Number(order.tip || 0);
    return {
      ...order,
      statusLabel: status.label,
      statusEn: status.en,
      statusColor: status.color,
      statusBg: status.bg,
      typeLabel: getOrderTypeLabel(order.orderType),
      displayPrice: Number(order.price || 0).toFixed(2),
      displayTip: tipNum.toFixed(2),
      hasTip: tipNum > 0,
      hasContent: !!(order.content && String(order.content).trim()),
      displayTime: formatTime(order.createTime),
    };
  },

  goDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/order/detail/detail?id=${id}` });
  },

  onTake(e) {
    const { id, title } = e.currentTarget.dataset;
    wx.showModal({
      title: '确认接单',
      content: `确定要接单「${title}」吗？`,
      confirmText: '接单',
      confirmColor: '#07c160',
      success: (res) => {
        if (res.confirm) this.doTake(id);
      },
    });
  },

  async doTake(orderId) {
    if (this.data.taking) return;
    this.setData({ taking: true });
    wx.showLoading({ title: '接单中...', mask: true });
    try {
      await request({
        url: API.ORDER_TAKE,
        method: 'POST',
        data: { orderId, runnerId: app.globalData.runnerId },
      });
      wx.hideLoading();
      wx.showToast({ title: '接单成功', icon: 'success' });
      this.loadHall();
    } catch (e) {
      wx.hideLoading();
      // request.js 已 toast
    } finally {
      this.setData({ taking: false });
    }
  },
});
