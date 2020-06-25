package com.pozyx.nfctool.Util;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetroConfig {
    @FormUrlEncoded
    @POST("pozyxtag")
    Call<String> call(@Body String jsonObject);

}
