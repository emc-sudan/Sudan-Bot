package com.Sudan.SudanBot.commands;

import com.Sudan.SudanBot.Colours;
import com.Sudan.SudanBot.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Ping implements ICommand {
    @Override
    public CommandData command() {
        return new CommandData("ping", "Gets the current ping to Discord");
    }

    @Override
    public void handle(SlashCommandEvent ctx) {
        JDA jda = ctx.getJDA();
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Colours.INFO.colour)
                .setTitle("Pong!")
                .addField("Gateway", String.format("`%dms`", jda.getGatewayPing()), true)
                .addField("Rest", String.format("`%dms`", jda.getRestPing().complete()), true)
                .build();
        ctx.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
    }
}
