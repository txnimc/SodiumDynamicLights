package toni.lib.networking;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import toni.lib.VersionUtils;
import java.util.function.Consumer;

#if FABRIC
    import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
    import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
    import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
    import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
#endif

#if AFTER_21_1
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
#else
import toni.lib.networking.codecs.StreamCodec;
#endif

public abstract class ToniPacket <TPacket extends ToniPacket> #if AFTER_21_1 implements CustomPacketPayload #endif
{
    public ResourceLocation Resource;
    public StreamCodec<FriendlyByteBuf, TPacket> CODEC;

    #if AFTER_21_1
    public CustomPacketPayload.Type<TPacket> ID;

    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
    #endif


    public ToniPacket(String modid, String path, StreamCodec<FriendlyByteBuf, TPacket> codec) {
        Resource = VersionUtils.resource(modid, path);
        CODEC = codec; //StreamCodec.composite(ByteBufCodecs.BOOL, SyncMediumcoreGameRuleMessage::mediumcoreMode, SyncMediumcoreGameRuleMessage::new);

        #if AFTER_21_1
        ID = new CustomPacketPayload.Type<>(Resource);
        #endif
    }

    public void registerType()
    {
        #if FABRIC
            #if AFTER_21_1
            // In your common initializer method
            PayloadTypeRegistry.playS2C().register(ID, CODEC);
            #endif
        #endif
    }

    public void registerClientHandler(Consumer<TPacket> consumer)
    {
        #if FABRIC
            #if AFTER_21_1
            ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> consumer.accept(payload));
            #else
            ClientPlayNetworking.registerGlobalReceiver(Resource, (client, handler, buf, responseSender) ->
            {
                var packet = CODEC.decode(buf);
                consumer.accept(packet);
            });
            #endif
        #endif
    }

    public void sendToAll(MinecraftServer server)
    {
        #if FABRIC
            #if BEFORE_21_1
            FriendlyByteBuf buf = PacketByteBufs.create();
            CODEC.encode(buf, (TPacket) this);
            #endif

            for (ServerPlayer player : server.getPlayerList().getPlayers())
            {
                #if AFTER_21_1
                ServerPlayNetworking.send(player, this);
                #else
                ServerPlayNetworking.send(player, Resource, buf);
                #endif
            }
        #endif
    }

    public void send(ServerPlayer player)
    {
        #if FABRIC
            #if AFTER_21_1
            ServerPlayNetworking.send(player, this);
            #else
            FriendlyByteBuf buf = PacketByteBufs.create();
            CODEC.encode(buf, (TPacket) this);
            ServerPlayNetworking.send(player, Resource, buf);
            #endif
        #endif
    }
}
