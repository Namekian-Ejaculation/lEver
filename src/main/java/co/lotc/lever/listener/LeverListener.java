package co.lotc.lever.listener;

import co.lotc.core.bukkit.util.Run;
import co.lotc.core.bukkit.util.WeakBlock;
import co.lotc.lever.Lever;
import co.lotc.lever.Lever.StaticInventory;
import co.lotc.lever.OmniUtil;
import co.lotc.lever.cmd.*;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class LeverListener implements Listener {

	@EventHandler
	public void handle(InventoryClickEvent e) {
		inv(e);
	}
	
	@EventHandler
	public void handle(InventoryDragEvent e) {
		inv(e);
	}
	
	public void inv(InventoryInteractEvent e) {
		if (e.getInventory().getHolder() instanceof StaticInventory) {
			Run.as(Lever.get()).sync(()->e.getInventory().getViewers().get(0).closeInventory());
			e.setCancelled(true);
		}
	}
  
  @EventHandler
  public void onLog(final PlayerQuitEvent e) {
  	Player p = e.getPlayer();
  	UUID u = p.getUniqueId();
  	
  	InvSearch.requests.remove(u);

  	if(Walk.isWalking(p)) Walk.disableWalk(p);
  	if(Vanish.VANISHED.contains(u)) {
  		if(p.hasPermission("lever.vanish.persist")) Vanish.persist(p);
  		Vanish.deactivate(p);
  	} else if(p.getAllowFlight() && p.isFlying() && p.hasPermission("lever.fly.persist")) {
  		p.addScoreboardTag(Fly.FLY_PERSIST_TAG);
  	}
  }
  
  @EventHandler(ignoreCancelled = true)
	public void target(EntityTargetLivingEntityEvent e) {
		var target = e.getTarget();
		if(target instanceof Player && Vanish.VANISHED.contains(target.getUniqueId()))
			e.setCancelled(true);
	}
  
  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void tp(PlayerTeleportEvent e) {
  	var u = e.getPlayer().getUniqueId();
  	var wb = new WeakBlock(e.getFrom());
  	
  	Back.BACKS.put(u, wb);
  }
  
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
  	Player p = event.getPlayer();
    
  	if(p.removeScoreboardTag(Vanish.VANISH_PERSIST_TAG))
  		p.performCommand("vanish");
  	else if(p.removeScoreboardTag(Fly.FLY_PERSIST_TAG)) {
  		p.setAllowFlight(true);
  		p.setFlying(true);
  	}
  	
  	Run.as(Lever.get()).delayed(10, () -> p.setViewDistance(ViewDistance.viewDistance));
    
  	Vanish.VANISHED.stream()
  	.map(Bukkit::getPlayer)
  	.filter(Objects::nonNull)
  	.filter(who->Vanish.shouldHide(who, p))
  	.forEach(x->p.hidePlayer(Lever.get(), x));
  }
  
  @EventHandler
  public void onInvClose(InventoryCloseEvent e) {
  	if(!Bukkit.getPluginManager().isPluginEnabled("Omniscience"))
  		return;
  	
  	var i = e.getInventory();
  	if(i.getHolder() instanceof Trash.TrashCan) {
  		Stream.of(i.getContents())
  		.filter(Objects::nonNull)
  		.forEach(is-> OmniUtil.logItem(e.getPlayer(), is));
  	}
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void loginAsync(AsyncPlayerPreLoginEvent e) {
  	UUID u = e.getUniqueId();
  	var pp = Impersonate.REDIRECTS.remove(u);
  	if(pp != null){
  		pp.complete();
  		e.setPlayerProfile(pp);
  	}
  }
  
  @EventHandler(ignoreCancelled = true)
  public void sprint(PlayerToggleSprintEvent event) {
  	final Player p = event.getPlayer();
  	if (p.isSneaking()) {
  		p.setSneaking(false);
  	}
  	
  	if(Walk.isWalking(p)) {
  		Walk.disableWalk(p);
  	}
  }

}
