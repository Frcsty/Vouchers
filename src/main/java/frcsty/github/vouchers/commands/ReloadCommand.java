package frcsty.github.vouchers.commands;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.lib.api.general.StringUtil;
import frcsty.github.vouchers.VouchersPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class ReloadCommand
{

    @Command(permission = "vouchers.reload", aliases = "reload", usage = "reload")
    public static void execute(final CommandSender sender, final VouchersPlugin plugin, final String[] args)
    {

        final long startTime = System.currentTimeMillis();
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    plugin.reloadConfig();
                    plugin.getCommand(plugin.getConfig().getString("settings.base-command")).setTabCompleter(plugin.getTabComplete());
                }
                catch (Exception ex)
                {
                    plugin.getLogger().log(Level.SEVERE, "An exception occurred while reloading the plugin!", ex);
                }
            }
        }.runTaskAsynchronously(plugin);
        String message = StringUtil.translate(plugin.getConfig().getString("messages.reload-plugin"));

        if (message.contains("%time%"))
        {
            final String estimatedTime = String.valueOf(System.currentTimeMillis() - startTime);
            message = message.replace("%time%", estimatedTime);
        }

        sender.sendMessage(message);

    }

}
