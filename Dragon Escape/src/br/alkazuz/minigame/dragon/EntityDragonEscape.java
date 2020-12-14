package br.alkazuz.minigame.dragon;

import java.lang.reflect.Field;

import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.PathfinderGoalTargetNearestPlayer;
import net.minecraft.server.v1_8_R3.World;

public class EntityDragonEscape extends EntityEnderDragon
{
    public EntityDragonEscape(World world) {
        super(world);
        this.targetSelector.a(2, (PathfinderGoal)new PathfinderGoalTargetNearestPlayer((EntityInsentient)this));
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(500.0);
        this.setCustomName("Drag\u00e3o");
        this.setCustomNameVisible(true);
    }
    
    public void move(double d0, double d1, double d2) {
        super.move(d0 * 0.2, d1 * 0.2, d2 * 0.2);
    }
    
    public static Object getPrivateField(String fieldName, Class oclass, Object object) {
        Field field = null;
        try {
            field = oclass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        }
        catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            return field;
        }
    }
}
