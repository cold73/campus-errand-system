const app = getApp();
const { request } = require('../../../utils/request');
const { API } = require('../../../config/api');

// 订单类型选项（索引对应后端 orderType: 0-代拿快递, 1-代买商品, 2-代办事务, 3-其他）
const ORDER_TYPES = ['代拿快递', '代买商品', '代办事务', '其他'];

Page({
  data: {
    orderTypes: ORDER_TYPES,
    orderTypeIndex: 0,
    date: '',
    time: '',
    today: '',
    submitting: false,
    form: {
      title: '',
      content: '',
      price: '',
      tip: '',
      receiverName: '',
      receiverPhone: '',
      receiverAddress: '',
      pickupAddress: '',
    },
  },

  onLoad() {
    // 计算今天日期字符串 YYYY-MM-DD，用作 date picker 的 start 限制
    const d = new Date();
    const pad = (n) => String(n).padStart(2, '0');
    this.setData({
      today: `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`,
    });
  },

  // 通用 input 双向绑定
  onInput(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  onOrderTypeChange(e) {
    this.setData({ orderTypeIndex: Number(e.detail.value) });
  },

  onDateChange(e) {
    this.setData({ date: e.detail.value });
  },

  onTimeChange(e) {
    this.setData({ time: e.detail.value });
  },

  // 基础校验，返回错误信息；无错返回 null
  validate() {
    const f = this.data.form;
    if (!f.title.trim()) return '请输入订单标题';
    if (!f.price) return '请输入订单金额';
    if (isNaN(Number(f.price)) || Number(f.price) < 0) return '订单金额格式不正确';
    if (f.tip && (isNaN(Number(f.tip)) || Number(f.tip) < 0)) return '跑腿费格式不正确';
    if (!f.receiverName.trim()) return '请输入收货人姓名';
    if (!f.receiverPhone.trim()) return '请输入收货人电话';
    if (!/^1\d{10}$/.test(f.receiverPhone)) return '手机号格式不正确';
    if (!f.receiverAddress.trim()) return '请输入收货地址';
    // 期望完成时间：若填了，不能早于当前时刻
    if (this.data.date && this.data.time) {
      const eftMs = new Date(`${this.data.date}T${this.data.time}:00`).getTime();
      if (eftMs < Date.now()) return '期望完成时间不能早于当前时刻';
    }
    return null;
  },

  // 拼装后端需要的 LocalDateTime 字符串：YYYY-MM-DDTHH:mm:ss
  buildExpectFinishTime() {
    const { date, time } = this.data;
    if (!date || !time) return null;
    return `${date}T${time}:00`;
  },

  async onSubmit() {
    const err = this.validate();
    if (err) {
      wx.showToast({ title: err, icon: 'none' });
      return;
    }

    const f = this.data.form;
    const payload = {
      userId: app.globalData.userId,
      title: f.title.trim(),
      orderType: this.data.orderTypeIndex,
      price: Number(f.price),
      receiverName: f.receiverName.trim(),
      receiverPhone: f.receiverPhone.trim(),
      receiverAddress: f.receiverAddress.trim(),
    };

    // 可选字段：有值才带上，避免传空串给后端
    if (f.content.trim()) payload.content = f.content.trim();
    if (f.tip) payload.tip = Number(f.tip);
    if (f.pickupAddress.trim()) payload.pickupAddress = f.pickupAddress.trim();
    const eft = this.buildExpectFinishTime();
    if (eft) payload.expectFinishTime = eft;

    this.setData({ submitting: true });
    try {
      const orderId = await request({
        url: API.ORDER_CREATE,
        method: 'POST',
        data: payload,
      });
      wx.showToast({ title: `下单成功 #${orderId}`, icon: 'success' });
      setTimeout(() => {
        wx.redirectTo({ url: '/pages/order/list/list' });
      }, 1200);
    } catch (err) {
      // request.js 内部已 toast
    } finally {
      this.setData({ submitting: false });
    }
  },
});
