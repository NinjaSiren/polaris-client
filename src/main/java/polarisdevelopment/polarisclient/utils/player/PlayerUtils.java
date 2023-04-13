/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package polarisdevelopment.polarisclient.utils.player;

import baritone.api.BaritoneAPI;
import baritone.api.utils.Rotation;
import polarisdevelopment.polarisclient.mixininterface.IVec3d;
import polarisdevelopment.polarisclient.systems.config.Config;
import polarisdevelopment.polarisclient.systems.friends.Friends;
import polarisdevelopment.polarisclient.systems.modules.Modules;
import polarisdevelopment.polarisclient.systems.modules.movement.NoFall;
import polarisdevelopment.polarisclient.utils.Utils;
import polarisdevelopment.polarisclient.utils.entity.EntityUtils;
import polarisdevelopment.polarisclient.utils.misc.BaritoneUtils;
import polarisdevelopment.polarisclient.utils.misc.text.TextUtils;
import polarisdevelopment.polarisclient.utils.render.color.Color;
import polarisdevelopment.polarisclient.utils.world.Dimension;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;
import polarisdevelopment.polarisclient.MeteorClient;

public class PlayerUtils {
    private static final double diagonal = 1 / Math.sqrt(2);
    private static final Vec3d horizontalVelocity = new Vec3d(0, 0, 0);

    private static final Color color = new Color();

    public static Color getPlayerColor(PlayerEntity entity, Color defaultColor) {
        if (Friends.get().isFriend(entity)) {
            return color.set(Config.get().friendColor.get()).a(defaultColor.a);
        }

        if (Config.get().useTeamColor.get() && !color.set(TextUtils.getMostPopularColor(entity.getDisplayName())).equals(Utils.WHITE)) {
            return color.a(defaultColor.a);
        }

        return defaultColor;
    }

    public static Vec3d getHorizontalVelocity(double bps) {
        float yaw = MeteorClient.mc.player.getYaw();

        if (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            Rotation target = BaritoneUtils.getTarget();
            if (target != null) yaw = target.getYaw();
        }

        Vec3d forward = Vec3d.fromPolar(0, yaw);
        Vec3d right = Vec3d.fromPolar(0, yaw + 90);
        double velX = 0;
        double velZ = 0;

        boolean a = false;
        if (MeteorClient.mc.player.input.pressingForward) {
            velX += forward.x / 20 * bps;
            velZ += forward.z / 20 * bps;
            a = true;
        }
        if (MeteorClient.mc.player.input.pressingBack) {
            velX -= forward.x / 20 * bps;
            velZ -= forward.z / 20 * bps;
            a = true;
        }

        boolean b = false;
        if (MeteorClient.mc.player.input.pressingRight) {
            velX += right.x / 20 * bps;
            velZ += right.z / 20 * bps;
            b = true;
        }
        if (MeteorClient.mc.player.input.pressingLeft) {
            velX -= right.x / 20 * bps;
            velZ -= right.z / 20 * bps;
            b = true;
        }

        if (a && b) {
            velX *= diagonal;
            velZ *= diagonal;
        }

        ((IVec3d) horizontalVelocity).setXZ(velX, velZ);
        return horizontalVelocity;
    }

    public static void centerPlayer() {
        double x = MathHelper.floor(MeteorClient.mc.player.getX()) + 0.5;
        double z = MathHelper.floor(MeteorClient.mc.player.getZ()) + 0.5;
        MeteorClient.mc.player.setPosition(x, MeteorClient.mc.player.getY(), z);
        MeteorClient.mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(MeteorClient.mc.player.getX(), MeteorClient.mc.player.getY(), MeteorClient.mc.player.getZ(), MeteorClient.mc.player.isOnGround()));
    }

    public static boolean canSeeEntity(Entity entity) {
        Vec3d vec1 = new Vec3d(0, 0, 0);
        Vec3d vec2 = new Vec3d(0, 0, 0);

        ((IVec3d) vec1).set(MeteorClient.mc.player.getX(), MeteorClient.mc.player.getY() + MeteorClient.mc.player.getStandingEyeHeight(), MeteorClient.mc.player.getZ());
        ((IVec3d) vec2).set(entity.getX(), entity.getY(), entity.getZ());
        boolean canSeeFeet = MeteorClient.mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, MeteorClient.mc.player)).getType() == HitResult.Type.MISS;

        ((IVec3d) vec2).set(entity.getX(), entity.getY() + entity.getStandingEyeHeight(), entity.getZ());
        boolean canSeeEyes = MeteorClient.mc.world.raycast(new RaycastContext(vec1, vec2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, MeteorClient.mc.player)).getType() == HitResult.Type.MISS;

        return canSeeFeet || canSeeEyes;
    }

    public static float[] calculateAngle(Vec3d target) {
        Vec3d eyesPos = new Vec3d(MeteorClient.mc.player.getX(), MeteorClient.mc.player.getY() + MeteorClient.mc.player.getEyeHeight(MeteorClient.mc.player.getPose()), MeteorClient.mc.player.getZ());

        double dX = target.x - eyesPos.x;
        double dY = (target.y - eyesPos.y) * -1.0D;
        double dZ = target.z - eyesPos.z;

        double dist = Math.sqrt(dX * dX + dZ * dZ);

        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dZ, dX)) - 90.0D), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dY, dist)))};
    }

    public static boolean shouldPause(boolean ifBreaking, boolean ifEating, boolean ifDrinking) {
        if (ifBreaking && MeteorClient.mc.interactionManager.isBreakingBlock()) return true;
        if (ifEating && (MeteorClient.mc.player.isUsingItem() && (MeteorClient.mc.player.getMainHandStack().getItem().isFood() || MeteorClient.mc.player.getOffHandStack().getItem().isFood()))) return true;
        return ifDrinking && (MeteorClient.mc.player.isUsingItem() && (MeteorClient.mc.player.getMainHandStack().getItem() instanceof PotionItem || MeteorClient.mc.player.getOffHandStack().getItem() instanceof PotionItem));
    }

    public static boolean isMoving() {
        return MeteorClient.mc.player.forwardSpeed != 0 || MeteorClient.mc.player.sidewaysSpeed != 0;
    }

    public static boolean isSprinting() {
        return MeteorClient.mc.player.isSprinting() && (MeteorClient.mc.player.forwardSpeed != 0 || MeteorClient.mc.player.sidewaysSpeed != 0);
    }

    public static boolean isInHole(boolean doubles) {
        if (!Utils.canUpdate()) return false;

        BlockPos blockPos = MeteorClient.mc.player.getBlockPos();
        int air = 0;

        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP) continue;

            BlockState state = MeteorClient.mc.world.getBlockState(blockPos.offset(direction));

            if (state.getBlock().getBlastResistance() < 600) {
                if (!doubles || direction == Direction.DOWN) return false;

                air++;

                for (Direction dir : Direction.values()) {
                    if (dir == direction.getOpposite() || dir == Direction.UP) continue;

                    BlockState blockState1 = MeteorClient.mc.world.getBlockState(blockPos.offset(direction).offset(dir));

                    if (blockState1.getBlock().getBlastResistance() < 600) {
                        return false;
                    }
                }
            }
        }

        return air < 2;
    }

    public static double possibleHealthReductions() {
        return possibleHealthReductions(true, true);
    }

    public static double possibleHealthReductions(boolean entities, boolean fall) {
        double damageTaken = 0;

        if (entities) {
            for (Entity entity : MeteorClient.mc.world.getEntities()) {
                // Check for end crystals
                if (entity instanceof EndCrystalEntity && damageTaken < DamageUtils.crystalDamage(MeteorClient.mc.player, entity.getPos())) {
                    damageTaken = DamageUtils.crystalDamage(MeteorClient.mc.player, entity.getPos());
                }
                // Check for players holding swords
                else if (entity instanceof PlayerEntity && damageTaken < DamageUtils.getSwordDamage((PlayerEntity) entity, true)) {
                    if (!Friends.get().isFriend((PlayerEntity) entity) && isWithin(entity, 5)) {
                        if (((PlayerEntity) entity).getActiveItem().getItem() instanceof SwordItem) {
                            damageTaken = DamageUtils.getSwordDamage((PlayerEntity) entity, true);
                        }
                    }
                }
            }

            // Check for beds if in nether
            if (PlayerUtils.getDimension() != Dimension.Overworld) {
                for (BlockEntity blockEntity : Utils.blockEntities()) {
                    BlockPos bp = blockEntity.getPos();
                    Vec3d pos = new Vec3d(bp.getX(), bp.getY(), bp.getZ());

                    if (blockEntity instanceof BedBlockEntity && damageTaken < DamageUtils.bedDamage(MeteorClient.mc.player, pos)) {
                        damageTaken = DamageUtils.bedDamage(MeteorClient.mc.player, pos);
                    }
                }
            }
        }

        // Check for fall distance with water check
        if (fall) {
            if (!Modules.get().isActive(NoFall.class) && MeteorClient.mc.player.fallDistance > 3) {
                double damage = MeteorClient.mc.player.fallDistance * 0.5;

                if (damage > damageTaken && !EntityUtils.isAboveWater(MeteorClient.mc.player)) {
                    damageTaken = damage;
                }
            }
        }

        return damageTaken;
    }

    public static double distanceTo(Entity entity) {
        return distanceTo(entity.getX(), entity.getY(), entity.getZ());
    }

    public static double distanceTo(BlockPos blockPos) {
        return distanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static double distanceTo(Vec3d vec3d) {
        return distanceTo(vec3d.getX(), vec3d.getY(), vec3d.getZ());
    }

    public static double distanceTo(double x, double y, double z) {
        return Math.sqrt(squaredDistanceTo(x, y, z));
    }

    public static double squaredDistanceTo(Entity entity) {
        return squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ());
    }

    public static double squaredDistanceTo(BlockPos blockPos) {
        return squaredDistanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static double squaredDistanceTo(double x, double y, double z) {
        return squaredDistance(MeteorClient.mc.player.getX(), MeteorClient.mc.player.getY(), MeteorClient.mc.player.getZ(), x, y, z);
    }

    public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        float f = (float) (x1 - x2);
        float g = (float) (y1 - y2);
        float h = (float) (z1 - z2);
        return org.joml.Math.fma(f, f, org.joml.Math.fma(g, g, h * h));
    }

    public static boolean isWithin(Entity entity, double r) {
        return squaredDistanceTo(entity.getX(), entity.getY(), entity.getZ()) <= r * r;
    }

    public static boolean isWithin(Vec3d vec3d, double r) {
        return squaredDistanceTo(vec3d.getX(), vec3d.getY(), vec3d.getZ()) <= r * r;
    }

    public static boolean isWithin(BlockPos blockPos, double r) {
        return squaredDistanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ()) <= r * r;
    }

    public static boolean isWithin(double x, double y, double z, double r) {
        return squaredDistanceTo(x, y, z) <= r * r;
    }

    public static double distanceToCamera(double x, double y, double z) {
        return Math.sqrt(squaredDistanceToCamera(x, y, z));
    }

    public static double distanceToCamera(Entity entity) {
        return distanceToCamera(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
    }

    public static double squaredDistanceToCamera(double x, double y, double z) {
        Vec3d cameraPos = MeteorClient.mc.gameRenderer.getCamera().getPos();
        return squaredDistance(cameraPos.x, cameraPos.y, cameraPos.z, x, y, z);
    }

    public static double squaredDistanceToCamera(Entity entity) {
        return squaredDistanceToCamera(entity.getX(), entity.getY() + entity.getEyeHeight(entity.getPose()), entity.getZ());
    }

    public static boolean isWithinCamera(Entity entity, double r) {
        return squaredDistanceToCamera(entity.getX(), entity.getY(), entity.getZ()) <= r * r;
    }

    public static boolean isWithinCamera(Vec3d vec3d, double r) {
        return squaredDistanceToCamera(vec3d.getX(), vec3d.getY(), vec3d.getZ()) <= r * r;
    }

    public static boolean isWithinCamera(BlockPos blockPos, double r) {
        return squaredDistanceToCamera(blockPos.getX(), blockPos.getY(), blockPos.getZ()) <= r * r;
    }

    public static boolean isWithinCamera(double x, double y, double z, double r) {
        return squaredDistanceToCamera(x, y, z) <= r * r;
    }

    public static boolean isWithinReach(Entity entity) {
        return isWithinReach(entity.getX(), entity.getY(), entity.getZ());
    }

    public static boolean isWithinReach(Vec3d vec3d) {
        return isWithinReach(vec3d.getX(), vec3d.getY(), vec3d.getZ());
    }

    public static boolean isWithinReach(BlockPos blockPos) {
        return isWithinReach(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static boolean isWithinReach(double x, double y, double z) {
        return squaredDistance(MeteorClient.mc.player.getX(), MeteorClient.mc.player.getEyeY(), MeteorClient.mc.player.getZ(), x, y, z) <= MeteorClient.mc.interactionManager.getReachDistance() * MeteorClient.mc.interactionManager.getReachDistance();
    }

    public static Dimension getDimension() {
        if (MeteorClient.mc.world == null) return Dimension.Overworld;

        return switch (MeteorClient.mc.world.getRegistryKey().getValue().getPath()) {
            case "the_nether" -> Dimension.Nether;
            case "the_end" -> Dimension.End;
            default -> Dimension.Overworld;
        };
    }

    public static GameMode getGameMode() {
        PlayerListEntry playerListEntry = MeteorClient.mc.getNetworkHandler().getPlayerListEntry(MeteorClient.mc.player.getUuid());
        if (playerListEntry == null) return GameMode.SPECTATOR;
        return playerListEntry.getGameMode();
    }

    public static double getTotalHealth() {
        return MeteorClient.mc.player.getHealth() + MeteorClient.mc.player.getAbsorptionAmount();
    }

    public static boolean isAlive() {
        return MeteorClient.mc.player.isAlive() && !MeteorClient.mc.player.isDead();
    }

    public static int getPing() {
        if (MeteorClient.mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = MeteorClient.mc.getNetworkHandler().getPlayerListEntry(MeteorClient.mc.player.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }
}
