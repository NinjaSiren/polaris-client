/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.utils.render.postprocess;

import polarisdevelopment.polarisclient.renderer.GL;
import polarisdevelopment.polarisclient.renderer.PostProcessRenderer;
import polarisdevelopment.polarisclient.renderer.Shader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.entity.Entity;
import polarisdevelopment.polarisclient.MeteorClient;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public abstract class PostProcessShader {
    public OutlineVertexConsumerProvider vertexConsumerProvider;
    public Framebuffer framebuffer;
    protected Shader shader;

    public void init(String frag) {
        vertexConsumerProvider = new OutlineVertexConsumerProvider(MeteorClient.mc.getBufferBuilders().getEntityVertexConsumers());
        framebuffer = new SimpleFramebuffer(MeteorClient.mc.getWindow().getFramebufferWidth(), MeteorClient.mc.getWindow().getFramebufferHeight(), false, MinecraftClient.IS_SYSTEM_MAC);
        shader = new Shader("post-process/base.vert", "post-process/" + frag + ".frag");
    }

    protected abstract boolean shouldDraw();
    public abstract boolean shouldDraw(Entity entity);

    protected void preDraw() {}
    protected void postDraw() {}

    protected abstract void setUniforms();

    public void beginRender() {
        if (!shouldDraw()) return;

        framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);
        MeteorClient.mc.getFramebuffer().beginWrite(false);
    }

    public void endRender(Runnable draw) {
        if (!shouldDraw()) return;

        preDraw();
        draw.run();
        postDraw();

        MeteorClient.mc.getFramebuffer().beginWrite(false);

        GL.bindTexture(framebuffer.getColorAttachment(), 0);

        shader.bind();

        shader.set("u_Size", MeteorClient.mc.getWindow().getFramebufferWidth(), MeteorClient.mc.getWindow().getFramebufferHeight());
        shader.set("u_Texture", 0);
        shader.set("u_Time", glfwGetTime());
        setUniforms();

        PostProcessRenderer.render();
    }

    public void onResized(int width, int height) {
        if (framebuffer == null) return;
        framebuffer.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
    }
}
