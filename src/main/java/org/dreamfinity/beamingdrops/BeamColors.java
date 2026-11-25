package org.dreamfinity.beamingdrops;

import java.awt.Color;
import java.util.Locale;

/** Name formatting and filter colors, independent of the Minecraft renderer. */
public final class BeamColors {

    private static final String CODES = "0123456789abcdefg";
    private static final String[] NAMES = {
        "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "gray",
        "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "white", "minecoin_gold"
    };
    private static final int[] RGB = {
        0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
        0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF, 0xDDD605
    };

    private BeamColors() {}

    public static Color fromFormattingCode(char code) {
        int index = CODES.indexOf(Character.toLowerCase(code));
        return index < 0 ? null : new Color(RGB[index]);
    }

    /**
     * Use the effective color of the first visible, non-whitespace name character.
     * Style codes do not change the color; reset restores white. A trailing reset
     * cannot erase the color of text that has already been encountered.
     */
    public static Color fromDisplayName(String name, Color rarityColor) {
        Color fallback = rarityColor == null ? Color.WHITE : rarityColor;
        Color current = fallback;
        if (name == null) return fallback;

        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);
            if (character == '\u00a7') {
                if (i + 1 >= name.length()) break;
                char code = Character.toLowerCase(name.charAt(++i));
                Color color = fromFormattingCode(code);
                if (color != null) {
                    current = color;
                } else if (code == 'r') {
                    current = Color.WHITE;
                }
            } else if (!Character.isWhitespace(character)) {
                return current;
            }
        }
        return fallback;
    }

    /** Accept Minecraft color names, a formatting code, or an exact RGB hex value. */
    public static Color parseFilterColor(String entry) {
        if (entry == null) return null;
        String value = entry.replace("\"", "").trim().toLowerCase(Locale.ROOT);
        if (value.startsWith("\u00a7") && value.length() == 2) {
            return fromFormattingCode(value.charAt(1));
        }
        if (value.length() == 1) return fromFormattingCode(value.charAt(0));
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(value)) return new Color(RGB[i]);
        }
        if (value.startsWith("#")) value = value.substring(1);
        else if (value.startsWith("0x")) value = value.substring(2);
        if (value.matches("[0-9a-f]{6}")) return new Color(Integer.parseInt(value, 16));
        return null;
    }

    public static boolean isColorInArray(Color color, String[] entries) {
        if (entries == null) return false;
        for (String entry : entries) {
            if (color.equals(parseFilterColor(entry))) return true;
        }
        return false;
    }
}
