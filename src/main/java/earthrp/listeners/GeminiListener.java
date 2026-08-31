package earthrp.listeners;

import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.*;
import earthrp.database.ServerDatabase;
import earthrp.events.NewDayEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;


public class GeminiListener implements Listener {




    @EventHandler
    public void onNewDay(NewDayEvent e)  {
        ServerDatabase db = Earth.getInstance().getDatabase();

        Set<EPlayer> players = db.getPlayers();
        boolean status = false; //db.getStatusMora();

        if (status) {

            StringBuilder otherTownPromt = new StringBuilder();
            StringBuilder otherPlayerPromt = new StringBuilder();

            for(Town t:db.getTowns()){
                otherTownPromt.append(t.getName() + ":\n"+
                        "type - " + t.getType() + "\n" +
                        "people - " + t.getPeasant() + "\n" +
                        "location - X=" + t.getLocation().getBlockX() + " Z=" + t.getLocation().getBlockZ() + "\n" +
                        "owner - " + t.getOwnerName() + "\n" );
            }
            for(EPlayer p:players) {
                otherPlayerPromt.append(p.getCountryName() + ":\n" +
                        "isBot - " + p.getData().isBot + "\n" +
                        "treasury - " + p.getAttribute(EPlayerAttribute.TREASURY) + "\n");
                int n = 0;
                for (Army army : p.getArmies()) {
                    n++;
                    int inf = 0;
                    int cav = 0;
                    for (ArmyUnit unit : army.getUnits()) {
                        if (unit.getTech().equals("inf")) {
                            inf++;
                        } else {
                            cav++;
                        }
                    }
                    otherPlayerPromt.append("army" + n + ":\n" +
                            "Infantry: " + inf + "\n" +
                            "Cavalry: " + cav + "\n" +
                            "Morale: " + army.getMorale() + "\n"

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
                    for(ArmyUnit unit:army.getUnits()){
                        if(unit.getTech().equals("inf")){
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

                for (Town t : p.getTowns()) {
                    int maxPeople = t.isCapital() ? 15 : 13;

                    if (p.getData().isBot){
                        townPromt.append(t.getName() + ":\n"+
                                "type - " + t.getType() + "\n" +
                                "people - " + t.getPeasant() + "\n" +
                                "location - X=" + t.getLocation().getBlockX() + " Z=" + t.getLocation().getBlockZ() + "\n" +
                                "maxPeople - " + maxPeople + "\n");
                    }
                }

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
                            "Your max manpower: " + p.getManpowerLimit() +
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
        }

    }
}

