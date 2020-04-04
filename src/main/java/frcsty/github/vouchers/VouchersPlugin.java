package frcsty.github.vouchers;

import frcsty.github.vouchers.commands.manager.CommandsManager;
import frcsty.github.vouchers.commands.manager.DataManager;
import frcsty.github.vouchers.listeners.VoucherRedeemListener;
import frcsty.github.vouchers.listeners.actions.ActionsManager;
import frcsty.github.vouchers.utility.TabComplete;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class VouchersPlugin extends JavaPlugin
{

    private final TabComplete tabComplete = new TabComplete(this);
    private final CommandsManager manager = new CommandsManager(this);
    private final ActionsManager actionsManager = new ActionsManager(this);
    private final DataManager dataManager = new DataManager(this);

    @Override
    public void onEnable()
    {
        if (!getServer().getPluginManager().getPlugin("Autosell").isEnabled())
        {
            getLogger().log(Level.WARNING, "AutoSell not found! Plugin disabling!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        dataManager.createConfigFile();
        dataManager.createFileSection();

        manager.registerCommand();

        actionsManager.registerActions();

        getCommand(getConfig().getString("settings.base-command")).setTabCompleter(tabComplete);

        getServer().getPluginManager().registerEvents(new VoucherRedeemListener(this), this);
    }

    @Override
    public void onDisable()
    {
        reloadConfig();
    }

    public TabComplete getTabComplete()
    {
        return tabComplete;
    }

    public CommandsManager getManager()
    {
        return manager;
    }

    public ActionsManager getActionsManager()
    {
        return actionsManager;
    }

    public DataManager getDataManager()
    {
        return dataManager;
    }

}
