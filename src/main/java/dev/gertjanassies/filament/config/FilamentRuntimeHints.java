package dev.gertjanassies.filament.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import dev.gertjanassies.filament.domain.CostCalculation;
import dev.gertjanassies.filament.domain.Filament;
import dev.gertjanassies.filament.domain.FilamentType;
import dev.gertjanassies.filament.dto.FilamentListWithType;
import dev.gertjanassies.filament.dto.FilamentWithType;

/**
 * Runtime hints for GraalVM native compilation.
 * Registers record classes for Jackson serialization/deserialization.
 */
public class FilamentRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(@NonNull RuntimeHints hints, @Nullable ClassLoader classLoader) {
        // Register record classes for reflection (needed for Jackson serialization/deserialization)
        // Records require: constructors, methods (for accessors), and fields (for record components)
        hints.reflection()
            .registerType(Filament.class, 
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.DECLARED_FIELDS)
            .registerType(FilamentType.class, 
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.DECLARED_FIELDS)
            .registerType(CostCalculation.class, 
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.DECLARED_FIELDS)
            .registerType(FilamentWithType.class, 
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.DECLARED_FIELDS)
            .registerType(FilamentListWithType.class, 
                MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.DECLARED_FIELDS);
    }
}
