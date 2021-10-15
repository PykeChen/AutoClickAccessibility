package com.mostone.tikaaccessibility

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.mostone.tikaaccessibility.accessibility.SendGiftService
import com.mostone.tikaaccessibility.accessibility.SendMsgService
import com.mostone.tikaaccessibility.accessibility.base.tkServices
import com.mostone.tikaaccessibility.databinding.ActivityMainBinding
import com.mostone.tikaaccessibility.utils.goAccess
import com.mostone.tikaaccessibility.utils.initImmersionBar
import com.mostone.tikaaccessibility.utils.isAccessibilityEnabled

class MainActivity : AppCompatActivity() {
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) {
        AccessibilityAdapter(this)
    }


    private lateinit var mBinding: ActivityMainBinding

    private var mDialog: MaterialDialog? = null

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
        if (!isAccessibilityEnabled()) {
            if (mDialog == null) {
                mDialog = MaterialDialog(this).cancelable(false)
                    .title(R.string.confirm_title)
                    .message(R.string.accessibility_tip)
                    .positiveButton(R.string.accessibility_go) {
                        goAccess()
                    }
            }
            mDialog?.show()
        }
    }

    private fun initView() {
        with(mBinding.accessibilityList) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mAdapter
            mAdapter.submitList(tkServices)
        }
    }


    private fun initList() {
        val mTestMode = AccessibilityMode(SendMsgService(), "自动发消息", "会话页自动发送消息")
        val mSendGiftMode = AccessibilityMode(SendGiftService(), "送随机礼物", "随机普通礼物")
        tkServices.add(mSendGiftMode)
        tkServices.add(mTestMode)
    }
}