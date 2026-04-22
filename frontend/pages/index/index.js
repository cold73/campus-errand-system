const app = getApp();
const { request } = require('../../utils/request');
const { API } = require('../../config/api');

const WEEK_ABBR = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];

Page({
  data: {
    // 顶部时间信息
    timeLabel: '',
    // 动态数量
    activeOrders: 0, // 我的订单中未完成的数量（status 0/1/2）
    hallCount: 0,    // 大厅待接订单
    doingTasks: 0,   // 我的接单中进行中（status 1/2）
    // 测试用户
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

  // 从子页面返回刷新数量
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
    // 并发拉取，静默失败（首页不 toast 骚扰）
    const [myOrders, hallOrders, myTasks] = await Promise.all([
      request({ url: API.ORDER_LIST, method: 'GET', data: { userId }, silent: true }).catch(() => []),
      request({ url: API.ORDER_HALL, method: 'GET', silent: true }).catch(() => []),
      request({ url: API.ORDER_MY_TASKS, method: 'GET', data: { runnerId }, silent: true }).catch(() => []),
    ]);
    this.setData({
      activeOrders: (myOrders || []).filter((o) => [0, 1, 2].includes(o.status)).length,
      hallCount: (hallOrders || []).length,
      doingTasks: (myTasks || []).filter((t) => [1, 2].includes(t.status)).length,
    });
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
