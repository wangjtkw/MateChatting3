package com.example.matechatting.utils

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class ExecuteObserver<T>(val onExecuteNext: (T) -> Unit = {},
                         val onExecuteComplete: () -> Unit = {},
                         val onExecuteError: (Throwable) -> Unit = {}) : Observer<T> {
    private var mDisposable: Disposable? = null

    override fun onComplete() {
        onExecuteComplete()
    }

    override fun onSubscribe(d: Disposable) {
        mDisposable = d
    }

    override fun onNext(t: T) {
        try {
            onExecuteNext(t)
            this.onComplete()
        } catch (e: Throwable) {
            this.onError(e)
        } finally {
            if (mDisposable != null && !mDisposable!!.isDisposed) {
                mDisposable!!.dispose()
            }
        }
    }
    override fun onError(e: Throwable) {
        onExecuteError(e)
    }
}