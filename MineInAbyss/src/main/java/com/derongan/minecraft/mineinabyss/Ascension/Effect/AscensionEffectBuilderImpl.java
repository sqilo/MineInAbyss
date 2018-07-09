package com.derongan.minecraft.mineinabyss.Ascension.Effect;

import com.derongan.minecraft.mineinabyss.Ascension.Effect.Effects.*;
import com.derongan.minecraft.mineinabyss.AbyssContext;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


//TODO look at java patterns and figure out a nice way to not have to repeat the builder multiple times
public abstract class AscensionEffectBuilderImpl<E extends AscensionEffect> implements AscensionEffectBuilder {
    private int offset = 0;
    private int strength = 1;
    private int duration = 200;   // Pass it in as as ticks here
    private int iterations = 1;

    int getOffset() {
        return offset;
    }

    int getStrength() {
        return strength;
    }

    int getIterations(){
        return iterations;
    }

    public AscensionEffectBuilderImpl<E> setStrength(int strength) {
        this.strength = strength;
        return this;
    }

    public AscensionEffectBuilderImpl<E> setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    int getDuration() {
        return duration;
    }

    public AscensionEffectBuilderImpl<E> setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public AscensionEffectBuilderImpl<E> setIterations(int iterations) {
        this.iterations = iterations;
        return this;
    }

    public static class PotionAscensionEffectBuilder extends AscensionEffectBuilderImpl<PotionAscensionEffect> {
        List<PotionEffectType> applyEffects;

        public PotionAscensionEffectBuilder setEffects(List<String> listedEffects){
            applyEffects = listedEffects.stream()
                    .map(s -> { try { return PotionEffectType.getByName(s); } catch (IllegalArgumentException iae) { return null; } })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return this;
        } //Convert string list to potion effect types and filter out false entries

        List<PotionEffectType> getEffects(){return applyEffects;}

        @Override
        public PotionAscensionEffect build() {
            return new PotionAscensionEffect(getOffset(), getStrength(), getDuration(), getIterations(), getEffects());
        }
    }

    public static class VomitAscensionEffectBuilder extends AscensionEffectBuilderImpl<VomitAscensionEffect> {
        @Override
        public VomitAscensionEffect build() {
            return new VomitAscensionEffect(getOffset(), getStrength(), getDuration(), getIterations());
        }
    }

    public static class BloodyAscensionEffectBuilder extends AscensionEffectBuilderImpl {
        @Override
        public AscensionEffect build() {
            return new BloodyAscensionEffect(getOffset(), getStrength(), getDuration(), getIterations());
        }
    }

    public static class DeathAscensionEffectBuilder extends AscensionEffectBuilderImpl<DeathAscensionEffect> {
        @Override
        public DeathAscensionEffect build() {
            return new DeathAscensionEffect(getOffset(), getStrength(), getDuration(), getIterations());
        }
    }

    public static class
    HallucinatingAscensionEffectBuilder extends AscensionEffectBuilderImpl<HallucinatingAscensionEffect> {
        @Override
        public HallucinatingAscensionEffect build() {
            return new HallucinatingAscensionEffect(getOffset(), getStrength(), getDuration(), getIterations());
        }
    }

    public static class SoundAscensionEffectBuilder extends AscensionEffectBuilderImpl<SoundAscensionEffect> {
        private static final List<String> ALL_SOUNDS = Arrays.stream(Sound.values()).map(sound->sound.name()).collect(Collectors.toList());
        List<String> sounds = ALL_SOUNDS;

        @Override
        public SoundAscensionEffect build() {
            return new SoundAscensionEffect(getOffset(), getStrength(), getDuration(), getIterations(), getSounds());
        }

        public SoundAscensionEffectBuilder setSounds(List<String> allowedSounds){
            sounds = allowedSounds;
            return this;
        }

        List<String> getSounds() {
            return sounds;
        }
    }
}
