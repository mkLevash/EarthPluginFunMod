package earthrp.customObjects;

import earthrp.customEnums.EPlayerAttribute;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class PlayerData {

    // Структура: Атрибут -> Список его модификаторов
    public final Map<EPlayerAttribute, List<PlayerModifier>> attributeModifiers = new EnumMap<>(EPlayerAttribute.class);

    // Метод для удобного добавления модификатора
    public void addModifier(EPlayerAttribute attribute, PlayerModifier modifier) {
        // Если списка еще нет, создаем его, иначе добавляем в существующий
        this.attributeModifiers.computeIfAbsent(attribute, k -> new ArrayList<>()).add(modifier);
    }

    // Метод для удаления модификатора по его ID (например, когда кончился бафф)
    public void removeModifier(EPlayerAttribute attribute, String modifierId) {
        List<PlayerModifier> modifiers = this.attributeModifiers.get(attribute);
        if (modifiers != null) {
            modifiers.removeIf(mod -> mod.getId().equalsIgnoreCase(modifierId));
        }
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
    private boolean retreat = false;

    @Getter
    @Setter
    private boolean battle = false;


    @Getter
    private final Set<UUID> war = new HashSet<>();
    @Getter
    private final Set<UUID> ally =  new HashSet<>();






}
