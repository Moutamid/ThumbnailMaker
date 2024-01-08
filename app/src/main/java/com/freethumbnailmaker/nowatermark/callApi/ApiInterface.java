package com.freethumbnailmaker.nowatermark.callApi;

import com.freethumbnailmaker.nowatermark.model.ThumbBG;
import com.freethumbnailmaker.nowatermark.model.ThumbnailCategoryList;
import com.freethumbnailmaker.nowatermark.model.ThumbnailInfo;
import com.freethumbnailmaker.nowatermark.model.ThumbnailKey;
import com.freethumbnailmaker.nowatermark.model.ThumbnailThumb;
import com.freethumbnailmaker.nowatermark.model.ThumbnailWithList;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("poster/background")
    Call<ThumbBG> getBackground(@Field("device") int i);

    @FormUrlEncoded
    @POST("poster/category")
    Call<ThumbnailCategoryList> getPosterCatList(@Field("key") String str, @Field("device") int i);

    @FormUrlEncoded
    @POST("poster/swiperCat")
    Call<ThumbnailWithList> getPosterCatListFull(@Field("key") String str, @Field("device") int i, @Field("cat_id") int i2, @Field("ratio") String str2);

    @FormUrlEncoded
    @POST("poster/poster")
    Call<ThumbnailInfo> getPosterDetails(@Field("key") String str, @Field("device") int i, @Field("cat_id") int i2, @Field("post_id") int i3);

    @FormUrlEncoded
    @POST("apps_key")
    Call<ThumbnailKey> getPosterKey(@Field("device") int i);

    @FormUrlEncoded
    @POST("poster/poster")
    Call<ThumbnailThumb> getPosterThumbList(@Field("key") String str, @Field("device") int i, @Field("cat_id") int i2);

    @FormUrlEncoded
    @POST("poster/stickerT")
    Call<ThumbBG> getSticker(@Field("device") int i);
}
