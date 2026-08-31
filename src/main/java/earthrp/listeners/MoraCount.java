package earthrp.listeners;

import earthrp.Earth;
import earthrp.customEnums.BuildingType;
import earthrp.customEnums.EarthItem;
import earthrp.customEnums.UnitTech.UnitType;
import earthrp.customObjects.*;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import earthrp.events.NewDayEvent;
import earthrp.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.lang.Integer.parseInt;

public class MoraCount implements Listener {

    private final Earth plugin;
    private ServerDatabase db;
    public MoraCount (Earth plugin) {
        this.plugin = plugin;
        db = plugin.getDatabase();
    }

    @EventHandler
    public void onNewDay(NewDayEvent e)  {

        Set<EPlayer> players = db.getPlayers();
        boolean status = db.getStatusMora();
        if (status) {

            Bukkit.broadcastMessage("New day!");
            int today = db.getStatusDay();

            for(Army a:db.getArmies()){
                if(!a.isBattle()){
                    a.setRetreat(false);
                }
            }


            for(EPlayer p:players){




                Map<UUID, Integer> truceMap = p.getData().getTruceMap();
                if(!truceMap.isEmpty()){
                    truceMap.entrySet().removeIf(entry -> entry.getValue() == today);
                }

                Set<PlayerModifier> modifiers = p.getData().getModifiers();
                if(!modifiers.isEmpty()){
                    modifiers.removeIf(modifier -> modifier.getDateEnd() == today);
                }



                p.getData().getSiegeStorm().clear();

                for(Building b:p.getBuildings()){
                    b.getData().setStatus(true);
                    if( b.getData().getItem()!=null){
                        if (b.getData().getType().equals(BuildingType.FARM)) {
                            long wheat = b.getFarmProduction();
                            b.getTown().addItem(EarthItem.WHEAT, wheat);
                        }
                        if(b.getData().getType().equals(BuildingType.PASTURE)){
                            int count = b.getLocation().getChunk().getEntities().length;
                            int F = b.getTown().getPeasant() * 50;
                            int S = b.getData().pastureArea;
                            int animalAmount = (int) Math.round(( (0.1) * S * F ) / (S + F));
                            if(b.getData().isPastureMobSpawn() && count <= 100){
                                for (int i = 0; i < animalAmount; i++) {
                                    if(b.getData().getItem().equals(EarthItem.BEEF)){
                                        Cow cow = b.getLocation().getWorld().spawn(b.getLocation(), Cow.class);

                                        cow.setBaby();
                                    }
                                    if(b.getData().getItem().equals(EarthItem.CHICKEN)){
                                        Chicken animal = b.getLocation().getWorld().spawn(b.getLocation(), Chicken.class);

                                        animal.setBaby();
                                    }
                                    if(b.getData().getItem().equals(EarthItem.MUTTON)){
                                        Sheep animal = b.getLocation().getWorld().spawn(b.getLocation(), Sheep.class);

                                        animal.setBaby();
                                    }
                                    if(b.getData().getItem().equals(EarthItem.PORKCHOP)){
                                        Pig animal = b.getLocation().getWorld().spawn(b.getLocation(), Pig.class);

                                        animal.setBaby();
                                    }

                                }
                            }
                        }
                    }
                }
                double armySatietyTarget = 1.0;
                for (Town t : p.getTowns()) {

                    if(t.isSiege()){
                        for(Army a:t.getSiegeArmyList()){
                            if(a.getOwner().getData().getEnemies().contains(t.getController().getUniqueId())) continue;
                            t.getData().getSiegeArmy().remove(a.getUuid());
                            a.getData().setSiegeTown(Tools.EMPTY_UUID);
                        }
                        int siegeChance = t.getSiegeChance();
                        int d = (int) (Math.random() * 100) + 1;
                        if(siegeChance<d){
                            t.getData().addSiegeChance(10);
                        }else{
                            t.besieged();
                        }
                    }

                    int peopleHunger = (int) Math.round(((t.getPeasant() * 10) + (t.getData().noble * 50)) * t.getHungerMod());
                    int armyHunger = 0;

                    // 1. Рассчитываем базовый запас (пассивный доход еды города)
                    if(t.isCapital()) t.addItem(EarthItem.WHEAT,100);
                    else t.addItem(EarthItem.WHEAT,50);



                    if (t.isCapital()) {
                        armyHunger = (p.getUnits().size()*5);
                        for(ArmyUnit u:p.getUnits()){
                            if(u.getData().isMerc()) continue;
                            if(u.getType().equals(UnitType.ART)){
                                if(t.getItem(EarthItem.FIRE_CHARGE) > 0) {
                                    t.addItem(EarthItem.FIRE_CHARGE, -1);
                                    u.setDisc(0.0);
                                }
                                else u.setDisc(-0.9);
                            }else{
                                switch (u.getLvl()){
                                    case 1,2 ->{
                                        if(t.getItem(EarthItem.NETHERITE_SWORD) > 0) {
                                            t.addItem(EarthItem.IRON_SWORD, -1);
                                            u.setDisc(0.15);
                                        }
                                        if(t.getItem(EarthItem.DIAMOND_SWORD) > 0) {
                                            t.addItem(EarthItem.IRON_SWORD, -1);
                                            u.setDisc(0.1);
                                        }

                                        if(t.getItem(EarthItem.IRON_SWORD) > 0) {
                                            t.addItem(EarthItem.IRON_SWORD, -1);
                                            u.setDisc(0.05);
                                        }
                                        else if(t.getItem(EarthItem.COPPER_SWORD) > 0) {
                                            t.addItem(EarthItem.COPPER_SWORD, -1);
                                            u.setDisc(0.00);
                                        }
                                        if(t.getItem(EarthItem.STONE_SWORD) > 0) {
                                            t.addItem(EarthItem.IRON_SWORD, -1);
                                            u.setDisc(-0.05);
                                        }
                                        if(t.getItem(EarthItem.WOODEN_SWORD) > 0) {
                                            t.addItem(EarthItem.IRON_SWORD, -1);
                                            u.setDisc(-0.1);
                                        }
                                        else u.setDisc(-0.9);
                                    }
                                    case 4,5,6 ->{
                                        if(t.getItem(EarthItem.GUN) > 0) {
                                            t.addItem(EarthItem.GUN, -1);
                                            u.setDisc(0.0);
                                        }
                                        else u.setDisc(-0.9);
                                    }
                                }
                            }
                        }
                    }


                    // Общий спрос на еду
                    int totalHunger = peopleHunger + armyHunger;



                    for(EarthItem item: t.getData().getItems().keySet()){
                        if(item!=null && item.getType()== EarthItem.ItemType.FOOD){
                            totalHunger = consumeFood(t, item, item.getFood(), totalHunger);
                        }

                    }

                    // 3. Проверка на голод (если после всей еды голод остался)
                    if (totalHunger > 0 && peopleHunger > 0) {
                        // Рассчитываем коэффициент голода относительно изначального спроса людей
                        double famineSeverity = (double) totalHunger / (peopleHunger + armyHunger);
                        t.setFamine(famineSeverity);
                        if (t.isCapital()) {
                            armySatietyTarget = Math.max(0.1 , 1.0 - famineSeverity);
                        }
                    } else {
                        t.setFamine(0);
                        p.setAttribute(EPlayerAttribute.ARMY_SATIETY_MAX,1.0);
                    }

                }
                p.setAttribute(EPlayerAttribute.ARMY_SATIETY_MAX, armySatietyTarget);

                p.addAttribute(EPlayerAttribute.INFLATION,p.getData().getDebtMap().size() * 0.1);







                p.payDay();


                p.addAttribute(EPlayerAttribute.OI_BALANCE,p.getOiIncome());

                int pp_balance = (int) p.getAttribute(EPlayerAttribute.POLIT_BALANCE);
                int politIncome = p.getPolitIncome();
                int politMax = p.getMaxPolit();
                p.setAttribute(EPlayerAttribute.POLIT_BALANCE, Math.min(politMax, pp_balance + politIncome));

                p.addAttribute(EPlayerAttribute.INFLATION,p.getAttribute(EPlayerAttribute.INFLATION_REDUCE));

                double newManpower = p.getManpowerIncrease() + p.getAttribute(EPlayerAttribute.MANPOWER);
                p.setAttribute(EPlayerAttribute.MANPOWER,Math.min(newManpower,Math.max(p.getManpowerLimit(),p.getAttribute(EPlayerAttribute.MANPOWER))));


//                int manpowerIncrease = 3;
//                if (p.getAttribute(EPlayerAttribute.WAR_STATUS) == 1){
//                    manpowerIncrease = (int) (p.getAttribute(EPlayerAttribute.WAR_SUPPORT));
//                }
//                double newManpower = Math.round((p.getAttribute(EPlayerAttribute.MANPOWER)+manpowerIncrease)*1000.0);
//                for(Unit u:p.getUnits()){
//                    if(u.getMorale()!=u.getMaxMorale()){
//                        double newMorale = Tools.round(u.getMorale() + u.getMaxMorale()*p.getAttribute(EPlayerAttribute.MORALE_REDUCE));
//                        u.setMorale(Math.min(u.getMaxMorale(),newMorale));
//                    }
//
//                    if(u.getHp()!=1000 && newManpower>=u.getDamageTaken()){
//                        newManpower -= u.getDamageTaken();
//                        u.setHp(1000);
//                    }
//
//                }
//                p.setAttribute(EPlayerAttribute.MANPOWER, Math.min(p.getManpowerLimit(), Math.floor(newManpower/1000.0)));


            }
            int day = db.getStatusDay();
            db.updateStatusDay(++day);
        }




    }

    public static int countFarmland(Location center) {
        World world = center.getWorld();
        int bX = center.getBlockX();
        int bY = center.getBlockY() - 1;
        int bZ = center.getBlockZ();

        int farmlandCount = 0;

        for (int x = bX - 13; x <= bX + 13; x++) {
            for (int z = bZ - 13; z <= bZ + 13; z++) {
                if (world.getBlockAt(x, bY, z).getType() == Material.FARMLAND) {
                    farmlandCount++;
                }
            }
        }
        return farmlandCount;
    }

    private int consumeFood(Town t, EarthItem item, int multiplier, int currentHunger) {
        if (currentHunger <= 0) return 0;

        long amountAvailable = t.getItem(item);
        long foodValueAvailable = amountAvailable * multiplier;

        if (foodValueAvailable <= currentHunger) {
            // Съедаем всё
            t.addItem(item, -amountAvailable);
            return Math.toIntExact(currentHunger - foodValueAvailable);
        } else {
            // Съедаем только часть
            int itemsToEat = (int) Math.ceil((double) currentHunger / multiplier);
            t.addItem(item, -itemsToEat);
            return 0;
        }
    }

//    public double calculateSupplyAndModifier(Town town) {
//        Map<EarthItem, Integer> supplyNeeded = getArmySupply();
//
//        // Если армии нет, списывать нечего, возвращаем базовый модификатор
//        if (supplyNeeded.isEmpty()) {
//            return 1.0;
//        }
//
//        int totalRequiredItems = 0;
//        int totalSecuredItems = 0;
//
//        int ironSwordsNeeded = supplyNeeded.getOrDefault(EarthItem.IRON_SWORD, 0);
//        int ironSwordsUsed = 0;
//
//        // Карта для отложенного списания ресурсов
//        Map<EarthItem, Integer> resourcesToRemove = new HashMap<>();
//
//        // 1. Проверяем склад и собираем ресурсы для списания
//        for (Map.Entry<EarthItem, Integer> entry : supplyNeeded.entrySet()) {
//            EarthItem item = entry.getKey();
//            int neededAmount = entry.getValue();
//            totalRequiredItems += neededAmount;
//
//            long availableInTown = town.getItem(item);
//
//            if (item == EarthItem.IRON_SWORD) {
//                // Пытаемся забрать железные мечи
//                ironSwordsUsed = Math.toIntExact(Math.min(neededAmount, availableInTown));
//                totalSecuredItems += ironSwordsUsed;
//
//                if (ironSwordsUsed > 0) {
//                    resourcesToRemove.put(EarthItem.IRON_SWORD, ironSwordsUsed);
//                }
//
//                int remainingSwordNeed = neededAmount - ironSwordsUsed;
//
//                // Если железа не хватило, забираем остаток медными мечами
//                if (remainingSwordNeed > 0) {
//                    long availableCopper = town.getItem(EarthItem.COPPER_SWORD);
//                    int copperSwordsUsed = Math.toIntExact(Math.min(remainingSwordNeed, availableCopper));
//                    totalSecuredItems += copperSwordsUsed;
//
//                    if (copperSwordsUsed > 0) {
//                        resourcesToRemove.put(EarthItem.COPPER_SWORD, copperSwordsUsed);
//                    }
//                }
//            } else {
//                // Для GUN и CANNONBALL забираем сколько есть
//                int usedAmount = Math.toIntExact(Math.min(neededAmount, availableInTown));
//                totalSecuredItems += usedAmount;
//
//                if (usedAmount > 0) {
//                    resourcesToRemove.put(item, usedAmount);
//                }
//            }
//        }
//
//        // 2. Рассчитываем бафф за качественные железные мечи
//        double modifier = 1.0;
//
//        if (ironSwordsNeeded > 0) {
//            int totalSwordsSecured = Math.min(ironSwordsNeeded, town.getItem(EarthItem.IRON_SWORD) + town.getItem(EarthItem.COPPER_SWORD));
//
//            // Бафф рассчитывается, только если потребность в мечах закрыта на 100% (будь то медью или железом)
//            if (totalSwordsSecured >= ironSwordsNeeded) {
//                double ironShare = (double) ironSwordsUsed / ironSwordsNeeded;
//                modifier += (ironShare * 0.05); // +5% если все мечи были железными
//            }
//        }
//
//        // 3. БЕЗУСЛОВНОЕ СПИСАНИЕ: списываем всё, что армия успела урвать со склада
//        for (Map.Entry<EarthItem, Integer> toRemove : resourcesToRemove.entrySet()) {
//            town.addItem(toRemove.getKey(), -toRemove.getValue());
//        }
//
//        return modifier;
//    }

}
