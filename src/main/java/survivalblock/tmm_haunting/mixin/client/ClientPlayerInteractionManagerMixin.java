package survivalblock.tmm_haunting.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.tmm_haunting.common.GhostsOfChristmasPast;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Definition(id = "gameMode", field = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;gameMode:Lnet/minecraft/world/GameMode;")
    @Definition(id = "SPECTATOR", field = "Lnet/minecraft/world/GameMode;SPECTATOR:Lnet/minecraft/world/GameMode;")
    @Expression("this.gameMode == SPECTATOR")
    @ModifyExpressionValue(method = "interactBlockInternal", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean bypassSpectatorCheck(boolean original, @Local(argsOnly = true) BlockHitResult hitResult) {
        if (!original) {
            return false;
        }

        BlockPos blockPos = hitResult.getBlockPos();
        if (blockPos == null) {
            return true;
        }
        if (this.client.world == null) {
            return true;
        }

        return !GhostsOfChristmasPast.canBeUsedBySpectator(this.client.world.getBlockState(blockPos).getBlock());
    }
}
