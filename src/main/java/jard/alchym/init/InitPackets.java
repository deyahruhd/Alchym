package jard.alchym.init;

import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import jard.alchym.client.gui.screen.AlchymRefBookScreen;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class InitPackets {
    static final Map<Identifier, PacketConsumer> PACKET_BEHAVIOR = new HashMap<> ();

    static {
        PACKET_BEHAVIOR.put (AlchymReference.Packets.OPEN_ALCH_REF.id,
                (packetContext, data) -> {
                    AlchymRefBookScreen screen = new AlchymRefBookScreen (new LiteralText (""));

                    net.minecraft.client.MinecraftClient.getInstance ().openScreen (screen);
                });
    }

    public void initialize () {
        Stream<AlchymReference.Packets> serverboundPackets = Stream.of (AlchymReference.Packets.values ()).filter (AlchymReference.Packets::isServerbound);
        Stream<AlchymReference.Packets>	clientboundPackets = Stream.of (AlchymReference.Packets.values ()).filter (AlchymReference.Packets::isClientbound);

        serverboundPackets.forEach (
                (packet) -> {
                    PacketConsumer action = PACKET_BEHAVIOR.get (packet.id);
                    ServerSidePacketRegistry.INSTANCE.register (packet.id,
                            (packetContext, packetByteBuf) -> packetContext.getTaskQueue ().execute (
                                    () -> action.accept (packetContext, packetByteBuf)));
                });
        clientboundPackets.forEach (
                (packet) -> {
                    PacketConsumer action = PACKET_BEHAVIOR.get (packet.id);
                    Alchym.getProxy ().registerPacket (packet, action);
                });
    }
}
