package pl.wolny.junglenokaut.utilities;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ConfigFile {
    private File file;
    private YamlConfiguration yamlConfig;
    private Plugin plugin;

    public ConfigFile(Plugin plugin){
        file = new File("plugins/" + plugin.getDescription().getName() +  "/settings.yml");
        yamlConfig = YamlConfiguration.loadConfiguration(file);
    }
    public Boolean check(String[] values){
        for (String value: values) {
            if(yamlConfig.get(value) == null){return false;}
        }
        return true;
    }

    public YamlConfiguration getYamlConfig() {
        return yamlConfig;
    }
    public Object get(String path){
        return yamlConfig.get(path);
    }
    public File getFile(){
        return file;
    }
    public void set(String path, String value){
        yamlConfig.set(path, value);
    }
    public void setYamlConfig(YamlConfiguration yamlConfiguration){
        yamlConfig = yamlConfiguration;
    }
    public void save(){
        try {
            yamlConfig.save(file);
        }
        catch (IOException iOException) {
            Bukkit.getLogger().info("Can not create config - exiting");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }
}
