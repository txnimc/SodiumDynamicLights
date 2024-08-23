package toni.lib.networking.codecs;

public interface StreamDecoder<B, V>
{
    public abstract V decode(B object);
}
