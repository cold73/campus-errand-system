const app = getApp();
const { request } = require('../../utils/request');
const { API } = require('../../config/api');
const { sumTodayIncome } = require('../../utils/income');

Page({
  data: {
    userId: 1,
    runnerId: 1,
    totalIncome: '0.00',
    todayIncome: '0.00',
    settledIncome: '0.00',
    orderCount: 0,
    loading: true,
  },

  onLoad() {
    this.setData({
      userId: app.globalData.userId,
      runnerId: app.globalData.runnerId,
    });
    this.loadData();
  },

  onShow() {
    this.loadData();
  },

  onPullDownRefresh() {
    this.loadData().finally(() => wx.stopPullDownRefresh());
  },

  async loadData() {
    this.setData({ loading: true });
    const runnerId = app.globalData.runnerId;
    const results = await Promise.allSettled([
      request({ url: API.INCOME_SUMMARY, method: 'GET', data: { runnerId }, silent: true }),
      request({ url: API.INCOME_LIST, method: 'GET', data: { runnerId }, silent: true }),
    ]);
    const summary  = results[0].status === 'fulfilled' ? (results[0].value || {}) : {};
    const records  = results[1].status === 'fulfilled' ? (results[1].value || []) : [];
    this.setData({
      totalIncome:   Number(summary.totalIncome   || 0).toFixed(2),
      settledIncome: Number(summary.settledIncome || 0).toFixed(2),
      orderCount:    summary.orderCount || 0,
      todayIncome:   sumTodayIncome(records),
      loading:       false,
    });
  },

  goPage(e) {
    const url = e.currentTarget.dataset.url;
    wx.navigateTo({ url });
  },

  onPlaceholder() {
    wx.showToast({ title: '敬请期待', icon: 'none' });
  },
});
