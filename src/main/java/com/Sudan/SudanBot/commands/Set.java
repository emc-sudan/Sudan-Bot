package com.Sudan.SudanBot.commands;

import com.Sudan.SudanBot.Colours;
import com.Sudan.SudanBot.ICommand;
import com.Sudan.SudanBot.tables.Guild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.sql.SQLException;

public class Set implements ICommand {
    @Override
    public CommandData command() {
        return new CommandData("set", "Changes settings")
                .setDefaultEnabled(false)
                .addSubcommands(
                        new SubcommandData("unset", "Unsets an unsettable setting")
                                .addOptions(new OptionData(OptionType.STRING, "setting", "The setting to unset", true)
                                        .addChoice("Music Stage", "Music Stage")
                                        .addChoice("Stage Playlist", "Stage Playlist")
                                ),
                        new SubcommandData("music-stage", "Sets the stage channel for music")
                                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel to automatically join", true)
                                        .setChannelTypes(ChannelType.STAGE)),
                        new SubcommandData("stage-playlist", "Sets the playlist to pick songs from when in the set stage channel")
                                .addOption(OptionType.STRING, "playlist", "The URL of the playlist", true)
                );
    }

    @Override
    public boolean ephemeral() {
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(SlashCommandEvent ctx) {
        String message;
        try {
            Guild.GuildData guild = Guild.get(ctx.getGuild().getId());
            switch (ctx.getSubcommandName()) {
                case "unset" -> {
                    String setting = ctx.getOption("setting").getAsString();
                    switch (setting) {
                        case "Music Stage" -> guild.setMusicStage(null);
                        case "Stage Playlist" -> guild.setStagePlaylist(null);
                        default -> throw new IllegalArgumentException("Invalid setting to unset " + setting);
                    }
                    message = setting + "unset";
                }
                case "music-stage" -> {
                    GuildChannel channel = ctx.getOption("channel").getAsGuildChannel();
                    guild.setMusicStage(channel.getId());
                    message = "Set music stage channel to " + channel.getAsMention();
                }
                case "stage-playlist" -> {
                    String playlist = ctx.getOption("playlist").getAsString();
                    guild.setStagePlaylist(playlist);
                    message = "Set stage playlist to " + playlist;
                }
                default -> throw new IllegalArgumentException("Invalid setting " + ctx.getSubcommandName());
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Colours.ERROR.colour)
                    .setTitle("An unknown error has occurred")
                    .build();
            ctx.getHook().sendMessageEmbeds(embed).queue();
            return;
        }
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Colours.SUCCESS.colour)
                .setTitle("Settings updated")
                .setDescription(message)
                .build();
        ctx.getHook().sendMessageEmbeds(embed).queue();
    }
}
