package co.lotc.lever;

import static net.md_5.bungee.api.ChatColor.GREEN;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.google.common.collect.Lists;

import co.lotc.core.bukkit.util.Run;
import co.lotc.lever.cmd.Vanish;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SneakToggleListener implements Listener {
	private final Lever plugin;
	private final List<String> sneakAttempts = Lists.newArrayList();
	
	@EventHandler(ignoreCancelled = true, priority= EventPriority.MONITOR)
	public void onSneak(PlayerToggleSneakEvent e){
		final Player p = e.getPlayer();
		if(e.isSneaking()){
			if(Vanish.isVanished(p)){
				if(!sneakAttempts.contains(p.getName())){ //Shift pressed down once.
					sneakAttempts.add(p.getName());
					Run.as(plugin).delayed(8, ()->sneakAttempts.remove(p.getName()));
				} else { //Shift pressed down twice in short time
					p.sendMessage(GREEN + "Toggled gamemode");
					if(p.getGameMode() != GameMode.SPECTATOR) {
						p.setGameMode(GameMode.SPECTATOR);
					} else {
						p.setGameMode(GameMode.SURVIVAL);
						p.setAllowFlight(true);
						p.setFlying(true);
					}
				}
			}
		}
	}
}