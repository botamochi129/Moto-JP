package jp.mtjp.client.util;

import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Objects;

public class MtlLoader {
    public static class MtlMaterial {
        public float[] kd = {1f, 1f, 1f};
        public String texturePath = null; // as in mtl, e.g. "entity/rx7.png"
    }

    public static Map<String, MtlMaterial> load(Identifier id) {
        Map<String, MtlMaterial> map = new HashMap<>();
        String path = id.getPath();
        if (path.startsWith("/")) path = path.substring(1);
        String resPath = "/assets/" + id.getNamespace() + "/" + path;
        System.out.println("[Obj] Loading MTL file from: " + resPath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(MtlLoader.class.getResourceAsStream(resPath))
        ))) {
            MtlMaterial current = null;
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("newmtl ")) {
                    String matName = line.substring(7).trim();
                    current = new MtlMaterial();
                    map.put(matName, current);
                    System.out.println("[Obj] newmtl: " + matName);
                } else if (line.startsWith("Kd ") && current != null) {
                    String[] s = line.split("\\s+");
                    if (s.length >= 4) {
                        current.kd = new float[]{
                                Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3])
                        };
                        System.out.println("[Obj] Kd: " + Arrays.toString(current.kd));
                    }
                } else if (line.startsWith("map_Kd ") && current != null) {
                    String tex = line.substring(7).trim();
                    current.texturePath = tex;
                    System.out.println("[Obj] map_Kd: " + tex);
                }
            }
        } catch (Exception e) {
            System.err.println("[Obj] Failed to load MTL file: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("[Obj] Loaded materials: " + map.keySet());
        return map;
    }
}
