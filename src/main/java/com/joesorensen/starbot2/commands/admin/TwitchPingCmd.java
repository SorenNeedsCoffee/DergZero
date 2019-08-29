package com.joesorensen.starbot2.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.joesorensen.starbot2.commands.AdminCommand;
import com.joesorensen.starbot2.listeners.TwitchEventManager;
import com.joesorensen.starbot2.listeners.TwitchListener;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TwitchPingCmd extends AdminCommand {
    private String id;
    private boolean live = false;

    public TwitchPingCmd(String clientID) {
        this.name = "twitchping";
        this.help = "manually check if channel is live";
        this.aliases = new String[]{
                "tping"
        };
        this.guildOnly = true;
        id = clientID;
    }

    @Override
    protected void execute(CommandEvent event) {
        Logger log = LoggerFactory.getLogger("Twitch Ping");

        try {
            String url = "https://api.twitch.tv/helix/streams?user_login=" + "JoeSorensen";
            URL obj;

            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Client-ID", this.id);

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

            if (data.isEmpty()) {
                if (live)
                    TwitchEventManager.offline();
                live = false;
            } else {
                if (!live)
                    TwitchEventManager.live();
                live = true;
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
