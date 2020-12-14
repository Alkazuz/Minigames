package br.alkazuz.minigame.game;

import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.material.*;

import com.boydti.fawe.bukkit.wrapper.*;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.regions.*;

public class BoundingBox
{
    public int x0, y0, z0;
    public int x1, y1, z1;
    
    public BoundingBox(
        int x0, int y0, int z0, 
        int x1, int y1, int z1
    )
    {
        this.x0 = Math.min(x0, x1);
        this.y0 = Math.min(y0, y1);
        this.z0 = Math.min(z0, z1);
        this.x1 = Math.max(x0, x1);
        this.y1 = Math.max(y0, y1);
        this.z1 = Math.max(z0, z1);
    }
    /** Parses a BB from a string in the format <code>X0,Y0,Z0,X1,Y1,Z1</code> */
    public static BoundingBox parse(String str)
    {
        String[] s = str.split(",");
        return new BoundingBox(
            Integer.parseInt(s[0]),
            Integer.parseInt(s[1]),
            Integer.parseInt(s[2]),
            Integer.parseInt(s[3]),
            Integer.parseInt(s[4]),
            Integer.parseInt(s[5])
        );
    }
    
    /** Checks whether the specified point is inside this AABB */
    public boolean contains(int x, int y, int z)
    {
        return x >= x0 && y >= y0 && z >= z0 &&
               x <= z1 && y <= y1 && z <= z1;
    }
    /** Checks whether the specified location is inside this AABB */
    public boolean contains(Location loc)
    {
        return contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    public void fill(World world, Material block, MaterialData data)
    {
        AsyncWorld aw = AsyncWorld.wrap(world);
        
        CuboidRegion region = new CuboidRegion(
            new Vector(x0, y0, z0),
            new Vector(x1, y1, z1)
        );
        aw.setBlocks(region, block.getId(), data == null ? 0 : data.getData());
        aw.commit();
    }
    
    public String toString()
    {
        return String.format("%d,%d,%d,%d,%d,%d", x0, y0, z0, x1, y1, z1);
    }
}
