package jp.mtjp;

import jp.mtjp.registry.ModEntities;
import net.fabricmc.api.ModInitializer;

public class Mtjp implements ModInitializer {
    public static final String MOD_ID = "mtjp";

    @Override
    public void onInitialize() {
        ModEntities.register();
    }
}
