package org.dreamfinity.beamingdrops;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

public class BeamColorsTest {

    @Test
    public void resetAndStylesBeforeColorDoNotHideBlueOrPurple() {
        assertEquals(new Color(0x5555FF), BeamColors.fromDisplayName("\u00a7r\u00a79Blue item\u00a7r", Color.WHITE));
        assertEquals(new Color(0xFF55FF), BeamColors.fromDisplayName("\u00a7l\u00a7dPurple item", Color.WHITE));
        assertEquals(new Color(0xFF55FF), BeamColors.fromDisplayName("\u00a7r\u00a7D\u00a7oPurple item\u00a7r", Color.WHITE));
    }

    @Test
    public void effectiveColorAtFirstVisibleCharacterWins() {
        assertEquals(new Color(0xFFAA00), BeamColors.fromDisplayName("\u00a76\u00a7lGold\u00a7r suffix", Color.WHITE));
        assertEquals(new Color(0x5555FF), BeamColors.fromDisplayName("\u00a7c\u00a79Blue", Color.WHITE));
        assertEquals(Color.WHITE, BeamColors.fromDisplayName("\u00a79\u00a7rWhite", Color.MAGENTA));
        assertEquals(new Color(0xFF55FF), BeamColors.fromDisplayName("  \u00a7dPurple", Color.WHITE));
    }

    @Test
    public void uncoloredAndStyleOnlyNamesUseRarity() {
        Color rare = new Color(0x5555FF);
        assertEquals(rare, BeamColors.fromDisplayName("Rare item", rare));
        assertEquals(rare, BeamColors.fromDisplayName("\u00a7lStyled item", rare));
        assertEquals(Color.WHITE, BeamColors.fromDisplayName("Cobblestone", Color.WHITE));
    }

    @Test
    public void incompleteAndUnknownFormattingDoesNotCrash() {
        assertEquals(Color.WHITE, BeamColors.fromDisplayName("\u00a7", Color.WHITE));
        assertEquals(Color.WHITE, BeamColors.fromDisplayName(null, null));
        assertEquals(Color.WHITE, BeamColors.fromDisplayName("\u00a7zUnknown", Color.WHITE));
    }

    @Test
    public void filtersAcceptNamesCodesAndHex() {
        Color blue = new Color(0x5555FF);
        assertEquals(blue, BeamColors.parseFilterColor(" BLUE "));
        assertEquals(blue, BeamColors.parseFilterColor("9"));
        assertEquals(blue, BeamColors.parseFilterColor("\u00a79"));
        assertEquals(blue, BeamColors.parseFilterColor("#5555FF"));
        assertEquals(blue, BeamColors.parseFilterColor("0x5555ff"));
        assertEquals(new Color(0xFF55FF), BeamColors.parseFilterColor("light_purple"));
        assertEquals(new Color(0xFFAA00), BeamColors.parseFilterColor("gold"));
        assertEquals(Color.WHITE, BeamColors.parseFilterColor("white"));
        assertNull(BeamColors.parseFilterColor("not_a_color"));
        assertNull(BeamColors.parseFilterColor("#1234567"));
        assertNull(BeamColors.parseFilterColor("r"));
    }

    @Test
    public void colorFilterCanExcludeWhiteOrWhitelistBlueAndPurple() {
        assertFalse(BeamFilters.allows(BeamColors.isColorInArray(Color.WHITE, new String[] {"white"}), false));
        assertTrue(BeamFilters.allows(BeamColors.isColorInArray(new Color(0x5555FF),
            new String[] {"blue", "light_purple"}), true));
        assertFalse(BeamFilters.allows(BeamColors.isColorInArray(new Color(0xFFAA00),
            new String[] {"blue", "light_purple"}), true));
    }
}
