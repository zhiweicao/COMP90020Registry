package io.friday.registry.raft.handler;

import io.friday.registry.raft.entity.RaftPeerMessage;
import io.friday.registry.transport.util.SimpleSerializationConverter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class RaftPeerMessageCodec extends ByteToMessageCodec<RaftPeerMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RaftPeerMessage msg, ByteBuf out) throws Exception {
        byte[] data = SimpleSerializationConverter.objectToByte(msg);
        out.writeBytes(data);
        ctx.flush();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        Object obj = SimpleSerializationConverter.byteToObject(bytes);
        out.add(obj);
    }
}
