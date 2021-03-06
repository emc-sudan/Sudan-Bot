package com.Sudan.SudanBot.commands.music;

import com.Sudan.SudanBot.ICommand;
import com.Sudan.SudanBot.Music;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Join implements ICommand {
    @Override
    public CommandData command() {
        return new CommandData("join", "Makes the bot join your voice channel");
    }

    @Override
    public boolean ephemeral() {
        return true;
    }

    @Override
    public void handle(SlashCommandEvent ctx) {
        try {
            Music.join(ctx);
        } catch (IllegalStateException ignored) {
        }
    }
}
