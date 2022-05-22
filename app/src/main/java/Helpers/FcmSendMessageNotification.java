package Helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmSendMessageNotification {
    String token;
    String title;
    String body;
    String conversationId;
    int notificationId;
    Context mContext;
    Activity mActivity;

    private RequestQueue requestQueue;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey = "AAAACgFEwnI:APA91bFtln6TfRAneLL1jnrZmMWNy1qDdrmk9X-4zCHV5gDVeJ-58TPczeByTSTE7bOZIkxOH4vx_g7oNfKRQ__KwWKZB73hMS8co1XpVpydJ1WPN8Lx-dcj_sWowPAsdVAN9--OIp31";

    public FcmSendMessageNotification(String token, String title, String body, int notificationId ,String conversationId, Context mContext, Activity mActivity) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.conversationId = conversationId;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }
    public void sendNotification(){
        requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try{
            mainObj.put("to", token);


            // Data Object
            JSONObject dataObj = new JSONObject();
            dataObj.put("conversationId", conversationId);
            dataObj.put("dataFrom", "MessageNotification");
            dataObj.put("notificationId", notificationId);

            // Notification Object
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);

            mainObj.put("notification",notiObject);
            mainObj.put("data", dataObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("send_fcm", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("send_fcm", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;
                }
            };
            requestQueue.add(request);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
