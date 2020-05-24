package step2.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import step2.exception.LottoGamePriceException;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LottoGameTest {

  private static final String RATIO_FORMAT = "%.2f";

  @DisplayName("구매 금액이 1000이하일 경우 LottoGamePriceException 발")
  @ParameterizedTest
  @ValueSource(ints = { -1000, 0, 999 })
  void 구매_금액_검증_테스트 (int price) {
    assertThatExceptionOfType(LottoGamePriceException.class)
      .isThrownBy(() -> LottoGame.of(price));
  }

  @DisplayName("로또 당첨 수를 구하는 메소드 검증")
  @ParameterizedTest
  @MethodSource("provideWinningLottoCount")
  void 로또_당첨수_테스트 (LottoGame lottoGame, Rank rank, long expected) {
    assertEquals(expected, lottoGame.getWinningCount(rank));
  }

  private static Stream<Arguments> provideWinningLottoCount () {
    LottoGame lottoGame = LottoGame.of(
                            Stream.of(
                              Lotto.of("1,2,3,4,5,7"),
                              Lotto.of("1,2,3,4,5,8"),
                              Lotto.of("1,2,3,40,20,10"),
                              Lotto.of("1,2,3,30,20,10")
                            ).collect(toList())
                          );
    lottoGame.setWinningNumber(Lotto.of("1,2,3,4,5,6"));
    return Stream.of(
      Arguments.of(lottoGame, Rank.SECOND, 2),
      Arguments.of(lottoGame, Rank.FORTH, 2)
    );
  }

  @DisplayName("로또 당첨 금액 검증 테스트")
  @ParameterizedTest
  @MethodSource("provideLottoGameAndWinningPrice")
  void 로또_당첨_수익률_테스트 (LottoGame lottoGame, Lotto winning, String expected) {
    lottoGame.setWinningNumber(winning);
    String payoffRatio = String.format(RATIO_FORMAT, lottoGame.getPayoff());
    assertEquals(expected, payoffRatio);
  }

  private static Stream<Arguments> provideLottoGameAndWinningPrice () {
    long payoff = Rank.stream()
                      .mapToLong(Rank::getPrice)
                      .sum();
    String payoffRatio = String.format(RATIO_FORMAT, payoff / 4000.0);
    LottoGame lottoGame = LottoGame.of(
                            Stream.of(
                              Lotto.of("1,2,3,4,5,6"),
                              Lotto.of("1,2,3,4,5,10"),
                              Lotto.of("1,2,3,4,20,10"),
                              Lotto.of("1,2,3,30,20,10")
                            ).collect(toList()));
    return Stream.of(
      Arguments.of(lottoGame, Lotto.of("1,2,3,4,5,6"), payoffRatio),
      Arguments.of(lottoGame, Lotto.of("11,12,13,14,15,16"), "0.00")
    );
  }
}
