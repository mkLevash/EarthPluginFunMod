package earthrp.listeners;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Unit;
import earthrp.database.ServerDatabase;
import earthrp.events.NewDayEvent;
import org.bukkit.Bukkit;
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
            Bukkit.broadcastMessage("New day!");
            for(EPlayer p:players){
                int balance = Tools.getBalance(p);

                p.addAttribute(EPlayerAttribute.TREASURY, balance);


                p.addAttribute(EPlayerAttribute.OI_BALANCE,p.getOiIncome());

                int pp_balance = (int) p.getAttribute(EPlayerAttribute.POLIT_BALANCE);
                int politIncome = p.getPolitIncome();
                int politMax = p.getMaxPolit();
                p.setAttribute(EPlayerAttribute.POLIT_BALANCE, Math.min(politMax, pp_balance + politIncome));

                p.addAttribute(EPlayerAttribute.INFLATION,p.getAttribute(EPlayerAttribute.INFLATION_REDUCE));

                int manpowerIncrease = 3;
                if (p.getAttribute(EPlayerAttribute.WAR_STATUS) == 1){
                    manpowerIncrease = (int) (p.getAttribute(EPlayerAttribute.WAR_SUPPORT));
                }
                double newManpower = Math.round((p.getAttribute(EPlayerAttribute.MANPOWER)+manpowerIncrease)*1000.0);
                for(Unit u:p.getUnits()){
                    if(u.getMorale()!=u.getMaxMorale()){
                        double newMorale = Tools.round(u.getMorale() + u.getMaxMorale()*p.getAttribute(EPlayerAttribute.MORALE_REDUCE));
                        u.setMorale(Math.min(u.getMaxMorale(),newMorale));
                    }

                    if(u.getHp()!=1000 && newManpower>=u.getDamageTaken()){
                        newManpower -= u.getDamageTaken();
                        u.setHp(1000);
                    }

                }
                p.setAttribute(EPlayerAttribute.MANPOWER, Math.min(p.getManpowerLimit(), Math.floor(newManpower/1000.0)));
            }
            int day = db.getStatusDay();
            db.updateStatusDay(++day);
        }




    }

}
