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
        val mTestMode = AccessibilityMode(
            SendMsgService(), "自动发消息", "会话页自动发送消息：\n" +
                    " 1.需要提前进入全屏的会话页"
        )
        val mSendGiftMode = AccessibilityMode(
            SendGiftService(), "送随机礼物", "赠送礼物列表第一页的随机礼物:\n" +
                    " 1.未进入房间时必须要在首页tab下并且确保推荐列表不是空的，会在可见区域下进入任意一个房间。\n" +
                    " 2.已在房间的情况下别提前打开礼物箱，目前无法识别。"
        )
        tkServices.add(mSendGiftMode)
        tkServices.add(mTestMode)
    }
}