package me.comu.exeter.commands;

import me.comu.exeter.core.Core;
import me.comu.exeter.interfaces.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CreateTextChannelCommand implements ICommand {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        try {
            int input = Integer.parseInt(args.get(0));
            for (int i = 0; i < input; i++) {
                event.getGuild().createTextChannel("GRIEFED BY POODLECOORP " + (this.getRandom())).queue();
                if (i == input - 1) {
                    List<Message> messages = event.getChannel().getHistory().retrievePast(2).complete();
                    event.getChannel().deleteMessages(messages).queue();
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setColor(346626);
                    eb.setDescription(String.format("✅ Successfully created %s text channel(s) ✅", args.get(0)));
                    event.getChannel().sendMessage(eb.build()).queue();
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            event.getChannel().sendMessage("Insert an amount of text channels to create, dummy! ").queue();;
        }

    }
    private int getRandom() {
        Random randy = new Random();
        return randy.nextInt();
    }

    @Override
    public String getHelp() {
        return "Creates the specified amounts of text channels\n`" + Core.PREFIX + getInvoke() + " [amount]`\nAliases: `" + Arrays.deepToString(getAlias()) + "`";
    }

    @Override
    public String getInvoke() {
        return "ctc";
    }

    @Override
    public String[] getAlias() {
        return new String[] {"createtext","createtextchannel","createtc"};
    }
}
