package jp.mtjp.client;

import jp.mtjp.client.util.ObjModel;
import jp.mtjp.entity.RX7Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class RX7EntityRenderer extends EntityRenderer<RX7Entity> {

    private static final Identifier DUMMY_TEXTURE = new Identifier("mtjp", "models/obj/rx7.png");
    private static final ObjModel MODEL = new ObjModel(new Identifier("mtjp", "/models/obj/rx7.obj"));

    public RX7EntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(RX7Entity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {

        matrices.push();

        // モデルのスケーリング例 (3倍に拡大)
        matrices.scale(3.0f, 3.0f, 3.0f);

        // レンダリング
        MODEL.renderPart("body", matrices, vertexConsumers, DUMMY_TEXTURE, 255);
        MODEL.renderPart("light_lib", matrices, vertexConsumers, DUMMY_TEXTURE, 255);
        MODEL.renderPart("doorL", matrices, vertexConsumers, DUMMY_TEXTURE, 255);
        MODEL.renderPart("doorR", matrices, vertexConsumers, DUMMY_TEXTURE, 255);
        MODEL.renderPart("FL", matrices, vertexConsumers, DUMMY_TEXTURE, 255);
        MODEL.renderPart("FR", matrices, vertexConsumers, DUMMY_TEXTURE, 255);
        MODEL.renderPart("BL", matrices, vertexConsumers, DUMMY_TEXTURE, 255);
        MODEL.renderPart("BR", matrices, vertexConsumers, DUMMY_TEXTURE, 255);

        matrices.pop();
    }

    @Override
    public Identifier getTexture(RX7Entity entity) {
        return DUMMY_TEXTURE;
    }
}
