package toni.lib.networking.codecs;

public interface StreamEncoder<B, V>
{
    public abstract void encode(B object, V object2);
}
