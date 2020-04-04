package frcsty.github.vouchers.utility;

import frcsty.github.vouchers.VouchersPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TabComplete implements TabCompleter
{
    private final VouchersPlugin plugin;

    public TabComplete(final VouchersPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args)
    {
        if (args.length > 3)
        {
            return Collections.emptyList();
        }

        if (args.length == 1)
        {
            final List<String> commands = new ArrayList<>();
            for (final Map.Entry<String, Method> commandPair : plugin.getManager().getManager().getCommands().entrySet())
            {
                if (commandPair.getKey().toLowerCase().startsWith(args[0].toLowerCase()))
                {
                    commands.add(commandPair.getKey());
                }
            }

            return commands;
        }
        else if (args.length == 2)
        {
            final List<String> players = new ArrayList<>();
            players.add("All");
            for (Player plr : plugin.getServer().getOnlinePlayers())
            {
                players.add(plr.getName());
            }

            return players;
        }
        else if (args.length == 3)
        {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("vouchers");
            if (section == null)
            {
                return Collections.emptyList();
            }

            return new ArrayList<>(section.getKeys(false));
        }
        return Collections.emptyList();
    }

}
