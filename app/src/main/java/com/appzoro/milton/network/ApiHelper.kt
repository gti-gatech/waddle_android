package com.appzoro.milton.network

import com.appzoro.milton.model.*
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import kotlin.reflect.KClass


interface ApiHelper {

    @POST(ApiEndPoint.LOGIN_URL)
    @ErrorType(ErrorResponse::class)
    fun userLogin(@Body mJsonObject: JsonObject): Observable<Response<LoginResponse>>

    @POST(ApiEndPoint.REGISTER_URL)
    @ErrorType(ErrorResponse::class)
    fun userRegister(@Body mJsonObject: JsonObject): Observable<LoginResponse>

    @POST(ApiEndPoint.PASSWORD_RESET_URL)
    @ErrorType(ErrorResponse::class)
    fun forgotPassword(@Body mJsonObject: JsonObject): Observable<Response<DefaultResponse>>

    @POST(ApiEndPoint.SEND_OTP)
    @ErrorType(ErrorResponse::class)
    fun sendOtp(@Body jsonObject: JsonObject): Observable<DefaultResponse>

    @POST(ApiEndPoint.VERIFY_OTP)
    @ErrorType(ErrorResponse::class)
    fun verifyOtp(@Body jsonObject: JsonObject): Observable<DefaultResponse>

    @Multipart
    @POST(ApiEndPoint.UPLOAD_MEDIA)
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Header("authtoken") authToken: String
    ): Observable<MediaUploadResponse>

    @POST(ApiEndPoint.PARENT_PROFILE)
    @ErrorType(ErrorResponse::class)
    fun updateParentProfile(
        @Body mJsonObject: JsonObject,
        @Header("authtoken") authToken: String
    ): Observable<LoginResponse>

    @POST(ApiEndPoint.ADD_STUDENT)
    @ErrorType(ErrorResponse::class)
    fun addStudent(
        @Body mJsonObject: JsonObject,
        @Header("authtoken") authToken: String
    ): Observable<CommonListResponse>

    @POST(ApiEndPoint.JOIN_GROUP)
    @ErrorType(ErrorResponse::class)
    fun joinGroup(
        @Body mJsonObject: JsonObject,
        @Header("authtoken") authToken: String
    ): Observable<CommonObjectResponse>

    @GET(ApiEndPoint.TRIPS_HISTORY)
    @ErrorType(ErrorResponse::class)
    fun getTripsHistory(@Header("authtoken") authToken: String
    ): Observable<TripsResponse>

    @POST(ApiEndPoint.GROUP_EDIT)
    @ErrorType(ErrorResponse::class)
    fun updateGroupDetails(
        @Body mJsonObject: JsonObject,
        @Header("authtoken") authToken: String
    ): Observable<CommonObjectResponse>

    @POST("{fullUrl}")
    @ErrorType(ErrorResponse::class)
    fun editStudentStudent(
        @Path(value = "fullUrl", encoded = true) fullUrl: String,
        @Body mJsonObject: JsonObject,
        @Header("authtoken") authToken: String
    ): Observable<CommonObjectResponse>

    //    @GET(ApiEndPoint.SCHEDULE_TIME + "{date}")
    @GET("{fullUrl}")
    @ErrorType(ErrorResponse::class)
    fun getSchedule(
        @Path(value = "fullUrl", encoded = true) fullUrl: String,
        @Header("authtoken") authToken: String
    ): Observable<GetScheduleResponse>

    @GET("{fullUrl}")
    @ErrorType(ErrorResponse::class)
    fun getGroupDetails(
        @Path(value = "fullUrl", encoded = true) fullUrl: String,
        @Header("authtoken") authToken: String
    ): Observable<GroupDetailsResponse>

    @GET("{fullUrl}")
    @ErrorType(ErrorResponse::class)
    fun getScheduleDates(
        @Path(value = "fullUrl", encoded = true) fullUrl: String,
        @Header("authtoken") authToken: String
    ): Observable<GetScheduleResponse>

//    @GET("{fullUrl}")
    @HTTP(method = "DELETE", path = "{fullUrl}", hasBody = true)
    @ErrorType(ErrorResponse::class)
    fun withdrawSupervisor(
        @Path(value = "fullUrl", encoded = true) fullUrl: String,
        @Header("authtoken") authToken: String
    ): Observable<CommonObjectResponse>

    @HTTP(method = "PUT", path = "{fullUrl}", hasBody = true)
    @ErrorType(ErrorResponse::class)
    fun putSupervisor(
        @Path(value = "fullUrl", encoded = true) fullUrl: String,
        @Header("authtoken") authToken: String
    ): Observable<CommonObjectResponse>

    @HTTP(method = "PUT", path = "{fullUrl}", hasBody = true)
    @ErrorType(ErrorResponse::class)
    fun putTripStart(
        @Path(value = "fullUrl", encoded = true) fullUrl: String,
        @Header("authtoken") authToken: String
    ): Observable<CommonObjectResponse>

    @HTTP(method = "PUT", path = "{fullUrl}", hasBody = true)
    @ErrorType(ErrorResponse::class)
    fun putTripEnd(
        @Path(value = "fullUrl", encoded = true) fullUrl: String,
        @Header("authtoken") authToken: String
    ): Observable<CommonObjectResponse>

    @POST(ApiEndPoint.SCHEDULE_CREATE)
    @ErrorType(ErrorResponse::class)
    fun createSchedule(
        @Body mJsonObject: JsonObject,
        @Header("authtoken") authToken: String
    ): Observable<CommonStringTypeResponse>

    @HTTP(method = "DELETE", path = "schedule", hasBody = true)
    @ErrorType(ErrorResponse::class)
    fun deleteSchedules(
        @Body mJsonObject: JsonObject,
        @Header("authtoken") authToken: String
    ): Observable<JsonObject>



    @GET(ApiEndPoint.HOME_PAGE)
    @ErrorType(ErrorResponse::class)
    fun getHomePage(
        @Header("authtoken") authToken: String
    ): Observable<HomePageResponse>

    @GET
    @ErrorType(ErrorResponse::class)
    fun getCommonStop(@Url url: String): Observable<CommonListResponse>

    @GET
    @ErrorType(ErrorResponse::class)
    fun getCommonStopByRoute(@Url url: String): Observable<CommonListResponse>

    @GET
    @ErrorType(ErrorResponse::class)
    fun getGroupList(@Url url: String): Observable<CommonListResponse>

    @GET
    @ErrorType(ErrorResponse::class)
    fun getStudentList(
        @Url url: String,
        @Header("authtoken") authToken: String
    ): Observable<CommonListResponse>

    @GET
    @ErrorType(ErrorResponse::class)
    fun getMyGroups(
        @Url url: String,
        @Header("authtoken") authToken: String
    ): Observable<CommonListResponse>

    @GET(ApiEndPoint.NOTIFICATIONS)
    @ErrorType(ErrorResponse::class)
    fun getNotifications(
        @Header("authtoken") authToken: String
    ): Observable<NotificationsListResponse>

    @GET(ApiEndPoint.MESSAGES_LIST)
    @ErrorType(ErrorResponse::class)
    fun getMessagesList(
        @Header("authtoken") authToken: String
    ): Observable<MessageListResponse>

    @GET
    @ErrorType(ErrorResponse::class)
    fun getGroupsInMessages(
        @Url url: String,
        @Header("authtoken") authToken: String
    ): Observable<MessageListResponse>

    @GET
    @ErrorType(ErrorResponse::class)
    fun getTrackMapData(
        @Url url: String,
        @Header("authtoken") authToken: String
    ): Observable<GetMapTrackResponse>

    @POST(ApiEndPoint.MARK_NOTIFICATION_READ)
    @ErrorType(ErrorResponse::class)
    fun notificationReadMark(
        @Body mJsonObject: JsonObject,
        @Header("authtoken") authToken: String
    ): Observable<DefaultResponse>

    @PUT
    @ErrorType(ErrorResponse::class)
    fun tripsStart(@Url url: String,
        @Header("authtoken") authToken: String
    ): Observable<DefaultResponse>

    @POST
    @ErrorType(ErrorResponse::class)
    fun leaveGroup(
        @Url url: String,
        @Header("authtoken") authToken: String
    ): Observable<CommonListResponse>

    @PUT
    @ErrorType(ErrorResponse::class)
    fun superviseGroup(
        @Url url: String,
        @Header("authtoken") authToken: String
    ): Observable<CommonObjectResponse>

    @POST(ApiEndPoint.SCHEDULE_EDIT)
    @ErrorType(ErrorResponse::class)
    fun editSchedule(
        @Body jsonObject: JsonObject,
        @Header("authtoken") authToken: String
    ): Observable<CommonObjectResponse>

    @HTTP(method = "DELETE", path = ApiEndPoint.SCHEDULE_DELETE, hasBody = true)
    @ErrorType(ErrorResponse::class)
    fun deleteSchedule(
        @Body mJsonObject: JsonObject,
        @Header("authtoken") authToken: String
    ): Observable<CommonObjectResponse>

    @HTTP(method = "DELETE", path = "{fullUrl}", hasBody = true)
    @ErrorType(ErrorResponse::class)
    fun deleteStudent(
        @Path(value = "fullUrl", encoded = true) fullUrl: String,
        @Header("authtoken") authToken: String
    ): Observable<CommonObjectResponse>

    @Retention(AnnotationRetention.RUNTIME)
    annotation class ErrorType(val type: KClass<*>)

}