package org.dreamfinity.beamingdrops;

import static org.junit.Assert.*;

import org.junit.Test;

public class BeamFiltersTest {

    @Test
    public void bareIdsAndWildcardsIncludeNonzeroMetadata() {
        assertTrue(BeamFilters.matchesItemId("gregtech:gt.metaitem.01", 12345, "gregtech:gt.metaitem.01"));
        assertTrue(BeamFilters.matchesItemId("gregtech:gt.metaitem.01", 12345, "gregtech:gt.metaitem.01:*"));
        assertTrue(BeamFilters.matchesItemId("gregtech:gt.metaitem.01", 12345, "gregtech:gt.metaitem.01:12345"));
        assertFalse(BeamFilters.matchesItemId("gregtech:gt.metaitem.01", 12345, "gregtech:gt.metaitem.01:0"));
    }

    @Test
    public void idsMustMatchExactlyAndInvalidMetadataIsIgnored() {
        assertFalse(BeamFilters.matchesItemId("minecraft:stone", 0, "minecraft:cobblestone"));
        assertFalse(BeamFilters.matchesItemId("other:stone", 0, "minecraft:stone"));
        assertFalse(BeamFilters.matchesItemId("minecraft:stone", 0, "minecraft:stone:bad"));
        assertFalse(BeamFilters.matchesItemId("minecraft:stone", 0, "minecraft:stone:"));
        assertFalse(BeamFilters.matchesItemId("minecraft:stone", 0, "minecraft:stone:0:1"));
        assertFalse(BeamFilters.matchesItemId("minecraft:stone", 0, "stone"));
        assertFalse(BeamFilters.matchesItemId("minecraft:stone", 0, null));
        assertTrue(BeamFilters.matchesItemId("minecraft:stone", 0, " \"minecraft:stone:0\" "));
    }

    @Test
    public void blacklistsAndWhitelistsHaveExplicitEmptyListBehavior() {
        assertTrue(BeamFilters.allows(false, false));
        assertFalse(BeamFilters.allows(true, false));
        assertFalse(BeamFilters.allows(false, true));
        assertTrue(BeamFilters.allows(true, true));
    }
}
