package jp.mtjp.client.util;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class ObjModel {
    private final Map<String, Mesh> meshes = new HashMap<>();

    // 頂点群は全メッシュで共有
    private final List<float[]> vertices = new ArrayList<>();
    private final List<float[]> texCoords = new ArrayList<>();
    private final List<float[]> normals = new ArrayList<>();

    public ObjModel(Identifier id) {
        System.out.println("Loading OBJ model from: " + "/assets/" + id.getNamespace() + id.getPath());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(ObjModel.class.getResourceAsStream(
                        "/assets/" + id.getNamespace() + id.getPath()))
        ))) {
            Mesh currentMesh = null;

            String currentMaterial = null; // usemtl を考慮する場合のマテリアル管理

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("g ") || line.startsWith("o ")) {
                    String name = line.substring(2).trim();
                    System.out.println("New mesh detected: " + name);
                    currentMesh = new Mesh(name);
                    meshes.put(name, currentMesh);
                } else if (line.startsWith("v ")) {
                    String[] s = line.split("\\s+");
                    vertices.add(new float[]{Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3])});
                } else if (line.startsWith("vt ")) {
                    String[] s = line.split("\\s+");
                    texCoords.add(new float[]{Float.parseFloat(s[1]), 1 - Float.parseFloat(s[2])});
                } else if (line.startsWith("vn ")) {
                    String[] s = line.split("\\s+");
                    normals.add(new float[]{Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3])});
                } else if (line.startsWith("usemtl ")) {
                    currentMaterial = line.substring(7).trim();
                    // 現時点ではcurrentMaterialを管理する方法を拡張可能
                } else if (line.startsWith("f ")) {
                    if (currentMesh == null) continue;
                    String[] s = line.split("\\s+");
                    for (int i = 1; i <= 3; i++) {
                        String[] idx = s[i].split("/");
                        int v = Integer.parseInt(idx[0]) - 1;
                        int vt = idx.length > 1 && !idx[1].isEmpty() ? Integer.parseInt(idx[1]) - 1 : -1;
                        int vn = idx.length > 2 ? Integer.parseInt(idx[2]) - 1 : -1;
                        currentMesh.faces.add(new int[]{v, vt, vn});
                        // 必要ならマテリアルや面識別情報も保持する
                    }
                }
            }

            System.out.println("Loaded meshes:");
            for (Mesh mesh : meshes.values()) {
                System.out.printf("Mesh %s - Vertices: %d, TexCoords: %d, Normals: %d, Faces: %d\n",
                        mesh.name, vertices.size(), texCoords.size(), normals.size(), mesh.faces.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ObjModelクラス内
    public void renderPart(String name, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier texture, int light) {
        Mesh mesh = meshes.get(name);
        if (mesh == null) return;

        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));

        for (int[] f : mesh.faces) {
            int vi = f[0];
            int vti = f[1];
            int vni = f[2];

            if (vi < 0 || vi >= vertices.size()) continue;
            float[] v = vertices.get(vi);

            float[] uv = (vti >= 0 && vti < texCoords.size()) ? texCoords.get(vti) : new float[]{0f, 0f};
            float[] n = (vni >= 0 && vni < normals.size()) ? normals.get(vni) : new float[]{0f, 1f, 0f};

            vc.vertex(matrices.peek().getPositionMatrix(), v[0], v[1], v[2])
                    .color(255, 255, 255, 255)
                    .texture(uv[0], uv[1])
                    .overlay(OverlayTexture.DEFAULT_UV)
                    .light(light)
                    .normal(matrices.peek().getNormalMatrix(), n[0], n[1], n[2])
                    .next();
        }
    }

    public Set<String> getPartNames() {
        System.out.println("Available parts: " + meshes.keySet());
        return meshes.keySet();
    }
}
