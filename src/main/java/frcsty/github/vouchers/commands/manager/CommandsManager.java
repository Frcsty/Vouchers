package frcsty.github.vouchers.commands.manager;

import com.codeitforyou.lib.api.command.CommandManager;
import com.codeitforyou.lib.api.general.StringUtil;
import frcsty.github.vouchers.VouchersPlugin;
import frcsty.github.vouchers.commands.GiveCommand;
import frcsty.github.vouchers.commands.ReloadCommand;
import frcsty.github.vouchers.utility.TabComplete;

import java.util.Arrays;

public class CommandsManager
{

    private final VouchersPlugin        plugin;
    private final CommandManager        manager;
    private final CommandManager.Locale locale;
    private final TabComplete           tabComplete;

    public CommandsManager(final VouchersPlugin plugin)
    {
        this.plugin = plugin;

        this.manager = new CommandManager(Arrays.asList(
                GiveCommand.class, ReloadCommand.class
        ), plugin.getConfig().getString("settings.base-command"), plugin);

        this.locale = manager.getLocale();
        this.tabComplete = plugin.getTabComplete();
    }

    public void registerCommand()
    {
        this.createCommandAttributes();

        this.manager.register();

        final String command = plugin.getConfig().getString("settings.base-command");
        if (command != null)
        {
            plugin.getCommand(command).setTabCompleter(tabComplete);
        }
    }

    private void createCommandAttributes()
    {
        this.manager.setMainCommand(ReloadCommand.class);

        for (final String cmd : plugin.getConfig().getStringList("settings.alias"))
        {
            this.manager.addAlias(cmd);
        }

        this.locale.setUsage(getDefaultMessage(plugin.getConfig().getString("messages.usage")));
        this.locale.setUnknownCommand(getDefaultMessage(plugin.getConfig().getString("messages.unknown-command")));
        this.locale.setPlayerOnly("You can't execute this through console!");
        this.locale.setNoPermission(getDefaultMessage(plugin.getConfig().getString("messages.no-permission")));
    }

    private String getDefaultMessage(final String message)
    {
        return message == null ? StringUtil.translate("&cNo default message specified in the config!") : StringUtil.translate(message);
    }

    public CommandManager getManager()
    {
        return manager;
    }

}
