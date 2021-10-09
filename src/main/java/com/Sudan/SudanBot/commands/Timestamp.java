package com.Sudan.SudanBot.commands;

import com.Sudan.SudanBot.Colours;
import com.Sudan.SudanBot.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.*;
import java.time.format.DateTimeParseException;

public class Timestamp implements ICommand {
    @Override
    public CommandData command() {
        return new CommandData("timestamp", "Sends the timestamp for the specified time")
                .addOptions(
                        new OptionData(OptionType.STRING, "time", "The time to get the timestamp for", true),
                        new OptionData(OptionType.INTEGER, "timezone", "Your timezone", true)
                                .addChoices(
                                        new Command.Choice("UTC-12", -12),
                                        new Command.Choice("UTC-11", -11),
                                        new Command.Choice("UTC-10", -10),
                                        new Command.Choice("UTC-9", -9),
                                        new Command.Choice("UTC-8", -8),
                                        new Command.Choice("UTC-7", -7),
                                        new Command.Choice("UTC-6", -6),
                                        new Command.Choice("UTC-5", -5),
                                        new Command.Choice("UTC-4", -4),
                                        new Command.Choice("UTC-3", -3),
                                        new Command.Choice("UTC-2", -2),
                                        new Command.Choice("UTC-1", -1),
                                        new Command.Choice("UTC", 0),
                                        new Command.Choice("UTC+1", 1),
                                        new Command.Choice("UTC+2", 2),
                                        new Command.Choice("UTC+3", 3),
                                        new Command.Choice("UTC+4", 4),
                                        new Command.Choice("UTC+5", 5),
                                        new Command.Choice("UTC+6", 6),
                                        new Command.Choice("UTC+7", 7),
                                        new Command.Choice("UTC+8", 8),
                                        new Command.Choice("UTC+9", 9),
                                        new Command.Choice("UTC+10", 10),
                                        new Command.Choice("UTC+11", 11),
                                        new Command.Choice("UTC+12 (+13 and +14 via offset)", 12)
                                ),
                        new OptionData(OptionType.INTEGER, "offset", "Offset to apply to timezone incase of half or quarter timezones or timezones > UTC+12", false)
                                .addChoices(
                                        new Command.Choice("30", 30),
                                        new Command.Choice("45", 45),
                                        new Command.Choice("+1h", 60),
                                        new Command.Choice("+2h", 120)
                                ),
                        new OptionData(OptionType.INTEGER, "date", "Relative date", false),
                        new OptionData(OptionType.STRING, "format", "The format of the timestamp", false)
                                .addChoices(
                                        new Command.Choice("Short Date Time", ":f"),
                                        new Command.Choice("Long Date Time", ":F"),
                                        new Command.Choice("Short Date", ":d"),
                                        new Command.Choice("Long Date", ":D"),
                                        new Command.Choice("Short Time", ":t"),
                                        new Command.Choice("Long Time", ":T"),
                                        new Command.Choice("Relative Time", ":R")
                                )
                );
    }

    @Override
    public boolean ephemeral() {
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(SlashCommandEvent ctx) {
        LocalTime localTime;
        try {
            localTime = LocalTime.parse(ctx.getOption("time").getAsString());
        } catch (DateTimeParseException e) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("An error occurred")
                    .setDescription("Invalid time")
                    .build();
            ctx.replyEmbeds(embed).setEphemeral(true).queue();
            return;
        }
        byte timezone_ = (byte) ctx.getOption("timezone").getAsLong();
        OptionMapping offset_ = ctx.getOption("offset");
        byte offset = (byte) (offset_ != null ? offset_.getAsLong() : 0);
        if (offset >= 60) {
            timezone_ += offset / 60;
            offset = 0;
        }
        if (timezone_ < 0) offset = (byte) -offset;
        ZoneOffset timezone = ZoneOffset.ofHoursMinutes(timezone_, offset);
        OffsetTime time = OffsetTime.of(localTime, timezone);
        OptionMapping date_ = ctx.getOption("date");
        byte date = (byte) (date_ != null ? date_.getAsLong() : 0);
        OffsetDateTime dateTime = time.atDate(LocalDate.now(timezone).plusDays(date));
        OptionMapping format_ = ctx.getOption("format");
        String format = format_ != null ? format_.getAsString() : "";
        String timestamp = String.format("<t:%d%s>", dateTime.toEpochSecond(), format);
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Colours.INFO.colour)
                .setTitle("Timestamp")
                .setDescription(timestamp)
                .setFooter(timestamp)
                .build();
        ctx.replyEmbeds(embed).queue();
    }
}
