const { BASE_URL } = require('../config/api');

/**
 * 统一请求封装
 * - 自动拼接 baseURL
 * - 自动解包后端 Result { code, message, data }：code==200 返回 data，否则 toast 报错并 reject
 * - TODO: 接入 JWT 后在 header 注入 Authorization
 *
 * @param {Object}   options
 * @param {string}   options.url      后端接口路径（以 / 开头，如 /api/order/list）
 * @param {string}  [options.method]  HTTP 方法，默认 GET
 * @param {Object}  [options.data]    请求参数（GET 走 query，POST 走 body）
 * @param {Object}  [options.header]  额外请求头
 * @param {boolean} [options.silent]  true 时失败不 toast（用于首页并发拉取等静默场景）
 * @returns {Promise<any>} 成功 resolve(data)；失败 reject(Error)
 */
function request({ url, method = 'GET', data, header = {}, silent = false }) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: BASE_URL + url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        // TODO: 'Authorization': 'Bearer <token>',
        ...header,
      },
      success: (res) => {
        if (res.statusCode !== 200) {
          if (!silent) wx.showToast({ title: `网络错误 ${res.statusCode}`, icon: 'none' });
          reject(new Error(`HTTP ${res.statusCode}`));
          return;
        }
        const body = res.data || {};
        if (body.code === 200) {
          resolve(body.data);
        } else {
          if (!silent) wx.showToast({ title: body.message || '业务错误', icon: 'none' });
          reject(new Error(body.message || '业务错误'));
        }
      },
      fail: (err) => {
        if (!silent) wx.showToast({ title: '请求失败', icon: 'none' });
        reject(err);
      },
    });
  });
}

module.exports = { request };
