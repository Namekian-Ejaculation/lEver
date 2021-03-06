package co.lotc.lever.cmd;

import co.lotc.core.command.annotate.Arg;
import co.lotc.core.command.annotate.Cmd;
import co.lotc.core.command.annotate.Default;
import co.lotc.core.command.annotate.Range;
import co.lotc.lever.BaseCommand;
import co.lotc.lever.Warp;
import lombok.var;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.WHITE;

public class WarpCommand extends BaseCommand {

	public void invoke(Player p, Warp warp, @Default("@p") Player target) {
		validate(p.hasPermission("lever.warp."+warp.getName()), "You do not have permission!");
		warp.teleport(target);
		target.sendMessage(AQUA + "Warped to " + WHITE + warp.getName());
		if (p != target) {
			p.sendMessage(target.getName() + AQUA + " has been warped to " + WHITE + warp.getName());
		}
	}
	
	@Cmd("List the currently defined warps")
	public void list(CommandSender s, @Default("1") @Arg("page") @Range(min=1) int page) {
		validate(page > 0, "Invalid page!");
		msg(AQUA + "Showing warps on page " + WHITE  + page);
		page--; //Arrays start at 1 though
		
		var warps = plugin.getWarpManager().getWarps(page*40, 40);
		StringUtils.join(warps, ", ");
		
		msg(StringUtils.join(warps, ", "));
	}
	
	@Cmd(value="Create a new warp at your location", permission="lever.warp.create")
	public void create(Player p, @Arg("warp name") String warp) {
		validate(warp.matches("[A-Za-z0-9]+"), "Invalid warp name!");
		//validate(warp.length() > 1, "Warp name too short!");
		validate(!NumberUtils.isDigits(warp), "Warp name can't be all numbers!");
		validate(!plugin.getWarpManager().hasWarp(warp), "Warp with this name already exists!");
		
		plugin.getWarpManager().add(warp, p.getLocation());
		msg(AQUA + "Successfully created the warp: " + WHITE + warp);
	}

	@Cmd(value="Create a new warp at your location",permission="lever.warp.delete")
	public void delete(CommandSender s, @Arg("warp name") String warp) {
		//validate(warp.matches("[A-Za-z0-9]+"), "Invalid warp name!");
		//validate(warp.length() > 3, "Warp name too short!");
		validate(!NumberUtils.isDigits(warp), "Warp name can't be all numbers!");
		validate(plugin.getWarpManager().hasWarp(warp), "No warp found with this name!");
		
		boolean result = plugin.getWarpManager().remove(warp);
		if(!result) error("That warp does not exist!");
		else msg(AQUA + " Successfully deleted the warp: " + WHITE + warp);
	}
	
}
