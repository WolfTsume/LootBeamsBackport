package org.dreamfinity.beamingdrops;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemHelper {

    private ItemHelper() {}

    public static boolean isStackInArray(ItemStack stack, String[] itemStrings) {
        if (stack == null || itemStrings == null) return false;
        for (String itemString : itemStrings) {
            if (matchesStack(stack, itemString)) return true;
        }
        return false;
    }

    public static boolean matchesStack(ItemStack stack, String stackString) {
        if (stack == null || stack.getItem() == null || stackString == null) return false;
        stackString = stackString.replace("\"", "").trim();
        String[] splitString = stackString.split(":", -1);
        if (splitString.length < 2 || splitString.length > 3) {
            return false;
        }
        // ore dictionary handling
        if ("ore".equals(splitString[0])) {
            if (splitString.length != 2 || !OreDictionary.doesOreNameExist(splitString[1])) return false;
            for (int ID : OreDictionary.getOreIDs(stack)) {
                if (OreDictionary.getOreID(splitString[1]) == ID) {
                    return true;
                }
            }
            return false;
        }
        GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(stack.getItem());
        return id != null && BeamFilters.matchesItemId(id.toString(), stack.getItemDamage(), stackString);
    }
}
