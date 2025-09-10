package jp.mtjp.client.util;

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

    public ObjModel(Identifier id) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(ObjModel.class.getResourceAsStream(
                        "/assets/" + id.getNamespace() + "/models/obj/" + id.getPath()))
        ))) {
            Mesh currentMesh = null;
            List<float[]> vertices = new ArrayList<>();
            List<float[]> texCoords = new ArrayList<>();
            List<float[]> normals = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("o ")) {
                    String name = line.substring(2).trim();
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
                } else if (line.startsWith("f ")) {
                    if (currentMesh == null) continue;
                    String[] s = line.split("\\s+");
                    for (int i = 1; i <= 3; i++) {
                        String[] idx = s[i].split("/");
                        int v = Integer.parseInt(idx[0]) - 1;
                        int vt = idx.length > 1 && !idx[1].isEmpty() ? Integer.parseInt(idx[1]) - 1 : -1;
                        int vn = idx.length > 2 ? Integer.parseInt(idx[2]) - 1 : -1;
                        currentMesh.faces.add(new int[]{v, vt, vn});
                    }
                }
            }

            for (Mesh mesh : meshes.values()) {
                mesh.vertices.addAll(vertices);
                mesh.texCoords.addAll(texCoords);
                mesh.normals.addAll(normals);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderPart(String name, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier texture) {
        Mesh mesh = meshes.get(name);
        if (mesh == null) return;

        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));

        for (int[] f : mesh.faces) {
            float[] v = mesh.vertices.get(f[0]);
            float[] uv = f[1] >= 0 ? mesh.texCoords.get(f[1]) : new float[]{0, 0};
            float[] n = f[2] >= 0 ? mesh.normals.get(f[2]) : new float[]{0, 1, 0};

            vc.vertex(matrices.peek().getPositionMatrix(), v[0], v[1], v[2])
                    .color(255, 255, 255, 255)
                    .texture(uv[0], uv[1])
                    .normal(matrices.peek().getNormalMatrix(), n[0], n[1], n[2])
                    .next();
        }
    }

    public Set<String> getPartNames() { return meshes.keySet(); }
}
