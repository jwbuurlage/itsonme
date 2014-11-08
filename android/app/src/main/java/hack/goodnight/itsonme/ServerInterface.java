package hack.goodnight.itsonme;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.mime.TypedString;
import retrofit.http.Path;

/**
 *
 * Created by tom on 11/8/14.
 */
public interface ServerInterface {
    @GET("/groups")
    void getGroup(Callback<Group> callback);

    @FormUrlEncoded
    @POST("/login")
    void login(@Field("facebook_access_token") String AuthToken, Callback<User> callback);
}