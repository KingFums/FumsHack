// 
// Decompiled by Procyon v0.5.36
// 

package me.zeroeightsix.kami.module.modules.combat;

import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import me.zeroeightsix.kami.command.Command;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockContainer;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import me.zeroeightsix.kami.util.Wrapper;
import net.minecraft.util.math.Vec3i;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Settings;
import java.util.Collections;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import me.zeroeightsix.kami.setting.Setting;
import net.minecraft.block.Block;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "AutoBarrier2", category = Module.Category.COMBAT)
public class AutoBarrier2 extends Module
{
    private final Vec3d[] surroundList;
    private final Vec3d[] surroundListFull;
    private final List<Block> obsidian;
    private Setting<Boolean> toggleable;
    private Setting<Boolean> slowmode;
    private Setting<Boolean> full;
    private Vec3d[] surroundTargets;
    private BlockPos basePos;
    private boolean slowModeSwitch;
    private int blocksPerTick;
    private int offsetStep;
    private int oldSlot;
    
    public AutoBarrier2() {
        this.surroundList = new Vec3d[] { new Vec3d(0.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 1.0, -1.0) };
        this.surroundListFull = new Vec3d[] { new Vec3d(0.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(-1.0, 1.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(1.0, 1.0, -1.0) };
        this.obsidian = Collections.singletonList(Blocks.OBSIDIAN);
        this.toggleable = this.register(Settings.b("Toggleable", true));
        this.slowmode = this.register(Settings.b("Slow", false));
        this.full = this.register(Settings.b("Full", false));
        this.slowModeSwitch = false;
        this.blocksPerTick = 3;
        this.offsetStep = 0;
        this.oldSlot = 0;
    }
    
    @Override
    public void onUpdate() {
        if (this.isDisabled() || AutoBarrier2.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        if (this.slowModeSwitch) {
            this.slowModeSwitch = false;
            return;
        }
        if (this.offsetStep == 0) {
            this.init();
        }
        for (int i = 0; i < this.blocksPerTick; ++i) {
            if (this.offsetStep >= this.surroundTargets.length) {
                this.end();
                return;
            }
            final Vec3d offset = this.surroundTargets[this.offsetStep];
            this.placeBlock(new BlockPos((Vec3i)this.basePos.add(offset.x, offset.y, offset.z)));
            ++this.offsetStep;
        }
        this.slowModeSwitch = true;
    }
    
    private void placeBlock(final BlockPos blockPos) {
        if (!Wrapper.getWorld().getBlockState(blockPos).getMaterial().isReplaceable()) {
            return;
        }
        int newSlot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (!BlockInteractionHelper.blackList.contains(block)) {
                        if (!(block instanceof BlockContainer)) {
                            if (Block.getBlockFromItem(stack.getItem()).getDefaultState().isFullBlock()) {
                                if (!(((ItemBlock)stack.getItem()).getBlock() instanceof BlockFalling) || !Wrapper.getWorld().getBlockState(blockPos.down()).getMaterial().isReplaceable()) {
                                    if (this.obsidian.contains(block)) {
                                        newSlot = i;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (newSlot == -1) {
            if (!this.toggleable.getValue()) {
                Command.sendChatMessage("AutoBarrier: No Obsidian in Hotbar!");
            }
            this.end();
            return;
        }
        Wrapper.getPlayer().inventory.currentItem = newSlot;
        if (!BlockInteractionHelper.checkForNeighbours(blockPos)) {
            return;
        }
        BlockInteractionHelper.placeBlockScaffold(blockPos);
    }
    
    private void init() {
        this.basePos = new BlockPos(AutoBarrier2.mc.player.getPositionVector()).down();
        if (this.slowmode.getValue()) {
            this.blocksPerTick = 1;
        }
        if (this.full.getValue()) {
            this.surroundTargets = this.surroundListFull;
        }
        else {
            this.surroundTargets = this.surroundList;
        }
    }
    
    private void end() {
        this.offsetStep = 0;
        if (!this.toggleable.getValue()) {
            this.disable();
        }
    }
    
    @Override
    protected void onEnable() {
        AutoBarrier2.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoBarrier2.mc.player, CPacketEntityAction.Action.START_SNEAKING));
        this.oldSlot = Wrapper.getPlayer().inventory.currentItem;
    }
    
    @Override
    protected void onDisable() {
        AutoBarrier2.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)AutoBarrier2.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        Wrapper.getPlayer().inventory.currentItem = this.oldSlot;
    }
}
