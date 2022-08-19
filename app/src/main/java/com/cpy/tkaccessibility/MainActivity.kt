package com.cpy.tkaccessibility

import Utils.REQUEST_FLOAT_CODE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.cpy.tkaccessibility.accessibility.DiscountFetchService
import com.cpy.tkaccessibility.accessibility.base.tkServices
import com.cpy.tkaccessibility.databinding.ActivityMainBinding
import com.cpy.tkaccessibility.float.SuspendWindowService
import com.cpy.tkaccessibility.utils.*
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
        startService(Intent(this, SuspendWindowService::class.java))
    }

    override fun onResume() {
        super.onResume()
        checkAccessibilityState()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FLOAT_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "悬浮窗权限已经打开", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        if (mBinding.switchCheck.isChecked && !isAccessibilitySettingsOn()) {
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

    private fun updateStartTime(minutesInFuture: Int) {
        with(mBinding.etStartTime) {
            val timeStamp =
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(minutesInFuture.toLong())
            val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            setText(timeFormat.format(Date(timeStamp)))
        }
        with(mBinding.etEndTime) {
            val timeStamp =
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis((minutesInFuture + 1).toLong())
            val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            setText(timeFormat.format(Date(timeStamp)))
        }
    }

    private fun initView() {
        mBinding.layoutServiceOpen.setOnClickListener {
            goAccess()
        }
        mBinding.let {
            it.etClassName.setText(
                SPUtils.getInstance().getString(KEY_CLASS_NAME, "BannerWebViewActivity")
            )
            it.etClickPosX.setText(SPUtils.getInstance().getString(KEY_X_POS, "600"))
            it.etClickPosY.setText(SPUtils.getInstance().getString(KEY_Y_POS, "1200"))

            it.etClassName.saveTxt2Sp(KEY_CLASS_NAME)
            it.etClickPosX.saveTxt2Sp(KEY_X_POS)
            it.etClickPosY.saveTxt2Sp(KEY_Y_POS)
        }
        updateStartTime(2)
        with(mBinding.accessibilityList) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mAdapter
            mAdapter.submitList(tkServices)
        }

        mBinding.switchCheck.isChecked = SPUtils.getInstance().getBoolean(KEY_SWITCH_ONE, true)
        mBinding.switchCheck.setOnCheckedChangeListener { _, isChecked ->
            SPUtils.getInstance().put(KEY_SWITCH_ONE, isChecked)
        }

        ViewModelMain.isShowSuspendWindow.observe(this) {
            mBinding.btnOpenFloat.text = if (it == true) "开启悬浮窗" else "关闭悬浮窗"
        }
        mBinding.btnOpenFloat.setOnClickListener {
            if (ViewModelMain.isShowSuspendWindow.value == true) {
                ViewModelMain.isShowSuspendWindow.postValue(false)
            } else {
                Utils.checkSuspendedWindowPermission(this) {
                    ViewModelMain.isShowSuspendWindow.postValue(true)
                }
            }
        }
        mBinding.btnUpdateTime.setOnClickListener {
            var count = (it.tag as Int?) ?: 0
            if (count == 10) {
                count = 0
            }
            updateStartTime( count * 2)
            it.tag = ++count
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
            val lastStartTime = Date(startTime.time + increaseMs)
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
