Page({
  goCreateOrder() {
    wx.navigateTo({ url: '/pages/order/create/create' });
  },
  goOrderList() {
    wx.navigateTo({ url: '/pages/order/list/list' });
  },
  goHall() {
    wx.navigateTo({ url: '/pages/runner/hall/hall' });
  },
  goRunnerTasks() {
    wx.navigateTo({ url: '/pages/runner/tasks/tasks' });
  },
});
