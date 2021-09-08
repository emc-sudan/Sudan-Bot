package com.Sudan.SudanBot;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA api = JDABuilder.createDefault(Config.get("token")).build();
        CommandHandler commands = new CommandHandler();
        api.awaitReady();
        if (args.length >= 1) {
            switch (args[0]) {
                case "register" -> {
                    for (Command command : api.retrieveCommands().complete()) {
                        command.delete().queue();
                    }
                    for (ICommand command : commands.commands) {
                        api.upsertCommand(command.command()).queue();
                    }
                    api.shutdown();
                }
                case "debug" -> {
                    Guild guild = api.getGuildById(args[1]);
                    assert guild != null;
                    for (Command command : guild.retrieveCommands().complete()) {
                        command.delete().queue();
                    }
                    for (ICommand command : commands.commands) {
                        guild.upsertCommand(command.command()).queue();
                    }
                    api.shutdown();
                }
                case "undebug" -> {
                    Guild guild = api.getGuildById(args[1]);
                    assert guild != null;
                    for (Command command : guild.retrieveCommands().complete()) {
                        command.delete().queue();
                    }
                    api.shutdown();
                }
                default -> throw new IllegalArgumentException("Invalid command");
            }
        } else {
            AudioSourceManagers.registerRemoteSources(Music.playerManager);
            api.addEventListener(commands);
        }
    }
}
