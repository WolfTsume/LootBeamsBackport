package org.dreamfinity.beamingdrops;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class BeamGeometryTest {

    @Test
    public void rotatingBeamStaysCenteredAtEveryAngleAndDiameter() {
        for (double diameter : new double[] {0.05, 0.25, 3.0}) {
            for (double angle : new double[] {0, 0.1, Math.PI / 4, Math.PI, 50}) {
                List<double[]> vertices = vertices(diameter, 4, angle, 0.2);
                double x = 0;
                double z = 0;
                for (double[] vertex : vertices) {
                    x += vertex[0];
                    z += vertex[2];
                }
                assertEquals("Rotation must not move the beam away from the entity", 0, x / vertices.size(), 1e-10);
                assertEquals("Rotation must not move the beam away from the entity", 0, z / vertices.size(), 1e-10);
            }
        }
    }

    @Test
    public void dimensionsUseBlocksAndDoNotDependOnTheItemRendererScale() {
        List<double[]> vertices = vertices(0.25, 4, 0, 0.2);
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (double[] vertex : vertices) {
            minX = Math.min(minX, vertex[0]);
            maxX = Math.max(maxX, vertex[0]);
            minY = Math.min(minY, vertex[1]);
            maxY = Math.max(maxY, vertex[1]);
        }
        assertEquals(-0.125, minX, 1e-10);
        assertEquals(0.125, maxX, 1e-10);
        assertEquals(0.2, minY, 1e-10);
        assertEquals(4.2, maxY, 1e-10);
    }

    @Test
    public void meshIncludesCoreAndShellWithValidTextureCoordinates() {
        List<double[]> vertices = vertices(0.25, 4, 0.5, 0.2);
        assertEquals(64, vertices.size());
        for (double[] vertex : vertices) {
            for (double coordinate : vertex) assertTrue(Double.isFinite(coordinate));
            assertTrue(vertex[3] >= 0 && vertex[3] <= 1);
            assertTrue(vertex[4] >= 0 && vertex[4] <= 1);
        }
    }

    private static List<double[]> vertices(double diameter, double height, double rotation, double bottom) {
        List<double[]> result = new ArrayList<>();
        BeamGeometry.emit(diameter, height, rotation, bottom,
            (x, y, z, u, v) -> result.add(new double[] {x, y, z, u, v}));
        return result;
    }
}
