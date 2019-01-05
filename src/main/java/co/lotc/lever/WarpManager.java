package co.lotc.lever;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Location;

import lombok.var;
import net.lordofthecraft.arche.ArcheCore;
import net.lordofthecraft.arche.interfaces.CommandHandle;

public class WarpManager {
	public static final String TABLE = "lever_warps";
	private final Map<String, Warp> warps = new TreeMap<>();

	public void load(String name, Warp warp) {
		warps.put(name, warp);
	}
	
	public void add(String name, Location location) {
		warps.put(name.toLowerCase(), new Warp(name, location));
		ArcheCore.getConsumerControls().replace(TABLE)
			.set("name", name.toLowerCase())
			.set("world", location.getWorld())
			.set("x", location.getX())
			.set("y", location.getY())
			.set("z", location.getZ())
			.queue();
	}
	
	public void init() {
		CommandHandle.defineArgumentType(Warp.class)
		.defaultName("warp name")
		.defaultError("Not a valid warp!")
		.completer(()->warps.keySet())
		.mapper(s->warps.get(s))
		.register();
		
	}
	
	public boolean remove(String name) {
		if(warps.remove(name) != null) {
			ArcheCore.getConsumerControls().delete(TABLE)
			.where("name", name).queue();
			return true;
		} else {
			return false;
		}
	}
	
	public Collection<String> getWarps(int offset, int amount){
		List<String> result = new ArrayList<>();
		int parsed = 0;
		
		var iter = warps.keySet().iterator();
		
		while(iter.hasNext() && parsed < offset + amount) {
			String aWarp = iter.next();
			if(parsed >= offset) result.add(aWarp);
			parsed++;
		}
		
		return result;
	}
	
	
}