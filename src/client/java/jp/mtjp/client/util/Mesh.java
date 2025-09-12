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