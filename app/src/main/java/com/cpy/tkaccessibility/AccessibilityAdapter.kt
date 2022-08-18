package com.cpy.tkaccessibility

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cpy.tkaccessibility.accessibility.DiscountFetchService
import com.cpy.tkaccessibility.accessibility.SendGiftService
import com.cpy.tkaccessibility.databinding.ItemServiceBinding
import com.cpy.tkaccessibility.utils.WakeHelper
import com.cpy.tkaccessibility.utils.commonDialog
import com.cpy.tkaccessibility.utils.debounceClick
import com.cpy.tkaccessibility.utils.sendGiftConfigDialog

class AccessibilityAdapter(
    val context: Context,
    private val obtainExtraData: () -> Pair<Boolean, MutableMap<String, Any>>
) :
    RecyclerView.Adapter<AccessibilityAdapter.AccessibilityViewHolder>(),
    DiscountFetchService.IServiceChange {

    private val mServices = mutableListOf<AccessibilityMode>()

    private val mIdleText = "空闲中"

    private val mRunningText = "已启用"

    private val mIdleRes by lazy(LazyThreadSafetyMode.NONE) {
        ContextCompat.getDrawable(context, R.drawable.bg_idle)
    }

    private val mRunningRes by lazy(LazyThreadSafetyMode.NONE) {
        ContextCompat.getDrawable(context, R.drawable.bg_running)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessibilityViewHolder =
        AccessibilityViewHolder(
            (ItemServiceBinding.inflate(
                LayoutInflater.from(context), parent, false
            ))
        )

    override fun onBindViewHolder(holder: AccessibilityViewHolder, position: Int) {
        val item = mServices[position]
        with(holder.binding) {
            title.text = item.serviceName
            description.text = item.description
            val running = !item.service.idleState()
            status.text = if (running) mRunningText else mIdleText
            status.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                if (running) mRunningRes else mIdleRes,
                null
            )
            root.debounceClick {
                when (item.service) {
                    is SendGiftService -> sendGiftConfigDialog(item, position)
                    else -> commonDialog(item, position, obtainExtraData)
                }
            }
        }
    }

    override fun getItemCount(): Int = mServices.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(services: List<AccessibilityMode>) {
        mServices.clear()
        mServices.addAll(services)
        notifyDataSetChanged()
    }

    inner class AccessibilityViewHolder(val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun idleChange(idle: Boolean, position: Int) {
        notifyItemChanged(position)
        WakeHelper.keepScreenState(!idle, activity = context as Activity)
    }

}