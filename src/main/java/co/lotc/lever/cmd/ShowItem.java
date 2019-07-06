package co.lotc.lever.cmd;

import static org.bukkit.ChatColor.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.lotc.core.bukkit.util.ChatBuilder;
import co.lotc.core.bukkit.util.ItemUtil;
import co.lotc.core.bukkit.util.LocationUtil;
import co.lotc.lever.BaseCommand;
import net.md_5.bungee.api.chat.TextComponent;

public class ShowItem extends BaseCommand {
	public void invoke(Player source, Player target) {
		ItemStack is = source.getInventory().getItemInMainHand();
		validate(ItemUtil.exists(is), "You need to hold an item in your hand to show!");
		validate(LocationUtil.isClose(source, target, 16), "Can only show items to nearby players!");
		ChatBuilder b = new ChatBuilder(source.getDisplayName()).color(GOLD)
				.append(" is showing you ").color(AQUA)
				.append('[').color(WHITE)
				.append(TextComponent.fromLegacyText(ItemUtil.getDisplayName(is))).hoverItem(is)
				.append(']').color(WHITE);


		msg("Showing held item to " + target.getName());
		b.send(target);
	}
}
