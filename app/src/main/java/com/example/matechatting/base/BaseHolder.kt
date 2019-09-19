package com.example.matechatting.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BaseHolder(viewDataBinding: ViewDataBinding) :RecyclerView.ViewHolder(viewDataBinding.root){

    protected val context = viewDataBinding.root.context

    open fun <T>bind(t:T){}

}