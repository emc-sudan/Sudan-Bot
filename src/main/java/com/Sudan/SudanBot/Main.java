package com.Sudan.SudanBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @SuppressWarnings("DuplicatedCode")
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA api = JDABuilder.createDefault(Config.get("token")).build();
        CommandHandler commands = new CommandHandler();
        api.awaitReady();
        if (args.length >= 1) {
            switch (args[0]) {
                case "register" -> {
                    LOGGER.info("Registering commands globally");
                    for (Command command : api.retrieveCommands().complete()) {
                        if (commands.commands.stream().noneMatch(
                                (cmd) -> cmd.command().getName().equals(command.getName()))) {
                            LOGGER.info("Deleting " + command.getName());
                            command.delete().queue();
                        }
                    }
                    for (ICommand command : commands.commands) {
                        LOGGER.info("Registering/Updating " + command.command().getName());
                        api.upsertCommand(command.command()).queue();
                    }
                    LOGGER.info("Done!");
                    api.shutdown();
                }
                case "debug" -> {
                    Guild guild = api.getGuildById(args[1]);
                    assert guild != null;
                    LOGGER.info("Registering commands in " + guild.getName());
                    for (Command command : guild.retrieveCommands().complete()) {
                        if (commands.commands.stream().noneMatch(
                                (cmd) -> cmd.command().getName().equals(command.getName()))) {
                            LOGGER.info(String.format("Deleting %s in %s", command.getName(), guild.getName()));
                            command.delete().queue();
                        }
                    }
                    for (ICommand command : commands.commands) {
                        LOGGER.info(String.format("Registering/Updating %s in %s", command.command().getName(), guild.getName()));
                        guild.upsertCommand(command.command()).queue();
                    }
                    LOGGER.info("Done!");
                    api.shutdown();
                }
                case "undebug" -> {
                    Guild guild = api.getGuildById(args[1]);
                    assert guild != null;
                    LOGGER.info("Deleting commands in " + guild.getName());
                    for (Command command : guild.retrieveCommands().complete()) {
                        LOGGER.info(String.format("Deleting %s in %s", command.getName(), guild.getName()));
                        command.delete().queue();
                    }
                    LOGGER.info("Done!");
                    api.shutdown();
                }
                case "initdb" -> {
                    LOGGER.info("Initialising database");
                    ITable[] tables = new ITable[]{
                            new com.Sudan.SudanBot.tables.Guild(),
                    };
                    try {
                        Connection connection = Database.getConnection();
                        for (ITable table : tables) {
                            LOGGER.info("Created " + table.getClass().getName());
                            connection.createStatement().execute(table.create());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    LOGGER.info("Done!");
                    api.shutdown();
                }
                default -> throw new IllegalArgumentException("Invalid command");
            }
        } else {
            api.addEventListener(commands);
        }
    }
}
