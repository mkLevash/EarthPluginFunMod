package earthrp.listeners;

import earthrp.Earth;
import earthrp.customEnums.BuildingType;
import earthrp.customEnums.EarthItem;
import earthrp.customObjects.Building;
import earthrp.customObjects.Town;
import earthrp.events.TownCheckEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BuildingProduction implements Listener {

    @EventHandler
    public void townCheck(TownCheckEvent e) {
        for(Building building: Earth.getInstance().getDatabase().getBuildings()){

            if(!building.getData().isStatus()) continue;

            if(building.getData().getItem()!=null){
                Town town = building.getTown();
                BuildingType bt = building.getData().getType();
                int N = town.getPeasant();
                long prod = Math.round(building.getBaseProduction() * N);
                if(bt.equals(BuildingType.PASTURE)){


                } else if (bt.equals(BuildingType.FORGE)) {
                    EarthItem ore = null;
                    EarthItem fuel = null;
                    EarthItem sword = null;
                    sword = building.getData().getItem();

                    switch (sword){
                        
                        case IRON_SWORD -> {

                            if(town.getItem(EarthItem.IRON_INGOT)>=prod) ore = EarthItem.IRON_INGOT;
                            if(town.getItem(EarthItem.RAW_IRON)>=prod) ore = EarthItem.RAW_IRON;
                            if(town.getItem(EarthItem.COAL)>0) fuel = EarthItem.COAL;
                            if(town.getItem(EarthItem.CHARCOAL)>0) fuel = EarthItem.CHARCOAL;
                        }
                        case COPPER_SWORD -> {

                            if(town.getItem(EarthItem.COPPER_INGOT)>=prod) ore = EarthItem.COPPER_INGOT;
                            if(town.getItem(EarthItem.RAW_COPPER)>=prod) ore = EarthItem.RAW_COPPER;
                            if(town.getItem(EarthItem.COAL)>0) fuel = EarthItem.COAL;
                            if(town.getItem(EarthItem.CHARCOAL)>0) fuel = EarthItem.CHARCOAL;
                        }
                        case WOODEN_SWORD -> {

                            if(town.getItem(EarthItem.OAK_PLANKS)>=prod) ore = EarthItem.OAK_PLANKS;
                            if(town.getItem(EarthItem.DARK_OAK_PLANKS)>=prod) ore = EarthItem.DARK_OAK_PLANKS;
                            if(town.getItem(EarthItem.BIRCH_PLANKS)>=prod) ore = EarthItem.BIRCH_PLANKS;
                            if(town.getItem(EarthItem.SPRUCE_PLANKS)>=prod) ore = EarthItem.SPRUCE_PLANKS;
                            if(town.getItem(EarthItem.JUNGLE_PLANKS)>=prod) ore = EarthItem.JUNGLE_PLANKS;
                            if(town.getItem(EarthItem.CHERRY_PLANKS)>=prod) ore = EarthItem.CHERRY_PLANKS;
                            if(town.getItem(EarthItem.ACACIA_PLANKS)>=prod) ore = EarthItem.ACACIA_PLANKS;
                            if(town.getItem(EarthItem.MANGROVE_PLANKS)>=prod) ore = EarthItem.MANGROVE_PLANKS;
                            if(town.getItem(EarthItem.PALE_OAK_PLANKS)>=prod) ore = EarthItem.PALE_OAK_PLANKS;

                        }

                        case STONE_SWORD -> {
                            if(town.getItem(EarthItem.COBBLESTONE)>=prod) ore = EarthItem.COBBLESTONE;
                        }

                        case DIAMOND_SWORD -> {
                            if(town.getItem(EarthItem.DIAMOND)>=prod) ore = EarthItem.DIAMOND;
                        }

                        case NETHERITE_SWORD -> {
                            if(town.getItem(EarthItem.EBONY_INGOT)>=prod) ore = EarthItem.EBONY_INGOT;
                            if(town.getItem(EarthItem.RAW_EBONY)>=prod) ore = EarthItem.RAW_EBONY;
                            if(town.getItem(EarthItem.COAL)>0) fuel = EarthItem.COAL;
                            if(town.getItem(EarthItem.CHARCOAL)>0) fuel = EarthItem.CHARCOAL;
                        }
                    }
                    if(ore != null){
                        if(fuel !=null){
                            town.addItem(ore,-prod);
                            town.addItem(fuel,-1);
                            town.addItem(sword,prod);
                        } else if (sword == EarthItem.WOODEN_SWORD || sword == EarthItem.STONE_SWORD) {
                            town.addItem(ore,-prod);
                            town.addItem(sword,prod);
                        }

                    }


                } else if (bt.equals(BuildingType.QUARRY)) {
                    EarthItem coal = null;
                    if(town.getItem(EarthItem.COAL)>0) coal = EarthItem.COAL;
                    if(town.getItem(EarthItem.CHARCOAL)>0) coal = EarthItem.CHARCOAL;
                    if(town.getItem(EarthItem.GUNPOWDER)>0 && coal !=null){
                        town.addItem(EarthItem.GUNPOWDER,-1);
                        town.addItem(coal,-1);
                        town.addItem(building.getData().getItem(),prod);
                    }

                } else if(!bt.equals(BuildingType.FARM)){

                    // Получаем предмет, который хочет произвести здание
                    EarthItem targetItem = building.getData().getItem();
                    Set<EarthItem> recipe = targetItem.getRequirement();

                    if (recipe.isEmpty()) {
                        town.addItem(targetItem, prod);
                    } else {


                        boolean canCraft = true;
                        List<EarthItem> itemsToRemove = new ArrayList<>();


                        if (targetItem.name().contains("SWORD") || targetItem.name().contains("PLANKS")) {

                            EarthItem foundIngredient = null;
                            for (EarthItem ingredient : recipe) {
                                if (town.getItem(ingredient) >= prod) {
                                    foundIngredient = ingredient;
                                    break;
                                }
                            }

                            if (foundIngredient != null) {
                                itemsToRemove.add(foundIngredient);
                            } else {
                                canCraft = false;
                            }

                        } else {

                            if (recipe.contains(EarthItem.IRON_INGOT) && recipe.contains(EarthItem.COPPER_INGOT)) {
                                if (town.getItem(EarthItem.IRON_INGOT) >= prod) itemsToRemove.add(EarthItem.IRON_INGOT);
                                else if (town.getItem(EarthItem.COPPER_INGOT) >= prod) itemsToRemove.add(EarthItem.COPPER_INGOT);
                                else canCraft = false;
                            }





                            canCraft = processCraftMaterials(canCraft,recipe,List.of("PLANKS"),town,prod,itemsToRemove);
                            canCraft = processCraftMaterials(canCraft,recipe,List.of("WOOD","LOG"),town,prod,itemsToRemove);


                            if (canCraft) {
                                for (EarthItem ingredient : recipe) {
                                    if (ingredient.name().matches(".*(INGOT|PLANKS|WOOD|LOG|COAL).*")) continue;

                                    if (town.getItem(ingredient) >= prod) {
                                        itemsToRemove.add(ingredient);
                                    } else {
                                        canCraft = false;
                                        break;
                                    }
                                }
                            }


                            if (canCraft && recipe.stream().anyMatch(i -> i.name().contains("CHARCOAL"))) {
                                EarthItem fuel = null;
                                if(town.getItem(EarthItem.COAL)>0) fuel = EarthItem.COAL;
                                if(town.getItem(EarthItem.CHARCOAL)>0) fuel = EarthItem.CHARCOAL;
                                if(fuel == null){
                                    canCraft = false;
                                }else{
                                    town.addItem(fuel,-1);
                                }

                            } else if (canCraft && recipe.stream().anyMatch(i -> i.name().contains("COAL"))) {
                                EarthItem foundPlank = recipe.stream()
                                        .filter(i -> i.name().equals("COAL") && town.getItem(i) >= prod)
                                        .findFirst().orElse(null);

                                if (foundPlank != null) itemsToRemove.add(foundPlank);
                                else canCraft = false;

                            }
                        }

                        if (canCraft && !itemsToRemove.isEmpty()) {
                            for (EarthItem itemToRemove : itemsToRemove) {
                                town.addItem(itemToRemove, -prod);
                            }
                            town.addItem(targetItem, prod);
                        }
                    }



                }
            }
        }
    }

    public boolean processCraftMaterials(boolean canCraft, Set<EarthItem> recipe, List<String> targetNames, Town town, long prod, List<EarthItem> itemsToRemove) {
        // Если крафтить уже нельзя, сразу возвращаем false без лишних проверок
        if (!canCraft) return false;

        // Проверяем, есть ли в рецепте хоть один ингредиент, содержащий любое из целевых слов
        boolean hasRequiredMaterial = recipe.stream()
                .anyMatch(item -> targetNames.stream().anyMatch(target -> item.name().contains(target)));

        if (hasRequiredMaterial) {
            // Ищем первый подходящий предмет, которого хватает в городе
            EarthItem foundMaterial = recipe.stream()
                    .filter(item -> targetNames.stream().anyMatch(target -> item.name().contains(target)))
                    .filter(item -> town.getItem(item) >= prod)
                    .findFirst()
                    .orElse(null);

            if (foundMaterial != null) {
                itemsToRemove.add(foundMaterial);
                return true; // Материал найден и добавлен в очередь на удаление, крафт возможен
            } else {
                return false; // Материал нужен, но на складе его нет -> крафтить нельзя
            }
        }

        return true; // В рецепте нет этих материалов, крафт не заблокирован
    }
}
