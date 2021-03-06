package net.aufdemrand.denizen.objects.properties.item;

import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.utilities.nbt.CustomNBT;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemNBT implements Property {

    public static boolean describes(dObject item) {
        return item instanceof dItem && ((dItem) item).getMaterial().getMaterial() != Material.AIR;
    }

    public static ItemNBT getFrom(dObject item) {
        if (!describes(item)) {
            return null;
        }
        else {
            return new ItemNBT((dItem) item);
        }
    }

    private ItemNBT(dItem item) {
        this.item = item;
    }

    dItem item;

    @Override
    public String getAttribute(Attribute attribute) {

        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <i@item.has_nbt[<key>]>
        // @returns Element(Boolean)
        // @group properties
        // @description
        // Returns whether this item has the specified NBT key.
        // -->
        if (attribute.startsWith("has_nbt")) {
            return new Element(CustomNBT.hasCustomNBT(item.getItemStack(), attribute.getContext(1)))
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <i@item.nbt_keys[<filter>]>
        // @returns dList
        // @group properties
        // @description
        // Returns all of this item's NBT keys as a dList. Optionally, specify
        // a filter for the start of the keys.
        // -->
        if (attribute.startsWith("nbt_keys")) {
            String filter = attribute.hasContext(1) ? attribute.getContext(1) : "";
            return new dList(CustomNBT.listNBT(item.getItemStack(), filter))
                    .getAttribute(attribute.fulfill(1));
        }

        // <--[tag]
        // @attribute <i@item.nbt[<key>]>
        // @returns Element
        // @group properties
        // @description
        // Returns the value of this item's NBT key as a string Element as best it can.
        // -->
        if (attribute.startsWith("nbt")) {
            return new Element(CustomNBT.getCustomNBT(item.getItemStack(), attribute.getContext(1)))
                    .getAttribute(attribute.fulfill(1));
        }

        return null;
    }


    @Override
    public String getPropertyString() {
        ItemStack itemStack = item.getItemStack();
        List<String> nbtKeys = CustomNBT.getRegisteredNBT(itemStack);
        if (nbtKeys != null && !nbtKeys.isEmpty()) {
            dList list = new dList();
            for (String key : nbtKeys) {
                list.add(key + "/" + CustomNBT.getCustomNBT(itemStack, key));
            }
            return list.identify();
        }
        return null;
    }

    @Override
    public String getPropertyId() {
        return "nbt";
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // Internal use only
        if (mechanism.matches("nbt")) {
            dList list = mechanism.getValue().asType(dList.class);
            ItemStack itemStack = item.getItemStack();
            for (String string : list) {
                String[] split = string.split("/", 2);
                itemStack = CustomNBT.addCustomNBT(itemStack, split[0], split[1]);
            }
            item.setItemStack(itemStack);
        }
    }
}
