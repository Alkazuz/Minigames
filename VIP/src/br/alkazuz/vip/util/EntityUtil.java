package br.alkazuz.vip.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftBoat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHorse;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftWither;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import br.alkazuz.vip.main.Main;
import br.alkazuz.vip.pathfinder.CustomPathFinderGoalPanic;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityBoat;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityItem;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PathEntity;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R3.Vector3f;

/**
 * @author RadBuilder
 */
public class EntityUtil {

    
	
    public void setPassenger(Entity vehicle, Entity passenger) {
        vehicle.setPassenger(passenger);
    }

    
    public void resetWitherSize(Wither wither) {
        ((CraftWither) wither).getHandle().r(600);
    }

    
    public void setHorseSpeed(org.bukkit.entity.Entity horse, double speed) {
        ((CraftHorse) horse).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(speed);
    }

    static Random r = new Random();
    static Map<Player, List<EntityArmorStand>> fakeArmorStandsMap = new HashMap<>();
    static Map<Player, List<Entity>> cooldownJumpMap = new HashMap<>();

    
    public void sendBlizzard(Player player, Location loc, boolean affectPlayers, Vector v) {
        if (!fakeArmorStandsMap.containsKey(player))
            fakeArmorStandsMap.put(player, new ArrayList<>());
        if (!cooldownJumpMap.containsKey(player))
            cooldownJumpMap.put(player, new ArrayList<>());

        List<EntityArmorStand> fakeArmorStands = fakeArmorStandsMap.get(player);
        List<Entity> cooldownJump = cooldownJumpMap.get(player);

        EntityArmorStand as = new EntityArmorStand(((CraftWorld) player.getWorld()).getHandle());
        as.setInvisible(true);
        as.setSmall(true);
        as.setGravity(false);
        as.setArms(true);
        as.setHeadPose(new Vector3f((float) (r.nextInt(360)),
                (float) (r.nextInt(360)),
                (float) (r.nextInt(360))));
        as.setLocation(loc.getX() + MathUtils.randomDouble(-1.5, 1.5), loc.getY() + MathUtils.randomDouble(0, .5) - 0.75, loc.getZ() + MathUtils.randomDouble(-1.5, 1.5), 0, 0);
        fakeArmorStands.add(as);
        for (Player players : player.getWorld().getPlayers()) {
            PacketSender.send(players, new PacketPlayOutSpawnEntityLiving(as));
            PacketSender.send(players, new PacketPlayOutEntityEquipment(as.getId(), 4, CraftItemStack.asNMSCopy(new ItemStack(org.bukkit.Material.PACKED_ICE))));
        }
        UtilParticles.display(Particles.CLOUD, loc.clone().add(MathUtils.randomDouble(-1.5, 1.5), MathUtils.randomDouble(0, .5) - 0.75, MathUtils.randomDouble(-1.5, 1.5)), 2, 0.4f);
        Bukkit.getScheduler().runTaskLater(Main.theInstance(), () -> {
            for (Player pl : player.getWorld().getPlayers()) {
                if (as == null) {
                    continue;
                }
                PacketSender.send(pl, new PacketPlayOutEntityDestroy(as.getId()));
            }
            fakeArmorStands.remove(as);
        }, 20);
        if (affectPlayers)
            for (Entity ent : as.getBukkitEntity().getNearbyEntities(0.5, 0.5, 0.5)) {
                if (!cooldownJump.contains(ent) && ent != player) {
                    MathUtils.applyVelocity(ent, new Vector(0, 1, 0).add(v));
                    cooldownJump.add(ent);
                    Bukkit.getScheduler().runTaskLater(Main.theInstance(), () -> cooldownJump.remove(ent), 20);
                }
            }
    }

    
    public static void clearBlizzard(Player player) {
        if (!fakeArmorStandsMap.containsKey(player)) return;

        for (EntityArmorStand as : fakeArmorStandsMap.get(player))
            for (Player pl : player.getWorld().getPlayers())
                PacketSender.send(pl, new PacketPlayOutEntityDestroy(as.getId()));
        fakeArmorStandsMap.remove(player);
        cooldownJumpMap.remove(player);
    }

    
    public static void clearPathfinders(Entity entity) {
        EntityInsentient entitySheep = (EntityInsentient) ((CraftEntity) entity).getHandle();

        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);
            bField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(entitySheep.goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(entitySheep.targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    
    public static void makePanic(Entity entity) {
        EntityInsentient insentient = (EntityInsentient) ((CraftEntity) entity).getHandle();
        insentient.goalSelector.a(3, new CustomPathFinderGoalPanic((EntityCreature) insentient, 0.4d));
    }

    
    public static void sendDestroyPacket(Player player, Entity entity) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(((CraftEntity) entity).getHandle().getId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    
    public static void move(Creature creature, Location loc) {
        EntityCreature ec = ((CraftCreature) creature).getHandle();
        ec.S = 1;

        if (loc == null) return;

        ec.getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), (1.0D + 2.0D * 0.5d) * 1.0D);
    }

    
    public static void moveDragon(Player player, Vector vector, Entity entity) {
        EntityEnderDragon ec = ((CraftEnderDragon) entity).getHandle();

        ec.hurtTicks = -1;

        ec.getBukkitEntity().setVelocity(vector);

        ec.pitch = player.getLocation().getPitch();
        ec.yaw = player.getLocation().getYaw() - 180;
    }

    
    public static void setClimb(Entity entity) {
        ((CraftEntity) entity).getHandle().S = 1;
    }

    
    public static void moveShip(Player player, Entity entity, Vector vector) {
        EntityBoat ec = ((CraftBoat) entity).getHandle();

        ec.getBukkitEntity().setVelocity(vector);

        ec.pitch = player.getLocation().getPitch();
        ec.yaw = player.getLocation().getYaw() - 180;
    }
    
    public static Entity spawnItem(ItemStack itemStack, Location blockLocation) {
        EntityItem ei = new EntityItem(
                ((CraftWorld) blockLocation.clone().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getX(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getY(),
                blockLocation.clone().add(0.5D, 1.2D, 0.5D).getZ(),
                CraftItemStack.asNMSCopy(itemStack)) {


            public boolean a(EntityItem entityitem) {
                return false;
            }
        };
        ei.getBukkitEntity().setVelocity(new Vector(0.0D, 0.25D, 0.0D));
        ei.pickupDelay = 2147483647;
        ei.getBukkitEntity().setCustomName(Main.theInstance().getItemNoPickupString());
        ei.pickupDelay = 20;

        ((CraftWorld) blockLocation.clone().add(0.5D, 1.2D, 0.5D).getWorld()).getHandle().addEntity(ei);

        return ei.getBukkitEntity();
    }

    
    public static boolean isSameInventory(Inventory first, Inventory second) {
        return ((CraftInventory) first).getInventory().equals(((CraftInventory) second).getInventory());
    }

    
    public static void follow(Entity toFollow, Entity follower) {
        net.minecraft.server.v1_8_R3.Entity pett = ((CraftEntity) follower).getHandle();
        ((EntityInsentient) pett).getNavigation().a(2);
        Object petf = ((CraftEntity) follower).getHandle();
        Location targetLocation = toFollow.getLocation();
        PathEntity path;
        path = ((EntityInsentient) petf).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1);
        if (path != null) {
            ((EntityInsentient) petf).getNavigation().a(path, 1.05D);
            ((EntityInsentient) petf).getNavigation().a(1.05D);
        }
    }

    
    public static void chickenFall(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        if (!entityPlayer.onGround && entityPlayer.motY < 0.0D) {
            Vector v = player.getVelocity();
            player.setVelocity(v);
            entityPlayer.motY *= 0.85;
        }
    }

    
    public static void sendTeleportPacket(Player player, Entity entity) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(((CraftEntity) entity).getHandle()));
    }

    
    public static boolean isMoving(org.bukkit.entity.Player entity) {
        EntityPlayer ent = ((CraftPlayer) entity).getHandle();
        long time = System.currentTimeMillis() - ent.D();
        if (time > 0.001) {
            Bukkit.broadcastMessage("MOVING");
            return true;
        }
        Bukkit.broadcastMessage("NOT MOVING");
        return false;
    }


    
    public static byte[] getEncodedData(String url) {
        return Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
    }

}