package earthrp.customObjects;

import earthrp.tools.Tools;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ArmyData {





    @Getter
    @Setter
    private long location;

    @Getter
    @Setter
    private Double shulkerX;

    @Getter
    @Setter
    private Double shulkerY;

    @Getter
    @Setter
    private Double shulkerZ;

    @Getter
    @Setter
    private String world;

    @Getter
    @Setter
    private long locationTime;

    @Getter
    @Setter
    private boolean retreat = false;




    @Getter
    @Setter
    private UUID siegeTown = Tools.EMPTY_UUID;

    @Getter
    @Setter
    private UUID inHand = Tools.EMPTY_UUID;

    @Getter
    @Setter
    private int leaderSiege;

    @Getter
    @Setter
    private int leaderMovement;


    @Getter
    @Setter
    private boolean barbarian;




}
