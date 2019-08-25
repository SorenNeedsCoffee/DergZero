package com.joesorensen.starbot2;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TwitchListener {
    private static String id;
    Logger log = LoggerFactory.getLogger("Twitch Tracker");

    TwitchListener(String clientID) {
        id = clientID;
    }

    public static void main(String[] args) {
        try {
            new TwitchListener("6ny9c8urds69iwguc47gtgtflfkty7").track("JoeSorensen");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void track(String loginName) throws Exception {
        String url = "https://api.twitch.tv/helix/streams?user_login=" + loginName;
        URL obj;

        obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Client-ID", id);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject result = (JSONObject) new JSONParser().parse(response.toString());
        JSONArray data = (JSONArray) result.get("data");

        //print result
        System.out.println(data.isEmpty());
    }
}
