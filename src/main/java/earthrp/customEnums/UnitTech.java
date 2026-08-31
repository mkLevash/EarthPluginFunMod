package earthrp.customEnums;

import lombok.Getter;
import org.bukkit.Material;

import java.util.Map;

import static earthrp.customEnums.UnitTech.UnitType.*;

@Getter
public enum UnitTech {

    INF0(INF,"Пехота", 0, 2.0, 0.25, 0.20, 0, 0, 0,Map.of(Material.FLINT,2,Material.HAY_BLOCK,2,Material.STONE_SWORD,1,Material.LEATHER,2)),
    CAV0(CAV,"Кавалерия", 0, 2.0, 0.0, 0.8, 0, 0, 0,Map.of(Material.FLINT,2,Material.HAY_BLOCK,2,Material.LEATHER,2)),
    ART0(ART,"Артиллерия 0 ур", 0, 0.0, 0.0, 0.0, 0, 0, 0,Map.of()), // В конфиге нет ART0, оставлен заглушкой

    // ==================== ЭПОХА 1: ФЕОДАЛИЗМ ====================
    INF1(INF,"Пехота 1 ур", 1, 2.5, 0.35, 0.5, 0, 1, 0,Map.of(Material.IRON_BLOCK,2,Material.HAY_BLOCK,2,Material.ARROW,2,Material.LEATHER,1)),
    CAV1(CAV,"Кавалерия 1 ур", 1, 2.5, 0.0, 1.0, 0, 2, 1,Map.of(Material.IRON_BLOCK,2,Material.HAY_BLOCK,2,Material.ARROW,1,Material.LEATHER,1)),
    INF2(INF,"Пехота 2 ур", 2, 3.0, 0.8, 0.95, 0, 1, 1,Map.of(Material.IRON_BLOCK,2,Material.HAY_BLOCK,2,Material.ARROW,2,Material.LEATHER,1)),
    CAV2(CAV,"Кавалерия 2 ур", 2, 3.0, 0.0, 2.0, 0, 3, 1,Map.of(Material.IRON_BLOCK,2,Material.HAY_BLOCK,2,Material.ARROW,1,Material.LEATHER,1)),

    // ==================== ЭПОХА 2: РЕНЕССАНС ====================
    INF3(INF,"Аркебузиры", 3, 4.0, 1.6, 1.15, 2, 1, 2,Map.of(Material.HAY_BLOCK,2,Material.IRON_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.COOKED_PORKCHOP,2,Material.GUNPOWDER,1)),
    CAV3(CAV,"Рейтары", 3, 4.0, 0.0, 3.0, 0, 3, 3,Map.of(Material.HAY_BLOCK,1,Material.IRON_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.LEATHER,1,Material.COOKED_PORKCHOP,1,Material.GUNPOWDER,1)),
    ART1(ART,"Артиллерия 1 ур", 3, 4.0, 2.4, 0.05, 1, 0, 1,Map.of(Material.IRON_BLOCK,2,Material.DIAMOND_BLOCK,1,Material.GUNPOWDER,2,Material.FIRE_CHARGE,2)),
    INF4(INF,"Пехота 4 ур", 4, 4.5, 1.6, 1.65, 2, 1, 3,Map.of(Material.HAY_BLOCK,2,Material.IRON_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.COOKED_PORKCHOP,2,Material.GUNPOWDER,1)),
    CAV4(CAV,"Карабинеры", 4, 4.5, 0.5, 3.0, 0, 4, 4,Map.of(Material.HAY_BLOCK,1,Material.IRON_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.LEATHER,1,Material.COOKED_PORKCHOP,1,Material.GUNPOWDER,1)),
    ART2(ART,"Артиллерия 2 ур", 4, 4.5, 4.4, 0.35, 2, 1, 2,Map.of(Material.IRON_BLOCK,2,Material.DIAMOND_BLOCK,1,Material.GUNPOWDER,2,Material.FIRE_CHARGE,2)),

    // ==================== ЭПОХА 3: МАНУФАКТУРЫ ====================
    INF5(INF,"Пехота 5 ур", 5, 5.5, 2.1, 1.65, 3, 1, 4,Map.of(Material.HAY_BLOCK,2,Material.AMETHYST_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.GUNPOWDER,3)),
    CAV5(CAV,"Кавалерия 5 ур", 5, 5.5, 1.0, 4.0, 1, 5, 5,Map.of(Material.HAY_BLOCK,1,Material.AMETHYST_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.LEATHER,1,Material.GUNPOWDER,2)),
    ART3(ART,"Артиллерия 3 ур", 6, 5.5, 6.4, 0.45, 4, 1, 5,Map.of(Material.IRON_BLOCK,1,Material.AMETHYST_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.GUNPOWDER,2,Material.FIRE_CHARGE,2)),
    INF6(INF,"Пехота 6 ур", 6, 6.0, 3.1, 2.15, 4, 3, 3,Map.of(Material.HAY_BLOCK,2,Material.AMETHYST_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.GUNPOWDER,3)),
    CAV6(CAV,"Кавалерия 6 ур", 6, 6.0, 1.0, 5.0, 1, 6, 5,Map.of(Material.HAY_BLOCK,1,Material.AMETHYST_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.LEATHER,1,Material.GUNPOWDER,2)),
    ART4(ART,"Артиллерия 4 ур", 6, 6.0, 8.4, 0.55, 5, 4, 6,Map.of(Material.IRON_BLOCK,1,Material.AMETHYST_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.GUNPOWDER,2,Material.FIRE_CHARGE,2));

    UnitTech(UnitType type,String displayName, int lvl, double morale, double fire, double shock, int firePips, int shockPips, int moralePips, Map<Material,Integer> materials){
        this.type = type;
        this.displayName = displayName;
        this.lvl = lvl;
        this.morale = morale;
        this.fire = fire;
        this.shock = shock;
        this.firePips = firePips;
        this.shockPips = shockPips;
        this.moralePips = moralePips;
        this.materials = materials;
    }
    private final UnitType type;
    private final int lvl;
    private final double morale;
    private final double fire;
    private final double shock;
    private final int firePips;
    private final int shockPips;
    private final int moralePips;
    private final String displayName;
    private final Map<Material,Integer> materials;

    public enum UnitType {
        INF,
        CAV,
        ART,
    }

}
