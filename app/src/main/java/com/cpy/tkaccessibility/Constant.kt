package com.mostone.tikaaccessibility

import android.widget.Toast
import com.mostone.tikaaccessibility.utils.getAppContext

const val TiKaScheme = "com.mowang.tika"

const val PuPuScheme = "com.pupumall.customer"

const val TikaIdRes = "com.mostone.tika:id/"

const val HomePage = "$TiKaScheme.home.presentation.home.HomeActivity"

const val HomeChatPage = "$TiKaScheme.home.presentation.chat.HomeChatActivity"

const val RoomPage = "$TiKaScheme.live_room.app.presentation.room.RoomActivity"

const val SystemDialogClassName = " android.app.Dialog"

/*******************Key*******************/

const val KEY_CLASS_NAME = "cls-name"
const val KEY_X_POS = "x-pos"
const val KEY_Y_POS = "y-pos"
const val KEY_TAP_DUR = "tap_dur"
const val KEY_START_DATE = "start_date"
const val KEY_END_DATE = "end_date"


/*******************Home - id*******************/

const val HomeBottomTab1 = "${TikaIdRes}home_bbi_1"
const val HomeBottomTab2 = "${TikaIdRes}home_bbi_2"
const val HomeBottomTab3 = "${TikaIdRes}home_bbi_3"
const val HomeBottomTab4 = "${TikaIdRes}home_bbi_4"
const val HomeBottomTab5 = "${TikaIdRes}home_bbi_5"

const val HomeTabRv = "${TikaIdRes}home_rv_home"

/*******************Room - id*******************/
const val RoomGiftBtn = "${TikaIdRes}v_gift"
const val RoomGiftBoxMicUsersRv = "${TikaIdRes}rv_mic_users"
const val RoomGiftSendBtn = "${TikaIdRes}tv_gift_send"
const val RoomGiftPanel = "${TikaIdRes}cl_gift_panel"
const val RoomGiftBanner = "${TikaIdRes}banner_gift"

fun toast(content: String): Unit {
    Toast.makeText(getAppContext(), content, Toast.LENGTH_SHORT).show()
}