package com.Sudan.SudanBot.commands;

import com.Sudan.SudanBot.Colours;
import com.Sudan.SudanBot.Config;
import com.Sudan.SudanBot.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Invite implements ICommand {
    private static final String INVITE = "https://discord.com/api/oauth2/authorize?client_id=%s&scope=bot%%20applications.commands";

    @Override
    public CommandData command() {
        return new CommandData("invite", "Sends you a link to invite me to your own server");
    }

    @Override
    public boolean ephemeral() {
        return true;
    }

    @Override
    public void handle(SlashCommandEvent ctx) {
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Colours.INFO.colour)
                .setTitle("Click here to invite me to your own server", String.format(INVITE, Config.get("application id")))
                .build();
        ctx.getHook().sendMessageEmbeds(embed).queue();
    }
}
