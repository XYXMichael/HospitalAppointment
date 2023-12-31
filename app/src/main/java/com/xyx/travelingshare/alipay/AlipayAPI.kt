package com.xyx.travelingshare.alipay

import android.app.Activity
import android.icu.text.SimpleDateFormat
import com.alipay.sdk.app.PayTask
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.Date
import java.util.Locale
import java.util.Random


object AlipayAPI {
    /**
     * @param activity
     * @param subject 商品名称
     * @param body 商品的详细描述
     * @param price 支付金额
     * @return
     */
    fun pay(
        activity: Activity?,
        subject: String,
        body: String,
        price: String
    ): String {
        val orderInfo = getOrderInfo(subject, body, price) // 创建订单信息

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        var sign = sign(orderInfo)
        println("---sign--->$sign")
        try {
            sign = URLEncoder.encode(sign, "UTF-8") // 仅需对sign 做URL编码
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        val payInfo = orderInfo + "&sign=\"" + sign + "\"&" + signType
        val alipay = PayTask(activity)
        return alipay.pay(payInfo, false)
    }

    /**
     * create the order info. 创建订单信息
     *
     */
    private fun getOrderInfo(subject: String, body: String, price: String): String {

        // 签约合作者身份ID
        var orderInfo = "partner=" + "\"" + AlipayConfig.PARTNER + "\""

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + AlipayConfig.SELLER + "\""

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + outTradeNo + "\""

        // 商品名称
        orderInfo += "&subject=\"$subject\""

        // 商品详情
        orderInfo += "&body=\"$body\""

        // 商品金额
        orderInfo += "&total_fee=\"$price\""

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\""

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\""

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\""

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\""

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\""

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\""

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo
    }

    private val outTradeNo: String
        /**
         * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
         *
         */
        private get() {
            val format = SimpleDateFormat("MMddHHmmss", Locale.getDefault())
            val date = Date()
            var key: String = format.format(date)
            val r = Random()
            key = key + r.nextInt()
            key = key.substring(0, 15)
            return key
        }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     * 待签名订单信息
     */
    private fun sign(content: String): String? {
        return SignUtils.sign(content, AlipayConfig.RSA_PRIVATE)
    }

    private val signType: String
        /**
         * get the sign type we use. 获取签名方式
         *
         */
        private get() = "sign_type=\"RSA\""
}