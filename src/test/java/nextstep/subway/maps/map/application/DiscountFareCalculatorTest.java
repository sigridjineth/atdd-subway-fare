package nextstep.subway.maps.map.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import nextstep.subway.maps.line.domain.Money;
import nextstep.subway.maps.map.domain.DiscountPolicy;
import nextstep.subway.maps.map.domain.SubwayPath;

public class DiscountFareCalculatorTest {

    private DiscountFareCalculator fareCalculator;

    @BeforeEach
    void setUp() {
        this.fareCalculator = new DiscountFareCalculator();
    }

    @DisplayName("추가 요금이 있는 노선을 이용할 경우, 측정된 요금에 추가되어 계산한다.")
    @CsvSource({"5, 0, 1250", "13, 100, 1450", "18, 100, 1550", "50, 1000, 3050", "57, 0, 2150"})
    @ParameterizedTest
    void 지하철_운임을_추가요금_추가해서_계산한다(int distance, int extraFare, int expectedFareValue) {
        // given
        SubwayPath subwayPath = mock(SubwayPath.class);
        given(subwayPath.calculateDistance()).willReturn(distance);
        given(subwayPath.calculateMaxLineExtraFare()).willReturn(Money.drawNewMoney(extraFare));

        // when
        Money fare = fareCalculator.calculate(subwayPath);
        Money expectedFare = Money.drawNewMoney(expectedFareValue);

        // then
        assertThat(fare).isEqualTo(expectedFare);
    }

    @DisplayName("할인 정책과 함께 요청 시 할인된 금액을 리턴한다.")
    @CsvSource({"5, 0, 1250", "50, 1000, 3050", "57, 0, 2150"})
    @ParameterizedTest
    void 할인된_금액을_리턴한다(int distance, int extraFare, int expectedFare) {
        int DISCOUNT_VALUE = 500;

        // given
        SubwayPath subwayPath = mock(SubwayPath.class);
        given(subwayPath.calculateDistance()).willReturn(distance);
        given(subwayPath.calculateMaxLineExtraFare()).willReturn(Money.drawNewMoney(extraFare));

        // when
        DiscountPolicy discountPolicy = money -> money.minus(Money.drawNewMoney(DISCOUNT_VALUE));
        Money fare = fareCalculator.calculate(subwayPath, discountPolicy);

        // then
        assertThat(fare).isEqualTo(Money.drawNewMoney(expectedFare - 500));
    }

}