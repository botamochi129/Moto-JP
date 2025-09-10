package jp.mtjp.client.util;

import java.util.List;
import java.util.ArrayList;

public class Mesh {
    public final String name;
    public final List<float[]> vertices;
    public final List<float[]> texCoords;
    public final List<float[]> normals;
    public final List<int[]> faces = new ArrayList<>();

    public Mesh(String name, List<float[]> vertices, List<float[]> texCoords, List<float[]> normals) {
        this.name = name;
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.normals = normals;
    }
}
