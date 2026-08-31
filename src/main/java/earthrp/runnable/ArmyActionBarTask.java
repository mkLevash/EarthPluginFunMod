package earthrp.runnable;

import earthrp.Earth;
import earthrp.customObjects.Army;
import earthrp.customObjects.EPlayer;
import earthrp.tools.Tools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArmyActionBarTask extends BukkitRunnable {

    // Если тебе нужен доступ к главному классу или менеджеру,
    // можно передать его через конструктор


    public ArmyActionBarTask() {

    }

    @Override
    public void run() {
        // Проходимся по всем игрокам онлайн
        for (Player player : Bukkit.getOnlinePlayers()) {
            EPlayer ePlayer = Earth.getInstance().getDatabase().getPlayer(player);
            // Проверяем, есть ли у игрока армия
            if (!ePlayer.getArmiesInHand().isEmpty()) {
                showArmyStats(ePlayer,player);
            }
        }
    }


    // Логика отображения статов
    private void showArmyStats(EPlayer ePlayer, Player player) {
        // Допустим, мы получаем эти данные из твоего плагина
        int troops = 0;
        int totalSize = 0;
        double totalMoralePool = 0.0;
        double totalMaxMoralePool = 0.0;
        double morale;
        double maxMorale;
        for(var army : ePlayer.getArmiesInHand()){




            troops += army.getTroops();
            totalSize += army.getArmySize();
            totalMoralePool += (army.getArmySize() * army.getMorale());
            totalMaxMoralePool += (army.getArmySize() * army.getMaxMorale());
            // Умножаем размер на мораль


        }

        if (totalSize == 0) {
            morale = 0.0;
            maxMorale = 0.0;
        }else{
            morale = Tools.round(totalMoralePool / totalSize);
            maxMorale = Tools.round(totalMaxMoralePool / totalSize);
        }





        // Создаем текст с помощью Adventure API
        Component statsMessage = Component.text("⚔ Армия: ").color(NamedTextColor.GOLD)
                .append(Component.text(troops + " воинов ").color(NamedTextColor.WHITE))
                .append(Tools.deserialize("| ᠩ Мораль: <dark_green>" + morale + "<white>/<dark_green>" + maxMorale));

        // Отправляем Action Bar игроку
        player.sendActionBar(statsMessage);
    }
}