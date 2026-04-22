const app = getApp();
const { request } = require('../../../utils/request');
const { API } = require('../../../config/api');
const { getStatus, getOrderTypeLabel, formatTime } = require('../../../utils/orderMeta');

Page({
  data: {
    orderId: null,
    detail: null,
    loading: true,
    notFound: false,
    cancelling: false,
  },

  onLoad(options) {
    const orderId = options.id;
    this.setData({ orderId });
    this.loadDetail(orderId);
  },

  onPullDownRefresh() {
    this.loadDetail(this.data.orderId).finally(() => wx.stopPullDownRefresh());
  },

  async loadDetail(id) {
    if (!id) {
      this.setData({ loading: false, notFound: true });
      return;
    }
    this.setData({ loading: true, notFound: false });
    try {
      const data = await request({
        url: `${API.ORDER_DETAIL}/${id}`,
        method: 'GET',
      });
      this.setData({ detail: this.decorate(data), loading: false });
    } catch (e) {
      // request.js 已 toast；订单不存在/网络错误等统一走这里
      this.setData({ loading: false, notFound: true });
    }
  },

  // 给视图层补充展示用字段，避免 wxml 里写复杂表达式
  decorate(data) {
    if (!data) return null;
    const order = data.order || {};
    const receive = data.receive || {};
    const status = getStatus(order.status);
    const tipNum = Number(order.tip || 0);
    return {
      order: {
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
        displayCreateTime: formatTime(order.createTime),
        displayExpectFinishTime: formatTime(order.expectFinishTime),
        canCancel: Number(order.status) === 0,
      },
      receive: {
        ...receive,
        hasPickupAddress: !!(receive.pickupAddress && String(receive.pickupAddress).trim()),
      },
    };
  },

  onCancelOrder() {
    wx.showModal({
      title: '取消订单',
      content: '确定要取消该订单吗？',
      confirmText: '确定取消',
      confirmColor: '#FF5B1F',
      success: (res) => {
        if (res.confirm) this.doCancel();
      },
    });
  },

  async doCancel() {
    if (this.data.cancelling || !this.data.orderId) return;
    this.setData({ cancelling: true });
    wx.showLoading({ title: '取消中...', mask: true });
    try {
      await request({
        url: API.ORDER_CANCEL,
        method: 'POST',
        silent: true,
        data: {
          orderId: this.data.orderId,
          userId: app.globalData.userId || 1,
        },
      });
      wx.hideLoading();
      wx.showToast({ title: '订单已取消', icon: 'success' });
      await this.loadDetail(this.data.orderId);
    } catch (e) {
      wx.hideLoading();
      wx.showToast({ title: e.message || '取消失败', icon: 'none' });
    } finally {
      this.setData({ cancelling: false });
    }
  },
});
