package io.friday.registry.core.handler;

import io.friday.registry.core.entity.PeerMessage;
import io.friday.registry.core.util.SimpleSerializationConverter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

public class PeerMessageCodec extends ByteToMessageCodec<PeerMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, PeerMessage msg, ByteBuf out) throws Exception {
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
