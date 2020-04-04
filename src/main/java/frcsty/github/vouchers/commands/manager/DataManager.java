package frcsty.github.vouchers.commands.manager;

import frcsty.github.vouchers.VouchersPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class DataManager
{

    private final VouchersPlugin    plugin;
    private       File              file;
    private       FileConfiguration config;

    public DataManager(final VouchersPlugin plugin)
    {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "vouchers.yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    private File getDataFile()
    {
        if (this.file == null)
        {
            this.file = new File(plugin.getDataFolder(), "vouchers.yml");
        }
        return this.file;
    }

    public void createConfigFile()
    {
        if (!getDataFile().exists())
        {
            try
            {
                getDataFile().createNewFile();
            }
            catch (IOException ex)
            {
                plugin.getLogger().log(Level.WARNING, "Plugin failed to create a new file! 'vouchers.yml' ", ex);
            }
        }
        this.saveFileAsync(true);
    }

    public void createFileSection()
    {
        if (!getDataConfig().isSet("vouchers"))
        {
            getDataConfig().createSection("vouchers");
        }
        this.saveFileAsync(true);
    }

    public void saveFileAsync(final boolean async)
    {
        if (!async)
        {
            try
            {
                getDataConfig().save(getDataFile());
            }
            catch (IOException ex)
            {
                plugin.getLogger().log(Level.SEVERE, "Plugin failed to save a file! 'vouchers.yml' ", ex);
            }
        }
        else
        {
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        getDataConfig().save(getDataFile());
                    }
                    catch (IOException ex)
                    {
                        plugin.getLogger().log(Level.SEVERE, "Plugin failed to save a file! 'vouchers.yml' ", ex);
                    }
                }
            }.runTaskAsynchronously(this.plugin);
        }
    }

    private FileConfiguration getDataConfig()
    {
        if (this.config == null)
        {
            this.config = YamlConfiguration.loadConfiguration(getDataFile());
        }
        return this.config;
    }

    public void setVoucherStatus(final String voucherUUID, final boolean voucherStatus)
    {
        getDataConfig().set("vouchers." + voucherUUID + ".status", voucherStatus);
    }

    public boolean getVoucherStatus(final String voucherUUID)
    {
       return getDataConfig().getBoolean("vouchers." + voucherUUID +".status");
    }

}
