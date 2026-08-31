package earthrp.customObjects;

import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import lombok.Data;
import org.bukkit.Material;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class PlayerModifier {

    private String name;
    private String id;
    private List<String> desc; // Уникальный ID (например, "vip_bonus", "town_tax")
    private Operation operation; // Тип операции (сложение или умножение)
    private int dateEnd;
    private Map<EPlayerAttribute, Double> attributes = new ConcurrentHashMap<>();
    private String material;

    private transient Material cachedMaterial;

    // Дефолтный конструктор нужен для библиотек десериализации (Gson/Jackson/SnakeYAML)
    public PlayerModifier() {
        if (this.attributes == null) {
            this.attributes = new ConcurrentHashMap<>();
        }
    }

    public PlayerModifier(String name, String id, List<String> desc, List<Double> value, Operation operation, int dateEnd, List<EPlayerAttribute> attribute, String material) {
        this();
        this.name = name;
        this.id = id;
        this.desc = desc;
        this.operation = operation;
        this.dateEnd = dateEnd;
        this.material = material;

        if (value != null && attribute != null) {
            for (int i = 0; i < value.size() && i < attribute.size(); i++) {
                this.attributes.put(attribute.get(i), value.get(i));
            }
        }
    }

    /**
     * Безопасный гетер для предотвращения NullPointerException при десериализации
     */
    public Map<EPlayerAttribute, Double> getAttributes() {
        if (this.attributes == null) {
            this.attributes = new ConcurrentHashMap<>();
        }
        return this.attributes;
    }

    public String getColorValueLegacy(EPlayerAttribute atr) {
        Double value = getAttributes().get(atr);
        if (value == null) return "&f0"; // Защита от NullPointerException, если атрибута нет в карте

        if (value > 0) return "&a +" + (int) (value * 100) + "&f%";
        if (value < 0) return "&c -" + (int) (value * 100) + "&f%";
        return "&f" + value;
    }

    public String getColorValue(EPlayerAttribute attribute) {
        Double value = getAttributes().get(attribute);
        if (value == null) return "<white>0"; // Защита от NullPointerException

        String color;
        if (attribute.isNegative()) {
            if (value > 0) color = " <red>+";
            else if (value < 0) color = " <green>";
            else color = "<white>";
        } else {
            if (value > 0) color = " <green>+";
            else if (value < 0) color = " <red>";
            else color = "<white>";
        }

        if (attribute.isFlat()) {
            return color + value;
        } else {
            return color + (int) (value * 100) + "%";
        }
    }

    public void setMaterial(String material) {
        this.material = material;
        this.cachedMaterial = null;
    }

    public Material getMaterial() {
        if (cachedMaterial != null) {
            return cachedMaterial;
        }

        if (material != null) {
            try {
                cachedMaterial = Material.valueOf(material);
                return cachedMaterial;
            } catch (IllegalArgumentException e) {
                Earth.getInstance().getLogger().warning("Неверный материал в конфиге: " + material);
            }
        }

        cachedMaterial = Material.NETHER_STAR;
        return cachedMaterial;
    }

    public enum Operation {
        ADD,        // Прибавить к базовому значению (+10)
        MULTIPLY    // Умножить итоговое значение (*1.2)
    }
}