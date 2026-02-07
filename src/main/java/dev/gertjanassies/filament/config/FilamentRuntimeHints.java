package dev.gertjanassies.filament.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import dev.gertjanassies.filament.domain.CostCalculation;
import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.domain.FilamentType;

/**
 * Runtime hints for GraalVM native compilation.
 * Registers record classes for Jackson serialization/deserialization.
 */
public class FilamentRuntimeHints implements RuntimeHintsRegistrar {

    private static final MemberCategory[] JACKSON_MEMBER_CATEGORIES = {
        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
        MemberCategory.INVOKE_DECLARED_METHODS,
        MemberCategory.DECLARED_FIELDS
    };

    @Override
    public void registerHints(@NonNull RuntimeHints hints, @Nullable ClassLoader classLoader) {
        // Register record classes for reflection (needed for Jackson serialization)
        hints.reflection()
            .registerType(Filament.class, JACKSON_MEMBER_CATEGORIES)
            .registerType(CostCalculation.class, JACKSON_MEMBER_CATEGORIES)
            .registerType(FilamentType.class, JACKSON_MEMBER_CATEGORIES);
    }
}
