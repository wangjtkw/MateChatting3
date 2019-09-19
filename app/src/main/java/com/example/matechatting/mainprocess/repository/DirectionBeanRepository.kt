package com.example.matechatting.mainprocess.repository

import android.util.Log
import com.example.matechatting.bean.*
import com.example.matechatting.database.DirectionDao
import com.example.matechatting.network.*
import com.example.matechatting.utils.ExecuteObserver
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DirectionBeanRepository(private val directionDao: DirectionDao) {

//    /**
//     * 获取所有的大方向
//     * @param parentId:大方向的父节点ID 为0
//     * @param callback:调用完成的回调
//     */
//    fun getAllBigDirectionFromDB(parentId: Int, callback: () -> Unit) {
//        directionDao.selectDirectionByParent(0)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSuccess {
//                Log.d(TAG, "getAllBigDirectionFromDB 数据库大方向获取成功")
//                callback()
//            }
//            .doOnError {
//                Log.d(TAG, "getAllBigDirectionFromDB 数据库大方向获取错误")
//                Log.d(TAG, it?.message ?: "")
//            }
//            .subscribe()
//    }

    /**
     * 根据方向的名称获取方向信息
     * @param directionName:方向名称
     * @param callback:返回方向的所有信息
     */
    fun getDirectionByName(directionName: String, callback: (DirectionBean) -> Unit) {
        directionDao.selectDirectionByName(directionName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "getDirectionByName 根据Name获取方向成功 -> $it")
                callback(it)
            }
            .doOnError {
                Log.d(TAG, "getDirectionByName 根据Name获取方向错误 -> $it")
                Log.d(TAG, it?.message ?: "")
                //需要返回一个空对象，表明数据库中没该数据，得进行网络请求
                callback(DirectionBean(0))
            }
            .subscribe()
    }

    /**
     * 根据方向的ID获取方向信息
     * @param id:方向的ID
     * @param callback:返回方向的详细信息
     */
    fun getDirectionById(id: Int, callback: (DirectionBean) -> Unit) {
        directionDao.selectDirectionById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "getDirectionById 根据ID从数据库获取方向成功 -> $it")
                callback(it)
            }
            .doOnError {
                Log.d(TAG, "getDirectionById 根据ID从数据库获取方向错误")
                Log.d(TAG, it?.message ?: "")
            }
            .subscribe()
    }

    /**
     * 更新方向的数据，主要是选中未选中状态，但实际调用的是@link [DirectionDao.insertDirection] 方法
     */
    fun updateDirection(directionBean: DirectionBean, callback: () -> Unit = {}) {
        directionDao.insertDirection(directionBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "updateDirection 方向更新成功")
                callback()
            }
            .doOnError {
                Log.d(TAG, "updateDirection 方向更新错误")
                Log.d(TAG, it?.message ?: "")
            }
            .subscribe()
    }

    /**
     * 向服务器上传更改后的方向
     * @param postDirectionBean:上传方向数据类
     * @param token:当为第一次登陆时，token不为Empty，否则为Empty
     * @param callback:上传完成后的返回结果
     */
    fun saveDirection(postDirectionBean: PostDirectionBean, token: String = "", callback: () -> Unit) {
        val gson = Gson()
        val array = gson.toJson(postDirectionBean.directions)
        val service: UpdateDirectionService = if (token.isEmpty()) {
            IdeaApi.getApiService(UpdateDirectionService::class.java)
        } else {
            IdeaApi.getApiService(UpdateDirectionService::class.java, false, OtherTokenInterceptor(token))
        }
        service.updateDirection(array)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteObserver(onExecuteNext = {
                if (it.success) {
                    Log.d(TAG, "saveDirection 方向上传服务器成功")
                    callback()
                }
            }))
    }

    /**
     * 更新方向是否选中，该处调用的是@link [DirectionDao.updateState]
     * @param isSelect:是否选中 true/false  选中/未选中
     * @param id:方向的ID
     * @param callback:更新完成后的回调
     */
    fun updateDirectionState(isSelect: Boolean, id: Int, callback: () -> Unit = {}) {
        directionDao.updateState(isSelect, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "updateDirectionState 方向状态更新成功")
                callback()
            }
            .doOnError {
                Log.d(TAG, "updateDirectionState 方向状态更新错误")
                Log.d(TAG, it?.message ?: "")
            }
            .subscribe()
    }

    /**
     * 从数据库获取所有的大方向，并转化为直接可用形式 @link [BigDirectionBean]
     * 由于服务器返回的数据顺序不符合学校情况，所有调用@link [changePosition] 来更改位置
     */
    fun getBigDirectionFromDB(callback: (List<BigDirectionBean>) -> Unit) {
        //大方向的父节点默认为0
        directionDao.selectDirectionByParent(0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                if (!it.isNullOrEmpty()) {
                    Log.d(TAG, "getBigDirectionFromDB 从数据库获取大方向成功 -> $it")
                    val bigArray = directionToBig(it)
                    val temp = changePosition(bigArray)
                    callback(temp)
                } else {
                    Log.d(TAG, "getBigDirectionFromDB 当前数据库中没用大方向数据")
                    val array = ArrayList<BigDirectionBean>()
                    callback(array)
                }
            }
            .doOnError {
                Log.d(TAG, "getBigDirectionFromDB 从数据库获取大方向错误")
                Log.d(TAG, it?.message ?: "")
                val array = ArrayList<BigDirectionBean>()
                callback(array)
            }
            .subscribe()
    }

    /**
     * 将数据库获取到的大方向数据转化为@link [BigDirectionBean]
     */
    private fun directionToBig(list: List<DirectionBean>): List<BigDirectionBean> {
        val bigArray = ArrayList<BigDirectionBean>()
        for (bean: DirectionBean in list) {
            bean.apply {
                val big = BigDirectionBean(directionName, id, isSelect)
                bigArray.add(big)
            }
        }
        return bigArray
    }

    /**
     * 从服务器获取大方向，并存入数据库
     * @param callback:回调服务器获取结果
     */
    fun getBigDirectionFromNet(callback: (List<BigDirectionBean>) -> Unit) {
        IdeaApi.getApiService(GetBigDirectionService::class.java, false).getBigDirection()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteObserver(onExecuteNext = {
                //更改获取到的数据位置
                val list = changePosition(it)
                saveBigDirection(list)
                callback(list)
            }))
    }

    /**
     * 将所有大方向从@link [BigDirectionBean] 转化为 @link [DirectionBean]
     * 并调用存入数据库的函数@link [insertDirection]
     */
    fun saveBigDirection(list: List<BigDirectionBean>) {
        for (bean: BigDirectionBean in list) {
            bean.apply {
                val directionBean = DirectionBean(id, directionName, 0)
                insertDirection(directionBean)
            }
        }
    }

    /**
     * 将方向存入数据库
     * @param directionBean:存放方向信息的数据类
     */
    private fun insertDirection(directionBean: DirectionBean) {
        directionDao.insertDirection(directionBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "insertDirection 方向存入成功")
            }
            .doOnError {
                Log.d(TAG, "insertDirection 方向存入错误")
                Log.d(TAG, it?.message ?: "")
            }
            .subscribe()
    }

    /**
     *交换某几个大方向的位置，使得更符合学校情况
     */
    private fun changePosition(list: List<BigDirectionBean>): List<BigDirectionBean> {
        val array = ArrayList<BigDirectionBean>()
        if (list.isNullOrEmpty()) {
            return array
        }
        array.add(list[8])
        array.add(list[9])
        array.add(list[14])
        for ((i, b: BigDirectionBean) in list.withIndex()) {
            if (i == 8 || i == 9 || i == 14) {
                continue
            } else {
                array.add(b)
            }
        }
        return array
    }

    /**
     * 根据大方向ID从服务器获取中小方向信息,并存入数据库
     * @param bigDirectionId:中小方向所在的大方向的ID
     * @param callback:回调直接可用的方向数据类，@link [SaveDirectionBean]
     * @link [SaveDirectionBean]:其中包含了中方向和小方向的说有信息，如果没有中方向，
     * 则@link [SaveDirectionBean.NormalDirection.direction] 信息为默认值
     */
    fun getSmallDirection(bigDirectionId: Int, callback: (SaveDirectionBean) -> Unit = {}) {
        IdeaApi.getApiService(GetSmallDirectionService::class.java, false).geySmallDirection(bigDirectionId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ExecuteObserver(onExecuteNext = {
                val saveDirectionBean = smallToSave(it)
                saveBean(saveDirectionBean)
                callback(saveDirectionBean)
            }))
    }

    /**
     * 需修改，逻辑混乱
     * 先将@link [SaveDirectionBean] 转化为 @link [DirectionBean]
     * 再调用存入数据库的函数
     */
    private fun saveBean(saveDirectionBean: SaveDirectionBean) {
        val bigId = saveDirectionBean.bigDirectionId
        if (!saveDirectionBean.normalDirectionList.isNullOrEmpty()) {
            for (normal: SaveDirectionBean.NormalDirection in saveDirectionBean.normalDirectionList!!) {
                if (normal.direction?.id == 0 && !normal.smallDirection.isNullOrEmpty()) { //只有小方向
                    for (small: Direction in normal.smallDirection!!) {
                        small.apply {
                            val bean = DirectionBean(
                                id,
                                directionName,
                                bigId,
                                bigId,
                                true
                            )
                            insertDirection(bean)
                            //插入数据库
                        }
                    }
                } else if (normal.direction != null && !normal.smallDirection.isNullOrEmpty()) { //中方向和小方向
                    val normalId = normal.direction!!.id
                    normal.direction?.apply {
                        //中标签无的bigDirectionId大标签id为0
                        val bean = DirectionBean(id, directionName, saveDirectionBean.bigDirectionId)
                        insertDirection(bean)
                        //插入数据库
                    }
                    for (small: Direction in normal.smallDirection!!) {
                        small.apply {
                            val bean =
                                DirectionBean(id, directionName, normalId, saveDirectionBean.bigDirectionId, true)
                            insertDirection(bean)
                            //插入数据库
                        }
                    }
                } else {

                }
            }
        }
    }

    /**
     * 根据大方向ID从数据库获取对应的中方向的方向信息
     * @param bigId:大方向ID
     * @param callback:回调可直接使用的中方向数据类@link [SaveDirectionBean]
     */
    fun selectNormalDirection(bigId: Int, callback: (SaveDirectionBean) -> Unit) {
        val normalArray = ArrayList<SaveDirectionBean.NormalDirection>()
        directionDao.selectDirectionByParent(bigId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                if (!(it.isNullOrEmpty() || it[0].isSmall)) { //有中方向的标签
                    var i = 0
                    for (normal: DirectionBean in it) {
                        selectSmellDirection(normal.id) { small ->
                            if (!small.isNullOrEmpty() && small[0].isSmall) {
                                val normalBean = Direction(normal.directionName, normal.id)
                                val smallDirection = ArrayList<Direction>()
                                for (bean: DirectionBean in small) {
                                    val smallBean = Direction(bean.directionName, bean.id, bean.isSelect)
                                    smallDirection.add(smallBean)
                                }
                                val normalDir = SaveDirectionBean.NormalDirection(normalBean, smallDirection)
                                normalArray.add(normalDir)
                                ++i
                                if (i == it.size) {
                                    val save = SaveDirectionBean(bigId, normalArray)
                                    callback(save)
                                }
                            }

                        }

                    }
                } else if (!it.isNullOrEmpty()) { //只有小标签
                    val normalBean = Direction()
                    val smallDirection = ArrayList<Direction>()
                    for (bean: DirectionBean in it) {
                        val smallBean = Direction(bean.directionName, bean.id, bean.isSelect)
                        smallDirection.add(smallBean)
                    }
                    val normal = SaveDirectionBean.NormalDirection(normalBean, smallDirection)
                    val save = SaveDirectionBean(bigId, arrayListOf(normal))
                    callback(save)
                } else {
                    callback(SaveDirectionBean())
                }
                return@doOnSuccess
            }
            .doOnError {
                callback(SaveDirectionBean())
            }
            .subscribe()
    }

    /**
     * 根据大方向ID获取小方向信息
     * @param bigId:大方向ID
     * @param callback:回调对应的小方向集合
     */
    private fun selectSmellDirection(bigId: Int, callback: (List<DirectionBean>) -> Unit) {
        directionDao.selectDirectionByParent(bigId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                if (!it.isNullOrEmpty() && it[0].isSmall) {
                    Log.d(TAG, "selectSmellDirection 数据库获取到的小方向 -> $it")
                    callback(it)
                }
            }
            .doOnError {
                Log.d(TAG, "selectSmellDirection 数据库小方向获取错误")
                Log.d(TAG, it?.message ?: "")
            }
            .subscribe()
    }

    /**
     * 将从服务器获取到的@link[SmallDirectionBean] 转化为直接可用的数据类 @link[SaveDirectionBean]
     */
    private fun smallToSave(smallDirectionBean: SmallDirectionBean): SaveDirectionBean {
        var saveDirectionBean = SaveDirectionBean()
        if (smallDirectionBean == null) {
            return saveDirectionBean
        }
        var isNull = false
        val bigDirectionId = smallDirectionBean.direction.id
        val normalArray = ArrayList<SaveDirectionBean.NormalDirection>()
        val nullNormal = Direction()
        val nullSmallArray = ArrayList<Direction>()
        for (c: SmallDirectionBean.ChildrenNormal in smallDirectionBean.children) {
            if (c.children.isNullOrEmpty()) {
                isNull = true
                nullSmallArray.add(c.direction)
            } else {
                val smallArray = ArrayList<Direction>()
                for (s: SmallDirectionBean.ChildrenNormal.Children in c.children) {
                    smallArray.add(s.direction)
                }
                val save = SaveDirectionBean.NormalDirection(c.direction, smallArray)
                normalArray.add(save)
            }
        }
        saveDirectionBean = if (isNull) {
            val save = SaveDirectionBean.NormalDirection(nullNormal, nullSmallArray)
            SaveDirectionBean(bigDirectionId, arrayListOf(save))
        } else {
            SaveDirectionBean(bigDirectionId, normalArray)
        }
        return saveDirectionBean
    }


    fun clearDirectionSelectState() {
        directionDao.clearSelectState()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                Log.d(TAG, "clearDirectionSelectState 选中状态清除成功")
            }
            .doOnError {
                Log.d(TAG, "clearDirectionSelectState 选中状态清除错误")
                Log.d(TAG, it?.message ?: "")
            }
            .subscribe()
    }

    companion object {
        private const val TAG = "DirectionBeanRepository"

        @Volatile
        private var instance: DirectionBeanRepository? = null

        fun getInstance(directionDao: DirectionDao) =
            instance ?: synchronized(this) {
                instance ?: DirectionBeanRepository(directionDao).also { instance = it }
            }
    }
}