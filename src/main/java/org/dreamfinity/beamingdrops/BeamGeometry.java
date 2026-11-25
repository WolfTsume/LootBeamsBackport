package org.dreamfinity.beamingdrops;

/** Beam vertices in block units, centered on the entity's local X/Z origin. */
public final class BeamGeometry {

    @FunctionalInterface
    public interface VertexSink {
        void vertex(double x, double y, double z, double u, double v);
    }

    private BeamGeometry() {}

    public static void emit(double diameter, double height, double rotation, double bottom, VertexSink sink) {
        double half = diameter / 2;
        double coreRadius = diameter * 0.4;
        for (int side = 0; side < 4; side++) {
            double angle = rotation + Math.PI * 0.75 - side * Math.PI / 2;
            double nextAngle = angle - Math.PI / 2;
            emitSide(coreRadius * Math.cos(angle), coreRadius * Math.sin(angle),
                coreRadius * Math.cos(nextAngle), coreRadius * Math.sin(nextAngle), bottom, height, sink);
        }
        for (int side = 0; side < 4; side++) {
            double x = side == 0 || side == 3 ? -half : half;
            double z = side < 2 ? half : -half;
            int next = (side + 1) % 4;
            double nextX = next == 0 || next == 3 ? -half : half;
            double nextZ = next < 2 ? half : -half;
            emitSide(x, z, nextX, nextZ, bottom, height, sink);
        }
    }

    private static void emitSide(double x, double z, double nextX, double nextZ,
        double bottom, double height, VertexSink sink) {
        double middle = bottom + height / 2;
        double top = bottom + height;
        sink.vertex(x, top, z, 0, 0);
        sink.vertex(x, middle, z, 0, 1);
        sink.vertex(nextX, middle, nextZ, 1, 1);
        sink.vertex(nextX, top, nextZ, 1, 0);

        sink.vertex(nextX, bottom, nextZ, 1, 0);
        sink.vertex(nextX, middle, nextZ, 1, 1);
        sink.vertex(x, middle, z, 0, 1);
        sink.vertex(x, bottom, z, 0, 0);
    }
}
