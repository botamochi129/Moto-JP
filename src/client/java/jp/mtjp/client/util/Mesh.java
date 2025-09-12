package jp.mtjp.client.util;

import java.util.List;
import java.util.ArrayList;

public class Mesh {
    public final String name;
    public final List<int[]> faces = new ArrayList<>();

    public Mesh(String name) {
        this.name = name;
    }
}
