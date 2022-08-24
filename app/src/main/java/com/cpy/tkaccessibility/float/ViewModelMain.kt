import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 用于和Service通信
 * @author cpy
 * Created on 2022-08-18 15:29
 */
object ViewModelMain : ViewModel() {

    //悬浮窗口创建 移除  基于无障碍服务
    var isShowWindow = MutableLiveData<Boolean>(false)
    //悬浮窗口创建 移除

    var isShowSuspendWindow = MutableLiveData<Boolean>(false)

    //悬浮窗口显示 隐藏
    var isVisible = MutableLiveData<Boolean>(false)

    //倒计时时间ms数
    var timeMs = MutableLiveData<Long>()

    //显示tips
    var tips = MutableLiveData<String>()


}