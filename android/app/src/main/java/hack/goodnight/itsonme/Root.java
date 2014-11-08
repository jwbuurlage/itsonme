package hack.goodnight.itsonme;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.RequestInterceptor;

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
                    .setRequestInterceptor(interceptor)
                    .build();
            service = restAdapter.create(ServerInterface.class);
        }
        return service;
    }

    //log out
    public void reset(){ user = null; auth_token = null; groupList.clear(); currentGroup = null; }

    private String auth_token;
    public void setAuth(String token){ auth_token = token; }
    public String getAuth(){ return auth_token; }

    private User user;
    public User getUser(){ return user; }
    public void setUser(User u){ user = u; }

    public List<Group> groupList;
    public Group currentGroup;
}
