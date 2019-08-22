package com.joesorensen.starbot2;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class StarBot2 {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger log = LoggerFactory.getLogger("Startup");

        Object raw;
        try {
            raw = new JSONParser().parse(new FileReader("config.json"));
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException: config file not found. Please ensure that the config file exsists, is in the same directory as the jar, and is called config.json");
            return;
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return;
        } catch (ParseException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return;
        }
        JSONObject config = (JSONObject) raw;
    }
}
