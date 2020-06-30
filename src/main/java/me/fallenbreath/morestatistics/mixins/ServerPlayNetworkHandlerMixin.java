package me.fallenbreath.morestatistics.mixins;

import me.fallenbreath.morestatistics.network.Network;
import me.fallenbreath.morestatistics.network.ServerHandler;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin
{
	@Shadow
	public ServerPlayerEntity player;

	@Inject(
			method = "onCustomPayload",
			at = @At("HEAD"),
			cancellable = true
	)
	private void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci)
	{
		synchronized (Network.sync)
		{
			Identifier channel = ((CustomPayloadC2SPacketAccessor) packet).getChannel();
			if (Network.CHANNEL.equals(channel))
			{
				ServerHandler.handleStatsListUpdate(((CustomPayloadC2SPacketAccessor) packet).getData(), player);
				ci.cancel();
			}
		}
	}

	@Inject(
			method = "onDisconnected",
			at = @At("HEAD")
	)
	private void onPlayerDisconnect(Text reason, CallbackInfo ci)
	{
		ServerHandler.onPlayerLoggedOut(this.player);
	}
}