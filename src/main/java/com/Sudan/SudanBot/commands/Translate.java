package com.Sudan.SudanBot.commands;

import com.Sudan.SudanBot.Colours;
import com.Sudan.SudanBot.Config;
import com.Sudan.SudanBot.ICommand;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;

public class Translate implements ICommand {
    private static final OkHttpClient client = new OkHttpClient();
    private static final HttpUrl.Builder url = new HttpUrl.Builder()
            .scheme("https")
            .host("api.cognitive.microsofttranslator.com")
            .addPathSegment("translate")
            .addQueryParameter("api-version", "3.0");

    @Override
    public CommandData command() {
        return new CommandData("translate", "Translates stuff")
                .addOptions(
                        new OptionData(OptionType.STRING, "text", "The stuff to translate"),
                        new OptionData(OptionType.STRING, "source", "The source language").setRequired(false),
                        new OptionData(OptionType.STRING, "destination", "The language to translate to. Default: en")
                );
    }

    @Override
    public boolean ephemeral() {
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void handle(SlashCommandEvent ctx) {
        OptionMapping source_ = ctx.getOption("source");
        String source = source_ != null ? source_.getAsString() : null;
        OptionMapping destination_ = ctx.getOption("destination");
        String destination = destination_ != null ? destination_.getAsString() : "en";
        String text = ctx.getOption("text").getAsString();

        url.setQueryParameter("to", destination);
        url.setQueryParameter("from", source);

        Request request = new Request.Builder()
                .url(url.build())
                .post(RequestBody.create(null, String.format("[{\"Text\": \"%s\"}]", text)))
                .addHeader("Ocp-Apim-Subscription-Key", Config.get("translate key"))
                .addHeader("Ocp-Apim-Subscription-Region", Config.get("translate region", "global"))
                .addHeader("Content-type", "application/json")
                .build();
        String response;
        try {
            response = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Could not translate")
                    .setColor(Colours.ERROR.colour)
                    .build();
            ctx.replyEmbeds(embed).setEphemeral(true).queue();
            return;
        }
        JsonObject result;
        try {
            result = new JsonParser().parse(response).getAsJsonArray().get(0).getAsJsonObject();
        } catch (IllegalStateException ignored) {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("Could not translate")
                    .setDescription("This probably means you inputted an invalid language, for a list of valid languages click [here](https://docs.microsoft.com/en-us/azure/cognitive-services/translator/language-support)")
                    .setColor(Colours.ERROR.colour)
                    .build();
            ctx.replyEmbeds(embed).setEphemeral(true).queue();
            return;
        }
        String language = source != null ? source : result.get("detectedLanguage").getAsJsonObject().get("language").getAsString();
        String translation = result.get("translations").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString();

        MessageEmbed embed = new EmbedBuilder()
                .setTitle(String.format("%s -> %s", language, destination))
                .setDescription(translation)
                .setColor(Colours.INFO.colour)
                .build();
        ctx.replyEmbeds(embed).setEphemeral(false).queue();
    }
}
