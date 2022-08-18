package com.cpy.tkaccessibility

import com.cpy.tkaccessibility.accessibility.base.contact.ITiKaAccessibilitySubProxy

data class AccessibilityMode(
    val service: ITiKaAccessibilitySubProxy,
    val serviceName: String,
    val description: String
)
