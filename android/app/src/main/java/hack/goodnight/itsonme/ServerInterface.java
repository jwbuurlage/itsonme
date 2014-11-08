package hack.goodnight.itsonme;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 *
 * Created by tom on 11/8/14.
 */
public interface ServerInterface {
    @POST("/groups")
    void getGroup(Callback<Group> callback);

    @POST("/login")
    void login(@Body String AuthTokenString, Callback<User> callback);
}