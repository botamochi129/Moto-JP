package jp.mtjp.client.util;

import jp.mtjp.client.util.MtlLoader;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class ObjModel {
    private final Map<String, Mesh> meshes = new HashMap<>();
    private final Map<String, Identifier> materialTextures = new HashMap<>();
    private final List<float[]> vertices = new ArrayList<>();
    private final List<float[]> texCoords = new ArrayList<>();
    private final List<float[]> normals = new ArrayList<>();

    private final Identifier defaultTexture = new Identifier("mtjp", "textures/default.png");

    public ObjModel(Identifier objId, Identifier mtlId) {
        // Load MTL
        Map<String, MtlLoader.MtlMaterial> materials = MtlLoader.load(mtlId);
        for (var e : materials.entrySet()) {
            String path = e.getValue().texturePath;
            if (path != null) {
                materialTextures.put(e.getKey(), new Identifier("mtjp", "textures/" + path));
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(ObjModel.class.getResourceAsStream("/assets/" + objId.getNamespace() + objId.getPath()))
        ))) {
            Mesh currentMesh = null;
            String currentMaterial = null;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("g ") || line.startsWith("o ")) {
                    String name = line.substring(2).trim();
                    currentMesh = new Mesh(name);
                    meshes.put(name, currentMesh);
                } else if (line.startsWith("v ")) {
                    String[] s = line.split("\\s+");
                    vertices.add(new float[] { Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3]) });
                } else if (line.startsWith("vt ")) {
                    String[] s = line.split("\\s+");
                    texCoords.add(new float[] { Float.parseFloat(s[1]), 1 - Float.parseFloat(s[2]) });
                } else if (line.startsWith("vn ")) {
                    String[] s = line.split("\\s+");
                    normals.add(new float[] { Float.parseFloat(s[1]), Float.parseFloat(s[2]), Float.parseFloat(s[3]) });
                } else if (line.startsWith("usemtl ")) {
                    currentMaterial = line.substring(7).trim();
                } else if (line.startsWith("f ")) {
                    if (currentMesh == null) continue;
                    String[] parts = line.split("\\s+");
                    if (parts.length < 4) continue;

                    int[] v0 = parseFaceElement(parts[1]);
                    for (int i = 2; i < parts.length - 1; i++) {
                        int[] v1 = parseFaceElement(parts[i]);
                        int[] v2 = parseFaceElement(parts[i + 1]);
                        currentMesh.triangles.add(new Triangle(v0, v1, v2));
                        currentMesh.faceMaterials.add(currentMaterial);
                    }
                }
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
        return new int[] { v, vt, vn };
    }

    public void renderPart(String name, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Mesh mesh = meshes.get(name);
        if (mesh == null) return;

        Map<String, List<Triangle>> materialTriangles = new HashMap<>();
        for (int i = 0; i < mesh.triangles.size(); i++) {
            String mat = mesh.faceMaterials.get(i);
            materialTriangles.computeIfAbsent(mat, k -> new ArrayList<>()).add(mesh.triangles.get(i));
        }

        for (var entry : materialTriangles.entrySet()) {
            Identifier tex = materialTextures.getOrDefault(entry.getKey(), defaultTexture);
            VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(tex));

            for (Triangle tri : entry.getValue()) {
                for (int i = 0; i < 3; i++) {
                    int vi = tri.vertex[i], vti = tri.texCoord[i], vni = tri.normal[i];

                    float[] v = vertices.get(vi);
                    float[] uv = (vti >= 0 && vti < texCoords.size()) ? texCoords.get(vti) : new float[] { 0f, 0f };
                    float[] n = (vni >= 0 && vni < normals.size()) ? normals.get(vni) : new float[] { 0f, 1f, 0f };

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
