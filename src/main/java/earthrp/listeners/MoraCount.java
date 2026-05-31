package earthrp.listeners;

import earthrp.Earth;
import earthrp.customEnums.TownItem;
import earthrp.customObjects.*;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import earthrp.events.NewDayEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

import static java.lang.Integer.parseInt;

public class MoraCount implements Listener {

    private final Earth plugin;
    private ServerDatabase db;
    public MoraCount (Earth plugin) {
        this.plugin = plugin;
        db = plugin.getServerDatabase();
    }

    @EventHandler
    public void onNewDay(NewDayEvent e)  {

        Set<EPlayer> players = db.getPlayers();
        boolean status = db.getStatusMora();
        if (status) {
            StringBuilder otherTownPromt = new StringBuilder();
            StringBuilder otherPlayerPromt = new StringBuilder();

            Bukkit.broadcastMessage("New day!");
            for(Town t:db.getTowns()){
                otherTownPromt.append(t.getName() + ":\n"+
                        "type - " + t.getType() + "\n" +
                        "people - " + t.getPeople() + "\n" +
                        "location - X=" + t.getLocation().getBlockX() + " Z=" + t.getLocation().getBlockZ() + "\n" +
                        "owner - " + t.getOwnerName() + "\n" );
            }
            for(EPlayer p:players){
                p.getData().setRetreat(false);
                otherPlayerPromt.append(p.getCountryName() + ":\n"+
                        "isBot - " + p.getData().isBot + "\n" +
                        "treasury - " + p.getAttribute(EPlayerAttribute.TREASURY) + "\n");
                int n = 0;
                for(Army army:p.getArmies()){
                    n++;
                    int inf = 0;
                    int cav = 0;
                    for(Unit unit:army.getUnits()){
                        if(unit.getType().equals("inf")){
                            inf++;
                        }else{
                            cav++;
                        }
                    }
                    otherPlayerPromt.append("army"+n +":\n" +
                            "Infantry: " + inf +"\n" +
                            "Cavalry: " + cav + "\n" +
                            "Morale: " +army.getMorale() + "\n"

                    );

                }
            }
            for(EPlayer p:players){
                StringBuilder townPromt = new StringBuilder();
                StringBuilder armyPromt = new StringBuilder();


                int lvl = 1;
                int n = 0;
                for(Army army:p.getArmies()){
                    n++;
                    int inf = 0;
                    int cav = 0;
                    for(Unit unit:army.getUnits()){
                        if(unit.getType().equals("inf")){
                            inf++;
                        }else{
                            cav++;
                        }
                    }
                    armyPromt.append("army"+n +":\n" +
                            "Infantry: " + inf +"\n" +
                            "Cavalry: " + cav + "\n" +
                            "Morale: " +army.getMorale() + "\n"

                    );

                }


                for(Building b:p.getBuildings()){
                    if(b.getItem()!=null){
                        if (b.getType().equals("farm")) {
                            int farmlands = countFarmland(b.getLocation());
                            long wheat = Math.round(farmlands * p.getAttribute(EPlayerAttribute.FARM_EFFICIENCY));
                            b.getTown().addItem(TownItem.WHEAT, wheat);
                        }
                    }
                }
                double armySatietyTarget = 1.0;
                for (Town t : p.getTowns()) {
                    int peopleHunger = t.getPeople() * 2;
                    int armyHunger = 0;

                    // 1. Рассчитываем базовый запас (пассивный доход еды города)
                    int baseFood = t.isCapital() ? 20 : 15;



                    if (t.isCapital()) {
                        for (Army a : p.getArmies()) {
                            armyHunger += a.getSize();
                        }
                    }

                    // Общий спрос на еду
                    int totalHunger = peopleHunger + armyHunger;

                    // Вычитаем базовое пропитание из общего голода
                    totalHunger = Math.max(0, totalHunger - baseFood);

                    // 2. Потребление ресурсов (от лучшего к худшему)
                    // Массив пар: Тип еды -> Питательность
                    totalHunger = consumeFood(t, TownItem.BREAD, 3, totalHunger);
                    totalHunger = consumeFood(t, TownItem.WHEAT, 1, totalHunger);

                    TownItem[] cookedItems = {TownItem.COOKED_BEEF, TownItem.COOKED_PORKCHOP, TownItem.COOKED_CHICKEN, TownItem.COOKED_MUTTON, TownItem.COOKED_COD};
                    TownItem[] rawItems = {TownItem.BEEF, TownItem.PORKCHOP, TownItem.CHICKEN, TownItem.MUTTON, TownItem.COD};

                    for (TownItem item : cookedItems) {
                        totalHunger = consumeFood(t, item, 4, totalHunger);
                    }
                    for (TownItem item : rawItems) {
                        totalHunger = consumeFood(t, item, 2, totalHunger);
                    }

                    // 3. Проверка на голод (если после всей еды голод остался)
                    if (totalHunger > 0 && peopleHunger > 0) {
                        // Рассчитываем коэффициент голода относительно изначального спроса людей
                        double famineSeverity = (double) totalHunger / (peopleHunger + armyHunger);
                        t.setFamine(famineSeverity);
                        if (t.isCapital()) {
                            armySatietyTarget = 1.0 - famineSeverity;
                        }
                    } else {
                        t.setFamine(0);
                        p.setAttribute(EPlayerAttribute.ARMY_SATIETY_MAX,1.0);
                    }

                    int maxPeople = t.isCapital() ? 15 : 13;

                    if (p.getData().isBot){
                        townPromt.append(t.getName() + ":\n"+
                                "type - " + t.getType() + "\n" +
                                "people - " + t.getPeople() + "\n" +
                                "location - X=" + t.getLocation().getBlockX() + " Z=" + t.getLocation().getBlockZ() + "\n" +
                                "maxPeople - " + maxPeople + "\n");
                    }
                }
                p.setAttribute(EPlayerAttribute.ARMY_SATIETY_MAX, armySatietyTarget);
                int balance = Tools.getBalance(p);

                p.addAttribute(EPlayerAttribute.TREASURY, balance);


                p.addAttribute(EPlayerAttribute.OI_BALANCE,p.getOiIncome());

                int pp_balance = (int) p.getAttribute(EPlayerAttribute.POLIT_BALANCE);
                int politIncome = p.getPolitIncome();
                int politMax = p.getMaxPolit();
                p.setAttribute(EPlayerAttribute.POLIT_BALANCE, Math.min(politMax, pp_balance + politIncome));

                p.addAttribute(EPlayerAttribute.INFLATION,p.getAttribute(EPlayerAttribute.INFLATION_REDUCE));
                double manpowerIncreaseMod = p.getAttribute(EPlayerAttribute.MANPOWER_REC_MOD) + (p.getAttribute(EPlayerAttribute.WAR_SUPPORT) * 0.1 );

                double newManpower = p.getAttribute(EPlayerAttribute.MANPOWER) + (p.getManpowerLimit()*manpowerIncreaseMod);

                p.setAttribute(EPlayerAttribute.MANPOWER,Math.min(Math.floor(newManpower),p.getManpowerLimit()));


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

                if(p.getData().isBot){
                    Earth.getInstance().getGeminiManager().askRulerAI("You are the Ruler of" + p.getCountryName() + " in a Minecraft Military-Political-server. You have " + p.getAttribute(EPlayerAttribute.TREASURY) + " money. You must protect your lands and behave like a proud medieval ruler.\n" +
                            "Your towns:\n" +
                            townPromt +
                            "your Army:\n" +
                            armyPromt +
                            "You are in war:" + p.isWar() +
                            "Other countries:\n"+
                            otherPlayerPromt +
                            "Other towns:\n" +
                            otherTownPromt +
                            "Your manpower: " + p.getAttribute(EPlayerAttribute.MANPOWER) +
                            "Your max manpower: " + p.getAttribute(EPlayerAttribute.PEOPLE) +
                            "You can:\n" +
                            "1. built new house for people, if current amount of people in town lower than maximum, its cost 5 money, and increase amount of people in town by 1."+
                            "2. recruit infantry or cavalry, each regiment cost 1 manpower. 1 regiment of infantry cost 9 money to recruit and 1 money to supply. 1 regiment of cavalry cost 18 money to recruit and 2 money to supply. cavalry stronger than infantry, but if amount of cavalry in battle more tha amount of infantry your army gain debuff = +25% damage taken"+
                            "3. declare wars on another countries or form an alliance" +
                            "Each people gives 1 money by tax income and increase your maximum of manpower by 1. If current manpower less than maximum, you gain 3 manpower ech day. Tax income is given once a day." +
                            "*CRITICAL RULES:\n" +
                            "\n" +
                            "ALWAYS respond in Russian language. Use a medieval, majestic, and slightly arrogant Russian tone (e.g., use words like \"Приветствую\", \"Владыка\", \"Ступай с миром\").\n" +
                            "\n" +
                            "NEVER speak English to the players.*");
                }
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

        for (int x = bX - 4; x <= bX + 4; x++) {
            for (int z = bZ - 4; z <= bZ + 4; z++) {
                if (world.getBlockAt(x, bY, z).getType() == Material.FARMLAND) {
                    farmlandCount++;
                }
            }
        }
        return farmlandCount;
    }

    private int consumeFood(Town t, TownItem item, int multiplier, int currentHunger) {
        if (currentHunger <= 0) return 0;

        long amountAvailable = t.getItem(item);
        long foodValueAvailable = amountAvailable * multiplier;

        if (foodValueAvailable <= currentHunger) {
            // Съедаем всё
            t.addItem(item, -foodValueAvailable);
            return Math.toIntExact(currentHunger - foodValueAvailable);
        } else {
            // Съедаем только часть
            int itemsToEat = (int) Math.ceil((double) currentHunger / multiplier);
            t.addItem(item, -itemsToEat);
            return 0;
        }
    }

}
