package br.alkazuz.vip.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import br.alkazuz.vip.main.DataManager;
import br.alkazuz.vip.main.Main;
import de.robingrether.idisguise.disguise.Disguise;
import de.robingrether.idisguise.disguise.DisguiseType;
import de.robingrether.idisguise.disguise.MobDisguise;
import de.robingrether.idisguise.disguise.PigDisguise;
import de.robingrether.idisguise.disguise.SheepDisguise;
import de.robingrether.idisguise.disguise.SizedDisguise;

public class MorphAPI
{
    public static HashMap<EntityType, MobDisguise> morphs;
    
    static {
        MorphAPI.morphs = new HashMap<EntityType, MobDisguise>();
    }
    
    public static void load() {
        MorphAPI.morphs.put(EntityType.BLAZE, new MobDisguise(DisguiseType.BLAZE));
        MorphAPI.morphs.put(EntityType.CAVE_SPIDER, new MobDisguise(DisguiseType.CAVE_SPIDER));
        MorphAPI.morphs.put(EntityType.CHICKEN, new MobDisguise(DisguiseType.CHICKEN));
        MorphAPI.morphs.put(EntityType.COW, new MobDisguise(DisguiseType.COW));
        MorphAPI.morphs.put(EntityType.MUSHROOM_COW, new MobDisguise(DisguiseType.MUSHROOM_COW));
        MorphAPI.morphs.put(EntityType.CREEPER, new MobDisguise(DisguiseType.CREEPER));
        MorphAPI.morphs.put(EntityType.ENDERMAN, new MobDisguise(DisguiseType.ENDERMAN));
        MorphAPI.morphs.put(EntityType.GHAST, new MobDisguise(DisguiseType.GHAST));
        MorphAPI.morphs.put(EntityType.HORSE, new MobDisguise(DisguiseType.HORSE));
        MorphAPI.morphs.put(EntityType.IRON_GOLEM, new MobDisguise(DisguiseType.IRON_GOLEM));
        MorphAPI.morphs.put(EntityType.PIG, new MobDisguise(DisguiseType.PIG));
        MorphAPI.morphs.put(EntityType.PIG_ZOMBIE, new MobDisguise(DisguiseType.PIG_ZOMBIE));
        MorphAPI.morphs.put(EntityType.SKELETON, new MobDisguise(DisguiseType.SKELETON));
        MorphAPI.morphs.put(EntityType.SLIME, new MobDisguise(DisguiseType.SLIME));
        MorphAPI.morphs.put(EntityType.SHEEP, new MobDisguise(DisguiseType.SHEEP));
        MorphAPI.morphs.put(EntityType.WOLF, new MobDisguise(DisguiseType.WOLF));
        MorphAPI.morphs.put(EntityType.ZOMBIE, new MobDisguise(DisguiseType.ZOMBIE));
        MorphAPI.morphs.put(EntityType.SPIDER, new MobDisguise(DisguiseType.SPIDER));
        MorphAPI.morphs.put(EntityType.VILLAGER, new MobDisguise(DisguiseType.VILLAGER));
        Iterator<EntityType> iterador = morphs.keySet().iterator();
        while(iterador.hasNext()) {
        	EntityType type = iterador.next();
        	if(Main.theInstance().config.get("morphs.mob."+type.getName()) == null) {
        		Main.theInstance().config.set("morphs.mob."+type.getName(), true);
        	}
        	if(!Main.theInstance().config.getBoolean("morphs.mob."+type.getName())) {
        		morphs.remove(type);
        	}
        }
        try {
            Main.theInstance().config.save(DataManager.getFile("config"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void morphPlayer(Player p, EntityType type) {
        if (MorphAPI.morphs.containsKey(type)) {
            MobDisguise dis = new MobDisguise(MorphAPI.morphs.get(type).getType());
            if (dis instanceof SheepDisguise) {
                SheepDisguise s = (SheepDisguise)dis;
                s.setAdult(true);
                dis = (MobDisguise)s;
            }
            if (dis instanceof PigDisguise) {
                PigDisguise s2 = (PigDisguise)dis;
                s2.setAdult(true);
                dis = (MobDisguise)s2;
            }
            if (dis instanceof SizedDisguise) {
                SizedDisguise s3 = (SizedDisguise)dis;
                s3.setSize(4);
                dis = (MobDisguise)s3;
            }
            if (dis instanceof SheepDisguise) {
                SheepDisguise s = (SheepDisguise)dis;
                s.setAdult(true);
                dis = (MobDisguise)s;
            }
            dis.setCustomNameVisible(true);
            dis.setCustomName(p.getName());
            Main.theInstance().api.disguise(p, (Disguise)dis);
            p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 4.0f, 4.0f);
            p.getWorld().playEffect(p.getLocation(), Effect.EXPLOSION_LARGE, 100);
            for (Entity e : p.getWorld().getEntities()) {
                if (e instanceof Player) {
                    Player d = (Player)e;
                    d.sendMessage("§6" + p.getName() + " §b\u00e9 VIP e se transformou em um(a) §6" + PT_BR.get().translateMob(type));
                }
            }
        }
    }
}
