package br.alkazuz.minigame.api.entities;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import net.minecraft.server.v1_8_R3.EntityAmbient;
import net.minecraft.server.v1_8_R3.World;

public class NametagEntity extends EntityAmbient
{
    public NametagEntity(Player player) {
        super((World)((CraftWorld)player.getWorld()).getHandle());
        Location location = player.getLocation();
        this.setInvisible(true);
        this.setPosition(location.getX(), location.getY(), location.getZ());
        try {
            Field invulnerable = Entity.class.getDeclaredField("invulnerable");
            invulnerable.setAccessible(true);
            invulnerable.setBoolean(this, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        this.persistent = true;
        this.hideTag(player);
    }
    
    public void hideTag(Player player) {
    	this.passenger = ((CraftPlayer)player).getHandle();
    }
    
    public void showTag() {
    	this.passenger = null;
    }
    
    @Override
    public void h() {
        double motX = 0.0;
        this.motZ = motX;
        this.motY = motX;
        this.motX = motX;
        this.a(0.0f, 0.0f);
        this.a(0.0f, 0.0f, 0.0f);
    }
}
