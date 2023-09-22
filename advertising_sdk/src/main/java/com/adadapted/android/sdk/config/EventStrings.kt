package com.adadapted.android.sdk.config

object EventStrings {
    const val EXPIRED_EVENT = "session_expired"
    const val EXCEPTION_MESSAGE = "exception_message"
    const val GAID_UNAVAILABLE = "GAID_UNAVAILABLE"
    const val SDK_EVENT_REQUEST_FAILED = "APP_EVENT_REQUEST_FAILED"
    const val SDK_EVENT_TYPE = "sdk"

    const val ADDIT_APP_OPENED = "addit_app_opened"
    const val ADDIT_DEEPLINK_HANDLING_ERROR = "ADDIT_DEEPLINK_HANDLING_ERROR"
    const val ADDIT_PAYLOAD_IS_EMPTY = "ADDIT_PAYLOAD_IS_EMPTY"
    const val ADDIT_NO_DEEPLINK_RECEIVED = "ADDIT_NO_DEEPLINK_RECEIVED"
    const val ADDIT_PAYLOAD_PARSE_FAILED = "ADDIT_PAYLOAD_PARSE_FAILED"
    const val ADDIT_ADDED_TO_LIST = "addit_added_to_list"
    const val ADDIT_ITEM_ADDED_TO_LIST = "addit_item_added_to_list"
    const val ADDIT_DUPLICATE_PAYLOAD = "addit_duplicate_payload"
    const val ADDIT_CONTENT_FAILED = "ADDIT_CONTENT_FAILED"
    const val ADDIT_CONTENT_ITEM_FAILED = "ADDIT_CONTENT_ITEM_FAILED"
    const val NO_ADDIT_CONTENT_LISTENER = "NO_ADDIT_CONTENT_LISTENER"
    const val LISTENER_REGISTRATION_ERROR = "App did not register an AddIt Content listener"

    const val AD_PAYLOAD_IS_EMPTY = "AD_PAYLOAD_IS_EMPTY"
    const val AD_GET_REQUEST_FAILED = "AD_GET_REQUEST_FAILED"
    const val AD_EVENT_TRACK_REQUEST_FAILED = "AD_EVENT_TRACK_REQUEST_FAILED"

    const val PAYLOAD_PICKUP_ATTEMPT = "payload_pickup_attempt"
    const val PAYLOAD_PICKUP_REQUEST_FAILED = "PAYLOAD_PICKUP_REQUEST_FAILED"
    const val PAYLOAD_EVENT_REQUEST_FAILED = "PAYLOAD_EVENT_REQUEST_FAILED"
    const val NO_DEEPLINK_URL = "Did not receive a deeplink url."

    const val SESSION_REQUEST_FAILED = "SESSION_REQUEST_FAILED"

    const val KI_INIT_REQUEST_FAILED = "KI_INIT_REQUEST_FAILED"
    const val KI_EVENT_REQUEST_FAILED = "KI_EVENT_REQUEST_FAILED"

    const val USER_ADDED_TO_LIST = "user_added_to_list"
    const val USER_CROSSED_OFF_LIST = "user_crossed_off_list"
    const val USER_DELETED_FROM_LIST = "user_deleted_from_list"

    const val ATL_ITEM_ADDED_TO_LIST = "atl_item_added_to_list"
    const val ATL_ADDED_TO_LIST_FAILED = "ATL_ADDED_TO_LIST_FAILED"
    const val ATL_ADDED_TO_LIST_ITEM_FAILED = "ATL_ADDED_TO_LIST_ITEM_FAILED"
    const val ATL_AD_CLICKED = "atl_ad_clicked"

    const val POPUP_ADDED_TO_LIST = "popup_added_to_list"
    const val POPUP_ITEM_ADDED_TO_LIST = "popup_item_added_to_list"
    const val POPUP_CONTENT_FAILED = "POPUP_CONTENT_FAILED"
    const val POPUP_CONTENT_ITEM_FAILED = "POPUP_CONTENT_ITEM_FAILED"
    const val POPUP_AD_CLICKED = "popup_ad_clicked"
    const val POPUP_CONTENT_CLICKED = "popup_content_clicked"
    const val POPUP_ATL_CLICKED = "popup_atl_clicked"
    const val POPUP_URL_MALFORMED ="POPUP_URL_MALFORMED"
    const val POPUP_URL_LOAD_FAILED = "POPUP_URL_LOAD_FAILED"
}
