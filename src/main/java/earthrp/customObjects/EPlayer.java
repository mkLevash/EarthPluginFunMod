package earthrp.customObjects;

import java.util.*;
import java.util.stream.Collectors;


import com.google.gson.Gson;
import earthrp.Earth;
import earthrp.customEnums.*;
import earthrp.tools.Tools;
import earthrp.database.ServerDatabase;
import earthrp.configs.CustomConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


@Getter
@Setter
public class EPlayer implements Comparable<EPlayer> {
    private final ServerDatabase db;

    public EPlayer(UUID playerUniqueId){
        this.uniqueId = playerUniqueId;
        db = Earth.getInstance().getDatabase();
    }

    private static final Gson gson = new Gson();


    private PlayerData data;

    private String rawJson;


    public void loadData(String json) {
        if (json == null || json.isEmpty()) {
            this.data = new PlayerData();
        } else {
            this.data = gson.fromJson(json, PlayerData.class);
        }
    }


    public String serializeData() {
        return gson.toJson(this.data);
    }


    public PlayerData getData() {
        if (this.data == null) this.data = new PlayerData();
        return this.data;
    }

    private final Map<EPlayerAttribute, Double> attributes = new EnumMap<>(EPlayerAttribute.class);
    private final Map<EPlayerTech, Boolean> techMap = new EnumMap<>(EPlayerTech.class);

    public void setAttribute(EPlayerAttribute type, double newValue) {
        attributes.put(type, newValue);
    }
    public void addAttribute(EPlayerAttribute type, double delta) {setAttribute(type, Tools.round(getAttributeValue(type) + delta));}
    public double getAttributeValue(EPlayerAttribute type) {return Tools.round(attributes.getOrDefault(type, type.getDefaultValue()));}

    public double getAttribute(EPlayerAttribute attribute) {
        double baseValue = getAttributeValue(attribute); // Твое дефолтное значение из Enum
        Set<PlayerModifier> modifiers = getAttributeModifiers(attribute);

        if (modifiers == null || modifiers.isEmpty()) {
            return baseValue;
        }

        // 1. Сначала применяем все операции ADD
        double totalAdd = 0;
        for (PlayerModifier mod : modifiers) {
            totalAdd += mod.getAttributes().get(attribute);
        }
        double result = baseValue + totalAdd;


        return Tools.round(result);
    }

    public Set<PlayerModifier> getAttributeModifiers(EPlayerAttribute attribute){
        return data.getModifiers().stream()
                .filter(modifier -> modifier.getAttributes().containsKey(attribute))
                .collect(Collectors.toSet());
    }

    public void setTech(EPlayerTech type, boolean newValue) {
        techMap.put(type, newValue);
    }
    public boolean getTech(EPlayerTech type) {return techMap.getOrDefault(type, type.isResearched());}

    private final UUID uniqueId;
    private String displayName;
    private String countryName;



    private final Set<Army> armies = new HashSet<>();
    public Set<Army> getArmies() {return Collections.unmodifiableSet(armies);}
    public void addArmy(Army army) {this.armies.add(army);}
    public void removeArmy(Army army) {this.armies.remove(army);}

    private final Set<Town> towns = new HashSet<>();
    public Set<Town> getTowns() {return Collections.unmodifiableSet(towns);}
    public void addTown(Town town) {
        this.towns.add(town);
        townsControlled.add(town);
    }
    public void removeTown(Town town) {
        this.towns.remove(town);
        townsControlled.remove(town);
    }

    private final Set<Town> townsControlled = new HashSet<>();
    public Set<Town> getTownsControlled() {return Collections.unmodifiableSet(townsControlled);}


    public void annexTown(Town town){
        controlTown(town);
        town.getOwner().removeTown(town);
        town.setType("townHall");
        town.setCore(false);
        town.setOwnerId(uniqueId);
        addTown(town);
    }

    public void controlTown(Town town) {
        townsControlled.add(town);
        town.getController().deControlTown(town);
        town.getData().setController(uniqueId);

    }
    public void deControlTown(Town town) {this.townsControlled.remove(town);}

    public int getOiIncome(){
        double oi = 0;
        double bMod = getAttribute(EPlayerAttribute.OI_FROM_BUILDING);
        for(Town town:getTowns()){
            if(town.isCapital()){
                oi += 5;
            }
        }
        for (Building building:getBuildings()){
            if(building.getData().getType().equals(BuildingType.LIBRARY)){
                oi += Tools.round(1 * bMod);
            }
            if(building.getData().getType().equals(BuildingType.UNIVERSITY)){
                oi += Tools.round(5 * bMod);
            }
        }

        return (int) Math.round(getAttribute(EPlayerAttribute.OI_INCOME) + oi);

    }

    public void takeArmy(Army army){
        data.armiesInHand.add(army.getUuid());
        army.getData().setInHand(uniqueId);

    }

    public void placeArmy(Army army){
        data.armiesInHand.remove(army.getUuid());
        army.getData().setInHand(Tools.EMPTY_UUID);
        if(data.armiesInHand.isEmpty()){
            Player player = Bukkit.getPlayer(uniqueId);
            if(player!=null) player.clearActivePotionEffects();
        }


    }

    public int getMaxLeaderMovement(){
        int move = 0;
        for(UUID armyId : getData().armiesInHand){
            int lm = db.getArmy(armyId).getData().getLeaderMovement();
            if(lm>move){
                move = lm;
            }
        }
        return move;
    }

    public void addBarbariansAmount(int amount){
        int old = data.getBarbarians();
        data.setBarbarians(old + amount);
    }

    public List<ArmyUnit> getUnits(){
        List<ArmyUnit> units = new ArrayList<>();
        for(Army a:armies){
            units.addAll(a.getUnits());
        }
        return units;
    }

    public int getRevanchism(){
        return (int) Math.min(3,Math.round(getAttribute(EPlayerAttribute.REVANCHISM)));
    }


    public double getLivingBuildingCost(){
        return Math.max( 0.1, Tools.round(getAttribute(EPlayerAttribute.BUILDING_COST) - (1 - getAttribute(EPlayerAttribute.LIVING_BUILDING_COST)) ));
    }

    public double getWarBuildingCost(){
        return Math.max( 0.1, Tools.round(getAttribute(EPlayerAttribute.BUILDING_COST) - (1 - getAttribute(EPlayerAttribute.WAR_BUILDING_COST))));
    }
    public double getScienceBuildingCost(){
        return Math.max(0.1, Tools.round(getAttribute(EPlayerAttribute.BUILDING_COST) - (1 - getAttribute(EPlayerAttribute.SCIENCE_BUILDING_COST))));
    }

    public double getTribute(){


        double tribute = getAttribute(EPlayerAttribute.TRIBUTE) + ( getIncome() * getAttribute(EPlayerAttribute.TRIBUTE_PERCENTAGE));
        return Tools.round( tribute * getAttribute(EPlayerAttribute.TRIBUTE_MOD));
    }

    public double getMoraleMod(){
        return Tools.round(getAttribute(EPlayerAttribute.MORALE_MOD) + (getAttribute(EPlayerAttribute.TRADITION)*getAttribute(EPlayerAttribute.MORALE_TRADITION)) + (getRevanchism() * getAttribute(EPlayerAttribute.REVANCHISM_MOD) * 0.1));
    }

    public String getSatietyColor(){
        if(getAttribute(EPlayerAttribute.ARMY_SATIETY) < 1){
            return "&c"+ (int) (getAttribute(EPlayerAttribute.ARMY_SATIETY) * 100) + "&f%";
        }else{
            return "&a"+ (int) (getAttribute(EPlayerAttribute.ARMY_SATIETY) * 100) + "&f%";
        }
    }

    public String getSupplyColor(){
        if(getAttribute(EPlayerAttribute.ARMY_SUPPLY) < 1){
            return "&c"+ (int) (getAttribute(EPlayerAttribute.ARMY_SUPPLY) * 100) + "&f%";
        }else{
            return "&a"+ (int) (getAttribute(EPlayerAttribute.ARMY_SUPPLY) * 100) + "&f%";
        }
    }



    public int getPeasant(){
        int people = 0;
        for(Town t: towns){

            if(t.isCore() && !t.isOccupied()) {
                people += t.getPeasant();
            };
        }
        return people;
    }

    public void breakAlly(EPlayer target){
        int date = (int) (Earth.getInstance().getDatabase().getStatusDay() + Math.max(1,3-getAttribute(EPlayerAttribute.TRUCE_LENGTH)));
        UUID targetId = target.getUniqueId();

        data.getAlly().remove(targetId);
        target.getData().getAlly().remove(uniqueId);

        data.getTruceMap().put(targetId,date);
        target.getData().getTruceMap().put(uniqueId,date);

    }


    public int getIdeaCost(){

        var config = Earth.getInstance().getConfig();
        return config.getInt("ideaCostBase") + (config.getInt("ideaIncMod") * data.getIdeas());
    }

    public void mergeLevies(){
        for(var u : getUnits()){
            if(u.getData().isLevies()){
                Earth.getInstance().getDatabase().deleteUnit(u);
            }
        }
        data.setLevies(false);
    }

    public void declareTruce(EPlayer target){
        int date = (int) (Earth.getInstance().getDatabase().getStatusDay() + Math.max(1,3-getAttribute(EPlayerAttribute.TRUCE_LENGTH)));
        UUID targetId = target.getUniqueId();

        data.getTruceMap().put(targetId,date);
        data.getEnemies().remove(targetId);
        removeTruceBroken(target);
        if(!isWar()) mergeLevies();


        target.getData().getTruceMap().put(uniqueId,date);
        target.getData().getEnemies().remove(uniqueId);
        target.removeTruceBroken(this);
        if(!target.isWar()) target.mergeLevies();

    }



    public void truceBreak(EPlayer target){
        if(!isImperialism()){
            truceBreak(target.getUniqueId());
        }
        getData().getTruceMap().remove(target.getUniqueId());
        target.getData().getTruceMap().remove(uniqueId);
    }

    public void truceBreak(UUID targetId){
        addAttribute(EPlayerAttribute.STABILITY,-getAttribute(EPlayerAttribute.TRUCE_BREAK_COST));
        if(getAttribute(EPlayerAttribute.STABILITY)<-3) setAttribute(EPlayerAttribute.STABILITY,-3);
        data.getTruceBroken().add(targetId);

    }

    public void removeTruceBroken(EPlayer target){
        removeTruceBroken(target.getUniqueId());
    }

    public void removeTruceBroken(UUID targetId){
        data.getTruceBroken().remove(targetId);
    }



    public long getMercAmount(){
        int amount = 0;
        for(ArmyUnit u: getUnits()){
            if (u.getData().isMerc()) amount++;
        }
        return amount;
    }

    public long getMercLimit(){
        return Math.round(getPeasant() * getAttribute(EPlayerAttribute.MERC_LIMIT));
    }



    public Map<EarthItem, Integer> getArmySupply() {
        Map<EarthItem, Integer> supply = new HashMap<>();

        for (ArmyUnit u : getUnits()) {
            if (u.getTech().equals("inf") || u.getTech().equals("cav")) {
                switch (u.getLvl()) {
                    case 1, 2 -> supply.merge(EarthItem.IRON_SWORD, 1, Integer::sum);
                    case 3, 4, 5 -> supply.merge(EarthItem.GUN, 1, Integer::sum);
                }
            } else {
                supply.merge(EarthItem.FIRE_CHARGE, 1, Integer::sum);
            }
        }
        return supply;
    }

    public boolean isWar(){
        return !data.getEnemies().isEmpty();
    }

    public boolean isImperialism(){
        return data.isImperialismWar();
    }


    public boolean isLevies(){
        return getData().isLevies();
    }

    public void setLevies(boolean bool){
        data.setLevies(bool);
    }

    public void setImperialism(boolean bool){
        data.setImperialismWar(bool);
    }


    public int getStabCost(){
        return (int) Math.round((5 + getAttribute(EPlayerAttribute.STABILITY)) * getAttribute(EPlayerAttribute.STAB_COST));
    }



    public int getAdminEff(){
        if(data.getTruceBroken().isEmpty()){
            return (int) getAttribute(EPlayerAttribute.ADMIN_EFFICIENCY);
        }else{
            return (int) Math.max(0,getAttribute(EPlayerAttribute.ADMIN_EFFICIENCY)-3);
        }
    }

    public double getCoreCost(){
        return Math.max(0.1, Tools.round(getAttribute(EPlayerAttribute.CORE_CREATION_COST) - (getAdminEff()*0.05) ));
    }

    public int getManpowerLimit(){

        return (int) Math.floor(getPeasant()*getAttribute(EPlayerAttribute.MANPOWER_LIMIT_MOD)*1000);
    }

    public int getPolitIncome(){
        return (int) Math.floor(getAttribute(EPlayerAttribute.POLIT_INCOME) * getAttribute(EPlayerAttribute.POLIT_INCOME_MOD) );
    }

    public List<Building> getBuildings(){
        List<Building> buildings = new ArrayList<>();
        for(Town t: towns){
            buildings.addAll(t.getBuildings());
        }
        return buildings;
    }

    public double getManpowerIncreaseMod(){
        return  Math.max(0.0 , getAttribute(EPlayerAttribute.MANPOWER_REC_MOD) + (getAttribute(EPlayerAttribute.WAR_SUPPORT) * 0.1 ));
    }

    public String getMPIncreaseModColor(){
        double mod = getManpowerIncreaseMod();
        String modifier = Tools.round(mod * 100) + "<white>%";
        if(mod <= 0){
            return "<red>"+modifier;
        }
        return "<green>" + modifier;
    }

    public long getManpowerIncrease(){

        return Math.round(1000 + (getManpowerLimit()*getManpowerIncreaseMod()));
    }

    public Set<EPlayer> getTraders(){
        Set<EPlayer> traders = new HashSet<>();
        Set<UUID> trade = data.getTrade();
        if(trade.isEmpty()) return traders;
        else{
            for(UUID id : trade){
                EPlayer trader = db.getPlayer(id);
                if (trader != null) traders.add(trader);
            }
        }
        return traders;
    }

    public boolean haveMarket(){
        if(!data.isMarket() && !getTraders().isEmpty()){
            for(EPlayer trader : getTraders()){
                if(trader.getData().isMarket()){
                    return true;
                }
            }
        }
        return data.isMarket();
    }


    public double getTaxIncome(){
        double tax = getAttribute(EPlayerAttribute.TAX_INCOME);
        for(Town t: getTowns()){
            if (t.isOccupied()) continue;
            tax += t.getTaxIncome();
        }
        return Tools.round(tax * getTaxMod());
    }

    public double getTaxMod(){
        double mod = getAttribute(EPlayerAttribute.TAX_MOD);
        if(isLevies()) mod *= 0.75;
        return mod;
    }

    public String getTaxModColor(){
        double mod = getTaxMod();
        String modifier = Tools.round((getTaxMod() - 1) * 100) + "<white>%";
        if(mod < 1){
            return "<red>" + modifier;
        }
        if(mod > 1){
            return "<green>" + modifier;
        }
        return "<gray>" + modifier;





    }


    public String getMPLimitModColor(){
        double mod = getAttribute(EPlayerAttribute.MANPOWER_LIMIT_MOD);
        String modifier = Tools.round((mod - 1) * 100) + "<white>%";
        if(mod < 1){
            return "<red>" + modifier;
        }
        if(mod > 1){
            return "<green>" + modifier;
        }
        return "<gray>" + modifier;
    }

    public String getMercLimitModColor(){
        double mod = getAttribute(EPlayerAttribute.MERC_LIMIT);
        String modifier = Tools.round((mod - 1) * 100) + "<white>%";
        if(mod < 1){
            return "<red>" + modifier;
        }
        if(mod > 1){
            return "<green>" + modifier;
        }
        return "<gray>" + modifier;
    }

    public String getMPRecModColor(){
        double mod = getTaxMod();
        String modifier = Tools.round((mod - 1) * 100) + "<white>%";
        if(mod < 1){
            return "<red>" + modifier;
        }
        if(mod > 1){
            return "<green>" + modifier;
        }
        return "<gray>" + modifier;
    }

    public double getProdMod(){
        double mod = getAttribute(EPlayerAttribute.PROD_MOD);
        if(isLevies()) mod *= 0.75;
        return mod;
    }

    public int getMaxPolit(){
        double max = getAttribute(EPlayerAttribute.POLIT_MAX);
        return (int) Math.floor(max*getAttribute(EPlayerAttribute.POLIT_MAX_MOD));
    }

    public double getTradeMod(){
        double mod = getAttribute(EPlayerAttribute.TRADE_MOD);
        for(Town t:getTowns()){
            mod += t.getTradeMod();
        }
        if(!getTraders().isEmpty()){
            for(EPlayer trader:getTraders()){
                for(Town t:trader.getTowns()){
                    mod += t.getTradeMod();
                }
            }
        }
        return mod;
    }

    public String getTradeModColor(){
        double mod = getTradeMod();
        String modifier = Tools.round((mod - 1) * 100) + "<white>%";
        if(mod < 1){
            return "<red>" + modifier;
        }
        if(mod > 1){
            return "<green>" + modifier;
        }
        return "<gray>" + modifier;
    }

    public String getMercMaintenanceColor(){
        double mod = getAttribute(EPlayerAttribute.MERC_COST);
        String modifier = Tools.round((mod - 1) * 100) + "<white>%";
        if(mod < 1){
            return "<green>" + modifier;
        }
        if(mod > 1){
            return "<red>" + modifier;
        }
        return "<gray>" + modifier;
    }

    public String getArmyMaintenanceColor(){
        double mod = getAttribute(EPlayerAttribute.ARMY_EXPENSE_MOD);
        String modifier = Tools.round((mod - 1) * 100) + "<white>%";
        if(mod < 1){
            return "<green>" + modifier;
        }
        if(mod > 1){
            return "<red>" + modifier;
        }
        return "<gray>" + modifier;
    }
    public String getArmyMaintenanceColor(UnitTech.UnitType type){
        double mod;
        switch (type){
            case ART -> {
                mod = getAttribute(EPlayerAttribute.ART_COST);
            }
            case INF -> {
                mod = getAttribute(EPlayerAttribute.INF_COST);
            }
            case CAV -> {
                mod = getAttribute(EPlayerAttribute.CAV_COST);
            }
            default -> {
                mod = getAttribute(EPlayerAttribute.ARMY_EXPENSE_MOD);
            }
        }

        String modifier = Tools.round((mod - 1) * 100) + "<white>%";
        if(mod < 1){
            return "<green>" + modifier;
        }
        if(mod > 1){
            return "<red>" + modifier;
        }
        return "<gray>" + modifier;
    }



    public double getTradeIncome(){
        double income = 0;
        Set<EarthItem> world = new HashSet<>();
        for (Building b:db.getBuildings()){

            if(b.getData().getItem()!=null && b.getOwner()!=this && !getTraders().contains(b.getOwner())  ) world.add(b.getData().getItem());
        }

        for (Town t : towns) {
            income += t.getTradeIncome(world);
        }

        return income;

    }

    public int getNavalLimit(){
        int limit = 0;
        for(var t : getTowns()){
            if(t.isCore() && !t.isOccupied()){
                if(t.isShipyard()) limit += 5;
            }
        }
        return limit;
    }



    public double getProdIncome(){
        double income = 0;
        for (Town t : towns) {
            income += t.getProdIncome();
        }
        return  Tools.round(income * getProdMod());
    }

    public double getIncome(){
        return  Tools.round(getTradeIncome() + getTaxIncome() + getAttribute(EPlayerAttribute.INCOME));
    }

//    public int getIncome(){
//
//        return (int) Math.round(getFlatIncome() * inflation);
//    }

    public double getExpense(){
        double inflation = Tools.round(1 + (getAttribute(EPlayerAttribute.INFLATION)*0.01));
        double exp = getMercExpense() + getLandArmyExpense() + getDebtExpense() + getTribute() + getAttribute(EPlayerAttribute.EXPENSE);

        return Tools.round(exp * inflation);
    }



    public long getBalance(){


        double corruption = Tools.round(1 - getAttribute(EPlayerAttribute.CORRUPTION) * 0.1);

        return Math.round( (getIncome() - getExpense()) * corruption );
    }

    public void payDay(){
        addAttribute(EPlayerAttribute.TREASURY,getBalance());

        while (getAttribute(EPlayerAttribute.TREASURY) < 0 && canDebt()){
            UUID debtId = UUID.randomUUID();
            getData().getDebtMap().put(debtId, getDebtSize());
            getData().getInterestMap().put(debtId,getInterest());
            addAttribute(EPlayerAttribute.TREASURY, getDebtSize());
        }
        if(getAttribute(EPlayerAttribute.TREASURY) < 0){
            declareBankruptcy();
        }

    }

    public void declareBankruptcy(){
        setAttribute(EPlayerAttribute.TREASURY,0);
        for(Army a : getArmies()){
            db.deleteArmy(a);
        }
        getData().getDebtMap().clear();
        getData().getInterestMap().clear();
        //getData().addModifier(EPlayerAttribute.CORRUPTION,);


    }

    public HashMap<String, List<ArmyUnit>> getTroops(){
        HashMap<String, List<ArmyUnit>> res = new HashMap<>();
        List<ArmyUnit> inf = new ArrayList<>();
        List<ArmyUnit> cav = new ArrayList<>();
        List<ArmyUnit> art = new ArrayList<>();
        for (Army a:armies){
            if(a.getOwnerId().equals(uniqueId)){
                Set<ArmyUnit> units = a.getUnits();
                for(ArmyUnit u:units){
                    switch (u.getType()){
                        case INF->{
                            inf.add(u);
                            //expense += Tools.round(u.getLvl()*getArmyStats().getInfCost());
                        }
                        case CAV ->{
                            cav.add(u);
                            //expense += Tools.round(u.getLvl()*2.5*getArmyStats().getCavCost());
                        }
                        case ART ->{
                            art.add(u);
                            //expense += Tools.round(u.getLvl()*3*getArmyStats().getArtCost());
                        }
                    }
                }
            }
        }
        res.put("inf",inf);
        res.put("cav",cav);
        res.put("art",art);
        return res;
    }

    public List<Town> getTradeTowns(){
        List<EPlayer> tradePlayers = new ArrayList<>();
        for(EPlayer p:db.getPlayers()){
            String path = "trade." + displayName + "." + p.getDisplayName();
            if (CustomConfig.get().getBoolean(path)){
                tradePlayers.add(p);
            }
        }
        List<Town> result = new ArrayList<>(towns);
        for(EPlayer p:tradePlayers){
            Set<Town> towns = p.getTowns();
            result.addAll(towns);
        }
        return result;
    }

    public double getInfExpense(){
        List<ArmyUnit> inf = getTroops().get("inf");
        double expense = 0.0;
        for(ArmyUnit u:inf){
            expense += Tools.round(u.getLvl() * getAttribute(EPlayerAttribute.INF_COST));
        }
        return expense;
    }
    public double getCavExpense(){
        List<ArmyUnit> inf = getTroops().get("cav");
        double expense = 0.0;
        for(ArmyUnit u:inf){
            expense += Tools.round(u.getLvl() * 2.5 * getAttribute(EPlayerAttribute.CAV_COST));
        }
        return expense;
    }
    public double getArtExpense(){
        List<ArmyUnit> inf = getTroops().get("art");
        double expense = 0.0;
        for(ArmyUnit u:inf){
            expense += Tools.round(u.getLvl()*3 * getAttribute(EPlayerAttribute.ART_COST));
        }
        return expense;
    }

    public double getLandArmyExpense(){
        double expense=0;

        for(ArmyUnit u:getUnits()){
            if(u.getData().isLevies()) continue;
            if(!u.getData().isMerc()){
                switch (u.getType()){
                    case INF->{
                        expense += Tools.round(1*getAttribute(EPlayerAttribute.INF_COST));
                    }
                    case CAV ->{
                        expense += Tools.round(2.5* getAttribute(EPlayerAttribute.CAV_COST));
                    }
                    case ART ->{
                        expense += Tools.round(3* getAttribute(EPlayerAttribute.ART_COST));
                    }
                }
            }

        }
        return Tools.round(expense*getAttribute(EPlayerAttribute.ARMY_EXPENSE_MOD));
    }

    public double getMercExpense(){

        double expense=0;

        for(ArmyUnit u:getUnits()){
            if(u.getData().isLevies()) continue;
            if(u.getData().isMerc()){
                switch (u.getType()){
                    case INF->{
                        expense += Tools.round((1+u.getLvl())*(getAttribute(EPlayerAttribute.INF_COST) - (1 - getAttribute(EPlayerAttribute.MERC_COST) ) ));
                    }
                    case CAV ->{
                        expense += Tools.round((2.5+u.getLvl())* (getAttribute(EPlayerAttribute.CAV_COST) - (1 - getAttribute(EPlayerAttribute.MERC_COST) )));
                    }
                    case ART ->{
                        expense += Tools.round((3+u.getLvl())* (getAttribute(EPlayerAttribute.ART_COST)  - (1 - getAttribute(EPlayerAttribute.MERC_COST) )));
                    }
                }
            }

        }
        return Tools.round(expense*getAttribute(EPlayerAttribute.MERC_COST));

    }

    public String getMercMoraleColor(){
        double mod = getAttribute(EPlayerAttribute.MERC_MORALE);
        String modifier = Tools.round(mod * 100) + "<white>%";
        if(mod < 0){
            return "<red>" + modifier;
        }
        if(mod > 0){
            return "<green>" + modifier;
        }
        return "<gray>" + modifier;
    }
    public String getMercDiscColor(){
        double mod = getAttribute(EPlayerAttribute.MERC_DISC);
        String modifier = Tools.round(mod * 100) + "<white>%";
        if(mod < 0){
            return "<red>" + modifier;
        }
        if(mod > 0){
            return "<green>" + modifier;
        }
        return "<gray>" + modifier;
    }



    public double getInterest(){
        double interest = getAttribute(EPlayerAttribute.INTEREST);
        interest -= (0.02 * getAttribute(EPlayerAttribute.STABILITY));
        return interest;
    }

    public boolean haveBank(){
        for(Building b: getBuildings()){
            if (b.getData().getType().equals(BuildingType.BANK)){
                return true;
            }
        }
        return false;
    }

    public int getDebtSize(){
        int size = 5;
        if(getTech(EPlayerTech.BANK_UP)) size = 10;
        if(haveBank()) size = 25;
        return size;
    }

    public boolean canDebt(){
        return getDebtExpense() < getIncome();
    }


    public int getDebt(){

        return (int) getData().getDebtMap().entrySet().stream()
                .mapToDouble(entry -> {
                    double interest = 1 + getData().getInterestMap().getOrDefault(entry.getKey(), 0.0);
                    return Math.ceil(entry.getValue() * interest);
                })
                .sum();
    }

    public List<ItemStack> getDebtList(){
        List<ItemStack> debts = new ArrayList<>();
        for (UUID debtId : getData().getDebtMap().keySet()) {

            debts.add(Tools.createDebtItem(this,debtId));
        }
        return debts;
    }

    public double getDebtExpense(){
        double ex = getData().getDebtMap().entrySet().stream()
                .mapToDouble(entry -> {
                    double interest = getData().getInterestMap().getOrDefault(entry.getKey(), 0.0);
                    return (entry.getValue() * interest) / 12.0;
                })
                .sum();
        return Tools.round(ex);
    }

    public List<Army> getArmiesInHand() {
        List<Army> armies = new ArrayList<>();
        for (UUID id : this.getData().armiesInHand) {
            Army army = Earth.getInstance().getDatabase().getArmy(id);
            if (army != null) { // Защита от NullPointerException, если армии нет в БД
                armies.add(army);
            }
        }
        return armies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EPlayer player = (EPlayer) o;
        return Objects.equals(uniqueId, player.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }

    @Override
    public String toString() {
        return "EPlayer{name='" + displayName + "', uuid=" + uniqueId.toString().substring(0,5) + "}";
    }

    @Override
    public int compareTo(EPlayer other) {
        // Сортировка по имени, а если имена равны — по UUID
        int res = displayName.compareTo(other.displayName);
        if (res == 0) res = uniqueId.compareTo(other.uniqueId);
        return res;
    }
}
