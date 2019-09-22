package com.example.matechatting.tcpprocess.service


interface NettyListener {

    /**
     * 对消息的处理
     */
    fun onMessageResponse(messageHolder: String)

    /**
     * 当服务状态发生变化时触发
     */
    fun onServiceStatusConnectChanged(statusCode: Int)

    companion object {

        val STATUS_CONNECT_SUCCESS: Byte = 1

        val STATUS_CONNECT_CLOSED: Byte = 2

        val STATUS_CONNECT_ERROR: Byte = 0
    }
}
