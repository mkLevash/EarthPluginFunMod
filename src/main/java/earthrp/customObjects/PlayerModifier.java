package earthrp.customObjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PlayerModifier {

    private String name;
    private String id;          // Уникальный ID (например, "vip_bonus", "town_tax")
    private double value;       // Значение модификатора
    private Operation operation;// Тип операции (сложение или умножение)

    public String getColorValue(){
        if (this.value > 0) return "&a +" + (int) (this.value * 100) + "&f%";
        if (this.value < 0) return "&c -" + (int) (this.value * 100) + "&f%";
        return "&f" + value;
    }

    public enum Operation {
        ADD,        // Прибавить к базовому значению (+10)
        MULTIPLY    // Умножить итоговое значение (*1.2)
    }

}
