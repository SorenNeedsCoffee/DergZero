package fyi.sorenneedscoffee.derg_zero.commands.fun;

import com.jagrosh.jdautilities.command.CommandEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import fyi.sorenneedscoffee.derg_zero.commands.FunCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * -=DergZero=-
 *
 * @author Soren Dangaard (joseph.md.sorensen@gmail.com)
 */
public class HelCmd extends FunCommand {

    public HelCmd() {
        this.name = "hel";
        this.help = "a \"special\" help command";
        this.aliases = new String[]{
                "halp",
                "elp",
                "hlp"
        };
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        //event.reply("hELP! ive fAlLeN and I ***CANT*** get uP!");

        String imgurl;
        while (true) {
            try {
                String url = "https://www.reddit.com/r/hmmm/best/.json?count=5&t=all";
                URL obj;

                obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject result = (JSONObject) new JSONParser().parse(response.toString());
                JSONObject data = (JSONObject) result.get("data");
                JSONArray children = (JSONArray) data.get("children");
                JSONObject post = (JSONObject) children.get((int) (Math.random() * ((5) + 1)));
                JSONObject postdata = (JSONObject) post.get("data");
                imgurl = (String) postdata.get("url");
                break;
            } catch (IOException | ParseException ignore) {
            }
        }

        event.reply(imgurl);
    }
}
