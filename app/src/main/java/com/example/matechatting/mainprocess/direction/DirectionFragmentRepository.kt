package com.example.matechatting.mainprocess.direction

import android.annotation.SuppressLint
import android.util.Log
import com.example.matechatting.base.BaseRepository
import com.example.matechatting.bean.Direction
import com.example.matechatting.bean.DirectionBean
import com.example.matechatting.bean.SaveDirectionBean
import com.example.matechatting.bean.SmallDirectionBean
import com.example.matechatting.database.DirectionDao
import com.example.matechatting.network.GetSmallDirectionService
import com.example.matechatting.network.IdeaApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DirectionFragmentRepository(private val directionDao: DirectionDao) : BaseRepository {

    @SuppressLint("CheckResult")
    fun getSmallDirection(bigDirectionId: Int, callback: (SaveDirectionBean) -> Unit = {}) {
        IdeaApi.getApiService(GetSmallDirectionService::class.java, false).geySmallDirection(bigDirectionId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val saveDirectionBean = smallToSave(it)
                saveBean(saveDirectionBean)
                callback(saveDirectionBean)
            }, {})
    }

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

    private fun insertDirection(directionBean: DirectionBean) {
        directionDao.insertDirection(directionBean)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {})
    }

    fun selectDirection(bigId: Int, callback: (SaveDirectionBean) -> Unit) {
        val normalArray = ArrayList<SaveDirectionBean.NormalDirection>()
        directionDao.selectDirectionByParent(bigId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
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
                                val normal = SaveDirectionBean.NormalDirection(normalBean, smallDirection)
                                normalArray.add(normal)
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
                return@subscribe
            }, {
                callback(SaveDirectionBean())
            })
    }

    private fun selectSmellDirection(bigId: Int, callback: (List<DirectionBean>) -> Unit) {
        directionDao.selectDirectionByParent(bigId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (!it.isNullOrEmpty() && it[0].isSmall) {
                    callback(it)
                }
            }, {})
    }

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


    companion object {
        @Volatile
        private var instance: DirectionFragmentRepository? = null

        fun getInstance(directionDao: DirectionDao) =
            instance ?: synchronized(this) {
                instance ?: DirectionFragmentRepository(directionDao).also { instance = it }
            }
    }
}