package jp.mtjp.client.util;

import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class MtlLoader {
    public static class MtlMaterial {
        public float[] kd = {1f, 1f, 1f};
        public String texturePath = null;
    }

    public static Map<String, MtlMaterial> load(Identifier id) {
        Map<String, MtlMaterial> map = new HashMap<>();
        System.out.println("Loading MTL file from: " + "/assets/" + id.getNamespace() + id.getPath());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(MtlLoader.class.getResourceAsStream(
                        "/assets/" + id.getNamespace() + id.getPath()))
        ))) {
            MtlMaterial current = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("newmtl ")) {
                    String matName = line.substring(7).trim();
                    System.out.println("New material: " + matName);
                    current = new MtlMaterial();
                    map.put(matName, current);
                } else if (line.startsWith("Kd ")) {
                    if (current == null) continue;
                    String[] s = line.split("\\s+");
                    current.kd = new float[]{Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3])};
                    System.out.println("Material Kd color: " + Arrays.toString(current.kd));
                } else if (line.startsWith("map_Kd ")) {
                    if (current == null) continue;
                    current.texturePath = line.substring(7).trim();
                    System.out.println("Material texture map: " + current.texturePath);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load MTL file: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Loaded materials: " + map.keySet());
        return map;
    }
}
