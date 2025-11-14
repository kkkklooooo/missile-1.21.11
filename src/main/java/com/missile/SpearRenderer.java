package com.missile;

import net.minecraft.client.data.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.RotationAxis;

import java.util.List;

import static net.minecraft.client.data.Models.SPEAR_IN_HAND;

public class SpearRenderer extends EntityRenderer<SpearEntity, SpearRenderState> {
    //public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/trident.png");
    private final Model model;
    public SpearRenderer(EntityRendererFactory.Context context) {

        super(context);
        this.model = SPEAR_IN_HAND;
    }

    @Override
    public SpearRenderState createRenderState() {
        return new SpearRenderState();
    }


//    public void render(
//            SpearRenderState sp,
//            MatrixStack matrixStack,
//            OrderedRenderCommandQueue orderedRenderCommandQueue,
//            CameraRenderState cameraRenderState
//    ) {
//        super.render(sp, matrixStack, orderedRenderCommandQueue, cameraRenderState);
//    }
//
//    public SpearRenderState createRenderState() {
//        return new SpearRenderState();
//    }


}
