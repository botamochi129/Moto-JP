package jp.mtjp.client.util;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    public final String name;
    public final List<float[]> vertices = new ArrayList<>();
    public final List<float[]> texCoords = new ArrayList<>();
    public final List<float[]> normals = new ArrayList<>();
    public final List<int[]> faces = new ArrayList<>();

    public Mesh(String name) {
        this.name = name;
    }
}
