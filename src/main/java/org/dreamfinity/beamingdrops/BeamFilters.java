package org.dreamfinity.beamingdrops;

/** Shared blacklist/whitelist policy and item ID matching. */
public final class BeamFilters {

    private BeamFilters() {}

    public static boolean allows(boolean matched, boolean whitelist) {
        return whitelist == matched;
    }

    /** A bare registry ID includes all metadata; an explicit third field narrows it. */
    public static boolean matchesItemId(String registryId, int metadata, String entry) {
        if (registryId == null || entry == null) return false;
        String[] parts = entry.replace("\"", "").trim().split(":", -1);
        if (parts.length < 2 || parts.length > 3 || parts[0].isEmpty() || parts[1].isEmpty()) return false;
        if (!registryId.equals(parts[0] + ":" + parts[1])) return false;
        if (parts.length == 2 || "*".equals(parts[2])) return true;
        try {
            return Integer.parseInt(parts[2]) == metadata;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
