package jard.alchym.init;

import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class InitPackets {
    static final Map<Identifier, PacketConsumer> PACKET_BEHAVIOR = new HashMap<> ();

    static {
        // Insert packet id-packet consumer pairs into the map here.
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
