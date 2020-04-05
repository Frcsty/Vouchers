package frcsty.github.vouchers.listeners;

import com.codeitforyou.lib.api.actions.ActionManager;
import com.codeitforyou.lib.api.general.PAPIUtil;
import com.codeitforyou.lib.api.general.StringUtil;
import com.codeitforyou.lib.api.item.ItemUtil;
import frcsty.github.vouchers.VouchersPlugin;
import me.clip.autosell.AutoSellAPI;
import me.clip.autosell.objects.Multiplier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VoucherRedeemListener implements Listener
{

    private final VouchersPlugin plugin;
    private final ActionManager  manager;

    public VoucherRedeemListener(final VouchersPlugin plugin)
    {
        this.plugin = plugin;
        this.manager = plugin.getActionsManager().getManager();
    }

    @EventHandler
    public void onVoucherRedeem(PlayerInteractEvent event)
    {
        final ItemStack item = event.getItem();
        final Action action = event.getAction();
        final Player player = event.getPlayer();

        if (item == null)
        {
            return;
        }

        final String voucher_type = ItemUtil.getNBTString(item, "voucher-type");
        final boolean voucher_store = Boolean.valueOf(ItemUtil.getNBTString(item, "voucher-store"));
        final ConfigurationSection vouchers = plugin.getConfig().getConfigurationSection("vouchers");
        final ConfigurationSection messages = plugin.getConfig().getConfigurationSection("messages");

        if (player == null || voucher_type == null | vouchers == null)
        {
            return;
        }

        if (action != Action.RIGHT_CLICK_AIR)
        {
            return;
        }

        final List<String> actions = new ArrayList<>();
        for (String a : vouchers.getStringList(voucher_type + ".commands"))
        {
            actions.add(PAPIUtil.parse(player, a));
        }

        if (voucher_store)
        {
            final String voucher_uuid = ItemUtil.getNBTString(item, "voucher-uuid");

            if (voucher_uuid != null)
            {
                if (plugin.getDataManager().getVoucherStatus(voucher_uuid))
                {
                    if (messages == null)
                    {
                        return;
                    }

                    String message = messages.getString("invalid-voucher");

                    if (message.contains("%voucher-uuid%"))
                    {
                        message = message.replace("%voucher-uuid%", voucher_uuid);
                    }
                    player.sendMessage(StringUtil.translate(message));
                    return;
                }
            }
        }

        final String voucherMultiplier = ItemUtil.getNBTString(item, "autosell");

        if (voucherMultiplier != null)
        {
            final double voucherModifier = Double.parseDouble(voucherMultiplier);
            final Multiplier playerMultiplier = AutoSellAPI.getMultiplier(player);

            if (playerMultiplier != null)
            {
                if (voucherModifier != playerMultiplier.getMultiplier())
                {
                    player.sendMessage(StringUtil.translate(messages.getString("active-multiplier")));
                    return;
                }
            }
        }

        if (voucher_store)
        {
            final String voucher_uuid = ItemUtil.getNBTString(item, "voucher-uuid");
            plugin.getDataManager().setVoucherStatus(voucher_uuid, true);
            plugin.getDataManager().saveFileAsync(true);
        }

        manager.runActions(player, actions);

        if (player.getInventory().getItemInHand().getAmount() == 1)
        {
            player.getInventory().setItemInHand(null);
        }
        else
        {
            player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
        }
    }

}
