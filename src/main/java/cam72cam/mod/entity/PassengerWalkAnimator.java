package cam72cam.mod.entity;

import cam72cam.mod.math.Vec3d;
import net.minecraft.entity.EntityLivingBase;

final class PassengerWalkAnimator {
    Vec3d lastOffset;
    private float amount;

    /** Client only. dist = horizontal distance the passenger moved inside the stock this tick (blocks). */
    void update(EntityLivingBase e, Vec3d offset, double dist) {
        lastOffset = offset;
        if (dist > 1.0) dist = 0;                                   // teleport / seat swap, not walking
        amount += ((float) Math.min(1.0, dist * 4.0) - amount) * 0.4f;
        e.limbSwingAmount = amount;
        e.limbSwing += amount;
    }
}
