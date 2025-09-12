package jp.mtjp.client.util;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    public final String name;
    public final List<Triangle> triangles = new ArrayList<>();
    public final List<String> faceMaterials = new ArrayList<>();

    public Mesh(String name) {
        this.name = name;
    }
}

class Triangle {
    public final int[] vertex;   // 頂点インデックス (3つ)
    public final int[] texCoord; // UVインデックス (3つ)
    public final int[] normal;   // 法線インデックス (3つ)

    public Triangle(int[] v, int[] vt, int[] vn) {
        this.vertex = v;
        this.texCoord = vt;
        this.normal = vn;
    }
}
