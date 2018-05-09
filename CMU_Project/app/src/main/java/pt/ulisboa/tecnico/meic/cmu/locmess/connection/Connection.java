package pt.ulisboa.tecnico.meic.cmu.locmess.connection;

/**
 * Created by Sheng on 23/04/2017.
 */

import android.os.AsyncTask;

import org.json.simple.JSONObject;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
/**
 * Created by Sheng on 21/04/2017.
 */

public class Connection {

    public JSONObject execute(Action action){
        JSONObject json=null;
        try {
            json= new HttpRequestTask().execute(action).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return json;
    }
    private class HttpRequestTask extends AsyncTask<Action, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Action... params) {
            try {
                Action action=params[0];
                final String url = new Interface().getLocalhost()+action.getType().toString();
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                JSONObject response = restTemplate.postForObject(url, action.getJSON(), JSONObject.class);
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
