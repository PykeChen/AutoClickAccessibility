package com.mostone.tikaaccessibility

import android.os.Bundle
import android.provider.SyncStateContract
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.mostone.tikaaccessibility.accessibility.DiscountFetchService
import com.mostone.tikaaccessibility.accessibility.base.tkServices
import com.mostone.tikaaccessibility.databinding.ActivityMainBinding
import com.mostone.tikaaccessibility.utils.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val mAdapter by lazy(LazyThreadSafetyMode.NONE) {
        AccessibilityAdapter(this) {
            obtainExtraData()
        }
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
        checkAccessibilityState()
    }

    override fun onStart() {
        super.onStart()
        if (!isAccessibilitySettingsOn()) {
            if (mDialog == null) {
                mDialog = MaterialDialog(this).cancelable(true)
                    .title(R.string.confirm_title)
                    .message(R.string.accessibility_tip)
                    .positiveButton(R.string.accessibility_go) {
                        goAccess()
                    }
            }
            mDialog?.show()
        }
    }

    private fun checkAccessibilityState() {
        val on = isAccessibilitySettingsOn()
        with(mBinding.serviceState) {
            mBinding.serviceState.text = if (on) "开启" else "关闭"
        }

    }

    private fun initView() {
        mBinding.layoutServiceOpen.setOnClickListener {
            goAccess()
        }
        with(mBinding.etStartTime) {
            val timeStamp = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2)
            val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            setText(timeFormat.format(Date(timeStamp)))
        }
        with(mBinding.etEndTime) {
            val timeStamp = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3)
            val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            setText(timeFormat.format(Date(timeStamp)))
        }
        with(mBinding.accessibilityList) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mAdapter
            mAdapter.submitList(tkServices)
        }
    }


    private fun initList() {
        /* val mTestMode = AccessibilityMode(
            SendMsgService(), "自动发消息", "会话页自动发送消息：\n" +
                    " 1.需要提前进入全屏的会话页"
        )
        val mSendGiftMode = AccessibilityMode(
            SendGiftService(), "送随机礼物", "赠送礼物列表第一页的随机礼物:\n" +
                    " 1.未进入房间时必须要在首页tab下并且确保推荐列表不是空的，会在可见区域下进入任意一个房间。\n" +
                    " 2.已在房间的情况下别提前打开礼物箱，目前无法识别。"
        )
        tkServices.add(mSendGiftMode)
        tkServices.add(mTestMode)*/
        val mDiscountMode = AccessibilityMode(
            DiscountFetchService(0, mAdapter), "优惠券", "抢购优惠券"
        )
        tkServices.add(mDiscountMode)
    }

    private fun obtainExtraData(): Pair<Boolean, MutableMap<String, Any>> {
        mBinding.let {
            val resultMap = mutableMapOf<String, Any>()
            val className = it.etClassName.text.toString()
            val startTime = TimeUtils.time2Date(it.etStartTime.text.toString())
            val endTime = TimeUtils.time2Date(it.etEndTime.text.toString())
            val increaseMs = it.etIncreaseMs.text.toString().toIntOrNull() ?: 0
            val xPos = it.etClickPosX.text.toString().toFloatOrNull()
            val yPos = it.etClickPosY.text.toString().toFloatOrNull()
            val clickDuration = it.etClickDuration.text.toString().toLongOrNull()
            val error = Pair(false, resultMap)
            if (className.isEmpty()) {
                toast("拦截的类名错误")
                return error
            }
            if (xPos == null || yPos == null) {
                toast("点击位置错误")
                return error
            }
            if (startTime == null || !startTime.afterNow1m()) {
                toast("启动时间有误")
                return error
            }
            if (endTime == null || endTime.before(startTime)) {
                toast("结束时间有误,不能早于启动时间")
                return error
            }
            if (clickDuration == null || clickDuration <= 0) {
                toast("间隔时间必须>0")
                return error
            }
            val lastStartTime = Date(startTime.time - increaseMs)
            resultMap[KEY_CLASS_NAME] = className
            resultMap[KEY_START_DATE] = lastStartTime
            resultMap[KEY_END_DATE] = endTime
            resultMap[KEY_TAP_DUR] = clickDuration
            resultMap[KEY_X_POS] = xPos
            resultMap[KEY_Y_POS] = yPos
            return Pair(true, resultMap)
        }
    }
}
