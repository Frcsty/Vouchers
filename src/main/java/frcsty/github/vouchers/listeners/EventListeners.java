package frcsty.github.vouchers.listeners;

import com.codeitforyou.lib.api.item.ItemUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class EventListeners implements Listener
{

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        final ItemStack item = event.getItemInHand();
        final String voucher_type = ItemUtil.getNBTString(item, "voucher-type");

        if (voucher_type != null)
        {
            event.setCancelled(true);
        }
    }

}
