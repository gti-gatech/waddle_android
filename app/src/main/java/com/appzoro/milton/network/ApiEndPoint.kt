package com.appzoro.milton.network

class ApiEndPoint {

    companion object {

        private const val START_URL = "http://34.209.64.150:1337/"
        const val BASE_URL = START_URL + "api/"
        const val BASE_MEDIA_URL = "https://waddlemilton.s3-us-west-2.amazonaws.com/"

        //socket url
        const val SOCKET_URL = "http://34.209.64.150:8000"
        const val updateLocation = "updateLocation"
        const val markPresent = "markPresent"
        const val joinTripUpdates = "joinTripUpdates"
        const val tripStatusChanged = "tripStatusChanged"

        const val joinMessageUpdates = "joinMessageUpdates"
        const val addMessage = "addMessage"
        const val readMessage = "readMessage"
        const val newMessageListener = "newMessageListener"

        const val TERMS_URL = START_URL + "admin/view/terms"
        const val PRIVACY_URL = START_URL + "admin/view/privacy"

        const val LOGIN_URL = "parents/login"
        const val REGISTER_URL = "parents/register"
        const val PASSWORD_RESET_URL = "parents/passwordReset"
        const val COMMON_STOP = "common/stops"
        const val ROUTE_STOP_BY_ID = "common/routeStops/"
        const val ADD_STUDENT = "students/add"
        const val EDIT_STUDENT = "student/edit/"
        const val DELETE_STUDENT = "students/"
        const val STUDENT_LIST = "students"
        const val SEARCH_GROUP = "common/groups"
        const val JOIN_GROUP = "group/joinGroup"
        const val GROUP_EDIT = "group/edit"
        const val SCHEDULE_CREATE = "schedule/create"
        const val SCHEDULE_TIME = "schedule/"//Demo live
        const val SCHEDULE_DELETE = "schedule"//Demo live
        const val SCHEDULE_EDIT = "schedule/edit"
        const val SCHEDULE_BY_MONTH = "schedule/byMonth/"//Demo live

        //        const val SCHEDULE_TIME = "/schedule/2020-07-22/false"//Demo live
        const val HOME_PAGE = "parents/homePage"
        const val PARENT_PROFILE = "parents/updateProfile"
        const val UPLOAD_MEDIA = "common/uploadMedia"
        const val GROUPS_DETAILS = "groups/details/"
        const val WITHDRAW_SUPERVISOR = "group/withdrawSupervisor/"
        const val PUT_SUPERVISOR = "group/superviseTrip/"
        const val GET_SCHEDULE_DATES = "schedule/byMonth/"

        //        const val GET_SCHEDULE_DATES = "schedule/byMonth/{{month}}/{{year}}/{{isSupervisor}}"
        const val GET_GROUPS = "groups"
        const val LEAVE_GROUPS = "group/leave/"
        const val SUPERVISE_GROUPS = "group/superviseTrip/"
        const val TRIPS_HISTORY = "trips/history"
        const val NOTIFICATIONS = "parents/notifications"
        const val MARK_NOTIFICATION_READ = "parents/markNotificationRead"
        const val TRIPS_START = "trips/start/"
        const val MAP_TRACK = "group/tripMap/"
        const val MESSAGES_LIST = "messages/list"
        const val GROUP_IN_MESSAGES_LIST = "messages/"

        const val START_TRIP = "trips/start/"
        const val END_TRIP = "trips/end/"
        const val SEND_OTP = "parents/sendOTP"
        const val VERIFY_OTP = "parents/verifyOTP"

    }
}