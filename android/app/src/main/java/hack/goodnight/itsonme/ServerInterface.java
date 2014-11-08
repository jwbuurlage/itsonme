package hack.goodnight.itsonme;

import java.util.List;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface ServerInterface {
    @FormUrlEncoded
    @POST("/login")
    void login(@Field("facebook_access_token") String AuthToken, Callback<User> callback);

    @FormUrlEncoded
    @PUT("/user/me")
    void sendPushToken(@Field("push_token") String pushToken, Callback<User> callback);

    @GET("/groups")
    void getGroups(Callback<List<Group>> callback);

    @GET("/groups/current")
    void getCurrentGroup(Callback<Group> callback);

    @FormUrlEncoded
    @POST("/groups")
    void createGroup(@Field("name") String groupname, Callback<Group> callback);

    @POST("/groups/{group_id}/participations")
    void joinGroup(@Path("group_id") int groupId, Callback<Group> callback);

    @DELETE("/participations/{id}")
    void leaveGroup(@Path("id") int id, Callback<Group> callback);

    @FormUrlEncoded
    @PUT("/participations/{id}")
    void updateParticipation(@Path("id") int id, @Field("is_drinking_alcohol") boolean drinkingAlcohol, @Field("is_ready") boolean isReady, Callback<Group> callback);

}