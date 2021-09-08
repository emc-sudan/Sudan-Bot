package com.Sudan.SudanBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.Command;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA api = JDABuilder.createDefault(Config.get("token")).build();
        CommandHandler commands = new CommandHandler();
        api.awaitReady();
        if (args.length >= 1 && args[0].equals("register")) {
            for (Command command : api.retrieveCommands().complete()) {
                command.delete().queue();
            }
            for (ICommand command : commands.commands) {
                api.upsertCommand(command.command()).queue();
            }
            api.shutdown();
        } else {
            api.addEventListener(commands);
        }
    }
}
