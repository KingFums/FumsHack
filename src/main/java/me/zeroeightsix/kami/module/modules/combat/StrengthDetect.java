package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.*;
import me.zeroeightsix.kami.setting.*;
import net.minecraft.init.*;
import me.zeroeightsix.kami.command.*;
import java.util.*;

@Module.Info(name = "StrengthEffectDetect", category = Module.Category.COMBAT, description = "Detects when players have Strength 2")
public class StrengthDetect extends Module
{
    private Setting<Boolean> watermark;
    private Setting<Boolean> color;
    private Set<EntityPlayer> str;
    public static final Minecraft mc;
    
    public StrengthDetect() {
        this.watermark = this.register(Settings.b("Watermark", true));
        this.color = this.register(Settings.b("Color", false));
        this.str = Collections.newSetFromMap(new WeakHashMap<EntityPlayer, Boolean>());
    }
    
    @Override
    public void onUpdate() {
        for (final EntityPlayer player : StrengthDetect.mc.world.playerEntities) {
            if (player.equals((Object)StrengthDetect.mc.player)) {
                continue;
            }
            if (player.isPotionActive(MobEffects.STRENGTH) && !this.str.contains(player)) {
                if (this.watermark.getValue()) {
                    if (this.color.getValue()) {
                        Command.sendChatMessage("&a" + player.getDisplayNameString() + " has drank strength");
                    }
                    else {
                        Command.sendChatMessage(player.getDisplayNameString() + " has drank strength");
                    }
                }
                else if (this.color.getValue()) {
                    Command.sendRawChatMessage("&a" + player.getDisplayNameString() + " has drank strength");
                }
                else {
                    Command.sendRawChatMessage(player.getDisplayNameString() + " has drank strength");
                }
                this.str.add(player);
            }
            if (!this.str.contains(player)) {
                continue;
            }
            if (player.isPotionActive(MobEffects.STRENGTH)) {
                continue;
            }
            if (this.watermark.getValue()) {
                if (this.color.getValue()) {
                    Command.sendChatMessage("&c" + player.getDisplayNameString() + " has ran out of strength");
                }
                else {
                    Command.sendChatMessage(player.getDisplayNameString() + " has ran out of strength");
                }
            }
            else if (this.color.getValue()) {
                Command.sendRawChatMessage("&c" + player.getDisplayNameString() + " has ran out of strength");
            }
            else {
                Command.sendRawChatMessage(player.getDisplayNameString() + " has ran out of strength");
            }
            this.str.remove(player);
        }
    }
    
    static {
        mc = Minecraft.getMinecraft();
    }
}
