Page({
  data: {
    orderId: null,
  },

  onLoad(options) {
    this.setData({ orderId: options.id });
  },
});
