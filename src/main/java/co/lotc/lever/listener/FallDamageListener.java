package co.lotc.lever.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL;

@RequiredArgsConstructor
public class FallDamageListener implements Listener {
	public static final Set<Player> negateFallDamage = new HashSet<>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onDamageEvent(EntityDamageEvent e){
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (e.getCause() == FALL && negateFallDamage.contains(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) { negateFallDamage.remove(event.getPlayer());	}
}
