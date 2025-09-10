package jp.mtjp.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.util.Identifier;

public class MtlLoader {
    public static class MtlMaterial {
        public float[] kd = {1f, 1f, 1f};
        public String texturePath = null;
    }

    public static Map<String, MtlMaterial> load(Identifier id) {
        Map<String, MtlMaterial> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(MtlLoader.class.getResourceAsStream(
                        "/assets/" + id.getNamespace() + "/models/obj/" + id.getPath()))
        ))) {
            MtlMaterial current = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("newmtl ")) {
                    current = new MtlMaterial();
                    map.put(line.substring(7).trim(), current);
                } else if (line.startsWith("Kd ")) {
                    String[] s = line.split("\\s+");
                    current.kd = new float[]{Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3])};
                } else if (line.startsWith("map_Kd ")) {
                    current.texturePath = line.substring(7).trim();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }
}
