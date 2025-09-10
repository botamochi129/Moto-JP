package jp.mtjp.client;

import jp.mtjp.client.util.Mesh;
import jp.mtjp.entity.RX7Entity;
import jp.mtjp.client.util.ObjModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RX7EntityRenderer extends EntityRenderer<RX7Entity> {

    private static final Identifier DUMMY_TEXTURE = new Identifier("mtjp", "textures/entity/rx7.png");
    private static final ObjModel MODEL = new ObjModel(new Identifier("mtjp", "rx7.obj"));

    public RX7EntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(RX7Entity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {

        matrices.push();
        // まずスケール・translateなし
        MODEL.renderPart("body", matrices, vertexConsumers, DUMMY_TEXTURE);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(RX7Entity entity) {
        return DUMMY_TEXTURE;
    }
}
