package com.Sudan.SudanBot.commands;

import com.Sudan.SudanBot.ICommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Ping implements ICommand {
    public CommandData command() {
        return new CommandData("ping", "Gets the current ping to Discord");
    }

    public void handle(SlashCommandEvent ctx) {
        ctx.getHook().sendMessage(String.format("Pong!\n**Gateway**: `%dms`\n**Rest**: `%dms`",
                        ctx.getJDA().getGatewayPing(), ctx.getJDA().getRestPing().complete())).setEphemeral(true).queue();
    }
}
