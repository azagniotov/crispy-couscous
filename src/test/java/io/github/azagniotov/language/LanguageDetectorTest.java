package io.github.azagniotov.language;

import static io.github.azagniotov.language.LanguageDetectionSettings.DEFAULT_SETTINGS_ALL_LANGUAGES;
import static io.github.azagniotov.language.TestHelper.testLanguage;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Before;
import org.junit.Test;

/** Sanity checks for {@link LanguageDetector#detectAll(String)}. */
public class LanguageDetectorTest {

  private static final String TRAINING_EN = "a a a b b c c d e";
  private static final String TRAINING_FR = "a b b c c c d d d";
  private static final String TRAINING_JA = "\u3042 \u3042 \u3042 \u3044 \u3046 \u3048 \u3048";
  private static final String SINGLE_SPACE = " ";

  private static final LanguageDetector DEFAULT_DETECTOR;

  static {
    final LanguageDetectorFactory factory =
        new LanguageDetectorFactory(DEFAULT_SETTINGS_ALL_LANGUAGES);
    DEFAULT_DETECTOR =
        new LanguageDetector(
            factory.getSupportedIsoCodes639_1(), factory.getLanguageCorporaProbabilities());
  }

  private LanguageDetector languageDetector;

  @Before
  public void setUp() throws Exception {
    final LanguageDetectionSettings emptySettings =
        LanguageDetectionSettings.fromIsoCodes639_1("").build();
    final LanguageDetectorFactory factory = new LanguageDetectorFactory(emptySettings);

    final String profileTemplate = "{\"freq\":{},\"n_words\":[0.0, 0.0, 0.0],\"name\":\"%s\"}";
    final InputStream enTest =
        new ByteArrayInputStream(
            String.format(profileTemplate, "en_test").getBytes(StandardCharsets.UTF_8));
    final LanguageProfile enProfile = LanguageProfile.fromJson(enTest);
    for (String w : TRAINING_EN.split(SINGLE_SPACE)) {
      enProfile.add(w);
    }
    factory.addProfile(enProfile, 0, 3);

    final InputStream frTest =
        new ByteArrayInputStream(
            String.format(profileTemplate, "fr_test").getBytes(StandardCharsets.UTF_8));
    final LanguageProfile frProfile = LanguageProfile.fromJson(frTest);
    for (String w : TRAINING_FR.split(SINGLE_SPACE)) {
      frProfile.add(w);
    }
    factory.addProfile(frProfile, 1, 3);

    final InputStream jaTest =
        new ByteArrayInputStream(
            String.format(profileTemplate, "ja_test").getBytes(StandardCharsets.UTF_8));
    final LanguageProfile jaProfile = LanguageProfile.fromJson(jaTest);
    for (String w : TRAINING_JA.split(SINGLE_SPACE)) {
      jaProfile.add(w);
    }
    factory.addProfile(jaProfile, 2, 3);

    languageDetector =
        new LanguageDetector(
            factory.getSupportedIsoCodes639_1(), factory.getLanguageCorporaProbabilities());
  }

  @Test
  public void shouldDetectEnglishDataset() throws Exception {
    assertEquals(languageDetector.detectAll("a").get(0).getIsoCode639_1(), "en_test");
  }

  @Test
  public void shouldDetectFrenchDataset() throws Exception {
    assertEquals(languageDetector.detectAll("b d").get(0).getIsoCode639_1(), "fr_test");
  }

  @Test
  public void shouldDetectEnglishDataset_v2() throws Exception {
    assertEquals(languageDetector.detectAll("d e").get(0).getIsoCode639_1(), "en_test");
  }

  @Test
  public void shouldDetectJapaneseDataset() throws Exception {
    assertEquals(
        languageDetector.detectAll("\u3042\u3042\u3042\u3042a").get(0).getIsoCode639_1(),
        "ja_test");
  }

  @Test
  public void languageDetectorShortStrings() throws Exception {
    final LanguageDetectionSettings supportedLanguages =
        LanguageDetectionSettings.fromIsoCodes639_1("az,br,cy,de,eu,ga,hy,ka,lb").build();
    final LanguageDetectorFactory factory = new LanguageDetectorFactory(supportedLanguages);
    final LanguageDetector detector =
        new LanguageDetector(
            factory.getSupportedIsoCodes639_1(), factory.getLanguageCorporaProbabilities());

    // "I am learning <LANGUAGE_NAME>" in various languages

    // Armenian
    assertEquals("hy", detector.detectAll("Սովորում եմ հայերեն").get(0).getIsoCode639_1());
    // Azerbaijani
    assertEquals("az", detector.detectAll("Azərbaycan dilini öyrənirəm").get(0).getIsoCode639_1());
    // Basque
    assertEquals("eu", detector.detectAll("Euskara ikasten ari naiz").get(0).getIsoCode639_1());
    // Breton
    assertEquals("br", detector.detectAll("Emaon o teskiñ brezhoneg").get(0).getIsoCode639_1());
    // Georgian
    assertEquals("ka", detector.detectAll("ვსწავლობ ქართულს").get(0).getIsoCode639_1());
    // German
    assertEquals("de", detector.detectAll("Ich lerne Deutsch").get(0).getIsoCode639_1());
    // Irish
    assertEquals("ga", detector.detectAll("Tá mé ag foghlaim Gaeilge").get(0).getIsoCode639_1());
    // Luxembourgish
    assertEquals("lb", detector.detectAll("Ech léiere Lëtzebuergesch").get(0).getIsoCode639_1());
    // Welsh
    assertEquals("cy", detector.detectAll("Dw i'n dysgu Cymraeg").get(0).getIsoCode639_1());
  }

  @Test
  public void testEnglish() throws Exception {
    testLanguage("english.txt", "en", DEFAULT_DETECTOR);
  }

  @Test
  public void testChinese() throws Exception {
    testLanguage("chinese.txt", "zh-cn", DEFAULT_DETECTOR);
  }

  @Test
  public void testJapanese() throws Exception {
    testLanguage("japanese.txt", "ja", DEFAULT_DETECTOR);
  }

  @Test
  public void testKorean() throws Exception {
    testLanguage("korean.txt", "ko", DEFAULT_DETECTOR);
  }

  @Test
  public void testGerman() throws Exception {
    testLanguage("german.txt", "de", DEFAULT_DETECTOR);
  }

  @Test
  public void testLuxembourgish() throws Exception {
    testLanguage("luxembourgish.txt", "lb", DEFAULT_DETECTOR);
  }

  @Test
  public void testBreton() throws Exception {
    testLanguage("breton.txt", "br", DEFAULT_DETECTOR);
  }

  @Test
  public void testWelsh() throws Exception {
    testLanguage("welsh.txt", "cy", DEFAULT_DETECTOR);
  }

  @Test
  public void testGeorgian() throws Exception {
    testLanguage("georgian.txt", "ka", DEFAULT_DETECTOR);
  }

  @Test
  public void testArmenian() throws Exception {
    testLanguage("armenian.txt", "hy", DEFAULT_DETECTOR);
  }

  @Test
  public void testBasque() throws Exception {
    testLanguage("basque.txt", "eu", DEFAULT_DETECTOR);
  }

  @Test
  public void testIrish() throws Exception {
    testLanguage("irish.txt", "ga", DEFAULT_DETECTOR);
  }

  @Test
  public void testAzerbaijani() throws Exception {
    testLanguage("azerbaijani.txt", "az", DEFAULT_DETECTOR);
  }

  @Test
  public final void languageDetectorShouldDetectChinese() throws Exception {
    final LanguageDetectorFactory factory =
        new LanguageDetectorFactory(DEFAULT_SETTINGS_ALL_LANGUAGES);
    final LanguageDetector detector =
        new LanguageDetector(
            factory.getSupportedIsoCodes639_1(), factory.getLanguageCorporaProbabilities());

    assertEquals(
        "zh-cn",
        detector
            .detectAll(
                "位于美国首都华盛顿都会圈的希望中文学校５日晚举办活动庆祝建立２０周年。"
                    + "从中国大陆留学生为子女学中文而自发建立的学习班，"
                    + "到学生规模在全美名列前茅的中文学校，这个平台的发展也折射出美国的中文教育热度逐步提升。"
                    + "希望中文学校是大华盛顿地区最大中文学校，现有７个校区逾４０００名学生，"
                    + "规模在美国东部数一数二。不过，见证了希望中文学校２０年发展的人们起初根本无法想象这个小小的中文教育平台能发展到今日之规模。")
            .get(0)
            .getIsoCode639_1());
  }

  @Test
  public final void languageDetectorShouldDetectShortStringsWithDefaultSanitization()
      throws Exception {

    assertEquals("ja", DEFAULT_DETECTOR.detectAll("ｼｰｻｲﾄﾞ_ﾗｲﾅｰ.pdf").get(0).getIsoCode639_1());

    assertEquals("ja", DEFAULT_DETECTOR.detectAll("東京に行き ABCDEF").get(0).getIsoCode639_1());

    assertEquals("ja", DEFAULT_DETECTOR.detectAll("\"ヨキ\" AND \"杉山\"").get(0).getIsoCode639_1());
    assertEquals(
        "ja", DEFAULT_DETECTOR.detectAll("\"ヨキ\" AND \"杉山\" OR \"分布\"").get(0).getIsoCode639_1());
    assertEquals(
        "ja", DEFAULT_DETECTOR.detectAll("#4_pj_23D002_HCMJ_デジ戦").get(0).getIsoCode639_1());
    assertEquals(
        "ja", DEFAULT_DETECTOR.detectAll("in1729x01_J-LCM刷新プロジェクト.docx").get(0).getIsoCode639_1());
    assertEquals("ja", DEFAULT_DETECTOR.detectAll("Ｃｕｌｔｕｒｅ　ｏｆ　Ｊａｐａｎ").get(0).getIsoCode639_1());
    assertEquals(
        "ja", DEFAULT_DETECTOR.detectAll("ﾊｰﾄﾞｶﾌﾟｾﾙ(ｻｲｽﾞ3号 NATURAL B／C)").get(0).getIsoCode639_1());
    assertEquals("ja", DEFAULT_DETECTOR.detectAll("Microsoft　インポート").get(0).getIsoCode639_1());

    assertEquals("da", DEFAULT_DETECTOR.detectAll("Ｃｕｌｔｕｒｅ　ｏｆ　Ｊａｐａｎ.pdf").get(0).getIsoCode639_1());

    assertEquals(
        "ca",
        DEFAULT_DETECTOR.detectAll("ﾊｰﾄﾞｶﾌﾟｾﾙ(ｻｲｽﾞ3号 NATURAL B／C).pdf").get(0).getIsoCode639_1());
    assertEquals("ca", DEFAULT_DETECTOR.detectAll("刷新eclipseRCPExt.pdf").get(0).getIsoCode639_1());
    assertEquals("ca", DEFAULT_DETECTOR.detectAll("report.xls").get(0).getIsoCode639_1());

    assertEquals(
        "en", DEFAULT_DETECTOR.detectAll("This is a very small test").get(0).getIsoCode639_1());

    assertEquals(
        "de", DEFAULT_DETECTOR.detectAll("Das kann deutsch sein").get(0).getIsoCode639_1());
    assertEquals("de", DEFAULT_DETECTOR.detectAll("Das ist ein Text").get(0).getIsoCode639_1());

    assertEquals("zh-tw", DEFAULT_DETECTOR.detectAll("TOEIC 分布").get(0).getIsoCode639_1());
    assertEquals("zh-tw", DEFAULT_DETECTOR.detectAll("１２３４５６７８９０.xls 報告").get(0).getIsoCode639_1());
    assertEquals("zh-tw", DEFAULT_DETECTOR.detectAll("富山県高岡市.txt").get(0).getIsoCode639_1());
    assertEquals(
        "zh-tw",
        DEFAULT_DETECTOR.detectAll("【12.21s】【流山運動公園：流山市市野谷】【中村P：3台】").get(0).getIsoCode639_1());
    assertEquals("zh-tw", DEFAULT_DETECTOR.detectAll("東京 ABCDEF").get(0).getIsoCode639_1());
    assertEquals("zh-tw", DEFAULT_DETECTOR.detectAll("東 ABCDE").get(0).getIsoCode639_1());
    assertEquals(
        "zh-tw", DEFAULT_DETECTOR.detectAll("AND \"杉山\" OR \"分布\"").get(0).getIsoCode639_1());

    assertEquals("zh-cn", DEFAULT_DETECTOR.detectAll("㈱_(株)_①②③_㈱㈲㈹").get(0).getIsoCode639_1());
    assertEquals(
        "zh-cn", DEFAULT_DETECTOR.detectAll("帮助他们以截然不同的方式探索和分析数据.pdf").get(0).getIsoCode639_1());
  }

  @Test
  public final void languageDetectorRespondsWithUndeterminedLanguage() throws Exception {
    final LanguageDetectionSettings supportedLanguages =
        LanguageDetectionSettings.fromIsoCodes639_1("en,de").build();
    final LanguageDetectorFactory factory = new LanguageDetectorFactory(supportedLanguages);
    final LanguageDetector detector =
        new LanguageDetector(
            factory.getSupportedIsoCodes639_1(), factory.getLanguageCorporaProbabilities());

    assertEquals("und", detector.detectAll("ｼｰｻｲﾄﾞ_ﾗｲﾅｰ").get(0).getIsoCode639_1());
    assertEquals("und", detector.detectAll("Ｃｕｌｔｕｒｅ　ｏｆ　Ｊａｐａｎ").get(0).getIsoCode639_1());
    assertEquals("und", detector.detectAll("㈱_(株)_①②③_㈱㈲㈹").get(0).getIsoCode639_1());
    assertEquals("und", detector.detectAll("...").get(0).getIsoCode639_1());
    assertEquals("und", detector.detectAll("1234567").get(0).getIsoCode639_1());
    assertEquals("und", detector.detectAll("한국어").get(0).getIsoCode639_1());
  }
}
