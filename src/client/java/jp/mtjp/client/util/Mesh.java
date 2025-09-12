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
    public final int[] vertex = new int[3];
    public final int[] texCoord = new int[3];
    public final int[] normal = new int[3];

    public Triangle(int[] v0, int[] v1, int[] v2) {
        this.vertex[0] = v0[0];
        this.texCoord[0] = v0[1];
        this.normal[0] = v0[2];
        this.vertex[1] = v1[0];
        this.texCoord[1] = v1[1];
        this.normal[1] = v1[2];
        this.vertex[2] = v2[0];
        this.texCoord[2] = v2[1];
        this.normal[2] = v2[2];
    }
}