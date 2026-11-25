package org.dreamfinity.beamingdrops;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import cpw.mods.fml.relauncher.FMLInjectionData;

public class ConfigTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Field minecraftHome;
    private Object previousMinecraftHome;

    @Before
    public void provideLauncherDirectoryForForgeConfiguration() throws Exception {
        // Forge's config constructor expects the directory normally supplied by LaunchWrapper.
        minecraftHome = FMLInjectionData.class.getDeclaredField("minecraftHome");
        minecraftHome.setAccessible(true);
        previousMinecraftHome = minecraftHome.get(null);
        minecraftHome.set(null, temporaryFolder.getRoot());
    }

    @After
    public void restoreLauncherDirectory() throws Exception {
        minecraftHome.set(null, previousMinecraftHome);
    }

    @Test
    public void existingConfigKeepsItemRulesAndGetsEmptyColorBlacklist() throws Exception {
        File file = temporaryFolder.newFile("BeamingDropMixinsEarly.cfg");
        String legacy = "\"beam settings\" {\n"
            + "    B:needRenderBeam=true\n"
            + "    B:isWhitelist=true\n"
            + "    S:itemBlackList <\n        minecraft:wool:14\n     >\n"
            + "    S:beamHeight=6.0\n}\n";
        Files.write(file.toPath(), legacy.getBytes(StandardCharsets.UTF_8));

        Config.loadEarlyMixinConfig(file);

        assertTrue(Config.isWhitelist);
        assertArrayEquals(new String[] {"minecraft:wool:14"}, Config.itemBlackList);
        assertEquals(6.0, Config.beamHeight, 0.001);
        assertFalse(Config.isColorWhitelist);
        assertEquals(0, Config.colorBlackList.length);
        String saved = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        assertTrue(saved.contains("colorBlackList"));
        assertTrue(saved.contains("isColorWhitelist=false"));
    }

    @Test
    public void colorWhitelistAndDefaultAllItemsAreReadFromForgeConfig() throws Exception {
        File file = temporaryFolder.newFile("colors.cfg");
        String selectedColors = "\"beam settings\" {\n"
            + "    B:isColorWhitelist=true\n"
            + "    S:colorBlackList <\n        blue\n        light_purple\n     >\n}\n";
        Files.write(file.toPath(), selectedColors.getBytes(StandardCharsets.UTF_8));

        Config.loadEarlyMixinConfig(file);

        assertFalse(Config.isWhitelist);
        assertEquals(0, Config.itemBlackList.length);
        assertTrue(Config.isColorWhitelist);
        assertArrayEquals(new String[] {"blue", "light_purple"}, Config.colorBlackList);
    }
}
