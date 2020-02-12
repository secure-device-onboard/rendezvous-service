// Copyright 2019 Intel Corporation
// SPDX-License-Identifier: Apache 2.0

package org.sdo.rendezvous.services.errormessage;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class MessageSanitizerTest {

  private final String invisibleAsciiCharacters;
  private MessageSanitizer messageSanitizer;

  public MessageSanitizerTest() {
    invisibleAsciiCharacters = generateStringInRange(0, 32);
  }

  @BeforeTest
  public void setUp() {
    messageSanitizer = new MessageSanitizer();
  }

  @Test
  public void testWithReplacementAtBeginning() {
    Optional<String> sanitizedMessage;
    sanitizedMessage =
        messageSanitizer.sanitizeMessage(
            "]abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPRSTUVWXYZ0123456789$-_.+!*(), ");
    Assert.assertTrue(sanitizedMessage.isPresent());
    Assert.assertEquals(
        sanitizedMessage.get(),
        "#abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPRSTUVWXYZ0123456789$-_.+!*(), ");
  }

  @Test
  public void testWithReplacementAtEnding() {
    Optional<String> sanitizedMessage;
    sanitizedMessage =
        messageSanitizer.sanitizeMessage(
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPRSTUVWXYZ0123456789$-_.+!*(), \\");
    Assert.assertTrue(sanitizedMessage.isPresent());
    Assert.assertEquals(
        sanitizedMessage.get(),
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPRSTUVWXYZ0123456789$-_.+!*(), #");
  }

  @Test
  public void testWithReplacementDiacriticCharacters() {
    Optional<String> sanitizedMessage;
    sanitizedMessage =
        messageSanitizer.sanitizeMessage(
            "aąbcćdeęfghijklłmnoópqrsśtuvwxyzźżAĄBCĆDEĘFGHIJKLŁMNOÓPRSŚTUV"
                + "WXYZŹŻ0123456789$-_.+!*(), ");
    Assert.assertTrue(sanitizedMessage.isPresent());
    Assert.assertEquals(
        sanitizedMessage.get(),
        "a#bc#de#fghijkl#mno#pqrs#tuvwxyz##A#BC#DE#FGHIJKL#MNO#PRS#TUVWXYZ##0123456789$-_.+!*(), ");
  }

  @Test
  public void testWithReplacementSpecialAsciiCharactersVisible() {
    Optional<String> sanitizedMessage;
    sanitizedMessage =
        messageSanitizer.sanitizeMessage(
            "\"#%&'/:;<=>?@[\\]^`{|}~abcdefghijklmnopqrstuvwxyz\"#%&'/:;<=>?@[\\]^`{|}~"
                + "ABCDEFGHIJKLMNOPRSTUVWXYZ0123456789$-_.+!*(), \"#%&'/:;<=>?@[\\]^`{|}~");
    Assert.assertTrue(sanitizedMessage.isPresent());
    Assert.assertEquals(
        sanitizedMessage.get(),
        "######################abcdefghijklmnopqrstuvwxyz######################ABCDEFGHIJKLMN"
            + "OPRSTUVWXYZ0123456789$-_.+!*(), ######################");
  }

  @Test
  public void testWithReplacementSpecialAsciiCharactersInvisible() {
    Optional<String> sanitizedMessage;
    sanitizedMessage =
        messageSanitizer.sanitizeMessage(
            invisibleAsciiCharacters
                + "abcdefghijklmnopqrstuvwxyz"
                + invisibleAsciiCharacters
                + "ABCDEFGHIJKLMNOPRSTUVWXYZ0123456789$-_.+!*(), "
                + invisibleAsciiCharacters);
    Assert.assertTrue(sanitizedMessage.isPresent());
    Assert.assertEquals(
        sanitizedMessage.get(),
        repeat("#", invisibleAsciiCharacters.length())
            + "abcdefghijklmnopqrstuvwxyz"
            + repeat("#", invisibleAsciiCharacters.length())
            + "ABCDEFGHIJKLMNOPRSTUVWXYZ0123456789$-_.+!*(), "
            + repeat("#", invisibleAsciiCharacters.length()));
  }

  @Test
  public void testWithReplacementCombo() {
    Optional<String> sanitizedMessage;
    sanitizedMessage =
        messageSanitizer.sanitizeMessage(
            "[aąbcćdeęfghijklłmnoópqrsśtuv\"#%&'/:;<=>?@[\\]^`{|}~wxyzźż"
                + invisibleAsciiCharacters
                + "AĄBCĆDEĘFGHIJKLŁMNOÓPRSŚTUVWXYZŹŻ0123456789$-_.+!*(), }");
    Assert.assertTrue(sanitizedMessage.isPresent());
    Assert.assertEquals(
        sanitizedMessage.get(),
        "#a#bc#de#fghijkl#mno#pqrs#tuv######################wxyz##"
            + repeat("#", invisibleAsciiCharacters.length())
            + "A#BC#DE#FGHIJKL#MNO#PRS#TUVWXYZ##0123456789$-_.+!*(), #");
  }

  @Test
  public void testWithReplacementAllPossibleCharacters() {
    Optional<String> sanitizedMessage;
    sanitizedMessage =
        messageSanitizer.sanitizeMessage(generateStringInRange(0, Character.MAX_VALUE));
    Assert.assertTrue(sanitizedMessage.isPresent());
    Assert.assertEquals(
        sanitizedMessage.get(),
        repeat("#", 32)
            + " !##$###()*+,-.#"
            + generateStringInRange(48, 58)
            + repeat("#", 7)
            + generateStringInRange(65, 91)
            + "####_#"
            + generateStringInRange(97, 123)
            + repeat("#", Character.MAX_VALUE - 124));
  }

  @Test
  public void testWithoutReplacementAlphabetLowerCase() {
    Optional<String> sanitizedMessage;
    sanitizedMessage = messageSanitizer.sanitizeMessage("abcdefghijklmnopqrstuvwxyz");
    Assert.assertFalse(sanitizedMessage.isPresent());
  }

  @Test
  public void testWithoutReplacementAlphabetUpperCase() {
    Optional<String> sanitizedMessage;
    sanitizedMessage = messageSanitizer.sanitizeMessage("ABCDEFGHIJKLMNOPRSTUVWXYZ");
    Assert.assertFalse(sanitizedMessage.isPresent());
  }

  @Test
  public void testWithoutReplacementDigits() {
    Optional<String> sanitizedMessage;
    sanitizedMessage = messageSanitizer.sanitizeMessage("0123456789");
    Assert.assertFalse(sanitizedMessage.isPresent());
  }

  @Test
  public void testWithoutReplacementSpecialCharacters() {
    Optional<String> sanitizedMessage;
    sanitizedMessage = messageSanitizer.sanitizeMessage("$-_.+!*(), ");
    Assert.assertFalse(sanitizedMessage.isPresent());
  }

  @Test
  public void testWithoutReplacementCombo() {
    Optional<String> sanitizedMessage;
    sanitizedMessage =
        messageSanitizer.sanitizeMessage(
            "$-_.+!*(), abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz$-"
                + "_.+!*(), ABCDEFGHIJKLMNOPRSTUVWXYZ$-_.+!*(), ");
    Assert.assertFalse(sanitizedMessage.isPresent());
  }

  @Test
  public void testEmptyString() {
    Optional<String> sanitizedMessage;
    sanitizedMessage = messageSanitizer.sanitizeMessage("");
    Assert.assertFalse(sanitizedMessage.isPresent());
  }

  private String repeat(String text, int count) {
    return IntStream.range(0, count).mapToObj(i -> text).collect(Collectors.joining(""));
  }

  private String generateStringInRange(int from, int to) {
    return IntStream.range(from, to)
        .mapToObj(s -> (char) s)
        .map(String::valueOf)
        .collect(Collectors.joining());
  }
}
