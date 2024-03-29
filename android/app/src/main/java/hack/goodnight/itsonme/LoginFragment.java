package hack.goodnight.itsonme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginFragment extends Fragment {
    private static final String TAG = "ITSONME_LoginFragment";
    private UiLifecycleHelper uiHelper;
    private View view;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_login, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("email", "public_profile", "user_friends"));
        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state == SessionState.OPENED) {
            Log.i(TAG, "Facebook Session State = OPENED");
            //If it is not the second login
            //if(session.getAccessToken() != Root.getInstance().auth_token) {
                Root.getInstance().auth_token = session.getAccessToken();
                Root.getInstance().loggedInFacebook = true;
                Login activity = (Login)getActivity();
                activity.openLobby();
            //}

        } else if (state.isClosed()) {
            Root.getInstance().reset();
            Log.i(TAG, "Facebook Session State = CLOSED");
        }
    }

    @Override
    public void onResume() {
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


}
