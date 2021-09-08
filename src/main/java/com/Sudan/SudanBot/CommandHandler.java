package com.Sudan.SudanBot;

import com.Sudan.SudanBot.commands.*;
import com.Sudan.SudanBot.commands.music.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CommandHandler extends ListenerAdapter {
    public final ArrayList<ICommand> commands = new ArrayList<>();
    CommandHandler() {
        addCommand(new Ping());
        addCommand(new Invite());
        // Music
        addCommand(new Join());
    }
    private void addCommand(ICommand cmd) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.command().getName().equalsIgnoreCase(cmd.command().getName()));
        if (nameFound) {
            throw new IllegalArgumentException("A command with this name is already present");
        }
        commands.add(cmd);
    }
    @Nullable
    private ICommand getCommand(String search) {
        for (ICommand cmd : commands) {
            if (cmd.command().getName().equals(search)) {
                return cmd;
            }
        }
        return null;
    }
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        event.deferReply(true).queue();
        ICommand command = getCommand(event.getName());
        if (command == null) {
            event.reply("An unknown error occurred").setEphemeral(true).queue();
            throw new IllegalArgumentException("Command not found " + event.getName());
        }
        command.handle(event);
    }
}
