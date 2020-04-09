package frcsty.github.vouchers.commands;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.lib.api.general.StringUtil;
import com.codeitforyou.lib.api.item.ItemBuilder;
import com.google.common.primitives.Ints;
import frcsty.github.vouchers.VouchersPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class GiveCommand
{

    @Command(permission = "vouchers.give", aliases = {"give"}, usage = "give <target/all> [voucher] (amount)", requiredArgs = 2)
    public static void execute(final CommandSender sender, final VouchersPlugin plugin, final String[] args)
    {
        final String voucher = args[1];
        int amount = 1;
        final ConfigurationSection messages = plugin.getConfig().getConfigurationSection("messages");
        final ConfigurationSection vouchers = plugin.getConfig().getConfigurationSection("vouchers");
        Player target;

        if (messages == null)
        {
            plugin.getLogger().log(Level.WARNING, "Configuration section 'messages' is null!");
            return;
        }

        if (vouchers == null)
        {
            plugin.getLogger().log(Level.WARNING, "Configuration section 'vouchers' is null!");
            return;
        }

        Material material = Material.STONE;
        String display_name = null;
        List<String> lore = null;
        boolean glowing = false;
        boolean autosell = false;
        boolean store = false;

        final Set<String> v = vouchers.getKeys(false);
        for (String vo : v)
        {
            if (voucher.equalsIgnoreCase(vo))
            {
                material = Material.valueOf(vouchers.getString(vo + ".item-material"));
                display_name = vouchers.getString(vo + ".display-name");
                lore = vouchers.getStringList(vo + ".lore");
                glowing = vouchers.getBoolean(vo + ".glowing");
                autosell = vouchers.getBoolean(vo + ".autosell");
                store = vouchers.getBoolean(vo + ".store");
                break;
            }
        }

        final ItemBuilder builder = new ItemBuilder(material);

        if (display_name != null)
        {
            builder.withName(display_name);
        }
        if (lore != null)
        {
            builder.withLore(lore);
        }
        if (glowing)
        {
            ItemStack item = builder.getItem();
            ItemMeta meta = item.getItemMeta();

            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            item.setItemMeta(meta);
        }

        builder.withNBTString("voucher-type", voucher.toLowerCase());

        if (autosell)
        {
            builder.withNBTString("autosell", String.valueOf(vouchers.getInt(voucher + ".multiplier")));
        }

        if (!store)
        {
            amount = args.length == 3 ? (Ints.tryParse(args[2]) == null ? 1 : Integer.parseInt(args[2])) : 1;
            builder.withNBTString("voucher-store", String.valueOf(false));
        }

        if (!args[0].equalsIgnoreCase("all"))
        {
            target = Bukkit.getPlayerExact(args[0]);

            if (target == null || !target.isOnline())
            {
                sender.sendMessage(StringUtil.translate(messages.getString("invalid-target")));
                return;
            }

            if (store)
            {
                createVoucherData(builder, plugin);
                plugin.getDataManager().saveFileAsync(true);
            }

            sendVoucherReceiverMessage(target, messages, voucher, amount);
            sendVoucherGiveMessage(sender, messages, target.getName(), voucher, amount);
            giveVoucher(target, builder.getItem(), amount);
        }
        else
        {
            final Map<InetAddress, UUID> addressStatus = new HashMap<>();

            for (Player plr : Bukkit.getServer().getOnlinePlayers())
            {
                final InetAddress playerAddress = plr.getAddress().getAddress();

                if (!addressStatus.containsKey(playerAddress))
                {
                    addressStatus.put(playerAddress, plr.getUniqueId());
                }
            }

            for (InetAddress address : addressStatus.keySet())
            {
                final UUID uuid = addressStatus.get(address);

                if (store)
                {
                    createVoucherData(builder, plugin);
                }

                sendVoucherReceiverMessage(Bukkit.getPlayer(uuid), messages, voucher, amount);
                giveVoucher(Bukkit.getPlayer(uuid), builder.getItem(), amount);
            }

            sendVoucherGiveMessage(sender, messages, "Everyone", voucher, amount);
            plugin.getDataManager().saveFileAsync(true);
        }

    }

    private static void giveVoucher(final Player target, final ItemStack builder, final int amount)
    {
        if (target.getInventory().firstEmpty() != -1)
        {
            for (int i = 0; i < amount; i++)
            {
                target.getInventory().addItem(builder);
            }
        }
        else
        {
            final Location location = target.getLocation();

            for (int i = 0; i < amount; i++)
            {
                location.getWorld().dropItem(location, builder);
            }
        }
    }

    private static void createVoucherData(final ItemBuilder builder, final VouchersPlugin plugin)
    {
        final String voucher_uuid = UUID.randomUUID().toString();
        builder.withNBTString("voucher-uuid", voucher_uuid);
        builder.withNBTString("voucher-store", String.valueOf(true));

        plugin.getDataManager().setVoucherStatus(voucher_uuid, false);
    }

    private static void sendVoucherGiveMessage(final CommandSender sender, final ConfigurationSection messages, final String target, final String voucher, final int amount)
    {
        sender.sendMessage(StringUtil.translate(messages.getString("give-voucher"))
                                     .replace("%target%", target)
                                     .replace("%voucher%", StringUtils.capitalize(voucher))
                                     .replace("%amount%", String.valueOf(amount)));
    }

    private static void sendVoucherReceiverMessage(final Player target, final ConfigurationSection messages, final String voucher, final int amount)
    {
        target.sendMessage(StringUtil.translate(messages.getString("receive-voucher")
                                                        .replace("%voucher%", StringUtils.capitalize(voucher)
                                                        .replace("%amount%", String.valueOf(amount)))));
    }

}
