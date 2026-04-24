const app = getApp();
const { request } = require('../../utils/request');
const { API } = require('../../config/api');
const { sumTodayIncome } = require('../../utils/income');

const WEEK_ABBR = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];

Page({
  data: {
    timeLabel: '',
    activeOrders: 0,
    hallCount: 0,
    doingTasks: 0,
    todayIncome: '0.00',
    userId: 1,
    runnerId: 1,
  },

  onLoad() {
    this.setData({
      userId: app.globalData.userId,
      runnerId: app.globalData.runnerId,
      timeLabel: this.buildTimeLabel(),
    });
    this.loadStats();
  },

  onShow() {
    this.setData({ timeLabel: this.buildTimeLabel() });
    this.loadStats();
  },

  buildTimeLabel() {
    const d = new Date();
    const hh = String(d.getHours()).padStart(2, '0');
    const mm = String(d.getMinutes()).padStart(2, '0');
    return `${WEEK_ABBR[d.getDay()]} / ${hh}:${mm}`;
  },

  async loadStats() {
    const userId = app.globalData.userId;
    const runnerId = app.globalData.runnerId;
    const results = await Promise.allSettled([
      request({ url: API.ORDER_LIST, method: 'GET', data: { userId }, silent: true }),
      request({ url: API.ORDER_HALL, method: 'GET', silent: true }),
      request({ url: API.ORDER_MY_TASKS, method: 'GET', data: { runnerId }, silent: true }),
      request({ url: API.INCOME_LIST, method: 'GET', data: { runnerId }, silent: true }),
    ]);
    const myOrders  = results[0].status === 'fulfilled' ? (results[0].value || []) : [];
    const hallOrders = results[1].status === 'fulfilled' ? (results[1].value || []) : [];
    const myTasks   = results[2].status === 'fulfilled' ? (results[2].value || []) : [];
    const incRecords = results[3].status === 'fulfilled' ? (results[3].value || []) : [];
    this.setData({
      activeOrders: myOrders.filter((o) => [0, 1, 2].includes(o.status)).length,
      hallCount: hallOrders.length,
      doingTasks: myTasks.filter((t) => [1, 2].includes(t.status)).length,
      todayIncome: sumTodayIncome(incRecords),
    });
  },

  goMine() {
    wx.switchTab({ url: '/pages/mine/mine' });
  },

  goCreate() {
    wx.navigateTo({ url: '/pages/order/create/create' });
  },
  goOrderList() {
    wx.navigateTo({ url: '/pages/order/list/list' });
  },
  goHall() {
    wx.navigateTo({ url: '/pages/runner/hall/hall' });
  },
  goTasks() {
    wx.navigateTo({ url: '/pages/runner/tasks/tasks' });
  },
});
