package br.alkazuz.antiaura.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AimUtils {
	
	public static float[] getRotations(Entity entity, Entity target) {
		float entityHeadHeigh;
		float targetHeadHeigh;
		
		if(entity instanceof Player) {
			Player p = (Player)entity;
			CraftPlayer cp = (CraftPlayer)p;
			entityHeadHeigh = cp.getHandle().getHeadHeight();
		}else {
			entityHeadHeigh = ((net.minecraft.server.v1_8_R3.Entity) entity).getHeadHeight();
		}
		
		if(target instanceof Player) {
			Player p = (Player)target;
			CraftPlayer cp = (CraftPlayer)p;
			targetHeadHeigh = cp.getHandle().getHeadHeight();
		}else {
			targetHeadHeigh = ((net.minecraft.server.v1_8_R3.Entity) target).getHeadHeight();
		}
		
		double xDistance = target.getLocation().getX() - entity.getLocation().getX();
		double zDistance = target.getLocation().getZ() - entity.getLocation().getZ();
		double yDistance = target.getLocation().getY() + (targetHeadHeigh - 0.1D) / 1.4D - entity.getLocation().getY()
				- entityHeadHeigh / 1.4D;
		double angleHelper = MathHelper.sqrt_double(xDistance * xDistance + zDistance * zDistance);

		float newYaw = (float) Math.toDegrees(-Math.atan(xDistance / zDistance));
		float newPitch = (float) -Math.toDegrees(Math.atan(yDistance / angleHelper));
		if ((zDistance < 0.0D) && (xDistance < 0.0D)) {
			newYaw = (float) (90.0D + Math.toDegrees(Math.atan(zDistance / xDistance)));
		} else if ((zDistance < 0.0D) && (xDistance > 0.0D)) {
			newYaw = (float) (-90.0D + Math.toDegrees(Math.atan(zDistance / xDistance)));
		}
		return new float[] { newYaw, newPitch };
	}

	public static void entityFaceEntity(Entity entity, Entity target) {
		float entityHeadHeigh;
		float targetHeadHeigh;
		
		if(entity instanceof Player) {
			Player p = (Player)entity;
			CraftPlayer cp = (CraftPlayer)p;
			entityHeadHeigh = cp.getHandle().getHeadHeight();
		}else {
			entityHeadHeigh = ((net.minecraft.server.v1_8_R3.Entity) entity).getHeadHeight();
		}
		
		if(target instanceof Player) {
			Player p = (Player)target;
			CraftPlayer cp = (CraftPlayer)p;
			targetHeadHeigh = cp.getHandle().getHeadHeight();
		}else {
			targetHeadHeigh = ((net.minecraft.server.v1_8_R3.Entity) target).getHeadHeight();
		}
		
		double xDistance = target.getLocation().getX() - entity.getLocation().getX();
		double zDistance = target.getLocation().getZ() - entity.getLocation().getZ();
		double yDistance = target.getLocation().getY() + (targetHeadHeigh - 0.1D) / 1.4D - entity.getLocation().getY()
				- entityHeadHeigh / 1.4D;
		double angleHelper = MathHelper.sqrt_double(xDistance * xDistance + zDistance * zDistance);

		float newYaw = (float) Math.toDegrees(-Math.atan(xDistance / zDistance));
		float newPitch = (float) -Math.toDegrees(Math.atan(yDistance / angleHelper));
		if ((zDistance < 0.0D) && (xDistance < 0.0D)) {
			newYaw = (float) (90.0D + Math.toDegrees(Math.atan(zDistance / xDistance)));
		} else if ((zDistance < 0.0D) && (xDistance > 0.0D)) {
			newYaw = (float) (-90.0D + Math.toDegrees(Math.atan(zDistance / xDistance)));
		}
		Location loc = entity.getLocation().clone();
		loc.setPitch(newPitch);
		loc.setYaw(newYaw);
		entity.teleport(loc);
	}
	
	public static float getYawChangeToEntity(Entity entity, Entity target) {
		double deltaX = target.getLocation().getX() - entity.getLocation().getX();
		double deltaZ = target.getLocation().getZ() - entity.getLocation().getZ();
		double yawToEntity;
		if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
			yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
		} else {
			if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
				yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
			} else {
				yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
			}
		}
		return MathHelper.wrapAngleTo180_float(-(entity.getLocation().getYaw() - (float) yawToEntity));
	}

	
	
	public static boolean isLookingHead(Entity entity, Entity target) {
		float entityHeadHeigh;
		float targetHeadHeigh;
		
		if(entity instanceof Player) {
			Player p = (Player)entity;
			CraftPlayer cp = (CraftPlayer)p;
			entityHeadHeigh = cp.getHandle().getHeadHeight();
		}else {
			entityHeadHeigh = ((net.minecraft.server.v1_8_R3.Entity) entity).getHeadHeight();
		}
		
		if(target instanceof Player) {
			Player p = (Player)target;
			CraftPlayer cp = (CraftPlayer)p;
			targetHeadHeigh = cp.getHandle().getHeadHeight();
		}else {
			targetHeadHeigh = ((net.minecraft.server.v1_8_R3.Entity) target).getHeadHeight();
		}
		
		double xDistance = target.getLocation().getX() - entity.getLocation().getX();
		double zDistance = target.getLocation().getZ() - entity.getLocation().getZ();
		double yDistance = target.getLocation().getY() + (targetHeadHeigh - 0.1D) / 1.4D - entity.getLocation().getY()
				- entityHeadHeigh / 1.4D;
		double angleHelper = MathHelper.sqrt_double(xDistance * xDistance + zDistance * zDistance);

		float newYaw = (float) Math.toDegrees(-Math.atan(xDistance / zDistance));
		float newPitch = (float) -Math.toDegrees(Math.atan(yDistance / angleHelper));
		if ((zDistance < 0.0D) && (xDistance < 0.0D)) {
			newYaw = (float) (90.0D + Math.toDegrees(Math.atan(zDistance / xDistance)));
		} else if ((zDistance < 0.0D) && (xDistance > 0.0D)) {
			newYaw = (float) (-90.0D + Math.toDegrees(Math.atan(zDistance / xDistance)));
		}
		//float yaw = entity.getLocation().getYaw() - newYaw;
		float pitch = entity.getLocation().getPitch() - newPitch;
		//Bukkit.broadcastMessage(""+yaw);
		//Bukkit.broadcastMessage(""+pitch);
		return pitch < 0 && pitch > -20;
	}
	
	public static float getPitchChangeToEntity(Entity entity, Entity target) {
		float entityHeadHeigh;
		float targetHeadHeigh;
		
		if(entity instanceof Player) {
			Player p = (Player)entity;
			CraftPlayer cp = (CraftPlayer)p;
			entityHeadHeigh = cp.getHandle().getHeadHeight();
		}else {
			entityHeadHeigh = ((net.minecraft.server.v1_8_R3.Entity) entity).getHeadHeight();
		}
		
		if(target instanceof Player) {
			Player p = (Player)target;
			CraftPlayer cp = (CraftPlayer)p;
			targetHeadHeigh = cp.getHandle().getHeadHeight();
		}else {
			targetHeadHeigh = ((net.minecraft.server.v1_8_R3.Entity) target).getHeadHeight();
		}
		double deltaX = target.getLocation().getX() - entity.getLocation().getX();
		double deltaZ = target.getLocation().getZ() - entity.getLocation().getZ();
		double deltaY = target.getLocation().getY() - 1.6D + targetHeadHeigh - 0.4D - entity.getLocation().getY();
		double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);

		double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));

		return -MathHelper.wrapAngleTo180_float(entity.getLocation().getPitch() - (float) pitchToEntity);
	}

	
	public static float[] getAngles(Entity entity, Entity target) {
		return new float[] { getYawChangeToEntity(entity, target) + entity.getLocation().getYaw(),
				getPitchChangeToEntity(entity, target) + entity.getLocation().getPitch() };
	}
	
}
