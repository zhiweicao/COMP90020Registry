package io.friday.registry.transport.handler;

import io.friday.registry.transport.util.SimpleSerializationConverter;
import io.friday.registry.transport.TransportNode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class TransportMessageCodec extends ByteToMessageCodec<TransportNode> {

    @Override
    protected void encode(ChannelHandlerContext ctx, TransportNode msg, ByteBuf out) throws Exception {
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
