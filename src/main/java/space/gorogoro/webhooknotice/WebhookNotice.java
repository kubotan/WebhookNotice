package space.gorogoro.webhooknotice;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * OPChat
 * @license    GPLv3
 * @copyright  Copyright gorogoro.space 2021
 * @author     kubotan
 * @see        <a href="https://gorogoro.space">Gorogoro Server.</a>
 */
public class WebhookNotice extends JavaPlugin implements Listener {

  /**
   * JavaPlugin method onEnable.
   */
  @Override
  public void onEnable() {
    try {
      getLogger().info("The Plugin Has Been Enabled!");

      // If there is no setting file, it is created
      if(!getDataFolder().exists()){
        getDataFolder().mkdir();
      }

      File configFile = new File(getDataFolder(), "config.yml");
      if(!configFile.exists()){
        saveDefaultConfig();
      }

      final PluginManager pm = getServer().getPluginManager();
      pm.registerEvents(this, this);

      sendDiscord(getConfig().getString("url"), getConfig().getString("start"));
    } catch (Exception e) {
      logStackTrace(e);
    }
    
  }

  /**
   * JavaPlugin method onDisable.
   */
  @Override
  public void onDisable() {
    try {
      sendDiscord(getConfig().getString("url"), getConfig().getString("stop"));
    } catch (Exception e) {
      logStackTrace(e);
    }
  }

  /**
   * Output stack trace to log file.
   * @param Exception Exception
   */
  private void logStackTrace(Exception e){
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      pw.flush();
      getLogger().warning(sw.toString());
  }
  
  private void sendDiscord(String discordUrl, String content) {
    try {
        URL url = new URL(discordUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        String jsonMessage = "{\"content\": \"" + content + "\"}";

        //コネクション、通信開始
        connection.connect();
        // jsonデータを出力ストリームへ書き出す
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
        writer.write(jsonMessage);
        writer.close();
        
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            getLogger().info("Sent!");
        } else {
            getLogger().warning("Failed. Response code: " + responseCode);
        }
    } catch (Exception e) {
      logStackTrace(e);
    }
  }
}