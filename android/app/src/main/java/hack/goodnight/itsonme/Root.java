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
            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://private-anon-265b71629-itsonme.apiary-mock.com").build();
            ServerInterface service = restAdapter.create(ServerInterface.class);
        }
        return service;
    }

    private String auth_token;
    public void setAuth(String token){ auth_token = token; }
    public String getAuth(){ return auth_token; }
}
