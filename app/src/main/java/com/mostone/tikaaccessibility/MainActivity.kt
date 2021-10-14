package com.mostone.tikaaccessibility

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mostone.tikaaccessibility.accessibility.SendMsgService
import com.mostone.tikaaccessibility.accessibility.base.TiKaAccessibilityService
import com.mostone.tikaaccessibility.accessibility.base.tkServices
import com.mostone.tikaaccessibility.databinding.ActivityMainBinding
import com.mostone.tikaaccessibility.utils.debounceClick
import com.mostone.tikaaccessibility.utils.goAccess
import com.mostone.tikaaccessibility.utils.initImmersionBar
import com.mostone.tikaaccessibility.utils.isAccessibilityEnabled

class MainActivity : AppCompatActivity() {
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) {
        AccessibilityAdapter(this)
    }


    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initImmersionBar(mBinding.accessibilityTitle)
        initList()
        initView()
    }

    override fun onResume() {
        super.onResume()
        mBinding.accessibilityOpenEntrance.visibility =
            if (isAccessibilityEnabled()) View.GONE else View.VISIBLE
    }

    private fun initView() {
        with(mBinding.accessibilityList) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mAdapter
            mAdapter.submitList(tkServices)
        }
        mBinding.accessibilityOpenEntrance.debounceClick {
            goAccess()
        }
    }


    private fun initList() {
        val mTestMode = AccessibilityMode(SendMsgService(), "自动发消息", "会话页自动发送消息")
        tkServices.add(mTestMode)
    }
}