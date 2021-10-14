package com.mostone.tikaaccessibility

import com.mostone.tikaaccessibility.accessibility.base.contact.ITiKaAccessibilitySubProxy

data class AccessibilityMode(
    val service: ITiKaAccessibilitySubProxy,
    val serviceName: String,
    val description: String
)
