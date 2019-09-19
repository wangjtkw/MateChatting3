package com.example.matechatting

//从相册选择页面返回数据的请求码
const val ALBUM_REQUEST_CODE = 0x111
//从裁剪页面返回给选择页面的请求码
const val CLIP_REQUEST_CODE = 0x112
//登陆界面返回登陆状态的请求码
const val LOGIN_REQUEST_CODE = 0x120
//个性标语编辑页返回给我的信息页的请求码
const val PERSON_SIGN_REQUEST_CODE = 0x130
//方向选择页返回给我的信息页的请求码
const val DIRECT_REQUEST_CODE = 0x131
//从忘记密码页返回登陆页的请求码
const val FORGET_REQUEST_CODE = 0x132
//第一次登陆从我的信息页返回登录页请求码
const val FIRST_MY_INFO_REQUEST_CODE = 0x133

const val BASE_URL = "http://39.100.233.149:8081"
const val MORE_BASE = "/postcard"
const val PATH = "/static/"
const val HOST = "39.100.233.149"
const val PORT = 8159
var PAGE = ArrayList<Int>()
/**
 * 广播
 */
const val ADD_FRIEND_REQUEST_BROADCAST_ACTION = "com.example.matechatting.addfriend"
const val ACCEPT_FRIEND_ACTION = "com.example.matechatting.acceptfriend"
const val HAS_NEW_MESSAGE_ACTION = "com.example.matechatting.hasnewmessage"
const val ON_LINE_FRIEND = "com.example.matechatting.onlinefriend"
const val LOG_IN_ACTION = "com.example.matechatting.login"
const val LOG_OUT_ACTION = "com.example.matechatting.logout"
/**
 * 加好友错误subject
 */
const val HAS_NEW_FRIEND = "has_new_friend"

/**
 * InfoDetail subject
 */
const val HOME_ITEM = 1
const val NEW_FRIEND = 2
const val NEW_CHATTING = 3
const val FRIEND = 4

/**
 * 正则
 */
val QQ_REGEX = Regex("^[1-9][0-9]{4,9}\$")
val EMAIL_REGEX = Regex("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")

const val JOB_SERVICE_NAME = 0x9999

//方向Map key：大标签 value：小标签
val list = hashMapOf<String, List<String>>()
//大方向的集合，
val listKey = ArrayList<String>()
val mapChecked = hashMapOf<String, Boolean>()
val clicked_names = ArrayList<String>()

interface MainConstValue {

    val tabSelectedDrawableIdList: Array<Int>
        get() = arrayOf(
            R.drawable.main_home_selected,
            R.drawable.main_milelist_selected,
            R.drawable.main_mine_selected
        )

    val tabUnselectedDrawableList: Array<Int>
        get() = arrayOf(
            R.drawable.main_home_unselected,
            R.drawable.main_milelist_unselected,
            R.drawable.main_mine_unselected
        )

    val tabText: Array<String> get() = arrayOf("首页", "名片夹", "我的")
}