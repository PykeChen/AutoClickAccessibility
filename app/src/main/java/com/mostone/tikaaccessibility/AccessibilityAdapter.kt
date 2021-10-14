package com.mostone.tikaaccessibility

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mostone.tikaaccessibility.accessibility.base.TiKaAccessibilityService
import com.mostone.tikaaccessibility.databinding.ItemServiceBinding
import com.mostone.tikaaccessibility.utils.debounceClick
import com.mostone.tikaaccessibility.utils.isAccessibilityEnabled
import com.mostone.tikaaccessibility.utils.isAccessibilitySettingsOn

class AccessibilityAdapter(private val context: Context) :
    RecyclerView.Adapter<AccessibilityAdapter.AccessibilityViewHolder>() {

    private val mServices = mutableListOf<AccessibilityMode>()

    private val mIdleText = "空闲中"

    private val mRunningText = "已启动"

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
            val running = item.service.idleState()
            status.text = if (running) mRunningText else mIdleText
            status.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                if (running) mRunningRes else mIdleRes,
                null
            )
            root.debounceClick {

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

}