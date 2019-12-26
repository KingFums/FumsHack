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

@Module.Info(name = "AutoTrap3", category = Module.Category.COMBAT)
public class AutoTrap3 extends Module
{
    BlockPos abovehead;
    BlockPos aboveheadpartner;
    BlockPos aboveheadpartner2;
    BlockPos aboveheadpartner3;
    BlockPos aboveheadpartner4;
    BlockPos floorpartner;
    BlockPos side11;
    BlockPos side22;
    BlockPos side33;
    BlockPos side44;
    BlockPos sideExta1;
    BlockPos sideExta2;
    BlockPos sideExta3;
    BlockPos sideExta4;
    int delay;
    public static EntityPlayer target;
    public static List<EntityPlayer> targets;
    public static float yaw;
    public static float pitch;
    
    public boolean isInBlockRange(final Entity target) {
        return target.getDistance((Entity)AutoTrap3.mc.player) <= 4.0f;
    }
    
    public static boolean canBeClicked(final BlockPos pos) {
        return AutoTrap3.mc.world.getBlockState(pos).getBlock().canCollideCheck(AutoTrap3.mc.world.getBlockState(pos), false);
    }
    
    private static void faceVectorPacket(final Vec3d vec) {
        final double diffX = vec.x - AutoTrap3.mc.player.posX;
        final double diffY = vec.y - AutoTrap3.mc.player.posY + AutoTrap3.mc.player.getEyeHeight();
        final double diffZ = vec.z - AutoTrap3.mc.player.posZ;
        final double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, dist)));
        AutoTrap3.mc.getConnection().sendPacket((Packet)new CPacketPlayer.Rotation(AutoTrap3.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - AutoTrap3.mc.player.rotationYaw), AutoTrap3.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - AutoTrap3.mc.player.rotationPitch), AutoTrap3.mc.player.onGround));
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
        for (final EntityPlayer player : AutoTrap3.mc.world.playerEntities) {
            if (!(player instanceof EntityPlayerSP)) {
                final EntityPlayer p = player;
                if (this.isValid(p)) {
                    AutoTrap3.targets.add(p);
                }
                else {
                    if (!AutoTrap3.targets.contains(p)) {
                        continue;
                    }
                    AutoTrap3.targets.remove(p);
                }
            }
        }
    }
    
    private boolean isStackObby(final ItemStack stack) {
        return stack != null && stack.getItem() == Item.getItemById(49);
    }
    
    private boolean doesHotbarHaveObby() {
        for (int i = 36; i < 45; ++i) {
            final ItemStack stack = AutoTrap3.mc.player.inventoryContainer.getSlot(i).getStack();
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
        return AutoTrap3.mc.world.getBlockState(pos);
    }
    
    public static boolean placeBlockLegit(final BlockPos pos) {
        final Vec3d eyesPos = new Vec3d(AutoTrap3.mc.player.posX, AutoTrap3.mc.player.posY + AutoTrap3.mc.player.getEyeHeight(), AutoTrap3.mc.player.posZ);
        final Vec3d posVec = new Vec3d((Vec3i)pos).add(0.5, 0.5, 0.5);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            if (canBeClicked(neighbor)) {
                final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                    AutoTrap3.mc.playerController.processRightClickBlock(AutoTrap3.mc.player, AutoTrap3.mc.world, neighbor, side.getOpposite(), hitVec, EnumHand.MAIN_HAND);
                    AutoTrap3.mc.player.swingArm(EnumHand.MAIN_HAND);
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
        if (AutoTrap3.mc.player.isHandActive()) {
            return;
        }
        if (!this.isValid(AutoTrap3.target) || AutoTrap3.target == null) {
            this.updateTarget();
        }
        for (final EntityPlayer player : AutoTrap3.mc.world.playerEntities) {
            if (!(player instanceof EntityPlayerSP)) {
                final EntityPlayer e = player;
                if (this.isValid(e) && e.getDistance((Entity)AutoTrap3.mc.player) < AutoTrap3.target.getDistance((Entity)AutoTrap3.mc.player)) {
                    AutoTrap3.target = e;
                    return;
                }
                continue;
            }
        }
        if (this.isValid(AutoTrap3.target) && AutoTrap3.mc.player.getDistance((Entity)AutoTrap3.target) < 4.0f) {
            this.trap(AutoTrap3.target);
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
            this.floorpartner = new BlockPos(player.posX + 1.0, player.posY, player.posZ);
            this.side11 = new BlockPos(player.posX + 1.0, player.posY + 1.0, player.posZ);
            this.sideExta1 = new BlockPos(player.posX + 1.0, player.posY + 1.0, player.posZ + 1.0);
            this.side22 = new BlockPos(player.posX, player.posY + 1.0, player.posZ + 1.0);
            this.sideExta2 = new BlockPos(player.posX - 1.0, player.posY + 1.0, player.posZ + 1.0);
            this.side33 = new BlockPos(player.posX - 1.0, player.posY + 1.0, player.posZ);
            this.sideExta3 = new BlockPos(player.posX + 1.0, player.posY + 1.0, player.posZ - 1.0);
            this.side44 = new BlockPos(player.posX, player.posY + 1.0, player.posZ - 1.0);
            this.sideExta4 = new BlockPos(player.posX - 1.0, player.posY + 1.0, player.posZ - 1.0);
            for (int i = 36; i < 45; ++i) {
                final ItemStack stack = AutoTrap3.mc.player.inventoryContainer.getSlot(i).getStack();
                if (stack != null && this.isStackObby(stack)) {
                    final int oldSlot = AutoTrap3.mc.player.inventory.currentItem;
                    if (AutoTrap3.mc.world.getBlockState(this.abovehead).getMaterial().isReplaceable() || AutoTrap3.mc.world.getBlockState(this.floorpartner).getMaterial().isReplaceable()) {
                        AutoTrap3.mc.player.inventory.currentItem = i - 36;
                        if (AutoTrap3.mc.world.getBlockState(this.floorpartner).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.floorpartner);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.side11).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side11);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.sideExta1).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.sideExta1);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.side22).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side22);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.sideExta2).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.sideExta2);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.side33).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side33);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.sideExta3).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.sideExta3);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.side44).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.side44);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.aboveheadpartner).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.aboveheadpartner);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.abovehead).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.abovehead);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.aboveheadpartner2).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.aboveheadpartner2);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.aboveheadpartner3).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.aboveheadpartner3);
                        }
                        if (AutoTrap3.mc.world.getBlockState(this.aboveheadpartner4).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.aboveheadpartner4);
                        }
                        AutoTrap3.mc.player.inventory.currentItem = oldSlot;
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
        AutoTrap3.yaw = AutoTrap3.mc.player.rotationYaw;
        AutoTrap3.pitch = AutoTrap3.mc.player.rotationPitch;
        AutoTrap3.target = null;
    }
    
    public void updateTarget() {
        for (final EntityPlayer player : AutoTrap3.mc.world.playerEntities) {
            if (!(player instanceof EntityPlayerSP)) {
                final EntityPlayer entity = player;
                if (entity instanceof EntityPlayerSP || !this.isValid(entity)) {
                    continue;
                }
                AutoTrap3.target = entity;
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
