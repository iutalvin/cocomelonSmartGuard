package com.cocomelon.smartguard;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    // Update FCM token API call
    @FormUrlEncoded
    @POST("updateFcmToken")
    Call<ResponseBody> updateFcmToken(
            @Field("userId") String userId,  // User ID
            @Field("fcmToken") String fcmToken  // FCM Token
    );
}

