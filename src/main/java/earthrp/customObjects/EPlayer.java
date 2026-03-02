package earthrp.customObjects;

import java.util.*;


import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.EPlayerTech;
import earthrp.database.ServerDatabase;
import earthrp.files.CustomConfig;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EPlayer implements Comparable<EPlayer> {
    private final ServerDatabase db;

    public EPlayer(UUID playerUniqueId){
        this.uniqueId = playerUniqueId;
        db = Earth.getInstance().getServerDatabase();
    }

    private final Map<EPlayerAttribute, Double> attributes = new EnumMap<>(EPlayerAttribute.class);
    private final Map<EPlayerTech, Boolean> techMap = new EnumMap<>(EPlayerTech.class);

    public void setAttribute(EPlayerAttribute type, double newValue) {
        attributes.put(type, newValue);
    }
    public void addAttribute(EPlayerAttribute type, double delta) {setAttribute(type, Tools.round(getAttribute(type) + delta));}
    public double getAttribute(EPlayerAttribute type) {return Tools.round(attributes.getOrDefault(type, type.getDefaultValue()));}

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
    public void addTown(Town town) {this.towns.add(town);}
    public void removeTown(Town town) {this.towns.remove(town);}

    public int getOiIncome(){
        double oi = 0;
        double bMod = 1 + getAttribute(EPlayerAttribute.OI_FROM_BUILDING);
        for (Building building:getBuildings()){
            if(building.getType().equals("school")){
                oi += Tools.round(1 * bMod);
            }
            if(building.getType().equals("university")){
                oi += Tools.round(3 * bMod);
            }
        }

        return (int) (getAttribute(EPlayerAttribute.OI_INCOME) + oi);

    }

    public List<Unit> getUnits(){
        List<Unit> units = new ArrayList<>();
        for(Army a:armies){
            units.addAll(a.getUnits());
        }
        return units;
    }

    public int getRevanchism(){
        return (int) Math.min(3,Math.round(getAttribute(EPlayerAttribute.REVANCHISM)));
    }

    public double getWarBuildingCost(){
        return Math.max( 0.1, Tools.round(getAttribute(EPlayerAttribute.BUILDING_COST) - (1 - getAttribute(EPlayerAttribute.WAR_BUILDING_COST))));
    }
    public double getScienceBuildingCost(){
        return Math.max(0.1, Tools.round(getAttribute(EPlayerAttribute.BUILDING_COST) - (1 - getAttribute(EPlayerAttribute.SCIENCE_BUILDING_COST))));
    }

    public int getTribute(){
        return (int) Math.round(getAttribute(EPlayerAttribute.TRIBUTE)*getAttribute(EPlayerAttribute.TRIBUTE_MOD));
    }

    public double getMorale(){
        return Tools.round(getAttribute(EPlayerAttribute.MORALE_MOD) + (getAttribute(EPlayerAttribute.TRADITION)*0.005) + (getRevanchism() * getAttribute(EPlayerAttribute.REVANCHISM_MOD) * 0.1));
    }

    public int getPeople(){
        int people = 0;
        for(Town t: towns){
            if(t.isCore()&&t.isStatus()) people+=t.getPeople();
        }
        return people;
    }

    public boolean isWar(){
        return getAttribute(EPlayerAttribute.WAR_STATUS)==1;
    }

    public boolean isLevies(){
        return getAttribute(EPlayerAttribute.LEVIES_STATUS)==1;
    }



    public double getCoreCost(){
        return Math.max(0.1, Tools.round(getAttribute(EPlayerAttribute.CORE_CREATION_COST) - (getAttribute(EPlayerAttribute.ADMIN_EFFICIENCY)*0.05) ));
    }

    public int getManpowerLimit(){
        return (int) Math.floor(getPeople()*getAttribute(EPlayerAttribute.MANPOWER_LIMIT_MOD));
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


    public int getTaxIncome(){
        double tax = getAttribute(EPlayerAttribute.TAX_INCOME) + getPeople();
        return (int) Math.floor(tax * getAttribute(EPlayerAttribute.TAX_MOD));
    }

    public int getMaxPolit(){
        double max = getAttribute(EPlayerAttribute.POLIT_MAX);
        return (int) Math.floor(max*getAttribute(EPlayerAttribute.POLIT_MAX_MOD));
    }

    public int getTradeIncome(){
        double income = 0;
        for (Town t : towns) {
            income += t.getTradeIncome();
        }
        return (int) Math.floor(income *  getAttribute(EPlayerAttribute.TRADE_MOD));
    }

    public int getProdIncome(){
        double income = 0;
        for (Town t : towns) {
            income += t.getProdIncome();
        }
        return (int) Math.floor(income * getAttribute(EPlayerAttribute.PROD_MOD));
    }

    public int getIncome(){
        return getProdIncome() + getTradeIncome() + getTaxIncome() + (int) getAttribute(EPlayerAttribute.INCOME);
    }

    public int getExpense(){
        return getArmyExpense()+getDebtExpense() + getTribute() + (int) getAttribute(EPlayerAttribute.EXPENSE);
    }

    public HashMap<String, List<Unit>> getTroops(){
        HashMap<String, List<Unit>> res = new HashMap<>();
        List<Unit> inf = new ArrayList<>();
        List<Unit> cav = new ArrayList<>();
        List<Unit> art = new ArrayList<>();
        for (Army a:armies){
            if(a.getOwnerId().equals(uniqueId)){
                Set<Unit> units = a.getUnits();
                for(Unit u:units){
                    switch (u.getType()){
                        case "inf"->{
                            inf.add(u);
                            //expense += Tools.round(u.getLvl()*getArmyStats().getInfCost());
                        }
                        case "cav" ->{
                            cav.add(u);
                            //expense += Tools.round(u.getLvl()*2.5*getArmyStats().getCavCost());
                        }
                        case "art" ->{
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
        List<Unit> inf = getTroops().get("inf");
        double expense = 0.0;
        for(Unit u:inf){
            expense += Tools.round(u.getLvl() * getAttribute(EPlayerAttribute.INF_COST));
        }
        return expense;
    }
    public double getCavExpense(){
        List<Unit> inf = getTroops().get("cav");
        double expense = 0.0;
        for(Unit u:inf){
            expense += Tools.round(u.getLvl() * 2.5 * getAttribute(EPlayerAttribute.CAV_COST));
        }
        return expense;
    }
    public double getArtExpense(){
        List<Unit> inf = getTroops().get("art");
        double expense = 0.0;
        for(Unit u:inf){
            expense += Tools.round(u.getLvl()*3 * getAttribute(EPlayerAttribute.ART_COST));
        }
        return expense;
    }

    public int getArmyExpense(){
        double expense=0;

        for(Unit u:getUnits()){
            switch (u.getType()){
                case "inf"->{
                    expense += Tools.round(u.getLvl()*getAttribute(EPlayerAttribute.INF_COST));
                }
                case "cav" ->{
                    expense += Tools.round(u.getLvl()*2.5* getAttribute(EPlayerAttribute.CAV_COST));
                }
                case "art" ->{
                    expense += Tools.round(u.getLvl()*3* getAttribute(EPlayerAttribute.ART_COST));
                }
            }
        }
        return (int) Math.ceil(expense*getAttribute(EPlayerAttribute.ARMY_EXPENSE_MOD));
    }

    public int getDebtLvl(){
        int oneDebt = getOneDebt();
        return switch (oneDebt) {
            case 10 -> 1;
            case 25 -> 2;
            default -> 0;
        };
    }

    public int getOneDebt(){
        int oneDebt = 5;
        if(getTech(EPlayerTech.BANK_BASE)){
            for(Building b:getBuildings()){
                if (b.getType().equals("bank")) {
                    oneDebt = 10;
                    if(getTech(EPlayerTech.BANK_UP)) oneDebt = 25;
                    break;
                }
            }

        }
        return oneDebt;
    }

    public int[] getDebts(){
        int[] debts = new int[3];
        String path = "debt."+displayName+".lvl";
        for (int i = 0; i < 3; i++) {
            debts[i] = CustomConfig.get().getInt(path+i);
        }
        return debts;
    }

    public int getDebt(){
        String path = "debt."+displayName+".lvl";
        int debt = CustomConfig.get().getInt(path+"0")*6;
        debt += CustomConfig.get().getInt(path+"1")*11;
        debt += CustomConfig.get().getInt(path+"2")*26;
        return debt;
    }

    public int getDebtExpense(){
        String path = "debt."+displayName+".lvl";
        int debt = CustomConfig.get().getInt(path+"0");
        debt += CustomConfig.get().getInt(path+"1");
        debt += CustomConfig.get().getInt(path+"2");
        return debt;
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


//
//    @Data
//    public class Country{
//
//        public Country(UUID uuid){
//            ownerId = uuid;
//        }
//
//
//
//        private final UUID ownerId;
//        private String displayName;
//        private int oiBalance;
//        private int oiIncome;
//        private int oiSpent;
//
//        private int oiIncomeMod;
//        private double techCost;
//        private double oiFromBuilding;
//
//        private int politBalance;
//        private int politIncome;
//        private double politIncomeMod;
//        private int politMax;
//        private double politMaxMod;
//        private int income;
//        private int prodIncome;
//        private double prodMod;
//        private double goodsMod;
//        private int tradeIncome;
//        private double tradeMod;
//
//        private double tradeGoodsMod;
//        private double frigateMod;
//
//        private int taxIncome;
//        private double taxMod;
//        private int expense;
//        private int corruption;
//        private int inflation;
//        private int inflationReduce;
//        private int warSup;
//        private int warStatus;
//        private int treasury;
//
//        private double coreCreationCost;
//        private int revanchism;
//        private double revanchismMod;
//        private double expandInfrCost;
//        private int buildSites;
//
//        private double buildingCost;
//        private double scienceBuildingCost;
//        private double warBuildingCost;
//
//
//
//        public int getPolitMax(){
//            return (int) Math.round(politMax*politMaxMod);
//        }
//        public int getPolitIncome(){
//            return (int) Math.round(politIncome*politIncomeMod);
//        }
//
//        public void removeTreasury(int amount){
//            updateTreasury(treasury-amount);
//        }
//
//        // Производство и товары
//        public void addProdMod(double delta) { updateProdMod(this.prodMod + delta); }
//        public void addGoodsMod(double delta) { updateGoodsMod(this.goodsMod + delta); }
//        public void addProdIncome(int delta) { updateProdIncome(this.prodIncome + delta); }
//
//        // Очки влияния (OI)
//        public void addOiBalance(int delta) { updateOiBalance(this.oiBalance + delta); }
//        public void addOiIncome(int delta) { updateOiIncome(this.oiIncome + delta); }
//        public void addOiSpent(int delta) { updateOiSpent(this.oiSpent + delta); }
//
//        // Политическая власть
//        public void addPolitBalance(int delta) { updatePolitBalance(this.politBalance + delta); }
//        public void addPolitIncome(int delta) { updatePolitIncome(this.politIncome + delta); }
//        public void addPolitIncomeMod(double delta) { updatePolitIncomeMod(this.politIncomeMod + delta); }
//        public void addPolitMax(int delta) { updatePolitMax(this.politMax + delta); }
//        public void addPolitMaxMod(double delta) { updatePolitMaxMod(this.politMaxMod + delta); }
//
//        // Финансы (Доходы)
//        public void addIncome(int delta) { updateIncome(this.income + delta); }
//        public void addTradeIncome(int delta) { updateTradeIncome(this.tradeIncome + delta); }
//        public void addTradeMod(double delta) { updateTradeMod(this.tradeMod + delta); }
//        public void addTaxIncome(int delta) { updateTaxIncome(this.taxIncome + delta); }
//        public void addTaxMod(double delta) { updateTaxMod(this.taxMod + delta); }
//
//        // Расходы и показатели экономики
//        public void addExpense(int delta) { updateExpense(this.expense + delta); }
//        public void addCorruption(int delta) { updateCorruption(this.corruption + delta); }
//        public void addInflation(int delta) { updateInflation(this.inflation + delta); }
//        public void addTreasury(int delta) { updateTreasury(this.treasury + delta); }
//
//        // Военные показатели
//        public void addWarSup(int delta) { updateWarSup(this.warSup + delta); }
//        public void addWarStatus(int delta) { updateWarStatus(this.warStatus + delta); }
//
//        public void updateProdMod(double newValue){
//            this.prodMod = newValue;
//            updateC(this);
//        }
//        public void updateGoodsMod(double newValue){
//            this.goodsMod = newValue;
//            updateC(this);
//        }
//
//        public void updateProdIncome(int newValue){
//            this.prodIncome = newValue;
//            updateC(this);
//        }
//
//        public void updateDisplayName(String displayName) {
//            this.displayName = displayName;
//            updateC(this);
//        }
//
//        public void updateOiBalance(int oiBalance) {
//            this.oiBalance = oiBalance;
//            updateC(this);
//        }
//
//        public void updateOiIncome(int oiIncome) {
//            this.oiIncome = oiIncome;
//            updateC(this);
//        }
//
//        public void updateOiSpent(int oiSpent) {
//            this.oiSpent = oiSpent;
//            updateC(this);
//        }
//
//        public void updatePolitBalance(int politBalance) {
//            this.politBalance = politBalance;
//            updateC(this);
//        }
//
//        public void updatePolitIncome(int politIncome) {
//            this.politIncome = politIncome;
//            updateC(this);
//        }
//
//        public void updatePolitIncomeMod(double politIncomeMod) {
//            this.politIncomeMod = politIncomeMod;
//            updateC(this);
//        }
//
//        public void updatePolitMax(int politMax) {
//            this.politMax = politMax;
//            updateC(this);
//        }
//
//        public void updatePolitMaxMod(double politMaxMod) {
//            this.politMaxMod = politMaxMod;
//            updateC(this);
//        }
//
//        public void updateIncome(int income) {
//            this.income = income;
//            updateC(this);
//        }
//
//        public void updateTradeIncome(int tradeIncome) {
//            this.tradeIncome = tradeIncome;
//            updateC(this);
//        }
//
//        public void updateTradeMod(double tradeMod) {
//            this.tradeMod = tradeMod;
//            updateC(this);
//        }
//
//        public void updateTaxIncome(int taxIncome) {
//            this.taxIncome = taxIncome;
//            updateC(this);
//        }
//
//        public void updateTaxMod(double taxMod) {
//            this.taxMod = taxMod;
//            updateC(this);
//        }
//
//        public void updateExpense(int expense) {
//            this.expense = expense;
//            updateC(this);
//        }
//
//        public void updateCorruption(int corruption) {
//            this.corruption = corruption;
//            updateC(this);
//        }
//
//        public void updateInflation(int inflation) {
//            this.inflation = inflation;
//            updateC(this);
//        }
//
//        public void updateWarSup(int warSup) {
//            this.warSup = warSup;
//            updateC(this);
//        }
//
//        public void updateWarStatus(int warStatus) {
//            this.warStatus = warStatus;
//            updateC(this);
//        }
//
//        public void updateTreasury(int treasury) {
//            this.treasury = treasury;
//            updateC(this);
//        }
//
//        public int getDebtSize(){
//            if(db.getPlayerTech(ownerId,2)==1) return 25;
//            if(db.getPlayerTech(ownerId,1)==1) return 10;
//            return 5;
//        }
//
//        public int getArmyExpense(){
//            db.getArmies();
//            return 0;
//        }
//    }
//
//
//    @Data
//    public class ArmyStats{
//
//        public ArmyStats(UUID uuid){
//            ownerId = uuid;
//        }
//
//        private final UUID ownerId;
//
//        private double expenseMod;
//
//        private double limitMod;
//        private int manpower;
//        private double manpowerLimitMod;
//        private int manpowerIncMod;
//
//        private double disciple;
//        private double tac;
//        private double morale;
//
//        private double fireDamage;
//        private double fireResist;
//        private double shockDamage;
//        private double shockResist;
//        private double moraleDamage;
//        private double moraleResist;
//
//        private double infCost;
//        private double infCombatAbility;
//
//        private double cavCost;
//        private double cavCombatAbility;
//        private double cavRatio;
//
//        private double artCost;
//        private double artCombatAbility;
//
//        public int getMaxManpower(){
//            Town[] towns = db.getPlayerTowns(ownerId);
//            int people = 0;
//            for(Town t: towns){
//                people += t.getPeople();
//            }
//            return (int) Math.floor(people*manpowerLimitMod);
//        }
//
//        public int getLimit(){
//            Town[] towns = db.getPlayerTowns(ownerId);
//            int people = 0;
//            for(Town t: towns){
//                people += t.getPeople();
//            }
//            return (int) Math.floor(people*limitMod);
//        }
//
//        public double getTactic(){
//            return Tools.round(tac*disciple);
//        }
//
//
//        // Модификаторы содержания и лимитов
//        public void addExpenseMod(double delta) { updateExpenseMod(this.expenseMod + delta); }
//        public void addLimitMod(double delta) { updateLimitMod(this.limitMod + delta); }
//
//        // Людские ресурсы (Manpower)
//        public void addManpower(int delta) { updateManpower(this.manpower + delta); }
//        public void addManpowerLimitMod(double delta) { updateManpowerLimitMod(this.manpowerLimitMod + delta); }
//        public void addManpowerIncMod(int delta) { updateManpowerIncMod(this.manpowerIncMod + delta); }
//
//        // Качество армии
//        public void addDisciple(double delta) { updateDisciple(this.disciple + delta); }
//        public void addTac(double delta) { updateTac(this.tac + delta); }
//
//        // Боевые показатели (Мораль)
//        public void addMorale(double delta) { updateMorale(this.morale + delta); }
//        public void addMoraleDamage(double delta) { updateMoraleDamage(this.moraleDamage + delta); }
//        public void addMoraleResist(double delta) { updateMoraleResist(this.moraleResist + delta); }
//
//        // Урон и сопротивление (Огонь и Натиск)
//        public void addFireDamage(double delta) { updateFireDamage(this.fireDamage + delta); }
//        public void addFireResist(double delta) { updateFireResist(this.fireResist + delta); }
//        public void addShockDamage(double delta) { updateShockDamage(this.shockDamage + delta); }
//        public void addShockResist(double delta) { updateShockResist(this.shockResist + delta); }
//
//        // Пехота (Стоимость и боеспособность)
//        public void addInfCost(double delta) { updateInfCost(this.infCost + delta); }
//        public void addInfCombatAbility(double delta) { updateInfCombatAbility(this.infCombatAbility + delta); }
//
//        // Кавалерия (Стоимость, боеспособность и соотношение)
//        public void addCavCost(double delta) { updateCavCost(this.cavCost + delta); }
//        public void addCavCombatAbility(double delta) { updateCavCombatAbility(this.cavCombatAbility + delta); }
//        public void addCavRatio(double delta) { updateCavRatio(this.cavRatio + delta); }
//
//        // Артиллерия (Стоимость и боеспособность)
//        public void addArtCost(double delta) { updateArtCost(this.artCost + delta); }
//        public void addArtCombatAbility(double delta) { updateArtCombatAbility(this.artCombatAbility + delta); }
//
//        public void updateExpenseMod(double expenseMod){
//            this.expenseMod = expenseMod;
//            updateA(this);
//        }
//
//        public void updateLimitMod(double limitMod) {
//            this.limitMod = limitMod;
//            updateA(this);
//        }
//
//        public void updateManpower(int manpower) {
//            this.manpower = manpower;
//            updateA(this);
//        }
//
//        public void updateManpowerLimitMod(double manpowerLimitMod) {
//            this.manpowerLimitMod = manpowerLimitMod;
//            updateA(this);
//        }
//
//        public void updateManpowerIncMod(int manpowerIncMod) {
//            this.manpowerIncMod = manpowerIncMod;
//            updateA(this);
//        }
//
//        public void updateDisciple(double disciple) {
//            this.disciple = disciple;
//            updateA(this);
//        }
//
//        public void updateTac(double tactic) {
//            this.tac = tactic;
//            updateA(this);
//        }
//
//
//        public void updateMorale(double morale) {
//            this.morale = morale;
//            updateA(this);
//        }
//
//        public void updateFireDamage(double fireDamage) {
//            this.fireDamage = fireDamage;
//            updateA(this);
//        }
//
//        public void updateFireResist(double fireResist) {
//            this.fireResist = fireResist;
//            updateA(this);
//        }
//
//        public void updateShockDamage(double shockDamage) {
//            this.shockDamage = shockDamage;
//            updateA(this);
//        }
//
//        public void updateShockResist(double shockResist) {
//            this.shockResist = shockResist;
//            updateA(this);
//        }
//
//        public void updateMoraleDamage(double moraleDamage) {
//            this.moraleDamage = moraleDamage;
//            updateA(this);
//        }
//
//        public void updateMoraleResist(double moraleResist) {
//            this.moraleResist = moraleResist;
//            updateA(this);
//        }
//
//
//        public void updateInfCost(double infCost) {
//            this.infCost = infCost;
//            updateA(this);
//        }
//
//        public void updateInfCombatAbility(double infCombatAbility) {
//            this.infCombatAbility = infCombatAbility;
//            updateA(this);
//        }
//
//
//        public void updateCavCost(double cavCost) {
//            this.cavCost = cavCost;
//            updateA(this);
//        }
//
//        public void updateCavCombatAbility(double cavCombatAbility) {
//            this.cavCombatAbility = cavCombatAbility;
//            updateA(this);
//        }
//
//
//        public void updateCavRatio(double cavRatio) {
//            this.cavRatio = cavRatio;
//            updateA(this);
//        }
//
//
//        public void updateArtCost(double artCost) {
//            this.artCost = artCost;
//            updateA(this);
//        }
//
//        public void updateArtCombatAbility(double artCombatAbility) {
//            this.artCombatAbility = artCombatAbility;
//            updateA(this);
//        }
//
//
//
//
//
//
//        public int getExpense(){
//            return 0;
//        }
//    }
}
