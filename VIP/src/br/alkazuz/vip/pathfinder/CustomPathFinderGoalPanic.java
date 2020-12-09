package br.alkazuz.vip.pathfinder;

import net.minecraft.server.v1_8_R3.*;

public class CustomPathFinderGoalPanic extends PathfinderGoal
{
    private EntityCreature b;
    protected double a;
    private double c;
    private double d;
    private double e;
    
    public CustomPathFinderGoalPanic(EntityCreature entitycreature, double d0) {
        this.b = entitycreature;
        this.a = d0;
        this.a(1);
    }
    
    public boolean a() {
        Vec3D vec3d = RandomPositionGenerator.a(this.b, 5, 4);
        this.c = vec3d.a;
        this.d = vec3d.b;
        this.e = vec3d.c;
        return true;
    }
    
    public void c() {
        Vec3D vec3d = RandomPositionGenerator.a(this.b, 5, 4);
        if (vec3d == null) {
            return;
        }
        this.b.getNavigation().a(vec3d.a, vec3d.b, vec3d.c, this.a);
    }
    
    public boolean b() {
        if (this.b.ticksLived - this.b.hurtTimestamp > 100) {
            this.b.b((EntityLiving)null);
            return false;
        }
        return !this.b.getNavigation().m();
    }
}
