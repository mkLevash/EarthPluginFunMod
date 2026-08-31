package earthrp.customObjects;

import earthrp.customEnums.EPlayerAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class PlayerData {


    @Getter
    private final Set<PlayerModifier> modifiers = new LinkedHashSet<>();

    // Метод для удобного добавления модификатора
    public void addModifier(PlayerModifier modifier) {
        // Если списка еще нет, создаем его, иначе добавляем в существующий
        modifiers.add(modifier);
    }

    // Метод для удаления модификатора по его ID (например, когда кончился бафф)
    public void removeModifier(PlayerModifier modifier) {
        modifiers.remove(modifier);
    }

    public PlayerModifier getModifier(String modifierId){
        for(PlayerModifier modifier:modifiers){
            if(modifier.getId().equals(modifierId)){
                return modifier;
            }
        }
        return null;
    }

    public Set<UUID> armiesInHand = new HashSet<>();

    @Setter
    public boolean isBot = false;

    @Getter
    private final Map<String, Integer> rgb = Map.of("red",255,"green",255,"blue",255);



    @Getter
    @Setter
    private long location;

    @Getter
    @Setter
    private long locationTime;


    @Getter
    @Setter
    private boolean levies = false;

    @Getter
    @Setter
    private boolean war = false;

    @Getter
    @Setter
    private boolean bankruptcy = false;

    @Getter
    @Setter
    private boolean imperialismWar = false;

    @Getter
    @Setter
    private boolean revanchism0 = false;

    @Getter
    @Setter
    private boolean revanchism1 = false;

    @Getter
    @Setter
    private boolean revanchism2 = false;

    @Getter
    @Setter
    private boolean revanchism3 = false;

    @Getter
    @Setter
    private boolean revanchism4 = false;

    @Getter
    @Setter
    private int barbarians = 1;





    @Getter
    private final Set<UUID> enemies = new HashSet<>();

    @Getter
    private final Set<UUID> truceBroken = new HashSet<>();

    @Getter
    private final Set<UUID> ally =  new HashSet<>();

    @Getter
    private final Set<UUID> waitingTruce =  new HashSet<>();

    @Getter
    private final Set<UUID> waitingAlly =  new HashSet<>();

    @Getter
    private final Set<UUID> trade =  new HashSet<>();

    @Getter
    @Setter
    private boolean market = false;




    @Getter
    @Setter
    private int tradeShips;

    @Getter
    @Setter
    private int ideas;

    @Getter
    private final Map<UUID, Boolean> siegeStorm = new HashMap<>();

    @Getter
    private final Map<UUID, Integer> truceMap = new HashMap<>();

    @Getter
    private final Map<UUID, Integer> debtMap = new LinkedHashMap<>();

    @Getter
    private final Map<UUID, Double> interestMap = new HashMap<>();






}
