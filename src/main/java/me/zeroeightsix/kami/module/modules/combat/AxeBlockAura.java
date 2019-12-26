package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Module.Info(name = "AxeBlockAura", category = Module.Category.COMBAT)
public class AxeBlockAura extends Module
{
    private Setting<Boolean> sign;
    private Setting<Boolean> banner;
    private Setting<Boolean> chest;
    private Setting<Boolean> bed;
    private Setting<Double> range;
    private Setting<Boolean> pickswitch;
    private Setting<Boolean> spacer;
    private int oldSlot;
    private boolean isMining;


    public AxeBlockAura() {
        this.sign = this.register(Settings.b("Sign Aura", false));
        this.banner = this.register(Settings.b("Banner Aura", false));
        this.chest = this.register(Settings.b("Chest Aura",false));
        this.bed = this.register(Settings.b("Bed Aura",false));
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
                            if (stack.getItem() instanceof ItemAxe) {
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

                    if (this.sign.getValue());
                    {
                        if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.STANDING_SIGN && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.sign.getValue()) {
                            maxDist = dist;
                            ret = pos;
                        }
                        else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.WALL_SIGN && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.sign.getValue()) {
                            maxDist = dist;
                            ret = pos;
                        }
                    }

                    if (this.banner.getValue());
                    {
                        if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.STANDING_BANNER && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.banner.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        }
                        else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.WALL_BANNER && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.banner.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        }
                    }
                    if (this.chest.getValue());
                    {
                        if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.TRAPPED_CHEST && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.chest.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        }
                        else if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.CHEST && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.chest.getValue()) {
                            maxDist = dist;
                            ret = pos;
                            this.isMining = true;
                        }
                    }
                    if (this.bed.getValue());
                    {
                        if (dist <= maxDist && Wrapper.getWorld().getBlockState(pos).getBlock() == Blocks.BED && this.canBreak(pos) && pos.getY() >= Wrapper.getPlayer().posY && this.bed.getValue()) {
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
