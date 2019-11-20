package com.beyondthecode.shared.data.config

import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RemoteAppConfigDataSource{

    companion object{
        const val WIFI_SSID_KEY = "wifi_ssid"
        const val WIFI_PASSWORD_KEY = "wifi_password"
        const val BADGE_PICK_UP_DAY0_START_TIME = "badge_pick_up_day0_start_time"
        const val BADGE_PICK_UP_DAY0_END_TIME = "badge_pick_up_day0_end_time"
        const val BADGE_PICK_UP_DAY1_START_TIME = "badge_pick_up_day1_start_time"
        const val BADGE_PICK_UP_DAY1_END_TIME = "badge_pick_up_day1_end_time"
        const val BREAKFAST_DAY1_START_TIME = "breakfast_day1_start_time"
        const val BREAKFAST_DAY1_END_TIME = "breakfast_day1_end_time"
        const val GOOGLE_KEYNOTE_START_TIME = "google_keynote_start_time"
        const val GOOGLE_KEYNOTE_END_TIME = "google_keynote_end_time"
        const val IO_STORE_DAY1_START_TIME = "io_store_day1_start_time"
        const val IO_STORE_DAY1_END_TIME = "io_store_day1_end_time"
        const val LUNCH_DAY1_START_TIME = "lunch_day1_start_time"
        const val LUNCH_DAY1_END_TIME = "lunch_day1_end_time"
        const val DEVELOPER_KEYNOTE_START_TIME = "developer_keynote_start_time"
        const val DEVELOPER_KEYNOTE_END_TIME = "developer_keynote_end_time"
        const val SESSIONS_DAY1_START_TIME = "sessions_day1_start_time"
        const val SESSIONS_DAY1_END_TIME = "sessions_day1_end_time"
        const val CODELABS_DAY1_START_TIME = "codelabs_day1_start_time"
        const val CODELABS_DAY1_END_TIME = "codelabs_day1_end_time"
        const val OFFICE_HOURS_DAY1_START_TIME = "office_hours_day1_start_time"
        const val OFFICE_HOURS_DAY1_END_TIME = "office_hours_day1_end_time"
        const val SANDBOXES_DAY1_START_TIME = "sandboxes_day1_start_time"
        const val SANDBOXES_DAY1_END_TIME = "sandboxes_day1_end_time"
        const val AFTER_DARK_START_TIME = "after_dark_start_time"
        const val AFTER_DARK_END_TIME = "after_dark_end_time"
        const val BADGE_DEVICE_PICK_UP_DAY2_START_TIME = "badge_device_pick_up_day2_start_time"
        const val BADGE_DEVICE_PICK_UP_DAY2_END_TIME = "badge_device_pick_up_day2_end_time"
        const val BREAKFAST_DAY2_START_TIME = "breakfast_day2_start_time"
        const val BREAKFAST_DAY2_END_TIME = "breakfast_day2_end_time"
        const val IO_STORE_DAY2_START_TIME = "io_store_day2_start_time"
        const val IO_STORE_DAY2_END_TIME = "io_store_day2_end_time"
        const val LUNCH_DAY2_START_TIME = "lunch_day2_start_time"
        const val LUNCH_DAY2_END_TIME = "lunch_day2_end_time"
        const val SESSIONS_DAY2_START_TIME = "sessions_day2_start_time"
        const val SESSIONS_DAY2_END_TIME = "sessions_day2_end_time"
        const val CODELABS_DAY2_START_TIME = "codelabs_day2_start_time"
        const val CODELABS_DAY2_END_TIME = "codelabs_day2_end_time"
        const val OFFICE_HOURS_DAY2_START_TIME = "office_hours_day2_start_time"
        const val OFFICE_HOURS_DAY2_END_TIME = "office_hours_day2_end_time"
        const val SANDBOXES_DAY2_START_TIME = "sandboxes_day2_start_time"
        const val SANDBOXES_DAY2_END_TIME = "sandboxes_day2_end_time"
        const val CONCERT_START_TIME = "concert_start_time"
        const val CONCERT_END_TIME = "concert_end_time"
        const val BADGE_DEVICE_PICK_UP_DAY3_START_TIME = "badge_device_pick_up_day3_start_time"
        const val BADGE_DEVICE_PICK_UP_DAY3_END_TIME = "badge_device_pick_up_day3_end_time"
        const val BREAKFAST_DAY3_START_TIME = "breakfast_day3_start_time"
        const val BREAKFAST_DAY3_END_TIME = "breakfast_day3_end_time"
        const val IO_STORE_DAY3_START_TIME = "io_store_day3_start_time"
        const val IO_STORE_DAY3_END_TIME = "io_store_day3_end_time"
        const val LUNCH_DAY3_START_TIME = "lunch_day3_start_time"
        const val LUNCH_DAY3_END_TIME = "lunch_day3_end_time"
        const val SESSIONS_DAY3_START_TIME = "sessions_day3_start_time"
        const val SESSIONS_DAY3_END_TIME = "sessions_day3_end_time"
        const val CODELABS_DAY3_START_TIME = "codelabs_day3_start_time"
        const val CODELABS_DAY3_END_TIME = "codelabs_day3_end_time"
        const val OFFICE_HOURS_DAY3_START_TIME = "office_hours_day3_start_time"
        const val OFFICE_HOURS_DAY3_END_TIME = "office_hours_day3_end_time"
        const val SANDBOXES_DAY3_START_TIME = "sandboxes_day3_start_time"
        const val SANDBOXES_DAY3_END_TIME = "sandboxes_day3_end_time"

        const val MAP_FEATURE_ENABLED = "map_enabled"
        const val EXPLORE_AR_FEATURE_ENABLED = "explore_ar_enabled"
        const val CODELABS_FEATURE_ENABLED = "codelabs_enabled"
        const val SEARCH_SCHEDULE_FEATURE_ENABLED = "search_schedule_enabled"
        const val SEARCH_USING_ROOM_FEATURE_ENABLED = "search_using_room_enabled"
        const val ASSISTANT_APP_FEATURE_ENABLED = "io_assistant_app_enabled"

        val DEFAULT_CACHE_EXPIRY_S = TimeUnit.MINUTES.toSeconds(12)
    }
}