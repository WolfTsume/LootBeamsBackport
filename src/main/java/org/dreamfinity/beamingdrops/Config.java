package org.dreamfinity.beamingdrops;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
    private static final String COMMON = "Beam settings";
    public static boolean needRenderBeam = true;
    public static boolean useHighResBeam = true;
    public static boolean useRotatingBeam = true;
    public static boolean useFloatingBeam = true;
    public static double beamDiameter = 0.25;
    public static double beamHeight = 4.0;
    public static String[] itemBlackList = new String[0];
    public static boolean isWhitelist = false;
    public static String[] colorBlackList = new String[0];
    public static boolean isColorWhitelist = false;

    public static boolean useDepthMask = false;

    public static void loadEarlyMixinConfig(File configFile) {
        Configuration config = new Configuration(configFile);

        needRenderBeam = config.getBoolean("needRenderBeam", COMMON, true,  "Render beam above items or not");
        useHighResBeam = config.getBoolean("useHighResBeam", COMMON, true,  "Use high-res (256x256) beam texture or low-res(16x16)");
        useRotatingBeam = config.getBoolean("useRotatingBeam", COMMON, true,  "Rotate beam over time (like beacon's beam)");
        useFloatingBeam = config.getBoolean("useFloatingBeam", COMMON, true,  "Slightly float beam over time");
        beamDiameter = config.getFloat("beamDiameter", COMMON, 0.25f, 0.05f, 3.0f, "Beam diameter");
        beamHeight = config.getFloat("beamHeight", COMMON, 4.0f, 0.5f, 32.0f, "Beam height (in blocks)");
        itemBlackList = config.getStringList("itemBlackList", COMMON, new String[] {},
            "Item ID filter: modid:item matches ALL metadata; modid:item:meta matches one variant; "
                + "modid:item:* matches ALL metadata. ore:oreName is also supported. "
                + "Empty blacklist allows all items; empty whitelist allows none.");
        isWhitelist = config.getBoolean("isWhitelist", COMMON, false, "If the beam blacklist should be used as a whitelist instead");
        colorBlackList = config.getStringList("colorBlackList", COMMON, new String[] {},
            "Beam color filter, based on the parsed name color (or rarity if unformatted). "
                + "Accepts Minecraft color names (white, blue, light_purple, gold, etc.), codes (0-f), "
                + "or RGB hex (#5555FF). White/common items are allowed by default. "
                + "Both the item filter and color filter must allow an item to render its beam.");
        isColorWhitelist = config.getBoolean("isColorWhitelist", COMMON, false,
            "Use colorBlackList as a whitelist. Empty blacklist allows all colors; empty whitelist allows none.");
        useDepthMask = config.getBoolean("useDepthMask", COMMON, false,
            "Write beam fragments to the depth buffer. Usually leave false for translucent beams. "
                + "Depth testing against blocks remains enabled in either mode.");

        if(config.hasChanged()) {
            config.save();
        }
    }
}
