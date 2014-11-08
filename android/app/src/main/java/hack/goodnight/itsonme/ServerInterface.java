package hack.goodnight.itsonme;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Path;

public interface ServerInterface {
    @GET("/groups")
    void getGroups(Callback<List<Group>> callback);

    @FormUrlEncoded
    @POST("/groups")
    void createGroup(@Field("name") String groupname, Callback<Group> callback);

    @FormUrlEncoded
    @POST("/login")
    void login(@Field("facebook_access_token") String AuthToken, Callback<User> callback);
}