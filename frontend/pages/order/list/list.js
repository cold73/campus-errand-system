const app = getApp();
const { request } = require('../../../utils/request');
const { API } = require('../../../config/api');
const { getStatus, formatTime } = require('../../../utils/orderMeta');

Page({
  data: {
    orders: [],
    loading: true,
    cancelling: false,
  },

  onLoad() {
    this.loadOrders();
  },

  // 每次页面展示都刷新（从详情页返回时看到最新状态）
  onShow() {
    this.loadOrders();
  },

  onPullDownRefresh() {
    this.loadOrders().finally(() => wx.stopPullDownRefresh());
  },

  async loadOrders() {
    this.setData({ loading: true });
    try {
      const list = await request({
        url: API.ORDER_LIST,
        method: 'GET',
        data: { userId: app.globalData.userId },
      });
      this.setData({ orders: (list || []).map(this.decorate) });
    } catch (e) {
      // request.js 已 toast
    } finally {
      this.setData({ loading: false });
    }
  },

  // 给订单补充展示用字段（避免 wxml 里写复杂表达式）
  decorate(order) {
    const status = getStatus(order.status);
    const tipNum = Number(order.tip || 0);
    return {
      ...order,
      statusLabel: status.label,
      statusEn: status.en,
      statusColor: status.color,
      statusBg: status.bg,
      displayPrice: Number(order.price || 0).toFixed(2),
      displayTip: tipNum.toFixed(2),
      hasTip: tipNum > 0,
      hasContent: !!(order.content && String(order.content).trim()),
      displayTime: formatTime(order.createTime),
      canCancel: Number(order.status) === 0,
    };
  },

  goDetail(e) {
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/order/detail/detail?id=${id}` });
  },

  goCreate() {
    wx.navigateTo({ url: '/pages/order/create/create' });
  },

  onCancelOrder(e) {
    const { id } = e.currentTarget.dataset;
    wx.showModal({
      title: '取消订单',
      content: '确定要取消该订单吗？',
      confirmText: '确定取消',
      confirmColor: '#FF5B1F',
      success: (res) => {
        if (res.confirm) this.doCancel(id);
      },
    });
  },

  async doCancel(orderId) {
    if (this.data.cancelling) return;
    this.setData({ cancelling: true });
    wx.showLoading({ title: '取消中...', mask: true });
    try {
      await request({
        url: API.ORDER_CANCEL,
        method: 'POST',
        silent: true,
        data: {
          orderId,
          userId: app.globalData.userId || 1,
        },
      });
      wx.hideLoading();
      wx.showToast({ title: '订单已取消', icon: 'success' });
      await this.loadOrders();
    } catch (e) {
      wx.hideLoading();
      wx.showToast({ title: e.message || '取消失败', icon: 'none' });
    } finally {
      this.setData({ cancelling: false });
    }
  },
});
