package earthrp.placeholders;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Town;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;



public class MoraExpansion extends PlaceholderExpansion {

    private static @NotNull String[] getColor(int amount, int modifier, int negative) {
        String mod;
        String sum;
        String prefix_p = "+";
        String prefix_n = "";
        String postfix = "&f%)";
        int amount_n = amount;
        int modifier_n = modifier;
        String[] rs;
        rs = new String[2];
        if (negative == 1){
            amount_n = -amount;
        } else if (negative == 2) {
            modifier_n = -modifier;
            prefix_p = "";
            prefix_n = "+";

        } else if (negative == 3) {
            amount_n = -amount;
            modifier_n = -modifier;
            prefix_p = "";
            prefix_n = "+";

        } else if (negative==4) {
            postfix = "&f)";
        }

        if (amount > 0){
            sum = "&a" + amount_n + "&f";
        }else if (amount < 0){
            sum = "&c" + amount_n + "&f";
        }else{
            sum = "&f" + amount_n;
        }
        rs[0] = sum;
        if (modifier >0){
            mod = "(&2" + prefix_p + modifier_n + postfix;
        }else if(modifier <0){
            mod = "(&c" + prefix_n + modifier_n + postfix;
        }else {
            mod = "";
        }
        rs[1] = mod;
        return rs;
    }
    public static String cutString(String str, int index) {
        String s = new StringBuilder(str).reverse().toString();
        if (s.length() < 2){
            s =  s + "00";
        }
        else if (s.length() < 3){
            s = s + "0";
        }
        return new StringBuilder(s.substring(0,index)).reverse().toString();
    }
    private final Earth earth;
    public MoraExpansion(Earth plugin) {
        this.earth = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mora";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", earth.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return earth.getDescription().getVersion();
    }


    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null){
            return "";
        }
        String value = "";
        EPlayer p = earth.getServerDatabase().getPlayer(player.getUniqueId());
        if (params.equalsIgnoreCase("treasury")){
            long treasury = (long) p.getAttribute(EPlayerAttribute.TREASURY);
            String sTreasury = String.valueOf(treasury);
            if(treasury>999) sTreasury = treasury / 1000 + "K";
            if(treasury>999999) sTreasury = treasury / 1000000 + "M";
            value = "Ѡ&6" + sTreasury;

            String balance = String.valueOf(Tools.getBalance(p));
            if(Math.abs(Tools.getBalance(p)) > 999) balance = Tools.CustomRound ( ((double) Tools.getBalance(p)) / 1000.0,1) + "K";
            if(Math.abs(Tools.getBalance(p)) > 999999) balance = Tools.getBalance(p) / 1000000 + "M";

            if(Tools.getBalance(p) > 0){
                value += " &fव" + "&a" + balance;
            }
            else if(Tools.getBalance(p) < 0){
                value += " &fश" + "&c" + balance;
            }
            else {
                value += " &fष" + balance;
            }

            //if (c.getInflation() != 0) value += " &fi&e" + c.getInflation() + "&f%";
        }
//        if (params.equalsIgnoreCase("income")){
//            UUID uuid = player.getUniqueId();
//            int income = (int) Math.floor((c.getIncome()+c.getTaxIncome()) * (c.getTaxMod()+1));
//            int incomeMod = (int) c.getTaxMod()*100;
//            int trade = (int) Math.floor(c.getTradeIncome()*(1+c.getTradeMod()));
//            int tradeMod = (int)c.getTradeMod()*100;
//            int total = income + trade;
//            String[] rs = getColor(total,incomeMod+tradeMod,0);
//
//            value = rs[0]+rs[1];
//        }


        if (params.equalsIgnoreCase("tax_income")){
//                try {
//                    UUID uuid = player.getUniqueId();
//                    int tax_income = earth.getServerDatabase().getPlayerTaxIncome(uuid);
//                    int tax_mod = earth.getServerDatabase().getPlayerTaxModifier(uuid);
//                    int tax = (int) Math.floor(tax_income * (tax_mod * 0.01 + 1));
//                    String[] rs = getColor(tax,tax_mod,0);
//
//                    return rs[0]+rs[1];
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
            return "";
        }
        if (params.equalsIgnoreCase("expense")){
//                try {
//                    UUID uuid = player.getUniqueId();
//                    int expense = earth.getServerDatabase().getPlayerExpense(uuid);
//
//                    return getColor(-expense,0,1)[0];
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
            return "";
        }

        if (params.equalsIgnoreCase("army_expense")){
//                try {
//                    UUID uuid = player.getUniqueId();
//                    int army_expense = earth.getServerDatabase().getPlayerArmyExpense(uuid);
//                    int army_expense_mod = earth.getServerDatabase().getPlayerArmyExpenseModifier(uuid);
//                    int expense = (int) Math.ceil(army_expense * (army_expense_mod * 0.01 + 1));
//                    String[] rs = getColor(-expense,-army_expense_mod,3);
//
//                    return rs[0]+rs[1];
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
            return "";
        }
        if (params.equalsIgnoreCase("balance")){
//                try {
//                    UUID uuid = player.getUniqueId();
//                    int income = earth.getServerDatabase().getPlayerIncome(uuid);
//                    int tax_income = earth.getServerDatabase().getPlayerTaxIncome(uuid);
//                    int tax_modifier = earth.getServerDatabase().getPlayerTaxModifier(uuid);
//                    int inflation = earth.getServerDatabase().getPlayerInflation(uuid);
//                    int tax = (int) Math.floor(tax_income * (tax_modifier * 0.01 + 1));
//                    income = (int) Math.floor( (income + tax) * (1 - inflation*0.01) );
//                    int expense = earth.getServerDatabase().getPlayerExpense(uuid);
//                    int army_expense = earth.getServerDatabase().getPlayerArmyExpense(uuid);
//                    int army_expense_mod = earth.getServerDatabase().getPlayerArmyExpenseModifier(uuid);
//                    int corruption = earth.getServerDatabase().getPlayerCorruption(uuid);
//                    army_expense = (int) Math.ceil(army_expense * (army_expense_mod * 0.01 + 1));
//                    int balance = (int) Math.floor((income - (expense + army_expense)) * (1 - corruption * 0.2));
//
//                    if(balance > 0){
//                        return "&a" + balance + "&fव";
//                    }
//                    else if(balance < 0){
//                        return "&c" + balance + "&fश";
//                    }
//                    else {
//                        return balance + "ष";
//                    }
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
            return "";
        }
        if (params.equalsIgnoreCase("main_balance")){
            int balance = Tools.getBalance(p);

            if(balance > 0){
                return "&fव" + "&a" + balance;
            }
            else if(balance < 0){
                return "&fश" + "&c" + balance;
            }
            else {
                return "ष" + balance;
            }
        }
        if (params.equalsIgnoreCase("oi_balance")){
//            String oi = String.valueOf(c.getOiBalance());
//            if(c.getTreasury()>999) oi = c.getOiBalance() / 1000 + "K";
//            value = "ૹ&b" + oi;
        }
        if (params.equalsIgnoreCase("oi_income")){
//                try {
//                    int amount = earth.getServerDatabase().getPlayerOiIncome(player.getUniqueId());
//
//                    return String.valueOf(amount);
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
            return "";
        }
        if (params.equalsIgnoreCase("oi_spent")){
//                try {
//                    int amount = earth.getServerDatabase().getPlayerOiSpent(player.getUniqueId());
//
//                    return String.valueOf(amount);
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
            return "";
        }
        if (params.equalsIgnoreCase("pp_bal")){
            int oiBalance = (int) p.getAttribute(EPlayerAttribute.OI_BALANCE);
            String oi = String.valueOf(oiBalance);
            if(oiBalance>999) oi = oiBalance / 1000 + "K";
            value = "ૹ&b" + oi + "&fस" + (int) p.getAttribute(EPlayerAttribute.WAR_SUPPORT) + "&fऴ" + (int) p.getAttribute(EPlayerAttribute.MANPOWER);
            value += "£" + (int) p.getAttribute(EPlayerAttribute.POLIT_BALANCE);

//            int corr = (int) p.getAttribute(EPlayerAttribute.CORRUPTION);
//            if (corr > 0) value += "&fখ&c" + corr;

        }
        if (params.equalsIgnoreCase("pp_inc")){
            return "";
        }
        if (params.equalsIgnoreCase("pp_max")){
            //value = String.valueOf(c.getPolitMax());
        }
        if (params.equalsIgnoreCase("war_support")){

            //value = String.valueOf(c.getWarSup());
        }
        if (params.equalsIgnoreCase("manpower")){
//
//            value = "ऴ" + a.getManpower() + "&f/" + a.getMaxManpower() + "ह" + a.getLimit();

        }
        if (params.equalsIgnoreCase("army_limit")){
//            int limit = (int) Math.floor(c.getTaxIncome() * (a.getLimitMod() + 1));
//            String modifier = getColor(limit,(int)a.getLimitMod()*100,0)[1];
//
//            return "&fह" + limit + modifier;
        }
        if (params.equalsIgnoreCase("war_status")){

            if (p.getAttribute(EPlayerAttribute.WAR_STATUS) == 0){
                return "&3In peace";
            }else{
                return "&4In war";
            }
        }
        if (params.equalsIgnoreCase("inflation")){
            //return getColor(-c.getInflation(),0,1)[0] + "%";
        }
        if (params.equalsIgnoreCase("corruption")){
            //return getColor(-c.getCorruption(),0,1)[0];
        }
        if (params.equalsIgnoreCase("mora")){
            boolean mora = earth.getServerDatabase().getStatusMora();

            if (mora){
                return "&aON ";
            }else{
                return "&cOFF ";
            }
        }
        if (params.equalsIgnoreCase("day")){
            String day = String.valueOf(earth.getServerDatabase().getStatusDay());
            long time = Bukkit.getServer().getWorld("world").getTime();
            int time_hour = (int) time /1000;
            String hour;
            String min;
            if (time_hour + 6 >= 24) {
                hour = "0" + (time_hour-18);
            }
            else if (time_hour < 4){
                hour = "0" + (time_hour + 6);
            }
            else {
                hour = String.valueOf(time_hour + 6);
            }
            int time_min = (int) Math.round( Integer.parseInt(cutString(String.valueOf(time),3))*0.06);

            if (time_min<10){
                min = "0" + time_min;
            }
            else if (time_min < 60) {
                min = String.valueOf(time_min);
            }
            else {
                min = "00";
            }

            return day + ", " + hour + ":" + min;
        }
        if(params.equalsIgnoreCase("town")){
            Location loc = player.getLocation();
            int x = loc.getChunk().getX();
            int z = loc.getChunk().getZ();
            String world = loc.getWorld().getName();

            Town town = earth.getServerDatabase().getTownAtChunk(x,z);
            if (town==null){
                earth.getServerDatabase().markChunk(x,z);
                return " ";
            }else if(town.getOwnerId().equals(player.getUniqueId())) {
                return "[&3"+earth.getServerDatabase().getPlayer(town.getOwnerId()).getCountryName() +"&f]&2"+town.getName();
            }else{
                return "[&b"+earth.getServerDatabase().getPlayer(town.getOwnerId()).getCountryName() +"&f]&5"+town.getName();
            }
        }
        return value;

    }


}
