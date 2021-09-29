package com.Sudan.SudanBot.tables;

import com.Sudan.SudanBot.Database;
import com.Sudan.SudanBot.ITable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Guild implements ITable {
    public static GuildData get(String key) throws SQLException {
        PreparedStatement preparedStatement = Database.getConnection().prepareStatement("SELECT * FROM guilds WHERE id = ?");
        preparedStatement.setString(1, key);
        ResultSet result = preparedStatement.executeQuery();
        if (!result.next()) {
            preparedStatement.close();
            preparedStatement = Database.getConnection().prepareStatement("INSERT INTO guilds (id) VALUES (?)");
            preparedStatement.setString(1, key);
            preparedStatement.execute();
            preparedStatement.close();
            return get(key);
        }
        GuildData guildData = new GuildData(
                result.getString("id"),
                result.getString("musicStage"),
                result.getString("stagePlaylist")
        );
        preparedStatement.close();
        return guildData;
    }

    @Override
    public String create() {
        return "CREATE TABLE IF NOT EXISTS guilds ("
                + "id VARCHAR(18) PRIMARY KEY,"
                + "musicStage VARCHAR(18),"
                + "stagePlaylist TEXT"
                + ");";
    }

    public record GuildData(
            String id,
            String musicStage,
            String stagePlaylist
    ) {
        public void setMusicStage(String id) throws SQLException {
            PreparedStatement preparedStatement = Database.getConnection().prepareStatement("UPDATE guilds SET musicStage = ? WHERE id = ?");
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, this.id);
            preparedStatement.execute();
            preparedStatement.close();
        }

        public void setStagePlaylist(String playlist) throws SQLException {
            PreparedStatement preparedStatement = Database.getConnection().prepareStatement("UPDATE guilds SET stagePlaylist = ? WHERE id = ?");
            preparedStatement.setString(1, playlist);
            preparedStatement.setString(2, this.id);
            preparedStatement.execute();
            preparedStatement.close();
        }
    }
}
