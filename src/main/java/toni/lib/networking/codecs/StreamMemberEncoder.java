package toni.lib.networking.codecs;

public interface StreamMemberEncoder<O, T> {
    void encode(T object, O object2);
}