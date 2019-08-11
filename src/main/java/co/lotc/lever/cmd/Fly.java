package co.lotc.lever.cmd;

import co.lotc.core.bukkit.util.Run;
import co.lotc.core.command.annotate.Cmd;
import co.lotc.core.command.annotate.Default;
import co.lotc.lever.BaseCommand;
import co.lotc.lever.listener.FallDamageListener;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

import static net.md_5.bungee.api.ChatColor.*;

public class Fly extends BaseCommand {
	public static final String FLY_PERSIST_TAG = "lever_flying";
	
	public void invoke(CommandSender s, @Default("@p") Player target) {
		target.setAllowFlight(!target.getAllowFlight());
		
		if(target.getAllowFlight()) target.sendMessage(GREEN + "You are now flying");
		else {
			FallDamageListener.negateFallDamage.add(target);
			Run.as(plugin).delayed(80L, () -> FallDamageListener.negateFallDamage.remove(target));
			target.sendMessage(LIGHT_PURPLE + "You are no longer flying");
		}

		if (s != target) {
			s.sendMessage(AQUA + "Fly toggled " + (target.getAllowFlight() ? GREEN + "ON" : RED + "OFF") + AQUA + " for " + WHITE + target.getName());
		}
	}

	@Cmd("List people in fly mode")
	public void list() {
		String names = Bukkit.getOnlinePlayers().stream()
				.filter(Player::getAllowFlight)
				.map(Player::getName)
				.collect(Collectors.joining(", "));

		if (StringUtils.isEmpty(names)) names = "none...";
		msg(AQUA + "Players in fly mode: " + names);
	}
}
