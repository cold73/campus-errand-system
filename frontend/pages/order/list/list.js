const app = getApp();
const { request } = require('../../../utils/request');
const { API } = require('../../../config/api');
const { getStatus, getUrgency, formatTime } = require('../../../utils/orderMeta');

Page({
  data: {
    orders: [],
    loading: true,
    cancelling: false,
    openId: null, // 当前左滑打开的订单 id
  },

  // 左滑交互常量
  ACTION_WIDTH: 100, // 背景按钮宽度（px），对应 wxss 的 200rpx
  SNAP_THRESHOLD: 50, // 松手吸附阈值（px）

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
    const urgency = getUrgency(order.urgencyLevel);
    const level = Number(order.urgencyLevel) || 0;
    const tipNum = Number(order.tip || 0);
    return {
      ...order,
      statusLabel: status.label,
      statusEn: status.en,
      statusColor: status.color,
      statusBg: status.bg,
      showUrgency: level > 0, // 普通不显示标签
      urgencyLabel: urgency.label, // 中文：紧急 / 超急
      urgencyColor: urgency.color,
      urgencyBg: urgency.bg,
      urgencyClass: level === 2 ? 'is-asap' : (level === 1 ? 'is-urgent' : ''),
      displayPrice: Number(order.price || 0).toFixed(2),
      displayTip: tipNum.toFixed(2),
      hasTip: tipNum > 0,
      hasContent: !!(order.content && String(order.content).trim()),
      displayTime: formatTime(order.createTime),
      canCancel: order.status === 0,
      swipeX: 0,
      animating: false,
    };
  },

  // -------- 左滑交互 --------
  onTouchStart(e) {
    const { id, cancancel } = e.currentTarget.dataset;
    this._touchId = id;
    this._canCancel = !!cancancel;
    this._startX = e.touches[0].pageX;
    this._startY = e.touches[0].pageY;
    const cur = this.data.orders.find(o => o.id === id);
    this._baseX = cur ? (cur.swipeX || 0) : 0;
    this._moved = false;
    this._swiping = false;
    // 点到另一张卡片时，先关闭已打开的
    if (this.data.openId && this.data.openId !== id) {
      this.closeOpened();
    }
    // 禁用 transition（跟手）
    this.setItem(id, { animating: false });
  },

  onTouchMove(e) {
    if (!this._canCancel) return;
    const dx = e.touches[0].pageX - this._startX;
    const dy = e.touches[0].pageY - this._startY;
    // 方向判定：主方向必须是横向，且位移 > 6px 才认为是滑动
    if (!this._swiping) {
      if (Math.abs(dx) < 6 && Math.abs(dy) < 6) return;
      if (Math.abs(dy) > Math.abs(dx)) {
        this._canCancel = false; // 竖向滚动，本次手势不再响应
        return;
      }
      this._swiping = true;
    }
    this._moved = true;
    let x = this._baseX + dx;
    if (x > 0) x = 0;
    if (x < -this.ACTION_WIDTH) x = -this.ACTION_WIDTH;
    this.setItem(this._touchId, { swipeX: x });
  },

  onTouchEnd() {
    if (!this._moved) return;
    const id = this._touchId;
    const cur = this.data.orders.find(o => o.id === id);
    if (!cur) return;
    const opened = cur.swipeX < -this.SNAP_THRESHOLD;
    this.setItem(id, {
      swipeX: opened ? -this.ACTION_WIDTH : 0,
      animating: true,
    });
    this.setData({ openId: opened ? id : (this.data.openId === id ? null : this.data.openId) });
  },

  setItem(id, patch) {
    const orders = this.data.orders.map(o => o.id === id ? { ...o, ...patch } : o);
    this.setData({ orders });
  },

  closeOpened() {
    if (!this.data.openId) return;
    const orders = this.data.orders.map(o =>
      o.id === this.data.openId ? { ...o, swipeX: 0, animating: true } : o
    );
    this.setData({ orders, openId: null });
  },

  goDetail(e) {
    // 刚刚发生过滑动 → 本次 tap 视为滑动结束，不跳转
    if (this._moved) { this._moved = false; return; }
    // 有打开的卡片 → 先关闭，不跳转
    if (this.data.openId) { this.closeOpened(); return; }
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/order/detail/detail?id=${id}` });
  },

  goCreate() {
    wx.navigateTo({ url: '/pages/order/create/create' });
  },

  onCancel(e) {
    const { id, title } = e.currentTarget.dataset;
    wx.showModal({
      title: '确认取消',
      content: `确定要取消订单「${title}」吗？`,
      confirmText: '取消订单',
      confirmColor: '#FF5B1F',
      cancelText: '再想想',
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
        data: { orderId, userId: app.globalData.userId },
      });
      wx.hideLoading();
      wx.showToast({ title: '已取消', icon: 'success' });
      this.setData({ openId: null });
      this.loadOrders();
    } catch (e) {
      wx.hideLoading();
      // request.js 已 toast
    } finally {
      this.setData({ cancelling: false });
    }
  },
});

