package toni.lib.networking.codecs;

import io.netty.buffer.ByteBuf;

public interface ByteBufCodecs
{
    StreamCodec<ByteBuf, Boolean> BOOL = new StreamCodec<ByteBuf, Boolean>() {
        public Boolean decode(ByteBuf byteBuf) {
            return byteBuf.readBoolean();
        }

        public void encode(ByteBuf byteBuf, Boolean boolean_) {
            byteBuf.writeBoolean(boolean_);
        }
    };
}
