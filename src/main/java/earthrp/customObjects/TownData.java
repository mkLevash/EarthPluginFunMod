package earthrp.customObjects;

import earthrp.customEnums.TownItem;

import java.util.EnumMap;
import java.util.Map;

public class TownData {

    public final Map<TownItem, Long> items = new EnumMap<>(TownItem.class);


}
