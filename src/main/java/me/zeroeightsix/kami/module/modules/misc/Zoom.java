package me.zeroeightsix.kami.module.modules.misc;

import net.minecraft.client.settings.GameSettings;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "Zoom", description = "An Optifine zoom alternative", category = Module.Category.RENDER)
public class Zoom extends Module
{
    private float maxzoom;
    private Setting<Float> zoom;
    public float fov;
    
    public Zoom() {
        this.maxzoom = 12.0f;
        this.zoom = this.register((Setting<Float>)Settings.floatBuilder("Range").withMinimum(1.0f).withMaximum(this.maxzoom).withValue(1.0f).build());
        this.fov = -1.0f;
    }
    
    @Override
    public void onUpdate() {
        final float zoom = this.maxzoom - this.zoom.getValue();
        if (Zoom.mc.gameSettings.fovSetting > zoom) {
            for (int i = 0; i < 100; ++i) {
                if (Zoom.mc.gameSettings.fovSetting > zoom) {
                    final GameSettings gameSettings = Zoom.mc.gameSettings;
                    gameSettings.fovSetting -= 0.1f;
                }
            }
        }
    }
    
    public void onEnable() {
        if (this.fov == -1.0f || Zoom.mc.gameSettings.fovSetting == this.fov) {
            this.fov = Zoom.mc.gameSettings.fovSetting;
        }
    }
    
    public void onDisable() {
        Zoom.mc.gameSettings.fovSetting = this.fov;
    }
}
