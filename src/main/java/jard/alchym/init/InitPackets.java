package jard.alchym.init;

import jard.alchym.Alchym;
import jard.alchym.AlchymReference;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;

import java.util.stream.Stream;

public class InitPackets {
    public void initialize () {
        Stream<AlchymReference.Packets> serverboundPackets = Stream.of (AlchymReference.Packets.values ()).filter (AlchymReference.Packets::isServerbound);
        Stream<AlchymReference.Packets>	clientboundPackets = Stream.of (AlchymReference.Packets.values ()).filter (AlchymReference.Packets::isClientbound);

        serverboundPackets.forEach (
                (packet) -> ServerSidePacketRegistry.INSTANCE.register (packet.id,
                            (packetContext, packetByteBuf) -> packetContext.getTaskQueue ().execute (
                                    () -> packet.action.accept (packetContext, packetByteBuf))));
        clientboundPackets.forEach (
                Alchym.getProxy ()::registerPacket);
    }
}
