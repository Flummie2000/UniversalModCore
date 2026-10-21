package cam72cam.mod.mixin.feat.deny_dismount;

import cam72cam.mod.entity.SeatEntity;
import cam72cam.mod.entity.ModdedEntity;
import cam72cam.mod.ModCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {

    //resolve riding
    @Unique
    private static ModdedEntity umc$getStock(Entity riding) {
        if (riding instanceof SeatEntity) {
            SeatEntity seat = (SeatEntity) riding;
            if (seat.getParent() == null) return null;
            riding = seat.getParent().internal;
        }
        return riding instanceof ModdedEntity ? (ModdedEntity) riding : null;
    }

    //lock sneak dismount if canPassengerLeaveVehicle is false
    @Redirect(method = "updateRidden", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayer;isSneaking()Z"))
    private boolean umc$lockSneakDismount(EntityPlayer self) {
        boolean sneaking = self.isSneaking();
        if (!sneaking) return false;

        ModdedEntity stock = umc$getStock(self.ridingEntity);
        if (stock != null && !stock.canPassengerLeaveVehicle(self)) {
            return false;
        }
        return true;
    }

    //1.7.10 canFitPassenger implementation
    @Inject(method = "mountEntity", at = @At("HEAD"), cancellable = true)
    private void umc$canFitPassenger(Entity entityIn, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (entityIn instanceof ModdedEntity
                && !((ModdedEntity) entityIn).canFitPassenger(self)) {
            ci.cancel();
        }
    }
}