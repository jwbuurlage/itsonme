package hack.goodnight.itsonme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.RequestInterceptor;
import retrofit.converter.GsonConverter;

public class Root {
    private static Root ourInstance = new Root();

    public static Root getInstance() {
        return ourInstance;
    }

    private Root() {
    }

    RestAdapter restAdapter;
    RequestInterceptor interceptor;
    ServerInterface service = null;

    public ServerInterface getService(){
        if(service==null){

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss.000000")
                    .create();

            interceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    if(user != null && !user.access_token.isEmpty()) {
                        request.addHeader("Authorization", "OAuth2 " + user.access_token);
                    }
                }
            };
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://itsonme-herokuapp-com-yfv0yulj1uzi.runscope.net")
                    .setConverter(new GsonConverter(gson))
                    .setRequestInterceptor(interceptor)
                    .build();
            service = restAdapter.create(ServerInterface.class);
        }
        return service;
    }

    //log out
    public void reset(){
        auth_token = "";
        loggedInFacebook = false;
        user = null;
        currentGroup = null;
    }

    public String auth_token; //facebook token
    public String gcmRegId; //google cloud messaging registration id
    public boolean loggedInFacebook = false;
    public User user;
    public Group currentGroup;
}
