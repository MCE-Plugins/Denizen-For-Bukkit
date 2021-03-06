package net.aufdemrand.denizen.objects.properties.entity;

import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.List;

public class EntityPotionEffects implements Property {
    public static boolean describes(dObject object) {
        return object instanceof dEntity && ((dEntity) object).isLivingEntity();
    }

    public static EntityPotionEffects getFrom(dObject object) {
        if (!describes(object)) {
            return null;
        }

        else {
            return new EntityPotionEffects((dEntity) object);
        }
    }

    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityPotionEffects(dEntity entity) {
        this.entity = entity;
    }

    dEntity entity;

    /////////
    // Property Methods
    ///////

    public String getPropertyString() {
        Collection<PotionEffect> effects = entity.getLivingEntity().getActivePotionEffects();
        if (effects.isEmpty()) {
            return null;
        }
        dList returnable = new dList();
        for (PotionEffect effect : effects) {
            returnable.add(effect.getType().getName() + "," + effect.getAmplifier() + "," + effect.getDuration());
        }
        return returnable.identify().substring(3);
    }

    public String getPropertyId() {
        return "potion_effects";
    }

    public String getAttribute(Attribute attribute) {

        if (attribute == null) {
            return "null";
        }

        // <--[tag]
        // @attribute <e@entity.list_effects>
        // @returns dList
        // @group attribute
        // @mechanism dEntity.potion_effects
        // @description
        // Returns the list of active potion effects on the entity, in the format: li@TYPE,AMPLIFIER,DURATION|...
        // Note that AMPLIFIER is a number representing the level, and DURATION is a number representing the time, in ticks, it will last for.
        // -->
        if (attribute.startsWith("list_effects")) {
            dList effects = new dList();
            for (PotionEffect effect : entity.getLivingEntity().getActivePotionEffects()) {
                effects.add(effect.getType().getName() + "," + effect.getAmplifier() + "," + effect.getDuration());
            }
            return effects.getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dEntity
        // @name potion_effects
        // @input dList
        // @description
        // Set the entity's active potion effects.
        // Each item in the list is formatted as: TYPE,AMPLIFIER,DURATION
        // Note that AMPLIFIER is a number representing the level, and DURATION is a number representing the time, in ticks, it will last for.
        // For example: SPEED,0,120 would give the entity a swiftness potion for 120 ticks.
        // @tags
        // <e@entity.list_effects>
        // -->
        if (mechanism.matches("potion_effects")) {
            dList effects = dList.valueOf(mechanism.getValue().asString());
            for (String effect : effects) {
                List<String> split = CoreUtilities.split(effect, ',');
                if (split.size() != 3) {
                    continue;
                }
                PotionEffectType effectType = PotionEffectType.getByName(split.get(0));
                if (Integer.valueOf(split.get(1)) == null || Integer.valueOf(split.get(2)) == null
                        || effectType == null) {
                    continue;
                }
                entity.getLivingEntity().addPotionEffect(new PotionEffect(effectType, Integer.valueOf(split.get(2)),
                        Integer.valueOf(split.get(1))));
            }
        }
    }
}
