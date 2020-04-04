package frcsty.github.vouchers.listeners.actions;

import com.codeitforyou.lib.api.actions.ActionManager;
import frcsty.github.vouchers.VouchersPlugin;

public class ActionsManager
{

    private final ActionManager manager;

    public ActionsManager(final VouchersPlugin plugin)
    {
        this.manager = new ActionManager(plugin);
    }

    public void registerActions()
    {
        this.manager.addDefaults();
    }

    public ActionManager getManager()
    {
        return manager;
    }

}
