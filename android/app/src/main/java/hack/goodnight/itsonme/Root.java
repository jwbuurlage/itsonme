package hack.goodnight.itsonme;

import retrofit.RestAdapter;

/**
 * Created by tom on 11/8/14.
 */
public class Root {
    private static Root ourInstance = new Root();

    public static Root getInstance() {
        return ourInstance;
    }

    private Root() {
    }

    ServerInterface service = null;

    public ServerInterface getService(){
        if(service==null){
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("https://itsonme-herokuapp-com-yfv0yulj1uzi.runscope.net").build();
            service = restAdapter.create(ServerInterface.class);
        }
        return service;
    }

    //log out
    public void reset(){ user = null; auth_token = null; }

    private String auth_token;
    public void setAuth(String token){ auth_token = token; }
    public String getAuth(){ return auth_token; }

    private User user;
    public User getUser(){ return user; }
    public void setUser(User u){ user = u; }
}
