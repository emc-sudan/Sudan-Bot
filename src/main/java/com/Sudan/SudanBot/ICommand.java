package com.Sudan.SudanBot;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface ICommand {
    CommandData command();
    void handle(SlashCommandEvent ctx);
}
