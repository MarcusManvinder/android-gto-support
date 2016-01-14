package org.ccci.gto.android.common.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;

import org.ccci.gto.android.common.util.LocaleCompat.Compat;
import org.ccci.gto.android.common.util.LocaleCompat.FroyoCompat;
import org.ccci.gto.android.common.util.LocaleCompat.LollipopCompat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

@RunWith(Parameterized.class)
public class LocaleCompatTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {{new FroyoCompat()}, {new LollipopCompat()}});
    }

    private final Compat compat;

    private static final Map<Locale, String> LANGUAGETAGS;
    static {
        final ImmutableMap.Builder<Locale, String> builder = ImmutableMap.builder();
        builder.put(Locale.US, "en-US");
        builder.put(new Locale("EN", "gb"), "en-GB");
        builder.put(Locale.ENGLISH, "en");
        builder.put(Locale.SIMPLIFIED_CHINESE, "zh-CN");
        builder.put(new Locale("ru", "143"), "ru-143");
        builder.put(new Locale("en", "", "scotland_fonipa"), "en-scotland-fonipa");
        LANGUAGETAGS = builder.build();
    }

    private static final Map<String, Locale> LOCALES;
    static {
        final ImmutableMap.Builder<String, Locale> builder = ImmutableMap.builder();
        builder.put("en-US", Locale.US);
        builder.put("en-GB", Locale.UK);
        builder.put("en", Locale.ENGLISH);
        builder.put("EN-us", Locale.US);
        builder.put("ru-143", new Locale("ru", "143"));
//        builder.put("zh-x-hui", new Locale.Builder().setLanguage("zh").setExtension('x', "hui").build());
        LOCALES = builder.build();
    }

    public LocaleCompatTest(final Compat compat) {
        this.compat = compat;
    }

    @Test
    public void testForLanguageTag() throws Exception {
        for (final Map.Entry<String, Locale> entry : LOCALES.entrySet()) {
            assertEquals(entry.getValue(), compat.forLanguageTag(entry.getKey()));
        }
    }

    @Test
    public void testToLanguageTag() throws Exception {
        for (final Map.Entry<Locale, String> entry : LANGUAGETAGS.entrySet()) {
            assertEquals(entry.getValue(), compat.toLanguageTag(entry.getKey()));
        }
    }

    @Test
    public void testGetFallbacks() throws Exception {
        assertArrayEquals(new Locale[] {Locale.US, Locale.ENGLISH}, compat.getFallbacks(Locale.US));
        assertArrayEquals(new Locale[] {Locale.ENGLISH}, compat.getFallbacks(Locale.ENGLISH));

        // test batch fallback resolution
        assertArrayEquals(new Locale[] {Locale.US, Locale.ENGLISH},
                          compat.getFallbacks(Locale.US, Locale.ENGLISH));
        assertArrayEquals(new Locale[] {Locale.ENGLISH, Locale.US},
                          compat.getFallbacks(Locale.ENGLISH, Locale.US));
        assertArrayEquals(new Locale[] {Locale.US, Locale.ENGLISH, Locale.CANADA, Locale.CANADA_FRENCH, Locale.FRENCH},
                          compat.getFallbacks(Locale.US, Locale.CANADA, Locale.CANADA_FRENCH));
    }
}
