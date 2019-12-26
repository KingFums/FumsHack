package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Module.Info(name = "PickaxeBlockAura", category = Module.Category.COMBAT)
public class PickAxeBlockAura extends Module
{
    private Setting<Boolean> echest;
    private Setting<Boolean> shulker;
    private Setting<Boolean> hopper;
    private Setting<Boolean> spawner;
    private Setting<Boolean> dispenser;
    private Setting<Double> range;
    private Setting<Boolean> pickswitch;
    private Setting<Boolean> spacer;
    private int oldSlot;
    private boolean isMining;


    public PickAxeBlockAura() {
        this.hopper = this.register(Settings.b("Hopper Aura", false));
        this.shulker = this.register(Settings.b("Shulker Aura", false));
        this.dispenser = this.register(Settings.b("Dispenser Aura",false));
        this.spawner = this.register(Settings.b("Spawner Aura",false));
        this.echest = this.register(Settings.b("Ender Chest Aura",false));
        this.spacer = this.register(Settings.b(" ", false));
        this.range = this.register(Settings.d("Range", 5.5));
        this.pickswitch = this.register(Settings.b("Auto Tool", true));
        this.oldSlot = -1;
        this.isMining = false;
    }

    @Override
    public void onUpdate() {
        final BlockPos pos = this.getNearestBlock();
        if (pos != null) {
            if (!this.isMining) {
                this.oldSlot = Wrapper.getPlayer().inventory.currentItem;
                this.isMining = false; //Should be true. Changed for testing purposes.
            }
            final float[] angle = calcAngle(Wrapper.getPlayer().getPositionEyes(Wrapper.getMinecraft().getRenderPartialTicks()), new Vec3d((double)(pos.getX() + 0.5f), (double)(pos.getY() + 0.5f), (double)(pos.getZ() + 0.5f)));
            Wrapper.getPlayer().rotationYaw = angle[0];
            Wrapper.getPlayer().rotationYawHead = angle[0];
            Wrapper.getPlayer().rotationPitch = angle[1];
            if (this.canBreak(pos)) {
                if (this.pickswitch.getValue()) {
                    int newSlot = -1;
                    for (int i = 0; i < 9; ++i) {
                        final ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
                        if (stack != ItemStack.EMPTY) {
                            if (stack.getItem() instanceof ItemPickaxe) {
                                newSlot = i;
                                break;
                            }
                        }
                    }
                    if (newSlot != -1) {
                        Wrapper.getPlayer().inventory.currentItem = newSlot;
                    }
                }
                Wrapper.getMinecraft().playerController.onPlayerDamageBlock(pos, Wrapper.getPlayer().getHorizontalFacing());
                Wrapper.getPlayer().swingArm(EnumHand.MAIN_HAND);
            }
        }
        else if (this.pickswitch.getValue() && this.oldSlot != -1) {
            Wrapper.getPlayer().inventory.currentItem = this.oldSlot;
            this.oldSlot = -1;
            this.isMining = false;
        }
    }

    private boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = Wrapper.getWorld().getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, Wrapper.getWorld(), pos) != -1.0f;
    }

    private BlockPos getNearestBlock() {
        Double maxDist = this.range.getValue();
        BlockPos ret = null;
        for (Double x = maxDist; x >= -maxDist; --x) {
            for (Double y = maxDist; y >= -maxDist; --y) {
                for (Double z = maxDist; z >= -maxDist; --z) {
                    final BlockPos pos = new BlockPos(Wrapper.getPlayer().posX + x, Wrapper.getPlayer().posY + y, Wrapper.getPlayer().posZ + z);
                    final double dist = Wrapper.getPlayer().getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());

                    if (this.hopper.getValue());
                    {
                        if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.HOPPER && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.hopper.getValue()) {
                            maxDist = dist;
                            ret = pos;
                        }
                    }

                    if (this.dispenser.getValue());
                    {
                        if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.DISPENSER && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.dispenser.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        }
                    }
                    if (this.echest.getValue());
                    {
                        if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.ENDER_CHEST && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.echest.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        }
                    }
                    if (this.spawner.getValue());
                    {
                        if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.MOB_SPAWNER && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.spawner.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        }
                    }

                    if (this.shulker.getValue());
                    {
                        if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.WHITE_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.ORANGE_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.MAGENTA_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.LIGHT_BLUE_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.YELLOW_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.LIME_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.PINK_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.GRAY_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.SILVER_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.CYAN_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.PURPLE_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.BLUE_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.BROWN_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.GREEN_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.RED_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        } else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.BLACK_SHULKER_BOX && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.shulker.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        }
                    }
                }
            }
        }
        return ret;
    }

    private static float[] calcAngle(final Vec3d from, final Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0;
        final double difZ = to.z - from.z;
        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[] { (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }
}
