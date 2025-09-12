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
            String currentMaterial = null;

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
                } else if (line.startsWith("f ")) {
                    if (currentMesh == null) continue;
                    String[] parts = line.split("\\s+");
                    if (parts.length < 4) continue;

                    // 多角形三角形化処理(トライアンギュレーション)
                    int[] v0 = parseFaceElement(parts[1]);
                    for (int i = 2; i < parts.length - 1; i++) {
                        int[] v1 = parseFaceElement(parts[i]);
                        int[] v2 = parseFaceElement(parts[i + 1]);
                        currentMesh.faces.add(v0);
                        currentMesh.faces.add(v1);
                        currentMesh.faces.add(v2);
                    }
                }
            }

            System.out.println("Loaded meshes:");
            for (Mesh mesh : meshes.values()) {
                System.out.printf("Mesh %s - Faces: %d\n", mesh.name, mesh.faces.size() / 3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int[] parseFaceElement(String element) {
        String[] idx = element.split("/");
        int v = Integer.parseInt(idx[0]) - 1;
        int vt = idx.length > 1 && !idx[1].isEmpty() ? Integer.parseInt(idx[1]) - 1 : -1;
        int vn = idx.length > 2 ? Integer.parseInt(idx[2]) - 1 : -1;
        return new int[]{v, vt, vn};
    }

    public void renderPart(String name, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier texture, int light) {
        Mesh mesh = meshes.get(name);
        if (mesh == null) return;

        VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));

        for (int i = 0; i < mesh.faces.size(); i += 3) {
            int[] f1 = mesh.faces.get(i);
            int[] f2 = mesh.faces.get(i + 1);
            int[] f3 = mesh.faces.get(i + 2);

            float[] v1 = vertices.get(f1[0]);
            float[] v2 = vertices.get(f2[0]);
            float[] v3 = vertices.get(f3[0]);

            float[] faceNormal = calculateFaceNormal(v1, v2, v3);

            int[][] indices = {f1, f2, f3};
            for (int[] f : indices) {
                int vi = f[0];
                int vti = f[1];
                int vni = f[2];

                float[] v = vertices.get(vi);
                float[] uv = (vti >= 0 && vti < texCoords.size()) ? texCoords.get(vti) : new float[]{0f, 0f};
                float[] n = (vni >= 0 && vni < normals.size()) ? normals.get(vni) : faceNormal;

                vc.vertex(matrices.peek().getPositionMatrix(), v[0], v[1], v[2])
                        .color(255, 255, 255, 255)
                        .texture(uv[0], uv[1])
                        .overlay(OverlayTexture.DEFAULT_UV)
                        .light(light)
                        .normal(matrices.peek().getNormalMatrix(), n[0], n[1], n[2])
                        .next();
            }
        }
    }

    private float[] calculateFaceNormal(float[] v1, float[] v2, float[] v3) {
        float[] u = new float[]{v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]};
        float[] v = new float[]{v3[0] - v1[0], v3[1] - v1[1], v3[2] - v1[2]};
        float nx = u[1] * v[2] - u[2] * v[1];
        float ny = u[2] * v[0] - u[0] * v[2];
        float nz = u[0] * v[1] - u[1] * v[0];
        float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length == 0f) return new float[]{0f, 1f, 0f};
        return new float[]{nx / length, ny / length, nz / length};
    }
}
