package com.Sudan.SudanBot.commands;

import com.Sudan.SudanBot.Config;
import com.Sudan.SudanBot.ICommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Invite implements ICommand {
    private static final String INVITE = "https://discord.com/api/oauth2/authorize?client_id=%s&scope=bot%%20applications.commands";
    public CommandData command() {
        return new CommandData("invite", "Sends you a link to invite me to your own server");
    }

    public void handle(SlashCommandEvent ctx) {
        ctx.getHook().sendMessage(String.format(INVITE, Config.get("application id"))).setEphemeral(true).queue();
    }
}
