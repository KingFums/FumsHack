// 
// Decompiled by Procyon v0.5.36
// 

package me.zeroeightsix.kami.module.modules.combat;

import java.util.concurrent.TimeUnit;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.Iterator;
import net.minecraft.client.entity.EntityPlayerSP;
import me.zeroeightsix.kami.util.Friends;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "AutoTrap2", category = Module.Category.COMBAT)
public class AutoTrap2 extends Module
{
    BlockPos abovehead;
    BlockPos aboveheadpartner;
    BlockPos aboveheadpartner2;
    BlockPos aboveheadpartner3;
    BlockPos aboveheadpartner4;
    BlockPos side1;
    BlockPos side2;
    BlockPos side3;
    BlockPos side4;
    BlockPos side11;
    BlockPos side22;
    BlockPos side33;
    BlockPos side44;
    int delay;
    public static EntityPlayer target;
    public static List<EntityPlayer> targets;
    public static float yaw;
    public static float pitch;
    
    public boolean isInBlockRange(final Entity target) {
        return target.getDistance((Entity)AutoTrap2.mc.player) <= 4.0f;
    }
    
    public static boolean canBeClicked(final BlockPos pos) {
        return AutoTrap2.mc.world.getBlockState(pos).getBlock().canCollideCheck(AutoTrap2.mc.world.getBlockState(pos), false);
    }
    
    private static void faceVectorPacket(final Vec3d vec) {
        final double diffX = vec.x - AutoTrap2.mc.player.posX;
        final double diffY = vec.y - AutoTrap2.mc.player.posY + AutoTrap2.mc.player.getEyeHeight();
        final double diffZ = vec.z - AutoTrap2.mc.player.posZ;
        final double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, dist)));
        AutoTrap2.mc.getConnection().sendPacket((Packet)new CPacketPlayer.Rotation(AutoTrap2.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - AutoTrap2.mc.player.rotationYaw), AutoTrap2.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - AutoTrap2.mc.player.rotationPitch), AutoTrap2.mc.player.onGround));
    }
    
    public boolean isValid(final EntityPlayer entity) {
        if (entity instanceof EntityPlayer) {
            final EntityPlayer animal = entity;
            if (this.isInBlockRange((Entity)animal) && animal.getHealth() > 0.0f && !animal.isDead && !animal.getName().startsWith("Body #") && !Friends.isFriend(animal.getName())) {
                return true;
            }
        }
        return false;
    }
    
    public void loadTargets() {
        for (final EntityPlayer player : AutoTrap2.mc.world.playerEntities) {
            if (!(player instanceof EntityPlayerSP)) {
                final EntityPlayer p = player;
                if (this.isValid(p)) {
                    AutoTrap2.targets.add(p);
                }
                else {
                    if (!AutoTrap2.targets.contains(p)) {
                        continue;
                    }
                    AutoTrap2.targets.remove(p);
                }
            }
        }
    }
    
    private boolean isStackObby(final ItemStack stack) {
        return stack != null && stack.getItem() == Item.getItemById(49);
    }
    
    private boolean doesHotbarHaveObby() {
        for (int i = 36; i < 45; ++i) {
            final ItemStack stack = AutoTrap2.mc.player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && this.isStackObby(stack)) {
                return true;
            }
        }
        return false;
    }
    
    public static Block getBlock(final BlockPos pos) {
        return getState(pos).getBlock();
    }
    
    public static IBlockState getState(final BlockPos pos) {
        return AutoTrap2.mc.world.getBlockState(pos);
    }
    
    public static boolean placeBlockLegit(final BlockPos pos) {
        final Vec3d eyesPos = new Vec3d(AutoTrap2.mc.player.posX, AutoTrap2.mc.player.posY + AutoTrap2.mc.player.getEyeHeight(), AutoTrap2.mc.player.posZ);
        final Vec3d posVec = new Vec3d((Vec3i)pos).add(0.5, 0.5, 0.5);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            if (canBeClicked(neighbor)) {
                final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                    AutoTrap2.mc.playerController.processRightClickBlock(AutoTrap2.mc.player, AutoTrap2.mc.world, neighbor, side.getOpposite(), hitVec, EnumHand.MAIN_HAND);
                    AutoTrap2.mc.player.swingArm(EnumHand.MAIN_HAND);
                    try {
                        TimeUnit.MILLISECONDS.sleep(10L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void onUpdate() {
        if (AutoTrap2.mc.player.isHandActive()) {
            return;
        }
        if (!this.isValid(AutoTrap2.target) || AutoTrap2.target == null) {
            this.updateTarget();
        }
        for (final EntityPlayer player : AutoTrap2.mc.world.playerEntities) {
            if (!(player instanceof EntityPlayerSP)) {
                final EntityPlayer e = player;
                if (this.isValid(e) && e.getDistance((Entity)AutoTrap2.mc.player) < AutoTrap2.target.getDistance((Entity)AutoTrap2.mc.player)) {
                    AutoTrap2.target = e;
                    return;
                }
                continue;
            }
        }
        if (this.isValid(AutoTrap2.target) && AutoTrap2.mc.player.getDistance((Entity)AutoTrap2.target) < 4.0f) {
            this.trap(AutoTrap2.target);
        }
        else {
            this.delay = 0;
        }
    }
    
    public static double roundToHalf(final double d) {
        return Math.round(d * 2.0) / 2.0;
    }
    
    public void onEnable() {
        this.delay = 0;
    }
    
    private void trap(final EntityPlayer player) {
        if (player.moveForward == 0.0 && player.moveStrafing == 0.0 && player.moveVertical == 0.0) {
            ++this.delay;
        }
        if (player.moveForward != 0.0 || player.moveStrafing != 0.0 || player.moveVertical != 0.0) {
            this.delay = 0;
        }
        if (!this.doesHotbarHaveObby()) {
            this.delay = 0;
        }
        if (this.delay == 20 && this.doesHotbarHaveObby()) {
            this.abovehead = new BlockPos(player.posX, player.posY + 2.0, player.posZ);
            this.aboveheadpartner = new BlockPos(player.posX + 1.0, player.posY + 2.0, player.posZ);
            this.aboveheadpartner2 = new BlockPos(player.posX - 1.0, player.posY + 2.0, player.posZ);
            this.aboveheadpartner3 = new BlockPos(player.posX, player.posY + 2.0, player.posZ + 1.0);
            this.aboveheadpartner4 = new BlockPos(player.posX, player.posY + 2.0, player.posZ - 1.0);
            this.side1 = new BlockPos(player.posX + 1.0, player.posY, player.posZ);
            this.side2 = new BlockPos(player.posX, player.posY, player.posZ + 1.0);
            this.side3 = new BlockPos(player.posX - 1.0, player.posY, player.posZ);
            this.side4 = new BlockPos(player.posX, player.posY, player.posZ - 1.0);
            this.side11 = new BlockPos(player.posX + 1.0, player.posY + 1.0, player.posZ);
            this.side22 = new BlockPos(player.posX, player.posY + 1.0, player.posZ + 1.0);
            this.side33 = new BlockPos(player.posX - 1.0, player.posY + 1.0, player.posZ);
            this.side44 = new BlockPos(player.posX, player.posY + 1.0, player.posZ - 1.0);
            for (int i = 36; i < 45; ++i) {
                final ItemStack stack = AutoTrap2.mc.player.inventoryContainer.getSlot(i).getStack();
                if (stack != null && this.isStackObby(stack)) {
                    final int oldSlot = AutoTrap2.mc.player.inventory.currentItem;
                    if (AutoTrap2.mc.world.getBlockState(this.abovehead).getMaterial().isReplaceable() || AutoTrap2.mc.world.getBlockState(this.side1).getMaterial().isReplaceable() || AutoTrap2.mc.world.getBlockState(this.side2).getMaterial().isReplaceable() || AutoTrap2.mc.world.getBlockState(this.side3).getMaterial().isReplaceable() || AutoTrap2.mc.world.getBlockState(this.side4).getMaterial().isReplaceable()) {
                        AutoTrap2.mc.player.inventory.currentItem = i - 36;
                        if (AutoTrap2.mc.world.getBlockState(this.side1).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side1);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.side2).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side2);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.side3).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side3);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.side4).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side4);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.side11).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side11);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.side22).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side22);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.side33).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side33);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.side44).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side44);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.aboveheadpartner).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.aboveheadpartner);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.abovehead).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.abovehead);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.aboveheadpartner2).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.aboveheadpartner2);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.aboveheadpartner3).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.aboveheadpartner3);
                        }
                        if (AutoTrap2.mc.world.getBlockState(this.aboveheadpartner4).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.aboveheadpartner4);
                        }
                        AutoTrap2.mc.player.inventory.currentItem = oldSlot;
                        this.delay = 0;
                        break;
                    }
                    this.delay = 0;
                }
                this.delay = 0;
            }
        }
    }
    
    public void onDisable() {
        this.delay = 0;
        AutoTrap2.yaw = AutoTrap2.mc.player.rotationYaw;
        AutoTrap2.pitch = AutoTrap2.mc.player.rotationPitch;
        AutoTrap2.target = null;
    }
    
    public void updateTarget() {
        for (final EntityPlayer player : AutoTrap2.mc.world.playerEntities) {
            if (!(player instanceof EntityPlayerSP)) {
                final EntityPlayer entity = player;
                if (entity instanceof EntityPlayerSP || !this.isValid(entity)) {
                    continue;
                }
                AutoTrap2.target = entity;
            }
        }
    }
    
    public EnumFacing getEnumFacing(final float posX, final float posY, final float posZ) {
        return EnumFacing.getFacingFromVector(posX, posY, posZ);
    }
    
    public BlockPos getBlockPos(final double x, final double y, final double z) {
        return new BlockPos(x, y, z);
    }
}
