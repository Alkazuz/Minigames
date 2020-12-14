package br.alkazuz.vip.api;

import org.bukkit.entity.*;

public class PT_BR
{
    public static PT_BR get() {
        return new PT_BR();
    }
    
    public EntityType translateToEnglish(String portuguese) {
        String upperCase;
        switch (upperCase = portuguese.toUpperCase()) {
            case "OVELHA": {
                return EntityType.SHEEP;
            }
            case "PORCO_ZUMBI": {
                return EntityType.PIG_ZOMBIE;
            }
            case "ENDERMAN": {
                return EntityType.ENDERMAN;
            }
            case "WITHER": {
                return EntityType.WITHER;
            }
            case "ESQUELETO": {
                return EntityType.SKELETON;
            }
            case "VACA": {
                return EntityType.COW;
            }
            case "BLAZE": {
                return EntityType.BLAZE;
            }
            case "GOLEM": {
                return EntityType.IRON_GOLEM;
            }
            case "MAGMA": {
                return EntityType.MAGMA_CUBE;
            }
            case "SLIME": {
                return EntityType.SLIME;
            }
            case "ZUMBI": {
                return EntityType.ZOMBIE;
            }
            case "GALINHA": {
                return EntityType.CHICKEN;
            }
            case "ARANHA": {
                return EntityType.SPIDER;
            }
            default:
                break;
        }
        return null;
    }
    
    public String translateMob(EntityType entity) {
        String ret = "";
        switch (entity) {
            case CAVE_SPIDER: {
                ret = "Aranha da Caverna";
                break;
            }
            case SPIDER: {
                ret = "Aranha";
                break;
            }
            case BLAZE: {
                ret = "Blaze";
                break;
            }
            case VILLAGER: {
                ret = "Alde\u00e3o";
                break;
            }
            case PIG_ZOMBIE: {
                ret = "Porco Zumbi";
                break;
            }
            case IRON_GOLEM: {
                ret = "Golem";
                break;
            }
            case PIG: {
                ret = "Porco";
                break;
            }
            case SHEEP: {
                ret = "Ovelha";
                break;
            }
            case ZOMBIE: {
                ret = "Zumbi";
                break;
            }
            case COW: {
                ret = "Vaca";
                break;
            }
            case MUSHROOM_COW: {
                ret = "Vaca de Cogumelo";
                break;
            }
            case ENDERMAN: {
                ret = "Enderman";
                break;
            }
            case HORSE: {
                ret = "Cavalo";
                break;
            }
            case SKELETON: {
                ret = "Esqueleto";
                break;
            }
            case WOLF: {
                ret = "Lobo";
                break;
            }
            case SLIME: {
                ret = "Slime";
                break;
            }
            case CHICKEN: {
                ret = "Galinha";
                break;
            }
            case CREEPER: {
                ret = "Creeper";
                break;
            }
            case WITHER: {
                ret = "Wither";
                break;
            }
            case WITCH: {
                ret = "Bruxa";
                break;
            }
            case GHAST: {
                ret = "Ghast";
                break;
            }
            case MAGMA_CUBE: {
                ret = "Magma";
                break;
            }
            default: {
                ret = String.valueOf(entity.toString().substring(0, 1).toUpperCase()) + entity.toString().substring(1, entity.toString().length());
                break;
            }
        }
        return ret;
    }
}
