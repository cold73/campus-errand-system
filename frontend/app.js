// 小程序全局入口
App({
  /**
   * 全局数据
   * TODO: 接入微信登录 + JWT 后，userId/runnerId 从登录态获取
   */
  globalData: {
    userId: 1,
    runnerId: 1,
  },

  onLaunch() {
    console.log('校园跑腿小程序启动');
  },
});
