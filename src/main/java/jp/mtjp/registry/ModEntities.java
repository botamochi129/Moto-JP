package jp.mtjp.registry;

import jp.mtjp.Mtjp;
import jp.mtjp.entity.RX7Entity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {
    public static EntityType<RX7Entity> RX7;

    public static void register() {
        RX7 = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(Mtjp.MOD_ID, "rx7"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, RX7Entity::new)
                        .dimensions(EntityDimensions.fixed(2.0F, 1.2F))
                        .trackRangeBlocks(80)
                        .trackedUpdateRate(2)
                        .build()
        );
    }
}
