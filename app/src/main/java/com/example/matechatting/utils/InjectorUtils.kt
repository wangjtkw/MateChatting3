package com.example.matechatting.utils

import android.content.Context
import com.example.matechatting.database.AppDatabase
import com.example.matechatting.mainprocess.bindphone.BindPhoneRepository
import com.example.matechatting.mainprocess.bindphone.BindPhoneViewModelFactory
import com.example.matechatting.mainprocess.changepassword.ChangePasswordByTokenRepository
import com.example.matechatting.mainprocess.changepassword.ChangePasswordByTokenViewModelFactory
import com.example.matechatting.mainprocess.chatting.ChattingRepository
import com.example.matechatting.mainprocess.chatting.ChattingViewModelFactory
import com.example.matechatting.mainprocess.cliphead.ClipRepository
import com.example.matechatting.mainprocess.cliphead.ClipViewModelFactory
import com.example.matechatting.mainprocess.direction.DirectionActivityRepository
import com.example.matechatting.mainprocess.direction.DirectionActivityViewModelFactory
import com.example.matechatting.mainprocess.direction.DirectionFragmentRepository
import com.example.matechatting.mainprocess.direction.DirectionFragmentViewModelFactory
import com.example.matechatting.mainprocess.forgetpassword.ForgetPasswordRepository
import com.example.matechatting.mainprocess.forgetpassword.ForgetPasswordViewModelFactory
import com.example.matechatting.mainprocess.forgetpassword.ResetPassRepository
import com.example.matechatting.mainprocess.forgetpassword.ResetPassViewModelFactory
import com.example.matechatting.mainprocess.home.HomeItemRepository
import com.example.matechatting.mainprocess.home.HomeItemViewModelFactory
import com.example.matechatting.mainprocess.homesearch.HomeSearchRepository
import com.example.matechatting.mainprocess.homesearch.HomeSearchViewModelFactory
import com.example.matechatting.mainprocess.infodetail.InfoDetailRepository
import com.example.matechatting.mainprocess.infodetail.InfoDetailViewModelFactory
import com.example.matechatting.mainprocess.login.LoginRepository
import com.example.matechatting.mainprocess.login.LoginViewModelFactory
import com.example.matechatting.mainprocess.main.MainRepository
import com.example.matechatting.mainprocess.main.MainViewModelFactory
import com.example.matechatting.mainprocess.milelist.MileListRepository
import com.example.matechatting.mainprocess.milelist.MileListViewModelFactory
import com.example.matechatting.mainprocess.milelistsearch.MileListSearchRepository
import com.example.matechatting.mainprocess.milelistsearch.MileListSearchViewModelFactory
import com.example.matechatting.mainprocess.mine.MineRepository
import com.example.matechatting.mainprocess.mine.MineViewModelFactory
import com.example.matechatting.mainprocess.myinfo.MyInfoRepository
import com.example.matechatting.mainprocess.myinfo.MyInfoViewModelFactory
import com.example.matechatting.mainprocess.repository.UserBeanRepository
import com.example.matechatting.tcpprocess.repository.AccountRepository

object InjectorUtils {

    fun getUserBeanRepository(context: Context): UserBeanRepository {
        return UserBeanRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).userInfoDao()
        )
    }

    fun getLoginRepository(context: Context): LoginRepository {
        return LoginRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).loginDao(),
            AppDatabase.getInstance(context.applicationContext).userInfoDao()
        )
    }

    fun provideLoginViewModelFactory(context: Context): LoginViewModelFactory {
        val repository = getLoginRepository(context)
        return LoginViewModelFactory(repository, getUserBeanRepository(context))
    }

    fun provideBindPhoneViewModelFactory(context: Context): BindPhoneViewModelFactory {
        return BindPhoneViewModelFactory(BindPhoneRepository())
    }

    fun provideChangePasswordByTokenViewModelFactory(context: Context): ChangePasswordByTokenViewModelFactory {
        return ChangePasswordByTokenViewModelFactory(
            ChangePasswordByTokenRepository()
        )
    }

    fun provideForgetPasswordViewModelFactory(context: Context): ForgetPasswordViewModelFactory {
        return ForgetPasswordViewModelFactory(ForgetPasswordRepository())
    }

    fun provideResetPassViewModelFactory(context: Context): ResetPassViewModelFactory {
        return ResetPassViewModelFactory(ResetPassRepository())
    }

    fun getClipRepository(context: Context): ClipRepository {
        return ClipRepository.getInstance(
            AppDatabase.getInstance(context).userInfoDao()
        )
    }

    fun provideClipViewModelFactory(context: Context): ClipViewModelFactory {
        return ClipViewModelFactory(getClipRepository(context))
    }

    fun getHomeItemRepository(context: Context): HomeItemRepository {
        return HomeItemRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).homeItemDao()
        )
    }

    fun provideHomeItemViewModelFactory(context: Context): HomeItemViewModelFactory {
        return HomeItemViewModelFactory(
            getHomeItemRepository(context),
            getUserBeanRepository(context)
        )
    }

    fun getInfoDetailRepository(context: Context): InfoDetailRepository {
        return InfoDetailRepository.getInstance(
            AppDatabase.getInstance(context).userInfoDao()
        )
    }

    fun provideInfoDetailViewModelFactory(context: Context): InfoDetailViewModelFactory {
        return InfoDetailViewModelFactory(getUserBeanRepository(context))
    }

    fun getMineRepository(context: Context): MineRepository {
        return MineRepository.getInstance(
            AppDatabase.getInstance(context).userInfoDao()
        )
    }

    fun provideMineViewModelFactory(context: Context): MineViewModelFactory {
        return MineViewModelFactory(getMineRepository(context))
    }

    fun getAccountRepository(context: Context): AccountRepository {
        return AccountRepository.getInstance(
            AppDatabase.getInstance(context).loginDao()
        )
    }

    fun getMileListSearchRepository(context: Context): MileListSearchRepository {
        return MileListSearchRepository.getInstance(
            AppDatabase.getInstance(context).userInfoDao()
        )
    }

    fun provideMileListSearchViewModelFactory(context: Context): MileListSearchViewModelFactory {
        return MileListSearchViewModelFactory(
            getMileListSearchRepository(context)
        )
    }

    fun getMyInfoRepository(context: Context): MyInfoRepository {
        return MyInfoRepository.getInstance(
            AppDatabase.getInstance(context).userInfoDao(),
            AppDatabase.getInstance(context).loginDao(),
            AppDatabase.getInstance(context).directionDao()
        )
    }

    fun provideMyInfoViewModelFactory(context: Context): MyInfoViewModelFactory {
        return MyInfoViewModelFactory(getMyInfoRepository(context))
    }

    fun provideHomeSearchViewModelFactory(context: Context): HomeSearchViewModelFactory {
        return HomeSearchViewModelFactory(HomeSearchRepository(), getUserBeanRepository(context))
    }

    fun getDirectionActivityRepository(context: Context): DirectionActivityRepository {
        return DirectionActivityRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).directionDao()
        )
    }

    fun provideDirectionActivityViewModelFactory(context: Context): DirectionActivityViewModelFactory {
        return DirectionActivityViewModelFactory(getDirectionActivityRepository(context))
    }

    fun getDirectionFragmentRepository(context: Context): DirectionFragmentRepository {
        return DirectionFragmentRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).directionDao()
        )
    }

    fun provideDirectionFragmentViewModelFactory(context: Context): DirectionFragmentViewModelFactory {
        return DirectionFragmentViewModelFactory(getDirectionFragmentRepository(context))
    }

    fun getMileListRepository(context: Context): MileListRepository {
        return MileListRepository.getInstance(
            AppDatabase.getInstance(context).userInfoDao(),
            AppDatabase.getInstance(context).hasMessageDao()
        )
    }

    fun provideMileListViewModelFactory(context: Context): MileListViewModelFactory {
        return MileListViewModelFactory(getMileListRepository(context))
    }

    fun getMainRepository(context: Context): MainRepository {
        return MainRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).userInfoDao()
        )
    }

    fun provideMainViewModelFactory(context: Context): MainViewModelFactory {
        return MainViewModelFactory(
            getUserBeanRepository(context),
            getDirectionActivityRepository(context),
            getDirectionFragmentRepository(context)
        )
    }

    fun getChattingRepository(context: Context): ChattingRepository {
        return ChattingRepository.getInstance(
            AppDatabase.getInstance(context).chattingDao(),
            AppDatabase.getInstance(context).userInfoDao(),
            AppDatabase.getInstance(context).hasMessageDao()
        )
    }

    fun provideChattingViewModelFactory(context: Context): ChattingViewModelFactory {
        return ChattingViewModelFactory(getChattingRepository(context))
    }

//
//    fun getDetailRepository(context: Context): DetailRepository {
//        return DetailRepository.getInstance(
//            AppDatabase.getInstance(context.applicationContext).detailDao()
//        )
//    }
//
//    fun provideInformationDetailViewModelFactory(context: Context): InformationDetailViewModelFactory {
//        val repository = getDetailRepository(context)
//        return InformationDetailViewModelFactory(repository)
//    }
//
//    fun getCollectionDetailRepository(context: Context): CollectionDetailRepository {
//        return CollectionDetailRepository.getInstance(
//            AppDatabase.getInstance(context.applicationContext).collectionDetailDao()
//        )
//    }
//
//    fun provideCollectionDetailViewModelFactory(context: Context): CollectionDetailViewModelFactory {
//        val repository = getCollectionDetailRepository(context)
//        return CollectionDetailViewModelFactory(repository)
//    }
//
//    fun getDetailActivityRepository(context: Context): DetailActivityRepository {
//        return DetailActivityRepository.getInstance(
//            AppDatabase.getInstance(context.applicationContext).collectionDetailDao()
//        )
//    }
//
//    fun provideDetailActivityViewModelFactory(context: Context): DetailActivityViewModelFactory {
//        val repository = getDetailActivityRepository(context)
//        return DetailActivityViewModelFactory(repository)
//    }
}